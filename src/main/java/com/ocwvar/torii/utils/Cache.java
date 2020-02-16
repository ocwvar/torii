package com.ocwvar.torii.utils;

import com.ocwvar.torii.Config;
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
		if ( !Config.ENABLE_RESPONSE_CACHE || hasResponseCache( request ) ) {
			return false;
		}

		final String[] paths = request.getRequestURL().toString().split( "/" );
		final String cachePath = Config.RESPONSE_CACHE_FOLDER + paths[ paths.length - 1 ] + ".kBin";
		final boolean result = IO.outputFile( true, cachePath, data );

		Log.getInstance().print( "生成请求缓存：" + paths[ paths.length - 1 ] + " 结果：" + result );
		return result;
	}

	/**
	 * @return 此请求的响应数据是否需要被缓存
	 */
	public static boolean shouldBeCached( HttpServletRequest request ) {
		if ( !Config.ENABLE_RESPONSE_CACHE ) {
			return false;
		}

		for ( String s : Config.CACHE_RESPONSE_NAMES ) {
			if ( request.getRequestURL().indexOf( s ) >= 0 ) {
				return true;
			}
		}

		return false;
	}

	/**
	 * 读取缓存
	 *
	 * @param request 请求对象
	 * @return 缓存数据
	 */
	public static @Nullable
	byte[] loadResponseCache( HttpServletRequest request ) {
		final String[] paths = request.getRequestURL().toString().split( "/" );
		return loadResponseCache( paths[ paths.length - 1 ] );
	}

	/**
	 * 读取缓存
	 *
	 * @param requestName 请求名称
	 * @return 缓存数据
	 */
	public static @Nullable
	byte[] loadResponseCache( String requestName ) {
		final String cachePath = Config.RESPONSE_CACHE_FOLDER + requestName + ".kBin";
		return IO.loadFile( cachePath );
	}

	/**
	 * @return 是否有缓存
	 */
	public static boolean hasResponseCache( HttpServletRequest request ) {
		final String[] paths = request.getRequestURL().toString().split( "/" );
		final String cachePath = Config.RESPONSE_CACHE_FOLDER + paths[ paths.length - 1 ] + ".kBin";
		return new File( cachePath ).exists();
	}

}
