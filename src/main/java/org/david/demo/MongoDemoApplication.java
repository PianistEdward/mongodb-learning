package org.david.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//@EnableTransactionManagement
@SpringBootApplication
public class MongoDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(MongoDemoApplication.class, args);
		System.out.println("Hello World");
	}

}
