package com.ocwvar.torii.controller.game.kfc;

import com.ocwvar.torii.controller.game.kfc.c2019100800.RequestHandler;
import com.ocwvar.torii.service.game.kfc.ProfileService;
import com.ocwvar.torii.utils.protocol.Protocol;
import com.ocwvar.xml.node.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 这里负责分配 SDVX 的游戏功能节点请求
 */
@RestController
public class KFCRouter {

	private final ProfileService profileService;

	@Autowired
	public KFCRouter( ProfileService profileService ) {
		this.profileService = profileService;
	}

	@SuppressWarnings( "SwitchStatementWithTooFewBranches" )
	@PostMapping( path = "torii/sdvx/{model}/game/{func}" )
	public void function(
			@PathVariable String model,
			@PathVariable String func,
			HttpServletRequest request,
			HttpServletResponse response
	) throws Exception {

		final String[] modelValues = model.split( ":" );
		Node call = Protocol.decrypt( request );
		Node root = null;

		switch ( modelValues[ modelValues.length - 1 ] ) {
			case "2019100800":
			case "2019020600":
				switch ( func ) {
					case "sv5_exception":
						root = RequestHandler.handle_sv5_exception( call );
						break;

					case "sv5_common":
					case "sv4_common":
						root = RequestHandler.handle_sv5_common( call );
						//Protocol.commitWithDumpXml( "H:\\IdeaProjects\\BServer\\DataTesting\\sample\\sv4_common\\response_lite.xml", request, response );
						//Protocol.commitWithDumpXml( "F:\\Users\\ocwvar\\Desktop\\t1_common.xml", request, response );
						break;

					case "sv5_load":
					case "sv4_load":
						root = RequestHandler.handle_sv5_load( call, this.profileService );
						//Protocol.commitWithDumpXml( "F:\\Users\\ocwvar\\Desktop\\sv5_load_checked.xml", request, response );
						//Protocol.commitWithDumpXml( "F:\\Users\\ocwvar\\Desktop\\t1_load.xml", request, response );
						break;

					case "sv4_frozen":
					case "sv5_frozen":
						root = RequestHandler.handle_sv5_frozen( call );
						break;

					case "sv5_hiscore":
					case "sv4_hiscore":
						root = RequestHandler.handle_sv5_hiscore( call );
						break;

					case "sv5_shop":
					case "sv4_shop":
						root = RequestHandler.handle_sv5_shop( call );
						break;

					case "sv5_load_m":
					case "sv4_load_m":
						root = RequestHandler.handle_sv5_load_m( call );
						break;

					case "sv5_load_r":
					case "sv4_load_r":
						root = RequestHandler.handle_sv5_load_r( call );
						break;
				}
				break;
		}

		Protocol.encryptAndCommit( root, request, response );
	}

}
