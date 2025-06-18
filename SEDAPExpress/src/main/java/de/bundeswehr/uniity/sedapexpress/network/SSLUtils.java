/**
 * Note: This license has also been called the “Simplified BSD License” and the “FreeBSD License”.
 *
 * Copyright 2024-2025 UNIITY POC: Volker Voß, Federal Armed Forces of Germany
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of
 * conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other materials provided with
 * the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS “AS IS” AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSEnARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGE.
 */
package de.bundeswehr.uniity.sedapexpress.network;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;

/**
 * Some useful SSL/TLS functions
 * 
 * @author Volker Voß
 *
 */
public class SSLUtils {

    protected static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    static {
	SSLUtils.logger.setLevel(Level.ALL);
    }

    private SSLUtils() {
	// Verstecken
    }

    public static SSLSocketFactory getSocketFactory(final InputStream caCertificateInputStream,
	    final InputStream clientCertificateInputStream, final InputStream clientKeyInputStream, final String password)
	    throws Exception {
	Security.addProvider(new BouncyCastleProvider());

	// CA Zertifikat laden
	X509Certificate caCert = null;

	BufferedInputStream bis = new BufferedInputStream(caCertificateInputStream);
	final CertificateFactory cf = CertificateFactory.getInstance("X.509");

	while (bis.available() > 0) {
	    caCert = (X509Certificate) cf.generateCertificate(bis);
	}

	// Client Zertifikat laden
	bis = new BufferedInputStream(clientCertificateInputStream);
	X509Certificate cert = null;
	while (bis.available() > 0) {
	    cert = (X509Certificate) cf.generateCertificate(bis);
	}

	// Client PrivateKey laden
	final PEMParser pemParser = new PEMParser(new InputStreamReader(clientKeyInputStream));
	final JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider("BC");

	final Object keyObject = pemParser.readObject();

	final PrivateKey key;
	if (keyObject instanceof PEMKeyPair) {
	    key = converter.getPrivateKey(((PEMKeyPair) keyObject).getPrivateKeyInfo());

	} else if (keyObject instanceof PrivateKeyInfo) {
	    key = converter.getPrivateKey((PrivateKeyInfo) keyObject);

	} else {
	    SSLUtils.logger.logp(Level.INFO, "SSLUtils", "getSocketFactory()", "Could not process keyfiles... unknown type, please report that to the UNIITY team!");
	    pemParser.close();
	    return null;
	}
	pemParser.close();

	// CA certificate is used to authenticate server
	final KeyStore caKs = KeyStore.getInstance(KeyStore.getDefaultType());
	caKs.load(null, null);
	caKs.setCertificateEntry("ca-certificate", caCert);
	final TrustManagerFactory tmf = TrustManagerFactory.getInstance("X509");
	tmf.init(caKs);

	// client key and certificates are sent to server so it can authenticate
	final KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
	ks.load(null, null);
	ks.setCertificateEntry("certificate", cert);
	ks.setKeyEntry("private-key", key, password.toCharArray(),
		new java.security.cert.Certificate[] { cert });
	final KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory
		.getDefaultAlgorithm());
	kmf.init(ks, password.toCharArray());

	// finally, create SSL socket factory
	final SSLContext context = SSLContext.getInstance("TLSv1.2");
	context.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

	return context.getSocketFactory();
    }

}
