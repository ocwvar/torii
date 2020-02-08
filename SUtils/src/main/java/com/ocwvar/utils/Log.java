package com.ocwvar.utils;

public class Log {

	private static Log self;

	static {
		self = new Log();
	}

	public static Log getInstance() {
		return self;
	}

	public void print( String message ) {
		print( message, false );
	}

	public void print( String message, boolean isError ) {
		System.out.println( ( isError ? "[错误] " : "[信息] " ) + message );
	}

}
