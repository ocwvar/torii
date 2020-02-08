package com.ocwvar.xml.node;

public class UrlNode extends Node {

	public UrlNode( String urlName, String url ) {
		super( "item" );
		addAttribute( "name", urlName );
		addAttribute( "url", url );
	}

}
