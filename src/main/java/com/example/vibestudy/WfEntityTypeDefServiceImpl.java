package com.example.vibestudy;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WfEntityTypeDefServiceImpl implements WfEntityTypeDefService {

    private final WfEntityTypeDefRepository repository;

    public WfEntityTypeDefServiceImpl(WfEntityTypeDefRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<WfEntityTypeDefResponseDto> findAll() {
        return repository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    private WfEntityTypeDefResponseDto toDto(WfEntityTypeDef e) {
        WfEntityTypeDefResponseDto dto = new WfEntityTypeDefResponseDto();
        dto.setEntityTypeCd(e.getEntityTypeCd());
        dto.setEntityTypeNm(e.getEntityTypeNm());
        dto.setTableNm(e.getTableNm());
        dto.setPkColumn(e.getPkColumn());
        dto.setStatusColumn(e.getStatusColumn());
        dto.setStatusCdGroup(e.getStatusCdGroup());
        dto.setBizKeyColumn(e.getBizKeyColumn());
        dto.setBizKeyLabel(e.getBizKeyLabel());
        dto.setRoutePath(e.getRoutePath());
        dto.setCreatedBy(e.getCreatedBy());
        dto.setCreatedDt(e.getCreatedDt());
        dto.setUpdatedBy(e.getUpdatedBy());
        dto.setUpdatedDt(e.getUpdatedDt());
        return dto;
    }
}
