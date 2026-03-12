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
public class SpclSubsMthBillQtyServiceImpl implements SpclSubsMthBillQtyService {

    private final SpclSubsMthBillQtyRepository repository;

    public SpclSubsMthBillQtyServiceImpl(SpclSubsMthBillQtyRepository repository) {
        this.repository = repository;
    }

    @Override
    public Page<SpclSubsMthBillQtyResponseDto> findPage(String spclSubsId, Pageable pageable) {
        Specification<SpclSubsMthBillQty> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (spclSubsId != null && !spclSubsId.isBlank()) {
                predicates.add(cb.like(root.get("id").get("spclSubsId"), "%" + spclSubsId + "%"));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        return repository.findAll(spec, pageable).map(this::toDto);
    }

    @Override
    public SpclSubsMthBillQtyResponseDto findById(String spclSubsId, String useMth) {
        return toDto(findOrThrow(spclSubsId, useMth));
    }

    @Override
    @Transactional
    public SpclSubsMthBillQtyResponseDto create(SpclSubsMthBillQtyRequestDto dto) {
        SpclSubsMthBillQtyId id = new SpclSubsMthBillQtyId(dto.getSpclSubsId(), dto.getUseMth());
        if (repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "이미 존재하는 특수가입별 월별과금량입니다: " + dto.getSpclSubsId() + "/" + dto.getUseMth());
        }

        SpclSubsMthBillQty entity = new SpclSubsMthBillQty();
        entity.setId(id);
        entity.setSubsId(dto.getSubsId());
        entity.setBillStdId(dto.getBillStdId());
        entity.setPue(dto.getPue());
        entity.setCreatedBy(SecurityUtils.getCurrentUserId());
        entity.setCreatedDt(LocalDateTime.now());

        return toDto(repository.save(entity));
    }

    @Override
    @Transactional
    public SpclSubsMthBillQtyResponseDto update(String spclSubsId, String useMth, SpclSubsMthBillQtyRequestDto dto) {
        SpclSubsMthBillQty entity = findOrThrow(spclSubsId, useMth);

        entity.setSubsId(dto.getSubsId());
        entity.setBillStdId(dto.getBillStdId());
        entity.setPue(dto.getPue());
        entity.setUpdatedBy(SecurityUtils.getCurrentUserId());
        entity.setUpdatedDt(LocalDateTime.now());

        return toDto(repository.save(entity));
    }

    @Override
    @Transactional
    public void delete(String spclSubsId, String useMth) {
        findOrThrow(spclSubsId, useMth);
        repository.deleteById(new SpclSubsMthBillQtyId(spclSubsId, useMth));
    }

    private SpclSubsMthBillQty findOrThrow(String spclSubsId, String useMth) {
        SpclSubsMthBillQtyId id = new SpclSubsMthBillQtyId(spclSubsId, useMth);
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "특수가입별 월별과금량을 찾을 수 없습니다: " + spclSubsId + "/" + useMth));
    }

    private SpclSubsMthBillQtyResponseDto toDto(SpclSubsMthBillQty e) {
        SpclSubsMthBillQtyResponseDto dto = new SpclSubsMthBillQtyResponseDto();
        dto.setSpclSubsId(e.getId().getSpclSubsId());
        dto.setUseMth(e.getId().getUseMth());
        dto.setSubsId(e.getSubsId());
        dto.setBillStdId(e.getBillStdId());
        dto.setPue(e.getPue());
        dto.setCreatedBy(e.getCreatedBy());
        dto.setCreatedDt(e.getCreatedDt());
        dto.setUpdatedBy(e.getUpdatedBy());
        dto.setUpdatedDt(e.getUpdatedDt());
        return dto;
    }
}
