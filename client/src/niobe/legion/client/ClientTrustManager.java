/*
 * Niobe Legion - a versatile client / server framework
 *     Copyright (C) 2013-2015 by fireandfuel (fireandfuel<at>hotmail<dot>de)
 *
 * This file (ClientTrustManager.java) is part of Niobe Legion (module niobe-legion-client).
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

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.X509TrustManager;

public class ClientTrustManager implements X509TrustManager
{

    final X509TrustManager trustManager;
    X509Certificate[] chain;

    ClientTrustManager(X509TrustManager tm)
    {
        this.trustManager = tm;
    }

    public X509Certificate[] getChain()
    {
        return this.chain;
    }

    @Override
    public X509Certificate[] getAcceptedIssuers()
    {
        return this.trustManager.getAcceptedIssuers();
    }

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException
    {
        this.trustManager.checkClientTrusted(chain, authType);
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException
    {
        this.chain = chain;
        this.trustManager.checkServerTrusted(chain, authType);
    }

}
