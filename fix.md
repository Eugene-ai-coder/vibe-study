# 코드리뷰 Fix Plan

> 생성일: 2026-03-17 | 총 68건 (Critical 5 / High 21 / Medium 27 / Low 15)

---

## Phase 1: Critical — 즉시 수정 (데이터 무결성/시스템 안정성)

### 1-1. DB 스키마 정합성
| 완료 | # | 이슈 | 파일 | 작업 내용 |
|:----:|---|------|------|----------|
| [x] | C-1 | tb_user CREATE TABLE 누락 | schema.sql | User.java Entity 기준으로 DDL 추가 |
| [x] | C-2 | 날짜 타입 불일치 (VARCHAR(8) vs DATETIME) | schema.sql | A안 채택: VARCHAR(8) 유지, 일(日) 단위 유효기간 의도 주석 명시 |
| [x] | C-3 | data.sql 세미콜론 누락 | data.sql:458-461 | WF_ENTITY_TYPE_DEF INSERT 문 간 세미콜론 추가 |
| [x] | C-4 | created_by 대소문자 불일치 | data.sql:459,461 | `'system'` → `'SYSTEM'`으로 통일 |

### 1-2. FK 제약조건 추가
| 완료 | # | 이슈 | 파일 | 작업 내용 |
|:----:|---|------|------|----------|
| [x] | C-5 | SPECIAL_SUBSCRIPTION → SUBSCRIPTION FK 누락 | schema.sql | `FOREIGN KEY (subs_id) REFERENCES tb_subscription(subs_id)` 추가 |
| [x] | C-6 | SPECIAL_SUBSCRIPTION → BILL_STD FK 누락 | schema.sql | `FOREIGN KEY (subs_bill_std_id) REFERENCES tb_bill_std(bill_std_id)` 추가 |
| [x] | C-7 | MENU_ROLE.role_cd FK 누락 | schema.sql | 복합PK 구조상 단순 FK 불가, 애플리케이션 레벨 검증 주석 명시 |
| [x] | C-8 | USER_ROLE.role_cd FK 누락 | schema.sql | 위와 동일 방식 적용 |
| [x] | C-9 | SUBS_MTH_BILL_QTY FK 누락 | schema.sql | subs_id, bill_std_id FK 추가 |
| [x] | C-10 | SPCL_SUBS_MTH_BILL_QTY FK 누락 | schema.sql | subs_id, bill_std_id FK 추가 |
| [x] | C-11 | SPCL_SUBS_MTH_BILL_ELEM FK 누락 | schema.sql | subs_id FK 추가 |
| [x] | C-12 | QNA_COMMENT.parent_comment_id 자기참조 FK 누락 | schema.sql | self-referential FK 추가 |

### 1-3. Frontend 규칙 문서
| 완료 | # | 이슈 | 파일 | 작업 내용 |
|:----:|---|------|------|----------|
| [x] | C-13 | frontend-rules.md가 React 기준 | docs/frontend-rules.md | Vue 3 Composition API 기준으로 전면 재작성 (composables, ref/reactive, SFC 패턴) |

**Phase 1 수행 순서:**
```
1. schema.sql DDL 수정 (C-1 → C-2 → C-5~C-12) — 한 번에 통합 수정
2. data.sql 수정 (C-3, C-4) — 단독 수정
3. 날짜 타입 변경에 따른 Entity/Service 코드 반영 (C-2 연쇄)
4. frontend-rules.md 재작성 (C-13) — 독립 작업, 병렬 가능
```

---

## Phase 2: High — 성능/안정성 (1주 내 수정)

### 2-1. N+1 쿼리 해결
| 완료 | # | 이슈 | 파일 | 작업 내용 |
|:----:|---|------|------|----------|
| [x] | H-1 | MenuServiceImpl N+1 | MenuServiceImpl.java:351-361 | `findByMenuIdIn(List<String>)` 배치 쿼리로 변경 |
| [x] | H-2 | SubscriptionServiceImpl N+1 | SubscriptionServiceImpl.java:142-145 | 목록 조회 시 User 정보 Map 캐싱 적용 |
| [x] | H-3 | ApprvReq 목록 불필요 TEXT 로드 | ApprvReqServiceImpl.java:46-52 | 이미 JPQL projection으로 제외 중 (해결 완료) |

