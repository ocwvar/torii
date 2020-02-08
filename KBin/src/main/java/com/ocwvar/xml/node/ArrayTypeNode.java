package com.ocwvar.xml.node;

import com.ocwvar.utils.TextUtils;
import com.ocwvar.xml.node.i.INode;

public class ArrayTypeNode extends Node {

	/**
	 * 创建一个带类型 "__type" 的数组节点
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
		for ( String item : items ) {
			builder.append( item ).append( " " );
		}
		builder.deleteCharAt( builder.length() - 1 );

		setContentValue( builder.toString() );
	}

	/**
	 * 创建一个带类型 "__type" 的数组节点
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
			addAttribute( INode.ATTR_COUNT, String.valueOf( item.split( " " ).length ) );
		} else {
			addAttribute( INode.ATTR_COUNT, "1" );
		}

		setContentValue( item );
	}

}
