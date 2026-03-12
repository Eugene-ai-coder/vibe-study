---
name: server
description: 프로젝트 서버(백엔드/프론트엔드) 시작·종료·상태 확인을 안정적으로 수행하는 스킬. Use when the user invokes /server, or says "서버 시작", "서버 종료", "서버 상태".
---

너는 이 프로젝트의 서버 라이프사이클을 관리하는 운영자다.
플랫폼은 Windows (bash 셸 사용). 프로젝트 루트에서 실행한다.

# 서버 구성

| 구분 | 기술 | 포트 | 실행 명령 |
|------|------|------|-----------|
| MySQL | MySQL 8.4 | 3306 | `/c/mysql/bin/mysqld.exe --console` |
| 백엔드 | Spring Boot 3.4 + MySQL | 8080 | `./mvnw.cmd spring-boot:run` |
| 프론트엔드 | Vue 3 + Vite | 5173 | `cd frontend-vue && npm run dev` |

- 프론트엔드 `/api` 요청은 Vite 프록시로 백엔드(8080)에 전달
- MySQL DB: `vibedb` / `vibeuser` / `vibe1234`

# 명령어

사용자 입력을 파싱하여 아래 중 하나를 실행하라:

## `/server start` (기본값)
아래 순서를 **반드시** 따른다:

### 1단계: 사전 점검
아래 항목을 **병렬로** 확인하고, 문제가 있으면 해결한 뒤 다음으로 진행:

| # | 점검 항목 | 확인 방법 | 문제 시 조치 |
|---|----------|-----------|-------------|
| 1 | Java 17+ 설치 | `java -version` | 에러 보고 후 중단 |
| 2 | MySQL 실행 여부 | `netstat -ano \| findstr :3306` | 미실행 시 `/c/mysql/bin/mysqld.exe --console` 백그라운드 시작 |
| 3 | 포트 8080 사용 여부 | `netstat -ano \| findstr :8080` | PID 표시 후 kill 여부 확인 |
| 4 | 포트 5173 사용 여부 | `netstat -ano \| findstr :5173` | PID 표시 후 kill 여부 확인 |
| 5 | node_modules 존재 | `frontend-vue/node_modules/` | 없으면 `cd frontend-vue && npm install` 실행 |

### 2단계: 백엔드 시작
```
./mvnw.cmd spring-boot:run
```
- `run_in_background: true`로 실행
- 시작 후 최대 30초간 `curl -s http://localhost:8080/api` 또는 유사 엔드포인트로 헬스체크
- 응답이 오면 "백엔드 시작 완료" 보고
- 30초 초과 시 로그 마지막 20줄을 읽어 에러 원인 분석 후 보고

### 3단계: 프론트엔드 시작
```
cd frontend-vue && npm run dev
```
- `run_in_background: true`로 실행
- "프론트엔드 시작 완료 (http://localhost:5173)" 보고

### 4단계: 최종 상태 보고
```
서버 상태:
  MySQL:     localhost:3306 ✅
  백엔드:    http://localhost:8080 ✅
  프론트엔드: http://localhost:5173 ✅
```

## `/server stop`
1. 포트 8080 사용 프로세스 찾아 종료: `netstat -ano | findstr :8080` → `taskkill /PID {pid} /F`
2. 포트 5173 사용 프로세스 찾아 종료: `netstat -ano | findstr :5173` → `taskkill /PID {pid} /F`
3. MySQL(3306) 종료 여부는 사용자에게 확인 후 결정
4. 종료 상태 보고

## `/server restart`
`/server stop` 실행 후 `/server start` 실행.

## `/server status`
1. 포트 8080, 5173 사용 여부 확인
2. 상태 보고 (실행 중 / 중지)

# 트러블슈팅 로그

실행 중 새로운 실패 패턴을 만나면 이 스킬과 같은 폴더의 `troubleshooting.md`에 아래 형식으로 누적 기록하라:

```markdown
## {날짜} — {에러 요약}
- **증상:** {에러 메시지 또는 현상}
- **원인:** {분석된 원인}
- **해결:** {적용한 조치}
```

다음 실행 시 사전 점검 단계에서 `troubleshooting.md`가 존재하면 읽어서, 기록된 실패 패턴에 대한 선제 점검을 추가로 수행하라.

# 규칙
- 서버 프로세스 kill은 반드시 사용자 확인 후 실행
- 에러 발생 시 로그를 분석하여 원인을 보고하고, 동일한 명령을 3회 이상 재시도하지 마라
- 대화창에 긴 로그를 출력하지 마라. 핵심 에러 라인만 발췌하라
