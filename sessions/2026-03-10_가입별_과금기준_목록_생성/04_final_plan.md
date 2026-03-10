# 최종 구현 계획서

## Critic 반영 사항

### 이슈 1: SubsBillStdResponseDto에 과금기준명(billStdNm) 누락
- **원인**: 요구사항 6에서 표시 컬럼으로 "과금기준명"을 명시했으나, 계획서의 `SubsBillStdResponseDto`(2-7)와 JPQL 프로젝션(2-9)에 `billStdNm` 필드가 빠져있음
- **반영**: 2-7의 DTO에 `billStdNm` 필드 추가, 2-9의 JPQL SELECT 절에 `b.billStdNm` 추가, 4-2 DataGrid 컬럼에 과금기준명 포함

### 이슈 2: SubsBillStdResponseDto에 all-args 생성자 누락
- **원인**: JPQL `SELECT new ...` 프로젝션은 매칭되는 생성자가 필수인데, 2-7에서 필드만 나열하고 생성자 언급 없음
- **반영**: 2-7에 전체 필드를 받는 생성자를 명시적으로 선언하도록 보완

---

## 구현 전략 개요

7개 요구사항을 **의존성 기반**으로 4단계로 나누어 구현한다.
- **1단계**: 독립적 백엔드 변경 (DB 스키마, Entity, DTO, Repository)
- **2단계**: 백엔드 서비스/컨트롤러 로직
- **3단계**: 프론트엔드 변경 (기존 화면 수정)
- **4단계**: 프론트엔드 신규 화면 + 연동

각 요구사항의 백엔드/프론트엔드 변경을 하나의 단계 안에서 처리하되, 의존관계가 있는 요구사항(2→3, 6→7)은 순서를 보장한다.

---

## 변경 파일 목록

### 백엔드 (수정)
| 파일 | 요구사항 | 변경 유형 |
|---|---|---|
| `src/main/resources/schema.sql` | 1, 5 | DDL 컬럼 추가 |
| `src/main/resources/data.sql` | -- | 변경 없음 |
| `Subscription.java` | 1 | `adminId` 필드 추가 |
| `SubscriptionRequestDto.java` | 1 | `adminId` 필드 추가 |
| `SubscriptionResponseDto.java` | 1 | `adminId` 필드 추가 |
| `SubscriptionServiceImpl.java` | 1 | create/update/toDto에 adminId 매핑 |
| `Qna.java` | 5 | `noticeYn`, `noticeStartDt`, `noticeEndDt` 필드 추가 |
| `QnaRequestDto.java` | 5 | 공지 관련 필드 추가 |
| `QnaResponseDto.java` | 5 | 공지 관련 필드 추가 |
| `QnaServiceImpl.java` | 5 | create/update/toDto에 공지 매핑 + findAll 정렬 변경 |
| `QnaRepository.java` | 5 | 공지사항 우선 정렬 커스텀 쿼리 |
| `BillStdService.java` | 2 | `findTodoList()` 메서드 선언 추가 |
| `BillStdServiceImpl.java` | 2 | `findTodoList()` 구현 |
| `BillStdRepository.java` | 2 | `findByStdRegStatCdNotIn()` 쿼리 메서드 추가 |
| `BillStdController.java` | 2 | `GET /api/bill-std/todo` 엔드포인트 추가 |

### 백엔드 (신규)
| 파일 | 요구사항 | 설명 |
|---|---|---|
| `SubsBillStdController.java` | 6 | 가입별 과금기준 목록 API |
| `SubsBillStdService.java` | 6 | 인터페이스 |
| `SubsBillStdServiceImpl.java` | 6 | LEFT JOIN JPQL 조회 |
| `SubsBillStdResponseDto.java` | 6 | 조회 결과 DTO (생성자 프로젝션 포함) |

