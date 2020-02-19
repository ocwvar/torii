package com.ocwvar.xml.node;

import com.ocwvar.utils.TextUtils;
import com.ocwvar.xml.node.i.INode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public abstract class BaseNode implements INode {

	private BaseNode parentNode = null;
	private String encodeCharset = null;

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
	 * 输出 XML 文本字符串
	 *
	 * @return XML 文本数据
	 */
	public String toXmlText() {
		return toXmlText( false );
	}

	/**
	 * 输出 XML 文本字符串
	 *
	 * @param encodeCharacter 是否需要转义字符
	 * @return XML 文本数据
	 */
	public String toXmlText( boolean encodeCharacter ) {
		final StringBuilder builder = new StringBuilder();
		__toXmlText( builder );

		if ( encodeCharacter ) {
			return builder.toString().replaceAll( "&", "&amp;" );
		} else {
			return builder.toString();
		}
	}

	/**
	 * @return 编码类型
	 */
	public String getEncodeCharset() {
		return this.encodeCharset == null ? StandardCharsets.UTF_8.name() : this.encodeCharset;
	}

	/**
	 * 设置编码类型，在进行KBIN加密时需要使用
	 *
	 * @param charset 编码类型，UTF-8 或 CP932
	 */
	public void setEncodeCharset( Charset charset ) {
		if ( charset == null ) {
			throw new RuntimeException( "未定义编码类型" );
		} else if ( charset == Charset.forName( "cp932" ) ) {
			this.encodeCharset = "cp932";
		} else if ( charset == StandardCharsets.UTF_8 ) {
			this.encodeCharset = "utf-8";
		} else {
			throw new RuntimeException( "不支持的编码类型：" + charset );
		}
	}

	/**
	 * 写入上层提供的 StringBuilder
	 *
	 * @param builder 上层的 StringBuilder
	 */
	protected void __toXmlText( StringBuilder builder ) {
		//添加Node名字
		builder.append( "<" ).append( getName() );

		//添加属性
		if ( attributeCount() > 0 ) {
			for ( Map.Entry< String, String > entry : getAttributes().entrySet() ) {
				builder.append( " " ).append( entry.getKey() ).append( "=" ).append( "\"" ).append( entry.getValue() ).append( "\"" );
			}
		}


		/*

			判断当前 node 的 子node 类型，一般的 xml 数据 子node 可以有Text以及Element
			但我们这里是不会的，所以仅需要判断 Text 或者 子Element 即可。所以情况有三种：

			1.没有子node，没有content
			2.没有子node，有content
			3.有子node，没有content

		 */
		if ( childCount() <= 0 && getContentValue() == null ) {
			// 1
			builder.append( " " ).append( "/>" );
		} else if ( getContentValue() != null ) {
			// 2
			builder.append( ">" ).append( getContentValue() ).append( "</" ).append( getName() ).append( ">" );
		} else {
			// 3
			builder.append( ">" );
			for ( BaseNode baseNode : getChildNodes() ) {
				baseNode.__toXmlText( builder );
			}
			builder.append( "</" ).append( getName() ).append( ">" );
		}
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
