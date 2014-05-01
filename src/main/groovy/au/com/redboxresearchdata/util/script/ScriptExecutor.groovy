/*******************************************************************************
 *Copyright (C) 2014 Queensland Cyber Infrastructure Foundation (http://www.qcif.edu.au/)
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
package au.com.redboxresearchdata.util.script

import groovy.util.ConfigObject;
import groovy.util.Expando;

import java.util.Map;

import javax.script.ScriptEngine
import javax.script.ScriptEngineManager
import org.apache.commons.io.FilenameUtils
import org.apache.log4j.Logger
import javax.script.ScriptException
/**
 * Convenience class for launching JVM supported scripts.
 * 
 * @author <a href="https://github.com/shilob">Shilo Banihit</a>
 *
 */
class ScriptExecutor {

	static final Logger log = Logger.getLogger(ScriptExecutor.class)

	/**
	 * A method for launching scripts 
	 * 
	 * @param scriptChain - list of maps that specify a script and a script config
	 * @param checkData - if true, stops chain execution when a previous script nulls the data 
	 * @param data
	 * @param type
	 * @param config
	 * @return Expando instance containing 'data' (Map) and 'message' (String from script[s]).
	 */
	public static Expando launchScripts(scriptBase, scriptChain, boolean checkData, data, String type, ConfigObject config) {
		def retval = new Expando()				
		log.debug(scriptChain)
		if (scriptChain != null && scriptChain.size() > 0) {
			ScriptEngineManager manager = new ScriptEngineManager()				
			scriptChain.each {scriptConfig->
				def script = scriptConfig.keySet().toArray()[0]
				script = scriptBase  + script
				def configPath = scriptConfig[script]
				if (!checkData || data != null) {		
					retval.script = script
					def engine = manager.getEngineByExtension(FilenameUtils.getExtension(script)) 
					if (engine != null) {
						if (log.isDebugEnabled()) {
							log.debug("Preparing to execute processing script:'${script}'...")
						}
						engine.put("type", type)
						engine.put("data", data)
						engine.put("config", config)
						engine.put("log", log)
						engine.put("scriptPath", script)
						engine.put("configPath", configPath)
                        //Do not allow errors in script to fail silently.
                        try {
                            engine.eval(new FileReader(new File(script)))
                        } catch (ScriptException scriptException){
                             log.error("An error has occurred in script ${script}. Halting further processing of this record.", scriptException)
                             return
                        }
						data = engine.get("data")					
						retval.data = engine.get("data")							
						retval.message = engine.get("message")				
						if (checkData && data == null) {
							log.error("Execution of '${script}' invalidated the record. The script returned the ff. message: '${retval.message}")
						} else {
							if (log.isDebugEnabled()) {
								log.debug("Execution of '${script}' successful, message is: '${retval.message}'")
							}
						}	
					} else {
						log.error("Script type not supported: '${script}'.")
					}
				} else {
					log.error("Previous processing had failed. Halting further pre-processing of this record.")
					return
				}
			}
		} else {
		 	retval.script = "None"
		 	retval.data = data
			retval.message = "No script executed, pass through." 
		}
		return retval		
	}
}
