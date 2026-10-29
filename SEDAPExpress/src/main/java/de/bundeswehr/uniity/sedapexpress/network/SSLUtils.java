/*******************************************************************************
 * Copyright (C)2012-2026, German Federal Armed Forces - All rights reserved.
 *
 * This source code is part of the UNIITY (MESE) project.
 * Person of contact (POC): Volker Voß, volker1voss@bundeswehr.org
 *
 * Unauthorized use, modification, redistributing, copying, selling and
 * printing of this file in source and binary form including accompanying
 * materials is STRICTLY prohibited.
 *
 * This source code and it's parts is classified as OFFEN / NATO UNCLASSIFIED!
 *******************************************************************************/
package de.bundeswehr.uniity.sedapexpress.network;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSessionContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jsse.BCSSLParameters;
import org.bouncycastle.jsse.BCSSLSocket;
import org.bouncycastle.jsse.provider.BouncyCastleJsseProvider;
import org.bouncycastle.openssl.PEMDecryptorProvider;
import org.bouncycastle.openssl.PEMEncryptedKeyPair;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JceOpenSSLPKCS8DecryptorProviderBuilder;
import org.bouncycastle.openssl.jcajce.JcePEMDecryptorProviderBuilder;
import org.bouncycastle.operator.InputDecryptorProvider;
import org.bouncycastle.pkcs.PKCS8EncryptedPrivateKeyInfo;

/**
 *
 * @author Volker Voß
 *
 */
public class SSLUtils {

//  Parameter                    BSI-Oriented Recommendation
//
//  TLS Version                  Prefer TLS 1.3; TLS 1.2 is also recommended
//
//  Key Exchange                 For TLS 1.2, prefer PFS mechanisms such as ECDHE or DHE
//
//  TLS 1.2 Cipher Suites        AES-GCM with ECDHE, e.g., ECDHE-ECDSA or ECDHE-RSA
//
//  TLS 1.3 Cipher Suites        AES-GCM, particularly AES-128-GCM or AES-256-GCM
//
//  Elliptic Curves              BSI guidelines specify Brainpool curves; alternatively,
//                               NIST curves such as secp256r1 may be considered,
//                               depending on availability
//
//  Hash Functions               Modern hash functions such as SHA-256 or SHA-384,
//                               appropriate for the selected cipher suite
//
//  Certificate Validation      Use a trust store containing the designated CA certificates,
//                               with hostname verification enabled
	private static final String[] TLS_PROTOCOLS = { "TLSv1.3", "TLSv1.2" };

	// TLS 1.3
	private static final Set<String> BSI_TLS13_SUITES = Set.of("TLS_AES_128_GCM_SHA256", "TLS_AES_256_GCM_SHA384", "TLS_AES_128_CCM_SHA256");

	// TLS 1.2 mit Perfect Forward Secrecy
	private static final Set<String> BSI_TLS12_SUITES = Set.of("TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256", "TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384", "TLS_ECDHE_ECDSA_WITH_AES_128_CCM",
			"TLS_ECDHE_ECDSA_WITH_AES_256_CCM", "TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256", "TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384", "TLS_ECDHE_RSA_WITH_AES_128_CCM", "TLS_ECDHE_RSA_WITH_AES_256_CCM");

	private static final String[] TLS_CIPHER_SUITES = {
			// TLS 1.3
			"TLS_AES_128_GCM_SHA256", "TLS_AES_256_GCM_SHA384",

			// TLS 1.2 mit Perfect Forward Secrecy
			"TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256", "TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384", "TLS_ECDHE_ECDSA_WITH_AES_128_CCM", "TLS_ECDHE_ECDSA_WITH_AES_256_CCM",
			"TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256", "TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384", "TLS_ECDHE_RSA_WITH_AES_128_CCM", "TLS_ECDHE_RSA_WITH_AES_256_CCM" };

	private static final String[] TLS_NAMED_GROUPS = {
			// Brainpool für TLS 1.3
			"brainpoolP256r1tls13", "brainpoolP384r1tls13", "brainpoolP512r1tls13",

			// Für TLS 1.2 und TLS 1.3
			"secp256r1", "secp384r1", "secp521r1",

			// Brainpool für TLS 1.2
			"brainpoolP256r1", "brainpoolP384r1", "brainpoolP512r1" };

