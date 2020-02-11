package com.ocwvar.torii.controller.game.kfc.c2019100800;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ocwvar.torii.Config;
import com.ocwvar.torii.Field;
import com.ocwvar.torii.data.game.kfc.Course;
import com.ocwvar.torii.db.entity.*;
import com.ocwvar.torii.service.game.kfc.ProfileService;
import com.ocwvar.torii.utils.IO;
import com.ocwvar.utils.Log;
import com.ocwvar.utils.Pair;
import com.ocwvar.utils.TextUtils;
import com.ocwvar.utils.annotation.Nullable;
import com.ocwvar.xml.node.*;

public class RequestHandler {

	/**
	 * @param call 请求的数据
	 * @return 响应的数据
	 */
	public static @Nullable
	Node handle_sv5_play_e( Node call ) {
		final Node root = new Node( "response" );
		root.addChildNode( new Node( "game_5" ) );
		return root;
	}

	/**
	 * @param call 请求的数据
	 * @return 响应的数据
	 */
	public static @Nullable
	Node handle_sv5_play_s( Node call ) {
		final Node root = new Node( "response" );
		final Node game = new Node( "game_5" );
		game.addChildNode( new TypeNode( "play_id", "1", "u32" ) );
		return root;
	}

	/**
	 * 保存用户配置以及数据
	 *
	 * @param call    请求的数据
	 * @param service 账号数据交互服务
	 * @return 响应的数据
	 */
	public static @Nullable
	Node handle_sv5_save( Node call, ProfileService service ) {
		final Node call_game = ( Node ) call.getFirstChildNode();
		final String refid = call_game.indexChildNode( "refid" ).getContentValue();
		final Sv5Profile old = service.getProfile( refid );

		//计算 packet、block、blaster_energy 的增减
		final int earned_packet = Integer.parseInt( call_game.indexChildNode( "earned_gamecoin_packet" ).getContentValue() );
		final int earned_block = Integer.parseInt( call_game.indexChildNode( "earned_gamecoin_block" ).getContentValue() );
		final int earned_blaster_energy = Integer.parseInt( call_game.indexChildNode( "earned_blaster_energy" ).getContentValue() );
		final int old_packet = Integer.parseInt( old.getPacket_point() );
		final int old_block = Integer.parseInt( old.getBlock_point() );
		final int old_blaster_energy = Integer.parseInt( old.getBlaster_energy() );
		final int blaster_count = Integer.parseInt( old.getBlaster_count() ) + ( earned_blaster_energy > 0 ? 1 : 0 );

		//最后一首歌曲数据
		final Node musicHistory = ( Node ) call_game.indexChildNode( "music" );
		final Node lastMusicInfo = ( Node ) musicHistory.indexChildNode( musicHistory.childCount() - 1 );

		//用户的其他参数
		final Node call_param = ( Node ) call_game.indexChildNode( "param" );

		//保存其他参数
		String id, type, p;
		for ( BaseNode n : call_param.getChildNodes() ) {
			id = n.indexChildNode( "id" ).getContentValue();
			type = n.indexChildNode( "type" ).getContentValue();
			p = n.indexChildNode( "param" ).getContentValue();

			//存入数据库
			final Sv5ProfileParam profileParam = new Sv5ProfileParam( refid, id, type, p );
			service.saveProfileParam( profileParam );
		}

		//保存解锁成就
		final Node items = ( Node ) call_game.indexChildNode( "item" );
		if ( items != null && items.childCount() > 0 ) {
			//如果没有任何成就获得，则客户端是不会返回这个节点
			for ( BaseNode it : items.getChildNodes() ) {
				service.saveUnlockItem(
						new UnlockItem(
								refid,
								it.indexChildNode( "id" ).getContentValue(),
								it.indexChildNode( "type" ).getContentValue(),
								it.indexChildNode( "param" ).getContentValue()
						)
				);
			}
		}

		final Sv5Profile profile = new Sv5Profile(
				refid,
				String.valueOf( old_packet + earned_packet ),
				String.valueOf( old_block + earned_block ),
				String.valueOf( old_blaster_energy + earned_blaster_energy ),
				String.valueOf( blaster_count ),
				call_game.indexChildNode( "appeal_id" ).getContentValue(),
				call_game.indexChildNode( "skill_level" ).getContentValue(),
				call_game.indexChildNode( "skill_base_id" ).getContentValue(),
				call_game.indexChildNode( "skill_name_id" ).getContentValue(),
				old.getPlayer_name(),
				old.getPlayer_code()
		);

		final Sv5Setting setting = new Sv5Setting(
				refid,
				lastMusicInfo.indexChildNode( "id" ).getContentValue(),
				lastMusicInfo.indexChildNode( "type" ).getContentValue(),
				call_game.indexChildNode( "sort_type" ).getContentValue(),
				call_game.indexChildNode( "narrow_down" ).getContentValue(),
				call_game.indexChildNode( "headphone" ).getContentValue(),
				call_game.indexChildNode( "gauge_option" ).getContentValue(),
				call_game.indexChildNode( "ars_option" ).getContentValue(),
				call_game.indexChildNode( "early_late_disp" ).getContentValue(),
				call_game.indexChildNode( "notes_option" ).getContentValue(),
				call_game.indexChildNode( "eff_c_left" ).getContentValue(),
				call_game.indexChildNode( "eff_c_right" ).getContentValue(),
				call_game.indexChildNode( "lanespeed" ).getContentValue(),
				call_game.indexChildNode( "hispeed" ).getContentValue(),
				call_game.indexChildNode( "draw_adjust" ).getContentValue(),
				call_game.indexChildNode( "p_start" ).getContentValue(),
				call_game.indexChildNode( "p_end" ).getContentValue()
		);

		//保存配置文件以及设置数据
		service.saveProfile( profile, setting );

		final Node root = new Node( "response" );
		root.addChildNode( new Node( "game" ) );
		return root;
	}

