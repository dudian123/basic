package org.example.runner;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * 系统初始化运行器
 * 
 * @author System
 */
@Slf4j
@Component("systemInitRunner")
public class SystemInitRunner implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("系统初始化完成");
    }
}