package com.example.vibestudy;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class BillStdFieldConfigServiceImpl implements BillStdFieldConfigService {

    private final BillStdFieldConfigRepository repository;
    private final BillStdFieldValueRepository fieldValueRepository;

    public BillStdFieldConfigServiceImpl(BillStdFieldConfigRepository repository,
                                          BillStdFieldValueRepository fieldValueRepository) {
        this.repository = repository;
        this.fieldValueRepository = fieldValueRepository;
    }

    @Override
    public List<BillStdFieldConfigResponseDto> findAll(String svcCd, String fieldCd) {
        Specification<BillStdFieldConfig> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (svcCd != null && !svcCd.isBlank()) {
                predicates.add(cb.equal(root.get("id").get("svcCd"), svcCd));
            }
            if (fieldCd != null && !fieldCd.isBlank()) {
                predicates.add(cb.like(root.get("id").get("fieldCd"), "%" + fieldCd + "%"));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        return repository.findAll(spec).stream().map(this::toDto).toList();
    }

    @Override
    public BillStdFieldConfigResponseDto findById(String svcCd, String fieldCd, String effStartDt) {
        return toDto(findOrThrow(svcCd, fieldCd, effStartDt));
    }

    @Override
    public List<BillStdFieldConfigResponseDto> findEffective(String svcCd) {
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        return repository.findEffectiveBySvcCd(svcCd, today).stream()
                .map(this::toDto).toList();
    }

    @Override
    @Transactional
    public BillStdFieldConfigResponseDto create(BillStdFieldConfigRequestDto dto) {
        BillStdFieldConfigId id = new BillStdFieldConfigId(dto.getSvcCd(), dto.getFieldCd(), dto.getEffStartDt());
        if (repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "이미 존재하는 필드설정입니다: " + dto.getSvcCd() + "/" + dto.getFieldCd() + "/" + dto.getEffStartDt());
        }

        BillStdFieldConfig entity = new BillStdFieldConfig();
        entity.setId(id);
        entity.setFieldNm(dto.getFieldNm());
        entity.setFieldType(dto.getFieldType());
        entity.setRequiredYn(dto.getRequiredYn() != null ? dto.getRequiredYn() : "N");
        entity.setSortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : 0);
        entity.setCommonCode(dto.getCommonCode());
        entity.setDefaultValue(dto.getDefaultValue());
        entity.setEffEndDt(dto.getEffEndDt() != null && !dto.getEffEndDt().isBlank() ? dto.getEffEndDt() : "99991231");
        entity.setCreatedBy(SecurityUtils.getCurrentUserId());
        entity.setCreatedDt(LocalDateTime.now());

        return toDto(repository.save(entity));
    }

    @Override
    @Transactional
    public BillStdFieldConfigResponseDto update(String svcCd, String fieldCd, String effStartDt, BillStdFieldConfigRequestDto dto) {
        BillStdFieldConfig entity = findOrThrow(svcCd, fieldCd, effStartDt);

        entity.setFieldNm(dto.getFieldNm());
        entity.setFieldType(dto.getFieldType());
        entity.setRequiredYn(dto.getRequiredYn());
        entity.setSortOrder(dto.getSortOrder());
        entity.setCommonCode(dto.getCommonCode());
        entity.setDefaultValue(dto.getDefaultValue());
        entity.setEffEndDt(dto.getEffEndDt());
        entity.setUpdatedBy(SecurityUtils.getCurrentUserId());
        entity.setUpdatedDt(LocalDateTime.now());

        return toDto(repository.save(entity));
    }

    @Override
    @Transactional
    public void delete(String svcCd, String fieldCd, String effStartDt) {
        findOrThrow(svcCd, fieldCd, effStartDt);
        long usageCount = fieldValueRepository.countByIdFieldCd(fieldCd);
        if (usageCount > 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "사용 중인 필드는 삭제할 수 없습니다. 사용종료 기능을 이용해 주세요.");
        }
        repository.deleteById(new BillStdFieldConfigId(svcCd, fieldCd, effStartDt));
    }

    @Override
    @Transactional
    public BillStdFieldConfigResponseDto expire(String svcCd, String fieldCd, String effStartDt) {
        BillStdFieldConfig entity = findOrThrow(svcCd, fieldCd, effStartDt);
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        entity.setEffEndDt(today);
        entity.setUpdatedBy(SecurityUtils.getCurrentUserId());
        entity.setUpdatedDt(LocalDateTime.now());
        return toDto(repository.save(entity));
    }

    // ── 내부 헬퍼 ────────────────────────────────────────────────

    private BillStdFieldConfig findOrThrow(String svcCd, String fieldCd, String effStartDt) {
        BillStdFieldConfigId id = new BillStdFieldConfigId(svcCd, fieldCd, effStartDt);
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "필드설정을 찾을 수 없습니다: " + svcCd + "/" + fieldCd + "/" + effStartDt));
    }

    private BillStdFieldConfigResponseDto toDto(BillStdFieldConfig e) {
        BillStdFieldConfigResponseDto dto = new BillStdFieldConfigResponseDto();
        dto.setSvcCd(e.getId().getSvcCd());
        dto.setFieldCd(e.getId().getFieldCd());
        dto.setEffStartDt(e.getId().getEffStartDt());
        dto.setFieldNm(e.getFieldNm());
        dto.setFieldType(e.getFieldType());
        dto.setRequiredYn(e.getRequiredYn());
        dto.setSortOrder(e.getSortOrder());
        dto.setCommonCode(e.getCommonCode());
        dto.setDefaultValue(e.getDefaultValue());
        dto.setEffEndDt(e.getEffEndDt());
        dto.setCreatedBy(e.getCreatedBy());
        dto.setCreatedDt(e.getCreatedDt());
        dto.setUpdatedBy(e.getUpdatedBy());
        dto.setUpdatedDt(e.getUpdatedDt());
        return dto;
    }
}
