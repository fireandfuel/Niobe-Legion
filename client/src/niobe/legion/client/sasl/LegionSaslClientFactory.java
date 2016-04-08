/*
 * Niobe Legion - a versatile client / server framework
 *     Copyright (C) 2013-2016 by fireandfuel (fireandfuel<at>hotmail<dot>de)
 *
 * This file (LegionSaslClientFactory.java) is part of Niobe Legion (module niobe-legion-client).
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

package niobe.legion.client.sasl;

import java.util.Map;
import javax.security.auth.callback.CallbackHandler;
import javax.security.sasl.SaslClient;
import javax.security.sasl.SaslClientFactory;
import javax.security.sasl.SaslException;
import niobe.legion.shared.sasl.LegionSaslBaseFactory;

public class LegionSaslClientFactory extends LegionSaslBaseFactory implements SaslClientFactory
{

    @Override
    public SaslClient createSaslClient(String[] mechanisms, String authorizationId, String protocol, String serverName,
                                       Map<String, ?> props, CallbackHandler callbackHandler) throws SaslException
    {
        for(String mechanism : mechanisms)
        {
            switch(mechanism)
            {
                case "SCRAM-MD5":
                    return new ScramClient("MD5", authorizationId, protocol, serverName, props, callbackHandler);
                case "SCRAM-SHA-1":
                    return new ScramClient("SHA-1", authorizationId, protocol, serverName, props, callbackHandler);
                case "SCRAM-SHA-224":
                    return new ScramClient("SHA-224", authorizationId, protocol, serverName, props, callbackHandler);
                case "SCRAM-SHA-256":
                    return new ScramClient("SHA-256", authorizationId, protocol, serverName, props, callbackHandler);
                case "SCRAM-SHA-384":
                    return new ScramClient("SHA-384", authorizationId, protocol, serverName, props, callbackHandler);
                case "SCRAM-SHA-512":
                    return new ScramClient("SHA-512", authorizationId, protocol, serverName, props, callbackHandler);
                case "SCRAM-KECCAK-224":
                    return new ScramClient("KECCAK-224", authorizationId, protocol, serverName, props, callbackHandler);
                case "SCRAM-KECCAK-256":
                    return new ScramClient("KECCAK-256", authorizationId, protocol, serverName, props, callbackHandler);
                case "SCRAM-KECCAK-288":
                    return new ScramClient("KECCAK-288", authorizationId, protocol, serverName, props, callbackHandler);
                case "SCRAM-KECCAK-384":
                    return new ScramClient("KECCAK-384", authorizationId, protocol, serverName, props, callbackHandler);
                case "SCRAM-KECCAK-512":
                    return new ScramClient("KECCAK-512", authorizationId, protocol, serverName, props, callbackHandler);
                case "SCRAM-RipeMD128":
                    return new ScramClient("RipeMD128", authorizationId, protocol, serverName, props, callbackHandler);
                case "SCRAM-RipeMD160":
                    return new ScramClient("RipeMD160", authorizationId, protocol, serverName, props, callbackHandler);
                case "SCRAM-Whirlpool":
                    return new ScramClient("Whirlpool", authorizationId, protocol, serverName, props, callbackHandler);
                case "ANONYMOUS":
                    return new AnonymousSaslClient();

            }
        }
        return null;
    }
}
