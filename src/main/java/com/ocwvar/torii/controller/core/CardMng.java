package com.ocwvar.torii.controller.core;

import com.ocwvar.torii.Configs;
import com.ocwvar.torii.Field;
import com.ocwvar.torii.db.entity.Card;
import com.ocwvar.torii.service.core.CardService;
import com.ocwvar.torii.utils.protocol.Protocol;
import com.ocwvar.utils.Log;
import com.ocwvar.utils.cardX.A;
import com.ocwvar.xml.node.Node;
import com.ocwvar.xml.node.NodeBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 负责读卡、创卡功能
 */
@RestController
public class CardMng {

	private final CardService cardService;

	@Autowired
	public CardMng( CardService cardService ) {
		this.cardService = cardService;
	}

	/*

	请求返回内容样本：
	<call model="KFC:J:A:A:2019100800" srcid="012085D5525C6F1504B0" tag="ABYaAOGw">
    	<cardmng cardid="E004123326432122" cardtype="1" method="inquire" update="0"/>
	</call>

	其中 method 中代表本次的执行类型：
	inquire —— 默认刷卡触发事件，在这事件中检测是否需要创建新用户
	authpass —— 通过了上面的事件后，检测用户输入的 PIN码 是否正确
	getrefid —— 传入 卡号、PIN码，注册新用户
	bindmodel —— 旧数据迁移——不做
	getkeepspan	—— 未知用途
	getdatalist	—— 未知用途

	 */

	@PostMapping( path = "/torii/cardmng/**" )
	public void function( HttpServletRequest request, HttpServletResponse response ) throws Exception {
		final Node info = ( Node ) Protocol.decrypt( request ).getFirstChildNode();

		final String rawId = info.getAttribute( "cardid" );
		final String method = info.getAttribute( "method" );
		final String pin = info.getAttribute( "pass" );
		final String refID = info.getAttribute( "refid" );
		final String pin_getrefid = info.getAttribute( "passwd" );

		Node responseNode = null;
		switch ( method ) {
			case "inquire":
				Log.getInstance().print( "触发刷卡流程，卡号：" + rawId );
				responseNode = inquire( rawId );
				break;

			case "authpass":
				Log.getInstance().print( "触发PIN验证流程，REF_ID：" + refID );
				responseNode = authpass( refID, pin );
				break;

			case "getrefid":
				Log.getInstance().print( "触发创建账号流程，卡号:" + rawId + " PIN:" + pin_getrefid );
				responseNode = getrefid( rawId, pin_getrefid );
				break;
		}

		Protocol.encryptAndCommit( responseNode, request, response );
		Log.getInstance().print( "已处理完成节点：CARD MANAGER" );
	}

	/**
	 * 处理 inquire 事件
	 *
	 * @param rawId E004卡号
	 * @return 返回的内容
	 */
	private Node inquire( String rawId ) {
		final Node root = new Node( "response" );
		final Card savedCard = this.cardService.findCardByRawId( rawId );
		if ( savedCard == null ) {
			Log.getInstance().print( "没有注册的卡号，请求进入注册流程" );
			root.addChildNode(
					new NodeBuilder( "cardmng" )
							.addAttribute( "status", Field.CODE_NOT_REGISTERED )
							.build()
			);

			return root;
		}

		Log.getInstance().print( "已注册的卡号，请求进入PIN码验证流程" );
		root.addChildNode(
				new NodeBuilder( "cardmng" )
						.addAttribute( "binded", "1" )    //账号是否已绑定到当前版本
						.addAttribute( "dataid", savedCard.getRefId() )    //加密后的卡号
						.addAttribute( "ecflag", Configs.isIsPaseliEnable() ? "1" : "0" )    //PASELI 支持
						.addAttribute( "expired", "0" )    //是否已过期需要迁移数据
						.addAttribute( "newflag", "1" )    //UNKNOWN
						.addAttribute( "refid", savedCard.getRefId() )    //加密后的卡号
						.build()
		);

		return root;
	}

	/**
	 * 处理 authpass 事件
	 *
	 * @param refId 加密卡号
	 * @param pin   PIN码
	 * @return 返回的内容
	 */
	private Node authpass( String refId, String pin ) {
		final Node root = new Node( "response" );
		root.addChildNode(
				new NodeBuilder( "cardmng" )
						.addAttribute( "status", this.cardService.checkPIN( pin, refId ) ? Field.CODE_SUCCESS : Field.CODE_INVALID_PIN )
						.build()
		);

		return root;
	}

	/**
	 * 处理 getrefid 事件
	 *
	 * @param rawId E004卡号
	 * @param pin   PIN码
	 * @return 返回的内容
	 */
	private Node getrefid( String rawId, String pin ) {

		//加密的E004卡号
		Log.getInstance().print( "开始计算新卡的 REF_ID" );
		final String refId = new A().toKonamiID( rawId );
		this.cardService.insertCard( new Card( refId, rawId, pin ) );

		final Node root = new Node( "response" );
		root.addChildNode(
				new NodeBuilder( "cardmng" )
						.addAttribute( "dataid", refId )
						.addAttribute( "refid", refId )
						.build()
		);


		Log.getInstance().print( "新卡片注册完成，REF_ID：" + refId );
		return root;
	}

}
