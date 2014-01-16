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

import au.com.redboxresearchdata.util.config.Config;

/**
 * Unit tests for Config
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
		def config = Config.getConfig("testConfigWithoutCustom", "config-unit-testing.groovy", "src/test/resources/")
		assertEquals("user", config.datasource.user)
		cleanUp(config)	
	}
	
	@Test
	public void validConfigWithCustom() {
		def config = Config.getConfig("testConfigWithCustom", "config-unit-testing.groovy", "src/test/resources/")
		assertEquals("user2", config.datasource.user)		
		cleanUp(config)
	}	

	@Test
	public void testValidConfigWithCustomWithRuntimeChanges() {
		String env = "testConfigWithCustomWithRuntimeChanges"
		String configFileName = "config-unit-testing.groovy"
		String configDir = "src/test/resources/generated/custom/"
		
		// clone a copy 
		new File(configDir).mkdirs()
		new File(configDir+configFileName).write(new File("src/test/resources/"+configFileName).text)
				
		// now load from that cloned copy and add some runtime values
		def runTimeConfig1 = Config.getConfig(env, configFileName, configDir)
		assertEquals("user2", runTimeConfig1.datasource.user)
		String someRuntimeValue1 = "someRuntimeValue1"
		runTimeConfig1.datasource.runtimeProperty1 = someRuntimeValue1
		Config.saveConfig(runTimeConfig1, env)
		
		// now reload and check if the runtime property exists... 
		def runTimeConfig2 = Config.getConfig(env, configFileName, configDir)
		assertEquals("user2", runTimeConfig1.datasource.user)
		assertEquals(someRuntimeValue1, runTimeConfig2.datasource.runtimeProperty1)
		
		// now make a change on the default config, as in the case of an upgrade, (just delete the default config property from the Filesystem in cases of upgrade).
		ConfigSlurper slurper = new ConfigSlurper(env)
		File defaultConfigFile = new File(configDir + configFileName)
		def defaultConfig = slurper.parse(defaultConfigFile.toURI().toURL())
		String someNewPropertyValue1 = "someNewPropertyValue1"
		defaultConfig.datasource[someNewPropertyValue1] = someNewPropertyValue1
		defaultConfigFile.withWriter {writer->
			def conf = new ConfigObject()
			conf["environments"][env] = defaultConfig
			conf.writeTo(writer)
		}
		// now check if the new and shiny upgrade property carried over...
		runTimeConfig2 = Config.getConfig(env, configFileName, configDir)
		assertEquals("user2", runTimeConfig1.datasource.user)
		assertEquals(someRuntimeValue1, runTimeConfig2.datasource.runtimeProperty1)
		assertEquals(someNewPropertyValue1, runTimeConfig2.datasource.someNewPropertyValue1)
		
		cleanUp(runTimeConfig2)
	}
	
	@Test
	public void testValidConfigWithoutCustomWithRuntimeChanges() {
		String env = "testConfigWithoutCustomWithRuntimeChanges"
		String configFileName = "config-unit-testing.groovy"
		String configDir = "src/test/resources/generated/withoutcustom/"
		
		// clone a copy
		new File(configDir).mkdirs()
		new File(configDir+configFileName).write(new File("src/test/resources/"+configFileName).text)
				
		// now load from that cloned copy and add some runtime values
		def runTimeConfig1 = Config.getConfig(env, configFileName, configDir)
		assertEquals("user", runTimeConfig1.datasource.user)
		String someRuntimeValue1 = "someRuntimeValue1"
		runTimeConfig1.datasource.runtimeProperty1 = someRuntimeValue1
		Config.saveConfig(runTimeConfig1, env)
		
		// now reload and check if the runtime property exists...
		def runTimeConfig2 = Config.getConfig(env, configFileName, configDir)
		assertEquals("user", runTimeConfig1.datasource.user)
		assertEquals(someRuntimeValue1, runTimeConfig2.datasource.runtimeProperty1)
		
		// now make a change on the default config, as in the case of an upgrade, (just delete the default config property from the Filesystem in cases of upgrade).
		ConfigSlurper slurper = new ConfigSlurper(env)
		File defaultConfigFile = new File(configDir + configFileName)
		def defaultConfig = slurper.parse(defaultConfigFile.toURI().toURL())
		String someNewPropertyValue1 = "someNewPropertyValue1"
		defaultConfig.datasource[someNewPropertyValue1] = someNewPropertyValue1
		defaultConfigFile.withWriter {writer->
			def conf = new ConfigObject()
			conf["environments"][env] = defaultConfig
			conf.writeTo(writer)
		}
		// now check if the new and shiny upgrade property carried over...
		runTimeConfig2 = Config.getConfig(env, configFileName, configDir)
		assertEquals("user", runTimeConfig1.datasource.user)
		assertEquals(someRuntimeValue1, runTimeConfig2.datasource.runtimeProperty1)
		assertEquals(someNewPropertyValue1, runTimeConfig2.datasource.someNewPropertyValue1)
		
		cleanUp(runTimeConfig2)
	}
	
}
