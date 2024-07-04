package com.lwb.yupao;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = "com.lwb.yupao.mapper")
public class YupaoBackApplication {

    public static void main(String[] args) {
        SpringApplication.run(YupaoBackApplication.class, args);
        System.out.println("启动成功！！！");
    }
}
