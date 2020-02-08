package com.ocwvar.torii.controller.game.kfc.c2019100800;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ocwvar.torii.Config;
import com.ocwvar.torii.data.StaticContainer;
import com.ocwvar.torii.data.game.kfc.Course;
import com.ocwvar.torii.db.entity.Sv5Profile;
import com.ocwvar.torii.db.entity.Sv5Setting;
import com.ocwvar.torii.service.game.kfc.ProfileService;
import com.ocwvar.torii.utils.IO;
import com.ocwvar.utils.Log;
import com.ocwvar.utils.Pair;
import com.ocwvar.utils.annotation.Nullable;
import com.ocwvar.xml.node.Node;
import com.ocwvar.xml.node.TypeNode;

public class RequestHandler {

	/**
	 * 读取玩家数据
	 * <p>
	 * TODO	段位记录存储获取
	 * TODO 游玩次数统计存储
	 *
	 * @param call    请求的数据
	 * @param service 配置文件管理服务
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
		final Node game = new Node( "game" );
		root.addChildNode( game );

		if ( profile != null ) {

			//有已保存的数据
			Log.getInstance().print( "已存在的玩家：" + refId.getContentValue() );
			game.addChildNode( new TypeNode( "result", "0", "u8" ) );
			game.addChildNode( new TypeNode( "name", profile.getPlayer_name() ) );
			game.addChildNode( new TypeNode( "code", profile.getPlayer_code() ) );
			game.addChildNode( new TypeNode( "creator_id", "0", "u32" ) );

			//最后保存的状态
			game.addChildNode( new TypeNode( "last_music_id", setting.getLast_music_id(), "u32" ) );
			game.addChildNode( new TypeNode( "last_music_type", setting.getLast_music_type(), "u8" ) );
			game.addChildNode( new TypeNode( "sort_type", setting.getSort_type(), "u8" ) );
			game.addChildNode( new TypeNode( "narrow_down", setting.getNarrow_down(), "u8" ) );
			game.addChildNode( new TypeNode( "headphone", setting.getHeadphone(), "u8" ) );
			game.addChildNode( new TypeNode( "appeal_id", profile.getAppeal_id(), "u16" ) );

			//当前段位
			game.addChildNode( new TypeNode( "skill_level", profile.getSkill_level(), "s16" ) );
			game.addChildNode( new TypeNode( "skill_base_id", profile.getSkill_base_id(), "s16" ) );
			game.addChildNode( new TypeNode( "skill_name_id", profile.getSkill_name_id(), "s16" ) );

			//游玩次数相关统计，待实现
			game.addChildNode( new TypeNode( "play_count", "100", "u32" ) );
			game.addChildNode( new TypeNode( "daily_count", "100", "u32" ) );
			game.addChildNode( new TypeNode( "play_chain", "100", "u32" ) );
			game.addChildNode( new TypeNode( "play_chain", "0", "u32" ) );
			game.addChildNode( new TypeNode( "week_count", "0", "u32" ) );
			game.addChildNode( new TypeNode( "week_play_count", "0", "u32" ) );
			game.addChildNode( new TypeNode( "week_chain", "0", "u32" ) );
			game.addChildNode( new TypeNode( "max_week_chain", "0", "u32" ) );

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
			game.addChildNode( service.loadUnlockItem( refId.getContentValue() ) );

			//PARAM对象
			game.addChildNode( service.loadParam( refId.getContentValue() ) );

			//段位成绩
			game.addChildNode( service.loadCourseHistory( refId.getContentValue() ) );
		} else {
			//没有数据
			Log.getInstance().print( "玩家数据不存在：" + refId.getContentValue() );
			game.addChildNode( new TypeNode( "result", "1", "u8" ) );
		}

		return root;
	}

	/**
	 * 读取玩家成绩数据
	 * <p>
	 * TODO	尚未实现，目前不清楚数据结构
	 *
	 * @param call 请求的数据
	 * @return 响应的数据
	 */
	public static @Nullable
	Node handle_sv5_load_m( Node call ) {
		final Node root = new Node( "response" );
		final Node game = new Node( "game" );
		root.addChildNode( game );

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

		//段位启用，有缓存好的数据，就不用生成了
		Node skillCourse = StaticContainer.getInstance().has( "skill_course_node" ) ? ( Node ) StaticContainer.getInstance().get( "skill_course_node" ) : null;
		if ( skillCourse == null ) {
			skillCourse = new Node( "skill_course" );

			Log.getInstance().print( "正在首次加载段位数据..." );
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

			StaticContainer.getInstance().set( "skill_course_node", skillCourse );
		}
		game.addChildNode( skillCourse );

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
		game.addChildNode( new TypeNode( "result", "0", "u8" ) );
		root.addChildNode( game );
		return root;
	}

	/**
	 * 应该是商店之类的
	 *
	 * @param call 请求的数据
	 * @return 响应的数据
	 */
	public static @Nullable
	Node handle_sv5_shop( Node call ) {
		final Node root = new Node( "response" );
		final Node game = new Node( "game" );
		game.addChildNode( new TypeNode( "nxt_time", String.valueOf( 1000 * 5 * 60 ), "u32" ) );
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

		final JsonArray jCourse = JsonParser.parseString( new String( bytes ) ).getAsJsonObject().getAsJsonArray( "course" );

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
