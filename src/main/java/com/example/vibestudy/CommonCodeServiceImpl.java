package com.example.vibestudy;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommonCodeServiceImpl implements CommonCodeService {

    private final CommonCodeRepository commonCodeRepository;
    private final CommonDtlCodeRepository commonDtlCodeRepository;

    public CommonCodeServiceImpl(CommonCodeRepository commonCodeRepository,
                                  CommonDtlCodeRepository commonDtlCodeRepository) {
        this.commonCodeRepository = commonCodeRepository;
        this.commonDtlCodeRepository = commonDtlCodeRepository;
    }

    @Override
    public List<CommonCodeResponseDto> findAll(String commonCode, String commonCodeNm) {
        List<CommonCode> list;
        if ((commonCode == null || commonCode.isBlank()) && (commonCodeNm == null || commonCodeNm.isBlank())) {
            list = commonCodeRepository.findAll();
        } else {
            String code = commonCode != null ? commonCode : "";
            String nm = commonCodeNm != null ? commonCodeNm : "";
            list = commonCodeRepository.findByCommonCodeContainingAndCommonCodeNmContaining(code, nm);
        }
        return list.stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public CommonCodeResponseDto create(CommonCodeRequestDto dto) {
        if (commonCodeRepository.existsById(dto.getCommonCode())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 존재하는 공통코드입니다: " + dto.getCommonCode());
        }
        CommonCode entity = new CommonCode();
        entity.setCommonCode(dto.getCommonCode());
        entity.setCommonCodeNm(dto.getCommonCodeNm());
        entity.setEffStartDt(dto.getEffStartDt());
        entity.setEffEndDt(dto.getEffEndDt());
        entity.setRemark(dto.getRemark());
        entity.setCreatedBy(SecurityUtils.getCurrentUserId());
        entity.setCreatedDt(LocalDateTime.now());
        return toDto(commonCodeRepository.save(entity));
    }

    @Override
    public CommonCodeResponseDto update(String commonCode, CommonCodeRequestDto dto) {
        CommonCode entity = findOrThrow(commonCode);
        entity.setCommonCodeNm(dto.getCommonCodeNm());
        entity.setEffStartDt(dto.getEffStartDt());
        entity.setEffEndDt(dto.getEffEndDt());
        entity.setRemark(dto.getRemark());
        entity.setUpdatedBy(SecurityUtils.getCurrentUserId());
        entity.setUpdatedDt(LocalDateTime.now());
        return toDto(commonCodeRepository.save(entity));
    }

    @Override
    public void delete(String commonCode) {
        if (commonDtlCodeRepository.existsByIdCommonCode(commonCode)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "연결된 상세코드가 존재하여 삭제할 수 없습니다: " + commonCode);
        }
        findOrThrow(commonCode);
        commonCodeRepository.deleteById(commonCode);
    }

    @Override
    public List<CommonDtlCodeResponseDto> findDetails(String commonCode) {
        return commonDtlCodeRepository.findByIdCommonCodeOrderBySortOrder(commonCode)
                .stream().map(this::toDtlDto).collect(Collectors.toList());
    }

    @Override
    public List<CommonDtlCodeResponseDto> findEffectiveDetails(String commonCode) {
        LocalDateTime now = LocalDateTime.now();
        return commonDtlCodeRepository
                .findByIdCommonCodeAndEffStartDtLessThanEqualAndEffEndDtGreaterThanEqualOrderBySortOrder(
                        commonCode, now, now)
                .stream().map(this::toDtlDto).collect(Collectors.toList());
    }

    @Override
    public CommonDtlCodeResponseDto createDetail(String commonCode, CommonDtlCodeRequestDto dto) {
        CommonDtlCodeId id = new CommonDtlCodeId(commonCode, dto.getCommonDtlCode());
        if (commonDtlCodeRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "이미 존재하는 상세코드입니다: " + commonCode + "/" + dto.getCommonDtlCode());
        }
        CommonDtlCode entity = new CommonDtlCode();
        entity.setId(id);
        entity.setCommonDtlCodeNm(dto.getCommonDtlCodeNm());
        entity.setSortOrder(dto.getSortOrder());
        entity.setEffStartDt(dto.getEffStartDt());
        entity.setEffEndDt(dto.getEffEndDt());
        entity.setRemark(dto.getRemark());
        entity.setCreatedBy(SecurityUtils.getCurrentUserId());
        entity.setCreatedDt(LocalDateTime.now());
        return toDtlDto(commonDtlCodeRepository.save(entity));
    }

    @Override
    public CommonDtlCodeResponseDto updateDetail(String commonCode, String dtlCode, CommonDtlCodeRequestDto dto) {
        CommonDtlCode entity = findDtlOrThrow(commonCode, dtlCode);
        entity.setCommonDtlCodeNm(dto.getCommonDtlCodeNm());
        entity.setSortOrder(dto.getSortOrder());
        entity.setEffStartDt(dto.getEffStartDt());
        entity.setEffEndDt(dto.getEffEndDt());
        entity.setRemark(dto.getRemark());
        entity.setUpdatedBy(SecurityUtils.getCurrentUserId());
        entity.setUpdatedDt(LocalDateTime.now());
        return toDtlDto(commonDtlCodeRepository.save(entity));
    }

    @Override
    public void deleteDetail(String commonCode, String dtlCode) {
        CommonDtlCodeId id = new CommonDtlCodeId(commonCode, dtlCode);
        findDtlOrThrow(commonCode, dtlCode);
        commonDtlCodeRepository.deleteById(id);
    }

    // ── 내부 헬퍼 ────────────────────────────────────────────────

    private CommonCode findOrThrow(String commonCode) {
        return commonCodeRepository.findById(commonCode)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "공통코드를 찾을 수 없습니다: " + commonCode));
    }

    private CommonDtlCode findDtlOrThrow(String commonCode, String dtlCode) {
        CommonDtlCodeId id = new CommonDtlCodeId(commonCode, dtlCode);
        return commonDtlCodeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "상세코드를 찾을 수 없습니다: " + commonCode + "/" + dtlCode));
    }

    private CommonCodeResponseDto toDto(CommonCode entity) {
        CommonCodeResponseDto dto = new CommonCodeResponseDto();
        dto.setCommonCode(entity.getCommonCode());
        dto.setCommonCodeNm(entity.getCommonCodeNm());
        dto.setEffStartDt(entity.getEffStartDt());
        dto.setEffEndDt(entity.getEffEndDt());
        dto.setRemark(entity.getRemark());
        dto.setCreatedBy(entity.getCreatedBy());
        dto.setCreatedDt(entity.getCreatedDt());
        dto.setUpdatedBy(entity.getUpdatedBy());
        dto.setUpdatedDt(entity.getUpdatedDt());
        return dto;
    }

    private CommonDtlCodeResponseDto toDtlDto(CommonDtlCode entity) {
        CommonDtlCodeResponseDto dto = new CommonDtlCodeResponseDto();
        dto.setCommonCode(entity.getId().getCommonCode());
        dto.setCommonDtlCode(entity.getId().getCommonDtlCode());
        dto.setCommonDtlCodeNm(entity.getCommonDtlCodeNm());
        dto.setSortOrder(entity.getSortOrder());
        dto.setEffStartDt(entity.getEffStartDt());
        dto.setEffEndDt(entity.getEffEndDt());
        dto.setRemark(entity.getRemark());
        dto.setCreatedBy(entity.getCreatedBy());
        dto.setCreatedDt(entity.getCreatedDt());
        dto.setUpdatedBy(entity.getUpdatedBy());
        dto.setUpdatedDt(entity.getUpdatedDt());
        return dto;
    }
}
