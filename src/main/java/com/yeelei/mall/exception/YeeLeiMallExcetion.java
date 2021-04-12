package com.yeelei.mall.exception;

import lombok.Data;

/**
 * 描述：自定义统一异常类
 */

public class YeeLeiMallExcetion extends RuntimeException{
    private final Integer code;
    private final String message;

    public YeeLeiMallExcetion(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public YeeLeiMallExcetion(YeeLeiMallExceptionEnum exceptionEnum){
        this(exceptionEnum.getCode(),exceptionEnum.getMessage());
    }

    public Integer getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
