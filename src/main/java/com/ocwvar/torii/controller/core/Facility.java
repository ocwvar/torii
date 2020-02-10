package com.ocwvar.torii.controller.core;

import com.ocwvar.torii.utils.protocol.Protocol;
import com.ocwvar.xml.node.Node;
import com.ocwvar.xml.node.TypeNode;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 返回当前服务器配置
 */
@RestController
public class Facility {

	/*

	请求样本内容：
	<call model="KFC:J:A:A:2016121200" srcid="012085D5525C6F1504B0" tag="023f5d7b">
	    <facility encoding="SHIFT_JIS" method="get"/>
	</call>

	 */

	@PostMapping( path = "/torii/facility/**" )
	public void function( HttpServletRequest request, HttpServletResponse response ) throws Exception {
		Protocol.encryptAndCommit( getFacilityNode(), request, response );
	}

	private Node getFacilityNode() {
		final Node root = new Node( "response" );

		final Node facilityNode = new Node( "facility" );
		facilityNode.addAttribute( "expire", "600" );

		final Node locationNode = new Node( "location" );
		locationNode.addChildNode( new TypeNode( "id", "11451419" ) );
		locationNode.addChildNode( new TypeNode( "country", "JP" ) );
		locationNode.addChildNode( new TypeNode( "region", "." ) );
		locationNode.addChildNode( new TypeNode( "name", "SONY_MOBILE_SHOP" ) );
		locationNode.addChildNode( new TypeNode( "type", "0", "u8" ) );

		final Node lineNode = new Node( "line" );
		lineNode.addChildNode( new TypeNode( "id", "." ) );
		lineNode.addChildNode( new TypeNode( "class", "0", "u8" ) );

		final Node portfwNode = new Node( "portfw" );
		portfwNode.addChildNode( new TypeNode( "globalip", "127.0.0.1", "ip4" ) );
		portfwNode.addChildNode( new TypeNode( "globalport", "50002", "u16" ) );
		portfwNode.addChildNode( new TypeNode( "privateport", "50002", "u16" ) );

		final Node publicNode = new Node( "public" );
		publicNode.addChildNode( new TypeNode( "flag", "1", "u8" ) );
		publicNode.addChildNode( new TypeNode( "name", "." ) );
		publicNode.addChildNode( new TypeNode( "latitude", "0" ) );
		publicNode.addChildNode( new TypeNode( "longitude", "0" ) );

		final Node eacoinNode = new Node( "eacoin" );
		eacoinNode.addChildNode( new TypeNode( "notchamount", "3000", "s32" ) );
		eacoinNode.addChildNode( new TypeNode( "notchcount", "3", "s32" ) );
		eacoinNode.addChildNode( new TypeNode( "supplylimit", "10000", "s32" ) );

		final Node eapassNode = new Node( "eapass" );
		eapassNode.addChildNode( new TypeNode( "valid", "365", "u16" ) );

		final Node urlNode = new Node( "url" );
		urlNode.addChildNode( new TypeNode( "eapass", "www.ea-pass.konami.net" ) );
		urlNode.addChildNode( new TypeNode( "arcadefan", "'www.konami.jp/am" ) );
		urlNode.addChildNode( new TypeNode( "konaminetdx", "http://am.573.jp" ) );
		urlNode.addChildNode( new TypeNode( "konamiid", "https://id.konami.net" ) );
		urlNode.addChildNode( new TypeNode( "eagate", "http://eagate.573.jp" ) );

		final Node shareNode = new Node( "share" );
		shareNode.addChildNode( eacoinNode );
		shareNode.addChildNode( urlNode );
		shareNode.addChildNode( eapassNode );

		facilityNode.addChildNode( locationNode );
		facilityNode.addChildNode( lineNode );
		facilityNode.addChildNode( portfwNode );
		facilityNode.addChildNode( publicNode );
		facilityNode.addChildNode( shareNode );

		root.addChildNode( facilityNode );
		return root;
	}

}
