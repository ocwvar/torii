package com.ocwvar.kbin;

import org.python.core.PyInteger;
import org.python.core.PyString;
import org.python.jline.internal.Nullable;

public class XmlFormatsNode {

	public final @Nullable
	PyString type;

	public final @Nullable
	PyInteger count;

	public final
	String[] names;

	public final @Nullable
	String fromStr;

	public final @Nullable
	String toStr;

	public XmlFormatsNode( @Nullable String type, int count, String fromStr, String toStr, String... names ) {
		this.type = type == null ? null : new PyString( type );
		this.count = count == 0 ? null : new PyInteger( count );
		this.fromStr = fromStr == null ? "null" : fromStr;
		this.toStr = toStr == null ? "null" : toStr;
		this.names = names;
	}
}
