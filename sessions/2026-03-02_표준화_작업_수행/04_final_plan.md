# 3단계: 구현 계획서 (Plan)

---

## 1. 구현 전략 개요

### 1.1 접근 방식

**Bottom-up + 의존성 순 직렬 실행** 전략을 채택한다.

- Backend 인프라 레이어(GlobalExceptionHandler, Entity)를 먼저 수정하고,
  Service 계층을 신설한 뒤 Controller를 정리한다.
- Frontend는 공유 Hook 신설 → 컴포넌트 디렉토리 재배치 → 페이지 수정 순으로 진행한다.
- 기계적 변경(h-10→h-8 일괄 치환, import 경로 수정)은 마지막 단계에서 일괄 처리하여
  리뷰 비용을 최소화한다.

### 1.2 실행 원칙

- 코드를 삭제할 때는 파일 단위로 삭제(주석 처리 금지).
- 신규 파일 생성 후 기존 파일의 import를 교체하는 방식으로 위험을 최소화한다.
- 각 단계는 서버 재시작 없이 컴파일 오류가 없는 상태를 유지한다.
- Frontend 변경은 Vite HMR로 즉시 확인 가능하다.

---

## 2. 변경 파일 목록

### 2.1 Backend

| 구분 | 파일 | 변경 유형 |
|---|---|---|
| 수정 | `GlobalExceptionHandler.java` | ResponseStatusException 핸들러 추가 |
| 수정 | `StudyLog.java` | @Table 추가, 시스템 필드 4개 추가 |
| 수정 | `StudyLogRequestDto.java` | createdBy 필드 추가 |
| 신규 | `StudyLogService.java` | Service Interface 생성 |
| 신규 | `StudyLogServiceImpl.java` | Service Impl 생성 |
| 수정 | `StudyLogController.java` | Repository 의존 제거, Service 의존으로 교체 |
| 수정 | `UserService.java` | listUsers() 메서드 추가 |
| 수정 | `UserServiceImpl.java` | listUsers() 구현 추가 |
| 수정 | `AuthController.java` | UserRepository 직접 주입 제거 |
| 삭제 | `HelloController.java` | 파일 삭제 |

### 2.2 Frontend

| 구분 | 파일 | 변경 유형 |
|---|---|---|
| 이동 | `components/StudyLogForm.jsx` → `components/studylog/StudyLogForm.jsx` | 디렉토리 이동 |
| 이동 | `components/StudyLogTable.jsx` → `components/studylog/StudyLogTable.jsx` | 디렉토리 이동 |
| 이동 | `components/StudyLogItem.jsx` → `components/studylog/StudyLogItem.jsx` | 디렉토리 이동 (import 경로 내부 수정 포함) |
| 이동 | `components/EditModal.jsx` → `components/studylog/EditModal.jsx` | 디렉토리 이동 |
| 수정 | `hooks/useStudyLogs.js` | errorMsg/successMsg 반환 구조로 전환 |
| 수정 | `pages/StudyLogPage.jsx` | Toast 적용, import 경로 수정 |
| 신규 | `hooks/useSubscriptionSearch.js` | SubscriptionSearchPopup용 Hook 신설 |
| 수정 | `components/common/SubscriptionSearchPopup.jsx` | Hook 경유로 교체 |
| 신규 | `hooks/useUser.js` | UserPage용 Hook 신설 |
| 수정 | `pages/UserPage.jsx` | authApi 직접 import 제거, useUser 사용 |
| 수정 | `pages/SubscriptionPage.jsx` | ConfirmDialog 추가, clearMessages useEffect 추가 |
| 수정 | `pages/BillStdPage.jsx` | ConfirmDialog 추가 |
| 수정 | `pages/SubscriptionMainPage.jsx` | searchError → 인라인 computed 전환 |
| 수정 | `components/subscription-main/SubscriptionMainSearchBar.jsx` | searchError prop 타입/전달 방식 수정 |
| 수정 | `components/subscription/SubscriptionForm.jsx` | h-10 → h-8 |
| 수정 | `components/subscription/SubscriptionActionBar.jsx` | h-10 → h-8 |
| 수정 | `components/subscription/SubscriptionSearchBar.jsx` | h-10 → h-8 |
| 수정 | `components/billstd/BillStdForm.jsx` | h-10 → h-8 |
| 수정 | `components/billstd/BillStdActionBar.jsx` | h-10 → h-8 |
| 수정 | `components/billstd/BillStdSearchBar.jsx` | h-10 → h-8 |
| 수정 | `pages/UserPage.jsx` | h-10 → h-8 (Hook 수정과 동시 처리) |
| 삭제 | `components/common/Header.jsx` | 파일 삭제 |
| 삭제 | `components/common/Layout.jsx` | 파일 삭제 |

### 2.3 문서

| 구분 | 파일 | 변경 유형 |
|---|---|---|
| 수정 | `docs/backend-rules.md` | SM(대표가입) ID 접두사 등록 |

---

## 3. 단계별 구현 순서

의존성 흐름:

