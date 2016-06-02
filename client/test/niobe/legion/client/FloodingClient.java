/*
 * Niobe Legion - a versatile client / server framework
 *     Copyright (C) 2013-2016 by fireandfuel (fireandfuel<at>hotmail<dot>de)
 *
 * This file (FloodingClient.java) is part of Niobe Legion (module niobe-legion-client).
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

/**
 * @author fireandfuel
 */
public class FloodingClient
{
    private static final int PORT = 5242;
    private static final String HOST = "localhost";

    public static void floodServer(int connections)
    {
        for(int index = 0; index < connections; index++)
        {
            try
            {
                Thread.sleep(10);
                new Thread(new FloodCommunicator(new Socket(InetAddress.getByName(HOST), PORT))).start();
            } catch(IOException | InterruptedException e)
            {
                e.printStackTrace();
                System.out.println("broken after " + index + " connections");
                break;
            }
        }
    }

    public static void main(String[] args)
    {
        floodServer(1000);
    }
}
