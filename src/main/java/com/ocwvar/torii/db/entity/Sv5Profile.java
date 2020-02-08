package com.ocwvar.torii.db.entity;

/**
 * SDVX 玩家数值信息
 */
public class Sv5Profile {

	private final String refId;
	private final String packet_point;
	private final String block_point;
	private final String blaster_energy;
	private final String blaster_count;
	private final String appeal_id;
	private final String skill_level;
	private final String skill_base_id;
	private final String skill_name_id;
	private final String player_name;
	private final String player_code;

	public Sv5Profile( String refId, String packet_point, String block_point, String blaster_energy, String blaster_count, String appeal_id, String skill_level, String skill_base_id, String skill_name_id, String player_name, String player_code ) {
		this.refId = refId;
		this.packet_point = packet_point;
		this.block_point = block_point;
		this.blaster_energy = blaster_energy;
		this.blaster_count = blaster_count;
		this.appeal_id = appeal_id;
		this.skill_level = skill_level;
		this.skill_base_id = skill_base_id;
		this.skill_name_id = skill_name_id;
		this.player_name = player_name;
		this.player_code = player_code;
	}

	/**
	 * @return 用户唯一标识 REF_ID
	 */
	public String getRefId() {
		return refId;
	}

	/**
	 * @return PACKET 点数
	 */
	public String getPacket_point() {
		return packet_point;
	}

	/**
	 * @return BLOCK 点数
	 */
	public String getBlock_point() {
		return block_point;
	}

	/**
	 * @return BLASTER 能量
	 */
	public String getBlaster_energy() {
		return blaster_energy;
	}

	/**
	 * @return BLASTER 次数
	 */
	public String getBlaster_count() {
		return blaster_count;
	}

	/**
	 * @return 玩家头像卡片
	 */
	public String getAppeal_id() {
		return appeal_id;
	}

	/**
	 * @return 段位等级ID
	 */
	public String getSkill_level() {
		return skill_level;
	}

	/**
	 * @return 段位基础ID
	 */
	public String getSkill_base_id() {
		return skill_base_id;
	}

	/**
	 * @return 段位名称ID
	 */
	public String getSkill_name_id() {
		return skill_name_id;
	}

	/**
	 * @return 玩家名
	 */
	public String getPlayer_name() {
		return player_name;
	}

	/**
	 * 用户码 如：1234-5678
	 *
	 * @return 用户码
	 */
	public String getPlayer_code() {
		return player_code;
	}
}
