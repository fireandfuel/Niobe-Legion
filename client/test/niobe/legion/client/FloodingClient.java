/*
 * Niobe Legion - a versatile client / server framework
 *     Copyright (C) 2013-2016 by fireandfuel (fireandfuel<at>hotmail<dot>de)
 *
 * This file (FloodingClient.java) is part of Niobe Legion (module niobe-legion-client_test).
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

package niobe.legion.client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import niobe.legion.client.communicator.FloodCommunicator;

/**
 * @author fireandfuel
 */
public class FloodingClient
{
    private static final int PORT = 5242;
    private static final String HOST = "localhost";
    private static final int SLEEP_TIME = 10;

    private static final List<FloodCommunicator> COMMUNICATORS = new ArrayList<FloodCommunicator>();
    private static boolean broken;

    public static int floodServer(int connections)
    {
        for(int index = 0; index < connections; index++)
        {
            try
            {
                if(!broken)
                {
                    Thread.sleep(SLEEP_TIME);
                    FloodCommunicator communicator = new FloodCommunicator(new Socket(InetAddress.getByName(HOST),
                                                                                      PORT));
                    COMMUNICATORS.add(communicator);
                    new Thread(communicator, "Communicator #" + index).start();
                } else
                {
                    break;
                }
            } catch(IOException | InterruptedException e)
            {
                e.printStackTrace();
                break;
            }
        }
        COMMUNICATORS.forEach(FloodCommunicator::closeConnection);
        return COMMUNICATORS.size();
    }

    public synchronized static void broken()
    {
        broken = true;
    }

    public static void main(String[] args)
    {
        System.out.println("Flooded: " + floodServer(100000));
    }
}
