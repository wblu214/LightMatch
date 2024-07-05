package com.lwb.yupao.common;

import lombok.Getter;

/**
 * 错误码
 * @author 路文斌
 */
@Getter
public enum ErrorCode {
    SUCCESS(20000, "请求成功"),
    PARAMS_ERROR(40000, "请求参数错误"),
    NULL_ERROR(40001, "请求参数为空"),
    NOT_FOUND(40400, "请求资源不存在"),
    FORBIDDEN(40300, "没有权限访问"),
    SYSTEM_ERROR(50000, "系统内部错误"),
    USER_NOT_LOGIN(40100, "用户未登录"),
    USER_NOT_EXIST(40101, "用户不存在"),
    ;
    private final int code;
    private final String msg;
    ErrorCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

}
