import com.ocwvar.kbin.KBinXml;
import com.ocwvar.xml.node.Node;
import com.ocwvar.xml.node.NodeHelper;

import java.io.File;
import java.io.FileInputStream;

public class DecodeTest {

	/*

	  KBin解密测试，给定加密数据流，即可输出解密后的文字数据

	 */

	//标准加密文件
	//private final static String PATH = "H:\\IdeaProjects\\BServer\\DataTesting\\kbinxml\\Encrypted_raw.kbin";

	//加密输出文件
	private final static String PATH = "H:\\note_output.kbin";
	//private final static String PATH = "H:\\TORII\\kbin\\sv5_common.kBin";
	//private final static String PATH = "H:\\IdeaProjects\\BServer\\DataTesting\\kbinxml\\Encrypted_output.kbin";

	public static void main( String[] args ) throws Exception {
		//读取 bin 已加密文件
		final FileInputStream inputStream = new FileInputStream( new File( PATH ) );
		final byte[] data = inputStream.readAllBytes();
		inputStream.close();


		//执行解密并输出明文结果
		System.out.println( "开始执行解密" );

		final long startM = System.currentTimeMillis();
		final Node result = KBinXml.decode( data );

		System.out.println( "解密执行完毕，耗时(ms)：" + ( System.currentTimeMillis() - startM ) );
		System.out.println( NodeHelper.xml2Text( NodeHelper.note2Xml( result ) ) );
	}

}
