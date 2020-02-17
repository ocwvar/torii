package com.ocwvar.torii.controller.core;

import com.ocwvar.torii.utils.protocol.Protocol;
import com.ocwvar.xml.node.Node;
import com.ocwvar.xml.node.TypeNode;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 应该是事件日志
 */
@RestController
public class SEventLog {

	/*

		请求样本：
		http://127.0.0.1/torii/sdvx/KFC:J:A:A:2019100800/eventlog/write
		<?xml version="1.0" encoding="ASCII"?>
		<call model="KFC:J:A:A:2019100800" srcid="012010000000233A0E29" tag="xBswACRk">
		    <eventlog method="write">
		        <retrycnt __type="u32">0</retrycnt>
		        <data>
		            <eventid __type="str">G_PCKIN</eventid>
		            <eventorder __type="s32">6</eventorder>
		            <pcbtime __type="u64">1581398317860</pcbtime>
		            <gamesession __type="s64">1</gamesession>
		            <strdata1 __type="str"/>
		            <strdata2 __type="str"/>
		            <numdata1 __type="s64">1</numdata1>
		            <numdata2 __type="s64">0</numdata2>
		            <locationid __type="str">ea</locationid>
		        </data>
		    </eventlog>
		</call>

	 */

	@PostMapping( path = "torii/{game}/{model}/eventlog/*" )
	public void function( HttpServletRequest request, HttpServletResponse response, @PathVariable String game, @PathVariable String model ) throws Exception {
		if ( Protocol.commitWithCache( request, response ) ) {
			return;
		}

		//直接返回数据即可
		final Node root = new Node( "response" );
		final Node eventlog = new Node( "eventlog" );
		root.addChildNode( eventlog );

		eventlog.addChildNode( new TypeNode( "eventlog", "1", "s64" ) );
		eventlog.addChildNode( new TypeNode( "logsendflg", "0", "s32" ) );
		eventlog.addChildNode( new TypeNode( "logerrlevel", "0", "s32" ) );
		eventlog.addChildNode( new TypeNode( "evtidnosendflg", "0", "s32" ) );

		Protocol.encryptAndCommit( root, request, response );
	}

}