### 프론트엔드 (수정)
| 파일 | 요구사항 | 변경 유형 |
|---|---|---|
| `frontend-vue/src/pages/SubscriptionPage.vue` | 1 | 관리자ID 필드 + 검색 팝업 버튼 |
| `frontend-vue/src/api/subscriptionApi.js` | -- | 변경 없음 (기존 API 재활용) |
| `frontend-vue/src/components/main/DashboardContent.vue` | 2, 3 | TODO 카드 섹션 추가 |
| `frontend-vue/src/api/billStdApi.js` | 2 | `getTodoList()` 함수 추가 |
| `frontend-vue/src/pages/CommonCodePage.vue` | 4 | EMPTY_FORM에 기본값 로직 |
| `frontend-vue/src/pages/QnaPage.vue` | 5 | 목록에 공지사항 아이콘/라벨 표시 |
| `frontend-vue/src/pages/QnaDetailPage.vue` | 5 | 공지 체크박스 + 유효기간 입력 |
| `frontend-vue/src/api/qnaApi.js` | -- | 변경 없음 (기존 DTO 확장으로 자동 전달) |
| `frontend-vue/src/pages/BillStdPage.vue` | 7 | `onMounted`에서 `route.query.subsId` 읽어 자동 조회 |
| `frontend-vue/src/router/index.js` | 6 | 가입별 과금기준 목록 라우트 추가 |

### 프론트엔드 (신규)
| 파일 | 요구사항 | 설명 |
|---|---|---|
| `frontend-vue/src/pages/SubsBillStdListPage.vue` | 6 | 가입별 과금기준 목록 화면 |
| `frontend-vue/src/api/subsBillStdApi.js` | 6 | API 호출 함수 |
| `frontend-vue/src/components/common/UserSearchPopup.vue` | 1 | 사용자 검색 팝업 |

### 기타
| 파일 | 요구사항 | 변경 유형 |
|---|---|---|
| `MenuDataInitializer.java` | 6 | 가입별 과금기준 목록 메뉴 등록 |

---

## 단계별 구현 순서

### 단계 1: 스키마 및 Entity 변경 (요구사항 1, 5)

**1-1. schema.sql — tb_subscription에 admin_id 추가**

Before:
```sql
    chg_dt                  TIMESTAMP      NULL,                   -- 변경일시 (비즈니스)

    /* ── System Fields ─── ...
```

After:
```sql
    chg_dt                  TIMESTAMP      NULL,                   -- 변경일시 (비즈니스)
    admin_id                VARCHAR(50)    NULL,                   -- 관리자ID

    /* ── System Fields ─── ...
```

**1-2. schema.sql — tb_qna에 공지사항 컬럼 추가**

Before:
```sql
    answer_yn           VARCHAR(1)      DEFAULT 'N',
    created_by          VARCHAR(50)     NOT NULL,
```

After:
```sql
    answer_yn           VARCHAR(1)      DEFAULT 'N',
    notice_yn           CHAR(1)         DEFAULT 'N',            -- 공지사항여부
    notice_start_dt     TIMESTAMP       NULL,                   -- 공지시작일시
    notice_end_dt       TIMESTAMP       NULL,                   -- 공지종료일시
    created_by          VARCHAR(50)     NOT NULL,
```

**1-3. Subscription.java — adminId 필드 추가**

Before (chgDt 뒤, System Fields 앞):
```java
    @Column(name = "chg_dt")
    private LocalDateTime chgDt;

    /* ── System Fields ── */
```

After:
```java
    @Column(name = "chg_dt")
    private LocalDateTime chgDt;

    @Column(name = "admin_id", length = 50)
    private String adminId;

    /* ── System Fields ── */
```
+ getter/setter 추가

**1-4. SubscriptionRequestDto.java — adminId 추가**

`chgDt` 아래에 `private String adminId;` + getter/setter 추가

**1-5. SubscriptionResponseDto.java — adminId 추가**

`chgDt` 아래에 `private String adminId;` + getter/setter 추가

**1-6. Qna.java — 공지사항 필드 추가**

Before (answerYn 뒤, System Fields 앞):
```java
    @Column(name = "answer_yn", length = 1)
    private String answerYn = "N";

    /* ── System Fields ── */
```

