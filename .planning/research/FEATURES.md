# Features Research — MindJelly Android
<!-- researched: 2026-05-12 | domain: Android 감정 일기 앱 | confidence: MEDIUM -->

## Table Stakes (Must Have)

| Feature | Why Expected | Complexity | MindJelly Status | Notes |
|---------|-------------|------------|-----------------|-------|
| 감정 선택 UI | 모든 경쟁 앱의 핵심 루프 | Low | 존재 (BasicEmoAdapter, 3열 그리드) | 아이콘만 있고 라벨 없음 |
| 선택 시 감정 이름 표시 | 사용자가 무엇을 선택하는지 알아야 함 | Low | **누락** — item_basic_emo.xml에 TextView 없음 | 아이콘 아래 TextView 추가 필요 |
| 자유 텍스트 일기 입력 | 감정에 맥락을 부여하는 핵심 기능 | Low | **누락** — TodayJellyActivity에 EditText 없음 | 핵심 루프 미완성 |
| 저장/제출 + 확인 피드백 | 저장됐다는 피드백 없으면 사용자 불안 | Low | **누락** — 저장 버튼, 성공 상태 없음 | 저장 버튼 + 성공 UI 추가 필요 |
| 날짜별 기록 목록 | 과거 기록 조회 기능 | Low | 존재 (JellyDrawerActivity) | 일기 텍스트, 감정 이름 미표시 |
| 기록당 날짜 표시 | 날짜 없는 히스토리는 의미 없음 | Low | DTO에 createDate 있음; 레이아웃 미노출 | item_jelly_drawer.xml에 날짜 표시 추가 |
| 히스토리에 감정 아이콘 | 선택한 감정 상기 | Low | **데이터 갭** — JellyDrawerResDTO에 jellyCombId만 있음 | emo1Icon, emo2Icon 필드 추가 필요 |
| 로그인 후 세션 유지 | 재설치해도 기록 보존 | Medium | Auth 화면 존재; JWT 헤더 주입 미구현 | **전체 앱의 블로커** |
| 비밀번호 찾기 | 기본 인증 기대값 | Low | FindPasswordActivity 존재 | 서버 구현 필요 |
| 로딩 상태 표시 | 멈춘 것처럼 느껴지면 이탈 | Low | **누락** — Resource<T>는 있지만 로딩 UI 없음 | 모든 네트워크 요청에 인디케이터 필요 |
| 빈 상태(Empty State) | 첫 사용자에게 안내 필요 | Low | **누락** | JellyDrawer, Museum에 빈 화면 안내 추가 |
| 사용자 친화적 에러 메시지 | 원시 API 에러 문자열은 낯설음 | Low | Raw string 노출 중 | 한국어 에러 메시지로 교체 |

## Differentiators (핵심 차별화)

| Feature | 가치 | Complexity | Dependency | Notes |
|---------|------|------------|------------|-------|
| 2개 감정 동시 선택 | "불안하면서도 설레는" 복합 감정 표현 가능 | Low | BasicEmo list API | max-2 제약 BasicEmoAdapter에 이미 구현 |
| 실시간 조합 프리뷰 | 선택하는 순간 합성 젤리 미리보기 — 마법 같은 느낌 | Low | JellyComb API | TodayJellyActivity에 이미 구현; 완성도 높이면 됨 |
| 7일 숙성 메타포 | 지연된 만족감: "지난 주 감정을 지금 다시 느끼면?" | Medium | AgedEmo domain | 핵심 콘셉트; 카운트다운 UI가 기대감을 만들어야 함 |
| 숙성 카운트다운 | 7일 대기가 마찰이 아닌 기대감이 되게 | Low | AgedEmo createDate | AgingRoomActivity 빈 껍데기 상태 |
| 합성 감정 공개 순간 | 숙성 완료 시 애니메이션 reveal — 감정 의식화 | Medium | AgedEmo + AgedEmoImage | v1은 단순 scale-in 애니메이션으로 충분 |
| 뮤지엄 컬렉션 뷰 | 숙성된 젤리들이 감정 성장 갤러리가 됨 | Medium | AgedEmoMuseumResDTO | 리스트가 아닌 그리드 레이아웃 — 뮤지엄 느낌 |
| 숙성 후 성찰 일기 | 공개 후 두 번째 일기 작성 — 감정 성장 완성 | Medium | AgedEmo save flow | PROJECT.md에 명시; 전/후 감정 아크 완성 |
| 감정 조합 테이블 | 2감정 → 1숙성 감정 매핑이 발견의 즐거움 생성 | High | Server JellyCombination table | 매핑 품질이 앱의 핵심 가치 결정 |

## Anti-Features (의도적으로 만들지 말 것)

