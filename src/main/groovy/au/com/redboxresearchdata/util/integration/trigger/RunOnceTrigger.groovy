package au.com.redboxresearchdata.util.integration.trigger

import org.springframework.scheduling.Trigger
import org.springframework.scheduling.TriggerContext
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler
import java.util.Date

/**
 * A "run once trigger" :)
 * 
 * @author Shilo Banihit
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