After:
```java
    @Column(name = "answer_yn", length = 1)
    private String answerYn = "N";

    @Column(name = "notice_yn", length = 1)
    private String noticeYn = "N";

    @Column(name = "notice_start_dt")
    private LocalDateTime noticeStartDt;

    @Column(name = "notice_end_dt")
    private LocalDateTime noticeEndDt;

    /* ── System Fields ── */
```
+ getter/setter 6개 추가

**1-7. QnaRequestDto.java — 공지 필드 추가**

```java
private String noticeYn;
private LocalDateTime noticeStartDt;
private LocalDateTime noticeEndDt;
```
+ getter/setter 추가

**1-8. QnaResponseDto.java — 공지 필드 추가**

```java
private String noticeYn;
private LocalDateTime noticeStartDt;
private LocalDateTime noticeEndDt;
```
+ getter/setter 추가

---

### 단계 2: 백엔드 서비스/컨트롤러 (요구사항 1, 2, 5, 6)

**2-1. SubscriptionServiceImpl — adminId 매핑**

`create()` 메서드 내:
```java
entity.setAdminId(dto.getAdminId());
```

`update()` 메서드 내:
```java
entity.setAdminId(dto.getAdminId());
```

`toDto()` 메서드 내:
```java
dto.setAdminId(e.getAdminId());
```

**2-2. QnaServiceImpl — 공지 매핑 + 정렬 변경**

`create()`:
```java
entity.setNoticeYn(dto.getNoticeYn() != null ? dto.getNoticeYn() : "N");
entity.setNoticeStartDt(dto.getNoticeStartDt());
entity.setNoticeEndDt(dto.getNoticeEndDt());
```

`update()`:
```java
entity.setNoticeYn(dto.getNoticeYn() != null ? dto.getNoticeYn() : "N");
entity.setNoticeStartDt(dto.getNoticeStartDt());
entity.setNoticeEndDt(dto.getNoticeEndDt());
```

`toDto()`:
```java
dto.setNoticeYn(entity.getNoticeYn());
dto.setNoticeStartDt(entity.getNoticeStartDt());
dto.setNoticeEndDt(entity.getNoticeEndDt());
```

`findAll()` — 정렬 변경: 유효한 공지사항을 최상단에 배치.

Before:
```java
Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDt"));
```

After: `QnaRepository`에 커스텀 JPQL 쿼리를 추가하고, ServiceImpl에서 호출.

**2-3. QnaRepository — 공지사항 우선 정렬 쿼리**

기존 `findAll(Pageable)` 대신 커스텀 쿼리 사용:

```java
@Query("""
    SELECT q FROM Qna q
    ORDER BY
      CASE WHEN q.noticeYn = 'Y'
            AND q.noticeStartDt <= CURRENT_TIMESTAMP
            AND q.noticeEndDt >= CURRENT_TIMESTAMP
           THEN 0 ELSE 1 END,
      q.createdDt DESC
    """)
Page<Qna> findAllWithNoticeFirst(Pageable pageable);

@Query("""
    SELECT q FROM Qna q
    WHERE q.title LIKE %:keyword% OR q.content LIKE %:keyword%
    ORDER BY
      CASE WHEN q.noticeYn = 'Y'
            AND q.noticeStartDt <= CURRENT_TIMESTAMP
            AND q.noticeEndDt >= CURRENT_TIMESTAMP
           THEN 0 ELSE 1 END,
      q.createdDt DESC
    """)
Page<Qna> findByKeywordWithNoticeFirst(@Param("keyword") String keyword, Pageable pageable);
```

> **주의**: `@Query`에 ORDER BY를 명시하므로 `Pageable`에는 Sort를 지정하지 않는다 (`PageRequest.of(page, size)` — Sort 없이).

**2-4. BillStdRepository — TODO 조회용 메서드 추가**

```java
List<BillStd> findByStdRegStatCdNotIn(List<String> excludedStatuses);
```

**2-5. BillStdService / BillStdServiceImpl — findTodoList()**

인터페이스:
```java
List<BillStdResponseDto> findTodoList();
```

