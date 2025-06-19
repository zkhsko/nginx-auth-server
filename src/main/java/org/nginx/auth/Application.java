package org.nginx.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

@SpringBootApplication
//@EnableAutoConfiguration(exclude = {SecurityAutoConfiguration.class, ReactiveSecurityAutoConfiguration.class})
@EnableAutoConfiguration(exclude = {UserDetailsServiceAutoConfiguration.class})
//@MapperScan("xyz.sketcherly.harbor.pay.*.repository")
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
