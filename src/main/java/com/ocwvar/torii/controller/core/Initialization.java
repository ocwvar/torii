package com.ocwvar.torii.controller.core;

import com.ocwvar.torii.Config;
import com.ocwvar.torii.Field;
import com.ocwvar.torii.utils.protocol.Protocol;
import com.ocwvar.utils.annotation.Nullable;
import com.ocwvar.xml.node.Node;
import com.ocwvar.xml.node.TypeNode;
import com.ocwvar.xml.node.UrlNode;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 获取功能节点地址XML数据
 */
@RestController
public class Initialization {

	/*

		使用 TORII 网络服务需要在 ea3_config 中进行如下配置：

		<network>
        	<timeout __type="u32">60000</timeout>
        	<sz_xrpc_buf __type="u32">102400</sz_xrpc_buf>
        	<url_slash __type="bool">1</url_slash>
        	<ssl __type="bool">0</ssl>
        	<services>http://127.0.0.1:50001/core</services>
  		</network>

		需要注意，如果 ea3_config 中的配置
		<url_slash __type="bool">1</url_slash>
		的值为 1 时，请求的网址会像：
		http://127.0.0.1/torii/sdvx/KFC:J:A:A:2019020600/game/sv4_common

		如果值为 0 是，请求的网址会像：（目前不支持这种请求方式）
		http://127.0.0.1/torii/sdvx
		具体请求的节点要看请求内容

	 */


	/*

	请求内容样本：
	URL:http://127.0.0.1/core/KFC:J:A:A:2019100800/services/get
	Need decompress:false
	Need rc4 decode:true
	<?xml version="1.0" encoding="UTF-8" standalone="no"?>
	<call model="KFC:J:A:A:2019100800" srcid="01201000003756CA9F84" tag="eFgHAOz1">
	    <services method="get">
	        <info>
	            <AVS2 __type="str">2.17.3 r8311</AVS2>
	        </info>
	        <net>
	            <if>
	                <id __type="u8">0</id>
	                <valid __type="bool">1</valid>
	                <type __type="u8">1</type>
	                <mac __count="6" __type="u8">44 208 90 90 44 2</mac>
	                <addr __type="ip4">192.168.0.106</addr>
	                <bcast __type="ip4">192.168.0.255</bcast>
	                <netmask __type="ip4">255.255.255.0</netmask>
	                <gateway __type="ip4">192.168.0.1</gateway>
	                <dhcp __type="ip4">192.168.0.1</dhcp>
	            </if>
	        </net>
	    </services>
	</call>

	 */

	@GetMapping( path = "/core/**" )
	public String defaultFunction() {
		return "HELLO THERE :)";
	}

	@PostMapping( path = "/core/**" )
	public void function( HttpServletRequest request, HttpServletResponse response ) throws Exception {
		final Node call = Protocol.decrypt( request );
		final String[] modelValues = call.getAttribute( "model" ).split( ":" );

		if ( !isValidPcbid( call.getAttribute( "srcid" ) ) ) {
			Protocol.commit( 404, null, request, response );
			return;
		}

		Protocol.encryptAndCommit( getFunctionsUrl( modelValues[ 0 ], modelValues[ modelValues.length - 1 ], "127.0.0.1" ), request, response );
	}

	/**
	 * 获取功能地址
	 *
	 * @param type          游戏类型
	 * @param version       游戏版本
	 * @param selfIpAddress 自身的IP地址
	 * @return 功能节点数据，如果不支持则返回 NULL
	 */
	@SuppressWarnings( "SwitchStatementWithTooFewBranches" )
	private @Nullable
	Node getFunctionsUrl( String type, String version, String selfIpAddress ) {
		final Node root = new Node( "response" );
		final Node services = new Node( "services" );
		services.addAttribute( "expire", "3600" );
		services.addAttribute( "method", "get" );

		//启动模式：operation、debug、test、factory
		services.addAttribute( "mode", "operation" );
		services.addAttribute( "product_domain", "1" );
		root.addChildNode( services );

		services.addChildNode( new UrlNode( "ntp", "ntp://eamuse.konami.fun/" ) );
		services.addChildNode( new UrlNode( "keepalive", "http://" + selfIpAddress + "/keepalive?pa=" + selfIpAddress + "&ia=" + selfIpAddress + "&ga=" + selfIpAddress + "&ma=" + selfIpAddress + "&t1=2&t2=10" ) );
		switch ( type ) {
			default:
				return null;

			case "KFC":
				switch ( version ) {
					default:
						return null;

					case "2019100800":    //SDVX V
					case "2019020600":    //SDVX IV
						services.addChildNode( new UrlNode( "pcbtracker", Config.BASE_URL + "/torii/pcbtracker" ) );
						services.addChildNode( new UrlNode( "facility", Config.BASE_URL + "/torii/facility" ) );
						services.addChildNode( new UrlNode( "pcbevent", Config.BASE_URL + "/torii/pcbevent" ) );
						services.addChildNode( new UrlNode( "package", Config.BASE_URL + "/torii/package" ) );
						services.addChildNode( new UrlNode( "message", Config.BASE_URL + "/torii/message" ) );
						services.addChildNode( new UrlNode( "dlstatus", Config.BASE_URL + "/torii/dlstatus" ) );
						services.addChildNode( new UrlNode( "cardmng", Config.BASE_URL + "/torii/cardmng" ) );
						services.addChildNode( new UrlNode( "eacoin", Config.BASE_URL + "/torii/eacoin" ) );
						services.addChildNode( new UrlNode( "local", Config.BASE_URL + "/torii/sdvx" ) );
						services.addChildNode( new UrlNode( "local2", Config.BASE_URL + "/torii/sdvx" ) );
						services.addChildNode( new UrlNode( "lobby", Config.BASE_URL + "/torii/sdvx" ) );
						services.addChildNode( new UrlNode( "slocal", Config.BASE_URL + "/torii/sdvx" ) );
						services.addChildNode( new UrlNode( "slocal2", Config.BASE_URL + "/torii/sdvx" ) );
						services.addChildNode( new UrlNode( "sglocal", Config.BASE_URL + "/torii/sdvx" ) );
						services.addChildNode( new UrlNode( "sglocal2", Config.BASE_URL + "/torii/sdvx" ) );
						services.addChildNode( new UrlNode( "lab", Config.BASE_URL + "/torii/sdvx" ) );
						services.addChildNode( new UrlNode( "globby", Config.BASE_URL + "/torii/sdvx" ) );
						services.addChildNode( new UrlNode( "slobby", Config.BASE_URL + "/torii/sdvx" ) );
						services.addChildNode( new UrlNode( "sglobby", Config.BASE_URL + "/torii/sdvx" ) );
						return root;
				}
		}
	}

	/**
	 * @return 此客户端的PCBID是否合法
	 */
	private boolean isValidPcbid( String clientPcbid ) {
		//todo 检测PCBID是否合法
		return true;
	}

}
