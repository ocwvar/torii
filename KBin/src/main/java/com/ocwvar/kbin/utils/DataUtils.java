package com.ocwvar.kbin.utils;

import org.python.core.PyByteArray;

import java.nio.charset.Charset;

public class DataUtils {

	private static final char[] HEX_ARRAY = "0123456789abcdef".toCharArray();

	public static String bytesToHex( PyByteArray bytes ) {
		final char[] hexChars = new char[ bytes.size() * 2 ];
		for ( int j = 0; j < bytes.size(); j++ ) {
			int v = ( byte ) bytes.get( j ).asInt() & 0xFF;
			hexChars[ j * 2 ] = HEX_ARRAY[ v >>> 4 ];
			hexChars[ j * 2 + 1 ] = HEX_ARRAY[ v & 0x0F ];
		}
		return new String( hexChars );
	}

	public static String bytesToHex( byte[] bytes ) {
		final char[] hexChars = new char[ bytes.length * 2 ];
		for ( int j = 0; j < bytes.length; j++ ) {
			int v = bytes[ j ] & 0xFF;
			hexChars[ j * 2 ] = HEX_ARRAY[ v >>> 4 ];
			hexChars[ j * 2 + 1 ] = HEX_ARRAY[ v & 0x0F ];
		}
		return new String( hexChars );
	}

	public static String bytesToString( PyByteArray byteArray, Charset charset ) {
		final byte[] temp = new byte[ byteArray.size() ];
		for ( int i = 0; i < temp.length; i++ ) {
			temp[ i ] = ( byte ) byteArray.get( i ).asInt();
		}

		return charset == null ? new String( temp ) : new String( temp, charset );
	}

}