```
Step 1: BE 인프라 (GlobalExceptionHandler, HelloController 삭제)
  ↓
Step 2: BE StudyLog Entity + DTO 수정
  ↓
Step 3: BE StudyLog Service 계층 신설 + Controller 교체
  ↓
Step 4: BE AuthController UserRepository 의존 제거
  ↓
Step 5: FE StudyLog 컴포넌트 디렉토리 이동 + useStudyLogs 리팩토링 + StudyLogPage 수정
  ↓
Step 6: FE Hook 신설 (useSubscriptionSearch, useUser) + 연결된 컴포넌트/페이지 수정
  ↓
Step 7: FE SubscriptionPage ConfirmDialog + clearMessages 추가
  ↓
Step 8: FE BillStdPage ConfirmDialog 추가
  ↓
Step 9: FE SubscriptionMainPage searchError 제거
  ↓
Step 10: FE h-10 → h-8 일괄 치환 (6개 파일)
  ↓
Step 11: FE 미사용 컴포넌트 삭제 (Header.jsx, Layout.jsx)
  ↓
Step 12: DOC backend-rules.md SM 접두사 등록
```

---

## 4. 각 단계별 상세 내용

---

### Step 1: BE 인프라 — GlobalExceptionHandler 확장 + HelloController 삭제

**파일:** `GlobalExceptionHandler.java`, `HelloController.java`

**Before (`GlobalExceptionHandler.java`):**
```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, List<String>>> handleValidation(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getDefaultMessage())
                .toList();
        return ResponseEntity.badRequest().body(Map.of("errors", errors));
    }
}
```

**After (`GlobalExceptionHandler.java`):**
```java
import org.springframework.web.server.ResponseStatusException;
import java.util.HashMap;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getDefaultMessage())
                .toList();
        Map<String, Object> body = new HashMap<>();
        body.put("errors", errors);
        body.put("message", String.join(", ", errors));
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleResponseStatus(ResponseStatusException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("errors", List.of(ex.getReason() != null ? ex.getReason() : ex.getMessage()));
        body.put("message", ex.getReason() != null ? ex.getReason() : ex.getMessage());
        return ResponseEntity.status(ex.getStatusCode()).body(body);
    }
}
```

**설계 포인트:**
- `Map.of()`는 null 값을 허용하지 않으므로 `HashMap`으로 교체.
- `message` 키를 유지하여 Frontend의 `err?.response?.data?.message` 참조 코드가 무수정 동작.
- `MethodArgumentNotValidException` 응답에도 `message` 키를 추가하여 포맷 통일.
- `HelloController.java`: 파일 전체 삭제.

---

### Step 2: BE StudyLog Entity + DTO 수정

**파일:** `StudyLog.java`, `StudyLogRequestDto.java`

**Before (`StudyLog.java`):**
```java
@Entity
public class StudyLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;
    private LocalDate date;

    public Long getId() { return id; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
}
```

**After (`StudyLog.java`):**
```java
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_study_log")
public class StudyLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    private LocalDate date;

    /* ── System Fields ─────────────────────────────── */
    @Column(name = "created_by", length = 50, nullable = false)
    private String createdBy;

    @Column(name = "created_dt", nullable = false)
    private LocalDateTime createdDt;

    @Column(name = "updated_by", length = 50)
    private String updatedBy;

    @Column(name = "updated_dt")
    private LocalDateTime updatedDt;

    public Long getId() { return id; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public LocalDateTime getCreatedDt() { return createdDt; }
    public void setCreatedDt(LocalDateTime createdDt) { this.createdDt = createdDt; }
    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }
    public LocalDateTime getUpdatedDt() { return updatedDt; }
    public void setUpdatedDt(LocalDateTime updatedDt) { this.updatedDt = updatedDt; }
}
```

**설계 포인트:**
- PK 타입 `Long` 유지 (요구사항 BE-02 확정).
- `@Table(name = "tb_study_log")` 추가. JPA DDL auto 설정이 `update` 또는 `create`이면
  컬럼이 자동 추가됨. 기존 `study_log` → `tb_study_log` 테이블명 변경은 DDL auto가
  `create`일 경우 자동 처리, `validate`이면 수동 DDL 실행 필요.
- `application.properties`의 `spring.jpa.hibernate.ddl-auto` 값 확인 후 대응 필요.

**Before (`StudyLogRequestDto.java`):**
```java
public class StudyLogRequestDto {
    @NotBlank(message = "학습 내용을 입력해주세요.")
    @Size(min = 2, message = "최소 2자 이상 입력해주세요.")
    private String content;

    @NotNull(message = "날짜를 선택해주세요.")
    private LocalDate date;
    // getter/setter
}
```

**After (`StudyLogRequestDto.java`):**
```java
public class StudyLogRequestDto {
    @NotBlank(message = "학습 내용을 입력해주세요.")
    @Size(min = 2, message = "최소 2자 이상 입력해주세요.")
    private String content;

    @NotNull(message = "날짜를 선택해주세요.")
    private LocalDate date;

    private String createdBy;  // INSERT → created_by, UPDATE → updated_by 에 각각 매핑

    // getter/setter (createdBy 포함)
}
```

---

### Step 3: BE StudyLog Service 계층 신설 + Controller 교체

**신규 파일:** `StudyLogService.java`
```java
package com.example.vibestudy;

import java.util.List;

public interface StudyLogService {
    List<StudyLog> getAll();
    StudyLog create(StudyLogRequestDto dto);
    StudyLog update(Long id, StudyLogRequestDto dto);
    void delete(Long id);
}
```

