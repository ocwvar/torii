package com.ocwvar.torii;

import com.ocwvar.torii.utils.protocol.remote.RemoteRequestManager;
import com.ocwvar.torii.utils.protocol.remote.RemoteServer;
import com.ocwvar.torii.utils.protocol.remote.Result;
import com.ocwvar.utils.IO;
import com.ocwvar.utils.node.Node;
import com.ocwvar.utils.node.NodeHelper;

import java.nio.charset.Charset;

@SuppressWarnings( "CharsetObjectCanBeUsed" )
public class RemoteProtocolTest {

	private static final Charset usingCharset = Charset.forName( "cp932" );

	public static void main( String[] args ) throws Exception {
		ToriiApplication.loadConfig();
		System.out.println( "开始处理" );

		//解密测试
		//final byte[] decodeSample = IO.loadFile( "H:\\1581952783233.kbin" );
		//benchmarkDecode( decodeSample, 50 );
		//testDecode( decodeSample, 1, 1, true );


		//加密测试
		final Node encodeSample = NodeHelper.xml2Node( new String( IO.loadFile( "H:\\test.xml" ), usingCharset ) );
		//benchmarkEncode( encodeSample, 50 );
		testEncode( encodeSample, 50, 50, false );

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
					final Result result = RemoteRequestManager.getInstance().sendKbin( sample );
					System.out.println( "执行结果：" + !result.hasException() + "  数据长度：" + ( result.getResult() != null ? result.getResult().length : "NULL" ) );
					if ( result.getResult() != null && outputResult ) {
						System.out.println( new String( result.getResult(), usingCharset ) );
					}
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
					final String tag = "线程编号[" + Thread.currentThread().getName() + "]--任务编号[" + j + "]";
					sample.setEncodeCharset( Charset.forName( "cp932" ) );
					final Result result = RemoteRequestManager.getInstance().sendXML( sample, tag );
					System.out.println( "执行结果：" + !result.hasException() + "  数据长度：" + ( result.getResult() != null ? result.getResult().length : "NULL" ) + "  一致性：" + tag.equals( result.getTag() ) );
					if ( result.getResult() != null && outputResult ) {
						IO.outputFile( true, "H:\\" + System.currentTimeMillis() + ".kbin", result.getResult() );
					}
				}
				System.out.println( "======== 执行耗时：" + ( System.currentTimeMillis() - startM ) );
			}, String.valueOf( i ) ).start();
		}
	}

	/**
	 * 测试单次加密请求
	 *
	 * @param sample 样本
	 * @param times  执行次数
	 */
	private static void benchmarkEncode( final Node sample, int times ) {
		System.out.println( "===========================================" );
		final long m1 = System.currentTimeMillis();
		final boolean connected = RemoteServer.getInstance().startServer();
		System.out.println( "连接远端结果：" + connected + "  耗时ms：" + ( System.currentTimeMillis() - m1 ) );
		System.out.println( "===========================================" );

		if ( !connected ) {
			return;
		}

		float sum = 0;
		final long m2 = System.currentTimeMillis();
		System.out.println( "开始执行测试..." );
		for ( int i = 0; i < times; i++ ) {
			final long m3 = System.currentTimeMillis();
			final Result result = RemoteRequestManager.getInstance().sendXML( sample );
			if ( result != null && result.getResult() != null && !result.hasException() ) {
				sum += System.currentTimeMillis() - m3;
			} else {
				throw new RuntimeException( "执行出现异常或超时" );
			}
		}

		System.out.println( "执行次数：" + times + "  平均耗时ms：" + ( sum / times ) + "  总耗时ms：" + ( System.currentTimeMillis() - m2 ) );
		System.out.println( "===========================================" );
	}

	/**
	 * 测试单次解密请求
	 *
	 * @param sample 样本
	 * @param times  执行次数
	 */
	private static void benchmarkDecode( final byte[] sample, int times ) {
		System.out.println( "===========================================" );
		final long m1 = System.currentTimeMillis();
		final boolean connected = RemoteServer.getInstance().startServer();
		System.out.println( "连接远端结果：" + connected + "  耗时ms：" + ( System.currentTimeMillis() - m1 ) );
		System.out.println( "===========================================" );

		if ( !connected ) {
			return;
		}

		float sum = 0;
		final long m2 = System.currentTimeMillis();
		System.out.println( "开始执行测试..." );
		for ( int i = 0; i < times; i++ ) {
			final long m3 = System.currentTimeMillis();
			final Result result = RemoteRequestManager.getInstance().sendKbin( sample );
			if ( result != null && result.getResult() != null && !result.hasException() ) {
				sum += System.currentTimeMillis() - m3;
			} else {
				throw new RuntimeException( "执行出现异常或超时" );
			}
		}

		System.out.println( "执行次数：" + times + "  平均耗时ms：" + ( sum / times ) + "  总耗时ms：" + ( System.currentTimeMillis() - m2 ) );
		System.out.println( "===========================================" );
	}

}
