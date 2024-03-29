package com.ocwvar.torii.controller.game.kfc;

import com.ocwvar.torii.controller.game.kfc.c2019100800.RequestHandler;
import com.ocwvar.torii.service.game.kfc.ProfileService;
import com.ocwvar.torii.utils.protocol.Protocol;
import com.ocwvar.utils.Log;
import com.ocwvar.utils.node.Node;
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

	@PostMapping( path = "torii/sdvx/{model}/game/{func}" )
	public void function(
			@PathVariable String model,
			@PathVariable String func,
			HttpServletRequest request,
			HttpServletResponse response
	) throws Exception {

		if ( Protocol.commitWithCache( request, response ) ) {
			return;
		}

		final String[] modelValues = model.split( ":" );
		Node call = Protocol.decrypt( request );
		Node root = null;

		switch ( modelValues[ modelValues.length - 1 ] ) {
			case "2019020600":
			case "2019100800":
				switch ( func ) {
					case "sv5_common":
					case "sv4_common":
						root = RequestHandler.handle_sv5_common( call );
						break;

					case "sv5_exception":
					case "sv4_exception":
						root = RequestHandler.handle_sv5_exception( call );
						break;

					case "sv5_new":
					case "sv4_new":
						root = RequestHandler.handle_sv5_new( call, this.profileService );
						break;

					case "sv5_load":
					case "sv4_load":
						root = RequestHandler.handle_sv5_load( call, this.profileService );
						break;

					case "sv5_frozen":
					case "sv4_frozen":
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
						root = RequestHandler.handle_sv5_load_m( call, this.profileService );
						break;

					case "sv5_load_r":
					case "sv4_load_r":
						root = RequestHandler.handle_sv5_load_r( call );
						break;

					case "sv5_play_s":
					case "sv4_play_s":
						root = RequestHandler.handle_sv5_play_s( call );
						break;

					case "sv5_play_e":
					case "sv4_play_e":
						root = RequestHandler.handle_sv5_play_e( call );
						break;

					case "sv5_save":
					case "sv4_save":
						root = RequestHandler.handle_sv5_save( call, this.profileService );
						break;

					case "sv5_save_e":
					case "sv4_save_e":
						root = RequestHandler.handle_sv5_save_e( call );
						break;

					case "sv5_save_c":
					case "sv4_save_c":
						root = RequestHandler.handle_sv5_save_c( call, this.profileService );
						break;

					case "sv5_save_m":
					case "sv4_save_m":
						root = RequestHandler.handle_sv5_save_m( call, this.profileService );
						break;

					case "sv5_buy":
					case "sv4_buy":
						root = RequestHandler.handle_sv5_buy( call, this.profileService );
						break;
				}
				break;
		}

		Protocol.encryptAndCommit( root, request, response );
		Log.getInstance().print( "已处理完成节点：" + func + "  " + root );
	}

}
