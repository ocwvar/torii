package com.ocwvar.torii;

import com.ocwvar.torii.db.dao.*;
import com.ocwvar.utils.TextUtils;
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

	@Autowired
	Sv5CourseDao courseDao;

	@Autowired
	PaseliDao paseliDao;

	@Test
	public void function() {

	}

}
