package com.lwb.yupao.common;

import com.lwb.yupao.utils.ResultUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * * 全局异常处理 利用Springboot提供的AOP(切面)，在调用方法前后进行额外处理
 * * 集中记录日志进行处理
 * @author 路文斌
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(BusinessesException.class)
    public BaseResult<Object> businessExceptionHandler(BusinessesException e) {
        log.error("业务异常:"+ e.getMessage(),e);
        return ResultUtil.error(e.getCode(),e.getMessage());

    }
    @ExceptionHandler(RuntimeException.class)
    public BaseResult<Object> businessExceptionHandler(RuntimeException e){
        log.error("运行时异常:",e);
        return ResultUtil.error(ErrorCode.SYSTEM_ERROR.getCode(),e.getMessage());
    }
}
