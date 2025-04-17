package com.liuxu.config;

import com.liuxu.common.threadlocal.AppThreadLocalUtils;
import feign.Logger;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("com.liuxu.feigns")
public class FeignAutoConfiguration {
    @Bean
    Logger.Level level() {
        return Logger.Level.FULL;
    }

    @Bean
    public RequestInterceptor userInfoRequestInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                // 获取登录用户
                Long userId = AppThreadLocalUtils.getUserId();
                if (userId == null || userId == 0L) {
                    return;
                }
                // 如果不为空则放入请求头中，传递给下游微服务
                template.header("userId", userId.toString());
            }
        };
    }
}
