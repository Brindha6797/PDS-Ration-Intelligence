package com.wipro.pds;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class PdsApplication {

    public static void main(String[] args) {
        SpringApplication.run(PdsApplication.class, args);
    }
}
