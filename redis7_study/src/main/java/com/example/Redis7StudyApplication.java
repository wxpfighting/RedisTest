package com.example;

import jakarta.annotation.Resource;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.*;

@SpringBootApplication
public class Redis7StudyApplication {

    public static void main(String[] args) {
        SpringApplication.run(Redis7StudyApplication.class, args);
    }

}
