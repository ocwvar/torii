package com.ocwvar.torii;

import com.ocwvar.kbin.KBinXml;
import com.ocwvar.torii.utils.IO;
import com.ocwvar.torii.utils.protocol.Lz77;
import com.ocwvar.xml.node.Node;
import com.ocwvar.xml.node.NodeHelper;

public class Lz77Test {

	public static void main( String[] args ) throws Exception {
		final byte[] kBinData = IO.loadFile( true, "H:\\TORII\\DEBUG_OUTPUT\\dump\\response\\game_sv4_load_m.kbin" );
		if ( kBinData == null ) {
			throw new RuntimeException( "读取不到文件" );
		}

		final Node resultNode = KBinXml.decode( kBinData );

		System.out.println( "=============================================" );
		System.out.println( "原始数据长度：" + kBinData.length );
		System.out.println( "节点名称：" + resultNode.getName() );
		System.out.println( "=============================================" );

		final byte[] compressData = Lz77.decompress( kBinData );
		System.out.println( "压缩后数据长度：" + compressData.length );
		System.out.println( "=============================================" );

		final byte[] decompressData = Lz77.compress( compressData );
		System.out.println( "解压后数据长度：" + decompressData.length );
		System.out.println( "=============================================" );

		final Node finalNode = KBinXml.decode( decompressData );
		System.out.println( "节点名称：" + finalNode.getName() );
		System.out.println( "=============================================" );
		System.out.println( "原始的内容：" );
		final String oldText = NodeHelper.xml2Text( NodeHelper.note2Xml( resultNode ) );
		System.out.println( oldText );
		System.out.println( "=============================================" );
		System.out.println( "压缩解压处理后的内容：" );
		final String finalText = NodeHelper.xml2Text( NodeHelper.note2Xml( finalNode ) );
		System.out.println( finalText );
		System.out.println( "=============================================" );
		System.out.println( "EQUAL RESULT :" + oldText.equals( finalText ) );
		System.out.println( "=============================================" );
		System.out.println();
		System.out.println();
		System.out.println();
	}

}