	private SSLUtils() {
		// Verstecken
	}

	/**
	 * 
	 * @param caCertificateFile
	 * @param clientCertificateFile
	 * @param clientKeyFile
	 * @param password
	 * @return
	 * @throws Exception
	 */
	public static SSLSocketFactory getSocketFactory(final String caCertificateFile, final String clientCertificateFile, final String clientKeyFile, final String password) throws Exception {

		Logger.getLogger("org.bouncycastle.jsse.provider.DisabledAlgorithmConstraints").setLevel(Level.SEVERE);

		// Register Bouncy Castle if it is not already registered.
		if (Security.getProvider("BC") == null) {
			Security.addProvider(new BouncyCastleProvider());
		}

		if (Security.getProvider("BCJSSE") == null) {
			Security.addProvider(new BouncyCastleJsseProvider());
		}

		final char[] keyPassword = password == null ? new char[0] : password.toCharArray();

		// Load trusted CA certificates.
		final List<X509Certificate> caCertificates = SSLUtils.loadCertificates(caCertificateFile);

		if (caCertificates.isEmpty()) {
			throw new IllegalArgumentException("No CA certificates found in file: " + caCertificateFile);
		}

		// Load the client certificate and any intermediate certificates.
		final List<X509Certificate> clientCertificates = SSLUtils.loadCertificates(clientCertificateFile);

		if (clientCertificates.isEmpty()) {
			throw new IllegalArgumentException("No client certificates found in file: " + clientCertificateFile);
		}

		// Load the client private key.
		final PrivateKey privateKey = SSLUtils.loadPrivateKey(clientKeyFile, keyPassword);

		// Check the validity period of the client leaf certificate.
		clientCertificates.get(0).checkValidity();

		// Create a trust store containing the configured CA certificates.
		final KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
		trustStore.load(null, null);

		int index = 0;
		for (final X509Certificate ca : caCertificates) {
			trustStore.setCertificateEntry("ca-" + index++, ca);
		}

		final TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		tmf.init(trustStore);

		// Create a key store containing the client key and certificate chain.
		final KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
		keyStore.load(null, null);

		final Certificate[] certificateChain = clientCertificates.toArray(new Certificate[0]);

		keyStore.setKeyEntry("client-key", privateKey, keyPassword, certificateChain);

		final KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
		kmf.init(keyStore, keyPassword);

		// Initialize the TLS context.
		final SSLContext context = SSLContext.getInstance("TLS", "BCJSSE");
		context.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

		// Allow TLS 1.3 and TLS 1.2 only, preferring TLS 1.3.
		return new TLS12And13SocketFactory(context.getSocketFactory());
	}

	private static List<X509Certificate> loadCertificates(final String fileName) throws Exception {

		final CertificateFactory factory = CertificateFactory.getInstance("X.509");

		try (InputStream input = Files.newInputStream(Path.of(fileName))) {
			final Collection<? extends Certificate> certificates = factory.generateCertificates(input);

			final List<X509Certificate> result = new ArrayList<>();

			for (final Certificate certificate : certificates) {
				if (!(certificate instanceof X509Certificate)) {
					throw new IllegalArgumentException("Unsupported certificate type in file: " + fileName);
				}

				result.add((X509Certificate) certificate);
			}

			return result;
		}
	}

	private static PrivateKey loadPrivateKey(final String fileName, final char[] password) throws Exception {

		final JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider("BC");

		try (Reader reader = Files.newBufferedReader(Path.of(fileName), StandardCharsets.UTF_8); PEMParser parser = new PEMParser(reader)) {

			final Object object = parser.readObject();

			if (object == null) {
				throw new IllegalArgumentException("No private key found in file: " + fileName);
			}

			if (parser.readObject() != null) {
				throw new IllegalArgumentException("Multiple PEM objects found in the private key file. " + "Exactly one private key is expected: " + fileName);
			}

			if (object instanceof PEMKeyPair) {
				return converter.getKeyPair((PEMKeyPair) object).getPrivate();
			}

			if (object instanceof PrivateKeyInfo) {
				return converter.getPrivateKey((PrivateKeyInfo) object);
			}

			if (object instanceof PEMEncryptedKeyPair) {
				final PEMDecryptorProvider decryptor = new JcePEMDecryptorProviderBuilder().setProvider("BC").build(password);

				final PEMKeyPair keyPair = ((PEMEncryptedKeyPair) object).decryptKeyPair(decryptor);

				return converter.getKeyPair(keyPair).getPrivate();
			}

			if (object instanceof PKCS8EncryptedPrivateKeyInfo) {
				final InputDecryptorProvider decryptor = new JceOpenSSLPKCS8DecryptorProviderBuilder().setProvider("BC").build(password);

				final PrivateKeyInfo keyInfo = ((PKCS8EncryptedPrivateKeyInfo) object).decryptPrivateKeyInfo(decryptor);

				return converter.getPrivateKey(keyInfo);
			}

			throw new IllegalArgumentException("Unsupported PEM private key format: " + object.getClass().getName());
		}
	}

