package com.ocwvar.torii.db.entity;

public class Sv5Setting {

	private final String refId;
	private final String last_music_id;
	private final String last_music_type;
	private final String sort_type;
	private final String narrow_down;
	private final String headphone;
	private final String gauge_option;
	private final String ars_option;
	private final String early_late_disp;
	private final String note_option;
	private final String eff_c_left;
	private final String eff_c_right;
	private final String lanespeed;
	private final String hispeed;
	private final String draw_adjust;

	public Sv5Setting( String refId, String last_music_id, String last_music_type, String sort_type, String narrow_down, String headphone, String gauge_option, String ars_option, String early_late_disp, String note_option, String eff_c_left, String eff_c_right, String lanespeed, String hispeed, String draw_adjust ) {
		this.refId = refId;
		this.last_music_id = last_music_id;
		this.last_music_type = last_music_type;
		this.sort_type = sort_type;
		this.narrow_down = narrow_down;
		this.headphone = headphone;
		this.gauge_option = gauge_option;
		this.ars_option = ars_option;
		this.early_late_disp = early_late_disp;
		this.note_option = note_option;
		this.eff_c_left = eff_c_left;
		this.eff_c_right = eff_c_right;
		this.lanespeed = lanespeed;
		this.hispeed = hispeed;
		this.draw_adjust = draw_adjust;
	}

	/**
	 * @return 用户唯一标识 REF_ID
	 */
	public String getRefId() {
		return refId;
	}

	/**
	 * @return 游玩的最后一首歌ID
	 */
	public String getLast_music_id() {
		return last_music_id;
	}

	/**
	 * @return 游玩的最后一首歌的难度
	 */
	public String getLast_music_type() {
		return last_music_type;
	}

	/**
	 * @return 排序类型
	 */
	public String getSort_type() {
		return sort_type;
	}

	/**
	 * @return 不懂
	 */
	public String getNarrow_down() {
		return narrow_down;
	}

	/**
	 * @return 耳机音量
	 */
	public String getHeadphone() {
		return headphone;
	}

	/**
	 * @return 血槽类型
	 */
	public String getGauge_option() {
		return gauge_option;
	}

	/**
	 * @return ARS模式
	 */
	public String getArs_option() {
		return ars_option;
	}

	/**
	 * @return 延迟
	 */
	public String getEarly_late_disp() {
		return early_late_disp;
	}

	/**
	 * @return NOTE 选项类型，随机、镜像 等
	 */
	public String getNote_option() {
		return note_option;
	}

	/**
	 * @return 不懂
	 */
	public String getEff_c_left() {
		return eff_c_left;
	}

	/**
	 * @return 不懂
	 */
	public String getEff_c_right() {
		return eff_c_right;
	}

	/**
	 * @return 不懂
	 */
	public String getLanespeed() {
		return lanespeed;
	}

	/**
	 * @return Hi-Speed
	 */
	public String getHispeed() {
		return hispeed;
	}

	/**
	 * @return 绘制延迟
	 */
	public String getDraw_adjust() {
		return draw_adjust;
	}
}
