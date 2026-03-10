# 01. 리서치 결과 — 대표가입 관리 엑셀 업로드/다운로드

## 1. 현행 구조 요약

### 1.1 백엔드 레이어 (SubscriptionMain 도메인)

| 레이어 | 파일 | 역할 |
|---|---|---|
| Controller | `SubscriptionMainController.java` | `GET /api/subscription-main` (페이징 목록), `POST /api/subscription-main` (단건 저장) |
| Service | `SubscriptionMainService.java` / `SubscriptionMainServiceImpl.java` | `findListPage()`, `save()` — 단건 저장만 지원 |
| Repository | `SubscriptionMainRepository.java` | `findActiveBySubsId()`, `findListRaw()` (네이티브 JOIN 쿼리) |
| Entity | `SubscriptionMain.java` | `tb_subscription_main` 테이블. PK=`subsMainId`, 이력관리용 `effStartDt`/`effEndDt` |
| DTO | `SubscriptionMainRequestDto.java` | subsId, mainSubsYn, mainSubsId, createdBy |
| DTO | `SubscriptionMainListResponseDto.java` | subsId, subsNm, svcCd, feeProdCd, mainSubsYn, mainSubsId |
| DTO | `SubscriptionMainResponseDto.java` | 저장 결과 반환용 (전체 필드) |

**핵심 비즈니스 로직 (`save()`):**
- 기존 활성 레코드의 `effEndDt`를 현재 시각으로 종료 처리 후, 새 레코드 INSERT (이력 관리 패턴)
- `mainSubsYn=Y`이면 `mainSubsId`를 null로 세팅
- `mainSubsYn=N`이면 `mainSubsId` 존재 여부를 `SubscriptionRepository.existsById()`로 검증

### 1.2 프론트엔드 (Vue 3)

| 파일 | 역할 |
|---|---|
| `pages/SubscriptionMainPage.vue` | 페이지 전체 상태 관리, 조회/저장 오케스트레이션 |
| `api/subscriptionMainApi.js` | `getSubscriptionMainList()`, `saveSubscriptionMain()` 2개 API 함수 |
| `components/common/DataGrid.vue` | 공통 그리드 (정렬, 필터, 리사이즈, 페이징 내장). **체크박스 선택 기능 미구현** |
| `components/common/FloatingActionBar.vue` | 하단 액션바 |
| `components/common/SubscriptionSearchPopup.vue` | 가입ID 검색 팝업 |
| `api/apiClient.js` | axios 래퍼 (baseURL=`/api`, withCredentials) |

**현재 페이지 흐름:**
서비스/검색유형/검색어로 조회 → 그리드 행 클릭 → 폼에 바인딩 → 대표가입여부/대표가입ID 편집 → 저장 (단건)

### 1.3 DB 스키마

`tb_subscription_main`은 JPA `ddl-auto=update`로 자동 생성됨 (schema.sql에 DDL 없음).

| 컬럼 | 타입 | 비고 |
|---|---|---|
| subs_main_id (PK) | VARCHAR(20) | `SM` + 타임스탬프 자동생성 |
| subs_id | VARCHAR(50) | `tb_subscription.subs_id` 참조 (FK 미설정) |
| main_subs_yn | VARCHAR(1) | Y/N |
| main_subs_id | VARCHAR(50) | 대표가입ID (Y일 때 null) |
| eff_start_dt / eff_end_dt | TIMESTAMP | 이력 유효기간 |
| 시스템 필드 4개 | | 표준 패턴 |

---

## 2. 영향 범위

### 2.1 신규 추가 필요

| 구분 | 내용 |
|---|---|
| **백엔드** | 엑셀 다운로드 API (`GET /api/subscription-main/excel`), 엑셀 업로드 파싱 API (`POST /api/subscription-main/excel`), 벌크 저장 API (`POST /api/subscription-main/bulk`) |
| **pom.xml** | Apache POI 의존성 추가 필요 (현재 없음) |
| **application.properties** | `spring.servlet.multipart.max-file-size` 설정 필요 |
| **프론트엔드** | DataGrid에 체크박스 선택 기능 추가, 엑셀 다운로드/업로드 버튼 및 로직, 변경 여부 상태 표시 컬럼 |
| **subscriptionMainApi.js** | 엑셀 다운로드/업로드/벌크 저장 API 함수 추가 |

### 2.2 기존 수정 필요

| 파일 | 변경 내용 |
|---|---|
| `SubscriptionMainController.java` | 엑셀 다운로드/업로드/벌크 저장 엔드포인트 추가 |
| `SubscriptionMainService.java` / `Impl` | 벌크 저장 메서드, 엑셀 생성/파싱 메서드 추가 |
| `SubscriptionMainPage.vue` | 그리드 체크박스 선택, 엑셀 업로드/다운로드 UI, 변경 표시 로직, 벌크 저장 호출 |
| `DataGrid.vue` | 체크박스 선택 기능 (optional — 페이지에서 직접 구현도 가능) |
| `SecurityConfig.java` | multipart 엔드포인트 허용 여부 확인 (현재 `/api/**`는 authenticated이므로 추가 설정 불필요) |

