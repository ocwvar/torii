package com.ocwvar.torii.utils.protocol;

import com.ocwvar.kbin.KBinXml;
import com.ocwvar.torii.Config;
import com.ocwvar.torii.Field;
import com.ocwvar.torii.utils.IO;
import com.ocwvar.utils.TextUtils;
import com.ocwvar.utils.annotation.NotNull;
import com.ocwvar.utils.annotation.Nullable;
import com.ocwvar.xml.node.Node;
import com.ocwvar.xml.node.NodeHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

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
			data = Lz77.decompress( data );
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
	 * 使用 DUMP 出来的 XML 进行响应
	 *
	 * @param xmlPath  XML文件路径
	 * @param request  请求对象
	 * @param response 响应对象
	 */
	public static void commitWithDumpXml( @NotNull String xmlPath, @NotNull HttpServletRequest request, @NotNull HttpServletResponse response ) throws Exception {
		final InputStream inputStream = new FileInputStream( xmlPath );
		final byte[] data = inputStream.readAllBytes();
		inputStream.close();
		encryptAndCommit( data, request, response );
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
			data = Lz77.decompress( data );
		}

		//Kbin解密
		final Node node;
		if ( KBinXml.isEncoded( data ) ) {
			node = KBinXml.decode( data );
		} else {
			node = NodeHelper.xml2Node( NodeHelper.byte2Xml( data ) );
		}

		//DEBUG：输出请求内容
		if ( Config.DEBUG_OUTPUT_REQUEST | print ) {
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
		if ( Config.DEBUG_DUMP_KBIN_REQUEST ) {
			final String[] paths = request.getRequestURL().toString().split( "/" );

			IO.outputFile( true,
					Config.DEBUG_OUTPUT_FOLDER + "/dump/request/",
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
		byte[] data = KBinXml.encode( node );

		final String eamuseInfo = request.getHeader( Field.HEADER_X_Eamuse_Info );
		final boolean needCompress = request.getHeader( Field.HEADER_COMPRESS ).equals( Field.HEADER_COMPRESS_TYPE_LZ77 );
		final boolean needEncryptRC4 = !TextUtils.isEmpty( eamuseInfo );

		//DEBUG：DUMP 出响应数据
		if ( Config.DEBUG_DUMP_RESPONSE ) {
			final String[] paths = request.getRequestURL().toString().split( "/" );

			IO.outputFile( true,
					Config.DEBUG_OUTPUT_FOLDER + "/dump/response/",
					paths[ paths.length - 2 ] + "_" + paths[ paths.length - 1 ] + ".kbin",
					data
			);
		}

		//需要压缩
		if ( needCompress ) {
			data = Lz77.compress( data );
		}

		//需要RC4加密
		if ( needEncryptRC4 ) {
			data = Rc4.encrypt( Rc4.getRC4Key( eamuseInfo ), data );
		}

		commit( 200, data, request, response );
	}

	/**
	 * 加密并传输数据
	 *
	 * @param rawData  传输的内容
	 * @param request  请求对象
	 * @param response 响应对象
	 */
	public static void encryptAndCommit( @Nullable byte[] rawData, @NotNull HttpServletRequest request, @NotNull HttpServletResponse response ) throws Exception {
		if ( rawData == null ) {
			commit( 403, null, request, response );
			return;
		}

		//首先必须Kbin加密
		byte[] data = KBinXml.encode( new String( rawData ) );

		final String eamuseInfo = request.getHeader( Field.HEADER_X_Eamuse_Info );
		final boolean needCompress = request.getHeader( Field.HEADER_COMPRESS ).equals( Field.HEADER_COMPRESS_TYPE_LZ77 );
		final boolean needEncryptRC4 = !TextUtils.isEmpty( eamuseInfo );

		//DEBUG：DUMP 出响应数据
		if ( Config.DEBUG_DUMP_RESPONSE ) {
			final String[] paths = request.getRequestURL().toString().split( "/" );

			IO.outputFile( true,
					Config.DEBUG_OUTPUT_FOLDER + "/dump/response/",
					paths[ paths.length - 2 ] + "_" + paths[ paths.length - 1 ] + ".kbin",
					data
			);
		}

		//需要压缩
		if ( needCompress ) {
			data = Lz77.compress( data );
		}

		//需要RC4加密
		if ( needEncryptRC4 ) {
			data = Rc4.encrypt( Rc4.getRC4Key( eamuseInfo ), data );
		}

		commit( 200, data, request, response );
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
