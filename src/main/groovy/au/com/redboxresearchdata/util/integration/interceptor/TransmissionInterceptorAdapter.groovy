package au.com.redboxresearchdata.util.integration.interceptor

import java.util.concurrent.atomic.AtomicInteger
import org.apache.log4j.Logger
import org.springframework.integration.Message
import org.springframework.integration.MessageChannel
import org.springframework.integration.channel.interceptor.ChannelInterceptorAdapter
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware
import org.springframework.context.support.AbstractApplicationContext

/**
 * Interceptor that is aware of message transmission, reacting to events by either processing the event or sends a message to the target channel.
 * 
 * Default processing is to shutdown the application context when the buffer is empty.
 * 
 * TODO: add event processing
 * 
 * @author Shilo Banihit
 * @since 1.0
 *
 */
class TransmissionInterceptorAdapter extends ChannelInterceptorAdapter implements ApplicationContextAware  {
	static final Logger log = Logger.getLogger(ChannelInterceptorAdapter.class)	 
	AbstractApplicationContext applicationContext
	/**
	 * Whether to count messages while presending or rely on manual message count increments. Default is true
	 */
	boolean shouldCountPresending = true
	/**
	 * Whether to shutdown the context after all messages are sent. Default is true.
	 */
	boolean shutdownContext = true
	/**
	 * Whether to send an event: when all messages are sent. Default is false.
	 */
	boolean sendCompletionMessage = false
	 
	final AtomicInteger sendCount = new AtomicInteger()
	 
	void postSend(Message<?> message, MessageChannel channel, boolean sent) {
		def curCount = sendCount.decrementAndGet()
		if (curCount == 0) {
			if (shutdownContext) {
				log.info("All messages sent, shutting down...")
				applicationContext.close()
			} 
		} else {
			if (log.isDebugEnabled()) {
				log.debug("Sending messages, pending messages:${curCount}")
			}
		}
	 }	 

	Message<?> preSend(Message<?> message, MessageChannel channel) {
		 if (shouldCountPresending) { 
		 	sendCount.incrementAndGet()
		 }
		 return message
	}
	 
	@Override
	public void setApplicationContext(ApplicationContext arg0)
			throws BeansException {
			applicationContext = (AbstractApplicationContext) arg0
	}
			
	public void incMessageCount() {
		sendCount.incrementAndGet()
	}
}
