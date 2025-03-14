/**
 * Note: This license has also been called the “Simplified BSD License” and the “FreeBSD License”.
 *
 * Copyright 2024 MESE POC: Volker Voß, Federal Armed Forces of Germany
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
 * CONTRIBUTORS BEn LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGE.
 */
package de.bundeswehr.mese.sedapexpress.messages;

import java.math.BigInteger;
import java.security.PublicKey;
import java.util.HexFormat;
import java.util.Iterator;
import java.util.logging.Level;

import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.jcajce.provider.asymmetric.dh.BCDHPublicKey;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.DecoderException;

public class KEYEXCHANGE extends SEDAPExpressMessage {

    private static final long serialVersionUID = -6681575300891302102L;

    public enum AlgorithmType {

	Diffie_Hellman_Merkle(0), Diffie_Hellman_Merkle_with_Curve25519(1), Kyber512(2), Kyber768(3), Kyber1024(4), FrodoKEM640(5), FrodoKEM976(6), FrodoKEM1344(7);

	int algorithm;

	public int getAlgorithmTypeValue() {

	    return this.algorithm;
	}

	AlgorithmType(int algorithm) {
	    this.algorithm = algorithm;
	}

	public static AlgorithmType valueOfAlgorithmType(int algorithm) {

	    return switch (algorithm) {
	    case 0 -> Diffie_Hellman_Merkle;
	    case 1 -> Diffie_Hellman_Merkle_with_Curve25519;
	    case 2 -> Kyber512;
	    case 3 -> Kyber768;
	    case 4 -> Kyber1024;
	    case 5 -> FrodoKEM640;
	    case 6 -> FrodoKEM976;
	    case 7 -> FrodoKEM1344;
	    default -> Diffie_Hellman_Merkle;
	    };
	}

	@Override
	public String toString() {
	    return String.valueOf(this.algorithm);
	}

    }

    private String recipient;

    private AlgorithmType algorithmType;
    private Integer phase;

    private Integer keyLengthSharedSecret;
    private Integer keyLengthDHKEM;

    private BigInteger primeNumber;
    private BigInteger naturalNumber;
    private Long iv;

    private PublicKey publicKey;

    public String getRecipient() {
	return this.recipient;
    }

    public void setRecipient(String recipient) {
	this.recipient = recipient;
    }

    public AlgorithmType getAlgorithmType() {
	return this.algorithmType;
    }

    public void setAlgorithmType(AlgorithmType algorithmType) {
	this.algorithmType = algorithmType;
    }

    public Integer getPhase() {
	return this.phase;
    }

    public void setPhase(Integer phase) {
	this.phase = phase;
    }

    public Integer getKeyLengthSharedSecret() {
	return this.keyLengthSharedSecret;
    }

    public void setKeyLengthSharedSecret(Integer keyLengthSharedSecret) {
	this.keyLengthSharedSecret = keyLengthSharedSecret;
    }

    public Integer getKeyLengthDHKEM() {
	return this.keyLengthDHKEM;
    }

    public void setKeyLengthDHKEM(Integer keyLengthDHKEM) {
	this.keyLengthDHKEM = keyLengthDHKEM;
    }

    public BigInteger getPrimeNumber() {
	return this.primeNumber;
    }

    public void setPrimeNumber(BigInteger primeNumber) {
	this.primeNumber = primeNumber;
    }

    public BigInteger getNaturalNumber() {
	return this.naturalNumber;
    }

    public void setNaturalNumber(BigInteger naturalNumber) {
	this.naturalNumber = naturalNumber;
    }

    public Long getIV() {
	return this.iv;
    }

    public void setIV(Long iv) {
	this.iv = iv;
    }

    public PublicKey getPublicKey() {
	return this.publicKey;
    }

    public void setPublicKey(PublicKey publicKey) {
	this.publicKey = publicKey;
    }

    /**
     * 
     * @param number
     * @param time
     * @param sender
     * @param classification
     * @param acknowledgement
     * @param mac
     * @param recipient
     * @param algorithmType
     * @param phase
     * @param keyLengthSharedSecret
     * @param keyLengthDHKEM
     * @param primeNumber
     * @param naturalNumber
     * @param iv
     * @param publicKey
     */
    public KEYEXCHANGE(Short number, Long time, String sender, Classification classification, Acknowledgement acknowledgement, String mac, String recipient,
	    AlgorithmType algorithmType, Integer phase, Integer keyLengthSharedSecret, Integer keyLengthDHKEM,
	    BigInteger primeNumber, BigInteger naturalNumber, Long iv, PublicKey publicKey) {
	super(number, time, sender, classification, acknowledgement, mac);
	this.recipient = recipient;
	this.algorithmType = algorithmType;
	this.phase = phase;
	this.keyLengthSharedSecret = keyLengthSharedSecret;
	this.keyLengthDHKEM = keyLengthDHKEM;
	this.primeNumber = primeNumber;
	this.naturalNumber = naturalNumber;
	this.iv = iv;
	this.publicKey = publicKey;
    }

