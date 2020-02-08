package com.ocwvar.kbin;

import com.ocwvar.kbin.utils.DataUtils;
import org.python.bouncycastle.pqc.math.linearalgebra.ByteUtils;
import org.python.core.*;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * Translate from sixbit.py
 */
public class KSixBit {

	private static final String _Charmap = "0123456789:ABCDEFGHIJKLMNOPQRSTUVWXYZ_abcdefghijklmnopqrstuvwxyz";

	//return PyInteger or PyLong
	private static BigInteger intFromBytes( PyByteArray b ) {
		return new BigInteger( DataUtils.bytesToHex( b ), 16 );
	}

	// arg : n for PyLong
	private static PyByteArray intToBytes( PyLong n, PyInteger length ) {
		final String h = n.getValue().toString( 16 );

		PyString s = new PyString( "0".repeat( h.length() % 2 ) + h );
		s = new PyString( s.zfill( length.asInt() * 2 ) );

		return new PyByteArray( ByteUtils.fromHexString( s.asString() ) );
	}

	public static void packSixBit( PyString string, KByteBuffer byteBuffer ) {
		final int[] charsIndex = new int[ string.__len__() ];
		for ( int i = 0; i < string.__len__(); i++ ) {
			charsIndex[ i ] = KSixBit._Charmap.indexOf( string.getString().charAt( i ) );
		}

		int padding = 8 - ( string.__len__() * 6 % 8 );
		if ( padding == 8 ) {
			padding = 0;
		}

		PyLong bits;    //这里需要一个超他妈大的数进行操作
		BigInteger jBits = BigInteger.valueOf( 0L );
		for ( int i : charsIndex ) {
			jBits = jBits.shiftLeft( 6 );
			jBits = jBits.or( BigInteger.valueOf( i ) );
		}
		jBits = jBits.shiftLeft( padding );
		bits = new PyLong( jBits );

		final int length = ( int ) Math.floor( ( string.__len__() * 6 + padding ) / 8d );
		final PyByteArray byteArray = intToBytes( bits, new PyInteger( length ) );
		byteBuffer.appendBytes( new PyTuple( new PyInteger( string.__len__() ) ) );
		byteBuffer.appendBytes( byteArray );
	}

	public static PyString unpackSixBit( KByteBuffer byteBuffer ) {
		final int length = ( ( PyInteger ) byteBuffer.$get( "u8" ) ).asInt();
		final int length_bits = length * 6;
		final int length_bytes = ( int ) Math.floor( ( length_bits + 7 ) / 8d );
		int padding = 8 - ( length_bits % 8 );
		padding = padding == 8 ? 0 : padding;

		final List< BigInteger > result = new ArrayList<>();
		BigInteger bits = intFromBytes( byteBuffer.getBytes( length_bytes ) );
		bits = bits.shiftRight( padding );
		for ( int i = 0; i < length; i++ ) {
			result.add( bits.and( BigInteger.valueOf( 0b111111 ) ) );
			bits = bits.shiftRight( 6 );
		}

		final StringBuilder stringBuilder = new StringBuilder( result.size() );
		BigInteger number;
		for ( int i = result.size() - 1; i >= 0; i-- ) {
			number = result.get( i );
			stringBuilder.append( KSixBit._Charmap.charAt( number.intValue() ) );
		}

		return new PyString( stringBuilder.toString() );
	}

}