	/**
	 * 保存成绩数据
	 *
	 * @param call    请求的数据
	 * @param service 账号数据交互服务
	 * @return 响应的数据
	 */
	public static @Nullable
	Node handle_sv5_save_m( Node call, ProfileService service ) {
		final Node call_game = ( Node ) call.getFirstChildNode();
		final Sv5Score score = new Sv5Score(
				call_game.indexChildNode( "refid" ).getContentValue(),
				call_game.indexChildNode( "music_id" ).getContentValue(),
				call_game.indexChildNode( "music_type" ).getContentValue(),
				call_game.indexChildNode( "score" ).getContentValue(),
				call_game.indexChildNode( "clear_type" ).getContentValue(),
				call_game.indexChildNode( "score_grade" ).getContentValue(),
				call_game.indexChildNode( "max_chain" ).getContentValue(),
				call_game.indexChildNode( "critical" ).getContentValue(),
				call_game.indexChildNode( "near" ).getContentValue(),
				call_game.indexChildNode( "error" ).getContentValue(),
				call_game.indexChildNode( "gauge_type" ).getContentValue(),
				call_game.indexChildNode( "effective_rate" ).getContentValue(),
				call_game.indexChildNode( "btn_rate" ).getContentValue(),
				call_game.indexChildNode( "long_rate" ).getContentValue(),
				call_game.indexChildNode( "vol_rate" ).getContentValue(),
				call_game.indexChildNode( "mode" ).getContentValue(),
				call_game.indexChildNode( "notes_option" ).getContentValue()
		);

		service.saveScore( score );

		final Node root = new Node( "response" );
		root.addChildNode( new Node( "game" ) );
		return root;
	}

