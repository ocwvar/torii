package com.ocwvar.torii.controller.core;

import com.ocwvar.torii.utils.protocol.Protocol;
import com.ocwvar.xml.node.Node;
import com.ocwvar.xml.node.NodeBuilder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 这里相当于接收每个事件的状态值，不需要管，只返回一个空的值即可
 */
@RestController
public class PCBEvent {

	/*

	请求样本内容：
	<call model="KFC:J:A:A:2016121200" srcid="01201000003756CA9F84" tag="e2b7ad6a">
    	<pcbevent method="put">
    	    <time __type="time">1580480610</time>
    	    <seq __type="u32">0</seq>
    	    <item>
    	        <name __type="str">boot</name>
    	        <value __type="s32">1</value>
    	        <time __type="time">1580480610</time>
    	    </item>
    	</pcbevent>
	</call>

	 */

	@PostMapping( path = "/torii/pcbevent/**" )
	public void function( HttpServletRequest request, HttpServletResponse response ) throws Exception {
		final Node root = new Node( "response" );
		root.addChildNode( new Node( "pcbevent" ) );

		Protocol.encryptAndCommit( root, request, response );
	}

}
