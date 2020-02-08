package com.ocwvar;

import org.python.Version;

public class JythonVersion {

	public static String getVersion() {
		return Version.getVersion();
	}

	public static String getBuildInfo() {
		return Version.getBuildInfo();
	}

}