**신규 파일:** `StudyLogServiceImpl.java`
```java
package com.example.vibestudy;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class StudyLogServiceImpl implements StudyLogService {

    private final StudyLogRepository studyLogRepository;

    public StudyLogServiceImpl(StudyLogRepository studyLogRepository) {
        this.studyLogRepository = studyLogRepository;
    }

    @Override
    public List<StudyLog> getAll() {
        return studyLogRepository.findAll();
    }

    @Override
    public StudyLog create(StudyLogRequestDto dto) {
        StudyLog log = new StudyLog();
        log.setContent(dto.getContent());
        log.setDate(dto.getDate());
        log.setCreatedBy(dto.getCreatedBy() != null ? dto.getCreatedBy() : "SYSTEM");
        log.setCreatedDt(LocalDateTime.now());
        return studyLogRepository.save(log);
    }

    @Override
    public StudyLog update(Long id, StudyLogRequestDto dto) {
        StudyLog log = studyLogRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "학습 로그를 찾을 수 없습니다."));
        log.setContent(dto.getContent());
        log.setDate(dto.getDate());
        log.setUpdatedBy(dto.getCreatedBy() != null ? dto.getCreatedBy() : "SYSTEM");
        log.setUpdatedDt(LocalDateTime.now());
        return studyLogRepository.save(log);
    }

    @Override
    public void delete(Long id) {
        if (!studyLogRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "학습 로그를 찾을 수 없습니다.");
        }
        studyLogRepository.deleteById(id);
    }
}
```

**Before (`StudyLogController.java`):**
```java
@RestController
@RequestMapping("/api/logs")
public class StudyLogController {

    private final StudyLogRepository repository;

    public StudyLogController(StudyLogRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<StudyLog> getAll() {
        return repository.findAll();
    }

    @PostMapping
    public ResponseEntity<StudyLog> create(@Valid @RequestBody StudyLogRequestDto dto) {
        StudyLog saved = repository.save(toEntity(dto));
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StudyLog> update(@PathVariable Long id, @Valid @RequestBody StudyLogRequestDto dto) {
        return repository.findById(id).map(log -> {
            log.setContent(dto.getContent());
            log.setDate(dto.getDate());
            return ResponseEntity.ok(repository.save(log));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!repository.existsById(id)) return ResponseEntity.notFound().build();
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private StudyLog toEntity(StudyLogRequestDto dto) {
        StudyLog log = new StudyLog();
        log.setContent(dto.getContent());
        log.setDate(dto.getDate());
        return log;
    }
}
```

**After (`StudyLogController.java`):**
```java
@RestController
@RequestMapping("/api/logs")
public class StudyLogController {

    private final StudyLogService studyLogService;

    public StudyLogController(StudyLogService studyLogService) {
        this.studyLogService = studyLogService;
    }

    @GetMapping
    public List<StudyLog> getAll() {
        return studyLogService.getAll();
    }

    @PostMapping
    public ResponseEntity<StudyLog> create(@Valid @RequestBody StudyLogRequestDto dto) {
        return ResponseEntity.ok(studyLogService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<StudyLog> update(@PathVariable Long id, @Valid @RequestBody StudyLogRequestDto dto) {
        return ResponseEntity.ok(studyLogService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        studyLogService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
```

**설계 포인트:**
- `toEntity()` private 메서드를 Controller에서 제거하고 ServiceImpl 내부로 이전.
- Controller는 HTTP 응답 구성에만 집중.
- `update` / `delete`에서 404 처리를 Service가 `ResponseStatusException`으로 던지므로
  Controller에서 Optional 분기 코드가 사라짐.

---

### Step 4: BE AuthController UserRepository 의존 제거

**파일:** `UserService.java`, `UserServiceImpl.java`, `AuthController.java`

**Before (`UserService.java`):**
```java
public interface UserService {
    UserSessionDto authenticate(String userId, String password);
    UserSessionDto register(RegisterRequestDto dto);
}
```

**After (`UserService.java`):**
```java
import java.util.List;
import java.util.Map;

public interface UserService {
    UserSessionDto authenticate(String userId, String password);
    UserSessionDto register(RegisterRequestDto dto);
    List<Map<String, Object>> listUsers();
}
```

**UserServiceImpl.java 추가 메서드:**
```java
@Override
public List<Map<String, Object>> listUsers() {
    return userRepository.findAll().stream()
        .map(u -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("userId", u.getUserId());
            m.put("nickname", u.getNickname());
            m.put("email", u.getEmail());
            m.put("accountStatus", u.getAccountStatus());
            return m;
        })
        .collect(Collectors.toList());
}
```

**Before (`AuthController.java` — 생성자 및 listUsers):**
```java
private final UserService userService;
private final UserRepository userRepository;

public AuthController(UserService userService, UserRepository userRepository) {
    this.userService = userService;
    this.userRepository = userRepository;
}

@GetMapping("/users")
public ResponseEntity<List<Map<String, Object>>> listUsers() {
    List<Map<String, Object>> users = userRepository.findAll().stream()
        .map(u -> { ... })
        .collect(Collectors.toList());
    return ResponseEntity.ok(users);
}
```

**After (`AuthController.java` — 생성자 및 listUsers):**
```java
private final UserService userService;

public AuthController(UserService userService) {
    this.userService = userService;
}

@GetMapping("/users")
public ResponseEntity<List<Map<String, Object>>> listUsers() {
    return ResponseEntity.ok(userService.listUsers());
}
```

**설계 포인트:**
- `UserRepository` import 및 필드 선언 전체 제거.
- 매핑 로직이 ServiceImpl로 이동하므로 AuthController의 import 목록(`LinkedHashMap`,
  `Collectors`)도 함께 제거.

---

### Step 5: FE StudyLog 컴포넌트 디렉토리 이동 + Hook/Page 리팩토링

#### 5-1. 디렉토리 생성 및 파일 이동

생성 디렉토리: `frontend/src/components/studylog/`

