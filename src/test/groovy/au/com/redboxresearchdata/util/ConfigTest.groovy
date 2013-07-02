package au.com.redboxresearchdata.util;

import static org.junit.Assert.*;

import org.apache.log4j.Logger;
import org.junit.Test;

import au.com.redboxresearchdata.util.ConfigTest;
import au.com.redboxresearchdata.util.config.Config;

/**
 * Unit tests for Config
 * 
 * TODO: Add more tests...
 * 
 * @author Shilo Banihit
 *
 */
class ConfigTest {

	private static final Logger log = Logger.getLogger(ConfigTest.class)
	
	void cleanUp(config) {
		if (config.file.cleanUnitTestingGenerated) {
			new File(config.file.runtimePath).delete()
		}
	}
	
	@Test
	public void validConfigWithoutCustom() {
		def config = Config.getConfig("testConfigWithoutCustom", "config-unit-testing.groovy")
		assertEquals("user", config.datasource.user)
		cleanUp(config)	
	}
	
	@Test
	public void validConfigWithCustom() {
		def config = Config.getConfig("testConfigWithCustom", "config-unit-testing.groovy")
		assertEquals("user2", config.datasource.user)
		cleanUp(config)
	}

}
