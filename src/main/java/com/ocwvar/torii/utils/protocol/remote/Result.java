package com.ocwvar.torii.utils.protocol.remote;

import com.ocwvar.utils.annotation.Nullable;

public class Result {
	private final boolean hasException;
	private final boolean isEncodeRequest;
	private final String exceptionMessage;
	private final byte[] result;
	private String tag = null;

	public Result( boolean hasException, boolean isEncodeRequest, String exceptionMessage, byte[] result ) {
		this.hasException = hasException;
		this.isEncodeRequest = isEncodeRequest;
		this.exceptionMessage = exceptionMessage;
		this.result = result;
	}

	/**
	 * @return 任务TAG
	 */
	public String getTag() {
		return tag;
	}

	/**
	 * @param tag 任务TAG
	 */
	public void setTag( String tag ) {
		this.tag = tag;
	}

	/**
	 * @return 是否出现了异常
	 */
	public boolean hasException() {
		return hasException;
	}

	/**
	 * @return 是否为加密请求，否则为解密请求
	 */
	public boolean isEncodeRequest() {
		return isEncodeRequest;
	}

	/**
	 * @return 结果数据，如果出现异常则为 NULL
	 */
	public @Nullable
	byte[] getResult() {
		return result;
	}

	/**
	 * @return 异常信息，如果没有异常则为 NULL
	 */
	public @Nullable
	String getExceptionMessage() {
		return exceptionMessage;
	}
}
