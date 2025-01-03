package com.mindJellyProject.mindjelly.common;

/**
 * @author : Jinhyeok
 * @className : com.mindJellyProject.mindjelly.common
 * @description :
 * @modification : 2025-01-03(KTDS) 수정
 * @date : 2025-01-03
 * <p>
 * ====개정이력(Modification Information)====
 * 수정일        수정자        수정내용
 * -----------------------------------------
 * 2025-01-03     KTDS        주석 생성
 */
public class Resource<T> {
    private T data;
    private String error;

    public Resource(T data) {
        this.data = data;
    }

    public Resource(String error) {
        this.error = error;
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

