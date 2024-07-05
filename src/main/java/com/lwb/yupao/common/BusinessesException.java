package com.lwb.yupao.common;

import lombok.Getter;

/**
 * 业务异常处理类--> 继承RuntimeException，相对于java的异常类，支持更多字段,更灵活快捷的处理异常
 * @author 路文斌
 */
@Getter
public class BusinessesException extends RuntimeException{
    private final int code;
    private String msg;
    public BusinessesException(int code, String msg) {
          super(msg);
        this.code = code;
    }

    public BusinessesException(ErrorCode errorCode) {
           super(errorCode.getMsg());
        this.code = errorCode.getCode();
    }

    public BusinessesException(ErrorCode errorCode, String msg) {
        super(msg);
        this.code = errorCode.getCode();
    }
}
