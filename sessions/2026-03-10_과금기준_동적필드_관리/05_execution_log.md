# 05. Execution Log - 과금기준 동적필드 관리

## 실행 결과: 전체 완료

| 단계 | 내용 | 완료 | 비고 |
|---|---|---|---|
| 1 | DB 스키마 변경 (schema.sql) | O | tb_bill_std 14개 컬럼 제거, tb_bill_std_field_config/value 추가 |
| 2 | BillStdFieldConfig 백엔드 | O | Id/Entity/Repo/Service/ServiceImpl/Controller/RequestDto/ResponseDto |
| 3 | BillStdFieldValue 백엔드 | O | Id/Entity/Repo |
| 4 | BillStd 백엔드 수정 | O | Entity/RequestDto/ResponseDto/ServiceImpl - 14개 필드 제거, fieldValues Map 추가 |
| 5 | data.sql 초기 데이터 | O | SVC01(14개), SVC02(3개), SVC03(2개) Config + 메뉴 등록 |
| 6 | Config 관리 프론트엔드 | O | BillStdFieldConfigPage.vue, billStdFieldConfigApi.js, router 등록 |
| 7 | BillStdPage 동적 폼 수정 | O | Config 기반 동적 렌더링, fieldValues 저장/조회 연동 |

## 빌드 검증

| 대상 | 결과 |
|---|---|
| Backend (mvnw compile) | 성공 |
| Frontend (vite build) | 성공 |

## 신규 파일 목록

| 파일 | 설명 |
|---|---|
| `src/main/java/.../BillStdFieldConfigId.java` | 복합 PK (svcCd + fieldCd + effStartDt) |
| `src/main/java/.../BillStdFieldConfig.java` | Entity |
| `src/main/java/.../BillStdFieldConfigRepository.java` | Repository (findEffectiveBySvcCd 포함) |
| `src/main/java/.../BillStdFieldConfigService.java` | Service Interface |
| `src/main/java/.../BillStdFieldConfigServiceImpl.java` | Service Impl (CRUD + 사용종료 + 삭제차단) |
| `src/main/java/.../BillStdFieldConfigController.java` | REST Controller (/api/bill-std-field-configs) |
| `src/main/java/.../BillStdFieldConfigRequestDto.java` | Request DTO |
| `src/main/java/.../BillStdFieldConfigResponseDto.java` | Response DTO |
| `src/main/java/.../BillStdFieldValueId.java` | 복합 PK (billStdId + fieldCd) |
| `src/main/java/.../BillStdFieldValue.java` | Entity |
| `src/main/java/.../BillStdFieldValueRepository.java` | Repository |
| `frontend-vue/src/pages/BillStdFieldConfigPage.vue` | Config 관리 화면 |
| `frontend-vue/src/api/billStdFieldConfigApi.js` | Config API 호출 |

## 수정 파일 목록

| 파일 | 변경 요약 |
|---|---|
| `src/main/resources/schema.sql` | tb_bill_std 14개 컬럼 제거, tb_bill_std_field_config/value 추가 |
| `src/main/resources/data.sql` | Config 초기 데이터 (SVC01/02/03) 추가 |
| `src/main/java/.../BillStd.java` | 14개 서비스별 필드 + getter/setter 제거 |
| `src/main/java/.../BillStdRequestDto.java` | 14개 필드 제거, Map<String,String> fieldValues 추가 |
| `src/main/java/.../BillStdResponseDto.java` | 14개 필드 제거, Map<String,String> fieldValues 추가 |
| `src/main/java/.../BillStdServiceImpl.java` | ID채번 UUID->타임스탬프, fieldValueRepository 주입, create/update @Transactional + fieldValue 처리, toDto에서 fieldValue 로드, delete에서 fieldValue 삭제 |
| `src/main/java/.../MenuDataInitializer.java` | MNU014 과금기준필드설정 메뉴 추가 |
| `frontend-vue/src/pages/BillStdPage.vue` | 동적 폼 섹션 추가, EMPTY_FORM/toFormData/toRequestDto 변경, Config 조회 연동 |
| `frontend-vue/src/router/index.js` | /bill-std-field-config 라우트 추가 |

## 이슈

없음.
