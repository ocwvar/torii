package com.ocwvar.torii.utils.protocol;

import com.ocwvar.utils.annotation.NotNull;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Rc4 {

	private final static byte[] KEY;

	static {
		KEY = stringHex2byteArray( "00000000000069D74627D985EE2187161570D08D93B12455035B6DF0D8205DF5" );
	}

	/**
	 * 获取 原始的RC4 key
	 *
	 * @param xEamuseInfo 对应 Request.Header 中 X-Eamuse-Info 的值
	 * @return RC4 key
	 * @throws NoSuchAlgorithmException 设备不支持的加解密方式
	 */
	public static byte[] getRC4Key( String xEamuseInfo ) throws NoSuchAlgorithmException {
		final String text = xEamuseInfo.substring( 2 ).replace( "-", "" );
		byte[] keys = KEY.clone();
		for ( int index = 0, cutFrom = 2; index < 6; index++, cutFrom += 2 ) {
			keys[ index ] = ( byte ) Integer.parseInt( text.substring( cutFrom - 2, cutFrom ), 16 );
		}

		final MessageDigest messageDigest = MessageDigest.getInstance( "MD5" );
		return messageDigest.digest( keys );
	}

	/**
	 * 内容解密操作
	 *
	 * @param key  RC4 Key
	 * @param data 数据内容
	 * @return 解密得到的数据
	 */
	public static byte[] decrypt( byte[] key, byte[] data ) {
		return computeIntegerArrayData( key, data );
	}

	/**
	 * 内容加密操作
	 *
	 * @param key  RC4 Key
	 * @param data 数据内容
	 * @return 加密得到的数据
	 */
	public static byte[] encrypt( byte[] key, byte[] data ) {
		return computeIntegerArrayData( key, data );
	}

	/**
	 * 将 16进制  字符串转换为 byte数组
	 *
	 * @param hexString 16进制字符串
	 * @return 结果数据
	 */
	@SuppressWarnings( "SameParameterValue" )
	private @NotNull
	static byte[] stringHex2byteArray( @NotNull String hexString ) {
		final byte[] result = new byte[ hexString.length() / 2 ];
		for ( int index = 0, cutFrom = 2; index < result.length; index++, cutFrom += 2 ) {
			result[ index ] = ( byte ) Integer.parseInt( hexString.substring( cutFrom - 2, cutFrom ), 16 );
		}
		return result;
	}

	/**
	 * 根据 KEY 对输入字节数组进行处理
	 *
	 * @param key  RC4 KEY
	 * @param data 输入的字节数组
	 * @return 输出的字节数组
	 */
	private @NotNull
	static byte[] computeIntegerArrayData( byte[] key, byte[] data ) {
		//生成0~255的 int数组
		final int[] array = new int[ 256 ];
		final byte[] result = new byte[ data.length ];
		for ( int i = 0; i < array.length; i++ ) {
			array[ i ] = i;
		}

		//第一步处理
		for ( int i = 0, j = 0; i < 256; i++ ) {
			j = ( j + key[ i % key.length ] + array[ i ] ) & 255;    //198
			swap( array, i, j );
		}

		//第二步处理
		for ( int i = 0, j = 0, k = 0; i < data.length; i++ ) {
			j = ( j + 1 ) & 255;
			k = ( k + array[ j ] ) & 255;
			swap( array, j, k );

			result[ i ] = ( byte ) ( data[ i ] ^ array[ ( array[ j ] + array[ k ] ) & 255 ] );
		}

		return result;
	}

	private static void swap( @NotNull int[] source, int from, int to ) {
		final int c = source[ from ];
		source[ from ] = source[ to ];
		source[ to ] = c;
	}

}
