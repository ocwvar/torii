package com.ocwvar.torii.db.entity;

public class Paseli {

	private final String rawId;
	private final String balance;
	private final String acid;
	private final String acname;
	private final boolean infinite_balance;

	public Paseli( String rawId, String balance, String acid, String acname, int infinite_balance ) {
		this.rawId = rawId;
		this.balance = balance;
		this.acid = acid;
		this.acname = acname;
		this.infinite_balance = infinite_balance > 0;
	}

	/**
	 * @return 卡片的RawID
	 */
	public String getRawId() {
		return rawId;
	}

	/**
	 * @return PASELI余额
	 */
	public String getBalance() {
		return balance;
	}

	/**
	 * @return 尚不清楚
	 */
	public String getAcid() {
		return acid;
	}

	/**
	 * @return 尚不清楚
	 */
	public String getAcname() {
		return acname;
	}

	/**
	 * @return 是否无限PASELI余额
	 */
	public boolean isInfiniteBalance() {
		return infinite_balance;
	}
}
