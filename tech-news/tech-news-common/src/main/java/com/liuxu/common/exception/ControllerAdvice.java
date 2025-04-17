package com.liuxu.common.exception;

import com.liuxu.common.dtos.ResponseResult;
import com.liuxu.common.enums.AppHttpCodeEnum;
import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;

/**
 * 全局异常处理
 */
@Slf4j
@Configuration
@RestControllerAdvice
public class ControllerAdvice {

    /**
     * 处理参数校验异常
     *
     * @param ex 参数校验异常
     * @return 统一响应结果
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public <T> ResponseResult<T> handleValidationException(MethodArgumentNotValidException ex) {
        log.error("参数校验异常: {}", Objects.requireNonNull(ex.getBindingResult().getFieldError()).getDefaultMessage());
        return ResponseResult.errorResult(
                AppHttpCodeEnum.PARAM_INVALID,
                ex.getBindingResult().getFieldError().getDefaultMessage());
    }

    /**
     * 处理自定义异常
     *
     * @param ex 自定义异常
     * @return 统一响应结果
     */
    @ExceptionHandler(CustomException.class)
    public <T> ResponseResult<T> handleCustomException(CustomException ex) {
        String errorMessage = ex.getAppHttpCodeEnum().getErrorMessage();
        if (StringUtils.isBlank(errorMessage)) {
            errorMessage = "服务器开小差了，请稍后再试";
        }
        log.error("自定义异常: {}", errorMessage, ex);
        return ResponseResult.errorResult(ex.getAppHttpCodeEnum(), errorMessage);
    }

    /**
     * 处理所有未捕获的异常
     *
     * @param ex 异常基类
     * @return 统一响应结果
     */
    @ExceptionHandler(Exception.class)
    public <T> ResponseResult<T> handleException(Exception ex) {
        log.error("系统异常: {}", ex.getMessage());
        return ResponseResult.errorResult(
                AppHttpCodeEnum.SERVER_ERROR,
                "服务器开小差了，请稍后再试");
    }
}