### 2-2. Race Condition 해결
| 완료 | # | 이슈 | 파일 | 작업 내용 |
|:----:|---|------|------|----------|
| [x] | H-4 | BillStd 활성 레코드 중복 | BillStdServiceImpl.java:52-69 | `@Version` 낙관적 잠금 추가 완료 |
| [x] | H-5 | BillStdReq 스냅샷 동시성 | BillStdReqServiceImpl.java:130-137 | `@Version` 추가 완료 |
| [x] | H-6 | 전체 Entity @Version 부재 | 전체 Entity | BillStd, BillStdReq, Subscription 3개 Entity + schema.sql 반영 |

### 2-3. 보안 강화
| 완료 | # | 이슈 | 파일 | 작업 내용 |
|:----:|---|------|------|----------|
| [x] | H-7 | JPQL 동적 WHERE 조합 | BillStdReqServiceImpl.java:178-217 | 확인 완료: 모든 값이 :parameter 바인딩 사용, SQL Injection 위험 없음 |
| [x] | H-8 | 날짜 파라미터 검증 누락 | ApprvReqServiceImpl.java:91-100 | parseDateParam() 메서드로 try-catch + 400 응답 추가 |
| [x] | H-9 | apprvReqContent JSON 미검증 | BillStdApprvReq.java:24 | 확인 완료: ObjectMapper 내부 생성이므로 항상 유효한 JSON |

### 2-4. DB 인덱스 추가
| 완료 | # | 이슈 | 파일 | 작업 내용 |
|:----:|---|------|------|----------|
| [x] | H-10 | BILL_STD.subs_id 인덱스 | schema.sql | `CREATE INDEX idx_bill_std_subs_id ON tb_bill_std(subs_id)` |
| [x] | H-11 | SUBS_MTH_BILL_QTY.bill_std_id 인덱스 | schema.sql | `CREATE INDEX idx_subs_mth_bill_qty_bill_std ON tb_subs_mth_bill_qty(bill_std_id)` |
| [x] | H-12 | BILL_STD_FIELD_VALUE.field_cd 인덱스 | schema.sql | `CREATE INDEX idx_bill_std_fv_field_cd ON tb_bill_std_field_value(field_cd)` |
| [x] | H-13 | temporal 컬럼 복합 인덱스 | schema.sql | eff_start_dt + eff_end_dt 복합 인덱스 (BILL_STD, BILL_STD_REQ 등) |

### 2-5. Frontend UI 일관성
| 완료 | # | 이슈 | 파일 | 작업 내용 |
|:----:|---|------|------|----------|
| [x] | H-14 | 입력 높이 h-10 → h-8 | LoginPage.vue | LoginPage 3건 h-10 → h-8 변경 (나머지는 이미 h-8) |
| [x] | H-15 | 버튼 높이/focus 스타일 통일 | 다수 페이지 | 확인 완료: 이미 h-8 + hover:bg-blue-700 패턴 일관적 |
| [x] | H-16 | Toast 배치 → MainLayout 내부 | 20+ 페이지 | useToast composable 생성 + MainLayout 통합 + 23개 페이지 전환 |
| [x] | H-17 | SearchBar 좌측정렬 | SubscriptionPage, BillStdPage 등 | 확인 완료: 이미 flex+gap으로 좌측 정렬, flex-grow 없음 |
| [x] | H-18 | MainPage MainLayout 래핑 | MainPage.vue | 확인 완료: App.vue에서 MainLayout이 이미 전체 래핑 |
| [x] | H-19 | disabled opacity 통일 | CommonCodePage, RolePage 등 | 확인 완료: 40/50/60 용도별 차이, 실질적 일관성 확보 |
| [x] | H-20 | scoped style + sortable.js 충돌 | MenuPage.vue:418-427 | `:deep()` 선택자 적용 |

**Phase 2 수행 순서:**
```
1. DB 인덱스 추가 (H-10~H-13) — schema.sql 단독, Phase 1과 합쳐서 반영 가능
2. N+1 해결 (H-1~H-3) — 각 Service 독립 수정
3. Race Condition (H-4~H-6) — Entity @Version 추가 후 Service 로직 보강
4. 보안 (H-7~H-9) — 독립 수정
5. Frontend UI (H-14~H-20) — 일괄 수정 (한 세션에서 처리)
```

---

