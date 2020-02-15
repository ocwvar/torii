package com.ocwvar.torii.utils.protocol;

import java.nio.IntBuffer;

public class KLz77 {

	private static final int RE_EXPEND_SIZE = 2000;
	private static final int WINDOW_SIZE = 256;
	private static final int LOOK_AHEAD = 0xf + 3;

	/**
	 * 数据解压
	 *
	 * @param bytes 数据
	 * @return 解压后的数据
	 */
	public static byte[] decompress( byte[] bytes ) {
		//必须要转换为无符号整数才能进行正确的操作
		int[] data = byteArray2intArray( bytes );

		//默认使用原数据的长度进行初始化
		IntBuffer res = IntBuffer.allocate( bytes.length );
		bytes = null;

		int pos = 0;
		int state = 0;
		while ( pos < data.length ) {
			state >>= 1;

			if ( state <= 1 ) {
				state = data[ pos++ ] | 0x100;
			}

			if ( ( state & 1 ) != 0 ) {
				if ( res.position() == res.limit() ) {
					//这里需要拓展长度
					IntBuffer temp = IntBuffer.allocate( res.limit() + RE_EXPEND_SIZE );
					temp.put( res.array() );
					res = temp;
				}

				res.put( data[ pos++ ] );
			} else {
				int b1 = data[ pos++ ];
				int b2 = data[ pos++ ];
				int length = ( b2 & 0xf ) + 3;
				int distance = ( b1 << 4 ) | ( b2 >> 4 );

				if ( distance == 0 ) {
					break;
				}

				int resPos = res.position();
				for ( int i = 0; i < length; ++i ) {
					if ( res.position() == res.limit() ) {
						//这里需要拓展长度
						IntBuffer temp = IntBuffer.allocate( res.limit() + RE_EXPEND_SIZE );
						temp.put( res.array() );
						res = temp;
					}

					int o = resPos - distance + i;
					res.put( o < 0 ? 0x0 : res.get( o ) );
				}
			}
		}

		int[] temp = new int[ res.position() ];
		res.rewind();    //这里重置 position 然后将有效数据拿出来
		res.get( temp, 0, temp.length );
		res.clear();
		res = null;

		//最后转换出 Java 标准的 byte 数组数据
		final byte[] result = intArray2byteArray( temp );
		temp = null;
		data = null;

		return result;
	}

	/**
	 * 数据压缩
	 *
	 * @param bytes 需要压缩的数据
	 * @return 压缩后的数据
	 */
	public static byte[] compress( byte[] bytes ) {

		int[] data = byteArray2intArray( bytes );
		int[] result = new int[ data.length + ( data.length / 8 ) + 4 ];
		bytes = null;

		int resOffset = 1;
		int resStateOffset = 0;
		int resStateShift = 0;
		int offset = 0;

		while ( offset < data.length ) {

			//match[0] - length      match[1] - distance
			int[] match = findLongestMatch( data, offset, KLz77.WINDOW_SIZE, KLz77.LOOK_AHEAD, 3 );
			if ( match[ 0 ] >= 3 && match[ 1 ] > 0 ) {
				int binLength = match[ 0 ] - 3;
				result[ resOffset++ ] = match[ 1 ] >> 4;
				result[ resOffset++ ] = ( ( ( match[ 1 ] & 0xf ) << 4 ) | binLength );
				resStateShift += 1;
				offset += match[ 0 ];
			} else {
				result[ resStateOffset ] |= ( 1 << resStateShift++ );
				result[ resOffset++ ] = data[ offset++ ];
			}

			if ( resStateShift >= 8 ) {
				resStateShift = 0;
				resStateOffset = resOffset++;
			}
		}

		final byte[] finalResult = intArray2byteArray( result, resOffset + 2 );
		data = null;
		result = null;

		return finalResult;
	}

	private static int[] findLongestMatch( int[] data, int offset, int windowSize, int lookAhead, int minLength ) {
		int[] res = new int[]{ -1, -1 };
		int maxLength = Math.min( lookAhead, data.length - offset );
		for ( int matchLength = maxLength; matchLength >= minLength; --matchLength ) {
			for ( int distance = 1; distance <= windowSize; ++distance ) {
				if ( repeatingSequencesEqual( data, offset, matchLength, offset - distance, distance ) ) {
					res[ 1 ] = distance;
					res[ 0 ] = matchLength;
					return res;
				}
			}
		}

		return res;
	}

	private static boolean repeatingSequencesEqual( int[] arr, int matchOffset, int matchLength, int compOffset, int compLength ) {
		int t1, t2;
		for ( int i = 0; i < matchLength; ++i ) {
			t1 = matchOffset + i < 0 ? 0 : arr[ matchOffset + i ];
			t2 = compOffset + ( i % compLength ) < 0 ? 0 : arr[ compOffset + ( i % compLength ) ];
			if ( t1 != t2 ) {
				return false;
			}
		}

		return true;
	}

	/**
	 * 将 byte数组 转换为 0~255 的 int数组
	 *
	 * @param data 数据源
	 * @return 结果数据
	 */
	public static int[] byteArray2intArray( byte[] data ) {
		final int[] result = new int[ data.length ];
		for ( int i = 0; i < data.length; i++ ) {
			result[ i ] = data[ i ] >= 0 ? data[ i ] : ( 128 + ( 128 - Math.abs( data[ i ] ) ) );
		}

		return result;
	}

	/**
	 * 将符合 0~255 的 int数组 转换为  byte数组
	 *
	 * @param data 数据源
	 * @return 结果数据
	 */
	public static byte[] intArray2byteArray( int[] data ) throws NumberFormatException {
		return intArray2byteArray( data, data.length );
	}

	/**
	 * 将符合 0~255 的 int数组 转换为  byte数组
	 *
	 * @param data 数据源
	 * @return 结果数据
	 */
	public static byte[] intArray2byteArray( int[] data, int length ) throws NumberFormatException {
		final byte[] result = new byte[ length ];
		for ( int i = 0; i < length; i++ ) {
			if ( data[ i ] > 255 || data[ i ] < 0 ) {
				throw new NumberFormatException( "所需要转换的数据中子元素超出边界 [0,255]" );
			}
			result[ i ] = ( byte ) data[ i ];
		}

		return result;
	}

}
