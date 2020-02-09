package com.ocwvar.torii.db.dao;

import com.ocwvar.torii.db.entity.UnlockItem;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface UnlockItemDao {

	List< UnlockItem > findAllByRefId( String refId );

	void add(UnlockItem item);

}
