package com.lwb.yupao.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 通用返回类
 * @param <T>
 * @author 路文斌
 */
@Data
public class BaseResult<T> implements Serializable {
    private int code;

    private String msg;

    private Object data;

    public BaseResult(int code, T data) {
        this.code = code;
        this.data = data;
    }

    public BaseResult(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }
    public BaseResult(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public BaseResult(ErrorCode errorCode) {
        this.code = errorCode.getCode();
        this.msg = errorCode.getMsg();
    }
}