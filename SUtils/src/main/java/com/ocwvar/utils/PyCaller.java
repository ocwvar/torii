package com.ocwvar.utils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * 这个类是提供调用 Python 脚本的方法
 */
public class PyCaller {

	//是否输出日志
	private static final boolean DEBUG = false;

	/**
	 * 执行调用Python脚本命令  (异步)
	 *
	 * @param path 脚本文件路径
	 * @param args 传递的参数，如果没有则为 NULL
	 */
	public static void execAsync( String path, String... args ) {
		new Thread( () -> {
			try {
				//执行命令并获得结果码
				final Process process = createProcess( path, args );
				if ( process == null ) {
					return;
				}

				final int resultCode = process.waitFor();

				//输出错误
				outputError( resultCode, process );
			} catch ( Exception ignore ) {
			}
		} ).start();
	}

	/**
	 * 执行调用Python脚本命令  (同步)
	 *
	 * @param path 脚本文件路径
	 * @param args 传递的参数，如果没有则为 NULL
	 * @return 是否调用成功
	 */
	public static boolean execSync( String path, String... args ) {
		try {
			//执行命令并获得结果码
			final Process process = createProcess( path, args );
			if ( process == null ) {
				return false;
			}

			final int resultCode = process.waitFor();

			//输出错误
			outputError( resultCode, process );
			return resultCode == 0;
		} catch ( Exception ignore ) {
			return false;
		}
	}

	/**
	 * 生成调用脚本的进程对象
	 *
	 * @param path 脚本文件路径
	 * @param args 传递的参数，如果没有则为 NULL
	 * @return 进程对象，如果执行失败，则返回 NULL
	 */
	public static Process createProcess( String path, String... args ) {
		try {
			//组装请求参数
			final String[] command;
			if ( args == null || args.length <= 0 ) {
				command = new String[]{
						"py ",
						path
				};
			} else {
				command = new String[ 2 + args.length ];
				command[ 0 ] = "py ";
				command[ 1 ] = path;
				System.arraycopy( args, 0, command, 2, args.length );
			}

			//输出执行的指令内容
			outputCommand( command );

			return Runtime.getRuntime().exec( command );
		} catch ( Exception ignore ) {
			return null;
		}
	}

	/**
	 * 输出命令内容
	 *
	 * @param command 命令对象
	 */
	private static void outputCommand( String[] command ) {
		if ( !DEBUG ) {
			return;
		}

		final StringBuilder builder = new StringBuilder();
		builder.append( "指令：" ).append( command[ 0 ] ).append( command[ 1 ] ).append( "\n" );
		builder.append( "参数：" );
		for ( String s : command ) {
			builder.append( s ).append( "  " );
		}
		System.out.println( builder.toString() );
	}

	/**
	 * 输出错误
	 *
	 * @param resultCode 错误码
	 * @param process    Process对象
	 */
	private static void outputError( int resultCode, Process process ) throws Exception {
		if ( !DEBUG ) {
			return;
		}

		if ( resultCode == 0 ) {
			Log.getInstance().print( "执行成功！" );
			return;
		}

		InputStream inputStream = process.getErrorStream();
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream( inputStream.available() );
		byte[] buffer = new byte[ 512 ];
		int length;
		String error;
		while ( ( length = inputStream.read( buffer ) ) != -1 ) {
			outputStream.write( buffer, 0, length );
		}
		inputStream.close();
		outputStream.flush();
		outputStream.close();
		error = new String( outputStream.toByteArray() );
		inputStream = null;
		outputStream = null;
		buffer = null;


		switch ( resultCode ) {
			case 1:
				Log.getInstance().print( "命令错误 [1:执行异常] :\n" + error, true );
				break;
			case 2:
				Log.getInstance().print( "命令错误 [2:找不到文件] :\n" + error, true );
				break;
		}
	}

}
