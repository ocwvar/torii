package com.ocwvar.torii.controller.core;

import com.ocwvar.torii.Configs;
import com.ocwvar.torii.Field;
import com.ocwvar.torii.db.entity.Card;
import com.ocwvar.torii.db.entity.Paseli;
import com.ocwvar.torii.service.core.CardService;
import com.ocwvar.torii.utils.protocol.Protocol;
import com.ocwvar.utils.Log;
import com.ocwvar.utils.TextUtils;
import com.ocwvar.utils.node.Node;
import com.ocwvar.utils.node.TypeNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 处理 PASELI 相关事件
 */
@RestController
public class EaCoin {

	/*
		请求样例：
		URL:http://127.0.0.1/torii/eacoin/KFC:J:A:A:2019100800/eacoin/checkin
		Need decompress:false
		Need rc4 decode:false
		<?xml version="1.0" encoding="UTF-8" standalone="no"?>
		<call model="KFC:J:A:A:2019100800" srcid="01201000003756CA9F84" tag="PTAGANNv">
		    <eacoin method="checkin">
		        <cardtype __type="str">1</cardtype>
		        <cardid __type="str">E004123326432122</cardid>
		        <passwd __type="str">8275</passwd>
		        <ectype __type="str">1</ectype>
		    </eacoin>
		</call>

		URL:http://127.0.0.1/torii/eacoin/KFC:J:A:A:2019100800/eacoin/consume
		Need decompress:false
		Need rc4 decode:false
		<?xml version="1.0" encoding="UTF-8" standalone="no"?>
		<call model="KFC:J:A:A:2019100800" srcid="01201000003756CA9F84" tag="ahMuAGKw">
		    <eacoin esdate="2020-02-08T15:22:53" esid="e310db2e25519c83447cd96c112ff46fd8d5473e44275272ee04787ab924d968" method="consume">
		        <sessid __type="str">S</sessid>
		        <sequence __type="s16">1</sequence>
		        <payment __type="s32">247</payment>		支付金额
		        <service __type="s32">0</service>
		        <itemtype __type="str">0</itemtype>
		        <detail __type="str">/eacoin/p1s04a1</detail>
		    </eacoin>
		</call>

	 */

	/**
	 * 无限 PASELI 账户的余额
	 */
	private final String INFINITE_BALANCE_NUMBER = "1145141919";

	private final CardService cardService;

	@Autowired
	public EaCoin( CardService cardService ) {
		this.cardService = cardService;
	}

	@PostMapping( path = "/torii/eacoin/**" )
	public void function( HttpServletRequest request, HttpServletResponse response ) throws Exception {
		if ( !Configs.isPaseliEnable() ) {
			//PASELI 不支持
			final Node root = new Node( "response" );
			final Node eacoin = new Node( "eacoin" );
			root.addChildNode( eacoin );
			eacoin.addAttribute( "status", Field.CODE_NOT_ALLOWED );
			Protocol.encryptAndCommit( root, request, response );
			return;
		}

		final Node root;
		final Node call = Protocol.decrypt( request );
		final Node call_eacoin = ( Node ) call.getFirstChildNode();
		final String pcbid = call.getAttribute( "srcid" );

		switch ( call_eacoin.getAttribute( "method" ) ) {
			default:
				root = null;
				break;

			case "checkin":
				Log.getInstance().print( "进入 PASELI 数据检查流程" );
				root = checkin( call_eacoin );
				break;

			case "checkout":
				Log.getInstance().print( "进入 PASELI SEASON 销毁流程" );
				root = checkout( call_eacoin );
				break;

			case "consume":
				Log.getInstance().print( "进入 PASELI 消费处理流程" );
				root = consume( call_eacoin );
				break;
		}

		Protocol.encryptAndCommit( root, request, response );
		Log.getInstance().print( "已处理完成节点：EACOIN PASELI" );
	}

