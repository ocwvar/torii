
import com.ocwvar.kbin.KBinXml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class EncodeTest {

	/*

	  KXml加密测试，给定原文本，即可输出加密后的数据流

	 */

	private final static String PATH = "H:\\TORII\\kbin\\raw.xml";

	private final static String OUTPUT_PATH = "H:\\TORII\\kbin\\encode.kbin";

	public static void main( String[] args ) throws Exception {
		//读取 xml 已解密文件
		final FileInputStream inputStream = new FileInputStream( new File( PATH ) );
		final byte[] data = inputStream.readAllBytes();
		inputStream.close();
		final String xmlContent = new String( data );

		try {
			final long startM = System.currentTimeMillis();

			//执行加密并输出加密文件
			System.out.println( "开始执行加密..." );
			final byte[] result = KBinXml.encode( xmlContent );
			System.out.println( "加密执行完毕，耗时(ms)：" + ( System.currentTimeMillis() - startM ) );

			final FileOutputStream outputStream = new FileOutputStream( OUTPUT_PATH, false );
			outputStream.write( result );
			outputStream.flush();
			outputStream.close();

			System.out.println( "已输出加密文件：" + OUTPUT_PATH );
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}

}
