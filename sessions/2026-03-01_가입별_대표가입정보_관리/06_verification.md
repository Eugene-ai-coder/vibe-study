# 6단계: 품질 검증 (Verification)

> 참조: 02_requirements.md, 04_final_plan.md, 05_execution_log.md

---

## 1. 수용 기준 충족 여부 (Acceptance Criteria)

| # | 수용 기준 | 판정 | 비고 |
|---|---|---|---|
| AC1 | 서비스=전체 + 조건 없음 조회 → 전체 목록 반환 | ⚠️ 조건변경 | 검토1 반영: keyword 필수(2자↑). 전체조회 대신 keyword 기반 전체 조회 |
| AC2 | 서비스≠전체 + 조건 없음 → 안내 메시지 표시 | ⚠️ 조건변경 | 검토1 반영: 서비스 무관 keyword 미입력 시 안내 메시지 표시 |
| AC3 | 대표가입여부='Y' 선택 → 대표가입ID 자동 비활성화 및 초기화 | ✅ Pass | SubscriptionMainForm.jsx L2-7 handleMainSubsYnChange 확인 |
| AC4 | 대표가입여부='N' + 대표가입ID 없음 저장 → 에러 처리 | ✅ Pass | ServiceImpl L52-59 유효성 체크 확인 |
| AC5 | 대표가입ID 미존재 가입ID 저장 → 에러 처리 | ✅ Pass | ServiceImpl L56-59 existsById 체크 확인 |
| AC6 | 저장 성공 → 기존 유효 레코드 종료 + 신규 INSERT 원자적 처리 | ✅ Pass | ServiceImpl @Transactional + findActiveBySubsId 패턴 확인 |
| AC7 | 가입검색 팝업 → 선택 → 대표가입ID 셋팅 → 팝업 닫힘 | ✅ Pass | SubscriptionSearchPopup.jsx handleSelect L28-31 확인 |
| AC8 | 모든 메인화면 MainLayout 적용 | ✅ Pass | BillStdPage, SubscriptionPage, StudyLogPage, SubscriptionMainPage 전체 확인 |
| AC9 | 목록 행 h-7(28px) 밀도 적용 | ✅ Pass | SubscriptionMainList.jsx, SubscriptionSearchPopup.jsx h-7 적용 확인 |

> AC1/AC2: 검토1에서 요구사항이 변경되어 04_final_plan.md에 반영됨. 설계자 승인 완료된 사항.

---

## 2. 계획 준수 여부

### 2.1 백엔드 (9개 파일)

| 파일 | 계획 | 실제 | 판정 |
|---|---|---|---|
| `SubscriptionMain.java` | Entity, 컬럼 8종 | 계획 동일 | ✅ |
| `SubscriptionMainRepository.java` | findActiveBySubsId / existsBySubsId / findListRaw | 3개 메서드 모두 구현, ON절 날짜조건 확인 | ✅ |
| `SubscriptionMainRequestDto.java` | subsId / mainSubsYn / mainSubsId / createdBy | 계획 동일 | ✅ |
| `SubscriptionMainResponseDto.java` | 전체 필드 | 계획 동일 | ✅ |
| `SubscriptionMainListResponseDto.java` | 5개 필드 | 계획 동일 | ✅ |
| `SubscriptionMainService.java` | Interface | 계획 동일 | ✅ |
| `SubscriptionMainServiceImpl.java` | @Transactional 저장 로직 | 계획 동일, toDto() 구현 포함 | ✅ |
| `SubscriptionMainController.java` | GET / POST | 계획 동일 | ✅ |
| `SubscriptionServiceImpl.java` | delete()에 existsBySubsId 409 체크 추가 | 계획 동일, 생성자 의존성 추가 확인 | ✅ |

### 2.2 프론트엔드 (13개 파일)

| 파일 | 계획 | 실제 | 판정 |
|---|---|---|---|
| `api/subscriptionMainApi.js` | getSubscriptionMainList / saveSubscriptionMain | 계획 동일 | ✅ |
| `hooks/useSubscriptionMain.js` | fetchList / handleSave | 계획 동일 | ✅ |
| `SubscriptionSearchPopup.jsx` | props: isOpen/onClose/onSelect, 기존 API 재사용 | 계획 동일, Enter 키 지원 | ✅ |
| `SubscriptionMainSearchBar.jsx` | 서비스/검색유형/키워드 + Enter 검색 | 계획 동일 | ✅ |
| `SubscriptionMainList.jsx` | 5컬럼, h-7, 선택 행 강조 | 계획 동일 | ✅ |
| `SubscriptionMainForm.jsx` | 5항목, 조건부 비활성화, 검색 버튼 | 계획 동일. subsId 없을 때 select disabled 추가(계획 외 보완) | ✅ |
| `SubscriptionMainActionBar.jsx` | fixed bottom-0, 저장 버튼 | 계획 동일 | ✅ |
| `SubscriptionMainPage.jsx` | SVC_MAP, 조회/저장 로직, MainLayout | 저장 후 목록 자동 갱신 + 선택 행 복원 추가(계획 외 보완) | ✅ |
| `App.jsx` | /subscription-main 라우트 | 계획 동일 | ✅ |
| `Sidebar.jsx` | 과금기준 하위 "대표가입 관리" | 계획 동일 | ✅ |
| `SubscriptionPage.jsx` | Layout → MainLayout | MainLayout 적용 확인 | ✅ |
| `BillStdPage.jsx` | Layout → MainLayout | MainLayout 적용 확인 | ✅ |
| `StudyLogPage.jsx` | Layout → MainLayout | MainLayout 적용 확인 | ✅ |

