package com.ocwvar.torii.utils.remote;

import com.ocwvar.torii.Configs;
import com.ocwvar.xml.node.Node;

public class RemoteRequestManager {

	private static RemoteRequestManager self;

	public static RemoteRequestManager getInstance() {
		if ( self != null ) {
			return self;
		}

		synchronized ( RemoteRequestManager.class ) {
			if ( self == null ) {
				self = new RemoteRequestManager();
			}
		}

		return self;
	}

	/**
	 * 超时结果
	 */
	protected static final Result RESULT_TIMEOUT;

	/**
	 * 未知异常结果
	 */
	protected static final Result RESULT_UNKNOWN_EXCEPTION;

	/**
	 * 等待响应超时时间，单位毫秒
	 */
	protected static final long RESPONSE_TIMEOUT_MS = 10L * 1000L;

	/**
	 * 连接丢失等待超时，单位毫秒
	 */
	protected static final int CONNECT_LOST_TIMEOUT_MS = 2000;

	/**
	 * 连接超时，单位毫秒
	 */
	protected static final int CONNECT_TIMEOUT_MS = 5000;

	/**
	 * 调试模式
	 */
	protected static final boolean DEBUG = Configs.isPrintRemoteClientText();

	static {
		RESULT_TIMEOUT = new Result( true, false, "等待结果超时", null );
		RESULT_UNKNOWN_EXCEPTION = new Result( true, false, "未知错误", null );
	}

	//客户端池
	private final ClientPool clientPool;

	public RemoteRequestManager() {
		this.clientPool = new ClientPool();
	}

	/**
	 * 发送XML数据请求进行加密
	 *
	 * @param node 数据
	 * @param tag  任务标记
	 * @return 结果数据
	 */
	public Result sendXML( Node node, String tag ) {
		return send( node, true, tag );
	}

	/**
	 * 发送XML数据请求进行加密
	 *
	 * @param node 数据
	 * @return 结果数据
	 */
	public Result sendXML( Node node ) {
		return send( node, true, null );
	}

	/**
	 * 发送KBIN数据请求解密
	 *
	 * @param kbinData 数据
	 * @return 结果数据
	 */
	public Result sendKbin( byte[] kbinData ) {
		return send( kbinData, false, null );
	}

	/**
	 * 发送数据
	 *
	 * @param object          请求数据
	 * @param isEncodeRequest 是否为加密请求
	 * @param tag             任务标记
	 * @return 结果数据
	 */
	private Result send( Object object, boolean isEncodeRequest, String tag ) {
		final Client client = this.clientPool.getClient();
		return client.sendRequest( object, isEncodeRequest, tag );
		//return client.testRequest( tag );
	}

}
