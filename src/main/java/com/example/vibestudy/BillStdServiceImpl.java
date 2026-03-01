package com.example.vibestudy;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class BillStdServiceImpl implements BillStdService {

    private static final LocalDateTime DEFAULT_EFF_END_DT =
            LocalDateTime.of(9999, 12, 31, 23, 59, 59);

    private static final DateTimeFormatter ID_FORMATTER =
            DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");

    private final BillStdRepository repository;

    public BillStdServiceImpl(BillStdRepository repository) {
        this.repository = repository;
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
            repository.save(existing);
        }

        BillStd entity = new BillStd();

        entity.setBillStdId(generateId());
        entity.setSubsId(dto.getSubsId());
        entity.setBillStdRegDt(dto.getBillStdRegDt());
        entity.setSvcCd(dto.getSvcCd());
        entity.setLastEffYn("Y");
        entity.setEffStartDt(dto.getEffStartDt());
        entity.setEffEndDt(dto.getEffEndDt() != null ? dto.getEffEndDt() : DEFAULT_EFF_END_DT);
        entity.setStdRegStatCd(dto.getStdRegStatCd());
        entity.setBillStdStatCd(dto.getBillStdStatCd());
        entity.setPwrMetCalcMethCd(dto.getPwrMetCalcMethCd());
        entity.setUprcDetMethCd(dto.getUprcDetMethCd());
        entity.setMeteringUnitPriceAmt(dto.getMeteringUnitPriceAmt());
        entity.setBillQty(dto.getBillQty());
        entity.setPueDetMethCd(dto.getPueDetMethCd());
        entity.setPue1Rt(dto.getPue1Rt());
        entity.setPue2Rt(dto.getPue2Rt());
        entity.setFirstDscRt(dto.getFirstDscRt());
        entity.setSecondDscRt(dto.getSecondDscRt());
        entity.setLossCompRt(dto.getLossCompRt());
        entity.setCntrcCapKmh(dto.getCntrcCapKmh());
        entity.setCntrcAmt(dto.getCntrcAmt());
        entity.setDscAmt(dto.getDscAmt());
        entity.setDailyUnitPrice(dto.getDailyUnitPrice());
        entity.setCreatedBy(dto.getCreatedBy());
        entity.setCreatedDt(LocalDateTime.now());

        return toDto(repository.save(entity));
    }

    // ── 수정 ─────────────────────────────────────────────────────

    @Override
    public BillStdResponseDto update(String billStdId, BillStdRequestDto dto) {
        BillStd entity = findOrThrow(billStdId);

        // subsId / createdBy / createdDt 변경 불가
        entity.setBillStdRegDt(dto.getBillStdRegDt());
        entity.setSvcCd(dto.getSvcCd());
        entity.setLastEffYn(dto.getLastEffYn());
        entity.setEffStartDt(dto.getEffStartDt());
        entity.setEffEndDt(dto.getEffEndDt());
        entity.setStdRegStatCd(dto.getStdRegStatCd());
        entity.setBillStdStatCd(dto.getBillStdStatCd());
        entity.setPwrMetCalcMethCd(dto.getPwrMetCalcMethCd());
        entity.setUprcDetMethCd(dto.getUprcDetMethCd());
        entity.setMeteringUnitPriceAmt(dto.getMeteringUnitPriceAmt());
        entity.setBillQty(dto.getBillQty());
        entity.setPueDetMethCd(dto.getPueDetMethCd());
        entity.setPue1Rt(dto.getPue1Rt());
        entity.setPue2Rt(dto.getPue2Rt());
        entity.setFirstDscRt(dto.getFirstDscRt());
        entity.setSecondDscRt(dto.getSecondDscRt());
        entity.setLossCompRt(dto.getLossCompRt());
        entity.setCntrcCapKmh(dto.getCntrcCapKmh());
        entity.setCntrcAmt(dto.getCntrcAmt());
        entity.setDscAmt(dto.getDscAmt());
        entity.setDailyUnitPrice(dto.getDailyUnitPrice());
        entity.setUpdatedBy(dto.getCreatedBy());
        entity.setUpdatedDt(LocalDateTime.now());

        return toDto(repository.save(entity));
    }

    // ── 삭제 ─────────────────────────────────────────────────────

    @Override
    public void delete(String billStdId) {
        BillStd entity = findOrThrow(billStdId);
        long historyCount = repository.countBySubsId(entity.getSubsId());
        if (historyCount > 1) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "다른 이력이 존재하여 삭제할 수 없습니다.");
        }
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

    private BillStdResponseDto toDto(BillStd e) {
        BillStdResponseDto dto = new BillStdResponseDto();
        dto.setBillStdId(e.getBillStdId());
        dto.setSubsId(e.getSubsId());
        dto.setBillStdRegDt(e.getBillStdRegDt());
        dto.setSvcCd(e.getSvcCd());
        dto.setLastEffYn(e.getLastEffYn());
        dto.setEffStartDt(e.getEffStartDt());
        dto.setEffEndDt(e.getEffEndDt());
        dto.setStdRegStatCd(e.getStdRegStatCd());
        dto.setBillStdStatCd(e.getBillStdStatCd());
        dto.setPwrMetCalcMethCd(e.getPwrMetCalcMethCd());
        dto.setUprcDetMethCd(e.getUprcDetMethCd());
        dto.setMeteringUnitPriceAmt(e.getMeteringUnitPriceAmt());
        dto.setBillQty(e.getBillQty());
        dto.setPueDetMethCd(e.getPueDetMethCd());
        dto.setPue1Rt(e.getPue1Rt());
        dto.setPue2Rt(e.getPue2Rt());
        dto.setFirstDscRt(e.getFirstDscRt());
        dto.setSecondDscRt(e.getSecondDscRt());
        dto.setLossCompRt(e.getLossCompRt());
        dto.setCntrcCapKmh(e.getCntrcCapKmh());
        dto.setCntrcAmt(e.getCntrcAmt());
        dto.setDscAmt(e.getDscAmt());
        dto.setDailyUnitPrice(e.getDailyUnitPrice());
        dto.setCreatedBy(e.getCreatedBy());
        dto.setCreatedDt(e.getCreatedDt());
        dto.setUpdatedBy(e.getUpdatedBy());
        dto.setUpdatedDt(e.getUpdatedDt());
        return dto;
    }
}
