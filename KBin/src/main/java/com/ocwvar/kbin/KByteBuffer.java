package com.ocwvar.kbin;

import org.python.core.*;
import org.python.jline.internal.Nullable;
import org.python.modules.struct;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Translate from ByteBuffer.py
 */
public class KByteBuffer {

	PyByteArray _input;
	String _endian;
	int _offset;
	int _end;

	private final Map< String, String > _typeMap;

	{
		Py.initPython();

		this._typeMap = new LinkedHashMap<>();
		this._typeMap.put( "s8", "b" );
		this._typeMap.put( "s16", "h" );
		this._typeMap.put( "s32", "i" );
		this._typeMap.put( "s64", "q" );
		this._typeMap.put( "u8", "B" );
		this._typeMap.put( "u16", "H" );
		this._typeMap.put( "u32", "I" );
		this._typeMap.put( "u64", "Q" );
	}

	public KByteBuffer() {
		this._offset = 0;
		this._endian = ">";
		this._input = new PyByteArray();
		this._end = this._input.size();
	}

	public KByteBuffer( PyByteArray pyByteArray ) {
		this._input = pyByteArray;
		this._offset = 0;
		this._endian = ">";
		this._end = this._input.size();
	}

	public KByteBuffer( PyByteArray input, int offset, String endian ) {
		this._input = input;
		this._offset = offset == -999 ? 0 : offset;
		this._endian = endian == null ? ">" : endian;
		this._end = this._input.size();
	}

	private static PyByteArray arraySplit( PyByteArray source, int start, int offset ) {
		final PyByteArray result = new PyByteArray( offset - start );
		for ( int i = start, j = 0; i < offset; i++, j++ ) {
			result.set( j, source.get( i ) );
		}
		return result;
	}

	private String formatType( String type, @Nullable PyInteger count ) {
		return count == null ? this._endian + type : this._endian + String.valueOf( count.asInt() ) + type;
	}

	public PyByteArray getBytes( int count ) {
		final int start = this._offset;
		this._offset += count;
		return arraySplit( this._input, start, this._offset );
	}

	//return PyInteger or PyTuple returned by peek( type , count )
	public Object get( String type, @Nullable PyInteger count ) {
		final Object ret = peek( type, count );
		int size = struct.calcsize( type );
		if ( count != null ) {
			size *= count.asInt();
		}
		this._offset += size;
		return ret;
	}

	//return PyInteger or PyTuple
	public Object peek( String type, @Nullable PyInteger count ) {
		final String fmt = formatType( type, count );
		final PyTuple ret = struct.unpack_from( fmt, this._input.asString(), this._offset );

		if ( count == null ) {
			final Object valueClass = ret.get( 0 );
			final int value;
			if ( valueClass instanceof Integer ) {
				value = ( Integer ) valueClass;
			} else if ( valueClass instanceof BigInteger ) {
				value = ( ( BigInteger ) valueClass ).intValue();
			} else {
				throw new NumberFormatException( "未预料到的数字类型:" + valueClass );
			}

			return new PyInteger( value );
		}

		return ret;
	}

	public void appendBytes( PyObject data ) {
		if ( data.getType().toString().equals( PyInteger.TYPE.toString() ) ) {
			data = new PyTuple( data );
		}
		this._input.extend( data );
		this._offset += data.__len__();
	}

	public void append( PyObject data, String type, @Nullable PyInteger count ) {
		final String fmt = formatType( type, count );
		this._offset += struct.calcsize( fmt );

		final PyObject[] array = handleAppendAndSetData( fmt, data );

		this._input.extend( struct.pack( array ) );
	}

