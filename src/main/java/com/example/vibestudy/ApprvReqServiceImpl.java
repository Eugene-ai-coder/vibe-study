package com.example.vibestudy;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ApprvReqServiceImpl implements ApprvReqService {

    private static final DateTimeFormatter ID_FORMATTER =
            DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");

    private static final LocalDateTime MAX_DT =
            LocalDateTime.of(9999, 12, 31, 23, 59, 59);

    private final EntityManager em;
    private final BillStdApprvReqRepository repository;
    private final BillStdRepository billStdRepository;
    private final BillStdFieldValueRepository fieldValueRepository;
    private final BillStdReqRepository billStdReqRepository;
    private final BillStdReqService billStdReqService;
    private final ObjectMapper objectMapper;

    public ApprvReqServiceImpl(EntityManager em,
                               BillStdApprvReqRepository repository,
                               BillStdRepository billStdRepository,
                               BillStdFieldValueRepository fieldValueRepository,
                               BillStdReqRepository billStdReqRepository,
                               BillStdReqService billStdReqService,
                               ObjectMapper objectMapper) {
        this.em = em;
        this.repository = repository;
        this.billStdRepository = billStdRepository;
        this.fieldValueRepository = fieldValueRepository;
        this.billStdReqRepository = billStdReqRepository;
        this.billStdReqService = billStdReqService;
        this.objectMapper = objectMapper;
    }

    @Override
    public Page<ApprvReqListResponseDto> findList(String subsId, String stdRegStatCd,
                                                   String startDt, String endDt, Pageable pageable) {
        String baseJpql = """
            FROM BillStdReq r
            JOIN BillStd b ON b.billStdId = r.billStdId AND b.lastEffYn = 'Y'
            JOIN Subscription s ON s.subsId = b.subsId
            LEFT JOIN BillStdApprvReq a ON a.billStdId = b.billStdId
            WHERE r.effEndDt = :maxDt
              AND r.stdRegStatCd IN ('REVIEW', 'APPRV_REQ', 'APPROVED', 'REJECTED')
            """;

        if (subsId != null && !subsId.isBlank()) {
            baseJpql += " AND b.subsId LIKE :subsId";
        }
        if (stdRegStatCd != null && !stdRegStatCd.isBlank()) {
            baseJpql += " AND r.stdRegStatCd = :stdRegStatCd";
        }
        if (startDt != null && !startDt.isBlank()) {
            baseJpql += " AND b.billStdRegDt >= :startDt";
        }
        if (endDt != null && !endDt.isBlank()) {
            baseJpql += " AND b.billStdRegDt <= :endDt";
        }

        String selectJpql = """
            SELECT new com.example.vibestudy.ApprvReqListResponseDto(
                s.subsId, s.subsNm, b.billStdId,
                r.stdRegStatCd, b.billStdRegDt, a.createdDt, a.apprvReqId
            )
            """ + baseJpql + " ORDER BY b.billStdRegDt DESC";

        String countJpql = "SELECT COUNT(r) " + baseJpql;

        TypedQuery<ApprvReqListResponseDto> query = em.createQuery(selectJpql, ApprvReqListResponseDto.class);
        TypedQuery<Long> countQuery = em.createQuery(countJpql, Long.class);

        query.setParameter("maxDt", MAX_DT);
        countQuery.setParameter("maxDt", MAX_DT);

        if (subsId != null && !subsId.isBlank()) {
            String kw = "%" + subsId.trim() + "%";
            query.setParameter("subsId", kw);
            countQuery.setParameter("subsId", kw);
        }
        if (stdRegStatCd != null && !stdRegStatCd.isBlank()) {
            query.setParameter("stdRegStatCd", stdRegStatCd);
            countQuery.setParameter("stdRegStatCd", stdRegStatCd);
        }
        if (startDt != null && !startDt.isBlank()) {
            LocalDateTime start = LocalDateTime.parse(startDt + "T00:00:00");
            query.setParameter("startDt", start);
            countQuery.setParameter("startDt", start);
        }
        if (endDt != null && !endDt.isBlank()) {
            LocalDateTime end = LocalDateTime.parse(endDt + "T23:59:59");
            query.setParameter("endDt", end);
            countQuery.setParameter("endDt", end);
        }

        long total = countQuery.getSingleResult();

        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());
        List<ApprvReqListResponseDto> content = query.getResultList();

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    @Transactional
    public BillStdApprvReqResponseDto createApprvReq(BillStdApprvReqRequestDto dto) {
        BillStd billStd = billStdRepository.findById(dto.getBillStdId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "과금기준을 찾을 수 없습니다: " + dto.getBillStdId()));

        // BillStdReq에서 현재 상태 확인
        List<BillStdReq> reqList = billStdReqRepository.findByBillStdIdAndEffEndDt(dto.getBillStdId(), MAX_DT);
        BillStdReq currentReq = reqList.stream()
                .filter(r -> "REVIEW".equals(r.getStdRegStatCd()))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "내부검토 상태의 과금기준신청이 없습니다."));

        String snapshot = buildSnapshot(billStd, currentReq);

        BillStdApprvReq entity = new BillStdApprvReq();
        entity.setApprvReqId(generateId());
        entity.setBillStdId(dto.getBillStdId());
        entity.setSubsId(dto.getSubsId());
        entity.setApprvReqContent(snapshot);
        entity.setApprvRemarks(dto.getApprvRemarks());
        entity.setCreatedBy(dto.getCreatedBy());
        entity.setCreatedDt(LocalDateTime.now());

        // BillStdReq 상태를 APPRV_REQ로 전이
        billStdReqService.changeStatus(currentReq.getBillStdReqId(), "APPRV_REQ", dto.getCreatedBy());

        return toDto(repository.save(entity));
    }

    @Override
    public BillStdApprvReqResponseDto findByApprvReqId(String apprvReqId) {
        BillStdApprvReq entity = repository.findById(apprvReqId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "결재요청을 찾을 수 없습니다: " + apprvReqId));
        return toDto(entity);
    }

    private String generateId() {
        return "APPRV" + LocalDateTime.now().format(ID_FORMATTER);
    }

    private String buildSnapshot(BillStd billStd, BillStdReq billStdReq) {
        try {
            Map<String, Object> snapshot = new LinkedHashMap<>();
            snapshot.put("billStdId", billStd.getBillStdId());
            snapshot.put("subsId", billStd.getSubsId());
            snapshot.put("billStdRegDt", billStd.getBillStdRegDt() != null ? billStd.getBillStdRegDt().toString() : null);
            snapshot.put("svcCd", billStd.getSvcCd());
            snapshot.put("lastEffYn", billStd.getLastEffYn());
            snapshot.put("effStartDt", billStd.getEffStartDt() != null ? billStd.getEffStartDt().toString() : null);
            snapshot.put("effEndDt", billStd.getEffEndDt() != null ? billStd.getEffEndDt().toString() : null);
            snapshot.put("stdRegStatCd", billStdReq.getStdRegStatCd());
            snapshot.put("billStdStatCd", billStd.getBillStdStatCd());

            List<BillStdFieldValue> fieldValues = fieldValueRepository.findByIdBillStdId(billStd.getBillStdId());
            Map<String, String> fields = new LinkedHashMap<>();
            for (BillStdFieldValue fv : fieldValues) {
                fields.put(fv.getId().getFieldCd(), fv.getFieldValue());
            }
            snapshot.put("fieldValues", fields);

            return objectMapper.writeValueAsString(snapshot);
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "스냅샷 생성 실패: " + e.getMessage());
        }
    }

    private BillStdApprvReqResponseDto toDto(BillStdApprvReq entity) {
        BillStdApprvReqResponseDto dto = new BillStdApprvReqResponseDto();
        dto.setApprvReqId(entity.getApprvReqId());
        dto.setBillStdId(entity.getBillStdId());
        dto.setSubsId(entity.getSubsId());
        dto.setApprvReqContent(entity.getApprvReqContent());
        dto.setApprvRemarks(entity.getApprvRemarks());
        dto.setApproverId(entity.getApproverId());
        dto.setCreatedBy(entity.getCreatedBy());
        dto.setCreatedDt(entity.getCreatedDt());
        return dto;
    }
}
