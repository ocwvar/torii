package com.ocwvar.torii;

import com.ocwvar.torii.utils.protocol.remote.RemoteServer;

public class ThreadTest {

	public static void main( String[] args ) throws InterruptedException {
		ToriiApplication.loadConfig();

		for ( int i = 0; i < 100; i++ ) {
			new Thread( () -> System.out.println( "执行结果：" + RemoteServer.getInstance().startServer() ) ).start();
		}

		Thread.sleep( 5000L );
		RemoteServer.getInstance().stopServer();
	}

}
