package com.yeelei.mall.common;

import com.yeelei.mall.exception.YeeLeiMallExceptionEnum;
import lombok.Data;

/**
 * 描述: 通用返回对象
 */
@Data
public class ApiRestResponse<T> {
    private Integer status;
    private String message;
    private T data;

    private static final int OK_CODE = 10000;
    private static final String OK_MESSAGE = "success";

    public ApiRestResponse() {
        this(OK_CODE, OK_MESSAGE);
    }

    public ApiRestResponse(Integer status, String message) {
        this.status = status;
        this.message = message;
    }

    public static <T> ApiRestResponse<T> success() {
        return new ApiRestResponse<>();
    }

    public static <T> ApiRestResponse<T> success(T data) {
        ApiRestResponse<T> restResponse = new ApiRestResponse<>();
        restResponse.setData(data);
        return restResponse;
    }

    public static <T> ApiRestResponse<T> error(Integer code, String message) {
        return new ApiRestResponse<>(code, message);
    }

    public static <T> ApiRestResponse<T> error(YeeLeiMallExceptionEnum exceptionEnum) {
        return new ApiRestResponse<>(exceptionEnum.getCode(), exceptionEnum.getMessage());
    }
}