	/**
	 * 保存段位成绩数据
	 *
	 * @param call    请求的数据
	 * @param service 账号数据交互服务
	 * @return 响应的数据
	 */
	public static @Nullable
	Node handle_sv5_save_c( Node call, ProfileService service ) {
		final Node call_game = ( Node ) call.getFirstChildNode();
		final String refId = call_game.indexChildNode( "refId" ).getContentValue();
		final String season_id = call_game.indexChildNode( "ssnid" ).getContentValue();
		final String course_id = call_game.indexChildNode( "crsid" ).getContentValue();
		final String clear_type = call_game.indexChildNode( "ct" ).getContentValue();
		final String achievement_rate = call_game.indexChildNode( "ar" ).getContentValue();
		final String grade = call_game.indexChildNode( "gr" ).getContentValue();
		final String score = call_game.indexChildNode( "sc" ).getContentValue();

		service.saveCourse( new Sv5Course(
				season_id,
				course_id,
				score,
				clear_type,
				grade,
				achievement_rate,
				"1",
				refId
		) );

		final Node root = new Node( "response" );
		root.addChildNode( new Node( "game" ) );
		return root;
	}

	/**
	 * Policy floor infection 相关的逻辑
	 *
	 * @param call 请求的数据
	 * @return 响应的数据
	 */
	public static @Nullable
	Node handle_sv5_save_e( Node call ) {
		final Node root = new Node( "response" );
		final Node game = new Node( "game" );
		root.addChildNode( game );

		final Node pbc_infection = new Node( "pbc_infection" );
		game.addChildNode( pbc_infection );

		for ( String name : new String[]{ "packet", "block", "coloris" } ) {
			final Node child = new Node( name );
			child.addChildNode( new TypeNode( "before", "0", "s32" ) );
			child.addChildNode( new TypeNode( "after", "0", "s32" ) );
			pbc_infection.addChildNode( child );
		}

		final Node pb_infection = new Node( "pb_infection" );
		game.addChildNode( pb_infection );

		for ( String name : new String[]{ "packet", "block" } ) {
			final Node child = new Node( name );
			child.addChildNode( new TypeNode( "before", "0", "s32" ) );
			child.addChildNode( new TypeNode( "after", "0", "s32" ) );
			pb_infection.addChildNode( child );
		}

		return root;
	}

