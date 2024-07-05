package com.lwb.yupao.utils;

import com.lwb.yupao.common.BaseResult;
import com.lwb.yupao.common.ErrorCode;

/**
 * 处理返回工具类
 * @author 路文斌
 */
public class ResultUtil {
    public static <T> BaseResult<T> success(T data) {
        return new BaseResult<>(20000, "请求成功", data);
    }

    public static <T> BaseResult<T> error(T data) {
        return new BaseResult<>(50000, "请求失败", data);
    }

    public static <T> BaseResult<T> error(ErrorCode errorCode) {
        return new BaseResult<>(errorCode);
    }

    public static <T> BaseResult<T> error(int code, String message) {
        return new BaseResult<>(code, message);
    }
}
