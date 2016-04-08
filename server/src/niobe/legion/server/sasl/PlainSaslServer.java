/*
 * Niobe Legion - a versatile client / server framework
 *     Copyright (C) 2013-2016 by fireandfuel (fireandfuel<at>hotmail<dot>de)
 *
 * This file (PlainSaslServer.java) is part of Niobe Legion (module niobe-legion-server).
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

package niobe.legion.server.sasl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.sasl.SaslException;
import javax.security.sasl.SaslServer;

public class PlainSaslServer implements SaslServer
{
    boolean completed = false;
    String authorizationId = null;
    CallbackHandler cbh;

    public PlainSaslServer(CallbackHandler cbh)
    {
        this.cbh = cbh;
    }

    @Override
    public String getMechanismName()
    {
        return "PLAIN";
    }

    @Override
    public byte[] evaluateResponse(byte[] response) throws SaslException
    {
        String authnID = null;

        int start = 0;
        int end = 0;
        int elementIdx = 0;

        try
        {

            for(byte b : response)
            {
                if(b == '\u0000')
                {
                    // empty string, only authzid allows this
                    if(end - start == 0)
                    {
                        if(elementIdx != 0)
                        {
                            throw new SaslException("null auth data");
                        }

                    } // data, wa-hoo
                    else
                    {
                        String element = new String(response, start, end - start, "UTF-8");
                        start = end + 1;

                        switch(elementIdx)
                        {
                            case 0:
                                this.authorizationId = element;
                                break;
                            case 1:
                                authnID = element;
                                break;
                            default:
                                throw new SaslException("Unexpected data in packet");
                        }
                    }
                    elementIdx++;

                }
                end++;
            }

            if(start == end)
            {
                throw new SaslException("null auth data");
            }

            char[] password = new String(response, start, end - start, "UTF-8").toCharArray();

            String userPrompt = this.getMechanismName() + " authentication id: ";
            String passwdPrompt = this.getMechanismName() + " password: ";

            NameCallback ncb = this.authorizationId == null ? new NameCallback(userPrompt) : new NameCallback(userPrompt,
                                                                                                              this.authorizationId);

            ncb.setName(authnID);

            PasswordCallback pcb = new PasswordCallback(passwdPrompt, false);

            try
            {
                this.cbh.handle(new Callback[]{ncb, pcb});

                if(pcb.getPassword() != null && Arrays.equals(password, pcb.getPassword()))
                {
                    this.completed = true;
                    return new byte[0];
                } else
                {
                    throw new SaslException("not authorized");
                }
            } catch(IOException e)
            {
                throw new SaslException("io error");
            } catch(UnsupportedCallbackException e)
            {
                throw new SaslException("unsupported callback");
            } finally
            {
                pcb.clearPassword();
            }

        } catch(UnsupportedEncodingException e)
        {
            throw new SaslException("utf-8 encoding");
        }
    }

    @Override
    public boolean isComplete()
    {
        return this.completed;
    }

    @Override
    public String getAuthorizationID()
    {
        if(!this.completed)
        {
            throw new IllegalStateException("not complete");
        }
        return this.authorizationId;
    }

    @Override
    public final byte[] unwrap(byte[] incoming, int offset, int len) throws SaslException
    {
        throw new SaslException("Unwrap not supported");
    }

    @Override
    public final byte[] wrap(byte[] outgoing, int offset, int len) throws SaslException
    {
        throw new SaslException("Wrap not supported");
    }

    @Override
    public Object getNegotiatedProperty(String propName)
    {
        return null;
    }

    @Override
    public void dispose() throws SaslException
    {
        this.authorizationId = null;
    }

}
