package com.wangshangxuankexitong;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.wangshangxuankexitong.dao") // 扫描DAO接口
public class WangshangxuankexitongApplication {
    public static void main(String[] args) {
        SpringApplication.run(WangshangxuankexitongApplication.class, args);
    }
}