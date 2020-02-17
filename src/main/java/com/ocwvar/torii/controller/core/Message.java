package com.ocwvar.torii.controller.core;

import com.ocwvar.torii.Configs;
import com.ocwvar.torii.utils.protocol.Protocol;
import com.ocwvar.xml.node.Node;
import com.ocwvar.xml.node.NodeBuilder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 传输特定消息给客户端，例如当前是否处于维护状态
 */
@RestController
public class Message {

	/*

	请求内容样本：
	<call model="KFC:J:A:A:2016121200" srcid="012085D5525C6F1504B0" tag="5ca5c375">
	    <message method="get"/>
	</call>

	 */

	@PostMapping( path = "/torii/message/**" )
	public void function( HttpServletRequest request, HttpServletResponse response ) throws Exception {
		if ( Protocol.commitWithCache( request, response ) ) {
			return;
		}

		final Node root = new Node( "response" );
		final Node message = new Node( "message" );
		message.addAttribute( "expire", "600" );
		root.addChildNode( message );

		//判断处于维护中
		if ( Configs.isIsUnderMaintenance() ) {
			message.addAttribute( "status", "0" );

			message.addChildNode(
					new NodeBuilder( "item" )
							.addAttribute( "end", "604800" )
							.addAttribute( "name", "sys.mainte" )
							.addAttribute( "start", "0" )
							.build()
			);

			message.addChildNode(
					new NodeBuilder( "item" )
							.addAttribute( "end", "604800" )
							.addAttribute( "name", "sys.eacoin.mainte" )
							.addAttribute( "start", "0" )
							.build()
			);
		}

		Protocol.encryptAndCommit( root, request, response );
	}

}
