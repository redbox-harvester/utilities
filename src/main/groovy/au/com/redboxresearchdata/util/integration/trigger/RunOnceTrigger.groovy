/*
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
*/
package au.com.redboxresearchdata.util.integration.trigger

import org.springframework.scheduling.Trigger
import org.springframework.scheduling.TriggerContext
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler
import java.util.Date

/**
 * A "run once trigger" :)
 * 
 * @author <a href="https://github.com/shilob" target="_blank">Shilo Banihit</a>
 * @since 1.0
 *
 */
class RunOnceTrigger implements Trigger {
	private volatile boolean initialized = false
	
	public Date nextExecutionTime(TriggerContext triggerContext) {
		if (!initialized){
            initialized = true
			return new Date(System.currentTimeMillis())
		}
		return null
	}	
}
