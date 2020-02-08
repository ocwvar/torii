package com.ocwvar.torii.db.dao;

import com.ocwvar.torii.db.entity.Sv5Course;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface Sv5CourseDao {

	List< Sv5Course > getList( String refId );

	Sv5Course get( @Param( "refId" ) String refId, @Param( "season_id" ) String season_id, @Param( "course_id" ) String course_id );

	void save( Sv5Course course );

	void update( Sv5Course course );

}