이동 파일 목록:
- `components/StudyLogForm.jsx` → `components/studylog/StudyLogForm.jsx`
- `components/StudyLogTable.jsx` → `components/studylog/StudyLogTable.jsx`
- `components/StudyLogItem.jsx` → `components/studylog/StudyLogItem.jsx`
- `components/EditModal.jsx` → `components/studylog/EditModal.jsx`

**StudyLogItem.jsx 내부 import 수정:**

Before:
```jsx
import ConfirmDialog from './common/ConfirmDialog'
```

After (경로 변경):
```jsx
import ConfirmDialog from '../common/ConfirmDialog'
```

**StudyLogTable.jsx 내부 import 수정:**

Before:
```jsx
import StudyLogItem from './StudyLogItem'
```

After:
```jsx
import StudyLogItem from './StudyLogItem'
// (같은 디렉토리이므로 경로 변경 없음)
```

#### 5-2. useStudyLogs.js 리팩토링

**Before:**
```js
const [isLoading, setIsLoading] = useState(true)
const [error, setError] = useState(null)
// ...
return {
  logs, isLoading, error, editingLog, setEditingLog,
  fetchLogs, handleCreate, handleDelete, handleUpdate,
}
```

**After:**
```js
const [isLoading, setIsLoading] = useState(true)
const [errorMsg, setErrorMsg] = useState(null)
const [successMsg, setSuccessMsg] = useState(null)

const clearMessages = () => { setErrorMsg(null); setSuccessMsg(null) }

const fetchLogs = useCallback(() => {
  setIsLoading(true)
  clearMessages()
  getLogs()
    .then(setLogs)
    .catch(() => setErrorMsg('서버와 연결할 수 없습니다.'))
    .finally(() => setIsLoading(false))
}, [])

const handleCreate = async (data) => {
  clearMessages()
  try {
    const newLog = await createLog(data)
    setLogs(prev => [...prev, newLog])
    setSuccessMsg('등록이 완료되었습니다.')
  } catch (err) {
    const status = err?.response?.status
    if (status === 400) {
      setErrorMsg(err?.response?.data?.message || '입력값을 확인해 주세요.')
    } else {
      setErrorMsg('저장에 실패했습니다.')
    }
  }
}

const handleDelete = async (id) => {
  clearMessages()
  try {
    await deleteLog(id)
    setLogs(prev => prev.filter(l => l.id !== id))
    setSuccessMsg('삭제가 완료되었습니다.')
  } catch {
    setErrorMsg('삭제에 실패했습니다.')
  }
}

const handleUpdate = async (id, data) => {
  clearMessages()
  try {
    const updated = await updateLog(id, data)
    setLogs(prev => prev.map(l => l.id === updated?.id ? updated : l))
    setEditingLog(null)
    setSuccessMsg('수정이 완료되었습니다.')
  } catch (err) {
    const status = err?.response?.status
    if (status === 400) {
      setErrorMsg(err?.response?.data?.message || '입력값을 확인해 주세요.')
    } else {
      setErrorMsg('수정에 실패했습니다.')
    }
  }
}

return {
  logs, isLoading, errorMsg, successMsg,
  editingLog, setEditingLog,
  fetchLogs, handleCreate, handleDelete, handleUpdate,
  clearMessages,
}
```

**설계 포인트:**
- `error` → `errorMsg`로 변수명 변경(표준 준수).
- `successMsg` 추가 (저장/수정/삭제 성공 시 Toast 표시).
- 각 액션 시작 전 `clearMessages()` 호출.
- HTTP 상태코드 기반 에러 분기 적용.

#### 5-3. StudyLogPage.jsx 수정

**Before:**
```jsx
import useStudyLogs from '../hooks/useStudyLogs'
import MainLayout from '../components/common/MainLayout'
import Loading from '../components/common/Loading'
import ErrorMessage from '../components/common/ErrorMessage'
import StudyLogForm from '../components/StudyLogForm'
import StudyLogTable from '../components/StudyLogTable'
import EditModal from '../components/EditModal'

export default function StudyLogPage() {
  const {
    logs, isLoading, error,
    editingLog, setEditingLog,
    fetchLogs, handleCreate, handleDelete, handleUpdate,
  } = useStudyLogs()

  return (
    <MainLayout>
      <StudyLogForm onSubmit={handleCreate} />
      {isLoading ? (
        <Loading />
      ) : error ? (
        <ErrorMessage message={error} onRetry={fetchLogs} />
      ) : (
        <StudyLogTable logs={logs} onDelete={handleDelete} onEditClick={setEditingLog} />
      )}
      <EditModal log={editingLog} onSubmit={handleUpdate} onClose={() => setEditingLog(null)} />
    </MainLayout>
  )
}
```

**After:**
```jsx
import useStudyLogs from '../hooks/useStudyLogs'
import MainLayout from '../components/common/MainLayout'
import Toast from '../components/common/Toast'
import Loading from '../components/common/Loading'
import ErrorMessage from '../components/common/ErrorMessage'
import StudyLogForm from '../components/studylog/StudyLogForm'
import StudyLogTable from '../components/studylog/StudyLogTable'
import EditModal from '../components/studylog/EditModal'

export default function StudyLogPage() {
  const {
    logs, isLoading, errorMsg, successMsg,
    editingLog, setEditingLog,
    fetchLogs, handleCreate, handleDelete, handleUpdate,
  } = useStudyLogs()

  return (
    <MainLayout>
      <Toast message={successMsg} type="success" onClose={() => {}} />
      <Toast message={errorMsg}   type="error"   onClose={() => {}} />

      <div className="space-y-4 pb-20">
        <h1 className="text-xl font-bold text-gray-800">학습 로그</h1>

        <StudyLogForm onSubmit={handleCreate} />

        {isLoading ? (
          <Loading />
        ) : (
          <StudyLogTable logs={logs} onDelete={handleDelete} onEditClick={setEditingLog} />
        )}
      </div>

      <EditModal log={editingLog} onSubmit={handleUpdate} onClose={() => setEditingLog(null)} />
    </MainLayout>
  )
}
```

