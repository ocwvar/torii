package com.ocwvar.torii.controller.core;

import com.ocwvar.torii.utils.protocol.Protocol;
import com.ocwvar.xml.node.Node;
import com.ocwvar.xml.node.NodeBuilder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 这里负责返回需要在线更新下载的包
 * <p>
 * **目前没有找到已实现的项目，还不知道怎么实现 :(
 */
@RestController
public class Package {

	/*

	请求样本内容：
	<call model="KFC:J:A:A:2016121200" srcid="012085D5525C6F1504B0" tag="07731507">
    	<package method="list" pkgtype="all"/>
	</call>

	 */

	@PostMapping( path = "/torii/package/**" )
	public void function( HttpServletRequest request, HttpServletResponse response ) throws Exception {
		if ( Protocol.commitWithCache( request, response ) ) {
			return;
		}

		final Node root = new Node( "response" );
		root.addChildNode(
				new NodeBuilder( "package" )
						.addAttribute( "expire", "600" )
						.build()
		);

		Protocol.encryptAndCommit( root, request, response );
	}

}
