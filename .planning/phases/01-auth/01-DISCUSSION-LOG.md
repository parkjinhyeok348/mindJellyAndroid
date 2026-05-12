# Phase 1: Auth 기반 - Discussion Log

> **Audit trail only.** Do not use as input to planning, research, or execution agents.
> Decisions are captured in CONTEXT.md — this log preserves the alternatives considered.

**Date:** 2026-05-12
**Phase:** 1-Auth 기반
**Areas discussed:** 이메일 인증 UX

---

## 이메일 인증 UX

### Q1: 회원가입 API 호출 성공 후, 앱은 어떤 화면을 보여줘야 할까요?

| Option | Description | Selected |
|--------|-------------|----------|
| 이메일 안내 후 로그인 화면 이동 | "발송된 이메일을 확인하세요" Toast/다이얼로그 후 LoginActivity로 이동. 인증 완료 전 로그인 시도 시 서버가 에러 내려옴 | ✓ |
| 별도 인증 대기 화면 | "이메일을 확인하세요" 전용 화면 + 인증 완료 후 다음으로 진행 버튼. 별도 Activity 필요함 — 구현 범위 커짐 | |

**User's choice:** 이메일 안내 후 로그인 화면 이동
**Notes:** 가장 간단한 구현. 별도 Activity 불필요.

---

### Q2: 이메일 인증 전에 로그인을 시도하면 서버가 에러를 내려보낼 텍데, 앱은 어떤 메시지를 보여줄까요?

| Option | Description | Selected |
|--------|-------------|----------|
| 이메일 인증이 필요합니다 | 서버 에러응답에서 구별해 명확한 한국어 메시지 표시 | ✓ |
| 일반 로그인 실패 메시지 | "이메일 또는 비밀번호가 틀렸습니다" 일반 에러로만 처리. 인증 미완료인지 비밀번호 오류인지 사용자가 알 수 없음 | |

**User's choice:** "이메일 인증이 필요합니다"
**Notes:** 서버가 인증 미완료 케이스에 대해 구별 가능한 에러 코드/메시지를 내려줘야 함. Researcher가 UserService API 응답 구조 확인 필요.

---

### Q3: ‹이메일 인증이 필요합니다› 메시지 옆에 인증 메일 재발송 기능이 필요한가요?

| Option | Description | Selected |
|--------|-------------|----------|
| 아니요, v1에서는 안내 메시지만 | Toast/다이얼로그로 안내만. 재발송은 v2 검토 | ✓ |
| 네, 로그인 화면에 재발송 버튼 추가 | 로그인 화면에 재발송 링크/버튼 추가. 서버 재발송 API 없으면 추가 협의 필요 | |

**User's choice:** 아니요, v1에서는 안내 메시지만
**Notes:** v1 범위 최소화. 재발송 API 존재 여부 미확인이므로 v2로 이월.

---

## Claude's Discretion

- **Splash 라우팅:** 사용자가 선택하지 않은 영역. 토큰 존재 여부만 체크하는 방식으로 결정. 만료 토큰은 첫 API 실패(401) 시 LoginActivity로 이동.
- **JWT userId 추출:** android.util.Base64로 JWT payload 수동 파싱. 추가 라이브러리 없이 구현.
- **회원가입 필드 범위:** UserSaveReqDTO 기준으로 판단. Researcher가 기존 SignUpActivity와 DTO 확인 후 구현 범위 결정.

## Deferred Ideas

- 로그아웃 (AUTH-04) — v2 이월
- 비밀번호 재설정 (AUTH-05) — v2 이월
- 인증 메일 재발송 — v2 검토
- HTTP → HTTPS 전환 (QUAL-03) — v2 이월
- 에뮬레이터/실기기 BASE_URL 자동 전환 (QUAL-04) — v2 이월
