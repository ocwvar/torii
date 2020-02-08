package com.ocwvar.utils;

import com.ocwvar.utils.annotation.Nullable;

public class TextUtils {

	/**
	 * 检测文本是否为空，包括 null 的情况
	 *
	 * @param s 需要进行检测的文本
	 * @return 是否为空
	 */
	public static boolean isEmpty( @Nullable String s ) {
		return s == null || s.trim().length() <= 0;
	}

}
