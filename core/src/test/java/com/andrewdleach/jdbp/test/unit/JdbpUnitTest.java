package com.andrewdleach.jdbp.test.unit;

import org.junit.Assert;
import org.junit.Test;

import com.andrewdleach.jdbp.Jdbp;
import com.andrewdleach.jdbp.driver.DriverLocator;
import com.andrewdleach.jdbp.exception.JdbpException;
import com.andrewdleach.jdbp.properties.PropertySetManager;
import com.andrewdleach.jdbp.schema.SchemaManager;

import mockit.Mock;
import mockit.MockUp;
import mockit.Tested;

public class JdbpUnitTest {

	@Tested
	Jdbp jdbp;

	@Test
	public void testGetInstance() {
		new MockUp<DriverLocator>() {
			@Mock
			void findJdbcDriver() {}
		};

		new MockUp<PropertySetManager>() {
			@Mock
			void loadAllProperties() {}
		};

		new MockUp<SchemaManager>() {
			@Mock
			void createAllSchemasFromProperties() {}
		};
		Assert.assertNotNull(Jdbp.getInstance());
	}

	@Test
	public void testGetInstance_throwsException() {
		new MockUp<DriverLocator>() {
			@Mock
			void findJdbcDriver() throws JdbpException {
				JdbpException.throwException("testing exception");
			}
		};
		try {
			Jdbp.getInstance();

		}
		catch(Exception e) {
			Assert.assertTrue(e instanceof JdbpException);
		}
	}
}
