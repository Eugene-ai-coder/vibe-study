package com.example.vibestudy;

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
public class BillStdServiceImpl implements BillStdService {

    private static final LocalDateTime DEFAULT_EFF_END_DT =
            LocalDateTime.of(9999, 12, 31, 23, 59, 59);

    private static final DateTimeFormatter ID_FORMATTER =
            DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");

    private final BillStdRepository repository;
    private final BillStdFieldValueRepository fieldValueRepository;

    public BillStdServiceImpl(BillStdRepository repository,
                              BillStdFieldValueRepository fieldValueRepository) {
        this.repository = repository;
        this.fieldValueRepository = fieldValueRepository;
    }

    // ── 조회 ─────────────────────────────────────────────────────

    @Override
    public List<BillStdResponseDto> findAll() {
        return repository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public BillStdResponseDto findById(String billStdId) {
        return toDto(findOrThrow(billStdId));
    }

    @Override
    public BillStdResponseDto findBySubsId(String subsId) {
        List<BillStd> list = repository.findBySubsIdAndLastEffYn(subsId, "Y");
        if (list.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "해당 가입ID의 활성 과금기준을 찾을 수 없습니다: " + subsId);
        }
        return toDto(list.get(0));
    }

    // ── 등록 ─────────────────────────────────────────────────────

    @Override
    @Transactional
    public BillStdResponseDto create(BillStdRequestDto dto) {
        // 기존 유효 레코드 이력 처리
        List<BillStd> activeList = repository.findBySubsIdAndLastEffYn(dto.getSubsId(), "Y");
        if (activeList.size() > 1) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "데이터 무결성 오류: 유효 과금기준이 중복 존재합니다.");
        }
        if (activeList.size() == 1) {
            BillStd existing = activeList.get(0);
            existing.setLastEffYn("N");
            existing.setEffEndDt(LocalDateTime.now());
            existing.setUpdatedBy(SecurityUtils.getCurrentUserId());
            existing.setUpdatedDt(LocalDateTime.now());
            repository.save(existing);
        }

        BillStd entity = new BillStd();
        String billStdId = generateId();
        entity.setBillStdId(billStdId);
        entity.setSubsId(dto.getSubsId());
        entity.setBillStdRegDt(dto.getBillStdRegDt());
        entity.setSvcCd(dto.getSvcCd());
        entity.setLastEffYn("Y");
        entity.setEffStartDt(dto.getEffStartDt());
        entity.setEffEndDt(dto.getEffEndDt() != null ? dto.getEffEndDt() : DEFAULT_EFF_END_DT);
        entity.setBillStdStatCd(dto.getBillStdStatCd());
        entity.setCreatedBy(SecurityUtils.getCurrentUserId());
        entity.setCreatedDt(LocalDateTime.now());
        BillStd saved = repository.save(entity);

        // 동적 필드값 저장
        saveFieldValues(billStdId, dto.getFieldValues(), SecurityUtils.getCurrentUserId());

        return toDto(saved);
    }

    // ── 수정 ─────────────────────────────────────────────────────

    @Override
    @Transactional
    public BillStdResponseDto update(String billStdId, BillStdRequestDto dto) {
        BillStd entity = findOrThrow(billStdId);

        // subsId / createdBy / createdDt 변경 불가
        entity.setBillStdRegDt(dto.getBillStdRegDt());
        entity.setSvcCd(dto.getSvcCd());
        entity.setLastEffYn(dto.getLastEffYn());
        entity.setEffStartDt(dto.getEffStartDt());
        entity.setEffEndDt(dto.getEffEndDt());
        entity.setBillStdStatCd(dto.getBillStdStatCd());
        entity.setUpdatedBy(SecurityUtils.getCurrentUserId());
        entity.setUpdatedDt(LocalDateTime.now());
        BillStd saved = repository.save(entity);

        // 기존 필드값 삭제 후 재저장
        fieldValueRepository.deleteByIdBillStdId(billStdId);
        saveFieldValues(billStdId, dto.getFieldValues(), SecurityUtils.getCurrentUserId());

        return toDto(saved);
    }

    // ── 삭제 ─────────────────────────────────────────────────────

    @Transactional
    @Override
    public void delete(String billStdId) {
        BillStd entity = findOrThrow(billStdId);
        long historyCount = repository.countBySubsId(entity.getSubsId());
        if (historyCount > 1) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "다른 이력이 존재하여 삭제할 수 없습니다.");
        }
        fieldValueRepository.deleteByIdBillStdId(billStdId);
        repository.deleteById(billStdId);
    }

    // ── 내부 헬퍼 ────────────────────────────────────────────────

    private BillStd findOrThrow(String billStdId) {
        return repository.findById(billStdId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "과금기준을 찾을 수 없습니다: " + billStdId));
    }

    private String generateId() {
        return "BS" + LocalDateTime.now().format(ID_FORMATTER);
    }

    private void saveFieldValues(String billStdId, Map<String, String> fieldValues, String userId) {
        if (fieldValues == null || fieldValues.isEmpty()) return;
        for (Map.Entry<String, String> entry : fieldValues.entrySet()) {
            BillStdFieldValue fv = new BillStdFieldValue();
            fv.setId(new BillStdFieldValueId(billStdId, entry.getKey()));
            fv.setFieldValue(entry.getValue());
            fv.setCreatedBy(userId);
            fv.setCreatedDt(LocalDateTime.now());
            fieldValueRepository.save(fv);
        }
    }

    private BillStdResponseDto toDto(BillStd e) {
        BillStdResponseDto dto = new BillStdResponseDto();
        dto.setBillStdId(e.getBillStdId());
        dto.setSubsId(e.getSubsId());
        dto.setBillStdRegDt(e.getBillStdRegDt());
        dto.setSvcCd(e.getSvcCd());
        dto.setLastEffYn(e.getLastEffYn());
        dto.setEffStartDt(e.getEffStartDt());
        dto.setEffEndDt(e.getEffEndDt());
        dto.setBillStdStatCd(e.getBillStdStatCd());
        dto.setCreatedBy(e.getCreatedBy());
        dto.setCreatedDt(e.getCreatedDt());
        dto.setUpdatedBy(e.getUpdatedBy());
        dto.setUpdatedDt(e.getUpdatedDt());

        // 동적 필드값 로드
        List<BillStdFieldValue> values = fieldValueRepository.findByIdBillStdId(e.getBillStdId());
        Map<String, String> fieldValues = new LinkedHashMap<>();
        for (BillStdFieldValue fv : values) {
            fieldValues.put(fv.getId().getFieldCd(), fv.getFieldValue());
        }
        dto.setFieldValues(fieldValues);

        return dto;
    }
}
