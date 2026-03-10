# 그리드 개선 리서치

## 1. 현행 구조 요약

### 1.1 기준(표준): UserPage 그리드 구현 방식

`UserPage.vue`는 `DataGrid` 공통 컴포넌트를 사용하며, 아래 props를 전달한다:
- `columns`: `{ key, header, size, minSize, filterable }` 배열
- `data`, `page`, `totalPages`, `totalElements`, `pageSize`: 페이징 관련 상태
- `onPageChange`: 페이지 변경 핸들러
- `rowIdAccessor`, `storageKey`, `title`, `pinnedCount`: 부가 설정

DataGrid 내부에서 **컬럼 리사이즈**, **드래그 순서 변경**, **필터**, **정렬**, **컬럼 설정(localStorage)**, **페이징** 기능을 일괄 제공한다.

### 1.2 개선 대상 화면 현황

| 화면 | 파일 | 그리드 사용 | 페이징 | 비고 |
|------|------|------------|--------|------|
| 가입 관리 | `SubscriptionPage.vue` | DataGrid 사용 | 전건(페이징 없음) | `selectedRowId`, `@row-click` 지원됨 |
| 대표가입 관리 | `SubscriptionMainPage.vue` | DataGrid 사용 | 전건(페이징 없음) | `selectedRowId`, `@row-click` 지원됨 |
| 특수가입 관리 | `SpecialSubscriptionPage.vue` | DataGrid 사용 | 전건(페이징 없음) | `selectedRowId`, `@row-click` 지원됨 |

**결론: 세 화면 모두 이미 DataGrid를 사용하고 있다.** UserPage와의 차이는 "페이징 여부"뿐이며, 이는 도메인 특성상 전건 목록이 맞으므로 차이가 아니라 의도된 설계이다. 그리드 자체의 구조적 개선 사항은 제한적이다.

### 1.3 SubscriptionMainPage의 `subsNm` 컬럼 문제

`SubscriptionMainPage.vue`의 columns에 `{ key: 'subsNm', header: '가입명' }`이 정의되어 있으나:
- `SubscriptionMainListResponseDto`에 `subsNm` 필드가 **없다**
- 네이티브 쿼리(`SubscriptionMainRepository.findListRaw`)에서 `subs_nm`을 **SELECT하지 않는다**
- 따라서 해당 컬럼은 항상 빈값으로 표시된다

---

## 2. 콤보 글자 깨짐 원인 분석

### CommonCodeSelect 컴포넌트 동작 흐름
1. `CommonCodeSelect.vue` - props로 `commonCode` (예: `svc_cd`)를 받음
2. `watch`에서 `commonCodeApi.getEffectiveDetails(code)` 호출
3. API: `GET /api/common-codes/{code}/details/effective`
4. 응답의 `commonDtlCode`를 `<option :value>`, `commonDtlCodeNm`을 표시값으로 사용

### useCommonCodeLabel 컴포지블 동작 흐름
1. `useCommonCodeLabel.js` - commonCodes 배열을 받아 `commonCodeApi.getDetails(code)` 호출
2. API: `GET /api/common-codes/{code}/details` (유효기간 필터 없음)
3. `getLabel(commonCode, codeValue)` 함수로 코드값 -> 코드명 매핑

### 글자 깨짐 추정 원인
`getEffectiveDetails`와 `getDetails`는 서로 **다른 엔드포인트**를 호출한다.
- `getEffectiveDetails`: `/details/effective` -- 유효기간 내 항목만 반환
- `getDetails`: `/details` -- 전체 항목 반환

두 API의 응답 차이가 있을 수 있으나, 글자 깨짐의 실제 원인은 **인코딩 문제일 가능성이 높다**:
- DB 저장 시 한글 인코딩 불일치
- API 응답 charset 설정 문제
- 혹은 `commonDtlCodeNm` 값이 DB에 깨진 상태로 저장되어 있을 수 있음

> **확인 필요**: 실제 런타임에서 `/api/common-codes/svc_cd/details/effective` 응답을 확인해야 정확한 원인을 특정할 수 있다. 코드 레벨에서는 `CommonCodeSelect.vue`의 `<option>{{ opt.commonDtlCodeNm }}</option>` 렌더링 로직에 문제가 없으며, `data.sql`의 초기 데이터도 정상적인 한글(`전력`, `냉방`, `통신`)로 작성되어 있다.

---

## 3. 가입 테이블 서비스명/요금상품명 -> 서비스코드/요금상품코드 변경

### 현행 구조

**DB (`tb_subscription`)**:
- `svc_nm VARCHAR(100)` -- 서비스명 (자유 텍스트, 예: "서비스1", "서비스2")
- `fee_prod_nm VARCHAR(100)` -- 요금상품명 (자유 텍스트, 예: "요금상품1")

**Entity (`Subscription.java`)**:
- `private String svcNm;` / `private String feeProdNm;`

**DTO**: `SubscriptionRequestDto`, `SubscriptionResponseDto` 모두 `svcNm`, `feeProdNm` 필드 사용

