package com.example.vibestudy;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class BillStdReqServiceImpl implements BillStdReqService {

    private final BillStdReqRepository repository;
    private final BillStdReqFieldValueRepository fieldValueRepository;
    private final EntityManager em;
    private final TodoService todoService;
    private final SubscriptionRepository subscriptionRepository;
    private final BillStdService billStdService;

    public BillStdReqServiceImpl(BillStdReqRepository repository,
                                 BillStdReqFieldValueRepository fieldValueRepository,
                                 EntityManager em,
                                 TodoService todoService,
                                 SubscriptionRepository subscriptionRepository,
                                 BillStdService billStdService) {
        this.repository = repository;
        this.fieldValueRepository = fieldValueRepository;
        this.em = em;
        this.todoService = todoService;
        this.subscriptionRepository = subscriptionRepository;
        this.billStdService = billStdService;
    }

    // ── 조회 ─────────────────────────────────────────────────────

    @Override
    public BillStdReqResponseDto findCurrentByReqId(String billStdReqId) {
        BillStdReq entity = findCurrentOrThrow(billStdReqId);
        return toDto(entity);
    }

    @Override
    public BillStdReqResponseDto findBySubsId(String subsId) {
        // 1. 가입 존재 확인
        Subscription subs = subscriptionRepository.findById(subsId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "유효한 가입이 없습니다: " + subsId));

        // 2. 진행중 신청 확인 (DRAFT, REVIEW, APPRV_REQ)
        List<BillStdReq> currentReqs = repository.findBySubsIdAndEffEndDt(subsId, IdGenerator.MAX_DT);
        List<BillStdReq> inProgress = currentReqs.stream()
                .filter(r -> List.of("DRAFT", "REVIEW", "APPRV_REQ").contains(r.getStdRegStatCd()))
                .toList();

        if (inProgress.size() > 1) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "진행중인 신청이 2건 이상 존재합니다. 관리자에게 문의해 주세요.");
        }
        if (inProgress.size() == 1) {
            return toDto(inProgress.get(0));
        }

        // 3. 진행중 없음 → 활성 과금기준 확인하여 템플릿 반환
        BillStdReqResponseDto template = new BillStdReqResponseDto();
        template.setSubsId(subsId);
        template.setSvcCd(subs.getSvcCd());

        try {
            BillStdResponseDto activeBillStd = billStdService.findBySubsId(subsId);
            if (!"EXPIRED".equals(activeBillStd.getBillStdStatCd())) {
                template.setReqTypeCd("CHG");
                template.setBillStdId(activeBillStd.getBillStdId());
                template.setBasicProdCd(activeBillStd.getBasicProdCd());
                template.setFieldValues(activeBillStd.getFieldValues());
                return template;
            }
        } catch (ResponseStatusException ignored) {
            // 활성 과금기준 없음
        }

        // Active 과금기준 없음 → 신규용 템플릿
        template.setReqTypeCd("NEW");
        return template;
    }

    // ── 신규 생성 ─────────────────────────────────────────────────

    @Override
    @Transactional
    public BillStdReqResponseDto create(BillStdReqRequestDto dto) {
        LocalDateTime now = LocalDateTime.now();
        String reqId = generateReqId();
        String billStdId = dto.getBillStdId();
        if ("NEW".equals(dto.getReqTypeCd()) || billStdId == null || billStdId.isBlank()) {
            billStdId = generateBillStdId();
        }

        BillStdReq entity = new BillStdReq();
        entity.setBillStdReqId(reqId);
        entity.setFirstReqDt(now);
        entity.setEffStartDt(now);
        entity.setEffEndDt(IdGenerator.MAX_DT);
        entity.setReqTypeCd(dto.getReqTypeCd());
        entity.setStdRegStatCd("DRAFT");
        entity.setBillStdId(billStdId);
        entity.setSubsId(dto.getSubsId());
        entity.setSvcCd(dto.getSvcCd());
        entity.setBasicProdCd(dto.getBasicProdCd());
        entity.setCreatedBy(dto.getCreatedBy());
        entity.setCreatedDt(now);
        BillStdReq saved = repository.save(entity);

        saveFieldValues(saved.getBillStdReqSeq(), dto.getFieldValues(), dto.getCreatedBy());
        handleTodo(saved);
        return toDto(saved);
    }

    // ── DRAFT 재저장 ──────────────────────────────────────────────

    @Override
    @Transactional
    public BillStdReqResponseDto save(String billStdReqId, BillStdReqRequestDto dto) {
        BillStdReq current = findCurrentOrThrow(billStdReqId);
        if (!"DRAFT".equals(current.getStdRegStatCd())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "DRAFT 상태에서만 저장할 수 있습니다.");
        }
        BillStdReq snapshot = createSnapshot(current, "DRAFT", dto.getFieldValues(), dto.getCreatedBy());
        return toDto(snapshot);
    }

    // ── 상태 전이 ─────────────────────────────────────────────────

    @Override
    @Transactional
    public BillStdReqResponseDto changeStatus(String billStdReqId, String newStatus, String createdBy) {
        BillStdReq current = findCurrentOrThrow(billStdReqId);
        validateTransition(current.getStdRegStatCd(), newStatus);
        BillStdReq snapshot = createSnapshot(current, newStatus, null, createdBy);
        handleTodo(snapshot);
        if ("APPROVED".equals(newStatus)) {
            applyToBillStd(snapshot, createdBy);
        }
        return toDto(snapshot);
    }

    // ── 삭제 ─────────────────────────────────────────────────────

    @Override
    @Transactional
    public void delete(String billStdReqId) {
        List<BillStdReq> allSnapshots = repository.findAllByBillStdReqId(billStdReqId);
        BillStdReq current = allSnapshots.stream()
                .filter(r -> IdGenerator.MAX_DT.equals(r.getEffEndDt()))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "신청을 찾을 수 없습니다."));

        if (!"DRAFT".equals(current.getStdRegStatCd())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "DRAFT 상태에서만 삭제할 수 있습니다.");
        }
        for (BillStdReq snap : allSnapshots) {
            fieldValueRepository.deleteByIdBillStdReqSeq(snap.getBillStdReqSeq());
        }
        repository.deleteAll(allSnapshots);
    }

    // ── 목록 조회 ─────────────────────────────────────────────────

    @Override
    public Page<SubsBillStdReqResponseDto> findList(String keyword, Pageable pageable) {
        String baseJpql = """
            FROM BillStdReq r
            LEFT JOIN Subscription s ON s.subsId = r.subsId
            WHERE r.effEndDt = :maxDt
            """;

        if (keyword != null && !keyword.isBlank()) {
            baseJpql += " AND (r.subsId LIKE :kw OR s.subsNm LIKE :kw)";
        }

        String selectJpql = """
            SELECT new com.example.vibestudy.SubsBillStdReqResponseDto(
                r.subsId, s.subsNm, r.svcCd, r.basicProdCd,
                r.reqTypeCd, r.stdRegStatCd, r.billStdReqId,
                r.firstReqDt, r.effStartDt, r.effEndDt
            )
            """ + baseJpql + " ORDER BY r.firstReqDt DESC";

        String countJpql = "SELECT COUNT(r) " + baseJpql;

        TypedQuery<SubsBillStdReqResponseDto> query = em.createQuery(selectJpql, SubsBillStdReqResponseDto.class);
        TypedQuery<Long> countQuery = em.createQuery(countJpql, Long.class);

        query.setParameter("maxDt", IdGenerator.MAX_DT);
        countQuery.setParameter("maxDt", IdGenerator.MAX_DT);

        if (keyword != null && !keyword.isBlank()) {
            String kw = "%" + keyword.trim() + "%";
            query.setParameter("kw", kw);
            countQuery.setParameter("kw", kw);
        }

        long total = countQuery.getSingleResult();

        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());
        List<SubsBillStdReqResponseDto> content = query.getResultList();

        return new PageImpl<>(content, pageable, total);
    }

    // ── TODO 목록 ──────────────────────────────────────────────

    @Override
    public List<BillStdReqResponseDto> findTodoList() {
        return repository.findByEffEndDtAndStdRegStatCdNotIn(
                        IdGenerator.MAX_DT, List.of("APPROVED", "CANCELLED"))
                .stream()
                .map(this::toDto)
                .toList();
    }

    // ── 내부 헬퍼 ────────────────────────────────────────────────

    private void applyToBillStd(BillStdReq req, String userId) {
        LocalDateTime now = LocalDateTime.now();
        String statCd = "CAN".equals(req.getReqTypeCd()) ? "EXPIRED" : "ACTIVE";

        BillStdRequestDto dto = new BillStdRequestDto();
        dto.setSubsId(req.getSubsId());
        dto.setSvcCd(req.getSvcCd());
        dto.setBasicProdCd(req.getBasicProdCd());
        dto.setBillStdRegDt(now);
        dto.setLastEffYn("Y");
        dto.setEffStartDt(now);
        dto.setBillStdStatCd(statCd);
        dto.setFieldValues(loadFieldValues(req.getBillStdReqSeq()));
        dto.setCreatedBy(userId);

        billStdService.create(dto);
    }

    private void handleTodo(BillStdReq entity) {
        String statCd = entity.getStdRegStatCd();
        String reqId = entity.getBillStdReqId();

        // 이전 TODO 완료 처리
        todoService.completeTodo("BILL_STD_REQ", reqId, null);

        // 상태별 새 TODO 생성
        String adminId = subscriptionRepository.findById(entity.getSubsId())
                .map(Subscription::getAdminId)
                .orElse(null);

        switch (statCd) {
            case "DRAFT" -> todoService.createTodo("BILL_STD_REQ", reqId, null,
                    entity.getCreatedBy(), "과금기준신청 등록완료");
            case "REVIEW" -> todoService.createTodo("BILL_STD_REQ", reqId, null,
                    adminId, "과금기준신청 검토완료");
            case "APPRV_REQ" -> todoService.createTodo("BILL_STD_REQ", reqId, null,
                    adminId, "과금기준신청 결재요청");
            case "REJECTED" -> todoService.createTodo("BILL_STD_REQ", reqId, null,
                    adminId, "과금기준신청 반려확인");
        }
    }

    private BillStdReq createSnapshot(BillStdReq current, String newStatCd,
                                       Map<String, String> newFieldValues, String userId) {
        LocalDateTime now = LocalDateTime.now();

        current.setEffEndDt(now);
        current.setUpdatedBy(userId);
        current.setUpdatedDt(now);
        repository.save(current);

        BillStdReq snapshot = new BillStdReq();
        snapshot.setBillStdReqId(current.getBillStdReqId());
        snapshot.setFirstReqDt(current.getFirstReqDt());
        snapshot.setEffStartDt(now);
        snapshot.setEffEndDt(IdGenerator.MAX_DT);
        snapshot.setReqTypeCd(current.getReqTypeCd());
        snapshot.setStdRegStatCd(newStatCd);
        snapshot.setBillStdId(current.getBillStdId());
        snapshot.setSubsId(current.getSubsId());
        snapshot.setSvcCd(current.getSvcCd());
        snapshot.setBasicProdCd(current.getBasicProdCd());
        snapshot.setCreatedBy(userId);
        snapshot.setCreatedDt(now);
        BillStdReq saved = repository.save(snapshot);

        Map<String, String> values = (newFieldValues != null) ? newFieldValues : loadFieldValues(current.getBillStdReqSeq());
        saveFieldValues(saved.getBillStdReqSeq(), values, userId);

        return saved;
    }

    private void validateTransition(String from, String to) {
        boolean valid = switch (to) {
            case "REVIEW"    -> "DRAFT".equals(from);
            case "APPRV_REQ" -> "REVIEW".equals(from) || "REJECTED".equals(from);
            case "APPROVED"  -> "APPRV_REQ".equals(from);
            case "REJECTED"  -> "REVIEW".equals(from) || "APPRV_REQ".equals(from);
            case "CANCELLED" -> "DRAFT".equals(from) || "REVIEW".equals(from) || "APPRV_REQ".equals(from) || "REJECTED".equals(from);
            default -> false;
        };
        if (!valid) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    from + " 상태에서 " + to + "(으)로 전환할 수 없습니다.");
        }
    }

    private BillStdReq findCurrentOrThrow(String billStdReqId) {
        return repository.findByBillStdReqIdAndEffEndDt(billStdReqId, IdGenerator.MAX_DT)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "신청을 찾을 수 없습니다: " + billStdReqId));
    }

    private String generateReqId() {
        return IdGenerator.generate("BREQ");
    }

    private String generateBillStdId() {
        return IdGenerator.generate("BS");
    }

    private Map<String, String> loadFieldValues(Long billStdReqSeq) {
        List<BillStdReqFieldValue> values = fieldValueRepository.findByIdBillStdReqSeq(billStdReqSeq);
        Map<String, String> map = new LinkedHashMap<>();
        for (BillStdReqFieldValue fv : values) {
            map.put(fv.getId().getFieldCd(), fv.getFieldValue());
        }
        return map;
    }

    private void saveFieldValues(Long billStdReqSeq, Map<String, String> fieldValues, String userId) {
        if (fieldValues == null || fieldValues.isEmpty()) return;
        LocalDateTime now = LocalDateTime.now();
        List<BillStdReqFieldValue> entities = new java.util.ArrayList<>();
        for (Map.Entry<String, String> entry : fieldValues.entrySet()) {
            BillStdReqFieldValue fv = new BillStdReqFieldValue();
            fv.setId(new BillStdReqFieldValueId(billStdReqSeq, entry.getKey()));
            fv.setFieldValue(entry.getValue());
            fv.setCreatedBy(userId);
            fv.setCreatedDt(now);
            entities.add(fv);
        }
        fieldValueRepository.saveAll(entities);
    }

    private BillStdReqResponseDto toDto(BillStdReq e) {
        BillStdReqResponseDto dto = new BillStdReqResponseDto();
        dto.setBillStdReqSeq(e.getBillStdReqSeq());
        dto.setBillStdReqId(e.getBillStdReqId());
        dto.setFirstReqDt(e.getFirstReqDt());
        dto.setEffStartDt(e.getEffStartDt());
        dto.setEffEndDt(e.getEffEndDt());
        dto.setReqTypeCd(e.getReqTypeCd());
        dto.setStdRegStatCd(e.getStdRegStatCd());
        dto.setBillStdId(e.getBillStdId());
        dto.setSubsId(e.getSubsId());
        dto.setSvcCd(e.getSvcCd());
        dto.setBasicProdCd(e.getBasicProdCd());
        dto.setCreatedBy(e.getCreatedBy());
        dto.setCreatedDt(e.getCreatedDt());
        dto.setUpdatedBy(e.getUpdatedBy());
        dto.setUpdatedDt(e.getUpdatedDt());

        List<BillStdReqFieldValue> values = fieldValueRepository.findByIdBillStdReqSeq(e.getBillStdReqSeq());
        Map<String, String> fieldValues = new LinkedHashMap<>();
        for (BillStdReqFieldValue fv : values) {
            fieldValues.put(fv.getId().getFieldCd(), fv.getFieldValue());
        }
        dto.setFieldValues(fieldValues);

        return dto;
    }
}
