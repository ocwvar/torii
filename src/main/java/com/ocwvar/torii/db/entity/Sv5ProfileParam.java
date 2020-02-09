package com.ocwvar.torii.db.entity;

public class Sv5ProfileParam {

	private final String refId;
	private final String id;
	private String type;
	private String param;

	public Sv5ProfileParam( String refId, String id, String type, String param ) {
		this.refId = refId;
		this.id = id;
		this.type = type;
		this.param = param;
	}

	public String getRefId() {
		return refId;
	}

	public String getId() {
		return id;
	}

	public String getType() {
		return type;
	}

	public String getParam() {
		return param;
	}

	public void setType( String type ) {
		this.type = type;
	}

	public void setParam( String param ) {
		this.param = param;
	}
}