**ServiceImpl**: `SubscriptionServiceImpl`에서 `svcNm`, `feeProdNm` 직접 매핑

**Repository**: `findBySvcNmContainingIgnoreCase`, `findByFeeProdNmContainingIgnoreCase` 메서드

**프론트엔드 (`SubscriptionPage.vue`)**: 컬럼 정의에 `svcNm`("서비스명"), `feeProdNm`("요금제명"), 폼에도 동일 필드

**DataInitializer**: 샘플 데이터에서 `setSvcNm("서비스1")`, `setFeeProdNm("요금상품1")` 식으로 텍스트 직접 설정

### 변경에 필요한 작업

| 레이어 | 파일 | 변경 내용 |
|--------|------|----------|
| DB | `schema.sql` | `svc_nm` -> `svc_cd VARCHAR(10)`, `fee_prod_nm` -> `fee_prod_cd VARCHAR(10)` |
| Entity | `Subscription.java` | 필드명 `svcNm`->`svcCd`, `feeProdNm`->`feeProdCd`, 컬럼 매핑 변경 |
| RequestDto | `SubscriptionRequestDto.java` | `svcNm`->`svcCd`, `feeProdNm`->`feeProdCd` |
| ResponseDto | `SubscriptionResponseDto.java` | `svcNm`->`svcCd`, `feeProdNm`->`feeProdCd` |
| ServiceImpl | `SubscriptionServiceImpl.java` | setter/getter 변경, 검색 메서드 변경 |
| Repository | `SubscriptionRepository.java` | `findBySvcNmContainingIgnoreCase` -> `findBySvcCdContainingIgnoreCase` 등 |
| DataInitializer | `SubscriptionDataInitializer.java` | `setSvcNm("서비스1")` -> `setSvcCd("SVC01")` |
| 프론트엔드 | `SubscriptionPage.vue` | 컬럼/폼에서 `svcNm`->`svcCd`, `feeProdNm`->`feeProdCd`, `CommonCodeSelect` 적용 |
| 프론트엔드 | `SubscriptionMainPage.vue` | `svcNm` 참조를 `svcCd`로 변경, 하드코딩 `SVC_MAP` 제거 |
| 프론트엔드 | `SubscriptionMainServiceImpl.java` | 네이티브 쿼리에서 `s.svc_nm` -> `s.svc_cd` |
| DTO | `SubscriptionMainListResponseDto.java` | `svcNm` -> `svcCd`, `feeProdNm` -> `feeProdCd` |
| 공통코드 | `data.sql` | `fee_prod_cd` 공통코드 그룹 및 상세코드 추가 필요 (현재 존재하지 않음) |

### 공통코드 테이블 현황

`data.sql`에서 `svc_cd` 공통코드는 이미 존재:
- `SVC01`(전력), `SVC02`(냉방), `SVC03`(통신)

`fee_prod_cd` 공통코드는 **존재하지 않음** -- 새로 추가 필요

---

## 4. "코드"로 끝나는 필드명 식별

### 화면별 "코드" 포함 레이블 목록

| 화면 | 위치 | 현재 레이블 | 필드/컬럼키 | 제안 레이블 |
|------|------|------------|-------------|------------|
| 특수가입 관리 | 그리드 헤더 | `서비스코드` | `svcCd` | `서비스` |
| 특수가입 관리 | 폼 레이블 | `서비스코드` | `svcCd` | `서비스` |
| 가입별 과금기준 | 폼 레이블 | `서비스코드` | `svcCd` | `서비스` |
| 공통코드 관리 | 페이지 타이틀 | `공통코드 관리` | - | 도메인 고유명칭으로 제외 판단 필요 |
| 공통코드 관리 | 조회/폼 레이블 | `공통코드`, `공통코드명`, `상세코드`, `상세코드명` | - | 도메인 고유명칭으로 제외 판단 필요 |

**판단**: "공통코드 관리" 화면은 코드 자체를 관리하는 화면이므로 "코드" 제거 대상에서 **제외**하는 것이 적절하다. 실제 변경 대상은 업무 화면(특수가입, 과금기준, 가입 관리)에서 `~코드`로 표시되는 필드에 한정된다. 가입 관리 화면은 요구사항 #3에서 `svcNm`->`svcCd` 변환 시 함께 처리된다.

---

## 5. 영향 범위 및 의존성 그래프

