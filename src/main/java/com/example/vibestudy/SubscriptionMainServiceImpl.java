package com.example.vibestudy;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class SubscriptionMainServiceImpl implements SubscriptionMainService {

    private static final LocalDateTime MAX_DT = LocalDateTime.of(9999, 12, 31, 23, 59, 59);
    private static final DateTimeFormatter ID_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");

    private final SubscriptionMainRepository repository;
    private final SubscriptionRepository subscriptionRepository;

    public SubscriptionMainServiceImpl(SubscriptionMainRepository repository,
                                       SubscriptionRepository subscriptionRepository) {
        this.repository = repository;
        this.subscriptionRepository = subscriptionRepository;
    }

    @Override
    public List<SubscriptionMainListResponseDto> findList(String svcNm, String searchType, String keyword) {
        LocalDateTime now = LocalDateTime.now();
        List<Object[]> rows = repository.findListRaw(
            (svcNm == null || svcNm.isEmpty()) ? null : svcNm,
            searchType,
            keyword,
            now
        );
        return rows.stream().map(r -> {
            SubscriptionMainListResponseDto dto = new SubscriptionMainListResponseDto();
            dto.setSubsId((String) r[0]);
            dto.setSvcNm((String) r[1]);
            dto.setFeeProdNm((String) r[2]);
            dto.setMainSubsYn(r[3] != null ? r[3].toString() : "N");
            dto.setMainSubsId((String) r[4]);
            return dto;
        }).toList();
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
            existing.setUpdatedBy(dto.getCreatedBy());
            existing.setUpdatedDt(now);
            repository.save(existing);
        });

        SubscriptionMain sm = new SubscriptionMain();
        sm.setSubsMainId(generateId());
        sm.setSubsId(dto.getSubsId());
        sm.setMainSubsYn(dto.getMainSubsYn());
        sm.setMainSubsId("Y".equals(dto.getMainSubsYn()) ? null : dto.getMainSubsId());
        sm.setEffStartDt(now);
        sm.setEffEndDt(MAX_DT);
        sm.setCreatedBy(dto.getCreatedBy());
        sm.setCreatedDt(now);

        return toDto(repository.save(sm));
    }

    private String generateId() {
        return "SM" + LocalDateTime.now().format(ID_FORMATTER);
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
