package com.ocwvar.torii.controller.game.kfc;

import com.ocwvar.torii.utils.protocol.Protocol;
import com.ocwvar.xml.node.Node;
import com.ocwvar.xml.node.TypeNode;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 事件记录
 */
@RestController
public class EventLog {

	@PostMapping( "sdvx/*/eventlog/write" )
	public void function( HttpServletRequest request, HttpServletResponse response ) throws Exception {
		//这里返回 dummy 数据
		final Node root = new Node( "response" );
		final Node eventLog = new Node( "eventlog" );
		root.addChildNode( eventLog );

		eventLog.addChildNode( new TypeNode( "gamesession", "0", "s64" ) );
		eventLog.addChildNode( new TypeNode( "logsendflg", "0", "s32" ) );
		eventLog.addChildNode( new TypeNode( "logerrlevel", "0", "s32" ) );
		eventLog.addChildNode( new TypeNode( "evtidnosendflg", "0", "s32" ) );

		Protocol.encryptAndCommit( root, request, response );
	}

}
