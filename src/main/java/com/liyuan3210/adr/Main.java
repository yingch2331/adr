package com.liyuan3210.adr;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(
exclude = {
DataSourceAutoConfiguration.class
},
scanBasePackages = "com.liyuan3210.adr"
)
public class Main {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(Main.class);

	public static void main(String[] args) {
		LOGGER.info("正在启动中......");
		SpringApplication.run(Main.class, args);
	}
	
}
