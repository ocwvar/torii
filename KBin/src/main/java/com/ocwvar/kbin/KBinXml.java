package com.ocwvar.kbin;


import com.ocwvar.kbin.utils.AssertCheck;
import com.ocwvar.xml.node.BaseNode;
import com.ocwvar.xml.node.Node;
import com.ocwvar.xml.node.NodeHelper;
import com.ocwvar.xml.node.i.INode;
import org.python.bouncycastle.pqc.math.linearalgebra.ByteUtils;
import org.python.core.*;
import org.python.modules.struct;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class KBinXml {

	private static final byte[] XML_PREFIX = "<?xml".getBytes();

	/**
	 * 加密
	 *
	 * @param text 需要加密的文本
	 * @return 加密后的字节数据
	 * @throws Exception 操作出现的异常
	 */
	public static byte[] encode( String text ) throws Exception {
		return new KBinXml( text ).getEncodedBytes();
	}

	/**
	 * 加密
	 *
	 * @param node 需要加密的Node对象
	 * @return 加密后的字节数据
	 * @throws Exception 操作出现的异常
	 */
	public static byte[] encode( Node node ) throws Exception {
		return new KBinXml( node ).getEncodedBytes();
	}

	/**
	 * 解密
	 *
	 * @param input 需要解密的字节数据
	 * @return 解密后的XML数据结构对象
	 * @throws Exception 操作出现的异常
	 */
	public static Node decode( byte[] input ) throws Exception {
		return new KBinXml( input ).getDecodedNode();
	}

	/**
	 * 判断是否为加密了的数据
	 *
	 * @param data 需要检测的数据
	 * @return 是否为加密后的数据
	 */
	public static boolean isEncoded( byte[] data ) {
		boolean isBinary = false;

		for ( int i = 0; i < XML_PREFIX.length; i++ ) {
			if ( data[ i ] != XML_PREFIX[ i ] ) {
				isBinary = true;
				break;
			}
		}

		return isBinary;
	}

	private final int _SIGNATURE = 0xA0;
	private final int _SIG_COMPRESSED = 0x42;
	private final int _SIG_UNCOMPRESSED = 0x45;

	//private final Charset _XML_ENCODING = StandardCharsets.US_ASCII;
	//private final Charset _XML_ENCODING = StandardCharsets.UTF_8;
	private final Charset _XML_ENCODING = Charset.forName( "cp932" );
	private final Charset _BIN_ENCODING = Charset.forName( "cp932" );
	//private final Charset _BIN_ENCODING = StandardCharsets.UTF_8;
	//private final Charset _BIN_ENCODING = StandardCharsets.US_ASCII;

	private final Map< Integer, String > _encoding_strings;
	private final Map< String, Integer > _encoding_vals;

	private final Node rootNode;

	{
		this._encoding_strings = new LinkedHashMap<>( 6 );
		this._encoding_vals = new LinkedHashMap<>( 5 );

		this._encoding_strings.put( 0x00, Charset.forName( "cp932" ).name() );
		this._encoding_strings.put( 0x20, StandardCharsets.US_ASCII.name() );
		this._encoding_strings.put( 0x40, StandardCharsets.ISO_8859_1.name() );
		this._encoding_strings.put( 0x60, Charset.forName( "EUC_JP" ).name() );
		this._encoding_strings.put( 0x80, Charset.forName( "cp932" ).name() );
		this._encoding_strings.put( 0xA0, StandardCharsets.UTF_8.name() );

		this._encoding_vals.put( StandardCharsets.US_ASCII.name(), 0x20 );
		this._encoding_vals.put( StandardCharsets.ISO_8859_1.name(), 0x40 );
		this._encoding_vals.put( Charset.forName( "EUC_JP" ).name(), 0x60 );
		this._encoding_vals.put( Charset.forName( "cp932" ).name(), 0x80 );
		this._encoding_vals.put( StandardCharsets.UTF_8.name(), 0xA0 );
	}

	private KByteBuffer _nodeBuf;
	private KByteBuffer _dataBuf;
	private KByteBuffer _dataByteBuf;
	private KByteBuffer _dataWordBuf;

	private PyString _encoding;
	private PyInteger _dataSize;
	private boolean _compressed = false;

	/////////////////////////已加密字节///////////////////////////////
	private byte[] encodedXmlBytes = null;
	/////////////////////////已加密字节///////////////////////////////

	/////////////////////////已解密对象///////////////////////////////
	private Node decodedNode = null;
	/////////////////////////已解密对象///////////////////////////////

	private KBinXml() {
		this.rootNode = null;
	}

	/**
	 * 加密
	 *
	 * @param encodeXmlText 需要加密的XML内容
	 */
	private KBinXml( String encodeXmlText ) throws Exception {
		this._encoding = Py.newString( this._XML_ENCODING.name() );
		this._compressed = true;
		this._dataSize = Py.newInteger( -1 );
		this.rootNode = NodeHelper.xml2Node( encodeXmlText );
		toBinary( null, true );
	}

	/**
	 * 加密
	 *
	 * @param encodeNode 需要加密的XML内容
	 */
	private KBinXml( Node encodeNode ) throws Exception {
		this._encoding = Py.newString( this._XML_ENCODING.name() );
		this._compressed = true;
		this._dataSize = Py.newInteger( -1 );
		this.rootNode = encodeNode;
		toBinary( null, true );
	}

	/**
	 * 解密
	 *
	 * @param decodeBinBytes 需要解密的字节数组
	 */
	private KBinXml( byte[] decodeBinBytes ) throws Exception {
		this.rootNode = null;
		fromBinary( new PyByteArray( decodeBinBytes ) );
	}

	/**
	 * @return 已解密的节点对象
	 */
	public Node getDecodedNode() {
		return this.decodedNode;
	}

	/**
	 * @return 已加密的字节数组
	 */
	public byte[] getEncodedBytes() {
		return this.encodedXmlBytes;
	}

	/**
	 * 将从构造方法传入的明文 XML 内容进行加密
	 *
	 * @param compressed 是否已被压缩处理，默认为 True
	 * @param encoding   数据的编码格式，默认为{@code Charset.forName("cp932")}
	 */
	private void toBinary( Charset encoding, boolean compressed ) throws UnsupportedEncodingException {
		this._encoding = Py.newString( encoding == null ? this._BIN_ENCODING.name() : encoding.name() );
		this._compressed = compressed;

		final KByteBuffer header = new KByteBuffer();
		header.$append( "u8", Py.newInteger( this._SIGNATURE ) );
		header.$append( "u8", this._compressed ? Py.newInteger( this._SIG_COMPRESSED ) : Py.newInteger( this._SIG_UNCOMPRESSED ) );
		header.$append( "u8", Py.newInteger( this._encoding_vals.get( this._encoding.asString() ) ) );
		header.$append( "u8", Py.newInteger( 0xFF ^ this._encoding_vals.get( this._encoding.asString() ) ) );
		this._nodeBuf = new KByteBuffer();
		this._dataBuf = new KByteBuffer();
		this._dataByteBuf = new KByteBuffer( this._dataBuf._input );
		this._dataWordBuf = new KByteBuffer( this._dataBuf._input );

		nodeToBinary( rootNode );

		this._nodeBuf.$append( "u8", Py.newInteger( KFormatIds.xmlTypes.get( "endSection" ) | 64 ) );
		this._nodeBuf.realignWrites( null );
		header.$append( "u32", Py.newInteger( __builtin__.len( this._nodeBuf._input ) ) );
		this._dataSize = Py.newInteger( __builtin__.len( this._dataBuf._input ) );
		this._nodeBuf.$append( "u32", this._dataSize );

		//组装三个部分的字节数据 --> result[]
		final byte[] headerPart = header.asJavaBytes();
		final byte[] nodePart = this._nodeBuf.asJavaBytes();
		final byte[] dataPart = this._dataBuf.asJavaBytes();
		final byte[] result = new byte[ headerPart.length + nodePart.length + dataPart.length ];
		System.arraycopy( headerPart, 0, result, 0, headerPart.length );
		System.arraycopy( nodePart, 0, result, headerPart.length, nodePart.length );
		System.arraycopy( dataPart, 0, result, headerPart.length + nodePart.length, dataPart.length );

		this.encodedXmlBytes = result;
	}

	/**
	 * 将 bin 数据流转换为明文 XML 内容 (解密)
	 *
	 * @param input 数据流
	 * @throws Exception 可能出现的异常
	 */
	private void fromBinary( PyByteArray input ) throws Exception {

		//工作 node
		Node xRootNode = null;
		Node xCurrentNode = null;

		this._nodeBuf = new KByteBuffer( input, -999, null );
		AssertCheck.check( ( ( PyInteger ) this._nodeBuf.$get( "u8" ) ).asInt() == this._SIGNATURE );

		//check compress
		PyInteger compress = ( PyInteger ) this._nodeBuf.$get( "u8" );
		AssertCheck.check( compress.asInt() == this._SIG_COMPRESSED || compress.asInt() == this._SIG_UNCOMPRESSED );
		this._compressed = compress.asInt() == this._SIG_COMPRESSED;

		//get encoding
		PyInteger encodingKey = ( PyInteger ) this._nodeBuf.$get( "u8" );
		AssertCheck.check( ( ( PyInteger ) this._nodeBuf.$get( "u8" ) ).asInt() == ( 0xFF ^ encodingKey.asInt() ) );
		this._encoding = new PyString( this._encoding_strings.get( encodingKey.asInt() ) );

		final int nodeEnd = ( ( PyInteger ) this._nodeBuf.$get( "u32" ) ).asInt() + 8;
		this._nodeBuf._end = nodeEnd;

		this._dataBuf = new KByteBuffer( input, nodeEnd, null );
		this._dataSize = ( PyInteger ) this._dataBuf.$get( "u32" );
		this._dataByteBuf = new KByteBuffer( input, nodeEnd, null );
		this._dataWordBuf = new KByteBuffer( input, nodeEnd, null );

		boolean nodesLeft = true;
		while ( nodesLeft && this._nodeBuf.hasData() ) {
			while ( ( ( PyInteger ) this._nodeBuf.$peek( "u8" ) ).asInt() == 0 ) {
				this._nodeBuf.$get( "u8" );
			}

			PyInteger nodeType = ( PyInteger ) this._nodeBuf.$get( "u8" );
			int isArray = nodeType.asInt() & 64;
			nodeType = new PyInteger( nodeType.asInt() & ~64 );

			XmlFormatsNode nodeFormat = nodeType.asInt() >= KFormatIds.xmlFormats.size() ? null : KFormatIds.xmlFormats.get( nodeType.asInt() );
			if ( nodeFormat == null ) {
				nodeFormat = new XmlFormatsNode( null, 0, null, "Unknown" );
			}

			PyString name = new PyString( "" );
			if ( nodeType.asInt() != KFormatIds.xmlTypes.get( "nodeEnd" ) && nodeType.asInt() != KFormatIds.xmlTypes.get( "endSection" ) ) {
				if ( this._compressed ) {
					name = KSixBit.unpackSixBit( this._nodeBuf );
				} else {
					final int length = ( ( ( PyInteger ) this._nodeBuf.$get( "u8" ) ).asInt() & ~64 ) + 1;
					name = new PyString( this._nodeBuf.getBytes( length ).asString() );
					name = ( PyString ) name.decode( this._encoding.asString() );
				}
			}

			boolean skip = true;

			if ( nodeType.asInt() == KFormatIds.xmlTypes.get( "attr" ) ) {
				//设置某个 node 的属性条目
				final String value = dataGrabString();
				xCurrentNode.addAttribute( name.asString(), value );
			} else if ( nodeType.asInt() == KFormatIds.xmlTypes.get( "nodeEnd" ) ) {
				//当前的 node 已处理完成，处理 node 切换回 root
				if ( xCurrentNode.getParentNode() != null ) {
					xCurrentNode = ( Node ) xCurrentNode.getParentNode();
				}
			} else if ( nodeType.asInt() == KFormatIds.xmlTypes.get( "endSection" ) ) {
				//解析结束，循环
				nodesLeft = false;
			} else if ( KFormatIds.xmlFormats.get( nodeType.asInt() ) == null ) {
				//不支持的 node 类型
				throw new UnsupportedOperationException( "Unknown node type：" + nodeType.asInt() );
			} else {
				//inner value to process
				skip = false;
			}

			if ( skip ) {
				continue;
			}

			//创建新的节点 node
			if ( xRootNode == null ) {
				xRootNode = new Node( name.asString() );
				xCurrentNode = xRootNode;
			} else {
				final Node childNode = new Node( name.asString() );
				xCurrentNode.addChildNode( childNode );
				xCurrentNode = childNode;
			}

			if ( nodeType.asInt() == KFormatIds.xmlTypes.get( "nodeStart" ) ) {
				continue;
			}

			//标签的 __type 类型
			int varCount = nodeFormat.count.asInt();
			int arrayCount = 1;

			if ( varCount == -1 ) {
				varCount = ( ( PyInteger ) this._dataBuf.$get( "u32" ) ).asInt();
				isArray = 1;
			} else if ( isArray != 0 ) {
				arrayCount = ( int ) Math.floor( ( ( PyInteger ) this._dataBuf.$get( "u32" ) ).asInt() * 1f / ( struct.calcsize( nodeFormat.type.asString() ) * varCount ) );
				xCurrentNode.addAttribute( INode.ATTR_COUNT, String.valueOf( arrayCount ) );
			}

			int totalCount = arrayCount * varCount;
			final PyTuple data;
			if ( isArray != 0 ) {
				data = ( PyTuple ) this._dataBuf.get( nodeFormat.type.asString(), new PyInteger( totalCount ) );
				this._dataBuf.realignReads( null );
			} else {
				data = dataGrabAligned( nodeFormat.type, new PyInteger( totalCount ) );
			}

			final String string;
			if ( nodeType.asInt() == KFormatIds.xmlTypes.get( "binary" ) ) {
				final StringBuilder stringBuilder = new StringBuilder();
				for ( int i = 0; i < data.__len__(); i++ ) {
					stringBuilder.append( String.format( "%02x", data.get( i ) ) );
				}
				string = stringBuilder.toString();
			} else if ( nodeType.asInt() == KFormatIds.xmlTypes.get( "string" ) ) {
				final byte[] temp = new byte[ data.size() - 1 ];
				for ( int i = 0; i < temp.length; i++ ) {
					temp[ i ] = ( ( Integer ) data.get( i ) ).byteValue();
				}
				string = new String( temp, this._encoding.asString() );
			} else {
				final StringBuilder stringBuilder = new StringBuilder();
				Object tempNumber;
				for ( int i = 0; i < data.size(); i++ ) {
					tempNumber = data.get( i );
					if ( tempNumber instanceof BigInteger ) {
						if ( nodeFormat.toStr.equals( "writeFloat" ) ) {
							stringBuilder.append( KFormatIds.write( nodeFormat.toStr, ( float ) ( ( BigInteger ) tempNumber ).doubleValue() ) );
						} else {
							//这里不能转成 float，否则精度会丢失
							stringBuilder.append( KFormatIds.write( nodeFormat.toStr, ( ( BigInteger ) tempNumber ).longValue() ) );
						}
					} else if ( tempNumber instanceof Integer ) {
						stringBuilder.append( KFormatIds.write( nodeFormat.toStr, ( ( Integer ) tempNumber ).floatValue() ) );
					} else if ( tempNumber instanceof Double ) {
						stringBuilder.append( KFormatIds.write( nodeFormat.toStr, tempNumber ) );
					} else {
						throw new NumberFormatException( "未预料到的数字类型" + tempNumber );
					}

					if ( i + 1 < data.size() ) {
						stringBuilder.append( " " );
					}
				}
				string = stringBuilder.toString();
			}

			//这里统一写入 Node 标签
			xCurrentNode.addAttribute( INode.ATTR_TYPE, nodeFormat.names[ 0 ] );

			if ( nodeType.asInt() == KFormatIds.xmlTypes.get( "binary" ) ) {
				xCurrentNode.addAttribute( INode.ATTR_SIZE, String.valueOf( totalCount ) );
			}

			xCurrentNode.setContentValue( string );
		}

		this.decodedNode = xRootNode;
	}

	/**
	 * 将 XML 的 node 转换为 bin (加密)
	 *
	 * @param node 需要进行转换的 node 对象
	 */
	private void nodeToBinary( Node node ) throws UnsupportedEncodingException {
		String noteType = node.getAttribute( INode.ATTR_TYPE );
		if ( noteType == null ) {
			if ( node.childCount() > 0 ) {
				noteType = "void";
			} else {
				noteType = node.getContentValue() == null ? "void" : "str";
			}
		}

		final int nodeId = KFormatIds.xmlTypes.get( noteType );
		final String countString = node.getAttribute( INode.ATTR_COUNT );

		final String name = node.getName();
		int isArray = 0;
		final int count;        // -1 means nothing
		if ( countString != null && !countString.isEmpty() ) {
			count = Integer.parseInt( countString );
			isArray = 64;
		} else {
			count = -1;
		}

		this._nodeBuf.$append( "u8", Py.newInteger( nodeId | isArray ) );
		appendNodeName( Py.newString( name ) );

		if ( !noteType.equals( "void" ) ) {
			final XmlFormatsNode formatsNode = KFormatIds.xmlFormats.get( nodeId );
			//final String val = node.getTextContent();
			final String val = node.getContentValue();

			final PyObject data;    //PyByteArray or PyList
			switch ( formatsNode.names[ 0 ] ) {
				case "bin":
					data = new PyByteArray( ByteUtils.fromHexString( val ) );
					break;
				case "str":
					//data = new PyByteArray( ( new PyString( val ).encode( this._encoding.asString(), "replace" ) + " " ).getBytes() );
					data = new PyByteArray( ( val + "\0" ).getBytes( this._encoding.asString() ) );
					break;
				default:
					final String[] vals = val.split( " " );
					data = new PyList();

					for ( final String string : vals ) {
						switch ( formatsNode.fromStr ) {
							case "float":
								( ( PyList ) data ).add( Py.newFloat( Float.parseFloat( string ) ) );
								break;

							case "parseIP":
								( ( PyList ) data ).add( KFormatIds.parseIP( string ) );
								break;

							case "null":
							default:
								try {
									( ( PyList ) data ).add( Py.newInteger( Integer.parseInt( string ) ) );
								} catch ( NumberFormatException e ) {
									( ( PyList ) data ).add( Py.newInteger( Long.parseLong( string ) ) );
								}
								break;
						}
					}

					if ( count != -1 && ( ( ( PyList ) data ).size() / formatsNode.count.asInt() ) != count ) {
						throw new IllegalStateException( "Array length does not match __count attribute" );
					}
					break;
			}

			if ( isArray != 0 || formatsNode.count.asInt() == -1 ) {
				this._dataBuf.$append( "u32", Py.newInteger( __builtin__.len( data ) * struct.calcsize( formatsNode.type.asString() ) ) );
				this._dataBuf.append( data, formatsNode.type.asString(), Py.newInteger( __builtin__.len( data ) ) );
				this._dataBuf.realignWrites( null );
			} else {
				dataAppendAligned( data, formatsNode.type.asString(), formatsNode.count );
			}
		}

		if ( node.attributeCount() > 0 ) {
			final Set< Map.Entry< String, String > > attrs = node.getAttributes().entrySet();
			for ( Map.Entry< String, String > attr : attrs ) {
				if ( !attr.getKey().matches( "(__type|__count|__size){1}" ) ) {
					this.dataAppendString( attr.getValue() );
					this._nodeBuf.$append( "u8", Py.newInteger( KFormatIds.xmlTypes.get( "attr" ) ) );
					this.appendNodeName( Py.newString( attr.getKey() ) );
				}
			}
		}

		if ( node.childCount() > 0 ) {
			final List< BaseNode > childNodes = node.getChildNodes();
			for ( BaseNode baseNode : childNodes ) {
				nodeToBinary( ( Node ) baseNode );
			}
		}

		this._nodeBuf.$append( "u8", Py.newInteger( KFormatIds.xmlTypes.get( "nodeEnd" ) | 64 ) );
	}

	private PyByteArray dataGrabAuto() {
		final int size = ( ( PyInteger ) this._dataBuf.$get( "s32" ) ).asInt();
		final PyByteArray ret = this._dataBuf.getBytes( size );
		this._dataBuf.realignReads( null );
		return ret;
	}

	private void dataAppendAuto( PyByteArray data ) {
		this._dataBuf.$append( "s32", Py.newInteger( __builtin__.len( data ) ) );
		this._dataBuf.appendBytes( data );
		this._dataBuf.realignWrites( null );
	}

	private String dataGrabString() throws UnsupportedEncodingException {
		final PyByteArray data = dataGrabAuto();
		final byte[] temp = new byte[ data.size() - 1 ];

		for ( int i = 0; i < temp.length; i++ ) {
			temp[ i ] = ( byte ) data.get( i ).asInt();
		}

		return new String( temp, this._encoding.asString() );
	}

	private void dataAppendString( String string ) throws UnsupportedEncodingException {
		final PyByteArray byteArray = new PyByteArray( ( string + "\0" ).getBytes( this._encoding.asString() ) );
		dataAppendAuto( byteArray );
	}

	private PyTuple dataGrabAligned( PyString type, PyInteger count ) {
		if ( this._dataByteBuf._offset % 4 == 0 ) {
			this._dataByteBuf._offset = this._dataBuf._offset;
		}

		if ( this._dataWordBuf._offset % 4 == 0 ) {
			this._dataWordBuf._offset = this._dataBuf._offset;
		}

		final int size = struct.calcsize( type.asString() ) * count.asInt();
		final PyTuple ret;
		if ( size == 1 ) {
			ret = ( PyTuple ) this._dataByteBuf.get( type.asString(), count );
		} else if ( size == 2 ) {
			ret = ( PyTuple ) this._dataWordBuf.get( type.asString(), count );
		} else {
			ret = ( PyTuple ) this._dataBuf.get( type.asString(), count );
			this._dataBuf.realignReads( null );
		}

		final int trailing = Math.max( this._dataByteBuf._offset, this._dataWordBuf._offset );
		if ( this._dataBuf._offset < trailing ) {
			this._dataBuf._offset = trailing;
			this._dataBuf.realignReads( null );
		}

		return ret;
	}

	private void dataAppendAligned( PyObject data, String type, PyInteger count ) {
		if ( this._dataByteBuf._offset % 4 == 0 ) {
			this._dataByteBuf._offset = this._dataBuf._offset;
		}

		if ( this._dataWordBuf._offset % 4 == 0 ) {
			this._dataWordBuf._offset = this._dataBuf._offset;
		}

		final int size = struct.calcsize( type ) * count.asInt();
		if ( size == 1 ) {
			if ( this._dataByteBuf._offset % 4 == 0 ) {
				this._dataBuf.$append( "u32", Py.newInteger( 0 ) );
			}
			this._dataByteBuf.set( data, type, Py.newInteger( this._dataByteBuf._offset ), count );
		} else if ( size == 2 ) {
			if ( this._dataWordBuf._offset % 4 == 0 ) {
				this._dataBuf.$append( "u32", Py.newInteger( 0 ) );
			}
			this._dataWordBuf.set( data, type, Py.newInteger( this._dataWordBuf._offset ), count );
		} else {
			this._dataBuf.append( data, type, count );
			this._dataBuf.realignWrites( null );
		}
	}

	private void appendNodeName( PyString name ) {
		if ( this._compressed ) {
			KSixBit.packSixBit( name, this._nodeBuf );
		} else {
			final PyString enc = new PyString( name.encode( this._encoding.asString() ) );
			this._nodeBuf.$append( "u8", enc );
			this._nodeBuf.appendBytes( enc );
		}
	}

}
