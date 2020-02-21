package com.ocwvar.torii;

import com.ocwvar.torii.utils.remote.Client;

import java.net.URI;

public class ThreadTest {

	public static void main( String[] args ) {
		ToriiApplication.loadConfig();

		for ( int i = 0; i < 50; i++ ) {
			Client client = create( i );
			client.connectRemote();
		}
	}

	/**
	 * @return 新的客户端
	 */
	private static Client create( int clientTag ) {
		return new Client(
				2000,
				2000,
				2000,
				true,
				URI.create( Configs.getRemoteProtocolServerUri() ),
				clientTag,
				null
		);
	}

}