	private static void printCheck(final String parameter, final String value, final String status, final String explanation) {

		System.out.printf(Locale.ROOT, "%-30s: %s (%s - %s)%n", parameter, value, status, explanation);
	}

	public static void printTlsSessionInfo(final SSLSocket sslSocket) {
		if (sslSocket == null) {
			System.out.println("TLS assessment: NOT POSSIBLE - Socket is null.");
			return;
		}

		System.out.println();
		System.out.println("========== TLS Security Assessment ==========");

		final SSLSession session;

		try {
			// getSession() initiates the handshake if it has not happened yet.
			session = sslSocket.getSession();
		} catch (final RuntimeException ex) {
			System.out.println("TLS assessment: NOT POSSIBLE - Unable to obtain TLS session.");
			System.out.println("Reason: " + ex.getMessage());
			System.out.println("=============================================");
			return;
		}

		final String protocol = session.getProtocol();
		final String cipherSuite = session.getCipherSuite();

		System.out.println("Provider socket: " + sslSocket.getClass().getName());
		System.out.println("Session ID available: " + ((session.getId() != null) && (session.getId().length > 0)));

		// 1. Negotiated TLS protocol
		final boolean tls13 = "TLSv1.3".equals(protocol);
		final boolean tls12 = "TLSv1.2".equals(protocol);

		SSLUtils.printCheck("TLS protocol", protocol, tls13 || tls12 ? "BSI-RECOMMENDED" : "NOT BSI-RECOMMENDED",
				tls13 ? "TLS 1.3 is recommended." : tls12 ? "TLS 1.2 is permitted by the referenced guideline." : "The negotiated protocol is outside the checked profile.");

		// 2. Cipher suite
		final boolean cipherSuiteRecommended = (tls13 && SSLUtils.BSI_TLS13_SUITES.contains(cipherSuite)) || (tls12 && SSLUtils.BSI_TLS12_SUITES.contains(cipherSuite));

		SSLUtils.printCheck("TLS cipher suite", cipherSuite, cipherSuiteRecommended ? "BSI-RECOMMENDED" : "NOT BSI-RECOMMENDED", "Assessment uses the explicitly configured allowlist in this class.");

		// 3. Record-layer encryption algorithm
		final String encryptionAlgorithm;
		if (cipherSuite.contains("_AES_")) {
			encryptionAlgorithm = "AES";
		} else if (cipherSuite.contains("CHACHA20")) {
			encryptionAlgorithm = "ChaCha20";
		} else {
			encryptionAlgorithm = "Unknown or not covered by this profile";
		}

		SSLUtils.printCheck("Encryption algorithm", encryptionAlgorithm, cipherSuiteRecommended && !encryptionAlgorithm.startsWith("Unknown") ? "BSI-RECOMMENDED" : "NOT BSI-RECOMMENDED",
				"Derived from the negotiated cipher suite.");

		// 4. Block cipher mode
		final String operationMode;
		final boolean aeadMode;

		if (cipherSuite.contains("_GCM_")) {
			operationMode = "GCM";
			aeadMode = true;
		} else if (cipherSuite.contains("_CCM")) {
			operationMode = "CCM";
			aeadMode = true;
		} else if (cipherSuite.contains("_CBC_")) {
			operationMode = "CBC";
			aeadMode = false;
		} else if (cipherSuite.contains("CHACHA20")) {
			operationMode = "ChaCha20-Poly1305 (AEAD; not a block mode)";
			aeadMode = true;
		} else {
			operationMode = "Unknown or not covered by this profile";
			aeadMode = false;
		}

		SSLUtils.printCheck("Encryption mode", operationMode, cipherSuiteRecommended && aeadMode ? "BSI-RECOMMENDED" : "NOT BSI-RECOMMENDED",
				"AEAD is required by this method's selected cipher-suite profile.");

		// 5. Hash used by the cipher suite / TLS key schedule
		final String hashAlgorithm;

		if (tls13 && cipherSuite.endsWith("_SHA256")) {
			hashAlgorithm = "SHA-256";
		} else if (tls13 && cipherSuite.endsWith("_SHA384")) {
			hashAlgorithm = "SHA-384";
		} else if (tls12 && cipherSuite.endsWith("_SHA256")) {
			hashAlgorithm = "SHA-256";
		} else if (tls12 && cipherSuite.endsWith("_SHA384")) {
			hashAlgorithm = "SHA-384";
		} else if (tls12 && cipherSuite.contains("_CCM")) {
			// TLS 1.2 CCM suites omit the hash in the suite name.
			// The TLS 1.2 PRF uses SHA-256 for these suites.
			hashAlgorithm = "SHA-256 (TLS 1.2 PRF)";
		} else {
			hashAlgorithm = "Unknown";
		}

		final boolean recommendedHash = "SHA-256".equals(hashAlgorithm) || "SHA-384".equals(hashAlgorithm) || "SHA-256 (TLS 1.2 PRF)".equals(hashAlgorithm);

		SSLUtils.printCheck("Cipher suite hash / PRF", hashAlgorithm, cipherSuiteRecommended && recommendedHash ? "BSI-RECOMMENDED" : "NOT BSI-RECOMMENDED",
				"This is not necessarily the hash used to sign the peer certificate.");

		// 6. Key exchange algorithm
		final String keyExchange;
		final String upperSuite = cipherSuite.toUpperCase(Locale.ROOT);

		if (tls13) {
			keyExchange = "Not encoded in TLS 1.3 cipher suite";
		} else if (upperSuite.startsWith("TLS_ECDHE_")) {
			keyExchange = "ECDHE";
		} else if (upperSuite.startsWith("TLS_DHE_")) {
			keyExchange = "DHE";
		} else if (upperSuite.startsWith("TLS_ECDH_")) {
			keyExchange = "ECDH";
		} else if (upperSuite.startsWith("TLS_DH_")) {
			keyExchange = "DH";
		} else {
			keyExchange = "Unknown or not covered by this profile";
		}

		if (tls13) {
			SSLUtils.printCheck("Key exchange algorithm", keyExchange, "NOT VERIFIABLE", "The cipher suite does not specify key exchange; " + "additional handshake information is required.");
		} else {
			final boolean pfsKeyExchange = "ECDHE".equals(keyExchange) || "DHE".equals(keyExchange);

			SSLUtils.printCheck("Key exchange algorithm", keyExchange, cipherSuiteRecommended && pfsKeyExchange ? "BSI-RECOMMENDED" : "NOT BSI-RECOMMENDED",
					"ECDHE/DHE provide ephemeral Diffie-Hellman key exchange.");
		}

		// 7. Perfect Forward Secrecy
		final boolean pfs;

		if (tls13) {
			// TLS 1.3 can also use PSK-only mode; the cipher suite alone
			// does not prove which handshake mode was negotiated.
			SSLUtils.printCheck("Perfect Forward Secrecy", "Not derivable from the cipher suite alone", "NOT VERIFIABLE", "The negotiated TLS 1.3 handshake mode must be inspected.");
		} else {
			pfs = "ECDHE".equals(keyExchange) || "DHE".equals(keyExchange);

			SSLUtils.printCheck("Perfect Forward Secrecy", Boolean.toString(pfs), pfs && cipherSuiteRecommended ? "BSI-RECOMMENDED" : "NOT BSI-RECOMMENDED",
					"In TLS 1.2, this check identifies ephemeral DH suites.");
		}

		// 8. Actual negotiated named group
		SSLUtils.printCheck("Negotiated DH named group", "Not obtained from SSLSession", "NOT VERIFIABLE", "Configured supported groups are not proof of the group actually used.");

		// 9. Certificate signature algorithm
		try {
			final Certificate[] peerCertificates = session.getPeerCertificates();

			if ((peerCertificates.length > 0) && peerCertificates[0] instanceof final X509Certificate peerCert) {
				final String signatureAlgorithm = peerCert.getSigAlgName();

				SSLUtils.printCheck("Peer certificate signature", signatureAlgorithm, "NOT VERIFIABLE", """
						The algorithm is reported, but its complete BSI assessment \
						requires signature parameters, key size and certificate \
						chain validation.""");
			} else {
				SSLUtils.printCheck("Peer certificate signature", "No X.509 peer certificate available", "NOT VERIFIABLE",
						"The peer may be authenticated differently or no certificate " + "could be retrieved.");
			}
		} catch (final Exception ex) {
			SSLUtils.printCheck("Peer certificate signature", "Unavailable: " + ex.getClass().getSimpleName(), "NOT VERIFIABLE", "The peer certificate could not be inspected.");
		}

		// 10. Session age and configured session cache timeout
		final long now = System.currentTimeMillis();
		final long creationTime = session.getCreationTime();
		final long sessionAgeSeconds = Math.max(0L, TimeUnit.MILLISECONDS.toSeconds(now - creationTime));

		System.out.println("Session creation time: " + new java.util.Date(creationTime));
		System.out.println("Session age at inspection: " + sessionAgeSeconds + " seconds");

		final SSLSessionContext sessionContext = session.getSessionContext();

		if (sessionContext != null) {
			final int cacheTimeoutSeconds = sessionContext.getSessionTimeout();

			SSLUtils.printCheck("Session cache timeout", cacheTimeoutSeconds + " seconds", "NOT VERIFIABLE", """
					This is the local session-cache timeout, not the negotiated \
					session-ticket lifetime. The referenced BSI guideline \
					does not make this value alone a compliance verdict.""");
		} else {
			SSLUtils.printCheck("Session cache timeout", "Not available", "NOT VERIFIABLE", "The provider does not expose an SSLSessionContext for this session.");
		}

		SSLUtils.printCheck("Actual session lifetime", "Not available through SSLSession", "NOT VERIFIABLE",
				"Ticket lifetime, server-side expiry and resumption policy require " + "additional provider/server information.");

		// 11. Socket / provider
		final boolean bcjsseSocket = sslSocket instanceof org.bouncycastle.jsse.BCSSLSocket;

		SSLUtils.printCheck("BCJSSE socket", Boolean.toString(bcjsseSocket), bcjsseSocket ? "INFO" : "NOT BSI-RECOMMENDED",
				bcjsseSocket ? "The socket exposes the BCJSSE-specific API." : "The active socket is not a BCJSSE socket.");

		System.out.println("---------------------------------------------");
		System.out.println("Overall assessment: This report evaluates only the parameters " + "explicitly checked above.");
		System.out.println("""
				A complete BSI assessment also requires verification of certificate \
				chain, hostname, key sizes, signature parameters, negotiated \
				named group and applicable policy/version requirements.""");
		System.out.println("=============================================");
	}

