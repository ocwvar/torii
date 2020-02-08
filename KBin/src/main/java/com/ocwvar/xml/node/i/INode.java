package com.ocwvar.xml.node.i;

import com.ocwvar.utils.annotation.NotNull;
import com.ocwvar.utils.annotation.Nullable;
import com.ocwvar.xml.node.BaseNode;

import java.util.List;
import java.util.Map;

/**
 * 节点接口
 */
public interface INode {

	String ATTR_TYPE = "__type";
	String ATTR_COUNT = "__count";
	String ATTR_SIZE = "__size";


	/**
	 * @return 节点名称
	 */
	@NotNull
	String getName();

	/**
	 * 获取此节点的类型
	 * <p>
	 * 如果此节点没有设置 "__type" 属性，则此方法返回 NULL
	 *
	 * @return 节点类型
	 */
	@Nullable
	String getType();

	/**
	 * @return 内容值
	 */
	@Nullable
	String getContentValue();

	/**
	 * 设置内容值
	 *
	 * @param value 内容值
	 */
	void setContentValue( String value );

	/**
	 * @return 获取属性Map对象，如果没有属性，则返回 NULL
	 */
	@Nullable
	Map< String, String > getAttributes();

	/**
	 * 获取属性值
	 *
	 * @param key 属性名
	 * @return 属性值，如果不存在检索的属性名，则返回 NULL
	 */
	@Nullable
	String getAttribute( String key );

	/**
	 * 添加属性
	 *
	 * @param key   属性名
	 * @param value 属性值
	 */
	void addAttribute( String key, String value );

	/**
	 * @return 第一个子节点，如果没有，则返回 NULL
	 */
	@Nullable
	BaseNode getFirstChildNode();

	/**
	 * 通过子节点名称来获取
	 *
	 * @param nodeName 子节点名称
	 * @return 节点对象，如果没有则返回 NULL
	 */
	@Nullable
	BaseNode indexChildNode( @NotNull String nodeName );

	/**
	 * 通过位置来获取子节点
	 *
	 * @param pos 子节点位置
	 * @return 节点对象，如果没有则返回 NULL
	 */
	@Nullable
	BaseNode indexChildNode( int pos );

	/**
	 * @return 子节点数组
	 */
	@Nullable
	List< BaseNode > getChildNodes();

	/**
	 * 设置父节点
	 *
	 * @param parentNode 父节点
	 */
	void setParentNode( @Nullable BaseNode parentNode );

	/**
	 * @return 父节点，有可能为 NULL
	 */
	@Nullable
	BaseNode getParentNode();

	/**
	 * 添加新的节点
	 *
	 * @param childNode 新的子节点
	 */
	void addChildNode( BaseNode childNode );

	/**
	 * @return 属性数量
	 */
	int attributeCount();

	/**
	 * @return 子节点数量
	 */
	int childCount();

}
