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
public class SpclSubsMthBillElemServiceImpl implements SpclSubsMthBillElemService {

    private final SpclSubsMthBillElemRepository repository;

    public SpclSubsMthBillElemServiceImpl(SpclSubsMthBillElemRepository repository) {
        this.repository = repository;
    }

    @Override
    public Page<SpclSubsMthBillElemResponseDto> findPage(String spclSubsId, Pageable pageable) {
        Specification<SpclSubsMthBillElem> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (spclSubsId != null && !spclSubsId.isBlank()) {
                predicates.add(cb.like(root.get("id").get("spclSubsId"), "%" + spclSubsId + "%"));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        return repository.findAll(spec, pageable).map(this::toDto);
    }

    @Override
    public SpclSubsMthBillElemResponseDto findById(String spclSubsId, String billMth) {
        return toDto(findOrThrow(spclSubsId, billMth));
    }

    @Override
    @Transactional
    public SpclSubsMthBillElemResponseDto create(SpclSubsMthBillElemRequestDto dto) {
        SpclSubsMthBillElemId id = new SpclSubsMthBillElemId(dto.getSpclSubsId(), dto.getBillMth());
        if (repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "이미 존재하는 특수가입별 월별빌링요소입니다: " + dto.getSpclSubsId() + "/" + dto.getBillMth());
        }

        SpclSubsMthBillElem entity = new SpclSubsMthBillElem();
        entity.setId(id);
        entity.setSubsId(dto.getSubsId());
        entity.setCalcAmt(dto.getCalcAmt());
        entity.setBillAmt(dto.getBillAmt());
        entity.setCreatedBy(SecurityUtils.getCurrentUserId());
        entity.setCreatedDt(LocalDateTime.now());

        return toDto(repository.save(entity));
    }

    @Override
    @Transactional
    public SpclSubsMthBillElemResponseDto update(String spclSubsId, String billMth, SpclSubsMthBillElemRequestDto dto) {
        SpclSubsMthBillElem entity = findOrThrow(spclSubsId, billMth);

        entity.setSubsId(dto.getSubsId());
        entity.setCalcAmt(dto.getCalcAmt());
        entity.setBillAmt(dto.getBillAmt());
        entity.setUpdatedBy(SecurityUtils.getCurrentUserId());
        entity.setUpdatedDt(LocalDateTime.now());

        return toDto(repository.save(entity));
    }

    @Override
    @Transactional
    public void delete(String spclSubsId, String billMth) {
        findOrThrow(spclSubsId, billMth);
        repository.deleteById(new SpclSubsMthBillElemId(spclSubsId, billMth));
    }

    private SpclSubsMthBillElem findOrThrow(String spclSubsId, String billMth) {
        SpclSubsMthBillElemId id = new SpclSubsMthBillElemId(spclSubsId, billMth);
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "특수가입별 월별빌링요소를 찾을 수 없습니다: " + spclSubsId + "/" + billMth));
    }

    private SpclSubsMthBillElemResponseDto toDto(SpclSubsMthBillElem e) {
        SpclSubsMthBillElemResponseDto dto = new SpclSubsMthBillElemResponseDto();
        dto.setSpclSubsId(e.getId().getSpclSubsId());
        dto.setBillMth(e.getId().getBillMth());
        dto.setSubsId(e.getSubsId());
        dto.setCalcAmt(e.getCalcAmt());
        dto.setBillAmt(e.getBillAmt());
        dto.setCreatedBy(e.getCreatedBy());
        dto.setCreatedDt(e.getCreatedDt());
        dto.setUpdatedBy(e.getUpdatedBy());
        dto.setUpdatedDt(e.getUpdatedDt());
        return dto;
    }
}
