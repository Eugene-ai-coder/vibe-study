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
import java.time.format.DateTimeParseException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ApprvReqServiceImpl implements ApprvReqService {

    private final EntityManager em;
    private final BillStdApprvReqRepository repository;
    private final BillStdReqRepository billStdReqRepository;
    private final BillStdReqFieldValueRepository billStdReqFieldValueRepository;
    private final BillStdReqService billStdReqService;
    private final ObjectMapper objectMapper;

    public ApprvReqServiceImpl(EntityManager em,
                               BillStdApprvReqRepository repository,
                               BillStdReqRepository billStdReqRepository,
                               BillStdReqFieldValueRepository billStdReqFieldValueRepository,
                               BillStdReqService billStdReqService,
                               ObjectMapper objectMapper) {
        this.em = em;
        this.repository = repository;
        this.billStdReqRepository = billStdReqRepository;
        this.billStdReqFieldValueRepository = billStdReqFieldValueRepository;
        this.billStdReqService = billStdReqService;
        this.objectMapper = objectMapper;
    }

    @Override
    public Page<ApprvReqListResponseDto> findList(String subsId, String stdRegStatCd,
                                                   String startDt, String endDt, Pageable pageable) {
        String baseJpql = """
            FROM BillStdReq r
            JOIN Subscription s ON s.subsId = r.subsId
            LEFT JOIN BillStdApprvReq a ON a.billStdReqId = r.billStdReqId
            WHERE r.effEndDt = :maxDt
              AND r.stdRegStatCd IN ('REVIEW', 'APPRV_REQ', 'APPROVED', 'REJECTED')
            """;

        if (subsId != null && !subsId.isBlank()) {
            baseJpql += " AND r.subsId LIKE :subsId";
        }
        if (stdRegStatCd != null && !stdRegStatCd.isBlank()) {
            baseJpql += " AND r.stdRegStatCd = :stdRegStatCd";
        }
        if (startDt != null && !startDt.isBlank()) {
            baseJpql += " AND r.firstReqDt >= :startDt";
        }
        if (endDt != null && !endDt.isBlank()) {
            baseJpql += " AND r.firstReqDt <= :endDt";
        }

        String selectJpql = """
            SELECT new com.example.vibestudy.ApprvReqListResponseDto(
                r.subsId, s.subsNm, r.billStdReqId,
                r.stdRegStatCd, r.firstReqDt, a.createdDt, a.apprvReqId
            )
            """ + baseJpql + " ORDER BY r.firstReqDt DESC";

        String countJpql = "SELECT COUNT(r) " + baseJpql;

        TypedQuery<ApprvReqListResponseDto> query = em.createQuery(selectJpql, ApprvReqListResponseDto.class);
        TypedQuery<Long> countQuery = em.createQuery(countJpql, Long.class);

        query.setParameter("maxDt", IdGenerator.MAX_DT);
        countQuery.setParameter("maxDt", IdGenerator.MAX_DT);

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
            LocalDateTime start = parseDateParam(startDt, "T00:00:00");
            query.setParameter("startDt", start);
            countQuery.setParameter("startDt", start);
        }
        if (endDt != null && !endDt.isBlank()) {
            LocalDateTime end = parseDateParam(endDt, "T23:59:59");
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
        BillStdReq currentReq = billStdReqRepository
                .findByBillStdReqIdAndEffEndDt(dto.getBillStdReqId(), IdGenerator.MAX_DT)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "과금기준신청을 찾을 수 없습니다: " + dto.getBillStdReqId()));

        if (!"REVIEW".equals(currentReq.getStdRegStatCd())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "내부검토 상태에서만 결재요청할 수 있습니다. 현재: " + currentReq.getStdRegStatCd());
        }

        String snapshot = buildSnapshot(currentReq);

        BillStdApprvReq entity = new BillStdApprvReq();
        entity.setApprvReqId(generateId());
        entity.setBillStdReqId(dto.getBillStdReqId());
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
        return IdGenerator.generate("APPRV");
    }

    private String buildSnapshot(BillStdReq req) {
        try {
            Map<String, Object> snapshot = new LinkedHashMap<>();
            snapshot.put("billStdReqId", req.getBillStdReqId());
            snapshot.put("billStdId", req.getBillStdId());
            snapshot.put("subsId", req.getSubsId());
            snapshot.put("svcCd", req.getSvcCd());
            snapshot.put("basicProdCd", req.getBasicProdCd());
            snapshot.put("reqTypeCd", req.getReqTypeCd());
            snapshot.put("stdRegStatCd", req.getStdRegStatCd());
            snapshot.put("firstReqDt", req.getFirstReqDt() != null ? req.getFirstReqDt().toString() : null);
            snapshot.put("effStartDt", req.getEffStartDt() != null ? req.getEffStartDt().toString() : null);
            snapshot.put("effEndDt", req.getEffEndDt() != null ? req.getEffEndDt().toString() : null);

            List<BillStdReqFieldValue> fieldValues = billStdReqFieldValueRepository.findByIdBillStdReqSeq(req.getBillStdReqSeq());
            Map<String, String> fields = new LinkedHashMap<>();
            for (BillStdReqFieldValue fv : fieldValues) {
                fields.put(fv.getId().getFieldCd(), fv.getFieldValue());
            }
            snapshot.put("fieldValues", fields);

            return objectMapper.writeValueAsString(snapshot);
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "스냅샷 생성 실패: " + e.getMessage());
        }
    }

    private BillStdApprvReqResponseDto toDto(BillStdApprvReq entity) {
        BillStdApprvReqResponseDto dto = new BillStdApprvReqResponseDto();
        dto.setApprvReqId(entity.getApprvReqId());
        dto.setBillStdReqId(entity.getBillStdReqId());
        dto.setSubsId(entity.getSubsId());
        dto.setApprvReqContent(entity.getApprvReqContent());
        dto.setApprvRemarks(entity.getApprvRemarks());
        dto.setApproverId(entity.getApproverId());
        dto.setCreatedBy(entity.getCreatedBy());
        dto.setCreatedDt(entity.getCreatedDt());
        return dto;
    }

    private LocalDateTime parseDateParam(String date, String timeSuffix) {
        try {
            return LocalDateTime.parse(date + timeSuffix);
        } catch (DateTimeParseException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "잘못된 날짜 형식입니다: " + date);
        }
    }
}
