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
package au.com.redboxresearchdata.util.config

import org.apache.log4j.Logger;
/**
 * 
 * Responsible for config seeding, loading and merging.
 * 
 * @author Shilo Banihit
 * @since 1.0
 *
 */
class Config {

	private static final Logger log = Logger.getLogger(Config.class)
	
	
	/**
	 * Loads the configuration using the environment and default config file path parameters. 
	 * <br>
	 * The configuration must be of <a href="http://groovy.codehaus.org/ConfigSlurper">ConfigSlurper</a> format and must specify 'file.customPath' and 'file.runtimePath'.
	 * Configuration must provide a non-empty 'file.runtimePath', whilst 'file.customPath' can be an empty string. 
	 * <br>
	 * This method attempts to load from the filesystem, then falls back to the classpath. It will then create a cache of the loaded configuration on the specified 'file.runtimePath'.
	 * If 'file.customPath' is not null, then this is merged with the default config, overwriting the default values. Date and time generated is saved at 'generated' property.
	 * Please do not modify the 'file.runtimePath' file directly, set and modify a 'file.customPath' instead.
	 * 
	 * @param environment
	 * @param defConfigPath
	 * @param baseDir = optional base directory prefixed to the defConfigPath.
	 * @return ConfigObject loaded 
	 */
	public static ConfigObject getConfig(environment, defConfigPath, baseDir= "", binding=[:]) {
		def defaultConfigPath = "${baseDir}${defConfigPath}"
		log.info("Loading base config path: ${defaultConfigPath}")
		def defaultConfigFile = new File(defaultConfigPath)
		
		if (!defaultConfigFile.exists()) {			
			log.info("Seeding configuration from classpath:/${defConfigPath}")
			def defStream = Config.class.getResourceAsStream("/${defConfigPath}")
			if (defStream) {
				def parentDirs = defaultConfigPath.lastIndexOf("/") > 0 ? defaultConfigPath.substring(0, defaultConfigPath.lastIndexOf("/")) : ""
				if (parentDirs) {
					if (log.isDebugEnabled()) {
						log.debug("Creating parent directory(ies): ${parentDirs}")
					}
					new File(parentDirs).mkdirs()
				}				
				if (!defaultConfigFile.createNewFile()) {
					log.error("Error creating base config file.")
					return null
				}
				defaultConfigFile.write(defStream.getText("UTF-8"))
				defStream.close()	
			} else {
				log.debug("Base config file cannot be accessed from the jar file.")
				return null
			}
		}
		if (log.isDebugEnabled()) {
			log.debug("Using environment: ${environment}")
			log.debug("Base config path at: ${defaultConfigPath}")
		}
		if (defaultConfigFile.exists() && defaultConfigFile.isFile()) {
			def slurper = new ConfigSlurper(environment)
			slurper.setBinding(binding)
			def defConfig = slurper.parse(defaultConfigFile.toURI().toURL())
			def createRuntimeConfig = false
			def customConfigPath = defConfig.file.customPath
			def runtimeConfigPath = defConfig.file.runtimePath
			log.debug("Custom config path at: ${customConfigPath}")
			log.debug("Runtime config path at: ${runtimeConfigPath}")
			def customConfigFile = null
			
			if (runtimeConfigPath?.trim()) {
				def runtimeConfigFile = new File(runtimeConfigPath)
				customConfigFile = new File(customConfigPath)
				if (customConfigFile.exists()) {
					if (customConfigFile.exists() && customConfigFile.lastModified() > runtimeConfigFile.lastModified() ) {
						createRuntimeConfig = true
					}
				} else {
					if (!runtimeConfigFile.exists()) {
						createRuntimeConfig = true
					}
				} 
				if (createRuntimeConfig) {
					if (customConfigFile?.exists()) {
						if (log.isDebugEnabled()) {
							log.debug("Custom config at '${customConfigPath}' exists and seems to have been updated, merging with default.")
						}
						slurper = new ConfigSlurper(environment)
						slurper.setBinding(binding)
						def customConfig = slurper.parse(customConfigFile.toURI().toURL())
						defConfig.merge(customConfig)
					}
					if (log.isDebugEnabled()) {
						log.debug("Creating runtime config at: ${runtimeConfigPath}")
					}
					if (runtimeConfigPath.lastIndexOf("/") > 0) {
						def parentDirs = runtimeConfigPath.substring(0, runtimeConfigPath.lastIndexOf("/"))
						if (log.isDebugEnabled()) {
							log.debug("Creating parent directory(ies): ${parentDirs}")
						}
						new File(parentDirs).mkdirs()
					}
					runtimeConfigFile.withWriter { writer ->
						def mainConfig = new ConfigObject()
						mainConfig['environments'][environment] = defConfig
						mainConfig.writeTo(writer)
					}
				}
				if (log.isInfoEnabled()) {
					log.info("Configuration loaded. Using environment '${environment}' of config at: ${runtimeConfigPath}. Please DO NOT DIRECTLY EDIT this file. If you want to modify configuration, please override corresponding entries at ${customConfigPath}")
				}
				slurper = new ConfigSlurper(environment)
				slurper.setBinding(binding)
				return slurper.parse(runtimeConfigFile.toURI().toURL())
			} else {
				log.error("Please specify 'config.runtimePath' in the default config: ${defaultConfigPath}")
			}
		} else {
			log.error("Please ensure that the default config file exists: ${defaultConfigPath}")
		}
		return null
	}
	
	public static void saveConfig(ConfigObject config) {
		def runtimeConfigPath = config.file.runtimePath
		def runtimeConfigFile = new File(runtimeConfigPath)
		if (!runtimeConfigFile.exists()) {
			log.info("Runtime config path does not exist, creating a new file: ${runtimeConfigPath}")			
		}
		def cloneConfig = config.clone()
		config.file.ignoreEntriesOnSave?.each {
			cloneConfig.remove(it)
		}
		runtimeConfigFile.withWriter { writer ->
			cloneConfig.writeTo(writer) 
		}		
	}
}
