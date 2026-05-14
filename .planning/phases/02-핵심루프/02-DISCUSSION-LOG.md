# Phase 2: 핵심 루프 - Discussion Log

**Session:** 2026-05-14
**Facilitator:** Claude (gsd-discuss-phase)
**Participant:** Jinhyeok

---

## Gray Areas Presented

5개 식별됨:
1. 백엔드 API 준비 상황 — JellyDrawerResDTO 감정 필드, startAging API 계약
2. 합성 젤리 프리뷰 — 트리거 타이밍, 표시 내용, API 연동 방식
3. 감정 선택 UX — 선택/해제 인터랙션
4. 로딩/에러 UX — QUAL-01/02 구현 방식
5. 빈 상태 UX — DRAW-04 안내 내용

사용자 선택: **1, 2번** (백엔드 API 준비 상황 + 합성 젤리 프리뷰)

---

## Discussion 1: 백엔드 API 준비 상황

**Q: JellyDrawerResDTO 감정 필드가 준비되었나요?**
A: 예, 이미 준비됨

**Q: 감정 아이콘 형식 (URL vs drawable)?**
A: 이미지 URL (http://...) → Glide로 로딩

**Q: startAging API 계약?**
A: `PATCH /api/jelly/{id}`, status 필드 업데이트

**결정:**
- JellyDrawerResDTO 감정 필드 즉시 추가 가능 (백엔드 대기 불필요)
- 아이콘 = 이미지 URL → Glide
- startAging = PATCH /api/jelly/{id}

---

## Discussion 2: 합성 젤리 프리뷰

**Q: 프리뷰 트리거 타이밍?**
A: 2개 모두 선택하면 즉시 API 호출

**Q: 프리뷰 표시 내용?**
A: 처음 "합성 AgedEmo 이름 + 이미지" → 재확인 후 "이미지만"

**Q: isAwaken 파라미터?**
A: false 고정

**Q: 응답(String)을 어떻게 사용?**
A: 이미지 URL로 바로 사용 (Glide)

**결정:**
- 트리거: 2개 선택 즉시 `GET /jellyComb/jelly-icon/{firstEmo}/{secondEmo}/false`
- 응답 String = 이미지 URL → Glide 표시
- 내용: 이미지만 (이름 없음, MVP 결정)

---

## Deferred (not discussed)

- 3번 (감정 선택 UX): Claude 재량 — 기존 BasicEmoAdapter 패턴 유지
- 4번 (로딩/에러 UX): Claude 재량 — Resource<T> 확장 또는 별도 LiveData<Boolean>
- 5번 (빈 상태 UX): Claude 재량 — 표준 빈 상태 TextView

---

*Completed: 2026-05-14*
