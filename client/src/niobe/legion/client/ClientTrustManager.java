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
