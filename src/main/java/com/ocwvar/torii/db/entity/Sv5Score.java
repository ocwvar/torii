package com.ocwvar.torii.db.entity;

public class Sv5Score {
	private final String refId;
	private final String music_id;
	private final String music_type;
	private final String score;
	private final String clear_type;
	private final String score_grade;
	private final String max_chain;
	private final String critical;
	private final String near;
	private final String error;
	private final String gauge_type;
	private final String effective_rate;
	private final String btn_rate;
	private final String long_rate;
	private final String vol_rate;
	private final String mode;
	private final String notes_option;

	public Sv5Score( String refId, String music_id, String music_type, String score, String clear_type, String score_grade, String max_chain, String critical, String near, String error, String gauge_type, String effective_rate, String btn_rate, String long_rate, String vol_rate, String mode, String notes_option ) {
		this.refId = refId;
		this.music_id = music_id;
		this.music_type = music_type;
		this.score = score;
		this.clear_type = clear_type;
		this.score_grade = score_grade;
		this.max_chain = max_chain;
		this.critical = critical;
		this.near = near;
		this.error = error;
		this.gauge_type = gauge_type;
		this.effective_rate = effective_rate;
		this.btn_rate = btn_rate;
		this.long_rate = long_rate;
		this.vol_rate = vol_rate;
		this.mode = mode;
		this.notes_option = notes_option;
	}

	/**
	 * @return REF_ID
	 */
	public String getRefId() {
		return refId;
	}

	/**
	 * @return 音乐ID
	 */
	public String getMusic_id() {
		return music_id;
	}

	/**
	 * @return 谱面类型
	 */
	public String getMusic_type() {
		return music_type;
	}

	/**
	 * @return 分数
	 */
	public String getScore() {
		return score;
	}

	/**
	 * @return 通过类型
	 */
	public String getClear_type() {
		return clear_type;
	}

	/**
	 * @return 得分等级
	 */
	public String getScore_grade() {
		return score_grade;
	}

	/**
	 * @return 最大连击数
	 */
	public String getMax_chain() {
		return max_chain;
	}

	/**
	 * @return Critical Note 数量
	 */
	public String getCritical() {
		return critical;
	}

	/**
	 * @return Near Note 数量
	 */
	public String getNear() {
		return near;
	}

	/**
	 * @return Error Note 数量
	 */
	public String getError() {
		return error;
	}

	/**
	 * @return 判定类型
	 */
	public String getGauge_type() {
		return gauge_type;
	}

	/**
	 * @return NOTE 模式
	 */
	public String getNotes_option() {
		return notes_option;
	}

	public String getEffective_rate() {
		return effective_rate;
	}

	public String getBtn_rate() {
		return btn_rate;
	}

	public String getLong_rate() {
		return long_rate;
	}

	public String getVol_rate() {
		return vol_rate;
	}

	public String getMode() {
		return mode;
	}

}
