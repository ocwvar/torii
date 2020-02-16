package com.ocwvar.torii.generator;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.ocwvar.torii.utils.IO;
import com.ocwvar.xml.node.Node;
import com.ocwvar.xml.node.NodeHelper;

import java.io.FileOutputStream;

/**
 * 根据 character_param.xml 来生成 sv_load 需要的解锁数据
 * 这部分数据为 领航员
 * <p>
 * 需求文件：X:\data\others\character_param.xml
 * 输出文件应当配置为 main\resources\CharacterUnlockJsonData\data.json
 */
public class CharacterUnlockJsonData {

	/**
	 * 文件路径配置
	 */
	private static final String LOAD_FILE_PATH = "D:\\KFC-2019100800\\data\\others\\character_param.xml";
	private static final String OUTPUT_FILE_PATH = "H:\\IdeaProjects\\torii\\src\\main\\resources\\generator\\CharacterUnlockJsonData\\data.json";

	public static void main( String[] args ) throws Exception {
		System.out.println( "正在读取文件：" + LOAD_FILE_PATH );
		byte[] bytes = IO.loadFile( true, LOAD_FILE_PATH );
		if ( bytes == null || bytes.length <= 0 ) {
			System.out.println( "文件读取失败" );
			return;
		}


		final Node root = NodeHelper.xml2Node( NodeHelper.byte2Xml( bytes ) );
		final JsonArray jsonArray = new JsonArray( root.childCount() );
		System.out.println( "character_param.xml 数据量：" + root.childCount() );
		JsonObject jsonObject;
		Node info;
		for ( int i = 0; i < root.childCount(); i++ ) {
			System.out.println( "正在处理   (" + root.childCount() + "/" + ( i + 1 ) + ")" );
			info = ( Node ) root.indexChildNode( i ).getFirstChildNode();
			jsonObject = new JsonObject();
			jsonObject.addProperty( "id", info.indexChildNode( "type_id" ).getContentValue() );
			jsonArray.add( jsonObject );
		}

		System.out.println( "正在输出文件：" + OUTPUT_FILE_PATH );
		final FileOutputStream outputStream = new FileOutputStream( OUTPUT_FILE_PATH );
		outputStream.write( jsonArray.toString().getBytes() );
		outputStream.flush();
		outputStream.close();

		System.out.println( "执行完毕" );
	}

}
