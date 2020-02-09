package com.ocwvar.torii.db.dao;

import com.ocwvar.torii.db.entity.Sv5Score;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface Sv5ScoreDao {

	List< Sv5Score > findAllScoreByRefId( String refId );

	Sv5Score findScore( @Param( "refId" ) String refId, @Param( "music_id" ) String music_id, @Param( "music_type" ) String music_type );

	void createNew( Sv5Score score );

	void update( Sv5Score score );

}
