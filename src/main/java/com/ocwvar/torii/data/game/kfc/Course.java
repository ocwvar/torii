package com.ocwvar.torii.data.game.kfc;


import com.ocwvar.utils.Pair;

/**
 * 段位数据
 * <p>
 * SEASON_ID
 * 段位所属活动ID
 * 第1回 Aコース = 0
 * 第1回 Bコース = 1
 * 第1回 Cコース = 2
 * 第2回 Aコース = 3
 * 第2回 Bコース = 4
 * 第3回 = 5
 * 第4回 Aコース = 6
 * 第4回 Bコース = 7
 * 第4回 Cコース = 8
 * 第5回 = 9
 * 第6回 Aコース = 10
 * 第6回 Bコース = 11
 * 第6回 Cコース = 12
 * The 6th KAC挑戦コース【体】 = 13
 * The 6th KAC挑戦コース【技】 = 14
 * The 6th KAC挑戦コース【心】 = 15
 * 天下一 (梅) = 16
 * 天下一 (竹) = 17
 * 天下一 (松) = 18
 * BEMANI MASTER KOREA 2017 = 19
 * The 7th KACチャレンジコース【猛虎】 = 20
 * The 7th KACチャレンジコース【餓狼】 = 21
 * The 8th KACチャレンジコース【阿修羅】 = 22
 * The 8th KACエンジョイコース【阿修羅】 = 23
 * 第5回 Bコース",
 * <p>
 * SEASON_NAME
 * 活动名称
 * <p>
 * COURSE_ID
 * 段位ID
 * <p>
 * COURSE_NAME
 * 段位分类名称
 * <p>
 * COURSE_TYPE
 * 段位类型，在普通段位中为 0 ，但若 SKILL_LEVEL == 0 的话，则此段位将会归类到 OTHER，否则将会根据 SKILL_LEVEL
 * 进行等级分类，如： level01、level02……
 * COURSE_TYPE = 2 (KAC 6th.)
 * COURSE_TYPE = 3 (TENKAICHI mode.)
 * COURSE_TYPE = 4 (KAC 7th.)
 * COURSE_TYPE = 5 (KAC 8th.)
 * <p>
 * SKILL_LEVEL
 * 段位等级 0 ~ 12
 * <p>
 * SKILL_NAME_ID
 * 段位可获得的名称ID
 * LV_01 = 1
 * LV_02 = 2
 * LV_03 = 3
 * LV_04 = 4
 * LV_05 = 5
 * LV_06 = 6
 * LV_07 = 7
 * LV_08 = 8
 * LV_09 = 9
 * LV_10 = 10
 * LV_11 = 11
 * LV_INF = 12
 * KAC_6TH_BODY = 13
 * KAC_6TH_TECHNOLOGY = 14
 * KAC_6TH_HEART = 15
 * TENKAICHI = 16
 * MUSIC_FESTIVAL = 17
 * YELLOWTAIL = 18
 * BMK2017 = 19
 * KAC_7TH_TIGER = 20
 * KAC_7TH_WOLF = 21
 * RIKKA = 22
 * KAC_8TH = 23
 * <p>
 * TRACKS
 * 段位需要通过的歌曲
 * Integer：歌曲ID
 * String：谱面难度类型 ↓
 * NOVICE = 0
 * ADVANCED = 1
 * EXHAUST = 2
 * INFINITE = 3
 * MAXIMUM = 4
 */
public class Course {

	public final String courseId;
	public final String skillLevel;
	public final String seasonId;
	public final String seasonName;
	public final String seasonNewFlg;
	public final String courseName;
	public final String courseType;
	public final String skillNameId;
	public final String matchingAssist;
	public final String gaugeType;
	public final String paseliType;
	public final Pair< Integer, String >[] tracks;

	public Course( String courseId, String skillLevel, String seasonId, String seasonName, String seasonNewFlg, String courseName, String courseType, String skillNameId, String matchingAssist, String gaugeType, String paseliType, Pair< Integer, String >[] tracks ) {
		this.courseId = courseId;
		this.skillLevel = skillLevel;
		this.seasonId = seasonId;
		this.seasonName = seasonName;
		this.seasonNewFlg = seasonNewFlg;
		this.courseName = courseName;
		this.courseType = courseType;
		this.skillNameId = skillNameId;
		this.matchingAssist = matchingAssist;
		this.gaugeType = gaugeType;
		this.paseliType = paseliType;
		this.tracks = tracks;
	}
}
