package com.ocwvar.xml.node;

import com.ocwvar.utils.annotation.NotNull;
import com.ocwvar.utils.annotation.Nullable;
import com.ocwvar.xml.node.i.INode;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Node extends BaseNode {

	private final String name;

	private List< BaseNode > childNodes = null;
	private Map< String, String > attributes = null;

	private String type = null;
	private String contentValue = null;

	public Node( @NotNull String name ) {
		this.name = name;
	}

	protected Node( @NotNull String name, @Nullable String contentValue, @Nullable List< BaseNode > childNodes, @Nullable Map< String, String > attributes ) {
		/*
			type 必须以 __type 的形式存在于 IAttribute[] 中
		 */
		if ( attributes != null ) {
			this.type = attributes.get( INode.ATTR_TYPE );
		} else {
			this.type = null;
		}

		this.name = name;
		this.childNodes = childNodes;
		this.attributes = attributes;
		this.contentValue = contentValue;
	}

	/**
	 * @return 节点名称
	 */
	@Override
	public String getName() {
		return this.name;
	}

	/**
	 * 获取此节点的类型
	 * <p>
	 * 如果此节点没有设置 "__type" 属性，则此方法返回 NULL
	 *
	 * @return 节点类型
	 */
	@Override
	public String getType() {
		return this.type;
	}

	/**
	 * @return 内容值
	 */
	@Override
	public String getContentValue() {
		return this.contentValue;
	}

	/**
	 * 设置内容值
	 *
	 * @param value 内容值
	 */
	@Override
	public void setContentValue( String value ) {
		this.contentValue = value;
	}

	/**
	 * @return 获取属性数组，如果没有属性，则返回 NULL
	 */
	@Override
	public Map< String, String > getAttributes() {
		return this.attributes;
	}

	/**
	 * 获取属性值
	 *
	 * @param key 属性名
	 * @return 属性值，如果不存在检索的属性名，则返回 NULL
	 */
	@Override
	public String getAttribute( String key ) {
		return attributeCount() <= 0 ? null : this.attributes.get( key );
	}

	/**
	 * 添加属性
	 *
	 * @param key   属性名
	 * @param value 属性值
	 */
	@Override
	public void addAttribute( @NotNull String key, @NotNull String value ) {
		if ( this.attributes == null ) {
			this.attributes = new LinkedHashMap<>();
		}

		if ( this.attributes.get( key ) != null ) {
			return;
		}

		//设置类型
		if ( key.equals( INode.ATTR_TYPE ) ) {
			this.type = value;
		}

		this.attributes.put( key, value );
	}

	/**
	 * @return 第一个子节点，如果没有，则返回 NULL
	 */
	@Override
	public BaseNode getFirstChildNode() {
		return childCount() > 0 ? this.childNodes.get( 0 ) : null;
	}

	/**
	 * 通过子节点名称来获取
	 *
	 * @param nodeName 子节点名称
	 * @return 节点对象，如果没有则返回 NULL
	 */
	@Override
	public BaseNode indexChildNode( String nodeName ) {
		if ( childCount() <= 0 ) {
			return null;
		}

		for ( BaseNode node : this.childNodes ) {
			if ( node.getName().equals( nodeName ) ) {
				return node;
			}
		}

		return null;
	}

	/**
	 * 通过位置来获取子节点
	 *
	 * @param pos 子节点位置
	 * @return 节点对象，如果没有则返回 NULL
	 */
	@Override
	public BaseNode indexChildNode( int pos ) {
		if ( childCount() <= 0 ) {
			return null;
		}

		if ( pos < 0 || pos >= this.childNodes.size() ) {
			return null;
		}

		return this.childNodes.get( pos );
	}

	/**
	 * @return 子节点数组
	 */
	@Override
	public List< BaseNode > getChildNodes() {
		return this.childNodes;
	}

	/**
	 * 添加新的节点
	 *
	 * @param childNode 新的子节点
	 */
	@Override
	public void addChildNode( BaseNode childNode ) {
		if ( this.childNodes == null ) {
			this.childNodes = new LinkedList<>();
		}

		childNode.setParentNode( this );
		this.childNodes.add( childNode );
	}

	/**
	 * @return 属性数量
	 */
	@Override
	public int attributeCount() {
		return this.attributes == null ? 0 : this.attributes.size();
	}

	/**
	 * @return 子节点数量
	 */
	@Override
	public int childCount() {
		return this.childNodes == null ? 0 : this.childNodes.size();
	}

}
