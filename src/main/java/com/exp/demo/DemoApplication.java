package com.exp.demo;

import com.exp.demo.config.ScheduledConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({WebMvcConfigration.class, ScheduledConfig.class})
@MapperScan({ "com.exp.demo.dao" })
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

}
