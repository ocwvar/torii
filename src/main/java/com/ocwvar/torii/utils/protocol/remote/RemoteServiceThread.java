package com.ocwvar.torii.utils.protocol.remote;

import com.ocwvar.torii.Configs;
import com.ocwvar.utils.IO;
import com.ocwvar.utils.Log;
import com.ocwvar.utils.PyCaller;
import com.ocwvar.utils.TextUtils;
import com.ocwvar.utils.annotation.NotNull;

/**
 * 远端服务运行线程
 */
class RemoteServiceThread extends Thread {

	/**
	 * 服务脚本地址
	 */
	private final String PATH;

	/**
	 * 回调方法
	 */
	private final Callback callback;

	private final String TAG = RemoteServiceThread.class.getSimpleName();

	/**
	 * 进程存活状态监测间隔时长
	 */
	private final long PROCESS_ALIVE_CHECK_TIME_MS = 500L;

	/**
	 * 当前是否正在运行
	 */
	private boolean isRunning = false;

	/**
	 * 是否调试模式，输出日志
	 */
	private final boolean DEBUG;

	/**
	 * 是否请求结束
	 */
	private boolean isRequestStop = false;

	public RemoteServiceThread( @NotNull Callback callback ) {
		super( "RemoteServiceThread" );
		this.callback = callback;
		this.PATH = IO.getResourceFilePath( Configs.getRemoteProtocolServerPath() );
		this.DEBUG = Configs.isPrintRemoteClientText();

		if ( TextUtils.isEmpty( this.PATH ) ) {
			throw new RuntimeException( "找不到远端服务脚本，配置路径：" + Configs.getRemoteProtocolServerPath() );
		}
	}

	@Override
	public void run() {
		Log.getInstance().print( TAG, "开始执行远端服务脚本" );
		final Process process = PyCaller.createProcess( this.PATH );
		if ( process == null ) {
			Log.getInstance().print( TAG, "服务启动失败！！", true );
			return;
		}

		Log.getInstance().print( TAG, "远端服务运行中  PID：" + process.pid() );
		while ( process.isAlive() ) {
			if ( this.isRequestStop ) {
				//请求销毁服务
				Log.getInstance().print( TAG, "执行终止" );
				process.destroy();
				break;
			} else {
				if ( this.DEBUG ) {
					Log.getInstance().print( TAG, "远端服务运行中  PID：" + process.pid() );
				}
				this.isRunning = true;
				this.callback.onStateChanged( true );
			}

			try {
				//等待一段时候后再检测进程的存活状态
				Thread.sleep( this.PROCESS_ALIVE_CHECK_TIME_MS );
			} catch ( InterruptedException ignore ) {
			}
		}

		this.isRunning = false;
		this.callback.onStateChanged( false );
		Log.getInstance().print( TAG, "远端服务已停止" );
	}

	/**
	 * 停止服务
	 */
	public synchronized void stopServer() {
		Log.getInstance().print( TAG, "接收到终止请求" );
		this.isRequestStop = true;
	}

	/**
	 * @return 当前远端服务是否正在运行
	 */
	public synchronized boolean isRunning() {
		return !this.isRequestStop && this.isRunning;
	}

	protected interface Callback {

		/**
		 * 状态发生改变时回调此方法
		 *
		 * @param isConnected 是否连接
		 */
		void onStateChanged( boolean isConnected );

	}

}
