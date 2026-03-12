package com.example.vibestudy;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SubsMthBillQtyServiceImpl implements SubsMthBillQtyService {

    private final SubsMthBillQtyRepository repository;

    public SubsMthBillQtyServiceImpl(SubsMthBillQtyRepository repository) {
        this.repository = repository;
    }

    @Override
    public Page<SubsMthBillQtyResponseDto> findPage(String keyword, String useMthFrom, String useMthTo, Pageable pageable) {
        Specification<SubsMthBillQty> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (keyword != null && !keyword.isBlank()) {
                predicates.add(cb.like(root.get("id").get("subsId"), "%" + keyword + "%"));
            }
            if (useMthFrom != null && !useMthFrom.isBlank()) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("id").get("useMth"), useMthFrom));
            }
            if (useMthTo != null && !useMthTo.isBlank()) {
                predicates.add(cb.lessThanOrEqualTo(root.get("id").get("useMth"), useMthTo));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        return repository.findAll(spec, pageable).map(this::toDto);
    }

    private SubsMthBillQtyResponseDto toDto(SubsMthBillQty e) {
        SubsMthBillQtyResponseDto dto = new SubsMthBillQtyResponseDto();
        dto.setSubsId(e.getId().getSubsId());
        dto.setUseMth(e.getId().getUseMth());
        dto.setBillStdId(e.getBillStdId());
        dto.setUseQty(e.getUseQty());
        dto.setCreatedBy(e.getCreatedBy());
        dto.setCreatedDt(e.getCreatedDt());
        dto.setUpdatedBy(e.getUpdatedBy());
        dto.setUpdatedDt(e.getUpdatedDt());
        return dto;
    }
}
