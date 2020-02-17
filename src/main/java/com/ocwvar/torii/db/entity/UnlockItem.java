package com.ocwvar.torii.db.entity;

public class UnlockItem {

	private final String refId;
	private final String id;
	private final String type;
	private final String param;

	public UnlockItem( String refId, String id, String type, String param ) {
		this.refId = refId;
		this.id = id;
		this.type = type;
		this.param = param;
	}

	/**
	 * @return REF_ID
	 */
	public String getRefId() {
		return refId;
	}

	/**
	 * @return 物品ID
	 */
	public String getId() {
		return id;
	}

	/**
	 * GAME_CATALOG_TYPE_SONG = 0
	 * GAME_CATALOG_TYPE_APPEAL_CARD = 1
	 * GAME_CATALOG_TYPE_CREW = 4
	 *
	 * @return 物品类型
	 */
	public String getType() {
		return type;
	}

	/**
	 * @return 物品参数
	 */
	public String getParam() {
		return param;
	}
}
