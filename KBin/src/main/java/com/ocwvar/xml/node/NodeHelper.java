package com.ocwvar.xml.node;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

public class NodeHelper {

	/**
	 * 将节点输出为XML对象
	 *
	 * @param node 节点对象
	 * @return XML 文档对象
	 */
	public static Document note2Xml( Node node ) throws ParserConfigurationException {
		final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		final Document document = factory.newDocumentBuilder().newDocument();
		Element root = document.createElement( node.getName() );
		root.setTextContent( node.getContentValue() );

		//处理参数
		node.__inputAttributes( root );

		//处理子节点
		node.__inputChildNodes( document, root );

		document.appendChild( root );
		return document;
	}

	/**
	 * 将 XML Document 转换为 XML 文本
	 *
	 * @param document Document对象
	 * @return XML 文本
	 * @throws Exception 异常
	 */
	public static String xml2Text( Document document ) throws Exception {
		final TransformerFactory factory = TransformerFactory.newDefaultInstance();
		final Transformer transformer = factory.newTransformer();
		final StringWriter writer = new StringWriter();
		transformer.setOutputProperty( OutputKeys.INDENT, "yes" );
		transformer.transform( new DOMSource( document ), new StreamResult( writer ) );
		writer.flush();
		writer.close();

		return writer.toString();
	}

	/**
	 * 将原始 XML 字节转换为 XML 对象
	 *
	 * @param data XML字节数组
	 * @return Document对象
	 */
	public static Document byte2Xml( byte[] data ) throws ParserConfigurationException, IOException, SAXException {
		final DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		final DocumentBuilder builder = builderFactory.newDocumentBuilder();
		final Document reqDocument = builder.parse( new ByteArrayInputStream( data ) );

		clean( reqDocument );
		return reqDocument;
	}

	/**
	 * 将 XML Document 转换为 Node 对象
	 *
	 * @param document Document对象
	 * @return Node 对象
	 */
	public static Node xml2Node( Document document ) {
		return xmlNodeConvert( document.getFirstChild(), null );
	}

	/**
	 * 将 XML文本 转换为 Node 对象
	 *
	 * @param rawXmlText 原始XML文本
	 * @return Node 对象
	 * @throws Exception 转换时出现的异常
	 */
	public static Node xml2Node( String rawXmlText ) throws Exception {
		final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setIgnoringComments( true );

		final DocumentBuilder builder = factory.newDocumentBuilder();
		final Document document = builder.parse( new InputSource( new StringReader( rawXmlText.replaceAll( ">\\s+<", "><" ) ) ) );
		return xml2Node( document );
	}

	private static Node xmlNodeConvert( org.w3c.dom.Node xmlNode, Node parentNode ) {
		switch ( xmlNode.getNodeType() ) {
			//这些节点不需要处理
			case org.w3c.dom.Node.TEXT_NODE:
			case org.w3c.dom.Node.ATTRIBUTE_NODE:
				return null;
		}

		//复制名称
		final Node node = new Node( xmlNode.getNodeName() );

		//复制属性
		if ( xmlNode.hasAttributes() ) {
			final NamedNodeMap attrs = xmlNode.getAttributes();
			org.w3c.dom.Node attNode;
			for ( int i = 0; i < attrs.getLength(); i++ ) {
				attNode = attrs.item( i );
				node.addAttribute( attNode.getNodeName(), attNode.getNodeValue() );
			}
		}

		//复制值
		if ( xmlNode.getFirstChild() != null && xmlNode.getFirstChild().getNodeType() == org.w3c.dom.Node.TEXT_NODE ) {
			node.setContentValue( xmlNode.getTextContent() );
		}

		//检查是否有子节点
		if ( xmlNode.hasChildNodes() ) {
			final NodeList childNodeList = xmlNode.getChildNodes();
			org.w3c.dom.Node childNode;
			for ( int i = 0; i < childNodeList.getLength(); i++ ) {
				childNode = childNodeList.item( i );

				switch ( xmlNode.getNodeType() ) {
					//这些节点不需要处理
					case org.w3c.dom.Node.TEXT_NODE:
					case org.w3c.dom.Node.ATTRIBUTE_NODE:
						continue;
				}

				xmlNodeConvert( childNode, node );
			}
		}

		//双向绑定到父节点
		if ( parentNode != null ) {
			parentNode.addChildNode( node );
		}

		return node;
	}

	/**
	 * Scrubs empty nodes from a document so we don't accidentally read them.
	 *
	 * @param node The root node of the document to clean.
	 */
	private static void clean( final org.w3c.dom.Node node ) {
		final NodeList childrem = node.getChildNodes();

		for ( int n = childrem.getLength() - 1; n >= 0; n-- ) {
			final org.w3c.dom.Node child = childrem.item( n );
			final short nodeType = child.getNodeType();

			if ( nodeType == org.w3c.dom.Node.ELEMENT_NODE ) {
				clean( child );
			} else if ( nodeType == org.w3c.dom.Node.TEXT_NODE ) {
				final String trimmedNodeVal = child.getNodeValue().trim();

				if ( trimmedNodeVal.length() == 0 ) {
					node.removeChild( child );
				} else {
					child.setNodeValue( trimmedNodeVal );
				}
			} else if ( nodeType == org.w3c.dom.Node.COMMENT_NODE ) {
				node.removeChild( child );
			}
		}
	}

}