	/**
	 * 处理购买物品请求
	 *
	 * @param call    请求的数据
	 * @param service 账号数据交互服务
	 * @return 响应的数据
	 */
	public static @Nullable
	Node handle_sv5_buy( Node call, ProfileService service ) {
		final Node call_game = ( Node ) call.getFirstChildNode();
		final String refId = call_game.indexChildNode( "refid" ).getContentValue();
		final Node item = ( Node ) call_game.indexChildNode( "item" );
		final Sv5Profile profile = service.getProfile( refId );

		//消费方式，0：PACKET   1：BLOCK
		final int currency_type = Integer.parseInt( call_game.indexChildNode( "currency_type" ).getContentValue() );

		//获取数据库内的数值，加上目前已获取到的数值，为当前可用数值
		final int old_packet = Integer.parseInt( profile.getPacket_point() );
		final int old_block = Integer.parseInt( profile.getBlock_point() );
		final int earned_packet = Integer.parseInt( call_game.indexChildNode( "earned_gamecoin_packet" ).getContentValue() );
		final int earned_block = Integer.parseInt( call_game.indexChildNode( "earned_gamecoin_block" ).getContentValue() );
		final int total_packet = old_packet + earned_packet;
		final int total_block = old_block + earned_block;

		//返回的数据
		final Node root = new Node( "response" );
		final Node game = new Node( "game" );
		root.addChildNode( game );

		//计算本次消费总额
		final ArrayTypeNode prices = new ArrayTypeNode( item.indexChildNode( "price" ) );
		int sumPrice = 0;
		for ( int i = 0; i < prices.count(); i++ ) {
			sumPrice += Integer.parseInt( prices.value( i ) );
		}

		//判断是否能购买成功
		final boolean enough;
		switch ( currency_type ) {
			case 0:
				final int packetResult = total_packet - sumPrice;
				enough = packetResult >= 0;
				game.addChildNode( new TypeNode( "gamecoin_block", String.valueOf( old_block ), "u32" ) );
				if ( enough ) {
					profile.setPacket_point( String.valueOf( packetResult ) );
					game.addChildNode( new TypeNode( "gamecoin_packet", String.valueOf( packetResult ), "u32" ) );
					game.addChildNode( new TypeNode( "result", "0", "u8" ) );
				}
				break;

			case 1:
				final int blockResult = total_block - sumPrice;
				enough = blockResult >= 0;
				game.addChildNode( new TypeNode( "gamecoin_packet", String.valueOf( old_packet ), "u32" ) );
				if ( enough ) {
					profile.setBlock_point( String.valueOf( blockResult ) );
					game.addChildNode( new TypeNode( "gamecoin_block", String.valueOf( blockResult ), "u32" ) );
					game.addChildNode( new TypeNode( "result", "0", "u8" ) );
				}
				break;

			default:
				//未定义的支付方式
				game.addChildNode( new TypeNode( "gamecoin_packet", String.valueOf( old_packet ), "u32" ) );
				game.addChildNode( new TypeNode( "gamecoin_block", String.valueOf( old_block ), "u32" ) );
				game.addChildNode( new TypeNode( "result", "1", "u8" ) );
				return root;
		}

		//所有要购买物品的数据
		final ArrayTypeNode itemsType = new ArrayTypeNode( item.indexChildNode( "item_type" ) );
		final ArrayTypeNode itemsID = new ArrayTypeNode( item.indexChildNode( "item_id" ) );
		final ArrayTypeNode itemsParam = new ArrayTypeNode( item.indexChildNode( "param" ) );
		final int count = itemsType.count();

		String type, id, param;
		for ( int i = 0; i < count; i++ ) {
			type = itemsType.value( i );
			id = itemsID.value( i );
			param = itemsParam.value( i );

			//保存已解锁数据到数据库
			service.saveUnlockItem( new UnlockItem( refId, id, type, param ) );
		}

		//更新账户数值
		service.saveProfile( profile );

		game.addChildNode( new TypeNode( "gamecoin_packet", String.valueOf( old_packet ), "u32" ) );
		game.addChildNode( new TypeNode( "gamecoin_block", String.valueOf( old_packet ), "u32" ) );
		game.addChildNode( new TypeNode( "result", "1", "u8" ) );

		return root;
	}

	/**
	 * 创建新的用户
	 *
	 * @param call    请求的数据
	 * @param service 账号数据交互服务
	 * @return 响应的数据
	 */
	public static @Nullable
	Node handle_sv5_new( Node call, ProfileService service ) {
		final Node call_game = ( Node ) call.getFirstChildNode();
		final String pcbid = call.getAttribute( "srcid" );
		final String refId = call_game.indexChildNode( "refid" ).getContentValue();
		final String name = call_game.indexChildNode( "name" ).getContentValue();
		final String userCode = TextUtils.getRandomText( true, 9 );

		//这个位置ID指 facility 中的 location 节点内的 id 值
		final String localId = call_game.indexChildNode( "locid" ).getContentValue();

		service.createDefaultProfile( refId, name, userCode );
		Log.getInstance().print( "新注册  用户名：" + name + "  用户码：" + userCode + "  REF_ID：" + refId );

		final Node root = new Node( "response" );
		root.addChildNode( new Node( "game" ) );
		return root;
	}

