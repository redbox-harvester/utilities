/*******************************************************************************
*Copyright (C) 2013 Queensland Cyber Infrastructure Foundation (http://www.qcif.edu.au/)
*
*This program is free software: you can redistribute it and/or modify
*it under the terms of the GNU General Public License as published by
*the Free Software Foundation; either version 2 of the License, or
*(at your option) any later version.
*
*This program is distributed in the hope that it will be useful,
*but WITHOUT ANY WARRANTY; without even the implied warranty of
*MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*GNU General Public License for more details.
*
*You should have received a copy of the GNU General Public License along
*with this program; if not, write to the Free Software Foundation, Inc.,
*51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
******************************************************************************/
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
	
	@Test
	public void validConfigWithCustomPath() {		
		def config = Config.getConfig("test", "config/config-sample.groovy", "custom/")
		assertTrue(new File("custom/config/config-sample.groovy").exists())
		assertFalse(new File("config/config-sample.groovy").exists())
		cleanUp(config)
	}

}
