package com.ocwvar.kbin.utils;

public class AssertCheck {

	public static void check(boolean b) throws AssertException{
		if ( !b ) throw new AssertException();
	}

	public static class AssertException extends Exception{}

}