**설계 포인트:**
- `ErrorMessage` 제거. 초기 로드 실패는 `errorMsg` Toast로 처리.
  (초기 로드 실패를 ErrorMessage로 구분해야 할 필요가 없는 단순 목록 화면으로 판단.)
- Toast onClose 핸들러: hook에서 setErrorMsg/setSuccessMsg 반환하거나 Page에서
  `useState`로 분리하는 방식 중 선택. 여기서는 Toast onClose 시 hook 내부 상태를
  직접 건드릴 수 없으므로, hook에서 `setErrorMsg`, `setSuccessMsg` setter를 함께 반환하는
  방식으로 처리:
  ```js
  return {
    ...,
    setErrorMsg,
    setSuccessMsg,
  }
  ```
  Page에서: `onClose={() => setErrorMsg(null)}`

---

### Step 6: FE Hook 신설 — useSubscriptionSearch + useUser

#### 6-1. useSubscriptionSearch.js (신규)

**파일:** `frontend/src/hooks/useSubscriptionSearch.js`

```js
import { useState } from 'react'
import { searchSubscriptions } from '../api/subscriptionApi'

export default function useSubscriptionSearch() {
  const [results, setResults] = useState([])
  const [isSearching, setIsSearching] = useState(false)

  const search = async (keyword) => {
    if (!keyword.trim()) return
    setIsSearching(true)
    try {
      const data = await searchSubscriptions('SUBS_ID', keyword.trim())
      setResults(data)
    } catch {
      setResults([])
    } finally {
      setIsSearching(false)
    }
  }

  const reset = () => setResults([])

  return { results, isSearching, search, reset }
}
```

**SubscriptionSearchPopup.jsx 수정:**

Before:
```jsx
import { useState } from 'react'
import { searchSubscriptions } from '../../api/subscriptionApi'

export default function SubscriptionSearchPopup({ isOpen, onClose, onSelect }) {
  const [keyword, setKeyword] = useState('')
  const [results, setResults] = useState([])
  const [isSearching, setIsSearching] = useState(false)

  const handleSearch = async () => {
    if (!keyword.trim()) return
    setIsSearching(true)
    try {
      const data = await searchSubscriptions('SUBS_ID', keyword.trim())
      setResults(data)
    } catch {
      setResults([])
    } finally {
      setIsSearching(false)
    }
  }
  // ...
}
```

After:
```jsx
import { useState } from 'react'
import useSubscriptionSearch from '../../hooks/useSubscriptionSearch'

export default function SubscriptionSearchPopup({ isOpen, onClose, onSelect }) {
  const [keyword, setKeyword] = useState('')
  const { results, isSearching, search } = useSubscriptionSearch()

  if (!isOpen) return null

  const handleSearch = () => search(keyword)
  const handleKeyDown = (e) => { if (e.key === 'Enter') handleSearch() }

  const handleSelect = (subsId) => {
    onSelect(subsId)
    onClose()
  }

  return (
    // JSX 구조 동일 유지 — 상태 선언부만 교체
    ...
  )
}
```

#### 6-2. useUser.js (신규)

**파일:** `frontend/src/hooks/useUser.js`

```js
import { useState, useEffect, useCallback } from 'react'
import { getUsers, register } from '../api/authApi'

const EMPTY_FORM = { userId: '', nickname: '', password: '', email: '' }

export default function useUser() {
  const [users, setUsers] = useState([])
  const [form, setForm] = useState(EMPTY_FORM)
  const [errorMsg, setErrorMsg] = useState(null)
  const [successMsg, setSuccessMsg] = useState(null)

  const clearMessages = () => { setErrorMsg(null); setSuccessMsg(null) }

  const fetchUsers = useCallback(async () => {
    try {
      setUsers(await getUsers())
    } catch {
      setErrorMsg('사용자 목록 조회에 실패했습니다.')
    }
  }, [])

  useEffect(() => { fetchUsers() }, [fetchUsers])

  const handleRegister = async () => {
    clearMessages()
    try {
      await register(form)
      setSuccessMsg('사용자가 등록되었습니다.')
      setForm(EMPTY_FORM)
      fetchUsers()
    } catch (err) {
      setErrorMsg(err?.response?.data?.message || '사용자 등록에 실패했습니다.')
    }
  }

  return {
    users, form, setForm, errorMsg, setErrorMsg, successMsg, setSuccessMsg,
    handleRegister,
  }
}
```

**UserPage.jsx 수정:**

Before:
```jsx
import { useState, useEffect } from 'react'
import { getUsers, register } from '../api/authApi'
// ...
const [users, setUsers] = useState([])
const [form, setForm] = useState(EMPTY_FORM)
const [errorMsg, setErrorMsg] = useState(null)
const [successMsg, setSuccessMsg] = useState(null)
// fetchUsers, handleRegister 인라인 구현
```