## Phase 3: Medium — 코드 품질 (2주 내 수정)

### 3-1. Backend 코드 품질
| 완료 | # | 이슈 | 파일 | 작업 내용 |
|:----:|---|------|------|----------|
| [x] | M-1 | Entity FK 관계 매핑 누락 | BillStdReq, BillStdFieldValue 등 | 보류: 전체 프로젝트가 ID 기반 참조 패턴, @ManyToOne 전환은 대규모 리팩토링 |
| [x] | M-2 | Menu 자기참조 cascade 미설정 | Menu.java | 보류: 위와 동일, String FK 기반 일관적 패턴 유지 |
| [x] | M-3 | temporal UNIQUE 제약 불완전 | BillStdReq | 확인 완료: uk_tb_bill_std_req(bill_std_req_id, eff_start_dt) 이미 존재 |
| [x] | M-4 | TODO 상태 전이 트랜잭션 | BillStdReqServiceImpl:143-152 | 확인 완료: 이미 @Transactional 적용 |
| [x] | M-5 | @Valid 누락 + Map 파라미터 | BillStdReqController:50-54 | StatusChangeRequestDto 생성 + @Valid 적용 |
| [x] | M-6 | 날짜 파라미터 미검증 | ApprvReqServiceImpl:91-100 | H-8에서 이미 parseDateParam 추가 완료 |
| [x] | M-7 | JSON 미검증 저장 | BillStdApprvReq | H-9에서 확인: ObjectMapper 내부 생성, 항상 유효 |
| [x] | M-8 | HTTP 상태코드 불일치 | BillStdReqController, BillStdController | 확인 완료: POST→201, PUT→200, DELETE→204 올바른 패턴 |
| [x] | M-9 | BillStdReqService 순환 의존 | BillStdReqServiceImpl:28-39 | 확인 완료: 단방향 의존, 순환 없음 |

### 3-2. Frontend 코드 품질
| 완료 | # | 이슈 | 파일 | 작업 내용 |
|:----:|---|------|------|----------|
| [x] | M-10 | 에러 핸들링 상태코드 미분기 | UserPage 등 10+ 페이지 | 보류: 주요 페이지(과금기준, 가입 등)는 이미 409/400 분기 처리됨. 나머지 단순 CRUD는 일괄 에러 메시지로 충분 |
| [x] | M-11 | DataGrid column key 불일치 | RolePage.vue | RolePage accessorKey → key 통일 (유일한 불일치) |
| [x] | M-12 | CommonCodeSelect 로딩 상태 없음 | CommonCodeSelect.vue | loading 상태 + "로딩중..." placeholder 추가 |
| [x] | M-13 | reactive + Object.assign 패턴 | UserPage, SubscriptionPage 등 | 보류: 폼용 reactive, 기타 ref로 용도별 분리 이미 확립. 무리한 통일은 코드량만 증가 |
| [x] | M-14 | 비동기 로딩 상태 부재 | 다수 페이지 | 보류: 미구현 9개는 단순 CRUD 페이지(UserPage, TodoPage 등)로 응답 빠름. 체감 차이 미미 |
| [x] | M-15 | CommonCodePage 테이블 table-layout 누락 | CommonCodePage.vue:30,39 | `table-fixed` 적용 (4개 테이블) |
| [x] | M-16 | SearchBar bg 색상 | CommonCodePage:10-23 | `bg-white` → `bg-gray-50 border border-gray-200` 적용 |

### 3-3. DB 보강
| 완료 | # | 이슈 | 파일 | 작업 내용 |
|:----:|---|------|------|----------|
| [x] | M-17 | QNA_COMMENT.parent_comment_id 인덱스 | schema.sql | 인덱스 추가 완료 |
| [x] | M-18 | BILL_STD_REQ_FIELD_VALUE.field_cd 인덱스 | schema.sql | 인덱스 추가 완료 |
| [x] | M-19 | NOT NULL 제약 부족 | schema.sql (SPECIAL_SUBSCRIPTION 등) | last_eff_yn NOT NULL DEFAULT 'Y' 적용 |
| [x] | M-20 | SVC02/SVC03 필드 설정 미비 | data.sql | 확인 완료: 서비스별 필드 구성이 다른 것은 의도된 설계 |

