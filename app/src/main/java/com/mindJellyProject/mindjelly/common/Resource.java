package com.mindJellyProject.mindjelly.common;

/**
 * @author : Jinhyeok
 * @className : com.mindJellyProject.mindjelly.common
 * @description : API 응답 상태를 래핑하는 클래스
 * @modification : 2025-01-03(KTDS) 수정
 * @date : 2025-01-03
 */
public class Resource<T> {
    private T data;
    private String error;

    // 기존 생성자 유지 (호환성)
    public Resource(T data) {
        this.data = data;
    }

    // String 타입의 데이터와 에러 메시지의 충돌을 방지하기 위한 생성자
    private Resource(T data, String error) {
        this.data = data;
        this.error = error;
    }

    // 정적 팩토리 메서드: 성공
    public static <T> Resource<T> success(T data) {
        return new Resource<>(data, null);
    }

    // 정적 팩토리 메서드: 실패
    public static <T> Resource<T> error(String error) {
        return new Resource<>(null, error);
    }

    public T getData() {
        return data;
    }

    public String getError() {
        return error;
    }

    public boolean isSuccess() {
        return data != null;
    }

    public boolean isError() {
        return error != null;
    }
}
