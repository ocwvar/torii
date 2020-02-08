package com.ocwvar.xml.node;

import com.ocwvar.xml.node.i.INode;

public class TypeNode extends Node {

	/**
	 * 创建一个带类型 "__type" 的节点，此构造方法默认为 str 类型
	 *
	 * @param name         节点名称
	 * @param contentValue 节点值
	 */
	public TypeNode( String name, String contentValue ) {
		super( name );
		setContentValue( contentValue );
		addAttribute( INode.ATTR_TYPE, "str" );
	}

	/**
	 * 创建一个带类型 "__type" 的节点
	 *
	 * @param name         节点名称
	 * @param contentValue 节点值
	 * @param type         节点类型
	 */
	public TypeNode( String name, String contentValue, String type ) {
		super( name );
		setContentValue( contentValue );
		addAttribute( INode.ATTR_TYPE, type );
	}

}
