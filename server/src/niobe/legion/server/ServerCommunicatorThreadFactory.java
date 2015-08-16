package niobe.legion.server;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class ServerCommunicatorThreadFactory implements ThreadFactory
{

	private final ThreadGroup group;
	private final AtomicInteger threadNumber = new AtomicInteger(1);
	private final String namePrefix;

	public ServerCommunicatorThreadFactory()
	{
		SecurityManager s = System.getSecurityManager();
		group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
		namePrefix = "ServerCommunicator#";
	}

	@Override
	public Thread newThread(Runnable communicator)
	{
		Thread thread = new Thread(group, communicator, namePrefix + threadNumber.getAndIncrement(), 0);

		if (thread.isDaemon())
		{
			thread.setDaemon(false);
		}
		if (thread.getPriority() != Thread.NORM_PRIORITY)
		{
			thread.setPriority(Thread.NORM_PRIORITY);
		}
		return thread;
	}
}
