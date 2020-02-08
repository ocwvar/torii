package com.ocwvar.kbin;

import org.python.core.Py;
import org.python.core.PyObject;
import org.python.core.PyString;
import org.python.core.PyTuple;
import org.python.modules.struct;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings( "ConstantConditions" )
public class KFormatIds {

	public static final List< XmlFormatsNode > xmlFormats;
	public static final Map< String, Integer > xmlTypes;

	static {
		xmlTypes = new LinkedHashMap<>();
		xmlFormats = new ArrayList<>( 56 + 1 ); // the first element is always null, because python "Dictionary" first position is 1
		xmlFormats.add( null );
		xmlFormats.add( new XmlFormatsNode( null, 0, null, null, "void" ) );
		xmlFormats.add( new XmlFormatsNode( "b", 1, null, null, "s8" ) );
		xmlFormats.add( new XmlFormatsNode( "B", 1, null, null, "u8" ) );
		xmlFormats.add( new XmlFormatsNode( "h", 1, null, null, "s16" ) );
		xmlFormats.add( new XmlFormatsNode( "H", 1, null, null, "u16" ) );
		xmlFormats.add( new XmlFormatsNode( "i", 1, null, null, "s32" ) );
		xmlFormats.add( new XmlFormatsNode( "I", 1, null, null, "u32" ) );
		xmlFormats.add( new XmlFormatsNode( "q", 1, null, null, "s64" ) );
		xmlFormats.add( new XmlFormatsNode( "Q", 1, null, null, "u64" ) );
		xmlFormats.add( new XmlFormatsNode( "B", -1, null, null, "bin", "binary" ) );
		xmlFormats.add( new XmlFormatsNode( "B", -1, null, null, "str", "string" ) );
		xmlFormats.add( new XmlFormatsNode( "I", 1, "parseIP", "writeIP", "ip4" ) );
		xmlFormats.add( new XmlFormatsNode( "I", 1, null, null, "time" ) );
		xmlFormats.add( new XmlFormatsNode( "f", 1, "float", "writeFloat", "float", "f" ) );
		xmlFormats.add( new XmlFormatsNode( "d", 1, "float", "writeFloat", "double", "d" ) );
		xmlFormats.add( new XmlFormatsNode( "b", 2, null, null, "2s8" ) );
		xmlFormats.add( new XmlFormatsNode( "B", 2, null, null, "2u8" ) );
		xmlFormats.add( new XmlFormatsNode( "h", 2, null, null, "2s16" ) );
		xmlFormats.add( new XmlFormatsNode( "H", 2, null, null, "2u16" ) );
		xmlFormats.add( new XmlFormatsNode( "i", 2, null, null, "2s32" ) );
		xmlFormats.add( new XmlFormatsNode( "I", 2, null, null, "2u32" ) );
		xmlFormats.add( new XmlFormatsNode( "q", 2, null, null, "2s64", "vs64" ) );
		xmlFormats.add( new XmlFormatsNode( "Q", 2, null, null, "2u64", "vu64" ) );
		xmlFormats.add( new XmlFormatsNode( "f", 2, "float", "writeFloat", "2f" ) );
		xmlFormats.add( new XmlFormatsNode( "d", 2, "float", "writeFloat", "2d", "vd" ) );
		xmlFormats.add( new XmlFormatsNode( "b", 3, null, null, "3s8" ) );
		xmlFormats.add( new XmlFormatsNode( "B", 3, null, null, "3u8" ) );
		xmlFormats.add( new XmlFormatsNode( "h", 3, null, null, "3s16" ) );
		xmlFormats.add( new XmlFormatsNode( "H", 3, null, null, "3u16" ) );
		xmlFormats.add( new XmlFormatsNode( "i", 3, null, null, "3s32" ) );
		xmlFormats.add( new XmlFormatsNode( "I", 3, null, null, "3u32" ) );
		xmlFormats.add( new XmlFormatsNode( "q", 3, null, null, "3s64" ) );
		xmlFormats.add( new XmlFormatsNode( "Q", 3, null, null, "3u64" ) );
		xmlFormats.add( new XmlFormatsNode( "f", 3, "float", "writeFloat", "3f" ) );
		xmlFormats.add( new XmlFormatsNode( "d", 3, "float", "writeFloat", "3d" ) );
		xmlFormats.add( new XmlFormatsNode( "b", 4, null, null, "4s8" ) );
		xmlFormats.add( new XmlFormatsNode( "B", 4, null, null, "4u8" ) );
		xmlFormats.add( new XmlFormatsNode( "h", 4, null, null, "4s16" ) );
		xmlFormats.add( new XmlFormatsNode( "H", 4, null, null, "4u16" ) );
		xmlFormats.add( new XmlFormatsNode( "i", 4, null, null, "4s32", "vs32" ) );
		xmlFormats.add( new XmlFormatsNode( "I", 4, null, null, "4u32", "vu32" ) );
		xmlFormats.add( new XmlFormatsNode( "q", 4, null, null, "4s64" ) );
		xmlFormats.add( new XmlFormatsNode( "Q", 4, null, null, "4u64" ) );
		xmlFormats.add( new XmlFormatsNode( "f", 4, "float", "writeFloat", "4f", "vf" ) );
		xmlFormats.add( new XmlFormatsNode( "d", 4, "float", "writeFloat", "4d" ) );
		xmlFormats.add( new XmlFormatsNode( null, 0, null, null, "attr" ) );
		xmlFormats.add( null );        //No.47 is not available yet
		xmlFormats.add( new XmlFormatsNode( "b", 16, null, null, "vs8" ) );
		xmlFormats.add( new XmlFormatsNode( "B", 16, null, null, "vu8" ) );
		xmlFormats.add( new XmlFormatsNode( "h", 8, null, null, "vs16" ) );
		xmlFormats.add( new XmlFormatsNode( "H", 8, null, null, "vu16" ) );
		xmlFormats.add( new XmlFormatsNode( "b", 1, null, null, "bool", "b" ) );
		xmlFormats.add( new XmlFormatsNode( "b", 2, null, null, "2b" ) );
		xmlFormats.add( new XmlFormatsNode( "b", 3, null, null, "3b" ) );
		xmlFormats.add( new XmlFormatsNode( "b", 4, null, null, "4b" ) );
		xmlFormats.add( new XmlFormatsNode( "b", 16, null, null, "vb" ) );

		XmlFormatsNode tempNode;
		String[] tempNames;
		for ( int i = 1; i < 56 + 1; i++ ) {
			tempNode = xmlFormats.get( i );
			tempNames = tempNode == null ? null : tempNode.names;
			if ( tempNode == null ) continue;

			for ( int j = 0; j < tempNames.length; j++ ) {
				xmlTypes.put( tempNames[ j ], i );
			}
		}

		xmlTypes.put( "nodeStart", 1 );
		xmlTypes.put( "nodeEnd", 190 );
		xmlTypes.put( "endSection", 191 );
	}

