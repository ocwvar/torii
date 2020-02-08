package com.ocwvar.torii.data;

import com.ocwvar.utils.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * 静态资源存储器
 */
public class StaticContainer {

	private volatile static StaticContainer self;

	public static StaticContainer getInstance() {
		if ( self != null ) {
			return self;
		}

		synchronized ( StaticContainer.class ) {
			if ( self == null ) {
				self = new StaticContainer();
			}
		}

		return self;
	}

	private final Map< String, Object > map;

	public StaticContainer() {
		this.map = new HashMap<>();
	}

	public Object get( String key ) {
		if ( !this.map.containsKey( key ) ) {
			return null;
		}

		return this.map.get( key );
	}

	public boolean has( String key ) {
		return this.map.containsKey( key );
	}

	public void set( String key, Object value ) {
		this.map.remove( key );
		this.map.put( key, value );
		Log.getInstance().print( "静态资源存储器，存入操作：" + key + "   " + value );
	}

}
