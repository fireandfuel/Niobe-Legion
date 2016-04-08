/*
 * Niobe Legion - a versatile client / server framework
 *     Copyright (C) 2013-2016 by fireandfuel (fireandfuel<at>hotmail<dot>de)
 *
 * This file (LegionSaslServerFactory.java) is part of Niobe Legion (module niobe-legion-server).
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

import java.util.Map;
import javax.security.auth.callback.CallbackHandler;
import javax.security.sasl.SaslException;
import javax.security.sasl.SaslServer;
import javax.security.sasl.SaslServerFactory;
import niobe.legion.shared.sasl.LegionSaslBaseFactory;

public class LegionSaslServerFactory extends LegionSaslBaseFactory implements SaslServerFactory
{
    @Override
    public SaslServer createSaslServer(String mechanism, String protocol, String serverName, Map<String, ?> props,
                                       CallbackHandler callbackHandler) throws SaslException
    {
        switch(mechanism)
        {
            case "SCRAM-MD5":
                return new ScramServer("MD5", props, callbackHandler);
            case "SCRAM-SHA-1":
                return new ScramServer("SHA-1", props, callbackHandler);
            case "SCRAM-SHA-224":
                return new ScramServer("SHA-224", props, callbackHandler);
            case "SCRAM-SHA-256":
                return new ScramServer("SHA-256", props, callbackHandler);
            case "SCRAM-SHA-384":
                return new ScramServer("SHA-384", props, callbackHandler);
            case "SCRAM-SHA-512":
                return new ScramServer("SHA-512", props, callbackHandler);
            case "SCRAM-KECCAK-224":
                return new ScramServer("KECCAK-224", props, callbackHandler);
            case "SCRAM-KECCAK-256":
                return new ScramServer("KECCAK-256", props, callbackHandler);
            case "SCRAM-KECCAK-288":
                return new ScramServer("KECCAK-288", props, callbackHandler);
            case "SCRAM-KECCAK-384":
                return new ScramServer("KECCAK-384", props, callbackHandler);
            case "SCRAM-KECCAK-512":
                return new ScramServer("KECCAK-512", props, callbackHandler);
            case "SCRAM-RipeMD128":
                return new ScramServer("RipeMD128", props, callbackHandler);
            case "SCRAM-RipeMD160":
                return new ScramServer("RipeMD160", props, callbackHandler);
            case "SCRAM-Whirlpool":
                return new ScramServer("Whirlpool", props, callbackHandler);
            case "ANONYMOUS":
                return new AnonymousSaslServer();
            case "PLAIN":
                return new PlainSaslServer(callbackHandler);
        }
        return null;
    }
}
