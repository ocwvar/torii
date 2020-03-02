package com.ocwvar.torii.controller.core;

import com.ocwvar.torii.Configs;
import com.ocwvar.torii.utils.protocol.Protocol;
import com.ocwvar.utils.node.Node;
import com.ocwvar.utils.node.NodeBuilder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * PCBTracker 负责处理 PCBTracker.alive 请求
 * 以及返回当前是否支持 PASELI 功能
 */
@RestController
public class PCBTracker {

	/*

	请求样本内容：
	<call model="KFC:J:A:A:2016121200" srcid="01201000003756CA9F84" tag="0c58689b">
	    <pcbtracker accountid="012085D5525C6F1504B0" ecflag="1" hardid="1000" method="alive" softid="1000"/>
	</call>

	 */

	@PostMapping( path = "/torii/pcbtracker/**" )
	public void function( HttpServletRequest request, HttpServletResponse response ) throws Exception {
		if ( Protocol.commitWithCache( request, response ) ) {
			return;
		}

		final Node root = new Node( "response" );
		root.addChildNode(
				new NodeBuilder( "pcbtracker" )
						.addAttribute( "ecenable", Configs.isPaseliEnable() ? "1" : "0" )
						.addAttribute( "expire", "600" )
						.build()
		);

		Protocol.encryptAndCommit( root, request, response );
	}

}
