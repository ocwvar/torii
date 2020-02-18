package com.ocwvar.torii.service.game.kfc;

import com.ocwvar.torii.Configs;
import com.ocwvar.torii.db.dao.*;
import com.ocwvar.torii.db.entity.*;
import com.ocwvar.utils.IO;
import com.ocwvar.utils.Log;
import com.ocwvar.utils.annotation.NotNull;
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

	private final Sv5ProfileParamDao profileParamDao;
	private final UnlockItemDao unlockItemDao;
	private final Sv5ProfileDao profileDao;
	private final Sv5SettingDao settingDao;
	private final Sv5CourseDao courseDao;
	private final Sv5ScoreDao scoreDao;

	@Autowired
	public ProfileService( Sv5ProfileParamDao profileParamDao, UnlockItemDao unlockItemDao, Sv5ProfileDao profileDao, Sv5SettingDao settingDao, Sv5CourseDao courseDao, Sv5ScoreDao scoreDao ) {
		this.profileParamDao = profileParamDao;
		this.unlockItemDao = unlockItemDao;
		this.profileDao = profileDao;
		this.settingDao = settingDao;
		this.courseDao = courseDao;
		this.scoreDao = scoreDao;
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
	 * 通过 REF_ID 获取所有成绩
	 *
	 * @param refId REF_ID
	 * @return 成绩列表
	 */
	public List< Sv5Score > getAllScore( @NotNull String refId ) {
		return this.scoreDao.findAllScoreByRefId( refId );
	}

	/**
	 * 保存成绩，如果没有已存在的记录，则新建，否则更新成绩数据
	 *
	 * @param score 成绩对象
	 */
	public void saveScore( @NotNull Sv5Score score ) {
		//检查是否存在旧的记录
		final Sv5Score old = this.scoreDao.findScore( score.getRefId(), score.getMusic_id(), score.getMusic_type() );
		if ( old != null ) {
			//仅更新记录
			this.scoreDao.update( score );
			return;
		}

		this.scoreDao.createNew( score );
	}

	/**
	 * 保存段位成绩，如果没有已存在的记录，则新建，否则更新成绩数据
	 *
	 * @param course 段位数据
	 */
	public void saveCourse( @NotNull Sv5Course course ) {
		final Sv5Course old = this.courseDao.get( course.getRefId(), course.getSeason_id(), course.getCourse_id() );
		if ( old != null ) {
			this.courseDao.update( course );
		}

		this.courseDao.save( course );
	}

	/**
	 * 保存操作
	 *
	 * @param profile 数据对象
	 * @param setting 设置对象
	 */
	public void saveProfile( Sv5Profile profile, Sv5Setting setting ) {
		this.settingDao.save( setting );
		this.profileDao.save( profile );
	}

	/**
	 * 保存操作
	 *
	 * @param profile 数据对象
	 */
	public void saveProfile( Sv5Profile profile ) {
		this.profileDao.save( profile );
	}

	/**
	 * 保存解锁物品
	 *
	 * @param unlockItem 物品数据
	 */
	public void saveUnlockItem( UnlockItem unlockItem ) {
		this.unlockItemDao.add( unlockItem );
	}

	/**
	 * 创建新的玩家数据
	 *
	 * @param refId      REF_ID
	 * @param playerName 玩家名称
	 * @param playerCode 玩家代码
	 */
	public void createDefaultProfile( String refId, String playerName, String playerCode ) {
		this.profileDao.createDefault( refId, playerName, playerCode );
		this.settingDao.createDefault( refId );
	}

	/**
	 * 保存配置文件的参数，如果不存在则新建，否则修改已存在的 param 字段
	 *
	 * @param param 需要保存的参数
	 */
	public void saveProfileParam( Sv5ProfileParam param ) {
		final Sv5ProfileParam old = this.profileParamDao.findById( param.getRefId(), param.getId() );
		if ( old == null ) {
			this.profileParamDao.save( param );
			return;
		}

		old.setParam( param.getParam() );
		old.setType( param.getType() );

		this.profileParamDao.updateParam( old );
	}

	/**
	 * 读取所有配置参数
	 *
	 * @param refId REF_ID
	 * @return 配置参数列表
	 */
	public List< Sv5ProfileParam > loadAllProfileParam( String refId ) {
		return this.profileParamDao.getAllByRefId( refId );
	}

	/**
	 * 读取所有成绩
	 *
	 * @param refId REF_ID
	 * @return 成绩数据节点
	 */
	public Node loadScoreNode( String refId ) {
		final List< Sv5Score > result = this.scoreDao.findAllScoreByRefId( refId );
		final Node music = new Node( "music" );

		for ( Sv5Score score : result ) {
			final Node info = new Node( "info" );
			music.addChildNode( info );
			info.addChildNode(
					new ArrayTypeNode( "param", "u32",
							score.getMusic_id(),
							score.getMusic_type(),
							score.getScore(),
							score.getClear_type(),
							score.getScore_grade(),
							"0",
							"0",
							score.getBtn_rate(),
							score.getLong_rate(),
							score.getVol_rate(),
							"0",
							"0",
							"0",
							"0",
							"0",
							"0"
					) );
		}

		return music;
	}

	/**
	 * 读取段位成绩
	 *
	 * @param refId REF_ID
	 * @return 段位成绩数据节点
	 */
	public Node loadCourseHistoryNode( String refId ) {
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
	 *
	 * @param refId REF_ID
	 * @return 解锁数据节点
	 */
	public Node loadUnlockItemNode( String refId ) throws Exception {
		Log.getInstance().print( "读取用户解锁物品数据，是否强制全解：" + Configs.isForceUnlockAllItems() );

		final Node item;

		if ( Configs.isForceUnlockAllItems() ) {
			//如果强制全解物品，则这里直接返回全解数据，否则需要根据存入的数据返回
			item = new Node( "item" );

			//解锁领航员
			addForceCrewData( item );

			return item;
		} else {
			item = new Node( "item" );

			final List< UnlockItem > result = this.unlockItemDao.findAllByRefId( refId );
			Log.getInstance().print( "用户已解锁物品数量：" + result.size() );

			for ( UnlockItem unlockItem : result ) {
				final Node info = new Node( "info" );
				item.addChildNode( info );
				info.addChildNode( new TypeNode( "type", unlockItem.getType(), "u8" ) );
				info.addChildNode( new TypeNode( "id", unlockItem.getId(), "u32" ) );
				info.addChildNode( new TypeNode( "param", unlockItem.getParam(), "u32" ) );
			}
		}

		return item;
	}

	/**
	 * 读取 PARAM 数据
	 *
	 * @param refId REF_ID
	 * @return PARAM 节点
	 */
	public Node loadParamNode( String refId ) {
		final List< Sv5ProfileParam > result = this.profileParamDao.getAllByRefId( refId );
		Log.getInstance().print( "正在读取用户 PARAM 参数数据，数量：" + result.size() );

		final Node root = new Node( "param" );
		for ( Sv5ProfileParam profileParam : result ) {
			final Node info = new Node( "info" );
			root.addChildNode( info );
			info.addChildNode( new TypeNode( "type", profileParam.getType(), "s32" ) );
			info.addChildNode( new TypeNode( "id", profileParam.getId(), "s32" ) );
			info.addChildNode( new ArrayTypeNode( 0, "param", "s32", profileParam.getParam() ) );
		}
		return root;
	}

	/**
	 * 添加强制解锁的领航员数据
	 *
	 * @param item 添加数据后的 item 节点
	 */
	private void addForceCrewData( Node item ) throws Exception {
		byte[] bytes = IO.loadResource( "generator/CharacterUnlockJsonData/data.json" );
		if ( bytes == null || bytes.length <= 0 ) {
			Log.getInstance().print( "没有预制领航员解锁数据" );
			return;
		}

		//TODO	目前需要清楚领航员的id从哪里能获得，光是靠穷举的方式不可靠

		/*final Node info = new Node( "info" );
		item.addChildNode( info );
		info.addChildNode( new TypeNode( "type", "11", "u8" ) );
		info.addChildNode( new TypeNode( "id", "103", "u32" ) );
		info.addChildNode( new TypeNode( "param", "1", "u32" ) );*/

		/*final JsonArray crewArray = JsonParser.parseString( new String( bytes ) ).getAsJsonArray();
		JsonObject jsonObject;
		for ( JsonElement element : crewArray ) {
			jsonObject = element.getAsJsonObject();

			final Node info = new Node( "info" );
			item.addChildNode( info );
			info.addChildNode( new TypeNode( "type", "11", "u8" ) );
			info.addChildNode( new TypeNode( "id", jsonObject.get( "id" ).getAsString(), "u32" ) );
			info.addChildNode( new TypeNode( "param", "1", "u32" ) );
		}*/

		for ( int i = 0; i < 200; i++ ) {
			final Node info = new Node( "info" );
			item.addChildNode( info );
			info.addChildNode( new TypeNode( "type", "11", "u8" ) );
			info.addChildNode( new TypeNode( "id", String.valueOf( i ), "u32" ) );
			info.addChildNode( new TypeNode( "param", "1", "u32" ) );
		}
	}

}
