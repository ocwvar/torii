package com.ocwvar.torii.service.game.kfc;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ocwvar.torii.Config;
import com.ocwvar.torii.data.StaticContainer;
import com.ocwvar.torii.db.dao.Sv5CourseDao;
import com.ocwvar.torii.db.dao.Sv5ProfileDao;
import com.ocwvar.torii.db.dao.Sv5SettingDao;
import com.ocwvar.torii.db.entity.Sv5Course;
import com.ocwvar.torii.db.entity.Sv5Profile;
import com.ocwvar.torii.db.entity.Sv5Setting;
import com.ocwvar.torii.utils.IO;
import com.ocwvar.utils.Log;
import com.ocwvar.utils.annotation.Nullable;
import com.ocwvar.xml.node.ArrayTypeNode;
import com.ocwvar.xml.node.Node;
import com.ocwvar.xml.node.TypeNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 玩家数据处理服务
 */
@Service
public class ProfileService {

	private final Sv5ProfileDao profileDao;
	private final Sv5SettingDao settingDao;
	private final Sv5CourseDao courseDao;

	@Autowired
	public ProfileService( Sv5ProfileDao profileDao, Sv5SettingDao settingDao, Sv5CourseDao courseDao ) {
		this.profileDao = profileDao;
		this.settingDao = settingDao;
		this.courseDao = courseDao;
	}

	/**
	 * 获取游戏数值存储数据
	 *
	 * @param refId REF_ID
	 * @return 数值对象，如果没有则返回 NULL
	 */
	public @Nullable
	Sv5Profile getProfile( String refId ) {
		return this.profileDao.findByRefId( refId );
	}

	/**
	 * 获取玩家设置储存数据
	 *
	 * @param refId REF_ID
	 * @return 设置对象，如果没有则返回 NULL
	 */
	public Sv5Setting getSetting( String refId ) {
		return this.settingDao.findByRefId( refId );
	}

	/**
	 * 保存操作
	 *
	 * @param profile 数据对象
	 * @param setting 设置对象
	 */
	public void save( Sv5Profile profile, Sv5Setting setting ) {
		this.settingDao.save( setting );
		this.profileDao.save( profile );
	}

	/**
	 * 创建新的玩家数据
	 *
	 * @param refId      REF_ID
	 * @param playerName 玩家名称
	 * @param playerCode 玩家代码
	 */
	public void createDefault( String refId, String playerName, String playerCode ) {
		this.profileDao.createDefault( refId, playerName, playerCode );
		this.settingDao.createDefault( refId );
	}

	/**
	 * 读取段位成绩
	 *
	 * @param refId REF_ID
	 * @return 段位成绩数据节点
	 */
	public Node loadCourseHistory( String refId ) {
		Log.getInstance().print( "读取用户段位数据" );

		final Node root = new Node( "skill" );
		final List< Sv5Course > result = this.courseDao.getList( refId );
		Log.getInstance().print( "用户段位数据量：" + ( result == null ? 0 : result.size() ) );

		if ( result != null && result.size() > 0 ) {
			for ( Sv5Course data : result ) {
				final Node course = new Node( "course" );
				root.addChildNode( course );
				course.addChildNode( new TypeNode( "ssnid", data.getSeason_id(), "s16" ) );
				course.addChildNode( new TypeNode( "crsid", data.getCourse_id(), "s16" ) );
				course.addChildNode( new TypeNode( "sc", data.getScore(), "s32" ) );
				course.addChildNode( new TypeNode( "ct", data.getClear_type(), "s16" ) );
				course.addChildNode( new TypeNode( "gr", data.getGrade(), "s16" ) );
				course.addChildNode( new TypeNode( "ar", data.getAchievement_rate(), "s16" ) );
				course.addChildNode( new TypeNode( "cnt", data.getCnt(), "s16" ) );
			}
		}

		return root;
	}

