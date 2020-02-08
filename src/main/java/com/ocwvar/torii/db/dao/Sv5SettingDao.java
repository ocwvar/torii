package com.ocwvar.torii.db.dao;

import com.ocwvar.torii.db.entity.Sv5Setting;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface Sv5SettingDao {

	Sv5Setting findByRefId( String refId );

	void save( Sv5Setting setting );

	void createDefault( @Param( "refId" ) String refId );

	void delete( String refId );

}