---

## 3. 의존성 그래프

```
SubscriptionMainPage.vue
  ├── subscriptionMainApi.js → apiClient.js → /api/subscription-main/*
  ├── DataGrid.vue (체크박스 선택 기능 추가 필요)
  ├── FloatingActionBar.vue (엑셀 다운로드/업로드/저장 버튼 배치)
  └── SubscriptionSearchPopup.vue (기존 유지)

SubscriptionMainController
  └── SubscriptionMainService(Impl)
        ├── SubscriptionMainRepository (JPA)
        ├── SubscriptionRepository.existsById() (대표가입ID 검증)
        └── [신규] Apache POI (엑셀 생성/파싱)
```

---

## 4. 기존 테스트 현황

- 프로젝트 전체에 `VibeStudyApplicationTests.java` 1개만 존재 (Spring Boot 기본 컨텍스트 로드 테스트 추정)
- SubscriptionMain 관련 단위/통합 테스트 **없음**
- 프론트엔드 테스트 설정 **없음** (package.json에 test 스크립트 미정의)

---

## 5. 기술적 제약사항

1. **Apache POI 미포함**: pom.xml에 POI 의존성 없음. 추가 필수
2. **Multipart 미설정**: application.properties에 파일 업로드 관련 설정 없음. `max-file-size`, `max-request-size` 설정 필요
3. **단건 저장 전용 설계**: 현재 `save()`가 단건 이력 관리 로직. 벌크 저장 시 N건 반복 호출하면 매 건마다 `findActiveBySubsId()` + 종료 + INSERT가 발생하므로, 트랜잭션 경계와 성능 고려 필요
4. **DataGrid 체크박스 없음**: 공통 DataGrid에 행 선택 체크박스 기능이 없음. 다운로드 대상 선택을 위해 추가 필요
5. **엑셀 업로드 후 미저장 상태**: 요구사항에 "아직 저장된 상태가 아니어야 함"이 명시됨. 업로드된 데이터를 프론트엔드 상태로만 관리하고, 명시적 저장 시에만 서버 반영
6. **변경 여부 판단**: 그리드 행의 원래 값(서버 데이터)과 업로드/편집 후 값을 비교하는 로직이 프론트엔드에 필요
7. **ID 자동생성 패턴**: `SM` + 타임스탬프. 벌크 INSERT 시 밀리초 단위 충돌 가능성 → 카운터 보완 또는 sleep 필요
8. **Lombok 미사용**: 모든 getter/setter 수동 선언 규칙 준수
9. **apiClient Content-Type**: 현재 `application/json` 고정. 파일 업로드 시 `multipart/form-data`로 요청해야 하므로 개별 호출에서 헤더 오버라이드 필요

---

## 6. 리스크 식별

| 리스크 | 영향 | 대응 방안 |
|---|---|---|
| DataGrid 체크박스 추가 시 공통 컴포넌트 변경 파급 | 다른 페이지에서 DataGrid 사용 중 (SubscriptionMainPage 외 다수) | props로 `selectable` 옵트인 방식으로 설계 |
| 벌크 저장 시 ID 충돌 | `SM` + 밀리초 패턴은 동시 생성 시 중복 가능 | AtomicLong 카운터 추가 또는 nanoTime 사용 |
| 대용량 엑셀 업로드 시 메모리 | POI의 XSSF(xlsx)는 전체를 메모리에 로드 | 업로드 건수 상한선 설정 (예: 1000건) |
| 엑셀 컬럼 매핑 오류 | 사용자가 컬럼 순서를 변경하거나 헤더명을 수정할 경우 | 헤더명 기반 매핑 + 유효성 검증 메시지 |
| 프론트 상태 관리 복잡도 | 서버 데이터 + 업로드 데이터 + 변경 추적을 동시에 관리 | 별도 상태 맵(originalData vs currentData)으로 관리 |

---

## Summary (다음 단계 전달용)
- **수정 대상 파일**: `pom.xml`, `application.properties`, `SubscriptionMainController.java`, `SubscriptionMainService.java`, `SubscriptionMainServiceImpl.java`, `SubscriptionMainPage.vue`, `subscriptionMainApi.js`, `DataGrid.vue`
- **핵심 의존성**: Apache POI (신규 추가), SubscriptionRepository (대표가입ID 검증), DataGrid 공통 컴포넌트 (체크박스 확장)
- **기술적 제약사항**: POI 미포함, Multipart 미설정, 단건 저장 전용 설계 (벌크 확장 필요), DataGrid 체크박스 미구현, 엑셀 업로드 데이터는 프론트 상태로만 관리 (미저장), apiClient 헤더 오버라이드 필요
- **리스크**: ID 밀리초 충돌 (벌크 시), DataGrid 공통 컴포넌트 변경 파급, 대용량 엑셀 메모리, 프론트 상태 관리 복잡도 (서버/업로드/변경 추적)
