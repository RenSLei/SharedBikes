package com.rsl.bike;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


//是spring的入口程序，在spring程序启动时，会进行扫描，扫描带有特殊注解的类
@SpringBootApplication
public class MyBikeApplication {
	public static void main(String[] args) {
		SpringApplication.run(MyBikeApplication.class, args);
	}
}