	public static String getEncryptionAlgorithm(final String cipherSuite) {
		if (cipherSuite.startsWith("TLS_AES_")) {
			return cipherSuite.substring(4, cipherSuite.lastIndexOf('_'));
		}

		if (cipherSuite.startsWith("TLS_CHACHA20_")) {
			return "CHACHA20";
		}

		final int withIndex = cipherSuite.indexOf("_WITH_");
		if (withIndex >= 0) {
			final String remainder = cipherSuite.substring(withIndex + 6);
			final int hashIndex = remainder.lastIndexOf('_');

			return hashIndex >= 0 ? remainder.substring(0, hashIndex) : remainder;
		}

		return "Not identifiable from cipher suite";
	}

	public static String getCipherMode(final String cipherSuite) {
		if (cipherSuite.contains("_GCM_")) {
			return "GCM";
		}
		if (cipherSuite.contains("_CCM_")) {
			return "CCM";
		}
		if (cipherSuite.contains("_CBC_")) {
			return "CBC";
		}
		if (cipherSuite.contains("CHACHA20")) {
			return "Poly1305 (AEAD)";
		}
		if (cipherSuite.contains("_CCM")) {
			return "CCM";
		}

		if (cipherSuite.startsWith("TLS_AES_")) {
			return "AEAD (TLS 1.3)";
		}

		return "Not identifiable from cipher suite";
	}

