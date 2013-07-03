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
package au.com.redboxresearchdata.util.integration.trigger

import java.util.Date

import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;

/**
 * Blocks the poller until its unblock method is called.
 * 
 * @author Shilo Banihit
 *
 */
class BlockingTrigger implements Trigger {

	private volatile boolean block = true
	
	public Date nextExecutionTime(TriggerContext arg0) {
		block = true	
		synchronized(this) {
			while (block) {
				this.wait()
			}	
		}
		return new Date(System.currentTimeMillis())
	}
	
	public void unblockTrigger() {
		block = false
		this.notifyAll()
	}
}
