package com.ocwvar.xml.node;

import com.ocwvar.utils.annotation.NotNull;
import com.ocwvar.xml.node.i.INode;

import java.util.LinkedHashMap;
import java.util.Map;

public class NodeBuilder {

	private String name;
	private String contentValue;
	private BaseNode parentNode;
	private Map< String, String > attributes;

	/**
	 * 构造一个不带值的节点
	 *
	 * @param name 节点名称
	 */
	public NodeBuilder( String name ) {
		this.name = name;
	}

	/**
	 * 构造带值节点
	 *
	 * @param name         节点名称
	 * @param contentValue 节点值
	 */
	public NodeBuilder( String name, String contentValue ) {
		this.name = name;
		this.contentValue = contentValue;
	}

	/**
	 * 构造带值节点
	 *
	 * @param name         节点名称
	 * @param type         节点类型
	 * @param contentValue 节点值
	 */
	public NodeBuilder( String name, String type, String contentValue ) {
		this.attributes = new LinkedHashMap<>();
		this.attributes.put( INode.ATTR_TYPE, type );

		this.name = name;
		this.contentValue = contentValue;
	}

	/**
	 * 添加参数
	 *
	 * @param key   参数名
	 * @param value 参数值
	 */
	public NodeBuilder addAttribute( String key, String value ) {
		if ( this.attributes == null ) {
			this.attributes = new LinkedHashMap<>();
		}

		if ( this.attributes.get( key ) != null ) {
			return this;
		}

		this.attributes.put( key, value );
		return this;
	}

	/**
	 * 设置此节点的父节点
	 *
	 * @param parentNode 父节点
	 */
	public NodeBuilder setParentNode( @NotNull BaseNode parentNode ) {
		this.parentNode = parentNode;
		return this;
	}

	/**
	 * 设置节点内容值
	 *
	 * @param contentValue 值
	 */
	public NodeBuilder setContentValue( String contentValue ) {
		this.contentValue = contentValue;
		return this;
	}

	/**
	 * 构造节点
	 *
	 * @return 节点对象
	 */
	public com.ocwvar.xml.node.Node build() {
		final Node node = new Node(
				this.name,
				this.contentValue,
				null,
				this.attributes
		);

		if ( this.parentNode != null ) {
			this.parentNode.addChildNode( node );
		}

		return node;
	}

}
