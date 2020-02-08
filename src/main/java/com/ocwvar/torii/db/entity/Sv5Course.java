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
}