구현:
```java
@Override
public List<BillStdResponseDto> findTodoList() {
    return repository.findByStdRegStatCdNotIn(List.of("APPROVED", "CANCEL"))
            .stream()
            .map(this::toDto)
            .toList();
}
```

**2-6. BillStdController — TODO 엔드포인트**

```java
@GetMapping("/todo")
public List<BillStdResponseDto> getTodoList() {
    return billStdService.findTodoList();
}
```

> **주의**: `/todo`는 `/{id}` 보다 먼저 선언해야 경로 충돌 방지.

**2-7. SubsBillStdResponseDto (신규) — [Critic 반영: billStdNm 추가 + 생성자 선언]**

플랫 DTO — LEFT JOIN 결과를 담는다. JPQL `SELECT new` 프로젝션을 위해 all-args 생성자 필수 선언:

```java
public class SubsBillStdResponseDto {
    private String subsId;
    private String subsNm;
    private String subsStatusCd;
    private String billStdId;        // nullable
    private String billStdNm;        // nullable  ← Critic 반영: 요구사항 6 표시 컬럼
    private String stdRegStatCd;     // nullable
    private LocalDateTime effStartDt; // nullable
    private LocalDateTime effEndDt;   // nullable

    // JPQL SELECT new 프로젝션용 생성자 (필수)
    public SubsBillStdResponseDto(String subsId, String subsNm, String subsStatusCd,
            String billStdId, String billStdNm, String stdRegStatCd,
            LocalDateTime effStartDt, LocalDateTime effEndDt) {
        this.subsId = subsId;
        this.subsNm = subsNm;
        this.subsStatusCd = subsStatusCd;
        this.billStdId = billStdId;
        this.billStdNm = billStdNm;
        this.stdRegStatCd = stdRegStatCd;
        this.effStartDt = effStartDt;
        this.effEndDt = effEndDt;
    }

    // getter/setter
}
```

**2-8. SubsBillStdService (신규 인터페이스)**

```java
public interface SubsBillStdService {
    Page<SubsBillStdResponseDto> findList(String keyword, Pageable pageable);
}
```

**2-9. SubsBillStdServiceImpl (신규) — [Critic 반영: JPQL에 billStdNm 추가]**

JPQL로 Subscription LEFT JOIN BillStd 조회. `subs_status_cd != 'TERMINATED'` 조건 + `last_eff_yn = 'Y'` (LEFT JOIN 조건).

```java
@Service
public class SubsBillStdServiceImpl implements SubsBillStdService {

    private final EntityManager em;

    public SubsBillStdServiceImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public Page<SubsBillStdResponseDto> findList(String keyword, Pageable pageable) {
        String jpql = """
            SELECT new com.example.vibestudy.SubsBillStdResponseDto(
                s.subsId, s.subsNm, s.subsStatusCd,
                b.billStdId, b.billStdNm, b.stdRegStatCd, b.effStartDt, b.effEndDt
            )
            FROM Subscription s
            LEFT JOIN BillStd b ON b.subsId = s.subsId AND b.lastEffYn = 'Y'
            WHERE s.subsStatusCd <> 'TERMINATED'
            """;
        // keyword 조건 동적 추가
        // count 쿼리 별도 실행
        // Page 객체 생성 반환
    }
}
```

> **대안**: EntityManager JPQL vs Specification. JPQL이 LEFT JOIN + ON 조건에 더 명확하므로 선택.

**2-10. SubsBillStdController (신규)**

```java
@RestController
@RequestMapping("/api/subs-bill-std")
public class SubsBillStdController {

    private final SubsBillStdService service;

    public SubsBillStdController(SubsBillStdService service) {
        this.service = service;
    }

    @GetMapping("/list")
    public Page<SubsBillStdResponseDto> getList(
            @RequestParam(required = false) String keyword,
            Pageable pageable) {
        return service.findList(keyword, pageable);
    }
}
```

**2-11. MenuDataInitializer — 메뉴 추가**

`initMenuData()` 내 가입관리 하위에 추가:

```java
createMenu("MNU015", "가입별과금기준", "/subs-bill-std", "MNU002", 6, 2);
```

