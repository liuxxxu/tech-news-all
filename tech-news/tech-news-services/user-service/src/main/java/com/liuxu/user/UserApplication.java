package com.liuxu.user;

import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@MapperScan("com.liuxu.user.mapper")
@EnableFeignClients(basePackages = "com.liuxu.feigns")
public class UserApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class, args);
    }

    /**
     * 分页
     * @return
     */
    @Bean
    PaginationInnerInterceptor PaginationInnerInterceptor() {
        return new PaginationInnerInterceptor();
    }
}
