package com.ocwvar.xml.node;

import com.ocwvar.utils.TextUtils;
import com.ocwvar.xml.node.i.INode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.Map;

public abstract class BaseNode implements INode {

	private BaseNode parentNode = null;

	/**
	 * 设置父节点
	 *
	 * @param parentNode 父节点
	 */
	@Override
	public void setParentNode( BaseNode parentNode ) {
		this.parentNode = parentNode;
	}

	/**
	 * @return 父节点，有可能为 NULL
	 */
	@Override
	public BaseNode getParentNode() {
		return this.parentNode;
	}

	/**
	 * 输入此节点所有子节点到给定的对象
	 *
	 * @param document      XML文档对象
	 * @param parentElement 父节点
	 */
	protected void __inputChildNodes( Document document, Element parentElement ) {
		if ( childCount() > 0 ) {
			//遍历自己内部的所有子节点
			for ( BaseNode node : this.getChildNodes() ) {
				final Element child = document.createElement( node.getName() );

				if ( !TextUtils.isEmpty( node.getContentValue() ) ) {
					child.setTextContent( node.getContentValue() );
				}

				node.__inputAttributes( child );
				node.__inputChildNodes( document, child );
				parentElement.appendChild( child );
			}
		}
	}

	/**
	 * 输入此节点所有参数到给定的对象
	 */
	protected void __inputAttributes( Element element ) {
		if ( attributeCount() > 0 ) {
			for ( Map.Entry< String, String > attr : this.getAttributes().entrySet() ) {
				element.setAttribute( attr.getKey(), attr.getValue() );
			}
		}
	}

	@Override
	public String toString() {
		return String.format( "Node{%s,%s,%d}", getName(), getType(), childCount() );
	}
}