	/**
	 * 读取玩家数据
	 * <p>
	 * TODO	段位记录存储获取
	 * TODO 游玩次数统计存储
	 *
	 * @param call    请求的数据
	 * @param service 账号数据交互服务
	 * @return 响应的数据
	 */
	public static @Nullable
	Node handle_sv5_load( Node call, ProfileService service ) {
		final String pcbid = call.getAttribute( "srcid" );

		final Node cGame = ( Node ) call.getFirstChildNode();
		final Node refId = ( Node ) cGame.indexChildNode( "refid" );
		if ( refId == null ) {
			Log.getInstance().print( "返回数据缺少 REF_ID 节点" );
			return null;
		}

		final Sv5Profile profile = service.getProfile( refId.getContentValue() );
		final Sv5Setting setting = service.getSetting( refId.getContentValue() );
		final Node root = new Node( "response" );

		if ( profile != null ) {

			//有已保存的数据
			final Node game = new Node( "game" );
			root.addChildNode( game );
			Log.getInstance().print( "已存在的玩家：" + refId.getContentValue() );
			game.addChildNode( new TypeNode( "result", "0", "u8" ) );
			game.addChildNode( new TypeNode( "name", profile.getPlayer_name() ) );
			game.addChildNode( new TypeNode( "code", profile.getPlayer_code() ) );
			game.addChildNode( new TypeNode( "kac_id", profile.getPlayer_code() ) );
			game.addChildNode( new TypeNode( "creator_id", "0", "u32" ) );

			//最后保存的状态
			game.addChildNode( new TypeNode( "last_music_id", setting.getLast_music_id(), "u32" ) );
			game.addChildNode( new TypeNode( "last_music_type", setting.getLast_music_type(), "u8" ) );
			game.addChildNode( new TypeNode( "sort_type", setting.getSort_type(), "u8" ) );
			game.addChildNode( new TypeNode( "narrow_down", setting.getNarrow_down(), "u8" ) );
			game.addChildNode( new TypeNode( "headphone", setting.getHeadphone(), "u8" ) );
			game.addChildNode( new TypeNode( "appeal_id", profile.getAppeal_id(), "u16" ) );
			game.addChildNode( new TypeNode( "p_start", setting.getP_start(), "u64" ) );
			game.addChildNode( new TypeNode( "p_end", setting.getP_end(), "u64" ) );

			//当前段位
			game.addChildNode( new TypeNode( "skill_level", profile.getSkill_level(), "s16" ) );
			game.addChildNode( new TypeNode( "skill_base_id", profile.getSkill_base_id(), "s16" ) );
			game.addChildNode( new TypeNode( "skill_name_id", profile.getSkill_name_id(), "s16" ) );

			//游玩次数相关统计，待实现
			game.addChildNode( new TypeNode( "play_count", "100", "u32" ) );
			game.addChildNode( new TypeNode( "daily_count", "100", "u32" ) );
			game.addChildNode( new TypeNode( "play_chain", "100", "u32" ) );
			game.addChildNode( new TypeNode( "play_chain", "100", "u32" ) );
			game.addChildNode( new TypeNode( "week_count", "100", "u32" ) );
			game.addChildNode( new TypeNode( "week_play_count", "100", "u32" ) );
			game.addChildNode( new TypeNode( "week_chain", "100", "u32" ) );
			game.addChildNode( new TypeNode( "max_week_chain", "100", "u32" ) );

			//游戏设置
			game.addChildNode( new TypeNode( "hispeed", setting.getHispeed(), "s32" ) );
			game.addChildNode( new TypeNode( "lanespeed", setting.getLanespeed(), "u32" ) );
			game.addChildNode( new TypeNode( "gauge_option", setting.getGauge_option(), "u8" ) );
			game.addChildNode( new TypeNode( "ars_option", setting.getArs_option(), "u8" ) );
			game.addChildNode( new TypeNode( "notes_option", setting.getNote_option(), "u8" ) );
			game.addChildNode( new TypeNode( "early_late_disp", setting.getEarly_late_disp(), "u8" ) );
			game.addChildNode( new TypeNode( "draw_adjust", setting.getDraw_adjust(), "s32" ) );
			game.addChildNode( new TypeNode( "eff_c_left", setting.getEff_c_left(), "u8" ) );
			game.addChildNode( new TypeNode( "eff_c_right", setting.getEff_c_right(), "u8" ) );

			//PACKET   BLOCK 点数
			game.addChildNode( new TypeNode( "gamecoin_packet", profile.getPacket_point(), "u32" ) );
			game.addChildNode( new TypeNode( "gamecoin_block", profile.getBlock_point(), "u32" ) );

			//解锁的对象
			game.addChildNode( service.loadUnlockItemNode( refId.getContentValue() ) );

			//PARAM对象
			game.addChildNode( service.loadParamNode( refId.getContentValue() ) );

			//段位成绩
			game.addChildNode( service.loadCourseHistoryNode( refId.getContentValue() ) );

		} else {
			//没有数据
			Log.getInstance().print( "玩家数据不存在：" + refId.getContentValue() );
			final Node game = new Node( "game_5" );
			root.addChildNode( game );
			game.addChildNode( new TypeNode( "result", "1", "u8" ) );
		}

		return root;
	}

