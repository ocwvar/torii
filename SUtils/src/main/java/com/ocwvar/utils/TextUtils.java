package com.ocwvar.utils;

import com.ocwvar.utils.annotation.Nullable;

import java.util.Random;

public class TextUtils {

	private final static String c = "abcdefghijklnmopqrstuvwxyzABCDEFGHIJKLINMOPQRSTUVWXYZ";

	/**
	 * 检测文本是否为空，包括 null 的情况
	 *
	 * @param s 需要进行检测的文本
	 * @return 是否为空
	 */
	public static boolean isEmpty( @Nullable String s ) {
		return s == null || s.trim().length() <= 0;
	}

	/**
	 * 获取指定长度的随机字符串
	 *
	 * @param length 长度
	 * @return 随机字符串
	 */
	public static String getRandomText( int length ) {

		final Random random = new Random( (System.currentTimeMillis() - System.nanoTime()) * 41232);
		final StringBuilder builder = new StringBuilder();
		while ( builder.length() < length ) {
			builder.append( c.charAt( random.nextInt( c.length() ) ) );
			random.setSeed( System.currentTimeMillis() / random.nextInt() );
		}

		return builder.toString();
	}

}