```
tb_subscription (DB)
  ├── Subscription.java (Entity)
  │   ├── SubscriptionRequestDto.java
  │   ├── SubscriptionResponseDto.java
  │   ├── SubscriptionServiceImpl.java
  │   ├── SubscriptionRepository.java (Spring Data)
  │   └── SubscriptionDataInitializer.java (초기 데이터)
  ├── SubscriptionPage.vue (프론트엔드)
  │   ├── subscriptionApi.js
  │   └── CommonCodeSelect.vue (svc_cd, fee_prod_cd 콤보 추가)
  │
  └─ 의존 테이블/화면 ─
      ├── SubscriptionMainPage.vue
      │   ├── SubscriptionMainServiceImpl.java (네이티브 쿼리에서 svc_nm 참조)
      │   ├── SubscriptionMainListResponseDto.java
      │   └── SubscriptionMainRepository.java (findListRaw 쿼리)
      └── BillStdPage.vue (svc_cd 콤보 -- 이미 코드 기반)

tb_common_code / tb_common_dtl_code (DB)
  └── fee_prod_cd 공통코드 그룹+상세 신규 추가 필요

CommonCodeSelect.vue
  └── commonCodeApi.getEffectiveDetails() -- 글자 깨짐 조사 대상
```

---

## 6. 기존 테스트 현황

- `VibeStudyApplicationTests.java`: Spring Boot 컨텍스트 로드 테스트 1건만 존재
- 도메인 단위 테스트, 통합 테스트: **없음**
- 프론트엔드 테스트: **없음**

---

## 7. 기술적 제약사항

1. **H2 인메모리 DB**: `schema.sql` + `data.sql`로 스키마/초기 데이터 관리. 컬럼 변경 시 `ALTER TABLE` 불가 -- `schema.sql`에서 테이블 정의를 직접 수정해야 함
2. **JPA Entity와 schema.sql 이중 관리**: Hibernate ddl-auto 설정에 따라 충돌 가능성 있음 (확인 필요: `application.properties`)
3. **SubscriptionDataInitializer**: 코드 변경 시 초기 샘플 데이터도 코드값으로 변경 필요
4. **네이티브 쿼리**: `SubscriptionMainRepository.findListRaw`에서 `svc_nm` 직접 참조 -- 컬럼명 변경 시 반드시 함께 수정
5. **SubscriptionMainPage 하드코딩**: `SVC_MAP` 객체에 서비스명이 하드코딩되어 있음 -- 코드 기반으로 전환 시 제거 가능
6. **프론트엔드 빌드**: `frontend-vue/dist/` 하위의 빌드 산출물이 git에 추적됨 -- 변경 후 재빌드 필요

---

## 8. 리스크 식별

| # | 리스크 | 영향도 | 완화 방안 |
|---|--------|--------|----------|
| R1 | `svc_nm` -> `svc_cd` 변경 시 기존 데이터 마이그레이션 | 중 | H2 인메모리이므로 DataInitializer만 수정하면 충분. 운영 DB가 있다면 별도 마이그레이션 필요 |
| R2 | `fee_prod_cd` 공통코드 미존재 | 중 | `data.sql`에 요금상품코드 그룹+상세코드 추가 필요 |
| R3 | 대표가입 관리 네이티브 쿼리 파손 | 고 | `findListRaw` 쿼리에서 `s.svc_nm` 참조 변경, `SubscriptionMainListResponseDto` 필드명 변경 필수 |
| R4 | 콤보 글자 깨짐 원인 불명확 | 저 | 런타임 API 응답 확인이 선행되어야 정확한 조치 가능 |
| R5 | `SubscriptionMainPage`의 `subsNm` 빈 컬럼 | 저 | 네이티브 쿼리에 `s.subs_nm` 추가 + DTO에 필드 추가로 해결 가능 |
| R6 | `SubscriptionMainPage`의 `SVC_MAP` 하드코딩 제거 후 조회조건 변환 | 중 | `svc_cd` 기반 콤보로 전환 시 서버에 코드값 직접 전달하는 방식으로 단순화 |

---

## Summary (다음 단계 전달용)
- **수정 대상 파일**:
  - Backend: `Subscription.java`, `SubscriptionRequestDto.java`, `SubscriptionResponseDto.java`, `SubscriptionServiceImpl.java`, `SubscriptionRepository.java`, `SubscriptionDataInitializer.java`, `SubscriptionMainServiceImpl.java`, `SubscriptionMainListResponseDto.java`, `SubscriptionMainRepository.java`, `schema.sql`, `data.sql`
  - Frontend: `SubscriptionPage.vue`, `SubscriptionMainPage.vue`, `SpecialSubscriptionPage.vue`, `BillStdPage.vue`
- **핵심 의존성**: `tb_subscription.svc_nm/fee_prod_nm` 컬럼이 Entity, DTO, Repository 네이티브 쿼리, DataInitializer, 프론트엔드 3개 화면에 걸쳐 참조됨. `fee_prod_cd` 공통코드 그룹이 미존재
- **기술적 제약사항**: H2 인메모리 DB로 schema.sql 직접 수정, 네이티브 쿼리 수동 동기화 필수, 프론트엔드 빌드 산출물 재생성 필요
- **리스크**: 대표가입 네이티브 쿼리 파손(R3), fee_prod_cd 공통코드 신규 추가 필요(R2), 콤보 글자 깨짐 런타임 확인 필요(R4), SubscriptionMainPage의 subsNm 빈 컬럼 기존 버그(R5)
