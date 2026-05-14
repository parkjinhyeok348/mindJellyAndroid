package com.mindJellyProject.mindjelly.common;

import android.content.Context;

import com.mindJellyProject.mindjelly.R;

/**
 * @author : Phase2
 * @className : com.mindJellyProject.mindjelly.common
 * @description : API 에러 → 한국어 사용자 친화적 메시지 변환 유틸 (QUAL-02)
 *                HTTP 상태 코드별 분기: 401, 404, 5xx, 네트워크 실패, 기타
 * @date : 2026-05-14
 *
 * Threat T-02A-02 (accept): getError() 내부 메시지는 Toast로만 표시,
 * 백엔드 스택트레이스는 포함되지 않음.
 */
public class ErrorMessageUtil {

    private ErrorMessageUtil() {
        // 유틸 클래스 — 인스턴스화 불필요
    }

    /**
     * Resource 래퍼에서 한국어 에러 메시지를 반환한다.
     *
     * @param resource API 응답 래퍼
     * @param context  R.string 접근용 Context
     * @return 성공이면 null, 에러이면 한국어 메시지 문자열
     */
    public static String getKoreanMessage(Resource<?> resource, Context context) {
        if (resource == null) {
            return context.getString(R.string.error_generic);
        }
        if (resource.isSuccess()) {
            return null;
        }
        return getKoreanMessage(resource.getError(), context);
    }

    /**
     * 에러 메시지 문자열에서 한국어 메시지를 반환한다.
     * Activity의 onFailure / error 경로에서 직접 errorMsg를 넘길 때 사용.
     *
     * 분기 로직:
     * - null 또는 네트워크 키워드 포함 → error_network
     * - "401" 포함 → error_auth
     * - "404" 포함 → error_not_found
     * - "5xx" (500~599) 포함 → error_server
     * - 나머지 → error_generic
     *
     * @param errorMsg Resource.getError() 또는 Throwable.getMessage() 값
     * @param context  R.string 접근용 Context
     * @return 한국어 에러 메시지 문자열
     */
    public static String getKoreanMessage(String errorMsg, Context context) {
        if (errorMsg == null || isNetworkError(errorMsg)) {
            return context.getString(R.string.error_network);
        }
        if (errorMsg.contains("401")) {
            return context.getString(R.string.error_auth);
        }
        if (errorMsg.contains("404")) {
            return context.getString(R.string.error_not_found);
        }
        if (isServerError(errorMsg)) {
            return context.getString(R.string.error_server);
        }
        return context.getString(R.string.error_generic);
    }

    /**
     * 네트워크 실패 키워드 검사.
     * Throwable.getMessage()가 포함할 수 있는 대표 패턴들을 체크한다.
     */
    private static boolean isNetworkError(String msg) {
        String lower = msg.toLowerCase();
        return lower.contains("unknownhostexception")
                || lower.contains("timeout")
                || lower.contains("unable to resolve")
                || lower.contains("no address")
                || lower.contains("sockettimeoutexception")
                || lower.contains("failed to connect")
                || lower.contains("network");
    }

    /**
     * 5xx 서버 에러 코드 검사.
     * "Error: " prefix 이후에 500~599 범위 숫자가 포함되어 있는지 확인한다.
     */
    private static boolean isServerError(String msg) {
        if (msg.contains("5")) {
            // 500~599 패턴: "5" + 두 자리 숫자
            for (int code = 500; code <= 599; code++) {
                if (msg.contains(String.valueOf(code))) {
                    return true;
                }
            }
        }
        return false;
    }
}