---

## 3. API 실동작 검증

### 3.1 서버 상태

| 항목 | 결과 |
|---|---|
| 서버 기동 여부 | ✅ 정상 (localhost:8080) |
| `/api/subscriptions` 응답 | ✅ 200 OK |
| `/api/subscription-main` 응답 | ✅ 200 OK (서버 재기동 후) |

### 3.2 TC 실행 결과

| TC | 설명 | 기대 | 실제 | 판정 |
|---|---|---|---|---|
| TC1 | 가입ID 전방일치 (keyword=SU) | 200, 10건 | 200, 10건 (SUBS0001~SUBS0009, SUS0011) | ✅ Pass |
| TC2 | svcNm=서비스1 + keyword=SU | 200, 3건 | 200, 3건 (SUBS0003/0006/0009) | ✅ Pass |
| TC3 | 대표가입ID 검색 keyword=SM (sm 없음) | 200, 0건 | 200, [] | ✅ Pass |
| TC4 | POST 저장 mainSubsYn=Y (SUBS0001) | 201 Created | 201, subsMainId=SM20260302003524797 | ✅ Pass |
| TC5 | POST 재저장 (기존 종료 + 신규 INSERT) | 201, 새 subsMainId | 201, subsMainId=SM20260302003531824 (신규) | ✅ Pass |
| TC6 | POST mainSubsYn=N + mainSubsId 없음 | 400 | 400 Bad Request | ✅ Pass |
| TC7 | POST mainSubsYn=N + 미존재 mainSubsId | 400 | 400 Bad Request | ✅ Pass |
| TC8 | DELETE SUBS0001 (sm 존재) | 409 | 409 Conflict | ✅ Pass |
| TC1-후 | TC4 이후 SUBS0001 조회 (sm 값 있음) | 200, mainSubsYn=Y | 200, mainSubsYn=Y (수정 후 재검증) | ✅ Pass |

### 3.3 TC1-후 버그 분석 (D-1)

**현상**: `tb_subscription_main`에 레코드가 없을 때(LEFT JOIN NULL)는 200 정상, 레코드 존재 시 500 발생

**재현 조건**: SUBS0001(sm 있음) 조회 → 500 / SUBS0002(sm 없음) 조회 → 200

**추정 원인**: `SubscriptionMainServiceImpl.findList()` L41의 타입 캐스트
```java
// 현재 코드
dto.setMainSubsYn(r[3] != null ? (String) r[3] : "N");
```
H2 데이터베이스에서 `VARCHAR(1)` 컬럼이 Native Query `Object[]`로 반환될 때
`Character` 타입으로 올 가능성 → `(String) r[3]` ClassCastException 발생 추정

**수정 방향**:
```java
// 수정안
dto.setMainSubsYn(r[3] != null ? r[3].toString() : "N");
```

**영향 범위**: 대표가입 관리 화면 목록 조회 전체 — **핵심 기능 결함** → ✅ 수정 완료 (r[3].toString())

---

## 4. 회귀 영향도 분석

### 4.1 수정 파일 기준 의존 관계

**`SubscriptionServiceImpl.java`** (delete() 수정)
- 직접 의존: `SubscriptionController.java` → `DELETE /api/subscriptions/{subsId}`
- 변경 성격: delete() 메서드에 조건 추가 (기존 billStd 체크 패턴과 동일)
- 영향 평가: **안전**. 기존 로직 변경 없이 guard 조건만 추가. 대표가입정보 없는 가입은 기존과 동일하게 삭제 가능.
- 잠재 파손: 없음

**신규 파일 (SubscriptionMain* 8종)**
- 기존 코드에 직접 의존 없음. 신규 Bean 등록만 발생.
- Spring Boot 자동 구성: Entity → JPA 테이블 자동 생성/검증 (`tb_subscription_main`)
- 잠재 파손: `tb_subscription_main` 테이블 스키마가 Entity 정의와 불일치 시 기동 실패 가능성 → 서버 재기동으로 확인 필요

**프론트엔드 수정 파일 (`App.jsx`, `Sidebar.jsx`, `3개 Page`)**
- App.jsx: 라우트 추가만 → 기존 라우트 영향 없음
- Sidebar.jsx: MENU 항목 추가만 → 기존 메뉴 영향 없음
- BillStdPage / SubscriptionPage / StudyLogPage: Layout → MainLayout 교체 → UI 레이아웃 변경만, 기능 로직 변경 없음

---

## 5. 코드 품질 확인

