package com.ocwvar.torii.db.entity;

public class Sv5Course {

	private final String season_id;
	private final String course_id;
	private final String score;
	private final String clear_type;
	private final String grade;
	private final String achievement_rate;
	private final String cnt;
	private final String refId;

	public Sv5Course( String season_id, String course_id, String score, String clear_type, String grade, String achievement_rate, String cnt, String refId ) {
		this.season_id = season_id;
		this.course_id = course_id;
		this.score = score;
		this.clear_type = clear_type;
		this.grade = grade;
		this.achievement_rate = achievement_rate;
		this.cnt = cnt;
		this.refId = refId;
	}

	/**
	 * @return 段位 SEASON_ID
	 */
	public String getSeason_id() {
		return season_id;
	}

	/**
	 * @return 段位课题ID
	 */
	public String getCourse_id() {
		return course_id;
	}

	/**
	 * @return 段位课题得分
	 */
	public String getScore() {
		return score;
	}

	/**
	 * @return 课题通过类型
	 */
	public String getClear_type() {
		return clear_type;
	}

	/**
	 * 1D 2C 3B 4A 5AA 6AAA...
	 *
	 * @return 课题通过等级
	 */
	public String getGrade() {
		return grade;
	}

	/**
	 * @return 通过率
	 */
	public String getAchievement_rate() {
		return achievement_rate;
	}

	/**
	 * @return 未知，可能为考课题次数
	 */
	public String getCnt() {
		return cnt;
	}

	/**
	 * @return REF_ID
	 */
	public String getRefId() {
		return refId;
	}
}