| Anti-Feature | 이유 | 대신 할 것 |
|-------------|------|----------|
| 소셜 공유 / 공개 피드 | 솔직한 감정 기록 방해; 개인 공간 약속 깨짐 | 완전 개인 일기 유지 |
| 연속 기록 스트릭 카운터 | 스트릭 불안이 차분한 일기 쓰기 방해 | 스트릭 없음; 강요 없는 UI |
| 적극적 푸시 알림 | 무드 앱 최대 삭제 원인 | v1 없음 (올바름); v2에서 선택적 opt-in |
| 통계/차트 (v1) | 4주 미만 데이터로는 의미 없음; 빈 차트가 신규 사용자 실망 | 30+ 엔트리 이후로 미룸 |
| AI 생성 글쓰기 프롬프트 | 감정 조합 메타포 자체가 프롬프트 역할 | 조합 결과가 충분한 동기 부여 |
| 감정 목록 커스터마이즈 | 조합 테이블 깨짐; 복잡도 폭발 | 서버 관리 목록; v1에서 커스텀 불가 |
| 과거 기록 수정/삭제 | 진정성 있는 기록 무결성 침해 | 불변 엔트리; 온보딩에서 안내 |
| 다크모드 (v1) | 1인 개발에서 비용 大; 젤리 팔레트 단일 테마 | 단일 테마; 다크모드는 v1 이후 |
| 온보딩 튜토리얼 캐러셀 | 사용자가 건너뜀; 첫 기록 전 이탈률 높음 | 빈 화면 안내 + 인라인 힌트 |

## Feature Dependency Chain

```
BasicEmo 목록 API
  └── 감정 선택 그리드 (TodayJellyActivity)
        └── 2개 선택 제한 (BasicEmoAdapter)
              └── 실시간 조합 프리뷰 (JellyComb API)
                    └── 일기 텍스트 입력 (EditText — 레이아웃 누락)
                          └── 저장 (Jelly save API)
                                └── 젤리서랍 히스토리 목록
                                      └── "숙성시키기" 버튼 → 에이징룸
                                            └── 7일 카운트다운 (AgedEmo createDate)
                                                  └── 7일 경과 후 자동 뮤지엄 이동
                                                        └── 뮤지엄 컬렉션 그리드
                                                              └── 숙성 후 성찰 일기

Auth (로그인 + JWT Retrofit 헤더 주입)
  └── 위 모든 것 (userId가 모든 API 요청 게이트)
      └── JellyDrawerActivity의 userId = 1L 하드코딩 교체 필수
```

## MVP 구현 우선순위

1. **Auth 토큰 Retrofit 헤더 주입** — 이것 없이는 모든 게 깨짐
2. **감정 선택 그리드에 이름 표시** — 아이콘만으론 신규 사용자 사용 불가
3. **TodayJellyActivity에 일기 EditText 추가** — 핵심 루프 미완성
4. **저장 버튼 + 성공 피드백** — 기록 제출 불가
5. **모든 네트워크 요청에 로딩 인디케이터** — 앱이 멈춘 것처럼 느껴짐
6. **JellyDrawer에 감정 아이콘 + 날짜 + 숙성 상태 표시** — DTO 데이터 갭 수정 필요
7. **AgingRoom 카운트다운 표시** — 기대감 메커니즘
8. **Museum 컬렉션 그리드** — 숙성 완료 페이오프

**v2로 미룸:** 숙성 후 일기, 통계/차트, 푸시 알림, 다크모드

## 데이터 모델 갭

| 갭 | 파일 | 영향 | 수정 방법 |
|----|------|------|---------|
| JellyDrawerResDTO 감정 필드 누락 | `jellyDomain/jelly/model/JellyDrawerResDTO.java` | 히스토리에 감정 정보 없음 | emo1Name, emo1Icon, emo2Name, emo2Icon 추가 |
| item_basic_emo.xml 라벨 없음 | `res/layout/item_basic_emo.xml` | 아이콘에 이름 없음 | emoName 바인딩 TextView 추가 |
| TodayJellyActivity 일기 EditText 없음 | `basicEmo/view/TodayJellyActivity.java` | 일기 입력 불가 | EditText + 저장 버튼 레이아웃 추가 |
| userId 하드코딩 1L | `jellyDomain/jelly/view/JellyDrawerActivity.java` | 모든 사용자가 같은 데이터 | SharedPreferences/auth 토큰에서 읽기 |
| AgingRoomActivity 빈 껍데기 | `agedEmoDomain/agedEmo/view/AgingRoomActivity.java` | 숙성 UX 없음 | 카운트다운 리스트 구현 |
| jellyMuseumActivity 빈 껍데기 | `agedEmoDomain/agedEmo/view/jellyMuseumActivity.java` | 뮤지엄 UX 없음 | 컬렉션 그리드 구현 |
