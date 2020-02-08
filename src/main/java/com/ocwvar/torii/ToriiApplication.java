package com.ocwvar.torii;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@MapperScan(value = "com.ocwvar.torii.db.dao")
@SpringBootApplication
public class ToriiApplication {

	public static void main( String[] args ) {
		SpringApplication.run( ToriiApplication.class, args );
	}

}