	public static String getHashAlgorithm(final String cipherSuite) {
		// In TLS 1.3, the suffix identifies the HKDF hash.
		// In TLS 1.2, it commonly identifies the PRF/MAC hash.
		if (cipherSuite.endsWith("_SHA256")) {
			return "SHA-256";
		}
		if (cipherSuite.endsWith("_SHA384")) {
			return "SHA-384";
		}
		if (cipherSuite.endsWith("_SHA512")) {
			return "SHA-512";
		}
		if (cipherSuite.endsWith("_SHA")) {
			return "SHA-1";
		}

		return "Not identifiable from cipher suite";
	}

	public static String getKeyExchangeAlgorithm(final String protocol, final String cipherSuite) {

		if ("TLSv1.3".equals(protocol)) {
			return "Not exposed by SSLSession; " + "inspect TLS handshake debug logs";
		}

		final int withIndex = cipherSuite.indexOf("_WITH_");

		if (withIndex >= 0) {
			final String prefix = cipherSuite.substring(0, withIndex);

			if (prefix.startsWith("TLS_")) {
				return prefix.substring(4);
			}

			return prefix;
		}

		return "Not identifiable from cipher suite";
	}

	public static String toHex(final byte[] bytes) {
		final StringBuilder result = new StringBuilder();

		for (final byte b : bytes) {
			result.append(String.format("%02x", b & 0xff));
		}

		return result.length() == 0 ? "(empty)" : result.toString();
	}

