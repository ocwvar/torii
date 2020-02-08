package com.ocwvar.torii.controller.core;

import com.ocwvar.torii.utils.protocol.Protocol;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 处理 PASELI 相关事件
 */
@RestController
public class EaCoin {

	@PostMapping( path = "/torii/eacoin/**" )
	public void function( HttpServletRequest request, HttpServletResponse response ) throws Exception {
		Protocol.commit( 403, null, request, response );
	}

}
