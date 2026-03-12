package com.example.vibestudy;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class SpecialSubscriptionServiceImpl implements SpecialSubscriptionService {

    private final SpecialSubscriptionRepository repository;

    public SpecialSubscriptionServiceImpl(SpecialSubscriptionRepository repository) {
        this.repository = repository;
    }

    @Override
    public Page<SpecialSubscriptionResponseDto> findPage(String subsBillStdId, String subsId, Pageable pageable) {
        Specification<SpecialSubscription> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (subsBillStdId != null && !subsBillStdId.isBlank()) {
                predicates.add(cb.like(root.get("id").get("subsBillStdId"), "%" + subsBillStdId + "%"));
            }
            if (subsId != null && !subsId.isBlank()) {
                predicates.add(cb.like(root.get("subsId"), "%" + subsId + "%"));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        return repository.findAll(spec, pageable).map(this::toDto);
    }

    @Override
    public SpecialSubscriptionResponseDto findById(String subsBillStdId, String effStartDt) {
        return toDto(findOrThrow(subsBillStdId, effStartDt));
    }

    @Override
    @Transactional
    public SpecialSubscriptionResponseDto create(SpecialSubscriptionRequestDto dto) {
        SpecialSubscriptionId id = new SpecialSubscriptionId(dto.getSubsBillStdId(), dto.getEffStartDt());
        if (repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "이미 존재하는 특수가입입니다: " + dto.getSubsBillStdId() + "/" + dto.getEffStartDt());
        }

        SpecialSubscription entity = new SpecialSubscription();
        entity.setId(id);
        entity.setSubsId(dto.getSubsId());
        entity.setSvcCd(dto.getSvcCd());
        entity.setEffEndDt(dto.getEffEndDt() != null && !dto.getEffEndDt().isBlank() ? dto.getEffEndDt() : "99991231");
        entity.setLastEffYn(dto.getLastEffYn());
        entity.setSpecSubsStatCd(dto.getSpecSubsStatCd());
        entity.setCntrcCapKmh(dto.getCntrcCapKmh());
        entity.setCntrcAmt(dto.getCntrcAmt());
        entity.setDscRt(dto.getDscRt());
        entity.setRemark(dto.getRemark());
        entity.setCreatedBy(SecurityUtils.getCurrentUserId());
        entity.setCreatedDt(LocalDateTime.now());

        return toDto(repository.save(entity));
    }

    @Override
    @Transactional
    public SpecialSubscriptionResponseDto update(String subsBillStdId, String effStartDt, SpecialSubscriptionRequestDto dto) {
        SpecialSubscription entity = findOrThrow(subsBillStdId, effStartDt);

        // PK 필드(subsBillStdId, effStartDt) 변경 불가
        entity.setSubsId(dto.getSubsId());
        entity.setSvcCd(dto.getSvcCd());
        entity.setEffEndDt(dto.getEffEndDt());
        entity.setLastEffYn(dto.getLastEffYn());
        entity.setSpecSubsStatCd(dto.getSpecSubsStatCd());
        entity.setCntrcCapKmh(dto.getCntrcCapKmh());
        entity.setCntrcAmt(dto.getCntrcAmt());
        entity.setDscRt(dto.getDscRt());
        entity.setRemark(dto.getRemark());
        entity.setUpdatedBy(SecurityUtils.getCurrentUserId());
        entity.setUpdatedDt(LocalDateTime.now());

        return toDto(repository.save(entity));
    }

    @Override
    @Transactional
    public void delete(String subsBillStdId, String effStartDt) {
        findOrThrow(subsBillStdId, effStartDt);
        repository.deleteById(new SpecialSubscriptionId(subsBillStdId, effStartDt));
    }

    // ── 내부 헬퍼 ────────────────────────────────────────────────

    private SpecialSubscription findOrThrow(String subsBillStdId, String effStartDt) {
        SpecialSubscriptionId id = new SpecialSubscriptionId(subsBillStdId, effStartDt);
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "특수가입을 찾을 수 없습니다: " + subsBillStdId + "/" + effStartDt));
    }

    private SpecialSubscriptionResponseDto toDto(SpecialSubscription e) {
        SpecialSubscriptionResponseDto dto = new SpecialSubscriptionResponseDto();
        dto.setSubsBillStdId(e.getId().getSubsBillStdId());
        dto.setEffStartDt(e.getId().getEffStartDt());
        dto.setSubsId(e.getSubsId());
        dto.setSvcCd(e.getSvcCd());
        dto.setEffEndDt(e.getEffEndDt());
        dto.setLastEffYn(e.getLastEffYn());
        dto.setSpecSubsStatCd(e.getSpecSubsStatCd());
        dto.setCntrcCapKmh(e.getCntrcCapKmh());
        dto.setCntrcAmt(e.getCntrcAmt());
        dto.setDscRt(e.getDscRt());
        dto.setRemark(e.getRemark());
        dto.setCreatedBy(e.getCreatedBy());
        dto.setCreatedDt(e.getCreatedDt());
        dto.setUpdatedBy(e.getUpdatedBy());
        dto.setUpdatedDt(e.getUpdatedDt());
        return dto;
    }
}
