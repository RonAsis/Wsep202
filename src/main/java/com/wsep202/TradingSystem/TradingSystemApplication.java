package com.wsep202.TradingSystem;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class TradingSystemApplication {

	public static void main(String[] args) {
		if(args.length < 2){
			log.error("Must enter admin user and password for start the application");
		}else {
			SpringApplication.run(TradingSystemApplication.class, args);
			log.info("The aaaaaaaaaaaaaaaapplication is starting");
		}
	}
}