package com.ocwvar.torii.utils.protocol;


import com.ocwvar.torii.Configs;
import com.ocwvar.utils.IO;
import com.ocwvar.utils.Log;
import com.ocwvar.utils.PyCaller;
import com.ocwvar.utils.TextUtils;
import com.ocwvar.utils.annotation.Nullable;
import com.ocwvar.xml.node.BaseNode;
import com.ocwvar.xml.node.Node;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

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
	 * 连接丢失等待超时，单位毫秒
	 */
	private static final int CONNECT_LOST_TIMEOUT_MS = 2000;

	/**
	 * 连接超时，单位毫秒
	 */
	private static final int CONNECT_TIMEOUT_MS = 5000;

	/**
	 * 调试模式
	 */
	private static final boolean DEBUG = Configs.isPrintRemoteClientText();

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

	//锁，别动
	private final Object LOCK = new Object();

	//是否曾经成功连接过
	private boolean isConnected = false;

	//任务TAG
	private String tag;

	//得到的结果 (回传对象)
	private volatile Result result = null;

	//是否是加密请求	(回传参数)
	private volatile boolean isEncodeRequest = false;

	public RemoteKBinClient() {
		super( URI.create( Configs.getRemoteProtocolServerUri() ) );
		setReuseAddr( true );
		setTcpNoDelay( true );
		setConnectionLostTimeout( CONNECT_LOST_TIMEOUT_MS );
	}

	/**
	 * 连接或重连远端WS，注意此方法会阻塞线程
	 *
	 * @return 是否连接上
	 */
	public boolean connectRemote() {
		if ( isOpen() ) {
			//连接中，什么都不用管
			return true;
		}

		try {
			String path;
			while ( !isOpen() ) {
				//服务线程挂了，重新启动
				printLog( "远程服务没有连接，尝试连接" );
				path = IO.getResourceFilePath( Configs.getRemoteProtocolServerPath() );
				if ( TextUtils.isEmpty( path ) ) {
					throw new RuntimeException( "找不到远端服务脚本，配置路径：" + Configs.getRemoteProtocolServerPath() );
				}

				//吊起服务端脚本，执行有可能出错，因为已经启动过了服务脚本，不用管
				printLog( "尝试启动新的服务线程" );
				PyCaller.execAsync( path );

				//等待100ms，确保已启动完全
				Thread.sleep( 100 );

				printLog( "尝试重连WebSocket" );
				if ( this.isConnected ) {
					reconnectBlocking();
				} else {
					connectBlocking( CONNECT_TIMEOUT_MS, TimeUnit.MILLISECONDS );
				}
			}

			printLog( "已启动完成！" );
			return true;
		} catch ( InterruptedException e ) {
			return false;
		}
	}

	/**
	 * 断开远端WS连接
	 */
	public void disconnectRemote() {
		if ( isOpen() ) {
			//发送远端终止命令
			printLog( "已发送终止命令给远端服务" );
			send( new byte[]{ 0x00 } );
		}
		close();
	}

	/**
	 * 发送XML数据请求进行加密
	 *
	 * @param node 数据
	 * @param tag  任务标记
	 * @return 结果数据
	 */
	public Result sendXML( Node node, String tag ) {
		return sendRequest( node, true, tag );
	}

	/**
	 * 发送XML数据请求进行加密
	 *
	 * @param node 数据
	 * @return 结果数据
	 */
	public Result sendXML( Node node ) {
		return sendRequest( node, true, null );
	}

	/**
	 * 发送KBIN数据请求解密
	 *
	 * @param kbinData 数据
	 * @return 结果数据
	 */
	public Result sendKbin( byte[] kbinData ) {
		return sendRequest( kbinData, false, null );
	}

	/**
	 * 发送数据
	 *
	 * @param object          请求数据
	 * @param isEncodeRequest 是否为加密请求
	 * @param tag             任务标记
	 * @return 结果数据
	 */
	private synchronized Result sendRequest( Object object, boolean isEncodeRequest, String tag ) {
		synchronized ( this.LOCK ) {
			try {

				//重置与更新状态
				this.tag = tag;
				this.result = null;
				this.isEncodeRequest = isEncodeRequest;

				//连接状态判断，如果断开了则重新连接
				final boolean connectResult = connectRemote();
				if ( !connectResult || !isOpen() || isClosed() || isClosing() ) {
					return RESULT_TIMEOUT;
				}

				//请求数据
				if ( object instanceof Node ) {
					//加密请求，第一位数据为编码方式
					//1: UTF-8
					//2: CP932 (SHIFT-JIS)
					byte[] bytes = ( ( Node ) object ).toXmlText( true ).getBytes( StandardCharsets.UTF_8 );    //进行加密的永远为 UTF-8
					ByteBuffer byteBuffer = ByteBuffer.allocate( bytes.length + 1 );
					switch ( ( ( BaseNode ) object ).getEncodeCharset().toLowerCase() ) {
						default:
						case "utf-8":
							byteBuffer.put( ( byte ) 0x01 );
							break;
						case "cp932":
							byteBuffer.put( ( byte ) 0x02 );
							break;
					}
					printLog( "编码格式：" + ( ( BaseNode ) object ).getEncodeCharset().toLowerCase() );
					byteBuffer.put( bytes );
					bytes = byteBuffer.array();
					send( bytes );
				} else {
					send( ( byte[] ) object );
				}

				//等待数据
				this.LOCK.wait( RESPONSE_TIMEOUT_MS );
				if ( result == null ) {
					//没有拿到数据
					if ( this.tag != null ) {
						RESULT_TIMEOUT.setTag( this.tag );
					}
					return RESULT_TIMEOUT;
				}
				return result;
			} catch ( Exception e ) {
				//执行异常
				if ( this.tag != null ) {
					RESULT_UNKNOWN_EXCEPTION.setTag( this.tag );
				}
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
			if ( this.tag != null ) {
				this.result.setTag( this.tag );
			}

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
		synchronized ( this.LOCK ) {
			printLog( "远端协议处理服务断开:" + reason );

			this.result = new Result( true, this.isEncodeRequest, "远端断开连接：" + reason, null );
			if ( this.tag != null ) {
				this.result.setTag( tag );
			}

			try {
				this.LOCK.notify();
			} catch ( Exception ignore ) {
			}
		}
	}

	/**
	 * 远端服务出现异常，回调方法
	 */
	@Override
	public void onError( Exception ex ) {
		synchronized ( this.LOCK ) {
			printLog( "远端协议处理服务异常：" + ex );

			this.result = new Result( true, this.isEncodeRequest, ex.getMessage(), null );
			if ( this.tag != null ) {
				this.result.setTag( tag );
			}

			try {
				this.LOCK.notify();
			} catch ( Exception ignore ) {
			}
		}
	}

	@Override
	public void onMessage( String message ) {

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

}
