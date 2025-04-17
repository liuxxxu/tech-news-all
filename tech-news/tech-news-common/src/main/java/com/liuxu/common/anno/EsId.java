package com.liuxu.common.anno;

import java.lang.annotation.*;

// 标记此注解的字段 为id字段
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EsId {
}