	/**
	 * 读取玩家成绩数据
	 * <p>
	 * TODO	尚未实现，目前不清楚数据结构
	 *
	 * @param call    请求的数据
	 * @param service 账号数据交互服务
	 * @return 响应的数据
	 */
	public static @Nullable
	Node handle_sv5_load_m( Node call, ProfileService service ) {
		final Node call_game = ( Node ) call.getFirstChildNode();
		final String refId = call_game.indexChildNode( "refid" ).getContentValue();

		final Node root = new Node( "response" );
		final Node game = new Node( "game" );
		root.addChildNode( game );

		game.addAttribute( "status", "0" );
		game.addChildNode( service.loadScoreNode( refId ) );

		try {
			System.out.println( NodeHelper.xml2Text( NodeHelper.note2Xml( root ) ) );
		} catch ( Exception e ) {
			e.printStackTrace();
		}

		return root;
	}

	/**
	 * 读取玩家好敌手数据
	 * <p>
	 * TODO	尚未实现，目前不清楚数据结构
	 *
	 * @param call 请求的数据
	 * @return 响应的数据
	 */
	public static @Nullable
	Node handle_sv5_load_r( Node call ) {
		final Node root = new Node( "response" );
		final Node game = new Node( "game" );
		root.addChildNode( game );

		return root;
	}

	/**
	 * 读取全服最高分数据
	 * <p>
	 * TODO	尚未实现，目前不清楚数据结构
	 *
	 * @param call 请求的数据
	 * @return 响应的数据
	 */
	public static @Nullable
	Node handle_sv5_hiscore( Node call ) {
		final Node root = new Node( "response" );
		final Node game = new Node( "game" );
		root.addChildNode( game );

		return root;
	}

	/**
	 * 处理报错信息
	 *
	 * @param call 请求的数据
	 * @return 响应的数据
	 */
	public static @Nullable
	Node handle_sv5_exception( Node call ) {
		//返回 dummy 数据
		final Node root = new Node( "response" );
		root.addChildNode( new Node( "game" ) );

		return root;
	}

