package com.ocwvar.torii.utils.protocol;


import com.ocwvar.torii.Config;
import com.ocwvar.utils.Log;
import com.ocwvar.utils.annotation.Nullable;
import com.ocwvar.xml.node.Node;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.nio.ByteBuffer;

public class RemoteKBinClient extends WebSocketClient {

	private static RemoteKBinClient self;

	public static RemoteKBinClient getInstance() {
		if ( self != null ) {
			return self;
		}

		synchronized ( RemoteKBinClient.class ) {
			if ( self == null ) {
				self = new RemoteKBinClient();
			}
		}

		return self;
	}

	/**
	 * 等待响应超时时间，单位毫秒
	 */
	private static final long RESPONSE_TIMEOUT_MS = 10L * 1000L;

	/**
	 * 连接超时，单位毫秒
	 */
	private static final int CONNECT_TIMEOUT_MS = 2000;

	/**
	 * 调试模式
	 */
	private static final boolean DEBUG = false;

	/**
	 * 超时结果
	 */
	private static final Result RESULT_TIMEOUT;

	/**
	 * 未知异常结果
	 */
	private static final Result RESULT_UNKNOWN_EXCEPTION;

	static {
		RESULT_TIMEOUT = new Result( true, false, "等待结果超时", null );
		RESULT_UNKNOWN_EXCEPTION = new Result( true, false, "未知错误", null );
	}

	private final Object LOCK = new Object();

	private boolean isConnected = false;
	private volatile Result result = null;
	private volatile boolean isEncodeRequest = false;

	public RemoteKBinClient() {
		super( URI.create( Config.REMOTE_PROTOCOL_SERVER_URI ) );
		setConnectionLostTimeout( CONNECT_TIMEOUT_MS );
	}

	/**
	 * 连接或重连远端WS，注意此方法会阻塞线程
	 *
	 * @return 是否连接上
	 */
	public boolean connectRemote() {
		if ( !isOpen() ) {
			return true;
		}

		try {
			if ( this.isConnected ) {
				return reconnectBlocking();
			} else {
				return connectBlocking();
			}
		} catch ( InterruptedException e ) {
			return false;
		}
	}

	/**
	 * 发送XML数据请求进行加密
	 *
	 * @param node 数据
	 */
	public Result sendXML( Node node ) {
		return sendRequest( node, true );
	}

	/**
	 * 发送KBIN数据请求解密
	 *
	 * @param kbinData 数据
	 */
	public Result sendKbin( byte[] kbinData ) {
		return sendRequest( kbinData, false );
	}

	/**
	 * 发送数据
	 *
	 * @param object          请求数据
	 * @param isEncodeRequest 是否为加密请求
	 * @return 结果数据
	 */
	private synchronized Result sendRequest( Object object, boolean isEncodeRequest ) {
		synchronized ( this.LOCK ) {
			try {

				//如果没有连接，则进行连接
				if ( !isOpen() ) {
					if ( this.isConnected ) {
						reconnectBlocking();
					} else {
						connectBlocking();
					}
				}

				//重置与更新状态
				this.result = null;
				this.isEncodeRequest = isEncodeRequest;

				//请求数据
				if ( object instanceof Node ) {
					send( ( ( Node ) object ).toXmlText() );
				} else {
					send( ( byte[] ) object );
				}

				//等待数据
				this.LOCK.wait( RESPONSE_TIMEOUT_MS );
				return result == null ? RESULT_TIMEOUT : result;
			} catch ( Exception e ) {
				return RESULT_UNKNOWN_EXCEPTION;
			}
		}
	}

	/**
	 * 这里处理接收到的数据
	 */
	@Override
	public void onMessage( ByteBuffer bytes ) {
		synchronized ( this.LOCK ) {
			printLog( "返回数据长度：" + bytes.limit() );

			this.result = new Result( false, this.isEncodeRequest, null, bytes.array() );
			try {
				this.LOCK.notifyAll();
			} catch ( Exception e ) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 连接上远端服务，回调方法
	 */
	@Override
	public void onOpen( ServerHandshake handshakedata ) {
		printLog( "远端协议处理服务已连接" );
		this.isConnected = true;
	}

	/**
	 * 远端服务断开，回调方法
	 */
	@Override
	public void onClose( int code, String reason, boolean remote ) {
		printLog( "远端协议处理服务断开:" + reason );

		this.result = new Result( true, this.isEncodeRequest, "远端断开连接：" + reason, null );
		try {
			this.LOCK.notify();
		} catch ( Exception ignore ) {
		}
	}

	/**
	 * 远端服务出现异常，回调方法
	 */
	@Override
	public void onError( Exception ex ) {
		printLog( "远端协议处理服务异常：" + ex );

		this.result = new Result( true, this.isEncodeRequest, ex.getMessage(), null );
		try {
			this.LOCK.notify();
		} catch ( Exception ignore ) {
		}
	}

	@Override
	public void onMessage( String message ) {
		//这个不用
	}

	private void printLog( String msg ) {
		if ( DEBUG ) {
			Log.getInstance().print( "[RemoteKBin] " + msg );
		}
	}

	public static class Result {
		private final boolean hasException;
		private final boolean isEncodeRequest;
		private final String exceptionMessage;
		private final byte[] result;

		public Result( boolean hasException, boolean isEncodeRequest, String exceptionMessage, byte[] result ) {
			this.hasException = hasException;
			this.isEncodeRequest = isEncodeRequest;
			this.exceptionMessage = exceptionMessage;
			this.result = result;
		}

		/**
		 * @return 是否出现了异常
		 */
		public boolean isHasException() {
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
}