	/**
	 * 读取解锁的 ITEM ，包含头像卡、领航员、歌曲
	 * TODO 目前为直接读取全解数据，日后需要添加读取保存的数据
	 *
	 * @param refId REF_ID
	 * @return 解锁数据节点
	 */
	public Node loadUnlockItem( String refId ) {
		Log.getInstance().print( "读取用户解锁物品数据，是否强制全解：" + Config.FUNCTION_FORCE_UNLOCK_ITEMS );

		final Node item;

		if ( Config.FUNCTION_FORCE_UNLOCK_ITEMS ) {
			//如果强制全解物品，则这里直接返回全解数据，否则需要根据存入的数据返回

			if ( StaticContainer.getInstance().has( "force_unlock_item" ) ) {
				return ( Node ) StaticContainer.getInstance().get( "unlock_item" );
			}

			item = new Node( "item" );
			final byte[] bytes = IO.loadResource( true, "game/kfc/force_unlock_items.json" );
			if ( bytes == null || bytes.length <= 0 ) {
				return item;
			}

			final JsonArray jItems = JsonParser.parseString( new String( bytes ) ).getAsJsonArray();
			JsonObject temp;
			for ( JsonElement element : jItems ) {
				temp = ( JsonObject ) element;

				final Node info = new Node( "info" );
				item.addChildNode( info );

			/*
				TYPE 对应
				GAME_CATALOG_TYPE_SONG = 0
				GAME_CATALOG_TYPE_APPEAL_CARD = 1
				GAME_CATALOG_TYPE_CREW = 4

				ID 对应解锁对象的ID
				PARAM 对应参数，传 1 即可
			 */
				info.addChildNode( new TypeNode( "type", temp.get( "type" ).getAsString(), "u8" ) );
				info.addChildNode( new TypeNode( "id", temp.get( "id" ).getAsString(), "u32" ) );
				info.addChildNode( new TypeNode( "param", temp.get( "param" ).getAsString(), "u32" ) );
			}

			StaticContainer.getInstance().set( "unlock_item", item );
			return item;
		} else {
			item = new Node( "item" );
		}

		return item;
	}

	/**
	 * 读取 PARAM 数据
	 *
	 * @param refId REF_ID
	 * @return PARAM 节点
	 */
	public Node loadParam( String refId ) {
		Log.getInstance().print( "读取用户 PARAM 参数" );

		//这里目前不清楚用途，先返回默认值
		final Node param = new Node( "param" );

		final Node info_1 = new Node( "info" );
		info_1.addChildNode( new TypeNode( "type", "2", "s32" ) );
		info_1.addChildNode( new TypeNode( "id", "1", "s32" ) );
		info_1.addChildNode( new ArrayTypeNode( 0, "param", "s32", "0 0 0 1529163586 18011602 18011602 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 59 0 0 0 0 1" ) );
		param.addChildNode( info_1 );

		final Node info_2 = new Node( "info" );
		info_2.addChildNode( new TypeNode( "type", "4", "s32" ) );
		info_2.addChildNode( new TypeNode( "id", "4", "s32" ) );
		info_2.addChildNode( new ArrayTypeNode( 0, "param", "s32", "8332 8582 2292" ) );
		param.addChildNode( info_2 );

		final Node info_3 = new Node( "info" );
		info_3.addChildNode( new TypeNode( "type", "4", "s32" ) );
		info_3.addChildNode( new TypeNode( "id", "5", "s32" ) );
		info_3.addChildNode( new ArrayTypeNode( 0, "param", "s32", "416 410 331" ) );
		param.addChildNode( info_3 );

		final Node info_4 = new Node( "info" );
		info_4.addChildNode( new TypeNode( "type", "5", "s32" ) );
		info_4.addChildNode( new TypeNode( "id", "0", "s32" ) );
		info_4.addChildNode( new ArrayTypeNode( 0, "param", "s32", "1 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 1 400 15 6 5 423 363 186 0 1 4 1 2 2 10 3 5 7 5 29 45 32 19 77 109 48 1 0 0 7 5 1 4 1 1 12 2 12 17 10 7 0 8 5 8 1 13 11 16 11 2 1 2 3" ) );
		param.addChildNode( info_4 );

		final Node info_5 = new Node( "info" );
		info_5.addChildNode( new TypeNode( "type", "7", "s32" ) );
		info_5.addChildNode( new TypeNode( "id", "10", "s32" ) );
		info_5.addChildNode( new ArrayTypeNode( 0, "param", "s32", "294910 6408064 9429248 0 131328 32 0 1075838976" ) );
		param.addChildNode( info_5 );

		final Node info_6 = new Node( "info" );
		info_6.addChildNode( new TypeNode( "type", "8", "s32" ) );
		info_6.addChildNode( new TypeNode( "id", "2", "s32" ) );
		info_6.addChildNode( new ArrayTypeNode( 0, "param", "s32", "2528" ) );
		param.addChildNode( info_6 );

		return param;
	}

}