메뉴-역할 매핑 리스트에 `"MNU015"` 추가.

---

### 단계 3: 프론트엔드 기존 화면 수정 (요구사항 1, 2, 3, 4, 5, 7)

**3-1. UserSearchPopup.vue (신규 공통 컴포넌트)**

- Props: `visible`, `onSelect`, `onClose`
- 내부: `/api/users/page` API 호출 (기존 `searchSubscriptions` 패턴 참고)
- 이름/ID 검색 → 목록 표시 → 행 클릭 시 `onSelect(user)` emit

**3-2. SubscriptionPage.vue — 관리자ID 필드 추가**

입력 폼 grid 내 `chgDt` (또는 상태) 뒤에:
```html
<div>
  <label class="block text-xs text-gray-500 mb-1">관리자ID</label>
  <div class="flex gap-2">
    <input v-model="formData.adminId" readonly
      class="flex-1 h-8 px-2 border border-gray-200 rounded text-sm bg-gray-50 text-gray-400" />
    <button @click="showUserPopup = true"
      class="h-8 px-3 border border-gray-300 rounded text-sm text-gray-600 hover:bg-gray-50">검색</button>
  </div>
</div>
```

+ `UserSearchPopup` import 및 사용
+ `EMPTY_FORM`에 `adminId: ''` 추가
+ `formData` 매핑 시 `adminId` 포함

**3-3. DashboardContent.vue — TODO 카드 추가**

기존 미납 리스트 아래 또는 위에 TODO 섹션 추가:

```html
<!-- TODO 과금기준 -->
<div class="bg-white rounded-lg shadow-sm p-5">
  <h2 class="text-sm font-semibold text-gray-700 mb-3">
    과금기준 TODO <span class="text-[#2563EB] font-bold">{{ todoItems.length }}</span>건
  </h2>
  <p v-if="todoItems.length === 0" class="text-sm text-gray-400">처리할 TODO가 없습니다.</p>
  <table v-else class="w-full text-sm">
    <thead>
      <tr class="border-b border-gray-100">
        <th class="py-1 text-left text-xs text-gray-500 font-medium">가입ID</th>
        <th class="py-1 text-left text-xs text-gray-500 font-medium">등록진행상태</th>
      </tr>
    </thead>
    <tbody>
      <tr v-for="item in todoItems" :key="item.billStdId"
        class="border-b border-gray-50 hover:bg-blue-50 cursor-pointer"
        @click="router.push(`/bill-std?subsId=${item.subsId}`)">
        <td class="py-1.5 text-[#2563EB]">{{ item.subsId }}</td>
        <td class="py-1.5">{{ getLabel('std_reg_stat_cd', item.stdRegStatCd) }}</td>
      </tr>
    </tbody>
  </table>
</div>
```

+ `billStdApi`에서 `getTodoList` import
+ `onMounted`에서 TODO 데이터 fetch
+ `useCommonCodeLabel`에 `'std_reg_stat_cd'` 추가

**3-4. billStdApi.js — getTodoList 추가**

```js
export const getTodoList = () => apiClient.get('/bill-std/todo').then(res => res.data)
```

**3-5. CommonCodePage.vue — 유효일자 기본값**

공통코드 신규 등록 시 (신규 버튼 클릭 or 폼 초기화 시):
- `effStartDt`가 비어있으면 오늘 날짜 (`new Date().toISOString().slice(0, 10)`)
- `effEndDt`가 비어있으면 `'9999-12-31'`

상세코드 신규 등록 시도 동일 적용.

기존 코드 선택(수정) 시에는 기존 값 그대로 유지.

**3-6. QnaDetailPage.vue — 공지사항 입력 추가**

