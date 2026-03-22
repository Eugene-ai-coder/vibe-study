package com.example.vibestudy;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class SubscriptionMainServiceImpl implements SubscriptionMainService {

    private final SubscriptionMainRepository repository;
    private final SubscriptionRepository subscriptionRepository;
    private final TransactionTemplate txTemplate;

    public SubscriptionMainServiceImpl(SubscriptionMainRepository repository,
                                       SubscriptionRepository subscriptionRepository,
                                       PlatformTransactionManager txManager) {
        this.repository = repository;
        this.subscriptionRepository = subscriptionRepository;
        this.txTemplate = new TransactionTemplate(txManager);
    }

    @Override
    public Page<SubscriptionMainListResponseDto> findListPage(String svcCd, String searchType, String keyword, Pageable pageable) {
        LocalDateTime now = LocalDateTime.now();
        Page<Object[]> page = repository.findListRaw(
            (svcCd == null || svcCd.isEmpty()) ? null : svcCd,
            searchType,
            keyword,
            now,
            pageable
        );
        return page.map(r -> {
            SubscriptionMainListResponseDto dto = new SubscriptionMainListResponseDto();
            dto.setSubsId((String) r[0]);
            dto.setSubsNm((String) r[1]);
            dto.setSvcCd((String) r[2]);
            dto.setBasicProdCd((String) r[3]);
            dto.setMainSubsYn(r[4] != null ? r[4].toString() : "N");
            dto.setMainSubsId((String) r[5]);
            return dto;
        });
    }

    @Override
    @Transactional
    public SubscriptionMainResponseDto save(SubscriptionMainRequestDto dto) {
        LocalDateTime now = LocalDateTime.now();

        if ("N".equals(dto.getMainSubsYn())) {
            if (dto.getMainSubsId() == null || dto.getMainSubsId().isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "대표가입ID를 입력해 주세요.");
            }
            if (!subscriptionRepository.existsById(dto.getMainSubsId())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "존재하지 않는 가입ID입니다: " + dto.getMainSubsId());
            }
        }

        repository.findActiveBySubsId(dto.getSubsId(), now).ifPresent(existing -> {
            existing.setEffEndDt(now);
            existing.setUpdatedBy(SecurityUtils.getCurrentUserId());
            existing.setUpdatedDt(now);
            repository.save(existing);
        });

        SubscriptionMain sm = new SubscriptionMain();
        sm.setSubsMainId(generateId());
        sm.setSubsId(dto.getSubsId());
        sm.setMainSubsYn(dto.getMainSubsYn());
        sm.setMainSubsId("Y".equals(dto.getMainSubsYn()) ? null : dto.getMainSubsId());
        sm.setEffStartDt(now);
        sm.setEffEndDt(IdGenerator.MAX_DT);
        sm.setCreatedBy(SecurityUtils.getCurrentUserId());
        sm.setCreatedDt(now);

        return toDto(repository.save(sm));
    }

    private String generateId() {
        return IdGenerator.generate("SM");
    }

    @Override
    public byte[] generateExcel(List<SubscriptionMainRequestDto> items) {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("대표가입");
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("가입ID");
            header.createCell(1).setCellValue("대표가입여부");
            header.createCell(2).setCellValue("대표가입ID");

            for (int i = 0; i < items.size(); i++) {
                SubscriptionMainRequestDto item = items.get(i);
                Row row = sheet.createRow(i + 1);
                row.createCell(0).setCellValue(item.getSubsId() != null ? item.getSubsId() : "");
                row.createCell(1).setCellValue(item.getMainSubsYn() != null ? item.getMainSubsYn() : "");
                row.createCell(2).setCellValue(item.getMainSubsId() != null ? item.getMainSubsId() : "");
            }

            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "엑셀 생성에 실패했습니다.");
        }
    }

    @Override
    public List<SubscriptionMainExcelResponseDto> parseExcel(MultipartFile file) {
        List<SubscriptionMainExcelResponseDto> results = new ArrayList<>();
        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                SubscriptionMainExcelResponseDto dto = new SubscriptionMainExcelResponseDto();
                dto.setSubsId(getCellString(row, 0));
                dto.setMainSubsYn(getCellString(row, 1));
                dto.setMainSubsId(getCellString(row, 2));
                results.add(dto);
            }
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "엑셀 파일을 읽을 수 없습니다.");
        }

        List<String> subsIds = results.stream()
            .map(SubscriptionMainExcelResponseDto::getSubsId)
            .filter(id -> id != null && !id.isBlank())
            .distinct()
            .toList();
        Set<String> existingIds = subsIds.isEmpty()
            ? Set.of()
            : subscriptionRepository.findExistingIds(subsIds);

        for (SubscriptionMainExcelResponseDto dto : results) {
            String subsId = dto.getSubsId();
            if (subsId == null || subsId.isBlank()) {
                dto.setValid(false);
                dto.setErrorMessage("가입ID가 비어있습니다.");
            } else if (!existingIds.contains(subsId)) {
                dto.setValid(false);
                dto.setErrorMessage("존재하지 않는 가입ID입니다: " + subsId);
            } else {
                dto.setValid(true);
            }
        }
        return results;
    }

    @Override
    public List<SubscriptionMainExcelResponseDto> saveBulk(SubscriptionMainBulkRequestDto dto) {
        List<SubscriptionMainExcelResponseDto> results = new ArrayList<>();
        for (SubscriptionMainRequestDto item : dto.getItems()) {
            SubscriptionMainExcelResponseDto result = new SubscriptionMainExcelResponseDto();
            result.setSubsId(item.getSubsId());
            result.setMainSubsYn(item.getMainSubsYn());
            result.setMainSubsId(item.getMainSubsId());
            try {
                txTemplate.executeWithoutResult(status -> save(item));
                result.setValid(true);
                result.setErrorMessage("저장완료");
            } catch (Exception e) {
                result.setValid(false);
                result.setErrorMessage(e.getMessage());
            }
            results.add(result);
        }
        return results;
    }

    private String getCellString(Row row, int cellIndex) {
        Cell cell = row.getCell(cellIndex);
        if (cell == null) return "";
        if (cell.getCellType() == CellType.NUMERIC) {
            return String.valueOf((long) cell.getNumericCellValue());
        }
        return cell.getStringCellValue() != null ? cell.getStringCellValue().trim() : "";
    }

    private SubscriptionMainResponseDto toDto(SubscriptionMain sm) {
        SubscriptionMainResponseDto dto = new SubscriptionMainResponseDto();
        dto.setSubsMainId(sm.getSubsMainId());
        dto.setSubsId(sm.getSubsId());
        dto.setMainSubsYn(sm.getMainSubsYn());
        dto.setMainSubsId(sm.getMainSubsId());
        dto.setEffStartDt(sm.getEffStartDt());
        dto.setEffEndDt(sm.getEffEndDt());
        dto.setCreatedBy(sm.getCreatedBy());
        dto.setCreatedDt(sm.getCreatedDt());
        dto.setUpdatedBy(sm.getUpdatedBy());
        dto.setUpdatedDt(sm.getUpdatedDt());
        return dto;
    }
}
