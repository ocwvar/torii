package com.ocwvar.torii.controller.core;

import com.ocwvar.torii.utils.protocol.Protocol;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 报错？
 */
@RestController
public class Error {

	@PostMapping( path = "/error" )
	public void function( HttpServletRequest request, HttpServletResponse response ) throws Exception {
		//Protocol.print( request );
		Protocol.commit( 200, null, request, response );
	}

}