	/**
	 * 处理一些基础的事件，例如：歌曲解锁、活动、限时段位... 等
	 * <p>
	 * TODO	全解功能
	 * TODO 成就，先不管
	 * TODO 段位通过率计算
	 *
	 * @param call 请求的数据
	 * @return 响应的数据
	 */
	public static @Nullable
	Node handle_sv5_common( Node call ) {
		final Node root = new Node( "response" );
		final Node game = new Node( "game" );
		root.addChildNode( game );

		//解锁歌曲
		final Node limitedMusic = new Node( "music_limited" );
		game.addChildNode( limitedMusic );
		if ( Config.FUNCTION_FORCE_UNLOCK_ALL_MUSIC ) {
			//TODO	歌曲全解
		}

		//启用的活动
		final Node event = new Node( "event" );
		game.addChildNode( event );
		enableEvent( event, "ACHIEVEMENT_ENABLE" );
		enableEvent( event, "CONTINUATION" );
		enableEvent( event, "EXTRACK_ENABLE" );
		enableEvent( event, "VOLFORCE_ENABLE" );
		enableEvent( event, "ICON_POLICY_BREAK" );
		enableEvent( event, "ICON_FLOOR_INFECTION" );
		enableEvent( event, "MATCHING_MODE" );
		enableEvent( event, "MATCHING_MODE_FREE_IP" );
		enableEvent( event, "EVENTDATE_ONIGO" );
		enableEvent( event, "SERIALCODE_JAPAN" );
		enableEvent( event, "APPEAL_CARD_UNLOCK" );
		enableEvent( event, "OMEGA_ENABLE" );
		enableEvent( event, "OMEGA_02_ENABLE" );
		enableEvent( event, "OMEGA_03_ENABLE" );
		enableEvent( event, "OMEGA_04_ENABLE" );
		enableEvent( event, "OMEGA_05_ENABLE" );
		enableEvent( event, "OMEGA_06_ENABLE" );
		enableEvent( event, "OMEGA_07_ENABLE" );
		enableEvent( event, "OMEGA_08_ENABLE" );
		enableEvent( event, "OMEGA_09_ENABLE" );
		enableEvent( event, "OMEGA_10_ENABLE" );
		enableEvent( event, "KONAMI_50TH_LOGO" );

		//段位启用
		//Node skillCourse = StaticContainer.getInstance().has( "skill_course_node" ) ? ( Node ) StaticContainer.getInstance().get( "skill_course_node" ) : null;
		final Node skillCourse = new Node( "skill_course" );
		final Course[] courses = loadCourseConfig();

		for ( Course course : courses ) {
			final Node info = new Node( "info" );
			skillCourse.addChildNode( info );

				/*
					各个字段的意义查看 CourseData.Course 的注释
				 */

			info.addChildNode( new TypeNode( "course_id", course.courseId, "s16" ) );
			info.addChildNode( new TypeNode( "skill_level", course.skillLevel, "s16" ) );
			info.addChildNode( new TypeNode( "season_id", course.seasonId, "s32" ) );
			info.addChildNode( new TypeNode( "season_name", course.seasonName ) );
			info.addChildNode( new TypeNode( "season_new_flg", course.seasonNewFlg, "bool" ) );
			info.addChildNode( new TypeNode( "course_name", course.courseName ) );
			info.addChildNode( new TypeNode( "course_type", course.courseType, "s16" ) );
			info.addChildNode( new TypeNode( "clear_rate", course.clearRate, "s32" ) );
			info.addChildNode( new TypeNode( "avg_score", course.avgScore, "u32" ) );
			info.addChildNode( new TypeNode( "skill_name_id", course.skillNameId, "s16" ) );
			info.addChildNode( new TypeNode( "matching_assist", course.matchingAssist, "bool" ) );
			info.addChildNode( new TypeNode( "gauge_type", course.gaugeType, "s16" ) );
			info.addChildNode( new TypeNode( "paseli_type", course.paseliType, "s16" ) );

			//存放段位曲目
			for ( int i = 0; i < course.tracks.length; i++ ) {
				final Node track = new Node( "track" );
				info.addChildNode( track );

				track.addChildNode( new TypeNode( "track_no", String.valueOf( i ), "s16" ) );
				track.addChildNode( new TypeNode( "music_id", String.valueOf( course.tracks[ i ].key ), "s32" ) );
				track.addChildNode( new TypeNode( "music_type", course.tracks[ i ].value, "u8" ) );
			}
		}
		game.addChildNode( skillCourse );
		game.addChildNode( new Node( "extend" ) );

		return root;
	}

	/**
	 * 应该是静止请求
	 *
	 * @param call 请求的数据
	 * @return 响应的数据
	 */
	public static @Nullable
	Node handle_sv5_frozen( Node call ) {
		final Node root = new Node( "response" );
		final Node game = new Node( "game_5" );
		game.addChildNode( new TypeNode( "result", "99", "u8" ) );
		root.addChildNode( game );
		return root;
	}

