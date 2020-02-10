package com.ocwvar.torii;

import com.ocwvar.torii.data.StaticContainer;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;

@MapperScan( value = "com.ocwvar.torii.db.dao" )
@SpringBootApplication
public class ToriiApplication {

	public static void main( String[] args ) {
		createEnvironment();
		SpringApplication.run( ToriiApplication.class, args );
	}

	private static void createEnvironment() {
		StaticContainer.getInstance();
		new File( Config.DEBUG_OUTPUT_FOLDER ).mkdirs();
		new File( Config.RESPONSE_CACHE_FOLDER ).mkdirs();
	}

}
