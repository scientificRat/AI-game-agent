package com.scientificrat.game.datastruct;

public class JsonResponse<T> {
    private Integer code;
    private T data;

    public JsonResponse(Integer code, T data) {
        this.code = code;
        this.data = data;
    }

    public JsonResponse() {
    }

    public Integer getCode() {
        return code;
    }

    public JsonResponse setCode(Integer code) {
        this.code = code;
        return this;
    }

    public T getData() {
        return data;
    }

    public JsonResponse setData(T data) {
        this.data = data;
        return this;
    }
}