	//return PyInteger or PyLong
	public static PyObject parseIP( String raw ) {
		final String[] temp = raw.split( "\\." );
		final PyObject[] temp2 = new PyObject[ 5 ];
		temp2[ 0 ] = Py.newString( "4B" );

		for ( int i = 1; i < temp.length + 1; i++ ) {
			temp2[ i ] = Py.newInteger( Integer.parseInt( temp[ i - 1 ] ) );
		}

		final PyString temp3 = struct.pack( temp2 );

		return struct.unpack( ">I", temp3.asString() ).getArray()[ 0 ];
	}

	//are "type" is writeFloat or writeIP or null.
	public static String write( String type, Object data ) {
		switch ( type ) {

			//transform data into float number string
			case "writeFloat": {
				if ( data instanceof Float ) {
					return writeFloat( ( Float ) data );
				} else if ( data instanceof Double ) {
					return writeDouble( ( Double ) data );
				}
				throw new UnsupportedOperationException( "writeFloat() 方法只能传入 Float 或 Double 类型数据" );
			}

			//transform data into IP  like:127.0.0.1
			case "writeIP": {
				if ( data instanceof Integer ) {
					return writeIP( Py.newInteger( ( Integer ) data ) );
				} else if ( data instanceof Long ) {
					return writeIP( Py.newLong( ( Long ) data ) );
				} else if ( data instanceof Float ) {
					return writeIP( Py.newLong( ( ( Float ) data ).longValue() ) );
				} else {
					throw new UnsupportedOperationException( "writeIP() 方法只能传入 Integer 或 Long 类型数据" );
				}
			}

			//just return number string
			case "null":
			default: {
				if ( data instanceof Integer ) {
					return String.format( "%d", data );
				} else if ( data instanceof Float ) {
					return String.format( "%.0f", data );
				} else if ( data instanceof Long ) {
					return String.format( "%d", data );
				}
				throw new UnsupportedOperationException( "未知类型：" + data.getClass() );
			}
		}
	}

	//arg "raw" is PyInteger or PyLong ONLY
	private static String writeIP( PyObject raw ) {
		final PyObject[] temp = new PyObject[]{
				new PyString( ">I" ),
				raw
		};

		final PyString temp2 = struct.pack( temp );
		final PyTuple ip = struct.unpack( "4B", temp2.asString() );        //should return string like "127.0.0.1"
		return ip.get( 0 ) + "." + ip.get( 1 ) + "." + ip.get( 2 ) + "." + ip.get( 3 );
	}

	private static String writeFloat( float raw ) {
		return String.format( "%.6f", raw );
	}

	private static String writeDouble( double raw ) {
		return String.format( "%.6f", raw );
	}

}
