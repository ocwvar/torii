package com.ocwvar.torii.controller.core;

import com.ocwvar.torii.Configs;
import com.ocwvar.torii.Field;
import com.ocwvar.torii.utils.protocol.Protocol;
import com.ocwvar.utils.Log;
import com.ocwvar.utils.node.Node;
import com.ocwvar.utils.node.TypeNode;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.InetAddress;

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
		if ( Protocol.commitWithCache( request, response ) ) {
			return;
		}

		Protocol.encryptAndCommit( getFacilityNode(), request, response );
	}

	private Node getFacilityNode() {

		String ip;
		try {
			ip = InetAddress.getLocalHost().getHostAddress();
		} catch ( Exception ignore ) {
			ip = "127.0.0.1";
		}

		Log.getInstance().print( "服务器地址：" + ip );
		final Node root = new Node( "response" );
		final Node facilityNode = new Node( "facility" );

		final Node locationNode = new Node( "location" );
		locationNode.addChildNode( new TypeNode( "id", "SONY" ) );
		locationNode.addChildNode( new TypeNode( "country", "JP" ) );
		locationNode.addChildNode( new TypeNode( "region", "." ) );
		locationNode.addChildNode( new TypeNode( "type", "0", "u8" ) );
		locationNode.addChildNode( new TypeNode( "latitude", "0", "u8" ) );
		locationNode.addChildNode( new TypeNode( "longitude", "0", "u8" ) );
		locationNode.addChildNode( new TypeNode( "accuracy", "0", "u8" ) );
		locationNode.addChildNode( new TypeNode( "name", Configs.getShopName() ) );
		locationNode.addChildNode( new TypeNode( "countryname", Configs.getShopName() ) );
		locationNode.addChildNode( new TypeNode( "countryjname", Configs.getShopName() ) );
		locationNode.addChildNode( new TypeNode( "regionname", Configs.getShopName() ) );
		locationNode.addChildNode( new TypeNode( "regionjname", Configs.getShopName() ) );
		locationNode.addChildNode( new TypeNode( "customercode", Configs.getShopName() ) );
		locationNode.addChildNode( new TypeNode( "customercode", Configs.getShopName() ) );

		final Node classNode = new Node( "class" );
		classNode.addChildNode( new TypeNode( "id", "ID" ) );
		classNode.addChildNode( new TypeNode( "line", "0", "u8" ) );

		final Node portfwNode = new Node( "portfw" );
		portfwNode.addChildNode( new TypeNode( "globalip", ip, "ip4" ) );
		portfwNode.addChildNode( new TypeNode( "globalport", Configs.getPort(), "u16" ) );
		portfwNode.addChildNode( new TypeNode( "privateport", Configs.getPort(), "u16" ) );

		final Node publicNode = new Node( "public" );
		publicNode.addChildNode( new TypeNode( "flag", "1", "u8" ) );
		publicNode.addChildNode( new TypeNode( "name", Configs.getShopName() ) );
		publicNode.addChildNode( new TypeNode( "latitude", "0" ) );
		publicNode.addChildNode( new TypeNode( "longitude", "0" ) );

		final Node eacoinNode = new Node( "eacoin" );
		eacoinNode.addChildNode( new TypeNode( "notchamount", "3000", "s32" ) );
		eacoinNode.addChildNode( new TypeNode( "notchcount", "3", "s32" ) );
		eacoinNode.addChildNode( new TypeNode( "supplylimit", "10000", "s32" ) );

		final Node eapassNode = new Node( "eapass" );
		eapassNode.addChildNode( new TypeNode( "valid", "0", "u16" ) );

		final Node urlNode = new Node( "url" );
		urlNode.addChildNode( new TypeNode( "eapass", Configs.getPinSceneMessage() ) );
		urlNode.addChildNode( new TypeNode( "arcadefan", "'www.konami.jp/am" ) );
		urlNode.addChildNode( new TypeNode( "konaminetdx", "http://am.573.jp" ) );
		urlNode.addChildNode( new TypeNode( "konamiid", "https://id.konami.net" ) );
		urlNode.addChildNode( new TypeNode( "eagate", "http://eagate.573.jp" ) );

		final Node shareNode = new Node( "share" );
		shareNode.addChildNode( eacoinNode );
		shareNode.addChildNode( urlNode );
		shareNode.addChildNode( eapassNode );

		facilityNode.addChildNode( locationNode );
		facilityNode.addChildNode( classNode );
		facilityNode.addChildNode( portfwNode );
		facilityNode.addChildNode( publicNode );
		facilityNode.addChildNode( shareNode );

		root.addChildNode( facilityNode );
		root.setEncodeCharset( Field.SHIFT_JIS );
		return root;
	}

}
