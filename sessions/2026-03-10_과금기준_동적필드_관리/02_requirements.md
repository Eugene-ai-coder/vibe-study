# 02. Requirements - 과금기준 동적필드 관리

## 1. 기능 명세

### 1.1 신규 테이블
- **tb_bill_std_field_config**: 서비스별 과금기준 필드 설정 (PK: svc_cd + field_cd + eff_start_dt)
  - eff_start_dt, eff_end_dt: VARCHAR(8) YYYYMMDD 형식 (특수가입과 동일 패턴)
- **tb_bill_std_field_value**: 과금기준별 동적 필드값 (PK: bill_std_id + field_cd)

### 1.2 기존 테이블 수정
- **tb_bill_std**: 서비스별 고정 컬럼 14개 제거, 공통 구조 필드만 유지
- DB 파일 삭제 후 schema.sql/data.sql로 재생성 (마이그레이션 불필요)

### 1.3 신규 화면: 서비스별 과금기준 Config 관리
- 표준 레이아웃: 조회 → 그리드 → 입력폼 → 플로팅 액션바
- **조회영역**: 서비스코드(공통코드 SELECT), 필드코드(TEXT)
- **그리드**: 서비스코드, 필드코드, 필드명, 필드타입, 필수여부, 정렬순서, 유효시작일, 유효종료일
- **입력폼**: 서비스코드, 필드코드, 필드명, 필드타입(TEXT/NUMBER/SELECT/DATE), 필수여부(Y/N), 정렬순서, 공통코드(SELECT 타입 전용), 기본값, 유효시작일(YYYYMMDD), 유효종료일(YYYYMMDD)
- **액션**: 등록, 변경, 삭제, 사용종료

### 1.4 기존 화면 수정: 과금기준 관리
- 기존 단건 조회 구조 유지
- 공통 필드 입력폼 아래에 "동적 필드" 섹션 추가
- 가입 선택 → svc_cd 기반 유효 Config 조회 → 동적 폼 렌더링
- 저장 시 공통 필드(tb_bill_std) + 동적 필드값(tb_bill_std_field_value) 동시 저장

### 1.5 ID 채번
- **기존 BillStd**: UUID → 타임스탬프 기반으로 수정 (규정 준수)
- **신규 테이블**: 복합 PK이므로 별도 ID 채번 불필요

## 2. 비기능 요건
- Lombok 사용 금지, Service Interface+Impl 분리
- 트랜잭션: 과금기준 저장 시 공통 필드 + 동적 필드값 동시 저장 보장
- 유효기간 필터: 동적 폼 렌더링 시 현재일자 기준 유효 Config만 조회

## 3. 수용 기준
- [ ] Config 관리 화면에서 CRUD 정상 동작
- [ ] Config 삭제 시, 사용 중인 필드는 삭제 차단 (409 에러)
- [ ] Config 사용종료 시, eff_end_dt를 현재일자(YYYYMMDD)로 설정
- [ ] 과금기준 관리 화면에서 가입 선택 시 동적 폼 렌더링
- [ ] 동적 폼의 field_type=SELECT 시 CommonCodeSelect 연동
- [ ] 과금기준 저장/조회 시 동적 필드값 정상 처리
- [ ] BillStd ID 채번 타임스탬프 기반으로 변경

## 4. 제외 범위
- 기존 DB 데이터 마이그레이션 (DB 재생성으로 처리)
- 과금기준 그리드(목록) 추가 (기존 단건 조회 방식 유지)
- Config 이력 조회 화면 (Config 관리에서 전체 이력 조회 가능하므로 별도 불필요)

## 5. 용어 정의
- **Config**: tb_bill_std_field_config 레코드. 서비스별 관리 필드 정의
- **동적 필드**: Config에 의해 결정되는 입력 필드. EAV 방식으로 저장
- **사용종료**: Config 삭제 대신 eff_end_dt를 현재일자로 설정하여 비활성화

---
## Summary (다음 단계 전달용)
- **핵심 기능**: (1) Config 관리 CRUD+사용종료 화면 신규, (2) BillStdPage 동적 폼 영역 추가, (3) tb_bill_std 고정컬럼 제거+EAV 전환, (4) BillStd ID 채번 타임스탬프 전환
- **수용 기준**: Config CRUD+삭제차단+사용종료, 동적폼 렌더링(SELECT→CommonCodeSelect), 과금기준 저장/조회 시 field_value 연동, ID 채번 규정 준수
- **제외 범위**: DB 마이그레이션(재생성), 과금기준 그리드 추가, Config 이력 별도 화면
- **비기능 요건**: Lombok 금지, Interface+Impl 분리, 트랜잭션 보장, 유효기간 필터링
- **설계 결정**: eff_start_dt/eff_end_dt는 VARCHAR(8) YYYYMMDD, 기존 단건 조회 구조 유지, Config 삭제 시 사용중 차단+사용종료 기능 제공
