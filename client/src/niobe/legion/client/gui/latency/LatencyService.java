/*
 * Niobe Legion - a versatile client / server framework
 *     Copyright (C) 2013-2016 by fireandfuel (fireandfuel<at>hotmail<dot>de)
 *
 * This file (LatencyService.java) is part of Niobe Legion (module niobe-legion-client).
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
 *     along with Niobe Legion. If not, see <http://www.gnu.org/licenses/>.
 */

package niobe.legion.client.gui.latency;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.util.Duration;

/**
 * @author fireandfuel
 */
public class LatencyService extends ScheduledService<Double>
{
    static final long MAX_WAIT = 10000;
    private static LatencyService INSTANCE;

    private static final ThreadFactory THREAD_FACTORY = run -> AccessController
            .doPrivileged((PrivilegedAction<Thread>) () -> {
                final Thread th = new Thread(run, "LatencyService");
                th.setPriority(Thread.MIN_PRIORITY);
                th.setDaemon(true);
                return th;
            });
    private static final Executor EXECUTOR = Executors.newSingleThreadScheduledExecutor(THREAD_FACTORY);

    private LatencyService()
    {
        this.setPeriod(Duration.millis(MAX_WAIT));
        this.setRestartOnFailure(true);
        this.setExecutor(EXECUTOR);
        this.start();
    }

    public static LatencyService getInstance()
    {
        if(INSTANCE == null)
        {
            INSTANCE = new LatencyService();
        }

        return INSTANCE;
    }

    @Override
    protected Task<Double> createTask()
    {
        return new LatencyMeasureTask();
    }


}
