package com.ocwvar.torii;

import com.ocwvar.torii.utils.protocol.remote.RemoteRequestManager;
import com.ocwvar.torii.utils.protocol.remote.Result;
import com.ocwvar.utils.IO;
import com.ocwvar.utils.node.Node;
import com.ocwvar.utils.node.NodeHelper;

import java.nio.charset.Charset;

@SuppressWarnings( "CharsetObjectCanBeUsed" )
public class RemoteManagerTest {

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
		testEncode( encodeSample, 500, 50, false );

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
					//System.out.println( "执行结果：" + !result.hasException() + "  数据长度：" + ( result.getResult() != null ? result.getResult().length : "NULL" ) );
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
				int eq = 0, ueq = 0, suc = 0, fail = 0;
				for ( int j = 0; j < times; j++ ) {
					final String tag = "线程[" + Thread.currentThread().getName() + "]--任务[" + j + "]";
					sample.setEncodeCharset( Charset.forName( "cp932" ) );
					final Result result = RemoteRequestManager.getInstance().sendXML( sample, tag );
					final boolean equals = tag.equals( result.getTag() );
					//System.out.println( "执行结果：" + !result.hasException() + "  数据长度：" + ( result.getResult() != null ? result.getResult().length : "NULL" ) + "  一致性：" + equals );
					if ( !equals ) {
						ueq += 1;
						System.out.println( "【请求与结果不对应】，请求：" + tag + "   结果：" + result.getTag() );
					} else {
						eq += 1;
					}

					if ( result.hasException() ) {
						fail += 1;
					} else {
						suc += 1;
					}
					if ( result.getResult() != null && outputResult ) {
						IO.outputFile( true, "H:\\" + System.currentTimeMillis() + ".kbin", result.getResult() );
					}
				}
				System.out.println( "线程 " + Thread.currentThread().getName() + " 已执行完毕，对应正确数：" + eq + "  对应错误数：" + ueq + "  成功数：" + suc + "  失败数：" + fail );
			}, String.valueOf( i ) ).start();
		}
	}

}
