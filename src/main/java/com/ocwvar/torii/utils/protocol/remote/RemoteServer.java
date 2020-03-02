package com.ocwvar.torii.utils.protocol.remote;

/**
 * 远程服务
 */
public class RemoteServer implements RemoteServiceThread.Callback {

	private static RemoteServer self;

	public static RemoteServer getInstance() {
		if ( self != null ) {
			return self;
		}

		synchronized ( RemoteServer.class ) {
			if ( self == null ) {
				self = new RemoteServer();
			}
		}

		return self;
	}

	//锁，别他妈动
	private final Object LOCK = new Object();

	//远端服务运行线程
	private RemoteServiceThread serviceThread = null;


	/**
	 * 启动并连接服务
	 *
	 * @return 是否启动并连接成功
	 */
	public synchronized boolean startServer() {
		if ( isRunning() ) {
			return true;
		}

		restartServer();
		return isRunning();
	}

	/**
	 * 停止服务
	 */
	public synchronized void stopServer() {
		if ( isRunning() ){
			this.serviceThread.stopServer();
			this.serviceThread = null;
		}
	}

	/**
	 * @return 远端服务是否正在运行
	 */
	public synchronized boolean isRunning() {
		return this.serviceThread != null && this.serviceThread.isRunning();
	}

	/**
	 * 重启服务
	 */
	private void restartServer() {
		if ( isRunning() ) {
			this.serviceThread.stopServer();
			this.serviceThread = null;
		}

		//这里等待最新的一次结果
		synchronized ( this.LOCK ) {
			this.serviceThread = new RemoteServiceThread( RemoteServer.this );
			this.serviceThread.start();
			try {
				this.LOCK.wait();
			} catch ( InterruptedException ignore ) {
			}
		}
	}


	/**
	 * 状态发生改变时回调此方法
	 *
	 * @param isConnected 是否连接
	 */
	@Override
	public void onStateChanged( boolean isConnected ) {
		synchronized ( this.LOCK ) {
			this.LOCK.notifyAll();
		}
	}
}
