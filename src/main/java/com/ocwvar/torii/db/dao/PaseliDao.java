package com.ocwvar.torii.db.dao;

import com.ocwvar.torii.db.entity.Paseli;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface PaseliDao {

	Paseli findByRawId( String rawId );

	void updateBalance( @Param( "rawId" ) String rawId, @Param( "balance" ) int balance );

	void createDefault( @Param( "rawId" ) String rawId, @Param( "balance" ) int balance, @Param( "infinite_balance" ) int infinite_balance );

	void createSeason( @Param( "rawId" ) String rawId, @Param( "season_id" ) String season_id );

	String findRawIdBySeasonID( String season_id );

	void destroySeasonByRawId( String rawId );

	void destroySeasonBySeasonID( String season_id );

}
