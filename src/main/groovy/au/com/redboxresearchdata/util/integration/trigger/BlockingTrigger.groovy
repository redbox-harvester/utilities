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
