package com.liuxu.common.exception;

import com.liuxu.common.enums.AppHttpCodeEnum;

/**
 * @Description: 抛异常工具类
 * @Version: V1.0
 */
public class CustException {
    public static void throwException(AppHttpCodeEnum codeEnum) {
        throw new CustomException(codeEnum);
    }

    public static void throwException(AppHttpCodeEnum codeEnum, String msg) {
        throw new CustomException(codeEnum, msg);
    }
}