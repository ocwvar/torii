package com.ocwvar.torii.db.dao;

import com.ocwvar.torii.db.entity.Sv5ProfileParam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface Sv5ProfileParamDao {

	List< Sv5ProfileParam > getAllByRefId( String refId );

	Sv5ProfileParam findById( @Param( "refId" ) String refId, @Param( "id" ) String id );

	void updateParam( Sv5ProfileParam param );

	void save( Sv5ProfileParam param );

}