	public static String formatDuration(final long millis) {
		final long seconds = millis / 1000;
		final long hours = seconds / 3600;
		final long minutes = (seconds % 3600) / 60;
		final long remainingSeconds = seconds % 60;

		return String.format(Locale.ROOT, "%d h %d min %d sec", hours, minutes, remainingSeconds);
	}

	/**
	 * SSLSocketFactory that enables TLS 1.3 and TLS 1.2 only.
	 */
	public static final class TLS12And13SocketFactory extends SSLSocketFactory {

		private final SSLSocketFactory delegate;

		private volatile SSLSocket lastSocket;

		public TLS12And13SocketFactory(final SSLSocketFactory delegate) {
			this.delegate = delegate;
		}

		public SSLSocket getLastSocket() {
			return this.lastSocket;
		}

		private Socket configure(final Socket socket) throws IOException {

			if (!(socket instanceof final SSLSocket sslSocket)) {
				return socket;
			}

			if (!(sslSocket instanceof final BCSSLSocket bcSocket)) {
				throw new IOException("BCJSSE erwartet, erhalten: " + sslSocket.getClass().getName());
			}

			final BCSSLParameters params = bcSocket.getParameters();

			params.setProtocols(SSLUtils.TLS_PROTOCOLS);
			params.setCipherSuites(SSLUtils.TLS_CIPHER_SUITES);
			params.setNamedGroups(SSLUtils.TLS_NAMED_GROUPS);
			params.setUseNamedGroupsOrder(true);

			bcSocket.setParameters(params);

			this.lastSocket = (SSLSocket) socket;

			return socket;
		}

		@Override
		public String[] getDefaultCipherSuites() {
			return this.delegate.getDefaultCipherSuites();
		}

		@Override
		public String[] getSupportedCipherSuites() {
			return this.delegate.getSupportedCipherSuites();
		}

		@Override
		public Socket createSocket() throws IOException {
			return configure(this.delegate.createSocket());
		}

		@Override
		public Socket createSocket(final Socket socket, final String host, final int port, final boolean autoClose) throws IOException {
			return configure(this.delegate.createSocket(socket, host, port, autoClose));
		}

		@Override
		public Socket createSocket(final String host, final int port) throws IOException {
			return configure(this.delegate.createSocket(host, port));
		}

		@Override
		public Socket createSocket(final String host, final int port, final InetAddress localAddress, final int localPort) throws IOException {
			return configure(this.delegate.createSocket(host, port, localAddress, localPort));
		}

		@Override
		public Socket createSocket(final InetAddress host, final int port) throws IOException {
			return configure(this.delegate.createSocket(host, port));
		}

		@Override
		public Socket createSocket(final InetAddress address, final int port, final InetAddress localAddress, final int localPort) throws IOException {
			return configure(this.delegate.createSocket(address, port, localAddress, localPort));
		}
	}

}
