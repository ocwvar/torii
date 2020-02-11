package com.ocwvar.torii;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * 相关的字段
 */
public class Field {

	//一些状态码，意思如名
	public static final String CODE_SUCCESS = "0";
	public static final String CODE_NO_PROFILE = "109";
	public static final String CODE_NOT_ALLOWED = "110";
	public static final String CODE_NOT_REGISTERED = "112";
	public static final String CODE_INVALID_PIN = "116";

	//SDVX 歌曲解锁状态
	public static final String SDVX_LIMIT_LOCKED = "1";
	public static final String SDVX_LIMIT_UNLOCKABLE = "2";
	public static final String SDVX_LIMIT_UNLOCKED = "3";

	public static final Charset UTF8 = StandardCharsets.UTF_8;
	public static final Charset SHIFT_JIS = Charset.forName( "cp932" );

	/**
	 * 请求头字段：X-Eamuse-Info
	 */
	public static final String HEADER_X_Eamuse_Info = "X-Eamuse-Info";

	/**
	 * 请求头字段：X-Compress
	 */
	public static final String HEADER_COMPRESS = "X-Compress";

	/**
	 * 请求头字段：X-Compress，参数 LZ77 加密方式
	 */
	public static final String HEADER_COMPRESS_TYPE_LZ77 = "lz77";

	/**
	 * 字段：无
	 */
	public static final String NONE = "none";

}
