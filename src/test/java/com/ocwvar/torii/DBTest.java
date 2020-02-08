package com.ocwvar.torii;

import com.ocwvar.torii.db.dao.CardDao;
import com.ocwvar.torii.db.dao.Sv5ProfileDao;
import com.ocwvar.torii.db.dao.Sv5SettingDao;
import com.ocwvar.torii.db.entity.Sv5Profile;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith( SpringRunner.class )
@SpringBootTest
public class DBTest {

	@Autowired
	CardDao cardDao;

	@Autowired
	Sv5ProfileDao profileDao;

	@Autowired
	Sv5SettingDao settingDao;

	@Test
	public void function() {
		settingDao.createDefault( "8ABX1AHT05PYGH1D" );
	}

}
