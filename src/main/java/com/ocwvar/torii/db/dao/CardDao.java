package com.ocwvar.torii.db.dao;

import com.ocwvar.torii.db.entity.Card;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface CardDao {

	void insert( Card card );

	Card findByRawId( String rawId );

	Card findByRefId( String refId );

	void deleteByRawId( String rawId );

	void deleteByRefId( String refId );

	void updatePinByRawId( String rawId, String pin );

	void updatePinByRefId( String refId, String pin );

}
