package com.liuxu.common.dtos;

import com.liuxu.common.enums.AppHttpCodeEnum;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 通用的结果返回类
 * 
 * @param <T> 数据类型泛型参数
 */
@Data
@ToString
public class ResponseResult<T> implements Serializable {
    private Integer code = 200; // 状态码
    private String errorMessage; // 提示信息
    private T data; // 数据 Object

    public ResponseResult() {
    }

    public ResponseResult(Integer code, T data) {
        this.code = code;
        this.data = data;
    }

    public ResponseResult(Integer code, String msg, T data) {
        this.code = code;
        this.errorMessage = msg;
        this.data = data;
    }

    public ResponseResult(Integer code, String msg) {
        this.code = code;
        this.errorMessage = msg;
    }

    public static <T> ResponseResult<T> errorResult(int code, String msg) {
        ResponseResult<T> result = new ResponseResult<>();
        return result.error(code, msg);
    }

    public static <T> ResponseResult<T> successResult(int code, String msg) {
        ResponseResult<T> result = new ResponseResult<>();
        return result.success(code, null, msg);
    }

    public static <T> ResponseResult<T> successResult(T data) {
        ResponseResult<T> result = setAppHttpCodeEnum(AppHttpCodeEnum.SUCCESS,
                AppHttpCodeEnum.SUCCESS.getErrorMessage());
        if (data != null) {
            result.setData(data);
        }
        return result;
    }

    public static <T> ResponseResult<T> successResult() {
        return successResult(null);
    }

    public static <T> ResponseResult<T> errorResult(AppHttpCodeEnum enums) {
        return setAppHttpCodeEnum(enums, enums.getErrorMessage());
    }

    public static <T> ResponseResult<T> errorResult(AppHttpCodeEnum enums, String errorMessage) {
        return setAppHttpCodeEnum(enums, errorMessage);
    }

    public static <T> ResponseResult<T> setAppHttpCodeEnum(AppHttpCodeEnum enums) {
        return successResult(enums.getCode(), enums.getErrorMessage());
    }

    private static <T> ResponseResult<T> setAppHttpCodeEnum(AppHttpCodeEnum enums, String errorMessage) {
        return successResult(enums.getCode(), errorMessage);
    }

    public ResponseResult<T> error(Integer code, String msg) {
        this.code = code;
        this.errorMessage = msg;
        return this;
    }

    public ResponseResult<T> success(Integer code, T data) {
        this.code = code;
        this.data = data;
        return this;
    }

    public ResponseResult<T> success(Integer code, T data, String msg) {
        this.code = code;
        this.data = data;
        this.errorMessage = msg;
        return this;
    }

    public ResponseResult<T> success(T data) {
        this.data = data;
        return this;
    }

    public boolean checkCode() {
        return this.getCode() != 0;
    }
}
