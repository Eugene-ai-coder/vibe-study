package com.example.vibestudy;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository repository;
    private final BillStdRepository billStdRepository;

    public SubscriptionServiceImpl(SubscriptionRepository repository,
                                   BillStdRepository billStdRepository) {
        this.repository = repository;
        this.billStdRepository = billStdRepository;
    }

    @Override
    public List<SubscriptionResponseDto> search(String type, String keyword) {
        List<Subscription> result;
        String kw = (keyword == null) ? "" : keyword.trim();

        if ("SUBS_STATUS_CD".equals(type)) {
            result = repository.findBySubsStatusCd(kw);
        } else if ("SVC_NM".equals(type)) {
            result = repository.findBySvcNmContainingIgnoreCase(kw);
        } else if ("FEE_PROD_NM".equals(type)) {
            result = repository.findByFeeProdNmContainingIgnoreCase(kw);
        } else {
            // SUBS_ID (기본)
            result = repository.findBySubsIdContainingIgnoreCase(kw);
        }
        return result.stream().map(this::toDto).toList();
    }

    @Override
    public SubscriptionResponseDto findById(String subsId) {
        return toDto(findOrThrow(subsId));
    }

    @Override
    public SubscriptionResponseDto create(SubscriptionRequestDto dto) {
        if (repository.existsById(dto.getSubsId())) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "이미 등록된 가입ID입니다.");
        }
        Subscription entity = new Subscription();
        entity.setSubsId(dto.getSubsId());
        entity.setSubsNm(dto.getSubsNm());
        entity.setSvcNm(dto.getSvcNm());
        entity.setFeeProdNm(dto.getFeeProdNm());
        entity.setSubsStatusCd(dto.getSubsStatusCd());
        entity.setSubsDt(dto.getSubsDt());
        entity.setChgDt(dto.getChgDt());
        entity.setCreatedBy(dto.getCreatedBy());
        entity.setCreatedDt(LocalDateTime.now());
        return toDto(repository.save(entity));
    }

    @Override
    public SubscriptionResponseDto update(String subsId, SubscriptionRequestDto dto) {
        Subscription entity = findOrThrow(subsId);
        // subsId / createdBy / createdDt 변경 불가
        entity.setSubsNm(dto.getSubsNm());
        entity.setSvcNm(dto.getSvcNm());
        entity.setFeeProdNm(dto.getFeeProdNm());
        entity.setSubsStatusCd(dto.getSubsStatusCd());
        entity.setSubsDt(dto.getSubsDt());
        entity.setChgDt(dto.getChgDt());
        entity.setUpdatedBy(dto.getCreatedBy());
        entity.setUpdatedDt(LocalDateTime.now());
        return toDto(repository.save(entity));
    }

    @Override
    public void delete(String subsId) {
        findOrThrow(subsId);  // 존재 확인 (없으면 404)
        long billStdCount = billStdRepository.countBySubsId(subsId);
        if (billStdCount > 0) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT, "과금기준이 존재하는 가입은 삭제할 수 없습니다.");
        }
        repository.deleteById(subsId);
    }

    private Subscription findOrThrow(String subsId) {
        return repository.findById(subsId)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "가입을 찾을 수 없습니다: " + subsId));
    }

    private SubscriptionResponseDto toDto(Subscription e) {
        SubscriptionResponseDto dto = new SubscriptionResponseDto();
        dto.setSubsId(e.getSubsId());
        dto.setSubsNm(e.getSubsNm());
        dto.setSvcNm(e.getSvcNm());
        dto.setFeeProdNm(e.getFeeProdNm());
        dto.setSubsStatusCd(e.getSubsStatusCd());
        dto.setSubsDt(e.getSubsDt());
        dto.setChgDt(e.getChgDt());
        dto.setCreatedBy(e.getCreatedBy());
        dto.setCreatedDt(e.getCreatedDt());
        dto.setUpdatedBy(e.getUpdatedBy());
        dto.setUpdatedDt(e.getUpdatedDt());
        return dto;
    }
}
