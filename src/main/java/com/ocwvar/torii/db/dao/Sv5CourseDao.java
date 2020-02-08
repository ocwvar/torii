package com.ocwvar.torii.db.dao;

import com.ocwvar.torii.db.entity.Sv5Course;

public interface Sv5CourseDao {

	void save( Sv5Course course );

	void update( Sv5Course course );

	void getList( String refId );

	void get( String refId, String season_id, String course_id );

}
