package com.ocwvar.torii;

/**
 * 配置
 */
public class Config {

	/**
	 * 是否支持 PASELI，默认 True
	 */
	public static final boolean FUNCTION_PASELI_ENABLE = true;

	/**
	 * 是否强制全解物品 (头像、领航员 等)
	 */
	public static final boolean FUNCTION_FORCE_UNLOCK_ITEMS = true;

	/**
	 * 是否强制全解歌曲，默认 False
	 */
	public static final boolean FUNCTION_FORCE_UNLOCK_ALL_MUSIC = false;

	/**
	 * 当前是否处于维护状态，默认 False
	 */
	public static final boolean IS_UNDER_MAINTENANCE = false;

	/**
	 * 是否启用响应缓存，以及在下面配置功能的名称
	 */
	public static final boolean ENABLE_RESPONSE_CACHE = true;
	public static final String RESPONSE_CACHE_FOLDER = "H:\\TORII\\RESPONSE_CACHE\\";
	public static final String[] CACHE_RESPONSE_NAMES = new String[]{
			"sv5_common"
	};

	/**
	 * 请求地址
	 */
	public static final String BASE_URL = "http://127.0.0.1:50001";

	/**
	 * 远端协议处理地址
	 */
	public static final String REMOTE_PROTOCOL_SERVER_URI = "ws://localhost:50000/websocket";

	/**
	 * DEBUG：DUMP请求的KBIN数据，默认 False
	 */
	public static final boolean DEBUG_DUMP_KBIN_REQUEST = false;

	/**
	 * DEBUG：DUMP响应的KBIN数据，默认 False
	 */
	public static final boolean DEBUG_DUMP_RESPONSE = false;

	/**
	 * DEBUG：输出请求的明文内容，默认 False
	 */
	public static final boolean DEBUG_OUTPUT_REQUEST = false;

	/**
	 * DEBUG：调试数据输出总目录
	 */
	public static final String DEBUG_OUTPUT_FOLDER = "H:\\TORII\\DEBUG_OUTPUT\\";

}
