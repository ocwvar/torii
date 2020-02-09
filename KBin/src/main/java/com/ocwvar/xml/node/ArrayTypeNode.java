package com.ocwvar.xml.node;

import com.ocwvar.utils.TextUtils;
import com.ocwvar.xml.node.i.INode;

public class ArrayTypeNode extends Node {

	private final String[] values;

	/**
	 * 将普通Node转换为数组节点
	 *
	 * @param source 源
	 */
	public ArrayTypeNode( BaseNode source ) {
		super( source.getName() );
		if ( source.getAttribute( INode.ATTR_COUNT ) == null ) {
			throw new RuntimeException( "此 node 非数组类型" );
		}

		if ( source.getAttribute( INode.ATTR_TYPE ) == null ) {
			throw new RuntimeException( "此 node 无指定数据类型" );
		}

		addAttribute( INode.ATTR_TYPE, source.getAttribute( INode.ATTR_TYPE ) );
		addAttribute( INode.ATTR_COUNT, source.getAttribute( INode.ATTR_COUNT ) );

		final String content = source.getContentValue();
		if ( content.contains( " " ) ) {
			this.values = content.split( " " );
		} else {
			this.values = new String[]{ content };
		}
	}

	/**
	 * 创建一个带类型的数组节点
	 *
	 * @param name  节点名称
	 * @param type  节点类型
	 * @param items 节点内容
	 */
	public ArrayTypeNode( String name, String type, String... items ) {
		super( name );
		addAttribute( INode.ATTR_TYPE, type );
		addAttribute( INode.ATTR_COUNT, String.valueOf( items.length ) );

		final StringBuilder builder = new StringBuilder();
		this.values = new String[ items.length ];
		for ( int i = 0; i < items.length; i++ ) {
			this.values[ i ] = items[ i ];
			builder.append( items[ i ] ).append( " " );
		}
		builder.deleteCharAt( builder.length() - 1 );

		setContentValue( builder.toString() );
	}

	/**
	 * 创建一个带类型的数组节点
	 *
	 * @param dummy 随便传，假参数
	 * @param name  节点名称
	 * @param type  节点类型
	 * @param item  节点内容，每个数值以一个空格相间，首尾不能有空格，如："1 2 3 54 56"
	 */
	public ArrayTypeNode( int dummy, String name, String type, String item ) {
		super( name );
		if ( TextUtils.isEmpty( item ) ) {
			throw new RuntimeException( "ArrayTypeNode ITEM 数据为空" );
		}

		addAttribute( INode.ATTR_TYPE, type );

		if ( item.contains( " " ) ) {
			this.values = item.split( " " );
			addAttribute( INode.ATTR_COUNT, String.valueOf( values.length ) );
		} else {
			addAttribute( INode.ATTR_COUNT, "1" );
			this.values = new String[]{ item };
		}

		setContentValue( item );
	}

	/**
	 * @return 元素数量
	 */
	public int count() {
		return this.values.length;
	}

	/**
	 * 获取元素对象
	 *
	 * @param pos 位置
	 * @return 元素对象，如果超过位置则返回 NULL
	 */
	public String value( int pos ) {
		try {
			return this.values[ pos ];
		} catch ( Exception e ) {
			return null;
		}
	}

}
