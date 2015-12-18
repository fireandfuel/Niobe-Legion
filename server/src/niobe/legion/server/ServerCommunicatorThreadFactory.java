/*
 * Niobe Legion - a versatile client / server framework
 *     Copyright (C) 2013-2015 by fireandfuel (fireandfuel<at>hotmail<dot>de)
 *
 * This file (ServerCommunicatorThreadFactory.java) is part of Niobe Legion (module niobe-legion-server).
 *
 *     Niobe Legion is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Niobe Legion is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with Niobe Legion.  If not, see <http://www.gnu.org/licenses/>.
 */

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

        if(thread.isDaemon())
        {
            thread.setDaemon(false);
        }
        if(thread.getPriority() != Thread.NORM_PRIORITY)
        {
            thread.setPriority(Thread.NORM_PRIORITY);
        }
        return thread;
    }
}