| 항목 | 결과 |
|---|---|
| Controller → Service → Repository 레이어 준수 | ✅ |
| REST API / 웹화면 분리 (@RestController 사용) | ✅ |
| @Transactional 적용 (save 메서드) | ✅ |
| 에러 응답 (ResponseStatusException → GlobalExceptionHandler) | ✅ |
| createdBy/dt, updatedBy/dt 시스템 필드 | ✅ Entity 모두 포함 |
| PK 채번 패턴 (SM + yyyyMMddHHmmssSSS) | ✅ |
| LEFT JOIN 유효기간 ON절 배치 | ✅ Repository 쿼리 확인 |
| now 파라미터 주입 (트랜잭션 내 시각 일관성) | ✅ |
| UI 밀도 (h-7, h-8, text-sm) | ✅ |
| MainLayout 적용 (전체 메인화면) | ✅ |

**잠재 이슈 1건:**
- `SubscriptionMainPage.jsx:95` — `createdBy: 'SYSTEM'` 하드코딩. 로그인 사용자 ID 연동 미적용 (요구사항 2.비기능 요건에 명시됨). 현재 로그인 사용자 ID 전달 메커니즘이 타 화면과 동일하게 하드코딩 패턴 사용 중이므로 시스템 전반 일관성 이슈. 별도 개선 과제로 등록 필요.

---

## 6. 잔여 이슈

| # | 이슈 | 심각도 | 조치 |
|---|---|---|---|
| D-1 | ~~목록 조회 500 오류~~ | ~~🔴 Critical~~ | ✅ 수정 완료 (`r[3].toString()`) |
| I-1 | `createdBy` 하드코딩 ('SYSTEM') | 🟢 Low | 로그인 연동 기능 구현 시 통합 개선 |

---

## 7. UI 회귀 체크리스트 (설계자 브라우저 확인)

> 서버 재기동 후 http://localhost:5173 접속하여 확인

### 7.1 레이아웃 통일
- [x] 가입관리 화면 `/subscriptions` — 헤더+LNB+본문 구조 확인
- [x] 과금기준 화면 `/bill-std` — 헤더+LNB+본문 구조 확인
- [x] 학습로그 화면 `/study-logs` — 헤더+LNB+본문 구조 확인

### 7.2 메뉴
- [x] LNB 가입관리 그룹에 "대표가입 관리" 표시
- [x] "대표가입 관리" 클릭 → `/subscription-main` 이동
- [x] 현재 페이지 메뉴 파란색 강조 표시

### 7.3 대표가입 관리 화면 기본
- [x] 조회조건 영역 (서비스 콤보, 검색유형 콤보, 키워드 입력, 조회 버튼) 표시
- [x] 목록 영역 (5컬럼 테이블 헤더) 표시
- [x] 상세 영역 (5항목 폼) 표시
- [x] 저장 버튼 (fixed bottom-0 플로팅) 표시

### 7.4 조회 유효성
- [x] 키워드 미입력 + 조회 → "조회조건을 입력해 주세요." 메시지
- [x] 키워드 1자 + 조회 → "조회조건은 2자 이상 입력해 주세요." 메시지
- [x] 키워드 2자 이상 + 조회 → 목록 표시 (결과 없으면 빈 테이블)
- [x] Enter 키 → 조회 실행

### 7.5 목록 → 폼 연동
- [x] 목록 행 클릭 → 상세 폼에 가입ID, 대표가입여부 표시
- [x] 선택 행 파란색 배경 강조

### 7.6 폼 동작
- [x] 대표가입여부 = Y → 대표가입ID 필드 비활성화(회색), 검색 버튼 비활성화
- [x] 대표가입여부 = N → 대표가입ID 필드 활성화, 검색 버튼 활성화
- [x] Y → N 전환 후 다시 Y 선택 → 대표가입ID 필드 자동 초기화

### 7.7 가입검색 팝업
- [x] 검색 버튼 클릭 → 팝업 모달 표시
- [x] 팝업 내 키워드 입력 후 조회 → 가입 목록 표시
- [x] 팝업 목록 행 클릭 → 대표가입ID 필드에 선택한 가입ID 셋팅 → 팝업 닫힘
- [x] 팝업 × 버튼 → 팝업 닫힘

### 7.8 저장
- [x] 목록 미선택 상태에서 저장 → "목록에서 가입을 선택해 주세요." 에러 Toast
- [x] 대표가입여부=N + 대표가입ID 없음 → 서버 400 에러 Toast 표시
- [x] 정상 저장 → "저장이 완료되었습니다." 성공 Toast + 목록 자동 갱신

---

## 8. 종합 판정

| 구분 | 결과 |
|---|---|
| 코드 구조/계획 준수 | ✅ 전체 22개 파일 계획 대비 완료 |
| 수용 기준 충족 | ✅ (AC1/AC2 설계자 승인 변경 포함) |
| API 실동작 | ✅ TC 전체 Pass (TC1~8 + TC1-후) |
| 회귀 영향도 | ✅ 안전 (기존 기능 파손 없음) |
| 잔여 이슈 | 1건 (I-1 Low) |

**✅ 전체 파이프라인 완료. 잔여 이슈 I-1(createdBy 하드코딩)은 로그인 연동 시 통합 개선.**
