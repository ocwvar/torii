package com.ocwvar.torii;

import com.ocwvar.torii.utils.protocol.RemoteKBinClient;
import com.ocwvar.utils.IO;
import com.ocwvar.xml.node.Node;
import com.ocwvar.xml.node.NodeHelper;

import java.nio.charset.Charset;

@SuppressWarnings( "CharsetObjectCanBeUsed" )
public class RemoteProtocolTest {

	private static final Charset usingCharset = Charset.forName( "utf8" );

	public static void main( String[] args ) throws Exception {
		System.out.println( "开始处理" );

		//解密测试
		//final byte[] decodeSample = IO.loadFile( "H:\\test.kbin" );
		//testDecode( decodeSample, 1, 2, false );

		//加密测试
		final Node encodeSample = NodeHelper.xml2Node( new String( IO.loadFile( "H:\\test.xml" ), usingCharset ) );
		testEncode( encodeSample, 1, 2, true );

		System.out.println( "处理结束" );
	}

	/**
	 * 解密测试
	 *
	 * @param sample       样本
	 * @param threadCount  同时启动线程数
	 * @param times        每条线程请求次数
	 * @param outputResult 是否输出结果
	 */
	private static void testDecode( final byte[] sample, final int threadCount, final int times, final boolean outputResult ) {
		for ( int i = 0; i < threadCount; i++ ) {
			new Thread( () -> {
				final long startM = System.currentTimeMillis();
				for ( int j = 0; j < times; j++ ) {
					final RemoteKBinClient.Result result = RemoteKBinClient.getInstance().sendKbin( sample );
					System.out.println( "执行结果：" + !result.isHasException() + "  数据长度：" + ( result.getResult() != null ? result.getResult().length : "NULL" ) );
				}
				System.out.println( "======== 执行耗时：" + ( System.currentTimeMillis() - startM ) );
			}, "线程 " + i ).start();
		}
	}

	/**
	 * 加密请求
	 *
	 * @param sample       样本
	 * @param threadCount  同时启动线程数
	 * @param times        每条线程请求次数
	 * @param outputResult 是否输出结果
	 */
	private static void testEncode( final Node sample, final int threadCount, final int times, final boolean outputResult ) {
		for ( int i = 0; i < threadCount; i++ ) {
			new Thread( () -> {
				final long startM = System.currentTimeMillis();
				for ( int j = 0; j < times; j++ ) {
					final RemoteKBinClient.Result result = RemoteKBinClient.getInstance().sendXML( sample );
					System.out.println( "执行结果：" + !result.isHasException() + "  数据长度：" + ( result.getResult() != null ? result.getResult().length : "NULL" ) );
					if ( result.getResult() != null && outputResult ) {
						IO.outputFile( true, "H:\\" + System.currentTimeMillis() + ".kbin", result.getResult() );
					}
				}
				System.out.println( "======== 执行耗时：" + ( System.currentTimeMillis() - startM ) );
			}, "线程 " + i ).start();
		}
	}

}
