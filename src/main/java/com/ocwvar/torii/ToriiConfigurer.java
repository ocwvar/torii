package com.ocwvar.torii;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ToriiConfigurer implements WebMvcConfigurer {

	@Override
	public void addInterceptors( InterceptorRegistry registry ) {
		registry.addInterceptor( new ToriiInterceptor() ).addPathPatterns( "/**" );
	}

}