제목 입력 아래 또는 내용 입력 아래에:
```html
<div class="flex items-center gap-4">
  <label class="flex items-center gap-2 text-sm text-gray-700">
    <input type="checkbox" v-model="form.noticeYn" true-value="Y" false-value="N" />
    공지사항
  </label>
  <template v-if="form.noticeYn === 'Y'">
    <div>
      <label class="block text-xs text-gray-500 mb-1">공지 시작일</label>
      <input type="date" v-model="form.noticeStartDt"
        class="h-8 px-2 border border-gray-300 rounded text-sm" />
    </div>
    <div>
      <label class="block text-xs text-gray-500 mb-1">공지 종료일</label>
      <input type="date" v-model="form.noticeEndDt"
        class="h-8 px-2 border border-gray-300 rounded text-sm" />
    </div>
  </template>
</div>
```

+ `form`에 `noticeYn: 'N'`, `noticeStartDt: null`, `noticeEndDt: null` 추가

**3-7. QnaPage.vue — 공지사항 시각적 구분**

DataGrid 컬럼에 공지 표시 추가. `title` 컬럼의 cell 렌더링에서:
- `noticeYn === 'Y'`이고 유효기간 내이면 `[공지]` 라벨 prefix

```js
{
  key: 'title',
  header: '제목',
  size: 300,
  cell: (row) => {
    const isNotice = row.noticeYn === 'Y'
      && row.noticeStartDt && row.noticeEndDt
      && new Date() >= new Date(row.noticeStartDt)
      && new Date() <= new Date(row.noticeEndDt)
    return isNotice ? `[공지] ${row.title}` : row.title
  }
}
```

**3-8. BillStdPage.vue — route.query.subsId 자동 조회**

`onMounted`에서:
```js
const route = useRoute()
onMounted(() => {
  const subsId = route.query.subsId
  if (subsId) {
    searchType.value = 'subsId'
    keyword.value = subsId
    handleSearch()
  }
})
```

---

### 단계 4: 프론트엔드 신규 화면 (요구사항 6, 7)

**4-1. subsBillStdApi.js (신규)**

```js
import apiClient from './apiClient'

export const getSubsBillStdList = (params = {}) =>
  apiClient.get('/subs-bill-std/list', { params }).then(res => res.data)
```

**4-2. SubsBillStdListPage.vue (신규) — [Critic 반영: 과금기준명 컬럼 포함]**

- `MainLayout` 래핑
- SearchBar: 키워드 검색
- DataGrid: 페이징 목록
  - 컬럼: 가입ID, 가입명, 가입상태, 과금기준ID, **과금기준명**, 등록진행상태, 유효시작일, 유효종료일
- 행 클릭 → `router.push({ path: '/bill-std', query: { subsId: row.subsId } })`
- `useCommonCodeLabel`로 상태코드 라벨 변환

**4-3. router/index.js — 라우트 추가**

```js
{ path: '/subs-bill-std', name: 'SubsBillStdList', component: () => import('../pages/SubsBillStdListPage.vue') },
```

---

## 트레이드오프 기록

| 결정 | 선택 | 대안 | 근거 |
|---|---|---|---|
| 가입별 과금기준 조회 | EntityManager JPQL | Specification/Native Query | LEFT JOIN ON 조건을 JPQL에서 명시적 제어 가능 |
| 공지사항 정렬 | `@Query` 커스텀 JPQL | 서비스 레벨 정렬 | DB 정렬이 페이징과 자연스럽게 결합 |
| TODO API | 별도 `/todo` 엔드포인트 | 기존 `/api/bill-std` + query param | 용도가 명확히 다르고 필터 조건이 고정적 |
| UserSearchPopup | 공통 컴포넌트로 분리 | SubscriptionPage 내 인라인 | 다른 화면에서도 재사용 가능 |
| SubsBillStdResponseDto | 생성자 프로젝션 DTO | Entity 조합 후 변환 | JPQL `new` 표현식으로 조회 성능 최적화 |

---

## 테스트 계획

### 백엔드 수동 검증
1. **요구사항 1**: `POST /api/subscriptions` + `PUT`에 `adminId` 포함 → 저장/조회 확인
2. **요구사항 2**: `GET /api/bill-std/todo` → `APPROVED`, `CANCEL` 상태 제외 확인
3. **요구사항 5**: `POST /api/qna`에 `noticeYn=Y` + 유효기간 → `GET /api/qna` 목록 최상단 정렬 확인
4. **요구사항 6**: `GET /api/subs-bill-std/list` → 해지 제외, LEFT JOIN 결과 확인

