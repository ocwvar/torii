package com.ocwvar.torii;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ocwvar.torii.data.StaticContainer;
import com.ocwvar.torii.utils.protocol.RemoteKBinClient;
import com.ocwvar.utils.IO;
import com.ocwvar.utils.Log;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;

import java.io.File;

@MapperScan( value = "com.ocwvar.torii.db.dao" )
@SpringBootApplication
public class ToriiApplication {

	public static void main( String[] args ) {
		SpringApplication application = new SpringApplication( ToriiApplication.class );

		//SpringBoot	启动回调
		application.addListeners( ( ApplicationListener< ContextRefreshedEvent > ) event -> {
			Log.getInstance().print( "重新读取 ServerConfig.json 文件" );
			loadConfig();	//必须必须必须 首先调用 ！！！！

			Log.getInstance().print( "重新生成必须目录结构" );
			createEnvironment();

			Log.getInstance().print( "连接到远端服务" );
			RemoteKBinClient.getInstance().connectRemote();
		} );

		//SpringBoot	关闭回调
		application.addListeners( ( ApplicationListener< ContextClosedEvent > ) event -> {
			//终端远端协议服务
			Log.getInstance().print( "SpringBoot 正在关闭，请求关闭远端协议服务" );
			RemoteKBinClient.getInstance().disconnectRemote();
		} );

		application.run( args );
	}

	/**
	 * 初始化目录环境
	 */
	private static void createEnvironment() {
		StaticContainer.getInstance();
		new File( Configs.getDumpFileOutputFolder() ).mkdirs();
		new File( Configs.getResponseCacheFolder() ).mkdirs();
	}

	/**
	 * 从 ServerConfig.json 中读取配置
	 */
	public static void loadConfig() {
		try {
			final byte[] bytes = IO.loadResource( "ServerConfig.json" );
			if ( bytes == null || bytes.length <= 0 ) {
				throw new RuntimeException( "没有找到配置文件 resources/ServerConfig.json，如果是第一次启动可以从 resources/backupFiles 中获取默认配置" );
			}

			final JsonObject config = JsonParser.parseString( new String( bytes, Field.UTF8 ) ).getAsJsonObject();

			Configs.setIsPaseliEnable( config.get( "FUNCTION_PASELI_ENABLE" ).getAsBoolean() );
			Configs.setIsForceUnlockAllItems( config.get( "FUNCTION_FORCE_UNLOCK_ITEMS" ).getAsBoolean() );
			Configs.setIsForceUnlockAllMusic( config.get( "FUNCTION_FORCE_UNLOCK_ALL_MUSIC" ).getAsBoolean() );
			Configs.setIsUnderMaintenance( config.get( "IS_UNDER_MAINTENANCE" ).getAsBoolean() );
			Configs.setIsResponseCacheEnable( config.get( "ENABLE_RESPONSE_CACHE" ).getAsBoolean() );
			Configs.setIsDumpRequestKbin( config.get( "DEBUG_DUMP_KBIN_REQUEST" ).getAsBoolean() );
			Configs.setIsDumpResponseKbin( config.get( "DEBUG_DUMP_RESPONSE" ).getAsBoolean() );
			Configs.setIsPrintRequestText( config.get( "IS_PRINT_REQUEST_TEXT" ).getAsBoolean() );
			Configs.setIsPrintRemoteClientText( config.get( "IS_PRINT_REMOTE_CLIENT_TEXT" ).getAsBoolean() );

			Configs.setRemoteProtocolServerPath( config.get( "REMOTE_PROTOCOL_SERVER_PATH" ).getAsString() );
			Configs.setShopName( config.get( "SHOP_NAME" ).getAsString() );
			Configs.setPort( config.get( "PORT" ).getAsString() );
			Configs.setServerUrl( config.get( "SERVER_URL" ).getAsString() );
			Configs.setRemoteProtocolServerUri( config.get( "REMOTE_PROTOCOL_SERVER_URI" ).getAsString() );
			Configs.setResponseCacheFolder( config.get( "RESPONSE_CACHE_FOLDER" ).getAsString() );
			Configs.setDumpFileOutputFolder( config.get( "DEBUG_OUTPUT_FOLDER" ).getAsString() );

			final JsonArray array = config.get( "CACHE_RESPONSE_NAMES" ).getAsJsonArray();
			final String[] responseNames = new String[ array.size() ];
			for ( int i = 0; i < responseNames.length; i++ ) {
				responseNames[ i ] = array.get( i ).getAsString();
			}
			Configs.setCacheResponseNames( responseNames );

		} catch ( Exception e ) {
			throw new RuntimeException( "加载配置文件失败:" + e );
		}
	}

}
