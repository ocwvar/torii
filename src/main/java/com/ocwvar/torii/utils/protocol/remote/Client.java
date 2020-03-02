package com.ocwvar.torii.utils.protocol.remote;

import com.ocwvar.utils.Log;
import com.ocwvar.utils.node.BaseNode;
import com.ocwvar.utils.node.Node;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

public class Client extends WebSocketClient {

	private final String TAG = "RemoteKBin";

	//锁，别动
	private final Object LOCK = new Object();

	//等待响应超时时间，单位毫秒
	private final int RESPONSE_TIMEOUT_MS;

	//连接等待超时，单位毫秒
	private final int CONNECT_TIMEOUT_MS;

	//调试模式
	private final boolean DEBUG;

	//客户端序号
	private final int clientIndex;

	//是否曾经成功连接过
	private boolean isConnected = false;

	//回调方法
	private Callback callback;

	//任务TAG
	private String tag;

	//得到的结果 (回传对象)
	private Result result = null;

	//是否是加密请求	(回传参数)
	private boolean isEncodeRequest = false;

	/**
	 * @param RESPONSE_TIMEOUT_MS     等待响应超时时间，单位毫秒
	 * @param CONNECT_LOST_TIMEOUT_MS 连接丢失等待超时，单位毫秒
	 * @param CONNECT_TIMEOUT_MS      连接等待超时，单位毫秒
	 * @param DEBUG                   调试模式
	 * @param address                 链接地址
	 * @param clientIndex             客户端序号
	 * @param callback                任务完成状态回调方法
	 */
	public Client( int RESPONSE_TIMEOUT_MS, int CONNECT_LOST_TIMEOUT_MS, int CONNECT_TIMEOUT_MS, boolean DEBUG, URI address, int clientIndex, Callback callback ) {
		super( address );
		this.RESPONSE_TIMEOUT_MS = RESPONSE_TIMEOUT_MS;
		this.CONNECT_TIMEOUT_MS = CONNECT_TIMEOUT_MS;
		this.clientIndex = clientIndex;
		this.DEBUG = DEBUG;
		this.callback = callback;

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
			while ( !isOpen() ) {
				//服务线程挂了，重新启动
				printLog( "远程服务没有连接，尝试连接" );

				//这里需要再次进行判断，避免重复启动server
				printLog( "服务脚本启动结果：" + RemoteServer.getInstance().startServer() );

				try {
					printLog( "尝试连接 WebSocket" );
					if ( this.isConnected ) {
						printLog( "通过 reconnectBlocking 连接，结果：" + reconnectBlocking() );
					} else {
						printLog( "通过 connectBlocking 连接，结果：" + connectBlocking( CONNECT_TIMEOUT_MS, TimeUnit.MILLISECONDS ) );
					}
				} catch ( Exception e ) {
					printLog( e.toString() );
				}
			}

			printLog( "已启动完成！" );
			return true;
		} catch ( Exception ignore ) {
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
	 * 发送数据
	 *
	 * @param object          请求数据
	 * @param isEncodeRequest 是否为加密请求
	 * @param tag             任务标记
	 * @return 结果数据
	 */
	public synchronized Result sendRequest( Object object, boolean isEncodeRequest, String tag ) {
		synchronized ( this.LOCK ) {
			try {
				//重置与更新状态
				this.tag = tag;
				this.result = null;
				this.isEncodeRequest = isEncodeRequest;

				//连接状态判断，如果断开了则重新连接
				final boolean connectResult = connectRemote();
				if ( !connectResult || !isOpen() || isClosed() || isClosing() ) {
					return RemoteRequestManager.RESULT_TIMEOUT;
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
						RemoteRequestManager.RESULT_TIMEOUT.setTag( this.tag );
					}
					return RemoteRequestManager.RESULT_TIMEOUT;
				}
				__taskFinish();
				return result;
			} catch ( Exception e ) {
				e.printStackTrace();
				//执行异常
				if ( this.tag != null ) {
					RemoteRequestManager.RESULT_UNKNOWN_EXCEPTION.setTag( this.tag );
				}
				__taskFinish();
				return RemoteRequestManager.RESULT_UNKNOWN_EXCEPTION;
			}
		}
	}

	/**
	 * 并发请求测试
	 *
	 * @param tag 请求TAG
	 * @return 测试结果对象
	 */
	public synchronized Result testRequest( String tag ) {
		this.tag = tag;
		this.result = null;

		try {
			Thread.sleep( 10 );
		} catch ( InterruptedException ignore ) {
		}

		this.result = new Result( true, true, "并发测试", null );
		this.result.setTag( this.tag );
		this.callback.onTaskFinish( clientIndex );
		return this.result;
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

	/**
	 * 任务结束调用此方法
	 */
	private void __taskFinish() {
		if ( this.callback != null ) {
			this.callback.onTaskFinish( this.clientIndex );
		}
	}

	@Override
	public void onMessage( String message ) {

	}

	private void printLog( String msg ) {
		if ( DEBUG ) {
			Log.getInstance().print( TAG, msg );
		}
	}

	@Override
	public String toString() {
		return "Client{index-" + clientIndex + "}";
	}

	public interface Callback {

		/**
		 * 当客户端完成了任务时的回调方法
		 *
		 * @param clientIndex 客户端序号
		 */
		void onTaskFinish( int clientIndex );
	}
}
