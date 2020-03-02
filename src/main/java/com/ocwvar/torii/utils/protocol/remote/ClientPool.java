package com.ocwvar.torii.utils.protocol.remote;

import com.ocwvar.torii.Configs;

import java.net.URI;

public class ClientPool implements Client.Callback {

	//池内最少客户端数量
	private static final int MIN_CLIENT_COUNT = 1;

	//池内最多客户端数量
	private static final int MAX_CLIENT_COUNT = 50;

	//锁：任务是否空闲
	private final Object LOCK_CLIENT_IDEL = new Object();

	//客户端池相关配置
	private final Client[] pool = new Client[ MAX_CLIENT_COUNT ];
	private final boolean[] usage = new boolean[ MAX_CLIENT_COUNT ];
	private int currentSize = MIN_CLIENT_COUNT;

	ClientPool() {
		//初始化最少客户端数量
		for ( int i = 0; i < MIN_CLIENT_COUNT; i++ ) {
			this.pool[ i ] = create( i );
		}
	}

	/**
	 * @return 当前可用的 Client
	 */
	public Client getClient() {
		return findAvailableClient();
	}

	/**
	 * @return 可用的客户端
	 */
	private synchronized Client findAvailableClient() {
		//找到当前没有任务处理的 Client，如果 Client 都满了，则创建新的 Client。
		//如果当前已经到了最多客户端数量，则等待
		while ( true ) {
			for ( int i = 0; i < this.pool.length; i++ ) {
				//在这里寻找所有空闲的客户端
				Client client = this.pool[ i ];
				if ( client != null && !this.usage[ i ] ) {
					this.usage[ i ] = true;    //登记已被使用
					return client;
				}
			}

			Client client = waitOrCreate();
			if ( client != null ) {
				//直接使用新的客户端，不需要等待
				return client;
			}
		}
	}

	/**
	 * 等待空闲客户端或创建新的客户端
	 *
	 * @return 新创建的客户端，如果已经达到池限制，则返回 NULL
	 */
	private Client waitOrCreate() {
		if ( this.currentSize == ClientPool.MAX_CLIENT_COUNT ) {
			//已到达最大值，等待
			synchronized ( this.LOCK_CLIENT_IDEL ) {
				try {
					this.LOCK_CLIENT_IDEL.wait();
				} catch ( InterruptedException ignore ) {
				}
				return null;
			}
		} else {
			//创建新的客户端
			Client newClient = null;
			for ( int i = MIN_CLIENT_COUNT; i < this.pool.length; i++ ) {
				if ( this.pool[ i ] == null ) {
					this.pool[ i ] = create( i );
					newClient = pool[ i ];
					this.currentSize += 1;
					break;
				}
			}
			return newClient;
		}
	}

	/**
	 * @return 新的客户端
	 */
	private Client create( int clientTag ) {
		System.out.println( "新客户端TAG：" + clientTag );
		return new Client(
				RemoteRequestManager.CONNECT_TIMEOUT_MS,
				RemoteRequestManager.CONNECT_LOST_TIMEOUT_MS,
				RemoteRequestManager.CONNECT_TIMEOUT_MS,
				RemoteRequestManager.DEBUG,
				URI.create( Configs.getRemoteProtocolServerUri() ),
				clientTag,
				ClientPool.this
		);
	}

	/**
	 * 当客户端完成了任务时的回调方法
	 *
	 * @param clientIndex 客户端序号
	 */
	@Override
	public void onTaskFinish( int clientIndex ) {
		//这里通知有新的客户端空闲
		synchronized ( this.LOCK_CLIENT_IDEL ) {
			this.usage[ clientIndex ] = false;
			this.LOCK_CLIENT_IDEL.notify();
		}
	}
}