After:
```jsx
import useUser from '../hooks/useUser'
import MainLayout from '../components/common/MainLayout'
import Toast from '../components/common/Toast'

const EMPTY_FORM = { userId: '', nickname: '', password: '', email: '' }

export default function UserPage() {
  const {
    users, form, setForm,
    errorMsg, setErrorMsg, successMsg, setSuccessMsg,
    handleRegister,
  } = useUser()

  const onFieldChange = (e) => {
    const { name, value } = e.target
    setForm(prev => ({ ...prev, [name]: value }))
  }

  return (
    <MainLayout>
      <Toast message={successMsg} type="success" onClose={() => setSuccessMsg(null)} />
      <Toast message={errorMsg}   type="error"   onClose={() => setErrorMsg(null)} />
      // ... 기존 JSX (h-10 → h-8 변경은 Step 10에서 처리)
    </MainLayout>
  )
}
```

---

### Step 7: FE SubscriptionPage — ConfirmDialog + clearMessages useEffect

**파일:** `frontend/src/pages/SubscriptionPage.jsx`

#### 7-1. ConfirmDialog 추가

Before (`onDelete`):
```jsx
const onDelete = async () => {
  clearMessages()
  if (!isRowSelected) { setErrorMsg('가입을 선택해 주세요.'); return }
  try {
    await handleDelete(selectedSubs.subsId)
    // ...
  } catch (err) { ... }
}
```

After:
```jsx
const [confirmOpen, setConfirmOpen] = useState(false)

const onDeleteClick = () => {
  clearMessages()
  if (!isRowSelected) { setErrorMsg('가입을 선택해 주세요.'); return }
  setConfirmOpen(true)
}

const onDelete = async () => {
  try {
    await handleDelete(selectedSubs.subsId)
    setItems(prev => prev.filter(item => item.subsId !== selectedSubs.subsId))
    setSelectedSubs(null)
    setFormData(EMPTY_FORM)
    setSuccessMsg('삭제가 완료되었습니다.')
  } catch (err) {
    const status = err?.response?.status
    setErrorMsg(status === 409
      ? '과금기준이 존재하는 가입은 삭제할 수 없습니다.'
      : '삭제에 실패했습니다.')
  }
}
```

JSX:
```jsx
import ConfirmDialog from '../components/common/ConfirmDialog'

// SubscriptionActionBar의 onDelete prop을 onDeleteClick으로 교체
<SubscriptionActionBar
  onRegister={onRegister}
  onUpdate={onUpdate}
  onDelete={onDeleteClick}
/>

{confirmOpen && (
  <ConfirmDialog
    message="삭제하시겠습니까? 이 작업은 되돌릴 수 없습니다."
    onConfirm={() => { setConfirmOpen(false); onDelete() }}
    onCancel={() => setConfirmOpen(false)}
  />
)}
```

#### 7-2. clearMessages useEffect 추가

Before:
```jsx
useEffect(() => {
  const subsId = searchParams.get('subsId')
  if (subsId) {
    setKeyword(subsId)
    setSearchType('SUBS_ID')
    handleSearch('SUBS_ID', subsId).then(result => setItems(result)).catch(() => setErrorMsg('조회에 실패했습니다.'))
  }
}, [])
```

After:
```jsx
useEffect(() => {
  clearMessages()
  const subsId = searchParams.get('subsId')
  if (subsId) {
    setKeyword(subsId)
    setSearchType('SUBS_ID')
    handleSearch('SUBS_ID', subsId)
      .then(result => setItems(result))
      .catch(() => setErrorMsg('조회에 실패했습니다.'))
  }
}, [])
```

---

### Step 8: FE BillStdPage — ConfirmDialog 추가

**파일:** `frontend/src/pages/BillStdPage.jsx`

Before (`handleDeleteClick`):
```jsx
const handleDeleteClick = async () => {
  if (!formData.billStdId) { setErrorMsg('조회 후 삭제할 수 있습니다.'); return }
  clearMessages()
  try {
    await handleDelete(formData.billStdId)
    // ...
  }
}
```

After:
```jsx
import ConfirmDialog from '../components/common/ConfirmDialog'

const [confirmOpen, setConfirmOpen] = useState(false)

const handleDeleteClick = () => {
  if (!formData.billStdId) { setErrorMsg('조회 후 삭제할 수 있습니다.'); return }
  clearMessages()
  setConfirmOpen(true)
}

const executeDelete = async () => {
  try {
    await handleDelete(formData.billStdId)
    setFormData(EMPTY_FORM)
    setSuccessMsg('삭제가 완료되었습니다.')
  } catch (err) {
    const status = err?.response?.status
    setErrorMsg(
      status === 409
        ? '다른 이력이 존재하여 삭제할 수 없습니다.'
        : '삭제에 실패했습니다.'
    )
  }
}
```

JSX 추가:
```jsx
{confirmOpen && (
  <ConfirmDialog
    message="삭제하시겠습니까? 이 작업은 되돌릴 수 없습니다."
    onConfirm={() => { setConfirmOpen(false); executeDelete() }}
    onCancel={() => setConfirmOpen(false)}
  />
)}
```

`BillStdActionBar`의 `onDelete` prop은 `handleDeleteClick` 유지.

---

### Step 9: FE SubscriptionMainPage — searchError 제거

**파일:** `frontend/src/pages/SubscriptionMainPage.jsx`,
         `frontend/src/components/subscription-main/SubscriptionMainSearchBar.jsx`

**설계:** `searchError`는 클라이언트 유효성 검사(keyword 비어있음, 2자 미만) 결과로만
사용됨. 이를 `computed` 파생값으로 전환하여 별도 state를 제거한다.

