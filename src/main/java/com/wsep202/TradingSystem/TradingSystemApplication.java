package com.wsep202.TradingSystem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TradingSystemApplication {

	private static final Logger logger = LoggerFactory.getLogger(TradingSystemApplication.class);
	public static void main(String[] args) {
		SpringApplication.run(TradingSystemApplication.class, args);
		/*logger.info("this is a info message");
		logger.warn("this is a warn message");
		logger.error("this is a error message");*/
	}

}
