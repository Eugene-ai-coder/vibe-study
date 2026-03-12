package com.example.vibestudy;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDateTime;

@Service
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository repository;
    private final BillStdRepository billStdRepository;
    private final SubscriptionMainRepository subscriptionMainRepository;
    private final TodoService todoService;
    private final UserRepository userRepository;

    public SubscriptionServiceImpl(SubscriptionRepository repository,
                                   BillStdRepository billStdRepository,
                                   SubscriptionMainRepository subscriptionMainRepository,
                                   TodoService todoService,
                                   UserRepository userRepository) {
        this.repository = repository;
        this.billStdRepository = billStdRepository;
        this.subscriptionMainRepository = subscriptionMainRepository;
        this.todoService = todoService;
        this.userRepository = userRepository;
    }

    @Override
    public Page<SubscriptionResponseDto> searchPage(String type, String keyword, Pageable pageable) {
        String kw = (keyword == null) ? "" : keyword.trim();

        Specification<Subscription> spec = (root, query, cb) -> {
            if (kw.isEmpty()) return cb.conjunction();
            if ("SUBS_STATUS_CD".equals(type)) {
                return cb.equal(root.get("subsStatusCd"), kw);
            } else if ("SVC_CD".equals(type)) {
                return cb.like(cb.lower(root.get("svcCd")), "%" + kw.toLowerCase() + "%");
            } else if ("FEE_PROD_CD".equals(type)) {
                return cb.like(cb.lower(root.get("feeProdCd")), "%" + kw.toLowerCase() + "%");
            } else if ("SUBS_NM".equals(type)) {
                return cb.like(cb.lower(root.get("subsNm")), "%" + kw.toLowerCase() + "%");
            } else {
                // SUBS_ID (기본)
                return cb.like(cb.lower(root.get("subsId")), "%" + kw.toLowerCase() + "%");
            }
        };
        return repository.findAll(spec, pageable).map(this::toDto);
    }

    @Override
    public SubscriptionResponseDto findById(String subsId) {
        return toDto(findOrThrow(subsId));
    }

    @Override
    @Transactional
    public SubscriptionResponseDto create(SubscriptionRequestDto dto) {
        if (repository.existsById(dto.getSubsId())) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "이미 등록된 가입ID입니다.");
        }
        Subscription entity = new Subscription();
        entity.setSubsId(dto.getSubsId());
        entity.setSubsNm(dto.getSubsNm());
        entity.setSvcCd(dto.getSvcCd());
        entity.setFeeProdCd(dto.getFeeProdCd());
        entity.setSubsStatusCd(dto.getSubsStatusCd());
        entity.setSubsDt(dto.getSubsDt());
        entity.setChgDt(dto.getChgDt());
        entity.setAdminId(dto.getAdminId());
        entity.setCreatedBy(SecurityUtils.getCurrentUserId());
        entity.setCreatedDt(LocalDateTime.now());
        Subscription saved = repository.save(entity);
        handleTodo(saved);
        return toDto(saved);
    }

    @Override
    @Transactional
    public SubscriptionResponseDto update(String subsId, SubscriptionRequestDto dto) {
        Subscription entity = findOrThrow(subsId);
        // subsId / createdBy / createdDt 변경 불가
        entity.setSubsNm(dto.getSubsNm());
        entity.setSvcCd(dto.getSvcCd());
        entity.setFeeProdCd(dto.getFeeProdCd());
        entity.setSubsStatusCd(dto.getSubsStatusCd());
        entity.setSubsDt(dto.getSubsDt());
        entity.setChgDt(dto.getChgDt());
        entity.setAdminId(dto.getAdminId());
        entity.setUpdatedBy(SecurityUtils.getCurrentUserId());
        entity.setUpdatedDt(LocalDateTime.now());
        Subscription saved = repository.save(entity);
        handleTodo(saved);
        return toDto(saved);
    }

    @Override
    public void delete(String subsId) {
        findOrThrow(subsId);  // 존재 확인 (없으면 404)
        long billStdCount = billStdRepository.countBySubsId(subsId);
        if (billStdCount > 0) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT, "과금기준이 존재하는 가입은 삭제할 수 없습니다.");
        }
        if (subscriptionMainRepository.existsBySubsId(subsId)) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT, "대표가입정보가 존재하는 가입은 삭제할 수 없습니다.");
        }
        repository.deleteById(subsId);
    }

    private void handleTodo(Subscription entity) {
        if ("PENDING".equals(entity.getSubsStatusCd())) {
            todoService.createTodo("SUBSCRIPTION", entity.getSubsId(), null,
                    entity.getAdminId(), "가입 검토 요청: " + entity.getSubsId());
        } else {
            todoService.completeTodo("SUBSCRIPTION", entity.getSubsId(), null);
        }
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
        dto.setSvcCd(e.getSvcCd());
        dto.setFeeProdCd(e.getFeeProdCd());
        dto.setSubsStatusCd(e.getSubsStatusCd());
        dto.setSubsDt(e.getSubsDt());
        dto.setChgDt(e.getChgDt());
        dto.setAdminId(e.getAdminId());
        if (e.getAdminId() != null) {
            userRepository.findById(e.getAdminId())
                .ifPresent(u -> dto.setAdminNickname(u.getNickname()));
        }
        dto.setCreatedBy(e.getCreatedBy());
        dto.setCreatedDt(e.getCreatedDt());
        dto.setUpdatedBy(e.getUpdatedBy());
        dto.setUpdatedDt(e.getUpdatedDt());
        return dto;
    }
}
