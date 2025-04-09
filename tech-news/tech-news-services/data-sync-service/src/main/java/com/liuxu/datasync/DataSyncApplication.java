package com.liuxu.datasync;

import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@MapperScan("com.liuxu.datasync.mapper")
public class DataSyncApplication {
    public static void main(String[] args) {
        SpringApplication.run(DataSyncApplication.class, args);
    }

    @Bean
    PaginationInnerInterceptor PaginationInnerInterceptor(){
        return new PaginationInnerInterceptor();
    }
}