	/**
	 * 应该是请求登记商铺名称
	 *
	 * @param call 请求的数据
	 * @return 响应的数据
	 */
	public static @Nullable
	Node handle_sv5_shop( Node call ) {
		final Node root = new Node( "response" );
		final Node game = new Node( "game" );
		game.addChildNode( new TypeNode( "next_time", String.valueOf( 1000 * 5 * 60 ), "u32" ) );
		root.addChildNode( game );

		return root;
	}

	/**
	 * 读取段位配置
	 *
	 * @return 所有段位数据
	 */
	private static Course[] loadCourseConfig() {
		final byte[] bytes = IO.loadResource( true, "game/kfc/c2019100800/courses.json" );
		if ( bytes == null || bytes.length <= 0 ) {
			throw new RuntimeException( "段位数据配置不存在" );
		}

		final JsonArray jCourse = JsonParser.parseString( new String( bytes, Field.SHIFT_JIS ) ).getAsJsonObject().getAsJsonArray( "course" );

		JsonObject joTemp;
		JsonArray jaTemp;

		//读取段位数据
		String course_id, skill_level, season_id, season_name, season_new_flg, course_name, course_type, clear_rate, avg_score, skill_name_id, matching_assist, gauge_type, paseli_type;
		final Course[] result = new Course[ jCourse.size() ];
		for ( int k = 0; k < result.length; k++ ) {
			joTemp = jCourse.get( k ).getAsJsonObject();

			//这里对应 JSON 数据里的字段
			course_id = joTemp.get( "course_id" ).getAsString();
			skill_level = joTemp.get( "skill_level" ).getAsString();
			season_id = joTemp.get( "season_id" ).getAsString();
			season_name = joTemp.get( "season_name" ).getAsString();
			season_new_flg = joTemp.get( "season_new_flg" ).getAsString();
			course_name = joTemp.get( "course_name" ).getAsString();
			course_type = joTemp.get( "course_type" ).getAsString();
			clear_rate = joTemp.get( "clear_rate" ).getAsString();
			avg_score = joTemp.get( "avg_score" ).getAsString();
			skill_name_id = joTemp.get( "skill_name_id" ).getAsString();
			matching_assist = joTemp.get( "matching_assist" ).getAsString();
			gauge_type = joTemp.get( "gauge_type" ).getAsString();
			paseli_type = joTemp.get( "paseli_type" ).getAsString();

			jaTemp = joTemp.get( "track" ).getAsJsonArray();

			final Pair< Integer, String >[] tracks = new Pair[ jaTemp.size() ];

			for ( int i = 0; i < jaTemp.size(); i++ ) {
				joTemp = jaTemp.get( i ).getAsJsonObject();
				tracks[ i ] = new Pair<>( joTemp.get( "music_id" ).getAsInt(), joTemp.get( "music_type" ).getAsString() );
			}

			result[ k ] = new Course(
					course_id,
					skill_level,
					season_id,
					season_name,
					season_new_flg,
					course_name,
					course_type,
					clear_rate,
					avg_score,
					skill_name_id,
					matching_assist,
					gauge_type,
					paseli_type,
					tracks
			);
		}

		return result;
	}

	/**
	 * 读取段位配置
	 *
	 * @return 所有段位数据
	 */
	private static Course[] _loadCourseConfig() {

		final Course result = new Course(
				"1",
				"1",
				"1",
				"SKILL ANALYZER 第1回 Aコース",
				"0",
				"SKILL ANALYZER Level.01",
				"0",
				"10000",
				"1000000",
				"1",
				"1",
				"1",
				"0",
				new Pair[]{
						new Pair( "17", "1" ),
						new Pair( "922", "0" ),
						new Pair( "76", "1" )
				}
		);

		return new Course[]{ result };
	}

	/**
	 * 启用活动
	 *
	 * @param eventNode 活动数据父节点
	 * @param eventName 活动名称
	 */
	private static void enableEvent( Node eventNode, String eventName ) {
		final Node info = new Node( "info" );
		info.addChildNode( new TypeNode( "event_id", eventName ) );

		eventNode.addChildNode( info );
	}

}
