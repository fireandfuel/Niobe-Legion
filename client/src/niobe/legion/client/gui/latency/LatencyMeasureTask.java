/*
 * Niobe Legion - a versatile client / server framework
 *     Copyright (C) 2013-2016 by fireandfuel (fireandfuel<at>hotmail<dot>de)
 *
 * This file (LatencyMeasureTask.java) is part of Niobe Legion (module niobe-legion-client).
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

import javafx.concurrent.Task;
import niobe.legion.client.Client;

/**
 * @author fireandfuel
 */
class LatencyMeasureTask extends Task<Double>
{
    @Override
    protected Double call() throws Exception
    {
        long startTime = System.nanoTime();
        synchronized(this)
        {
            Client.getCommunicator().ping(this);
            this.wait(LatencyService.MAX_WAIT);
        }

        return (System.nanoTime() - startTime) / 1000000D;
    }
}
