# 실행 기록

## 실행 일시
2026-03-10

## 단계별 완료 여부

| Step | 내용 | 상태 | 비고 |
|------|------|------|------|
| 1 | DB + 공통코드 (schema.sql, data.sql) | 완료 | svc_nm/fee_prod_nm → svc_cd/fee_prod_cd 컬럼 변경, fee_prod_cd 헤더+상세 6건 추가 |
| 2 | Entity/DTO 필드 변경 | 완료 | Subscription, RequestDto, ResponseDto, SubscriptionMainListResponseDto |
| 3 | Repository 변경 | 완료 | SubscriptionRepository(JpaSpecificationExecutor), SubscriptionMainRepository(페이징+countQuery), SpecialSubscriptionRepository(JpaSpecificationExecutor) |
| 4 | Service/Controller 페이징 전환 | 완료 | 3개 도메인 모두 Page<T> 반환으로 변경 |
| 5 | DataInitializer 수정 | 완료 | 코드값 사용, 30건으로 증설 |
| 6 | 프론트엔드 API 레이어 변경 | 완료 | *Page 함수 추가 (기존 함수 보존) |
| 7 | 프론트엔드 화면 변경 | 완료 | 4개 화면 모두 변경 |
| 8 | 콤보 글자 깨짐 확인 | 미확인 | 런타임 확인 필요 |

## 수정/생성된 파일 목록

### Backend (수정)
1. `src/main/resources/schema.sql` -- tb_subscription 컬럼 svc_nm/fee_prod_nm → svc_cd/fee_prod_cd
2. `src/main/resources/data.sql` -- fee_prod_cd 공통코드 헤더 + 상세 6건 추가
3. `src/main/java/com/example/vibestudy/Subscription.java` -- 필드 svcNm/feeProdNm → svcCd/feeProdCd
4. `src/main/java/com/example/vibestudy/SubscriptionRequestDto.java` -- 동일 필드 변경
5. `src/main/java/com/example/vibestudy/SubscriptionResponseDto.java` -- 동일 필드 변경
6. `src/main/java/com/example/vibestudy/SubscriptionMainListResponseDto.java` -- svcCd/feeProdCd + subsNm 추가
7. `src/main/java/com/example/vibestudy/SubscriptionRepository.java` -- JpaSpecificationExecutor 추가, findBy* 메서드 삭제
8. `src/main/java/com/example/vibestudy/SubscriptionMainRepository.java` -- 네이티브 쿼리 컬럼 변경 + countQuery + Pageable
9. `src/main/java/com/example/vibestudy/SpecialSubscriptionRepository.java` -- JpaSpecificationExecutor 추가, findBy* 메서드 삭제
10. `src/main/java/com/example/vibestudy/SubscriptionService.java` -- search → searchPage (Page<T> + Pageable)
11. `src/main/java/com/example/vibestudy/SubscriptionServiceImpl.java` -- Specification 기반 페이징 + 필드 매핑 변경
12. `src/main/java/com/example/vibestudy/SubscriptionController.java` -- Page<T> 반환 + Pageable 파라미터
13. `src/main/java/com/example/vibestudy/SubscriptionMainService.java` -- findList → findListPage (Page<T> + Pageable)
14. `src/main/java/com/example/vibestudy/SubscriptionMainServiceImpl.java` -- Page<Object[]> 매핑 변경 (6컬럼)
15. `src/main/java/com/example/vibestudy/SubscriptionMainController.java` -- Page<T> 반환 + svcNm → svcCd + Pageable
16. `src/main/java/com/example/vibestudy/SpecialSubscriptionService.java` -- findAll → findPage (Page<T> + Pageable)
17. `src/main/java/com/example/vibestudy/SpecialSubscriptionServiceImpl.java` -- Specification + 페이징
18. `src/main/java/com/example/vibestudy/SpecialSubscriptionController.java` -- Page<T> 반환 + Pageable
19. `src/main/java/com/example/vibestudy/SubscriptionDataInitializer.java` -- 코드값 사용 + 30건

### Frontend (수정)
20. `frontend-vue/src/api/subscriptionApi.js` -- searchSubscriptionsPage 추가
21. `frontend-vue/src/api/subscriptionMainApi.js` -- getSubscriptionMainListPage 추가
22. `frontend-vue/src/api/specialSubscriptionApi.js` -- getSpecialSubscriptionsPage 추가
23. `frontend-vue/src/pages/SubscriptionPage.vue` -- 페이징 + svcCd/feeProdCd 콤보 + 레이블 변경 + 검색유형 추가
24. `frontend-vue/src/pages/SubscriptionMainPage.vue` -- 페이징 + SVC_MAP 제거 + CommonCodeSelect + svcCd/feeProdCd
25. `frontend-vue/src/pages/SpecialSubscriptionPage.vue` -- 페이징 + "서비스코드" → "서비스" 레이블
26. `frontend-vue/src/pages/BillStdPage.vue` -- "서비스코드" → "서비스" 폼 레이블

### 생성
- `sessions/2026-03-10_그리드_개선/05_execution_log.md` (본 파일)

## 이슈

- BillStdPage.vue에는 DataGrid 그리드 columns 정의가 없음 (폼 기반 화면). 폼 레이블만 변경 완료.
- Step 8 (콤보 글자 깨짐)은 런타임 확인이 필요하며, 코드 레벨에서는 조치 완료.

## 검증 Fail 항목 수정 (2026-03-10)

| Fail ID | 내용 | 수정 파일 | 수정 내용 |
|---------|------|-----------|-----------|
| Fail-AC2 | 콤보 한글 깨짐 | `src/main/resources/application.properties` | `spring.sql.init.encoding=UTF-8` 추가 |
| Fail-R1 | DashboardContent.vue 회귀 파손 | `frontend-vue/src/components/main/DashboardContent.vue` | API 응답에서 `page.content` 배열 추출, `item.svcNm` → `item.svcCd` 변경 |
| Fail-R2 | SubscriptionSearchPopup.vue 회귀 파손 | `frontend-vue/src/components/common/SubscriptionSearchPopup.vue` | API 응답에서 `page.content` 배열 추출, `item.svcNm` → `item.svcCd` 변경 |

- `feeProdNm` 참조는 두 파일 모두 존재하지 않아 변경 불필요.
