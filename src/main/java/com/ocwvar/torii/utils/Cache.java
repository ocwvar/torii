package com.ocwvar.torii.utils;

import com.ocwvar.torii.Configs;
import com.ocwvar.utils.IO;
import com.ocwvar.utils.Log;
import com.ocwvar.utils.annotation.Nullable;

import javax.servlet.http.HttpServletRequest;
import java.io.File;

public class Cache {

	/**
	 * 创建响应的缓存
	 *
	 * @param data    响应数据
	 * @param request 请求对象
	 * @return 执行是否成功
	 */
	public static boolean createResponseCache( byte[] data, HttpServletRequest request ) throws Exception {
		final String requestTag = getRequestTag( request );
		if ( !Configs.isIsResoponseCacheEnable() || hasResponseCache( requestTag ) ) {
			return false;
		}

		final String cachePath = Configs.getResponseCacheFolder() + requestTag + ".kBin";
		final boolean result = IO.outputFile( true, cachePath, data );

		Log.getInstance().print( "生成请求缓存：" + requestTag + " 结果：" + result );
		return result;
	}

	/**
	 * @return 此请求的响应数据是否需要被缓存
	 */
	public static boolean shouldBeCached( HttpServletRequest request ) {
		if ( !Configs.isIsResoponseCacheEnable() ) {
			return false;
		}

		final String requestTag = getRequestTag( request );
		for ( String s : Configs.getCacheResponseNames() ) {
			if ( requestTag.equals( s ) ) {
				return true;
			}
		}

		return false;
	}

	/**
	 * 获取请求TAG
	 *
	 * @param request 请求对象
	 * @return TAG
	 */
	public static String getRequestTag( HttpServletRequest request ) {
		final String[] paths = request.getRequestURL().toString().split( "/" );
		return paths[ paths.length - 2 ] + "_" + paths[ paths.length - 1 ];
	}

	/**
	 * 读取缓存
	 *
	 * @param request 请求对象
	 * @return 缓存数据
	 */
	public static @Nullable
	byte[] loadResponseCache( HttpServletRequest request ) {
		return loadResponseCache( getRequestTag( request ) );
	}

	/**
	 * 读取缓存
	 *
	 * @param requestTag 请求名称
	 * @return 缓存数据
	 */
	public static @Nullable
	byte[] loadResponseCache( String requestTag ) {
		final String cachePath = Configs.getResponseCacheFolder() + requestTag + ".kBin";
		return IO.loadFile( cachePath );
	}

	/**
	 * @return 是否有缓存
	 */
	public static boolean hasResponseCache( String requestTag ) {
		return new File( Configs.getResponseCacheFolder() + requestTag + ".kBin" ).exists();
	}

}
