package com.ocwvar.torii.utils.protocol;

import com.ocwvar.kbin.KBinXml;
import com.ocwvar.torii.Configs;
import com.ocwvar.torii.Field;
import com.ocwvar.torii.data.StaticContainer;
import com.ocwvar.torii.utils.Cache;
import com.ocwvar.utils.IO;
import com.ocwvar.utils.Log;
import com.ocwvar.utils.TextUtils;
import com.ocwvar.utils.annotation.NotNull;
import com.ocwvar.utils.annotation.Nullable;
import com.ocwvar.xml.node.Node;
import com.ocwvar.xml.node.NodeHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class Protocol {

	/**
	 * 只输出请求内容
	 *
	 * @param request 请求对象
	 */
	public static void print( @NotNull HttpServletRequest request ) throws Exception {
		final String eamuseInfo = request.getHeader( Field.HEADER_X_Eamuse_Info );
		final boolean needDeCompress = request.getHeader( Field.HEADER_COMPRESS ).equals( Field.HEADER_COMPRESS_TYPE_LZ77 );
		final boolean needDecryptRC4 = !TextUtils.isEmpty( eamuseInfo );

		byte[] data = request.getInputStream().readAllBytes();

		//Rc4解密
		if ( needDecryptRC4 ) {
			data = Rc4.decrypt( Rc4.getRC4Key( eamuseInfo ), data );
		}

		//解压
		if ( needDeCompress ) {
			data = KLz77.decompress( data );
		}

		//Kbin解密
		final Node node;
		if ( KBinXml.isEncoded( data ) ) {
			node = KBinXml.decode( data );
		} else {
			node = NodeHelper.xml2Node( NodeHelper.byte2Xml( data ) );
		}

		final String content = NodeHelper.xml2Text( NodeHelper.note2Xml( node ) );
		final String string = "====================================" + "\n" +
				"URL:" + request.getRequestURL() + "\n" +
				"Need decompress:" + needDeCompress + "\n" +
				"Need rc4 decode:" + needDecryptRC4 + "\n" +
				content +
				"====================================" + "\n";
		System.out.println( string );
	}

	/**
	 * 解密数据
	 *
	 * @param request 请求对象
	 * @param print   是否打印出内容
	 * @return 数据内容
	 */
	public static Node decrypt( @NotNull HttpServletRequest request, boolean print ) throws Exception {
		final String eamuseInfo = request.getHeader( Field.HEADER_X_Eamuse_Info );
		final boolean needDeCompress = request.getHeader( Field.HEADER_COMPRESS ).equals( Field.HEADER_COMPRESS_TYPE_LZ77 );
		final boolean needDecryptRC4 = !TextUtils.isEmpty( eamuseInfo );

		byte[] data = request.getInputStream().readAllBytes();

		//Rc4解密
		if ( needDecryptRC4 ) {
			data = Rc4.decrypt( Rc4.getRC4Key( eamuseInfo ), data );
		}

		//解压
		if ( needDeCompress ) {
			data = KLz77.decompress( data );
		}

		//Kbin解密
		final Node node;
		if ( KBinXml.isEncoded( data ) ) {
			node = KBinXml.decode( data );
		} else {
			node = NodeHelper.xml2Node( NodeHelper.byte2Xml( data ) );
		}

		//DEBUG：输出请求内容
		if ( Configs.isDumpRequestKbin() | print ) {
			final String content = NodeHelper.xml2Text( NodeHelper.note2Xml( node ) );
			final String string = "====================================" + "\n" +
					"URL:" + request.getRequestURL() + "\n" +
					"Need decompress:" + needDeCompress + "\n" +
					"Need rc4 decode:" + needDecryptRC4 + "\n" +
					content +
					"====================================" + "\n";
			System.out.println( string );
		}

		//DEBUG：DUMP出请求内容
		if ( Configs.isDumpRequestKbin() ) {
			final String[] paths = request.getRequestURL().toString().split( "/" );

			IO.outputFile( true,
					Configs.getDumpFileOutputFolder() + "/dump/request/",
					paths[ paths.length - 2 ] + "_" + paths[ paths.length - 1 ] + ".kbin",
					data
			);
		}

		return node;
	}

	/**
	 * 解密数据
	 *
	 * @param request 请求对象
	 * @return 数据内容
	 */
	public static Node decrypt( @NotNull HttpServletRequest request ) throws Exception {
		return decrypt( request, false );
	}

	/**
	 * 加密并传输数据
	 *
	 * @param node     传输的内容
	 * @param request  请求对象
	 * @param response 响应对象
	 */
	public static void encryptAndCommit( @Nullable Node node, @NotNull HttpServletRequest request, @NotNull HttpServletResponse response ) throws Exception {
		if ( node == null ) {
			commit( 403, null, request, response );
			return;
		}

		//首先必须Kbin加密
		//byte[] data = KBinXml.encode( node );
		final RemoteKBinClient.Result result = RemoteKBinClient.getInstance().sendXML( node );
		byte[] data = result.getResult();
		if ( result.hasException() ) {
			Log.getInstance().print( "处理出现问题:" + result.getExceptionMessage() );
			commit( 403, null, request, response );
			return;
		}

		final String eamuseInfo = request.getHeader( Field.HEADER_X_Eamuse_Info );
		final boolean needCompress = request.getHeader( Field.HEADER_COMPRESS ).equals( Field.HEADER_COMPRESS_TYPE_LZ77 );
		final boolean needEncryptRC4 = !TextUtils.isEmpty( eamuseInfo );

		//DEBUG：DUMP 出响应数据
		if ( Configs.isDumpResponseKbin() ) {
			final String[] paths = request.getRequestURL().toString().split( "/" );

			IO.outputFile( true,
					Configs.getDumpFileOutputFolder() + "/dump/response/",
					paths[ paths.length - 2 ] + "_" + paths[ paths.length - 1 ] + ".kbin",
					data
			);
		}

		//需要缓存数据
		if ( Configs.isResponseCacheEnable() && Cache.shouldBeCached( request ) && !StaticContainer.getInstance().has( String.valueOf( request.getRequestURL().hashCode() ) ) ) {
			if ( Cache.createResponseCache( data, request ) ) {
				StaticContainer.getInstance().set( String.valueOf( request.getRequestURL().hashCode() ), 0 );
				Log.getInstance().print( "已生成缓存：" + request.getRequestURL() );
			}
		}

		//需要压缩
		if ( needCompress ) {
			data = KLz77.compress( data );
		}

		//需要RC4加密
		if ( needEncryptRC4 ) {
			data = Rc4.encrypt( Rc4.getRC4Key( eamuseInfo ), data );
		}

		commit( 200, data, request, response );
	}

	/**
	 * 通过本地缓存进行响应
	 *
	 * @param request  请求对象
	 * @param response 响应对象
	 * @return 是否执行成功
	 */
	public static boolean commitWithCache( @NotNull HttpServletRequest request, @NotNull HttpServletResponse response ) throws NoSuchAlgorithmException, IOException {
		final String requestTag = Cache.getRequestTag( request );
		if ( !Configs.isResponseCacheEnable() || !Cache.hasResponseCache( requestTag ) ) {
			return false;
		}

		byte[] data = Cache.loadResponseCache( request );
		if ( data == null ) {
			Log.getInstance().print( "读取缓存数据失败" );
			return false;
		}

		if ( !StaticContainer.getInstance().has( String.valueOf( request.getRequestURL().hashCode() ) ) ) {
			StaticContainer.getInstance().set( String.valueOf( request.getRequestURL().hashCode() ), 0 );
		}

		final String eamuseInfo = request.getHeader( Field.HEADER_X_Eamuse_Info );
		final boolean needCompress = request.getHeader( Field.HEADER_COMPRESS ).equals( Field.HEADER_COMPRESS_TYPE_LZ77 );
		final boolean needEncryptRC4 = !TextUtils.isEmpty( eamuseInfo );

		//需要压缩
		if ( needCompress ) {
			data = KLz77.compress( data );
		}

		//需要RC4加密
		if ( needEncryptRC4 ) {
			data = Rc4.encrypt( Rc4.getRC4Key( eamuseInfo ), data );
		}

		commit( 200, data, request, response );
		Log.getInstance().print( "已通过缓存完成请求：" + request.getRequestURL() );
		return true;
	}

	/**
	 * 响应请求
	 *
	 * @param code     响应码
	 * @param data     数据，如果没有则为 null
	 * @param request  请求对象
	 * @param response 响应对象
	 */
	public static void commit( int code, @Nullable byte[] data, @NotNull HttpServletRequest request, @NotNull HttpServletResponse response ) throws IOException {
		response.setHeader( Field.HEADER_X_Eamuse_Info, request.getHeader( Field.HEADER_X_Eamuse_Info ) );
		response.setHeader( Field.HEADER_COMPRESS, request.getHeader( Field.HEADER_COMPRESS ) );
		response.setStatus( code );

		if ( data != null ) {
			response.getOutputStream().write( data );
		}
	}

}
