# Project Glossary (용어 사전)

---

## 1. 도메인 용어 한→영 매핑

| 한국어 | 영어(코드) | DB 테이블 | 컬럼/필드 접두사 | 비고 |
|---|---|---|---|---|
| 가입 | Subscription | `tb_subscription` | `subs_` / `subs` | 가입자 단위 계약 |
| 과금기준 | BillStd | `tb_bill_std` | `bill_std_` / `billStd` | 가입별 유효기간 이력 관리 |
| 학습로그 | StudyLog | (JPA 자동생성) | `study_log_` / `studyLog` | |
| 사용자 | User | `tb_user` | `user_` / `user` | 로그인 계정 |
| 대표정보 | Main | — | — | 접미사로 사용 (예: SubscriptionMain) |

> **규칙:** 신규 도메인 추가 시 이 테이블에 먼저 등록하고 코딩 시작.

---

## 2. 이력 관련 용어

| 용어 | 코드/컬럼명 | 값 | 설명 |
|---|---|---|---|
| 최종유효 | `last_eff_yn` | `Y` / `N` | Y=현행 레코드, N=이력 레코드 |
| 유효시작일시 | `eff_start_dt` | TIMESTAMP | 해당 기준의 적용 시작 시점 |
| 유효종료일시 | `eff_end_dt` | TIMESTAMP | 기본값 `9999-12-31 23:59:59` |
| 이력화 | — | — | 신규 등록 시 기존 Y 레코드를 N으로 변경하는 처리 |

---

## 3. 컬럼 네이밍 규칙

| 규칙 | 예시 | 비고 |
|---|---|---|
| DB 컬럼: `snake_case`, 단어 축약 금지 | `eff_start_dt` (O) / `eff_sta_dt` (X) | `start`→`sta` 등 임의 축약 금지 |
| Java 필드: `camelCase`, DB 컬럼과 1:1 대응 | `effStartDt` ↔ `eff_start_dt` | getter/setter도 동일 규칙 |
| JS/Vue 키: Java DTO의 JSON 직렬화명과 동일 | `effStartDt` | 프론트↔백엔드 필드명 일치 |
| 유효기간 쌍: 반드시 `eff_start_dt` / `eff_end_dt` | — | start/end 대칭 유지 |

> **원칙:** 새 컬럼 추가 시 이 용어 사전에 먼저 등록. 기존 컬럼과 동일 개념이면 반드시 같은 이름 사용.
