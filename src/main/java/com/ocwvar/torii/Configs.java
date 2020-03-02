package com.ocwvar.torii;

public class Configs {

	private static boolean isPaseliEnable;
	private static boolean isForceUnlockAllItems;
	private static boolean isForceUnlockAllMusic;
	private static boolean isUnderMaintenance;
	private static boolean isResponseCacheEnable;
	private static boolean isDumpRequestKbin;
	private static boolean isDumpResponseKbin;
	private static boolean isPrintRequestText;
	private static boolean isPrintRemoteClientText;

	private static String shopName;
	private static String port;
	private static String serverUrl;
	private static String remoteProtocolServerUri;
	private static String responseCacheFolder;
	private static String dumpFileOutputFolder;
	private static String remoteProtocolServerPath;
	private static String pinSceneMessage;

	private static String[] cacheResponseNames;

	/**
	 * @return 是否启用 PASELI
	 */
	public static boolean isPaseliEnable() {
		return isPaseliEnable;
	}

	/**
	 * @return 是否强制全解所有物品
	 */
	public static boolean isForceUnlockAllItems() {
		return isForceUnlockAllItems;
	}

	/**
	 * @return 是否强制全解所有歌曲
	 */
	public static boolean isForceUnlockAllMusic() {
		return isForceUnlockAllMusic;
	}

	/**
	 * @return 当前是否为维护状态
	 */
	public static boolean isUnderMaintenance() {
		return isUnderMaintenance;
	}

	/**
	 * @return 是否启用响应缓存
	 */
	public static boolean isResponseCacheEnable() {
		return isResponseCacheEnable;
	}

	/**
	 * @return 是否DUMP请求数据
	 */
	public static boolean isDumpRequestKbin() {
		return isDumpRequestKbin;
	}

	/**
	 * @return 是否DUMP响应数据
	 */
	public static boolean isDumpResponseKbin() {
		return isDumpResponseKbin;
	}

	/**
	 * @return 是否打印请求数据
	 */
	public static boolean isPrintRequestText() {
		return isPrintRequestText;
	}

	/**
	 * @return 店铺名强制前缀
	 */
	public static String getShopName() {
		return shopName;
	}

	/**
	 * @return 端口
	 */
	public static String getPort() {
		return port;
	}

	/**
	 * @return 服务器地址 (不带端口)
	 */
	public static String getServerUrl() {
		return serverUrl;
	}

	/**
	 * @return 服务器完整地址 (带端口)
	 */
	public static String getFullServerUrl() {
		return serverUrl + ":" + port;
	}

	/**
	 * @return 远程协议处理WS地址
	 */
	public static String getRemoteProtocolServerUri() {
		return remoteProtocolServerUri;
	}

	/**
	 * @return 响应缓存目录
	 */
	public static String getResponseCacheFolder() {
		return responseCacheFolder;
	}

	/**
	 * @return DUMP文件保存目录
	 */
	public static String getDumpFileOutputFolder() {
		return dumpFileOutputFolder;
	}

	/**
	 * @return 需要缓存的响应名称
	 */
	public static String[] getCacheResponseNames() {
		return cacheResponseNames;
	}

	/**
	 * @return 远端协议服务脚本路径
	 */
	public static String getRemoteProtocolServerPath() {
		return remoteProtocolServerPath;
	}

	/**
	 * @return 是否打印远程协议端日志
	 */
	public static boolean isPrintRemoteClientText() {
		return isPrintRemoteClientText;
	}

	/**
	 * @return	输入密码界面显示的信息
	 */
	public static String getPinSceneMessage() {
		return pinSceneMessage;
	}

	public static void setPinSceneMessage( String pinSceneMessage ) {
		Configs.pinSceneMessage = pinSceneMessage;
	}

	public static void setIsPrintRemoteClientText( boolean isPrintRemoteClientText ) {
		Configs.isPrintRemoteClientText = isPrintRemoteClientText;
	}

	public static void setRemoteProtocolServerPath( String remoteProtocolServerPath ) {
		Configs.remoteProtocolServerPath = remoteProtocolServerPath;
	}

	protected static void setIsPaseliEnable( boolean isPaseliEnable ) {
		Configs.isPaseliEnable = isPaseliEnable;
	}

	protected static void setIsForceUnlockAllItems( boolean isForceUnlockAllItems ) {
		Configs.isForceUnlockAllItems = isForceUnlockAllItems;
	}


	protected static void setIsForceUnlockAllMusic( boolean isForceUnlockAllMusic ) {
		Configs.isForceUnlockAllMusic = isForceUnlockAllMusic;
	}

	protected static void setIsUnderMaintenance( boolean isUnderMaintenance ) {
		Configs.isUnderMaintenance = isUnderMaintenance;
	}

	protected static void setIsResponseCacheEnable( boolean isResponseCacheEnable ) {
		Configs.isResponseCacheEnable = isResponseCacheEnable;
	}

	protected static void setIsDumpRequestKbin( boolean isDumpRequestKbin ) {
		Configs.isDumpRequestKbin = isDumpRequestKbin;
	}

	protected static void setIsDumpResponseKbin( boolean isDumpResponseKbin ) {
		Configs.isDumpResponseKbin = isDumpResponseKbin;
	}

	protected static void setIsPrintRequestText( boolean isPrintRequestText ) {
		Configs.isPrintRequestText = isPrintRequestText;
	}

	protected static void setShopName( String shopName ) {
		Configs.shopName = shopName;
	}

	protected static void setPort( String port ) {
		Configs.port = port;
	}

	protected static void setServerUrl( String serverUrl ) {
		Configs.serverUrl = serverUrl;
	}

	protected static void setRemoteProtocolServerUri( String remoteProtocolServerUri ) {
		Configs.remoteProtocolServerUri = remoteProtocolServerUri;
	}

	protected static void setResponseCacheFolder( String responseCacheFolder ) {
		Configs.responseCacheFolder = responseCacheFolder;
	}

	protected static void setDumpFileOutputFolder( String dumpFileOutputFolder ) {
		Configs.dumpFileOutputFolder = dumpFileOutputFolder;
	}

	protected static void setCacheResponseNames( String[] cacheResponseNames ) {
		Configs.cacheResponseNames = cacheResponseNames;
	}
}