	/**
	 * 处理 checkin 事件
	 * <p>
	 * 查找 PASELI 账户是否存在，以及返回账户余额
	 *
	 * @param call_eacoin 请求数据
	 * @return 响应数据
	 */
	private Node checkin( Node call_eacoin ) {
		final Node root = new Node( "response" );
		final Node eacoin = new Node( "eacoin" );
		root.addChildNode( eacoin );

		final Node cardid = ( Node ) call_eacoin.indexChildNode( "cardid" );
		final Node passwd = ( Node ) call_eacoin.indexChildNode( "passwd" );

		//检查参数完整性
		if ( cardid == null || passwd == null ) {
			Log.getInstance().print( "客户端没有传递 cardid 或 passwd 参数" );
			eacoin.addAttribute( "status", Field.CODE_NO_PROFILE );
			return root;
		}

		//检查是否存在此卡片数据
		final Card card = this.cardService.findCardByRawId( cardid.getContentValue() );
		if ( card == null ) {
			Log.getInstance().print( "无对应卡号记录：" + cardid.getContentValue() );
			eacoin.addAttribute( "status", Field.CODE_NO_PROFILE );
			return root;
		}

		//检查卡片密码
		if ( !card.getPin().equals( passwd.getContentValue() ) ) {
			Log.getInstance().print( "密码错误" );
			eacoin.addAttribute( "status", Field.CODE_INVALID_PIN );
			return root;
		}

		//查找PASELI账户并返回信息
		final Paseli paseli = this.cardService.findPaseliByRawId( cardid.getContentValue() );
		final String seasonId = TextUtils.getRandomText( false, 10 );
		eacoin.addChildNode( new TypeNode( "sequence", "1", "s16" ) );
		eacoin.addChildNode( new TypeNode( "acstatus", "1", "u8" ) );
		eacoin.addChildNode( new TypeNode( "acid", paseli.getAcid() ) );
		eacoin.addChildNode( new TypeNode( "acname", paseli.getAcname() ) );
		eacoin.addChildNode( new TypeNode( "balance", paseli.isInfiniteBalance() ? INFINITE_BALANCE_NUMBER : paseli.getBalance(), "s32" ) );
		eacoin.addChildNode( new TypeNode( "sessid", seasonId ) );
		this.cardService.createSeason( cardid.getContentValue(), seasonId );

		return root;
	}

	/**
	 * 处理 checkout 事件
	 *
	 * @param call_eacoin 请求数据
	 * @return 响应数据
	 */
	private Node checkout( Node call_eacoin ) {
		final Node sessid = ( Node ) call_eacoin.indexChildNode( "sessid" );
		if ( sessid != null && !TextUtils.isEmpty( sessid.getContentValue() ) ) {
			this.cardService.destroySeasonBySeasonID( sessid.getContentValue() );
		}

		final Node root = new Node( "response" );
		root.addChildNode( new Node( "eacoin" ) );

		return root;
	}

	/**
	 * 处理 consume 事件
	 * <p>
	 * PASELI 消费扣费处理
	 *
	 * @param call_eacoin 请求数据
	 * @return 响应数据
	 */
	private Node consume( Node call_eacoin ) {
		final Node payment = ( Node ) call_eacoin.indexChildNode( "payment" );
		final Node sessid = ( Node ) call_eacoin.indexChildNode( "sessid" );
		final Node root = new Node( "response" );
		final Node eacoin = new Node( "eacoin" );
		root.addChildNode( eacoin );
		eacoin.addChildNode( new TypeNode( "autocharge", "0", "u8" ) );

		//参数缺失
		if ( payment == null || sessid == null ) {
			eacoin.addChildNode( new TypeNode( "acstatus", "2", "u8" ) );
			eacoin.addChildNode( new TypeNode( "balance", "0", "s32" ) );
			return root;
		}

		//检查余额
		final String rawId = this.cardService.findRawIdBySeasonID( sessid.getContentValue() );
		if ( TextUtils.isEmpty( rawId ) ) {
			//找不到这个 season
			eacoin.addChildNode( new TypeNode( "acstatus", "2", "u8" ) );
			eacoin.addChildNode( new TypeNode( "balance", "0", "s32" ) );
			return root;
		}

		final Paseli paseli = this.cardService.findPaseliByRawId( rawId );
		final int have = Integer.parseInt( paseli.getBalance() );
		final int spend = Integer.parseInt( payment.getContentValue() );
		final boolean need2UpdateBalance;

		if ( paseli.isInfiniteBalance() ) {
			//无限 PASELI 账户
			eacoin.addChildNode( new TypeNode( "acstatus", "0", "u8" ) );
			eacoin.addChildNode( new TypeNode( "balance", INFINITE_BALANCE_NUMBER, "s32" ) );
			need2UpdateBalance = false;
		} else {
			//计算扣费
			final boolean enoughBalance = have - spend >= 0;
			need2UpdateBalance = enoughBalance;

			if ( enoughBalance ) {
				eacoin.addChildNode( new TypeNode( "acstatus", "0", "u8" ) );
				eacoin.addChildNode( new TypeNode( "balance", String.valueOf( have - spend ), "s32" ) );
			} else {
				//费用不足
				eacoin.addChildNode( new TypeNode( "acstatus", "1", "u8" ) );
				eacoin.addChildNode( new TypeNode( "balance", paseli.getBalance(), "s32" ) );
			}
		}

		if ( need2UpdateBalance ) {
			this.cardService.updateBalance( rawId, have - spend );
		}

		return root;
	}

}
