package com.ocwvar.torii.db.dao;

import com.ocwvar.torii.db.entity.Sv5Profile;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface Sv5ProfileDao {

	Sv5Profile findByRefId( String refId );

	void save( Sv5Profile profile );

	void createDefault( @Param( "refId" ) String refId, @Param( "playerName" ) String playerName, @Param( "userCode" ) String userCode );

	void delete( String refId );

}
