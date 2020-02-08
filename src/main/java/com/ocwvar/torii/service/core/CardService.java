package com.ocwvar.torii.service.core;

import com.ocwvar.torii.db.dao.CardDao;
import com.ocwvar.torii.db.entity.Card;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CardService implements CardDao {

	//卡片数据Dao
	private final CardDao cardDao;

	@Autowired
	public CardService( CardDao cardDao ) {
		this.cardDao = cardDao;
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

	@Override
	public void insert( Card card ) {
		this.cardDao.insert( card );
	}

	@Override
	public Card findByRawId( String rawId ) {
		return this.cardDao.findByRawId( rawId );
	}

	@Override
	public Card findByRefId( String refId ) {
		return this.cardDao.findByRefId( refId );
	}

	@Override
	public void deleteByRawId( String rawId ) {
		this.cardDao.deleteByRawId( rawId );
	}

	@Override
	public void deleteByRefId( String refId ) {
		this.cardDao.deleteByRefId( refId );
	}

	@Override
	public void updatePinByRawId( String rawId, String pin ) {
		this.cardDao.updatePinByRawId( rawId, pin );
	}

	@Override
	public void updatePinByRefId( String refId, String pin ) {
		this.cardDao.updatePinByRefId( refId, pin );
	}

}
