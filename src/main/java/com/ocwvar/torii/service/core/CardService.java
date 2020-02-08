package com.ocwvar.torii.service.core;

import com.ocwvar.torii.db.dao.CardDao;
import com.ocwvar.torii.db.dao.PaseliDao;
import com.ocwvar.torii.db.entity.Card;
import com.ocwvar.torii.db.entity.Paseli;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CardService {

	//卡片数据Dao
	private final CardDao cardDao;

	//PASELI DAO
	private final PaseliDao paseliDao;

	@Autowired
	public CardService( CardDao cardDao, PaseliDao paseliDao ) {
		this.cardDao = cardDao;
		this.paseliDao = paseliDao;
	}

	/**
	 * 检查PIN码是否相同
	 *
	 * @param pin   PIN码
	 * @param refId 加密后的卡号
	 * @return 是否相同，如果卡号不存在，亦返回 False
	 */
	public boolean checkPIN( String pin, String refId ) {
		final Card savedCard = this.cardDao.findByRefId( refId );
		return savedCard != null && savedCard.getPin().equals( pin );
	}

	/**
	 * 新增卡片数据，此操作将会同时创建 PASELI 数据
	 *
	 * @param card 新的卡片数据
	 */
	public void insertCard( Card card ) {
		this.cardDao.insert( card );
		this.paseliDao.createDefault( card.getRawId(), 1000, 0 );
	}

	/**
	 * 通过 RAW_ID 查找卡片
	 *
	 * @param rawId RAW_ID
	 * @return 卡片数据，查找失败返回 NULL
	 */
	public Card findCardByRawId( String rawId ) {
		return this.cardDao.findByRawId( rawId );
	}

	/**
	 * 通过 REF_ID 查找卡片
	 *
	 * @param refId REF_ID
	 * @return 卡片数据，查找失败返回 NULL
	 */
	public Card findCardByRefId( String refId ) {
		return this.cardDao.findByRefId( refId );
	}

	/**
	 * 通过 RAW_ID 查找 PASELI 数据
	 *
	 * @param rawId RAW_ID
	 * @return PASELI数据，查找失败返回 NULL
	 */
	public Paseli findPaseliByRawId( String rawId ) {
		return this.paseliDao.findByRawId( rawId );
	}

	/**
	 * 更新 PASELI 账户余额
	 *
	 * @param rawId   raw_id
	 * @param balance 新的余额
	 */
	public void updateBalance( String rawId, int balance ) {
		this.paseliDao.updateBalance( rawId, balance );
	}

	/**
	 * 创建新的 Season ，如果已有相同的 raw_id 则会删除
	 *
	 * @param rawId     RAW_ID
	 * @param season_id season_id
	 */
	public void createSeason( String rawId, String season_id ) {
		this.paseliDao.destroySeasonByRawId( rawId );
		this.paseliDao.createSeason( rawId, season_id );
	}

	/**
	 * 通过 season_id 查找 raw_id
	 *
	 * @param season_id season_id
	 * @return raw_id，查找失败返回 NULL
	 */
	public String findRawIdBySeasonID( String season_id ) {
		return this.paseliDao.findRawIdBySeasonID( season_id );
	}

	/**
	 * 删除所有相同 raw_id 的 season 数据
	 *
	 * @param rawId rawId
	 */
	public void destroySeasonByRawId( String rawId ) {
		this.paseliDao.destroySeasonByRawId( rawId );
	}

	/**
	 * 删除所有相同 season_id 的 season 数据
	 *
	 * @param season_id season_id
	 */
	public void destroySeasonBySeasonID( String season_id ) {
		this.paseliDao.destroySeasonBySeasonID( season_id );
	}
}
