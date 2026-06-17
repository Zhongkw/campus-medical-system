package com.campus.medical;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.campus.medical.mapper")
public class CampusMedicalServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(CampusMedicalServerApplication.class, args);
    }

}
