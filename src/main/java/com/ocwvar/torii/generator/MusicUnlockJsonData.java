package com.ocwvar.torii.generator;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.ocwvar.utils.IO;
import com.ocwvar.utils.node.Node;
import com.ocwvar.utils.node.NodeHelper;

import java.io.FileOutputStream;

/**
 * 根据 music_db.xml 所有库存谱面的信息，以供 sv5_common 使用
 * <p>
 * 需求文件：X:\data\others\music_db.xml
 * 输出文件应当配置为 main\resources\MusicUnlockJsonData\data.json
 */
public class MusicUnlockJsonData {

	/**
	 * 文件路径配置
	 */
	private static final String LOAD_FILE_PATH = "D:\\KFC-2019100800\\data\\others\\music_db.xml";
	private static final String OUTPUT_FILE_PATH = "H:\\IdeaProjects\\torii\\src\\main\\resources\\generator\\MusicUnlockJsonData\\data.json";

	public static void main( String[] args ) throws Exception {
		System.out.println( "正在读取文件：" + LOAD_FILE_PATH );
		byte[] bytes = IO.loadFile( LOAD_FILE_PATH );
		if ( bytes == null || bytes.length <= 0 ) {
			System.out.println( "文件读取失败" );
			return;
		}


		final Node root = NodeHelper.xml2Node( NodeHelper.byte2Xml( bytes ) );
		final JsonArray jsonArray = new JsonArray( root.childCount() );
		System.out.println( "music_db.xml 数据量：" + root.childCount() );


		Node musicNode, trackNode;
		StringBuilder tracks = new StringBuilder();
		for ( int i = 0; i < root.childCount(); i++ ) {
			System.out.print( "(" + root.childCount() + "/" + ( i + 1 ) + ")  " );
			final JsonObject music = new JsonObject();
			musicNode = ( Node ) root.indexChildNode( i );
			trackNode = ( Node ) musicNode.indexChildNode( "difficulty" );

			//记录音乐ID
			music.addProperty( "id", musicNode.getAttribute( "id" ) );

			//记录谱面难度
			if ( trackNode.indexChildNode( "novice" ) != null ) {
				tracks.append( "0#" );
			}

			if ( trackNode.indexChildNode( "advanced" ) != null ) {
				tracks.append( "1#" );
			}

			if ( trackNode.indexChildNode( "exhaust" ) != null ) {
				tracks.append( "2#" );
			}

			if ( trackNode.indexChildNode( "infinite" ) != null ) {
				tracks.append( "3#" );
			}

			if ( trackNode.indexChildNode( "maximum" ) != null ) {
				tracks.append( "4#" );
			}

			music.addProperty( "tracks", tracks.toString() );
			jsonArray.add( music );
			System.out.println( music.toString() );

			tracks.delete( 0, tracks.length() );
		}

		System.out.println( "正在输出文件：" + OUTPUT_FILE_PATH );
		final FileOutputStream outputStream = new FileOutputStream( OUTPUT_FILE_PATH );
		outputStream.write( jsonArray.toString().getBytes() );
		outputStream.flush();
		outputStream.close();

		System.out.println( "执行完毕" );
	}

}