	public void set( PyObject data, String type, PyInteger offset, @Nullable PyInteger count ) {
		final String fmt = formatType( type, count );
		//TODO data 有可能是 PyList

		final PyObject[] array;
		final int oldLength = this._input.__len__();
		if ( data.getType().toString().equals( PyList.TYPE.toString() ) ) {
			//如果是一个列表数据，则拼接
			array = new PyObject[ 3 + ( ( PyList ) data ).size() ];
			array[ 0 ] = new PyString( fmt );
			array[ 1 ] = PyArray.array( this._input, Integer.TYPE );
			array[ 2 ] = offset;

			Object temp;
			for ( int i = 3, j = 0; j < ( ( PyList ) data ).size(); i++, j++ ) {
				temp = ( ( PyList ) data ).get( j );
				if ( temp instanceof Integer ) {
					array[ i ] = Py.newInteger( ( int ) temp );
				} else {
					throw new UnsupportedOperationException( "未知类型" );
				}
			}
		} else {
			array = new PyObject[]{
					new PyString( fmt ),
					PyArray.array( this._input, Integer.TYPE ),
					offset,
					data
			};
		}

		struct.pack_into( array );

		//如果尺寸变了，则先补零，操你妈
		if ( oldLength < array[ 1 ].__len__() ) {
			final int m = array[ 1 ].__len__() - oldLength;
			for ( int i = 0; i < m; i++ ) {
				this._input.extend( Py.newInteger( 0 ) );
			}
		}

		//然后整体替换数据
		final int[] result = ( int[] ) ( ( PyArray ) array[ 1 ] ).getArray();
		for ( int i = 0; i < this._input.__len__(); i++ ) {
			this._input.set( i, Py.newInteger( result[ i ] ) );
		}

		this._offset += struct.calcsize( fmt );
	}

	private PyObject[] handleAppendAndSetData( String fmt, PyObject data ) {
		final PyObject[] array;
		if ( data.getType().toString().equals( PyList.TYPE.toString() ) ) {
			final PyList raw = ( PyList ) data;
			array = new PyObject[ ( fmt == null ? 0 : 1 ) + raw.size() ];
			array[ 0 ] = fmt == null ? null : Py.newString( fmt );
			Object object;
			for ( int i = 1; i < raw.size() + 1; i++ ) {
				object = raw.get( i - 1 );

				if ( object instanceof BigInteger ) {
					array[ i ] = Py.newInteger( ( ( BigInteger ) object ).longValue() );
				} else if ( object instanceof Integer ) {
					array[ i ] = Py.newInteger( ( int ) object );
				} else if ( object instanceof BigDecimal ) {
					array[ i ] = Py.newInteger( ( ( BigDecimal ) object ).longValue() );
				} else if ( object instanceof Double ) {
					array[ i ] = Py.newFloat( ( double ) object );
				} else if ( object instanceof Float ) {
					array[ i ] = Py.newFloat( ( float ) object );
				} else {
					throw new UnsupportedOperationException( "不支持的操作类型" );
				}
			}

		} else if ( data.getType().toString().equals( PyByteArray.TYPE.toString() ) ) {
			final PyByteArray raw = ( PyByteArray ) data;
			array = new PyObject[ ( fmt == null ? 0 : 1 ) + raw.size() ];
			array[ 0 ] = fmt == null ? null : Py.newString( fmt );

			PyInteger object;
			for ( int i = 1; i < raw.size() + 1; i++ ) {
				object = raw.get( i - 1 );
				array[ i ] = object;
			}
		} else {
			array = new PyObject[]{ Py.newString( fmt ), data };
		}

		return array;
	}

	public boolean hasData() {
		return this._offset < this._end;
	}

	public void realignWrites( @Nullable PyInteger size ) {
		if ( size == null ) {
			size = new PyInteger( 4 );
		}
		while ( this._input.__len__() % size.asInt() != 0 ) {
			$append( "u8", new PyInteger( 0 ) );
		}
	}

	public void realignReads( @Nullable PyInteger size ) {
		if ( size == null ) {
			size = new PyInteger( 4 );
		}
		while ( this._offset % size.asInt() != 0 ) {
			this._offset += 1;
		}
	}

	//return PyInteger or PyObject[]{PyInteger,PyLong} returned by peek( type , count )
	public Object $get( String type ) {
		type = this._typeMap.get( type );
		return get( type, null );
	}

	public void $set( String type, PyObject data, PyInteger offset ) {
		type = this._typeMap.get( type );
		set( data, type, offset, null );
	}

	//return Python's number format : float long int... returned by peek( type , count )
	public Object $peek( String type ) {
		type = this._typeMap.get( type );
		return peek( type, null );
	}

	public void $append( String type, PyObject data ) {
		type = this._typeMap.get( type );
		append( data, type, null );
	}

	public byte[] asJavaBytes() {
		final byte[] result = new byte[ this._input.size() ];
		for ( int i = 0; i < result.length; i++ ) {
			result[ i ] = Py.py2byte( this._input.get( i ) );
		}

		return result;
	}

}
