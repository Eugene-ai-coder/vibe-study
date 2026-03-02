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