    /**
     *
     * @param message
     */
    public KEYEXCHANGE(String message) {

	this(SEDAPExpressMessage.splitMessage(message.substring(message.indexOf(';') + 1)).iterator());
    }

    /**
     *
     * @param message
     */
    public KEYEXCHANGE(Iterator<String> message) {

	super(message);

	String value;

	// Recipient
	if (message.hasNext()) {
	    value = message.next();
	    if (value.isBlank()) {
		SEDAPExpressMessage.logger.logp(Level.INFO, "KEYEXCHANGE", "KEYEXCHANGE(Iterator<String> message)", "Optional field \"recipient\" is empty!");
	    } else {
		this.recipient = value;
	    }
	} else {
	    SEDAPExpressMessage.logger.logp(Level.SEVERE, "KEYEXCHANGE", "KEYEXCHANGE(Iterator<String> message)", "Incomplete message!");
	}

	// Algorithm
	if (message.hasNext()) {
	    value = message.next();
	    if ("0".equals(value) || "1".equals(value) || "2".equals(value) || "3".equals(value) || "4".equals(value)) {
		this.algorithmType = AlgorithmType.valueOfAlgorithmType(Integer.parseInt(value));
	    } else if (value.isBlank()) {
		SEDAPExpressMessage.logger.logp(Level.SEVERE, "KEYEXCHANGE", "KEYEXCHANGE(Iterator<String> message)", "Mandatory field \"Algorithm\" contains not a valid number!", value);
	    }
	} else {
	    SEDAPExpressMessage.logger.logp(Level.SEVERE, "KEYEXCHANGE", "KEYEXCHANGE(Iterator<String> message)", "Incomplete message!");
	}

	// Phase
	if (message.hasNext()) {
	    value = message.next();
	    if ("0".equals(value) || "1".equals(value) || "2".equals(value) || "3".equals(value)) {
		this.phase = Integer.parseInt(value);
	    } else if (value.isBlank()) {
		SEDAPExpressMessage.logger.logp(Level.SEVERE, "KEYEXCHANGE", "KEYEXCHANGE(Iterator<String> message)", "Mandatory field \"Phase\" contains not a valid number!", value);
	    }
	} else {
	    SEDAPExpressMessage.logger.logp(Level.SEVERE, "KEYEXCHANGE", "KEYEXCHANGE(Iterator<String> message)", "Incomplete message!");
	}

	// KeyLengthSharedSecret
	if (message.hasNext()) {
	    value = message.next();
	    if ("128".equals(value) || "256".equals(value)) {
		this.keyLengthSharedSecret = Integer.parseInt(value);
	    } else if (!value.isBlank() && (this.phase == 0)) {
		SEDAPExpressMessage.logger.logp(Level.SEVERE, "KEYEXCHANGE", "KEYEXCHANGE(Iterator<String> message)", "Mandatory (phase 0) field \"KeyLengthSharedSecret\" contains not a valid number!", value);
	    } else if (!value.isBlank()) {
		SEDAPExpressMessage.logger.logp(Level.WARNING, "KEYEXCHANGE", "KEYEXCHANGE(Iterator<String> message)", "Optional (not in phase 0) field \"KeyLengthSharedSecret\" contains not a valid number!", value);
	    } else if (this.phase == 0) {
		SEDAPExpressMessage.logger.logp(Level.SEVERE, "KEYEXCHANGE", "KEYEXCHANGE(Iterator<String> message)", "Mandatory (phase 0) field \"KeyLengthSharedSecret\" is empty!");
	    } else {
		SEDAPExpressMessage.logger.logp(Level.SEVERE, "KEYEXCHANGE", "KEYEXCHANGE(Iterator<String> message)", "Optional (not in phase 0) field \"KeyLengthSharedSecret\" is empty!");
	    }
	} else {
	    SEDAPExpressMessage.logger.logp(Level.SEVERE, "KEYEXCHANGE", "KEYEXCHANGE(Iterator<String> message)", "Incomplete message!");
	}

	// KeyLengthDHKEM
	if (message.hasNext()) {
	    value = message.next();
	    if ("1024".equals(value) || "2048".equals(value) || "4096".equals(value)) {
		this.keyLengthSharedSecret = Integer.parseInt(value);
	    } else if (!value.isBlank() && (this.phase == 0)) {
		SEDAPExpressMessage.logger.logp(Level.SEVERE, "KEYEXCHANGE", "KEYEXCHANGE(Iterator<String> message)", "Mandatory (phase 0) field \"KeyLengthDHKEM\" contains not a valid number!", value);
	    } else if (!value.isBlank()) {
		SEDAPExpressMessage.logger.logp(Level.WARNING, "KEYEXCHANGE", "KEYEXCHANGE(Iterator<String> message)", "Optional (not in phase 0) field \"KeyLengthDHKEM\" contains not a valid number!", value);
	    } else if (this.phase == 0) {
		SEDAPExpressMessage.logger.logp(Level.SEVERE, "KEYEXCHANGE", "KEYEXCHANGE(Iterator<String> message)", "Mandatory (phase 0) field \"KeyLengthDHKEM\" is empty!");
	    } else {
		SEDAPExpressMessage.logger.logp(Level.SEVERE, "KEYEXCHANGE", "KEYEXCHANGE(Iterator<String> message)", "Optional (not in phase 0) field \"KeyLengthDHKEM\" is empty!");
	    }
	} else {
	    SEDAPExpressMessage.logger.logp(Level.SEVERE, "KEYEXCHANGE", "KEYEXCHANGE(Iterator<String> message)", "Incomplete message!");
	}

	// PrimeNumber
	if (message.hasNext()) {
	    value = message.next();
	    if (SEDAPExpressMessage.matchesPattern(SEDAPExpressMessage.BIGINTEGER_MATCHER, value)) {
		this.primeNumber = new BigInteger(value, 16);
	    } else if (!value.isBlank() && (this.phase == 0)) {
		SEDAPExpressMessage.logger.logp(Level.SEVERE, "KEYEXCHANGE", "KEYEXCHANGE(Iterator<String> message)", "Mandatory (phase 0) field \"PrimeNumber\" contains not a valid number!", value);
	    } else if (!value.isBlank()) {
		SEDAPExpressMessage.logger.logp(Level.WARNING, "KEYEXCHANGE", "KEYEXCHANGE(Iterator<String> message)", "Optional (not in phase 0) field \"PrimeNumber\" contains not a valid number!", value);
	    } else if (this.phase == 0) {
		SEDAPExpressMessage.logger.logp(Level.SEVERE, "KEYEXCHANGE", "KEYEXCHANGE(Iterator<String> message)", "Mandatory (phase 0) field \"PrimeNumber\" is empty!");
	    } else {
		SEDAPExpressMessage.logger.logp(Level.INFO, "KEYEXCHANGE", "KEYEXCHANGE(Iterator<String> message)", "Optional (not in phase 0) field \"PrimeNumber\" is empty!");
	    }
	} else {
	    SEDAPExpressMessage.logger.logp(Level.SEVERE, "KEYEXCHANGE", "KEYEXCHANGE(Iterator<String> message)", "Incomplete message!");
	}

	// NaturalNumber
	if (message.hasNext()) {
	    value = message.next();
	    if (SEDAPExpressMessage.matchesPattern(SEDAPExpressMessage.BIGINTEGER_MATCHER, value)) {
		this.naturalNumber = new BigInteger(value, 16);
	    } else if (!value.isBlank() && (this.phase == 0)) {
		SEDAPExpressMessage.logger.logp(Level.SEVERE, "KEYEXCHANGE", "KEYEXCHANGE(Iterator<String> message)", "Mandatory (phase 0) field \"NaturalNumber\" contains not a valid number!", value);
	    } else if (!value.isBlank()) {
		SEDAPExpressMessage.logger.logp(Level.WARNING, "KEYEXCHANGE", "KEYEXCHANGE(Iterator<String> message)", "Optional (not in phase 0) field \"NaturalNumber\" contains not a valid number!", value);
	    } else if (this.phase == 0) {
		SEDAPExpressMessage.logger.logp(Level.SEVERE, "KEYEXCHANGE", "KEYEXCHANGE(Iterator<String> message)", "Mandatory (phase 0) field \"NaturalNumber\" is empty!");
	    } else {
		SEDAPExpressMessage.logger.logp(Level.INFO, "KEYEXCHANGE", "KEYEXCHANGE(Iterator<String> message)", "Optional (not in phase 0) field \"NaturalNumber\" is empty!");
	    }
	} else {
	    SEDAPExpressMessage.logger.logp(Level.SEVERE, "KEYEXCHANGE", "KEYEXCHANGE(Iterator<String> message)", "Incomplete message!");
	}

	// IV
	if (message.hasNext()) {
	    value = message.next();
	    if (SEDAPExpressMessage.matchesPattern(SEDAPExpressMessage.HEXNUMBER_MATCHER, value)) {

		if (this.phase == 0)
		    this.iv = HexFormat.fromHexDigitsToLong(value);
		else
		    SEDAPExpressMessage.logger.logp(Level.SEVERE, "KEYEXCHANGE", "KEYEXCHANGE(Iterator<String> message)", "Optional field \"IV\" contains a valid value, but phase is not 0!", value);
	    } else if (!value.isBlank() && (this.phase == 0)) {
		SEDAPExpressMessage.logger.logp(Level.SEVERE, "KEYEXCHANGE", "KEYEXCHANGE(Iterator<String> message)", "Optional (phase 0) field \"IV\" contains not a valid value!", value);
	    } else if (value.isBlank() && (this.phase == 0)) {
		SEDAPExpressMessage.logger.logp(Level.INFO, "KEYEXCHANGE", "KEYEXCHANGE(Iterator<String> message)", "Optional (phase 0) field \"IV\" is empty!", value);
	    }
	} else {
	    SEDAPExpressMessage.logger.logp(Level.SEVERE, "KEYEXCHANGE", "KEYEXCHANGE(Iterator<String> message)", "Incomplete message!");
	}

	// PublicKey
	if (message.hasNext()) {
	    value = message.next();
	    if (value.isBlank() && (this.phase == 1)) {
		SEDAPExpressMessage.logger.logp(Level.SEVERE, "KEYEXCHANGE", "KEYEXCHANGE(Iterator<String> message)", "Mandatory (phase 1) field \"PublicKey\" is empty!");

	    } else if (value.isBlank()) {
		SEDAPExpressMessage.logger.logp(Level.INFO, "KEYEXCHANGE", "KEYEXCHANGE(Iterator<String> message)", "Optional (not phase 1) field \"PublicKey\" is empty!");

	    } else {
		try {
		    this.publicKey = new BCDHPublicKey(SubjectPublicKeyInfo.getInstance(ASN1Sequence.getInstance(Base64.decode(value))));
		} catch (DecoderException e) {

		    if (this.phase == 1) {
			SEDAPExpressMessage.logger.logp(Level.SEVERE, "KEYEXCHANGE", "KEYEXCHANGE(Iterator<String> message)", "Madatory (phase 1) field \"PublicKey\" could not be decoded from Base64!");
		    } else {
			SEDAPExpressMessage.logger.logp(Level.WARNING, "KEYEXCHANGE", "KEYEXCHANGE(Iterator<String> message)", "Optional (not in phase 1) field \"PublicKey\" could not be decoded from Base64!");
		    }
		}
	    }
	}

    }