### 프론트엔드 시나리오
1. **요구사항 1**: 가입 화면 → 관리자ID 돋보기 → 팝업 검색 → 선택 → 저장 → 재조회 확인
2. **요구사항 2+3**: 메인 대시보드 → TODO 카드에 미승인/미반려 과금기준 표시 확인 → 클릭 시 과금기준 화면 이동
3. **요구사항 4**: 공통코드 → 신규 등록 → effStartDt=오늘, effEndDt=9999-12-31 자동 입력 확인
4. **요구사항 5**: Q&A → 공지 등록 → 목록 최상단 [공지] 라벨 확인 → 기간 만료 후 일반 정렬 확인
5. **요구사항 6**: 사이드바 → 가입별과금기준 메뉴 → 목록 표시 → 해지 가입 미표시 확인
6. **요구사항 7**: 가입별과금기준 목록 → 행 클릭 → 과금기준 화면 자동 조회 확인

---

## 롤백 방안

- 모든 변경은 Git 단일 브랜치에서 커밋 단위로 관리
- 스키마 변경(schema.sql)은 `IF NOT EXISTS` / `ALTER TABLE ADD IF NOT EXISTS` 패턴 사용으로 멱등성 확보
- H2 인메모리 DB이므로 스키마 롤백은 서버 재시작으로 자동 복원
- 프론트엔드 변경은 기존 기능과 독립적 (신규 필드 추가, 신규 페이지) — 제거 시 기존 동작에 영향 없음
- 메뉴 데이터(`MenuDataInitializer`)는 `if (menuRepository.count() > 0) return` 조건 때문에 기존 환경에서는 수동 INSERT 또는 DB 리셋 필요

---
## Summary (다음 단계 전달용)
- **Critic 반영**: (1)SubsBillStdResponseDto에 billStdNm 필드 추가 — 요구사항 6 표시 컬럼 누락 보완 (2)동 DTO에 all-args 생성자 명시 — JPQL SELECT new 프로젝션 필수 요건
- **구현 순서**: (1)스키마+Entity 변경(tb_subscription.admin_id, tb_qna.notice_*) → (2)백엔드 서비스(Subscription adminId 매핑, Qna 공지정렬, BillStd TODO API, SubsBillStd 신규 API+Controller) → (3)프론트 기존화면(SubscriptionPage 관리자팝업, DashboardContent TODO, CommonCodePage 기본값, QnaDetailPage 공지입력, QnaPage 공지표시, BillStdPage query연동) → (4)프론트 신규화면(SubsBillStdListPage+API+라우트)
- **변경 파일**: `schema.sql`, `Subscription[.java|RequestDto|ResponseDto|ServiceImpl]`, `Qna[.java|RequestDto|ResponseDto|ServiceImpl|Repository]`, `BillStd[Service|ServiceImpl|Repository|Controller]`, `SubsBillStd[Controller|Service|ServiceImpl|ResponseDto]`(신규), `MenuDataInitializer`, `SubscriptionPage.vue`, `DashboardContent.vue`, `billStdApi.js`, `CommonCodePage.vue`, `QnaDetailPage.vue`, `QnaPage.vue`, `BillStdPage.vue`, `router/index.js`, `UserSearchPopup.vue`(신규), `SubsBillStdListPage.vue`(신규), `subsBillStdApi.js`(신규)
- **핵심 주의사항**: (1)BillStdController `/todo`를 `/{id}` 위에 선언 (2)QnaRepository 커스텀 쿼리 Pageable에 Sort 미지정 (3)SubsBillStdServiceImpl LEFT JOIN ON 조건에 `lastEffYn='Y'` 포함 (4)Lombok 금지 — 수동 getter/setter (5)MenuDataInitializer count>0 guard 때문에 기존 환경은 DB 리셋 필요 (6)SubsBillStdResponseDto에 all-args 생성자 필수 선언
