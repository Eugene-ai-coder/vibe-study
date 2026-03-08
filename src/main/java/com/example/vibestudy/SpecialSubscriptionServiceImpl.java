package com.example.vibestudy;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SpecialSubscriptionServiceImpl implements SpecialSubscriptionService {

    private final SpecialSubscriptionRepository repository;

    public SpecialSubscriptionServiceImpl(SpecialSubscriptionRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<SpecialSubscriptionResponseDto> findAll(String subsBillStdId, String subsId) {
        boolean hasBillStdId = subsBillStdId != null && !subsBillStdId.isBlank();
        boolean hasSubsId = subsId != null && !subsId.isBlank();

        List<SpecialSubscription> list;
        if (hasBillStdId && hasSubsId) {
            list = repository.findByIdSubsBillStdIdContainingAndSubsIdContaining(subsBillStdId, subsId);
        } else if (hasBillStdId) {
            list = repository.findByIdSubsBillStdIdContaining(subsBillStdId);
        } else if (hasSubsId) {
            list = repository.findBySubsIdContaining(subsId);
        } else {
            list = repository.findAll();
        }
        return list.stream().map(this::toDto).toList();
    }

    @Override
    public SpecialSubscriptionResponseDto findById(String subsBillStdId, String effStaDt) {
        return toDto(findOrThrow(subsBillStdId, effStaDt));
    }

    @Override
    @Transactional
    public SpecialSubscriptionResponseDto create(SpecialSubscriptionRequestDto dto) {
        SpecialSubscriptionId id = new SpecialSubscriptionId(dto.getSubsBillStdId(), dto.getEffStaDt());
        if (repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "이미 존재하는 특수가입입니다: " + dto.getSubsBillStdId() + "/" + dto.getEffStaDt());
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
        entity.setRmk(dto.getRmk());
        entity.setCreatedBy(SecurityUtils.getCurrentUserId());
        entity.setCreatedDt(LocalDateTime.now());

        return toDto(repository.save(entity));
    }

    @Override
    @Transactional
    public SpecialSubscriptionResponseDto update(String subsBillStdId, String effStaDt, SpecialSubscriptionRequestDto dto) {
        SpecialSubscription entity = findOrThrow(subsBillStdId, effStaDt);

        // PK 필드(subsBillStdId, effStaDt) 변경 불가
        entity.setSubsId(dto.getSubsId());
        entity.setSvcCd(dto.getSvcCd());
        entity.setEffEndDt(dto.getEffEndDt());
        entity.setLastEffYn(dto.getLastEffYn());
        entity.setSpecSubsStatCd(dto.getSpecSubsStatCd());
        entity.setCntrcCapKmh(dto.getCntrcCapKmh());
        entity.setCntrcAmt(dto.getCntrcAmt());
        entity.setDscRt(dto.getDscRt());
        entity.setRmk(dto.getRmk());
        entity.setUpdatedBy(SecurityUtils.getCurrentUserId());
        entity.setUpdatedDt(LocalDateTime.now());

        return toDto(repository.save(entity));
    }

    @Override
    @Transactional
    public void delete(String subsBillStdId, String effStaDt) {
        findOrThrow(subsBillStdId, effStaDt);
        repository.deleteById(new SpecialSubscriptionId(subsBillStdId, effStaDt));
    }

    // ── 내부 헬퍼 ────────────────────────────────────────────────

    private SpecialSubscription findOrThrow(String subsBillStdId, String effStaDt) {
        SpecialSubscriptionId id = new SpecialSubscriptionId(subsBillStdId, effStaDt);
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "특수가입을 찾을 수 없습니다: " + subsBillStdId + "/" + effStaDt));
    }

    private SpecialSubscriptionResponseDto toDto(SpecialSubscription e) {
        SpecialSubscriptionResponseDto dto = new SpecialSubscriptionResponseDto();
        dto.setSubsBillStdId(e.getId().getSubsBillStdId());
        dto.setEffStaDt(e.getId().getEffStaDt());
        dto.setSubsId(e.getSubsId());
        dto.setSvcCd(e.getSvcCd());
        dto.setEffEndDt(e.getEffEndDt());
        dto.setLastEffYn(e.getLastEffYn());
        dto.setSpecSubsStatCd(e.getSpecSubsStatCd());
        dto.setCntrcCapKmh(e.getCntrcCapKmh());
        dto.setCntrcAmt(e.getCntrcAmt());
        dto.setDscRt(e.getDscRt());
        dto.setRmk(e.getRmk());
        dto.setCreatedBy(e.getCreatedBy());
        dto.setCreatedDt(e.getCreatedDt());
        dto.setUpdatedBy(e.getUpdatedBy());
        dto.setUpdatedDt(e.getUpdatedDt());
        return dto;
    }
}