Before:
```jsx
const [searchError, setSearchError] = useState(null)

const handleSearch = async () => {
  setSearchError(null)
  if (!keyword.trim()) {
    setSearchError('조회조건을 입력해 주세요.')
    return
  }
  if (keyword.trim().length < 2) {
    setSearchError('조회조건은 2자 이상 입력해 주세요.')
    return
  }
  // ...
}
```

After:
```jsx
// searchError state 제거

const getSearchError = () => {
  if (!keyword.trim()) return '조회조건을 입력해 주세요.'
  if (keyword.trim().length < 2) return '조회조건은 2자 이상 입력해 주세요.'
  return null
}

const handleSearch = async () => {
  const validationError = getSearchError()
  if (validationError) {
    setErrorMsg(validationError)
    return
  }
  setErrorMsg(null)
  setSuccessMsg(null)
  // ...
}
```

**SubscriptionMainSearchBar.jsx 수정:**

`searchError` prop을 `SubscriptionMainSearchBar`에 전달하는 방식 대신,
Page에서 `errorMsg` Toast로 처리하므로 `SubscriptionMainSearchBar`의
`searchError` prop과 관련 JSX(`<p>` 태그) 제거.

Before:
```jsx
export default function SubscriptionMainSearchBar({
  ..., searchError,
}) {
  return (
    <div className="...">
      <div className="flex items-center gap-3">...</div>
      {searchError && (
        <p className="mt-1.5 text-xs text-red-500">{searchError}</p>
      )}
    </div>
  )
}
```

After:
```jsx
export default function SubscriptionMainSearchBar({
  svcNm, onSvcNmChange,
  searchType, onSearchTypeChange,
  keyword, onKeywordChange,
  onSearch, isLoading,
  // searchError prop 제거
}) {
  return (
    <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-3">
      <div className="flex items-center gap-3">
        // 기존 JSX 유지
      </div>
      // searchError <p> 태그 제거
    </div>
  )
}
```

SubscriptionMainPage에서 `SubscriptionMainSearchBar`로의 `searchError` prop 전달도 제거.

---

### Step 10: FE h-10 → h-8 일괄 치환

**대상 파일 6개:**

| 파일 | 치환 내용 |
|---|---|
| `components/subscription/SubscriptionForm.jsx` | 모든 `h-10` → `h-8` |
| `components/subscription/SubscriptionActionBar.jsx` | 모든 `h-10` → `h-8` |
| `components/subscription/SubscriptionSearchBar.jsx` | 모든 `h-10` → `h-8` |
| `components/billstd/BillStdForm.jsx` | `inputClass`의 `h-10` → `h-8` |
| `components/billstd/BillStdActionBar.jsx` | 모든 `h-10` → `h-8` |
| `components/billstd/BillStdSearchBar.jsx` | 모든 `h-10` → `h-8` |

`UserPage.jsx`의 `h-10` → `h-8` 변경은 Step 6에서 Hook 수정 시 함께 처리.

**BillStdForm.jsx 구체 변경:**

Before (Field 컴포넌트 내 inputClass):
```jsx
const inputClass = [
  'flex-1 h-10 border rounded-md px-3 text-sm transition-colors',
  ...
].join(' ')
```

After:
```jsx
const inputClass = [
  'flex-1 h-8 border rounded-md px-3 text-sm transition-colors',
  ...
].join(' ')
```

---

### Step 11: FE 미사용 컴포넌트 삭제

**삭제 파일:**
- `frontend/src/components/common/Header.jsx` — 삭제
- `frontend/src/components/common/Layout.jsx` — 삭제

App.jsx 및 모든 페이지에서 import 없음 확인 완료(01_research.md FE-16). 안전하게 삭제 가능.

---

### Step 12: DOC backend-rules.md SM 접두사 등록

**파일:** `docs/backend-rules.md`

Before (ID 자동생성 패턴 테이블):
```md
| 도메인 | 접두사 | 예시 |
|---|---|---|
| 과금기준 (BillStd) | `BS` | `BS20240315143022123` |
| (신규 도메인 추가 시 여기에 등록) | | |
```

After:
```md
| 도메인 | 접두사 | 예시 |
|---|---|---|
| 과금기준 (BillStd) | `BS` | `BS20240315143022123` |
| 대표가입 (SubscriptionMain) | `SM` | `SM20240315143022123` |
| (신규 도메인 추가 시 여기에 등록) | | |
```

---

## 5. 트레이드오프 기록

| 결정 | 채택 방향 | 대안 | 이유 |
|---|---|---|---|
| StudyLog PK 타입 | Long 유지 | Long→String 타임스탬프 변환 | DB 마이그레이션 위험 제거. 요구사항 명시적 제외. |
| GlobalExceptionHandler message 키 | 유지 (`"message"`) | `"reason"` 등 표준 키 사용 | Frontend 에러 파싱 코드 무수정 유지. |
| searchError 처리 방식 | computed + errorMsg Toast | searchError state 유지 | 표준 변수명(`errorMsg`) 단일화. 동시에 두 에러가 표시되는 UX 혼선 제거. |
| StudyLogPage 초기 로드 실패 | Toast(errorMsg) | ErrorMessage 컴포넌트 유지 | 재시도 버튼이 필요한 복잡한 오류 화면이 아닌 단순 목록. Toast로도 충분. |
| SubscriptionSearchPopup Hook 위치 | `hooks/useSubscriptionSearch.js` 신설 | `useSubscription.js`에 메서드 추가 | 팝업 전용 검색 로직을 기존 Hook에 혼재시키지 않음. 단일 책임 원칙. |
| UserPage Hook 위치 | `hooks/useUser.js` 신설 | `authApi` 직접 호출 유지 | Frontend 규칙 "컴포넌트는 UI 렌더링 전담, API 호출은 Hook 경유" 준수. |
| LoginPage h-10 | 변경 제외 | h-8로 통일 | 요구사항 명시적 제외. 로그인 페이지는 MainLayout 미사용 예외 영역. |

