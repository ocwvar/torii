package com.ocwvar.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.ocwvar.utils.annotation.NotNull;
import com.ocwvar.utils.annotation.Nullable;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;

public class IO {

	/**
	 * 将文件读取为字节数组
	 *
	 * @param filePath 读取文件路径
	 * @return 字节数组，如果读取失败则返回 NULL
	 */
	public static @Nullable
	byte[] loadFile( String filePath ) {
		try (
				final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
				final FileInputStream inputStream = new FileInputStream( new File( filePath ) )
		) {

			final byte[] buffer = new byte[ 1024 ];
			int length;
			while ( ( length = inputStream.read( buffer ) ) != -1 ) {
				outputStream.write( buffer, 0, length );
			}
			outputStream.flush();
			outputStream.close();
			inputStream.close();

			return outputStream.toByteArray();
		} catch ( Exception ignore ) {
			return null;
		}
	}

	/**
	 * 输出文件
	 *
	 * @param useBuffer  是否使用缓冲
	 * @param folderPath 输出文件夹路径
	 * @param fileName   文件名
	 * @param data       需要输出的数据
	 * @return 是否成功执行
	 */
	public static boolean outputFile( boolean useBuffer, String folderPath, String fileName, @NotNull byte[] data ) {
		new File( folderPath ).mkdirs();
		return outputFile( useBuffer, folderPath + fileName, data );
	}

	/**
	 * 输出文件
	 *
	 * @param useBuffer 是否使用缓冲
	 * @param path      输出文件路径
	 * @param data      需要输出的数据
	 * @return 是否成功执行
	 */
	public static boolean outputFile( boolean useBuffer, String path, @NotNull byte[] data ) {
		try (
				final ByteArrayInputStream inputStream = new ByteArrayInputStream( data );
				final FileOutputStream outputStream = new FileOutputStream( path, false )
		) {

			if ( !useBuffer ) {
				outputStream.write( data );
				outputStream.flush();
				outputStream.close();
				inputStream.close();
				return true;
			}

			final byte[] buffer = new byte[ 1024 ];
			int length;
			while ( ( length = inputStream.read( buffer ) ) != -1 ) {
				outputStream.write( buffer, 0, length );
			}
			outputStream.flush();
			outputStream.close();
			inputStream.close();

			return true;
		} catch ( Exception ignore ) {
			return false;
		}
	}

	/**
	 * 读取 Resource 下的文件流
	 *
	 * @param resourcePath 资源文件路径，如 "folder/file.txt
	 * @return 字节数组，如果读取失败则返回 NULL
	 */
	public static byte[] loadResource( String resourcePath ) {
		try (
				final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
				final InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream( resourcePath )
		) {
			final byte[] buffer = new byte[ 1024 ];
			int length;
			while ( ( length = inputStream.read( buffer ) ) != -1 ) {
				outputStream.write( buffer, 0, length );
			}
			outputStream.flush();
			outputStream.close();
			inputStream.close();

			return outputStream.toByteArray();
		} catch ( Exception ignore ) {
			return null;
		}
	}

	/**
	 * 读取 Resource 下的文件路径
	 *
	 * @param resourcePath 资源文件路径，如 "folder/file.txt
	 * @return 文件路径，如果读取失败，则返回NULL
	 */
	public static String getResourceFilePath( String resourcePath ) {
		final URL url = Thread.currentThread().getContextClassLoader().getResource( resourcePath );
		final String path;
		if ( url == null ) {
			return null;
		}
		path = url.getFile();
		if ( path.charAt( 0 ) == '/' ) {
			return path.substring( 1 );
		}

		return url.getPath();
	}

	/**
	 * 读取 JSON 文件
	 *
	 * @param useBuffer 是否使用缓冲
	 * @param filePath  读取文件路径
	 * @return JsonElement 对象，如果失败则返回 NULL
	 */
	public static JsonElement loadJsonFile( boolean useBuffer, String filePath, Charset charset ) {
		final byte[] bytes = loadFile( filePath );
		if ( bytes == null ) {
			return null;
		}

		return JsonParser.parseString( new String( bytes, charset ) );
	}

}
