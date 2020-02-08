package com.ocwvar.torii.db.entity;

public class Card {

	private final String refId;
	private final String rawId;
	private final String pin;

	public Card( String refId, String rawId, String pin ) {
		this.refId = refId;
		this.rawId = rawId;
		this.pin = pin;
	}

	/**
	 * @return E004加密卡号
	 */
	public String getRefId() {
		return refId;
	}

	/**
	 * @return E004卡号
	 */
	public String getRawId() {
		return rawId;
	}

	/**
	 * @return PIN密码
	 */
	public String getPin() {
		return pin;
	}

}
