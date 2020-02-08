package com.ocwvar.torii.controller.core;

import com.ocwvar.torii.utils.protocol.Protocol;
import com.ocwvar.xml.node.Node;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 目测是更新包下载进度通知接口……
 * <p>
 * 但目前不知道怎么实现
 */
@RestController
public class DLStatus {

	@PostMapping( path = "/torii/dlstatus/**" )
	public void function( HttpServletRequest request, HttpServletResponse response ) throws Exception {
		final Node root = new Node( "response" );
		root.addChildNode( new Node( "dlstatus" ) );

		Protocol.encryptAndCommit( root, request, response );
	}

}
