# 실행 기록 (Execution Log)

## 실행 일시
2026-03-10

## 단계별 완료 현황

### 단계 1: 스키마 및 Entity 변경 -- 완료
| 단계 | 파일 | 상태 |
|---|---|---|
| 1-1 | `src/main/resources/schema.sql` (tb_subscription admin_id) | 완료 |
| 1-2 | `src/main/resources/schema.sql` (tb_qna notice 컬럼) | 완료 |
| 1-3 | `Subscription.java` (adminId 필드+getter/setter) | 완료 |
| 1-4 | `SubscriptionRequestDto.java` (adminId) | 완료 |
| 1-5 | `SubscriptionResponseDto.java` (adminId) | 완료 |
| 1-6 | `Qna.java` (noticeYn, noticeStartDt, noticeEndDt) | 완료 |
| 1-7 | `QnaRequestDto.java` (공지 필드) | 완료 |
| 1-8 | `QnaResponseDto.java` (공지 필드) | 완료 |

### 단계 2: 백엔드 서비스/컨트롤러 -- 완료
| 단계 | 파일 | 상태 |
|---|---|---|
| 2-1 | `SubscriptionServiceImpl.java` (adminId 매핑) | 완료 |
| 2-2 | `QnaServiceImpl.java` (공지 매핑+정렬 변경) | 완료 |
| 2-3 | `QnaRepository.java` (findAllWithNoticeFirst, findByKeywordWithNoticeFirst) | 완료 |
| 2-4 | `BillStdRepository.java` (findByStdRegStatCdNotIn) | 완료 |
| 2-5 | `BillStdService.java` + `BillStdServiceImpl.java` (findTodoList) | 완료 |
| 2-6 | `BillStdController.java` (/todo 엔드포인트) | 완료 |
| 2-7 | `SubsBillStdResponseDto.java` (신규) | 완료 |
| 2-8 | `SubsBillStdService.java` (신규 인터페이스) | 완료 |
| 2-9 | `SubsBillStdServiceImpl.java` (신규) | 완료 |
| 2-10 | `SubsBillStdController.java` (신규) | 완료 |
| 2-11 | `MenuDataInitializer.java` (MNU015 추가) | 완료 |

### 단계 3: 프론트엔드 기존 화면 수정 -- 완료
| 단계 | 파일 | 상태 |
|---|---|---|
| 3-1 | `UserSearchPopup.vue` (신규 공통 컴포넌트) | 완료 |
| 3-2 | `SubscriptionPage.vue` (관리자ID 필드+팝업) | 완료 |
| 3-3 | `DashboardContent.vue` (TODO 카드) | 완료 |
| 3-4 | `billStdApi.js` (getTodoList) | 완료 |
| 3-5 | `CommonCodePage.vue` (유효일자 기본값) | 완료 |
| 3-6 | `QnaDetailPage.vue` (공지사항 입력) | 완료 |
| 3-7 | `QnaPage.vue` (공지사항 시각적 구분) | 완료 |
| 3-8 | `BillStdPage.vue` (route.query.subsId 자동 조회) | 완료 |

### 단계 4: 프론트엔드 신규 화면 -- 완료
| 단계 | 파일 | 상태 |
|---|---|---|
| 4-1 | `subsBillStdApi.js` (신규) | 완료 |
| 4-2 | `SubsBillStdListPage.vue` (신규) | 완료 |
| 4-3 | `router/index.js` (라우트 추가) | 완료 |

## 수정/생성된 파일 목록

### 백엔드 수정
- `src/main/resources/schema.sql`
- `src/main/java/com/example/vibestudy/Subscription.java`
- `src/main/java/com/example/vibestudy/SubscriptionRequestDto.java`
- `src/main/java/com/example/vibestudy/SubscriptionResponseDto.java`
- `src/main/java/com/example/vibestudy/SubscriptionServiceImpl.java`
- `src/main/java/com/example/vibestudy/Qna.java`
- `src/main/java/com/example/vibestudy/QnaRequestDto.java`
- `src/main/java/com/example/vibestudy/QnaResponseDto.java`
- `src/main/java/com/example/vibestudy/QnaServiceImpl.java`
- `src/main/java/com/example/vibestudy/QnaRepository.java`
- `src/main/java/com/example/vibestudy/BillStdService.java`
- `src/main/java/com/example/vibestudy/BillStdServiceImpl.java`
- `src/main/java/com/example/vibestudy/BillStdRepository.java`
- `src/main/java/com/example/vibestudy/BillStdController.java`
- `src/main/java/com/example/vibestudy/MenuDataInitializer.java`

### 백엔드 신규
- `src/main/java/com/example/vibestudy/SubsBillStdResponseDto.java`
- `src/main/java/com/example/vibestudy/SubsBillStdService.java`
- `src/main/java/com/example/vibestudy/SubsBillStdServiceImpl.java`
- `src/main/java/com/example/vibestudy/SubsBillStdController.java`

### 프론트엔드 수정
- `frontend-vue/src/pages/SubscriptionPage.vue`
- `frontend-vue/src/components/main/DashboardContent.vue`
- `frontend-vue/src/api/billStdApi.js`
- `frontend-vue/src/pages/CommonCodePage.vue`
- `frontend-vue/src/pages/QnaDetailPage.vue`
- `frontend-vue/src/pages/QnaPage.vue`
- `frontend-vue/src/pages/BillStdPage.vue`
- `frontend-vue/src/router/index.js`

### 프론트엔드 신규
- `frontend-vue/src/components/common/UserSearchPopup.vue`
- `frontend-vue/src/pages/SubsBillStdListPage.vue`
- `frontend-vue/src/api/subsBillStdApi.js`

## 이슈 기록

### 이슈 1: billStdNm 필드 부재
- **내용**: 계획서 2-7, 2-9에서 `b.billStdNm` (과금기준명) 필드를 참조하나, `BillStd` Entity와 `tb_bill_std` 스키마에 해당 필드가 존재하지 않음
- **계획서 확인**: 계획서의 변경 파일 목록에서도 `BillStd.java`에 `billStdNm` 추가가 언급되지 않음 (스키마 변경도 없음)
- **조치**: `SubsBillStdResponseDto`에는 계획서대로 `billStdNm` 필드를 포함하되, JPQL에서 `CAST(NULL AS string)` 으로 대체하여 컴파일/런타임 오류를 방지함. 프론트엔드 DataGrid 컬럼에도 계획서대로 `billStdNm` 컬럼을 포함 (현재 null로 표시됨)
- **권장 후속 조치**: `tb_bill_std`에 `bill_std_nm` 컬럼 추가 + `BillStd.java` Entity에 필드 추가 후, JPQL의 `CAST(NULL AS string)`을 `b.billStdNm`으로 교체