    @Override
    public boolean equals(Object obj) {
	if (obj == null) {
	    return false;
	} else if (!(obj instanceof KEYEXCHANGE)) {
	    return false;
	} else {
	    return (super.equals(obj) &&

		    (this.algorithmType == ((KEYEXCHANGE) obj).algorithmType) && (this.phase == ((KEYEXCHANGE) obj).phase) &&

		    (this.keyLengthSharedSecret == ((KEYEXCHANGE) obj).keyLengthSharedSecret) &&
		    (this.keyLengthDHKEM == ((KEYEXCHANGE) obj).keyLengthDHKEM) &&

		    (this.primeNumber == ((KEYEXCHANGE) obj).primeNumber) && (this.naturalNumber == ((KEYEXCHANGE) obj).naturalNumber) &&

		    this.getIV() == ((KEYEXCHANGE) obj).getIV() &&

		    (((this.publicKey == null) && (((KEYEXCHANGE) obj).publicKey == null)) || ((this.publicKey != null) && this.publicKey.equals(((KEYEXCHANGE) obj).publicKey))));

	}
    }

    @Override
    public int hashCode() {
	return super.hashCode();
    }

    @Override
    public String toString() {

	return SEDAPExpressMessage.removeSemicolons(serializeHeader()

		.append((this.algorithmType != null) ? this.algorithmType : "").append(";").append((this.phase != null) ? this.phase : "").append(";")
		.append((this.keyLengthSharedSecret != null) ? this.keyLengthSharedSecret : "").append(";")
		.append((this.keyLengthDHKEM != null) ? this.keyLengthDHKEM : "").append(";")
		.append((this.primeNumber != null) ? this.primeNumber : "").append(";")
		.append((this.naturalNumber != null) ? this.naturalNumber : "").append(";")
		.append((this.iv != null) ? SEDAPExpressMessage.HEXFOMATER.toHexDigits(this.iv, 16) : "").append(";")
		.append((this.publicKey != null) ? SEDAPExpressMessage.HEXFOMATER.formatHex(this.publicKey.getEncoded()) : "").toString());
    }
}