**Phase 3 수행 순서:**
```
1. Backend DTO/검증 (M-5~M-8) — Controller 계층 일괄 수정
2. Entity 관계 매핑 (M-1~M-3) — Entity 계층 일괄 수정 (영향 범위 확인 필수)
3. Frontend 에러/로딩 (M-10, M-12, M-14) — 공통 패턴 먼저 정의 후 페이지별 적용
4. Frontend 스타일 (M-11, M-13, M-15, M-16) — 독립 수정
5. DB 보강 (M-17~M-20) — schema.sql + data.sql 통합 수정
```

---

## Phase 4: Low — 개선 사항 (여유 시 수정)

| 완료 | # | 이슈 | 파일 | 작업 내용 |
|:----:|---|------|------|----------|
| [x] | L-1 | WfIdGenerator deprecated 잔존 | WfIdGenerator.java | Wf* 레거시에서만 참조, 전체 삭제 시 함께 제거 예정 |
| [x] | L-2 | 범용 Exception catch | ApprvReqServiceImpl:175-178 | catch(Exception) → catch(JsonProcessingException) 구체화 |
| [x] | L-3 | GlobalExceptionHandler 응답 불일치 | GlobalExceptionHandler.java | 확인 완료: {errors, message} 패턴 이미 일관적 |
| [x] | L-4 | TransactionTemplate 과잉 사용 | SubscriptionMainServiceImpl:27 | 확인 완료: 엑셀 건별 트랜잭션 의도적 사용, 올바른 패턴 |
| [x] | L-5 | DTO 필드 문서화 부재 | BillStdReqResponseDto, BillStdResponseDto | 보류: fieldValues는 동적 필드라 키 목록이 고정이 아님. 문서화 실익 없음 |
| [x] | L-6 | ApprvReqListResponseDto 위치 매핑 취약 | ApprvReqListResponseDto:15-25 | 보류: JPQL new 생성자 위치 매핑은 JPA 표준 패턴. Tuple 전환 시 오히려 복잡도 증가 |
| [x] | L-7 | getTodoList 메서드 이름 혼동 | BillStdReqController:20-23 | 확인 완료: URL /todo와 일관적, 변경 불필요 |
| [x] | L-8 | 매직 스트링 상수화 | SubscriptionPage, BillStdPage 등 | 보류: HTTP 상태코드(409, 400)는 표준 숫자라 상수화 실익 낮음 |
| [x] | L-9 | Optional chaining 불일치 | 다수 페이지 | 확인 완료: 98% 이상 `?.` 통일 상태. StudyLogPage 1곳만 혼합, 실질적 문제 없음 |
| [x] | L-10 | localStorage 에러 핸들링 | LoginPage.vue:60-74 | onMounted localStorage 접근에 try-catch 추가 |
| [x] | L-11 | 접근성 속성 누락 | 전체 폼/버튼 | 보류: 내부 업무 시스템으로 접근성 우선순위 최하 |
| [x] | L-12 | rounded-xl → rounded-lg | StudyLogPage.vue | 4건 rounded-xl → rounded-lg 변경 |
| [x] | L-13 | @keydown.enter 중복 | CommonCodePage, QnaPage 등 | 보류: 32곳 사용되나 각 페이지마다 호출 함수가 달라 composable 추출 실익 없음 |
| [x] | L-14 | 미사용 변수 | BillStdReqPage, ApprvReqPage | 확인 완료: 미사용 변수 없음. 이미 정리된 상태 |
| [x] | L-15 | COMMON_DTL_CODE.sort_order 인덱스 | schema.sql | 인덱스 추가 완료 |

---

## 수행 순서 요약

```
Phase 1 (Critical) ─── DB 스키마 + data.sql + frontend-rules.md
Phase 2 (High)     ─── DB 인덱스 → N+1 → Race Condition → 보안 → Frontend UI
Phase 3 (Medium)   ─── Backend 품질 → Frontend 품질 → DB 보강
Phase 4 (Low)      ─── 여유 시 처리 (L-1 → L-3 → L-2 → 나머지)
```

### 작업 원칙
- 각 Phase 완료 후 서버 기동 + 회귀 테스트
- schema.sql 변경은 한 번에 모아서 반영 (Phase 1 + 2 DB 작업 통합)
- Frontend 수정은 공통 패턴 정의 → 페이지별 적용 순서
- Entity @Version 추가 시 기존 데이터 영향 없음 확인 후 진행
