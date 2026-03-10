# 01. Research - 과금기준 동적필드 관리

## 1. 현행 구조 요약

### 1.1 DB 스키마
- **tb_bill_std**: 과금기준 테이블. 단일 PK(`bill_std_id`), FK(`subs_id` -> `tb_subscription`). 공통 필드 9개 + 서비스별 고정 컬럼 14개(pwr_met_calc_meth_cd ~ daily_unit_price) + 시스템 필드 4개.
- **tb_subscription**: 가입 테이블. `svc_cd` 필드를 보유하여 가입별 서비스코드 결정.
- **tb_common_code / tb_common_dtl_code**: 공통코드 헤더-상세 구조. `svc_cd`(서비스코드), `pwr_met_calc_meth_cd`, `uprc_det_meth_cd`, `pue_det_meth_cd` 등이 이미 공통코드로 등록됨.
- **tb_special_subscription**: 복합 PK(`subs_bill_std_id` + `eff_sta_dt`) 패턴 참조 사례.

### 1.2 백엔드 (BillStd 관련)
- **Entity** (`BillStd.java`): `@Entity` + `@Id(billStdId)`. 서비스별 14개 필드가 Entity에 직접 매핑.
- **Repository** (`BillStdRepository.java`): `JpaRepository<BillStd, String>`. 커스텀 메서드 `findBySubsIdAndLastEffYn`, `countBySubsId`.
- **Service** (`BillStdService.java` / `BillStdServiceImpl.java`): Interface + Impl 패턴. `findAll`, `findById`, `findBySubsId`, `create`, `update`, `delete`. create 시 기존 활성 레코드의 `lastEffYn=N` 이력 처리 로직 포함. ID 채번은 `"BS" + UUID 기반` (주의: backend-rules.md는 타임스탬프 기반을 규정하나 현재 UUID 사용 중).
- **Controller** (`BillStdController.java`): `/api/bill-std`. GET(전건/단건/by-subs), POST, PUT, DELETE.
- **DTO**: `BillStdRequestDto` (14개 서비스별 필드 포함, `@NotBlank` subsId/createdBy), `BillStdResponseDto` (동일 구조).
- **SecurityUtils**: Spring Security 인증 정보에서 userId 추출. ServiceImpl에서 createdBy/updatedBy 설정 시 사용.

### 1.3 프론트엔드 (BillStd 관련)
- **BillStdPage.vue**: 조회(검색유형+키워드) -> 입력폼 -> 플로팅 액션바 구조. 그리드(목록) 없음 -- 단건 조회 방식. EMPTY_FORM에 서비스별 필드 일부만 포함(billStdId, subsId, svcCd, lastEffYn, effStartDt, effEndDt, meteringUnitPriceAmt, cntrcCapKmh, cntrcAmt).
- **billStdApi.js**: `getBillStds`(전건), `getBillStd`(단건), `getBillStdBySubsId`, `createBillStd`, `updateBillStd`, `deleteBillStd`.
- **CommonCodeSelect.vue**: `commonCode` prop으로 공통코드 헤더를 받아 유효 상세코드 목록을 드롭다운으로 렌더링. `v-model` 지원.
- **commonCodeApi.js**: `getEffectiveDetails(code)` -- 현재 유효한 상세코드 조회 API.
- **라우터**: `/bill-std` -> `BillStdPage.vue`. 신규 Config 관리 화면 경로 추가 필요.

### 1.4 복합 PK 패턴 (SpecialSubscription 참고)
- `SpecialSubscriptionId.java` (@Embeddable, Serializable, equals/hashCode 구현)
- `SpecialSubscription.java` (@EmbeddedId)
- Controller URL 패턴: `/{pk1}/{pk2}`
- Repository: `JpaRepository<SpecialSubscription, SpecialSubscriptionId>` + `JpaSpecificationExecutor`
- ServiceImpl: Specification 기반 동적 검색, 서버 페이징 지원

## 2. 영향 범위