---

## 6. 테스트 계획

테스트 자동화 환경이 없으므로(기존 테스트 커버리지 0%) 수동 확인 체크리스트로 대체.

### 6.1 Backend 수동 확인

| 항목 | 확인 방법 |
|---|---|
| `ResponseStatusException` 표준 응답 | 존재하지 않는 학습로그 PUT/DELETE 요청 → 응답 JSON에 `errors`, `message` 키 확인 |
| `MethodArgumentNotValidException` 응답 포맷 | content 없이 POST /api/logs → `{"errors":[...], "message":"..."}` 형식 확인 |
| StudyLog CRUD | GET/POST/PUT/DELETE /api/logs 전체 흐름 확인 |
| StudyLog Entity @Table | DB 콘솔에서 `tb_study_log` 테이블명 및 시스템 필드 4개 존재 확인 |
| AuthController `/api/auth/users` | GET 요청 → 사용자 목록 정상 반환 확인 |
| `/hello` 엔드포인트 제거 | GET /hello → 404 확인 |

### 6.2 Frontend 수동 확인

| 항목 | 확인 방법 |
|---|---|
| StudyLogPage Toast | 등록/수정/삭제 성공 시 상단 Toast 표시 및 3초 후 소멸 확인 |
| StudyLogPage 에러 Toast | 네트워크 오류 상황(서버 중지) 시 errorMsg Toast 표시 확인 |
| 컴포넌트 경로 이동 | StudyLogPage 정상 렌더링 확인 |
| SubscriptionPage 삭제 ConfirmDialog | 삭제 버튼 클릭 → ConfirmDialog 표시 → 확인/취소 동작 확인 |
| BillStdPage 삭제 ConfirmDialog | 동일 |
| SubscriptionPage clearMessages | URL에 subsId 파라미터로 진입 시 기존 메시지 초기화 확인 |
| SubscriptionMainPage searchError → Toast | 조회조건 미입력/1자 입력 시 errorMsg Toast 표시 확인 |
| useSubscriptionSearch | SubscriptionMainPage에서 팝업 열기 → 가입ID 검색 → 결과 클릭 → mainSubsId 필드 반영 확인 |
| useUser | UserPage 사용자 목록 로드 + 신규 등록 성공/실패 Toast 확인 |
| h-8 적용 | 개발자 도구에서 대상 input/button 요소 height: 32px 확인 |
| 미사용 파일 삭제 후 | 앱 전체 콘솔 import 오류 없음 확인 |

---

## 7. 롤백 방안

### 7.1 Git 기반 롤백

모든 변경은 Git으로 관리되므로 단계별 커밋 후 `git revert` 또는 `git reset` 가능.

**권장 커밋 단위:**
```
Step 1-4 (BE): "refactor: backend 표준화 - StudyLog Service 신설, ExceptionHandler 확장"
Step 5   (FE): "refactor: StudyLog 컴포넌트 디렉토리 이동 및 Hook 표준화"
Step 6   (FE): "refactor: useSubscriptionSearch, useUser Hook 신설"
Step 7-9 (FE): "refactor: ConfirmDialog 적용 및 searchError 표준화"
Step 10-11 (FE): "style: 입력/버튼 높이 h-8 통일, 미사용 컴포넌트 삭제"
Step 12 (DOC): "docs: backend-rules.md SM 접두사 등록"
```

### 7.2 DB 롤백 (StudyLog Entity 변경)

`spring.jpa.hibernate.ddl-auto=update` 환경:
- 신규 컬럼(`created_by`, `created_dt`, `updated_by`, `updated_dt`)이 추가됨.
- `@Table` 변경으로 `study_log` → `tb_study_log` 테이블이 신규 생성됨
  (기존 `study_log` 테이블은 유지됨).
- 롤백 시: `DROP TABLE tb_study_log;`로 신규 테이블 삭제, 기존 `study_log` 테이블 유지.

`spring.jpa.hibernate.ddl-auto=create` 환경:
- 매 기동 시 재생성이므로 기존 데이터 보존 이슈 없음.

**주의:** 운영 환경(ddl-auto=validate)에서는 수동 DDL 실행 필요:
```sql
CREATE TABLE tb_study_log (
  id          BIGINT       NOT NULL AUTO_INCREMENT,
  content     VARCHAR(255),
  date        DATE,
  created_by  VARCHAR(50)  NOT NULL,
  created_dt  DATETIME     NOT NULL DEFAULT NOW(),
  updated_by  VARCHAR(50),
  updated_dt  DATETIME,
  PRIMARY KEY (id)
);
-- 기존 study_log 데이터 마이그레이션 필요 시:
INSERT INTO tb_study_log (id, content, date, created_by, created_dt)
SELECT id, content, date, 'SYSTEM', NOW() FROM study_log;
```

### 7.3 GlobalExceptionHandler 롤백

`ResponseStatusException` 핸들러 메서드를 제거하면 즉시 롤백.
Frontend는 `message` 키를 읽는 방식이 Spring 기본 응답과도 호환(우연히 동작)하므로
Frontend 코드 수정 없이 복구 가능.
