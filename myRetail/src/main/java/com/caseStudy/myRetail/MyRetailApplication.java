package com.caseStudy.myRetail;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"com.caseStudy.myRetail","com.caseStudy.myRetail.controllers", "com.caseStudy.myRetail.models", "com.caseStudy.myRetail.repositories"})
public class MyRetailApplication {

	public static void main(String[] args) {
		SpringApplication.run(MyRetailApplication.class, args);
	}
}