### 2.1 신규 생성 필요
| 계층 | 파일 | 설명 |
|---|---|---|
| DB | schema.sql | tb_bill_std_field_config, tb_bill_std_field_value 테이블 추가 |
| DB | data.sql | Config 초기 데이터 (기존 14개 고정 필드를 서비스별로 Config화) |
| Backend | BillStdFieldConfigId.java | 복합 PK (svc_cd + field_cd + eff_start_dt) |
| Backend | BillStdFieldConfig.java | Entity |
| Backend | BillStdFieldConfigRepository.java | Repository |
| Backend | BillStdFieldConfigService.java | Interface |
| Backend | BillStdFieldConfigServiceImpl.java | Impl |
| Backend | BillStdFieldConfigController.java | REST Controller |
| Backend | BillStdFieldConfig*Dto.java | Request/Response DTO |
| Backend | BillStdFieldValueId.java | 복합 PK (bill_std_id + field_cd) |
| Backend | BillStdFieldValue.java | Entity |
| Backend | BillStdFieldValueRepository.java | Repository |
| Frontend | BillStdFieldConfigPage.vue | 신규 Config 관리 화면 |
| Frontend | billStdFieldConfigApi.js | API 호출 |
| Frontend | router/index.js | 라우트 추가 |

### 2.2 수정 필요
| 계층 | 파일 | 변경 내용 |
|---|---|---|
| DB | schema.sql | tb_bill_std에서 서비스별 14개 컬럼 제거 |
| Backend | BillStd.java | 14개 서비스별 필드 제거 |
| Backend | BillStdRequestDto.java | 14개 필드 제거, 동적 필드값 Map/List 추가 |
| Backend | BillStdResponseDto.java | 14개 필드 제거, 동적 필드값 포함 |
| Backend | BillStdServiceImpl.java | create/update에서 동적 필드값 저장 로직, 조회 시 필드값 로드, toDto 변경 |
| Backend | BillStdRepository.java | 필요 시 쿼리 추가 |
| Frontend | BillStdPage.vue | 동적 폼 영역 추가, Config 기반 필드 렌더링, 저장/조회 로직 변경 |
| Frontend | billStdApi.js | 필요 시 API 추가 (동적 필드 Config 조회) |
| DB | data.sql | 메뉴 등록 데이터 추가 |

## 3. 의존성 그래프

```
tb_subscription.svc_cd
    |
    v
tb_bill_std_field_config (PK: svc_cd + field_cd + eff_start_dt)
    |                                   |
    | (svc_cd로 Config 목록 조회)       | (common_code -> tb_common_code 참조)
    v                                   v
BillStdPage.vue (동적 폼 렌더링)    CommonCodeSelect.vue
    |
    v
tb_bill_std (공통필드만) ---FK--- tb_subscription
    |
    v
tb_bill_std_field_value (PK: bill_std_id + field_cd)
    ^--- FK: bill_std_id -> tb_bill_std.bill_std_id
```

**주요 의존 흐름**:
1. 가입(tb_subscription) -> svc_cd 결정
2. svc_cd -> tb_bill_std_field_config에서 유효 Config 조회
3. Config 목록 -> 프론트엔드 동적 폼 렌더링
4. 과금기준 저장 시 -> tb_bill_std(공통) + tb_bill_std_field_value(동적) 동시 저장
5. Config의 field_type=SELECT 시 -> common_code로 CommonCodeSelect 연동

## 4. 기존 테스트 현황

- **테스트 파일**: `src/test/java/.../VibeStudyApplicationTests.java` 1건만 존재 (Spring Boot 기본 컨텍스트 로드 테스트 추정).
- **단위/통합 테스트 없음**: BillStd 관련 별도 테스트 코드 없음.
- 테스트 부재로 인해 리팩토링 시 회귀 검증은 수동 확인에 의존.

## 5. 기술적 제약사항

