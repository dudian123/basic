package org.example;


import cn.hutool.captcha.CircleCaptcha;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;


@SpringBootApplication
@MapperScan("org.example.mapper")
@ComponentScan(basePackages = {"org.example"})
@EnableAspectJAutoProxy
public class Main {
    
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}