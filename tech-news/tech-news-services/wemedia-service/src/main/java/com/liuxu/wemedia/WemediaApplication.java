package com.liuxu.wemedia;

import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@MapperScan("com.liuxu.wemedia.mapper")
@EnableFeignClients(basePackages = "com.liuxu.feigns")
public class WemediaApplication {
    public static void main(String[] args) {
        SpringApplication.run(WemediaApplication.class, args);
    }

    @Bean
    PaginationInnerInterceptor PaginationInnerInterceptor() {
        return new PaginationInnerInterceptor();
    }
}