1. **Lombok 사용 금지**: 모든 Entity/DTO에 명시적 getter/setter 필요 (backend-rules.md).
2. **Service Interface + Impl 분리 필수** (backend-rules.md).
3. **ID 채번**: 타임스탬프 기반 규정이나, 현행 BillStdServiceImpl은 UUID 사용 중 -- 신규 테이블은 규정 준수 필요, 기존 코드 수정 여부는 설계자 판단.
4. **복합 PK**: 3개 필드 복합 PK (svc_cd + field_cd + eff_start_dt) -- SpecialSubscriptionId 패턴 참조하되, PK 필드 3개로 확장 필요.
5. **createdBy 단일 필드 원칙**: RequestDto에 `createdBy` 1개만 선언, ServiceImpl에서 INSERT/UPDATE 분기 (backend-rules.md). 현행 BillStdServiceImpl은 SecurityUtils.getCurrentUserId() 사용 -- dto.getCreatedBy() 대신 보안 컨텍스트 직접 사용 패턴.
6. **프론트엔드 구조**: Vue 3 Composition API (script setup). 현행 BillStdPage는 그리드 없이 단건 조회 방식 -- 요구사항상 동적 폼 영역 추가이므로 기존 레이아웃 확장.
7. **H2 DB**: schema.sql/data.sql 기반 DDL/DML. JPA ddl-auto 설정 확인 필요 (schema.sql이 있으므로 `none` 또는 `validate` 추정).
8. **tb_bill_std 컬럼 제거**: 기존 데이터가 있을 경우 마이그레이션 필요. H2 인메모리라면 schema.sql 수정만으로 충분하나, 파일 기반 DB(vibedb.mv.db 존재)라면 ALTER TABLE 또는 재생성 고려.

## 6. 리스크 식별

| # | 리스크 | 영향도 | 완화 방안 |
|---|---|---|---|
| R1 | tb_bill_std 컬럼 제거 시 기존 데이터 유실 | 높음 | 마이그레이션 스크립트로 기존 값을 tb_bill_std_field_value로 이관. H2 파일 DB(vibedb.mv.db) 존재 확인됨 |
| R2 | 3개 필드 복합 PK URL 패턴 복잡도 | 중간 | eff_start_dt가 TIMESTAMP 타입이므로 URL 경로에 안전한 포맷 결정 필요 (ISO 8601 또는 epoch) |
| R3 | BillStdServiceImpl의 이력 처리 로직(lastEffYn 전환)이 동적 필드값과 연동 필요 | 중간 | create 시 신규 bill_std_id에 대한 field_value도 함께 INSERT하는 트랜잭션 보장 |
| R4 | 동적 폼 렌더링 시 field_type=SELECT일 때 공통코드 연동 | 낮음 | CommonCodeSelect 컴포넌트 재사용으로 해결 가능 |
| R5 | 현행 BillStdPage에 그리드가 없는 단건 조회 방식 | 낮음 | 요구사항은 동적 폼 영역 추가이므로 기존 구조 유지 가능. 단, 동적 필드 조회/저장 API 호출 추가 필요 |
| R6 | 현행 ID 채번이 UUID 기반(규정 위반) | 낮음 | 신규 코드는 규정 준수. 기존 코드 수정 여부는 별도 판단 |

---
## Summary (다음 단계 전달용)
- **수정 대상 파일**: schema.sql, data.sql, BillStd.java, BillStdRequestDto.java, BillStdResponseDto.java, BillStdServiceImpl.java, BillStdRepository.java, BillStdPage.vue, billStdApi.js, router/index.js
- **신규 생성 파일**: BillStdFieldConfig 관련 7개(Id/Entity/Repo/Service/ServiceImpl/Controller/DTO), BillStdFieldValue 관련 3개(Id/Entity/Repo), BillStdFieldConfigPage.vue, billStdFieldConfigApi.js
- **핵심 의존성**: tb_subscription.svc_cd -> Config 조회 -> 동적 폼 렌더링 -> field_value 저장. Config의 common_code 필드 -> CommonCodeSelect 컴포넌트 연동
- **기술적 제약사항**: Lombok 금지, Interface+Impl 분리, 복합 PK 3개 필드(@EmbeddedId), createdBy 단일 필드 원칙, SecurityUtils 사용 패턴, H2 파일 DB 존재로 마이그레이션 고려
- **리스크**: (R1) 기존 14개 컬럼 데이터 마이그레이션, (R2) TIMESTAMP 복합 PK의 URL 안전 포맷, (R3) 과금기준 생성 시 field_value 동시 저장 트랜잭션 보장
