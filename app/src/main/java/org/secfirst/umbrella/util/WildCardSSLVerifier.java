package org.secfirst.umbrella.util;

import org.apache.http.conn.ssl.AbstractVerifier;
import org.apache.http.conn.ssl.X509HostnameVerifier;

import javax.net.ssl.SSLException;

class WildCardSSLVerifier extends AbstractVerifier {

    private final X509HostnameVerifier delegate;

    public WildCardSSLVerifier(final X509HostnameVerifier delegate) {
        this.delegate = delegate;
    }

    @Override
    public void verify(String host, String[] cns, String[] subjectAlts)
            throws SSLException {
        boolean ok = false;
        try {
            delegate.verify(host, cns, subjectAlts);
        } catch (SSLException e) {
            for (String cn : cns) {
                if (cn.startsWith("*.")) {
                    try {
                        delegate.verify(host, new String[] {
                                cn.substring(2) }, subjectAlts);
                        ok = true;
                    } catch (Exception e1) { }
                }
            }
            if(!ok) throw e;
        }
    }
}