# Settings Button & Navigation Design

**Date:** 2026-05-19  
**Scope:** MainActivity에 설정 버튼 추가 → SettingActivity 이동 (로그아웃 포함)

---

## Goal

메인 화면 우측 상단에 설정(⚙️) 아이콘 버튼을 추가하고, 클릭 시 기존 SettingActivity로 이동한다.
SettingActivity에는 이미 로그아웃 버튼(`btnLogout`)이 구현되어 있으므로 별도 추가 불필요.

---

## Architecture

변경 없음. 기존 Activity → Activity 네비게이션 패턴 유지.

```
MainActivity → (btn_settings 클릭) → SettingActivity
SettingActivity → (btn_logout 클릭) → LoginActivity (기존 구현)
```

---

## Components

### 1. `res/drawable/ic_settings.xml` (신규)
Material Design 기어 아이콘 벡터 드로어블.

### 2. `res/layout/activity_main.xml` (수정)
기존 `RelativeLayout` 안에 `ImageButton` 추가:
- 위치: `alignParentTop + alignParentEnd`, margin 12dp
- 크기: 48×48dp
- 아이콘: `@drawable/ic_settings`, tint white
- 배경: transparent (배경 이미지 위 자연스럽게 표시)

### 3. `MainActivity.java` (수정)
`btn_settings` ImageButton 참조 후 클릭 리스너에서 `SettingActivity` 시작.

---

## Out of Scope

- SettingActivity 레이아웃 변경 없음
- 로그아웃 로직 변경 없음
- Toolbar/AppBar 도입 없음
