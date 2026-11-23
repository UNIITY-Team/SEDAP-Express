/**
 * Note: This license has also been called the “Simplified BSD License” and the “FreeBSD License”.
 *
 * Copyright 2024-2026 UNIITY POC: Volker Voß, Federal Armed Forces of Germany
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
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGE.
 */
package de.bundeswehr.uniity.sedapexpress.messages;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

import org.bouncycastle.crypto.agreement.DHStandardGroups;
import org.bouncycastle.crypto.params.DHParameters;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.DecoderException;

/**
 * Represents a phased SEDAP-Express key exchange using DH, ECDH or a supported KEM.
 * 
 * @author Volker Voß
 *
 */
public class KEYEXCHANGE extends SEDAPExpressMessage {

	private static final long serialVersionUID = -6681575300891302102L;

/**
 * Defines the three phases of the SEDAP-Express key-exchange protocol and their wire codes.
 */
	public static enum ExchangePhase {

		Init(0), Compute(1), Final(2);

		private final int phase;

		/**
		 * Returns the numeric phase code used on the wire.
		 * @return the numeric phase code
		 */
		public int getIntValue() {
			return this.phase;
		}

		@Override
		public String toString() {
			return Integer.toString(this.phase);
		}

		private ExchangePhase(int phase) {
			this.phase = phase;
		}
	}

/**
 * Identifies the key-agreement algorithm selected for a KEYEXCHANGE.
 */
	public enum AlgorithmType {

		Diffie_Hellman_Merkle(0), Diffie_Hellman_Merkle_with_BrainpoolP256r1(1), MLKEM512(2), MLKEM768(3), MLKEM1024(4), FrodoKEM976(5), FrodoKEM1344(6);

		int algorithm;

	/**
	 * Returns the algorithm type value.
	 * @return the algorithm type value
	 */
		public int getAlgorithmTypeValue() {

			return this.algorithm;
		}

		AlgorithmType(int algorithm) {
			this.algorithm = algorithm;
		}

	/**
	 * Resolves the corresponding value from the supplied representation.
	 * @param algorithm value for algorithm
	 * @return the result of the operation
	 */
		public static AlgorithmType valueOfAlgorithmType(int algorithm) {

			return switch (algorithm) {
			case 0 -> Diffie_Hellman_Merkle;
			case 1 -> Diffie_Hellman_Merkle_with_BrainpoolP256r1;
			case 2 -> MLKEM512;
			case 3 -> MLKEM768;
			case 4 -> MLKEM1024;
			case 5 -> FrodoKEM976;
			case 6 -> FrodoKEM1344;
			default -> throw new IllegalArgumentException("Unsupported algorithm type: " + algorithm);
			};
		}

	/**
	 * Returns the textual representation of this instance.
	 * @return the result of the operation
	 */
		@Override
		public String toString() {
			return String.valueOf(this.algorithm);
		}

	}

/**
 * Defines the supported RFC 7919 FFDHE group sizes in bits.
 */
	public static enum DHParameterLength {

		Parameter_LENGTH_2048_BITS(2048), Parameter_LENGTH_3072_BITS(3072), Parameter_LENGTH_4096_BITS(4096);

		private final int parameterLength;

		/**
		 * Returns the parameter-group size in bits.
		 * @return the parameter-group size in bits
		 */
		public int getIntValue() {
			return this.parameterLength;
		}

		@Override
		public String toString() {
			return Integer.toString(this.parameterLength);
		}

		private DHParameterLength(int parameterLength) {
			this.parameterLength = parameterLength;
		}
	}

/**
 * Defines the supported AES session-key sizes in bits.
 */
	public static enum AESKeyLength {

		KEY_LENGTH_128_BITS(128), KEY_LENGTH_256_BITS(256);

		private final int keyLength;

		/**
		 * Returns the AES key size in bits.
		 * @return the AES key size in bits
		 */
		public int getIntValue() {
			return this.keyLength;
		}

		@Override
		public String toString() {
			return Integer.toString(this.keyLength);
		}

		private AESKeyLength(int keyLength) {
			this.keyLength = keyLength;
		}
	}

	private String recipient;

	private AlgorithmType algorithmType;
	private ExchangePhase phase;

	private AESKeyLength aesKeyLength;
	private DHParameterLength dhParameterLength;

	private BigInteger primeNumber;
	private BigInteger naturalNumber;
	private byte[] salt;

	/**
	 * Algorithm- and phase-dependent public exchange data:
	 * DH public value, ECDH/KEM public key, or KEM encapsulation ciphertext.
	 */
	private byte[] publicData;

	/**
	 * Returns the recipient.
	 * @return the recipient
	 */
	public String getRecipient() {
		return this.recipient;
	}

	/**
	 * Sets the recipient.
	 * @param recipient value for recipient
	 */
	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}

	/**
	 * Returns the algorithm type.
	 * @return the algorithm type
	 */
	public AlgorithmType getAlgorithmType() {
		return this.algorithmType;
	}

	/**
	 * Sets the algorithm type.
	 * @param algorithmType value for algorithm type
	 */
	public void setAlgorithmType(AlgorithmType algorithmType) {
		this.algorithmType = algorithmType;
	}

	/**
	 * Returns the phase.
	 * @return the phase
	 */
	public ExchangePhase getPhase() {
		return this.phase;
	}

	/**
	 * Sets the phase.
	 * @param phase value for phase
	 */
	public void setPhase(ExchangePhase phase) {
		this.phase = phase;
	}

	/**
	 * Returns the aes key length.
	 * @return the aes key length
	 */
	public AESKeyLength getAesKeyLength() {
		return aesKeyLength;
	}

	/**
	 * Sets the aes key length.
	 * @param aesKeyLength value for aes key length
	 */
	public void setAesKeyLength(AESKeyLength aesKeyLength) {
		this.aesKeyLength = aesKeyLength;
	}

	/**
	 * Returns the dh parameter length.
	 * @return the dh parameter length
	 */
	public DHParameterLength getDhParameterLength() {
		return dhParameterLength;
	}

	/**
	 * Sets the dh parameter length.
	 * @param dhParameterLength value for dh parameter length
	 */
	public void setDhParameterLength(DHParameterLength dhParameterLength) {
		this.dhParameterLength = dhParameterLength;
	}

	/**
	 * Returns the prime number.
	 * @return the prime number
	 */
	public BigInteger getPrimeNumber() {
		return this.primeNumber;
	}

	/**
	 * Sets the prime number.
	 * @param primeNumber value for prime number
	 */
	public void setPrimeNumber(BigInteger primeNumber) {
		this.primeNumber = primeNumber;
	}

	/**
	 * Returns the natural number.
	 * @return the natural number
	 */
	public BigInteger getNaturalNumber() {
		return this.naturalNumber;
	}

	/**
	 * Sets the natural number.
	 * @param naturalNumber value for natural number
	 */
	public void setNaturalNumber(BigInteger naturalNumber) {
		this.naturalNumber = naturalNumber;
	}

	/**
	 * Returns the salt.
	 * @return the salt
	 */
	public byte[] getSalt() {
		return this.salt;
	}

	/**
	 * Sets the salt.
	 * @param salt value for salt
	 */
	public void setSalt(byte[] salt) {
		this.salt = salt;
	}

	/**
	 * Returns the public data.
	 * @return the public data
	 */
	public byte[] getPublicData() {
		return this.publicData;
	}

	/**
	 * Sets the public data.
	 * @param publicData value for public data
	 */
	public void setPublicData(byte[] publicData) {
		this.publicData = publicData;
	}

	/**
	 * Instantiate a new default KEYEXCHANGE message
	 */
	public KEYEXCHANGE() {

		super(null, null, null, null, null, null);

		this.recipient = null;
		this.algorithmType = null;
		this.phase = null;
		this.aesKeyLength = null;
		this.dhParameterLength = null;
		this.primeNumber = null;
		this.naturalNumber = null;
		this.salt = null;
		this.publicData = null;
	}

	/**
	 * Instantiate a new KEYEXCHANGE message
	 * 
	 * @param number value for number
	 * @param time value for time
	 * @param sender value for sender
	 * @param classification value for classification
	 * @param acknowledgement value for acknowledgement
	 * @param mac value for mac
	 * @param recipient value for recipient
	 * @param algorithmType value for algorithm type
	 * @param phase value for phase
	 * @param aesKeyLength value for aes key length
	 * @param dhParameterLength value for dh parameter length
	 * @param primeNumber value for prime number
	 * @param naturalNumber value for natural number
	 * @param salt value for salt
	 * @param publicData        algorithm- and phase-dependent public exchange data
	 */
	public KEYEXCHANGE(Byte number, Long time, String sender, Classification classification, Acknowledgement acknowledgement, String mac, String recipient, AlgorithmType algorithmType,
			ExchangePhase phase, AESKeyLength aesKeyLength, DHParameterLength dhParameterLength, BigInteger primeNumber, BigInteger naturalNumber, byte[] salt, byte[] publicData) {

		super(number, time, sender, classification, acknowledgement, mac);

		this.recipient = recipient;
		this.algorithmType = algorithmType;
		this.phase = phase;
		this.aesKeyLength = aesKeyLength;
		this.dhParameterLength = dhParameterLength;
		this.primeNumber = primeNumber;
		this.naturalNumber = naturalNumber;
		this.salt = salt;
		this.publicData = publicData;
	}

	/**
 * Creates a keyexchange instance.
	 *
	 * @param message value for message
	 */
	public KEYEXCHANGE(String message) {

		this(SEDAPExpressMessage.splitMessage(message.substring(message.indexOf(';') + 1)).iterator());
	}

	/**
 * Creates a keyexchange instance.
	 *
	 * @param message value for message
	 */
	public KEYEXCHANGE(Iterator<String> message) {

		super(message);

		// Keep the unmodified extension fields for strict phase validation below.
		List<String> rawFields = new ArrayList<>();
		message.forEachRemaining(rawFields::add);
		message = rawFields.iterator();

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
			if ("0".equals(value) || "1".equals(value) || "2".equals(value) || "3".equals(value) || "4".equals(value) || "5".equals(value) || "6".equals(value)) {
				this.algorithmType = AlgorithmType.valueOfAlgorithmType(Integer.parseInt(value));
			} else {
				SEDAPExpressMessage.logger.logp(Level.SEVERE, "KEYEXCHANGE", "KEYEXCHANGE(Iterator<String> message)", "Mandatory field \"Algorithm\" contains not a valid number!", value);
			}
		} else {
			SEDAPExpressMessage.logger.logp(Level.SEVERE, "KEYEXCHANGE", "KEYEXCHANGE(Iterator<String> message)", "Incomplete message!");
		}

		// Phase
		if (message.hasNext()) {
			value = message.next();
			this.phase = switch (value) {
			case "0" -> ExchangePhase.Init;
			case "1" -> ExchangePhase.Compute;
			case "2" -> ExchangePhase.Final;
			default -> null;
			};
			if (this.phase == null) {
				SEDAPExpressMessage.logger.logp(Level.SEVERE, "KEYEXCHANGE", "KEYEXCHANGE(Iterator<String> message)", "Mandatory field \"Phase\" contains not a valid number!", value);
			}
		} else {
			SEDAPExpressMessage.logger.logp(Level.SEVERE, "KEYEXCHANGE", "KEYEXCHANGE(Iterator<String> message)", "Incomplete message!");
		}

		boolean isDH = this.algorithmType == AlgorithmType.Diffie_Hellman_Merkle;
		boolean isECDH = this.algorithmType == AlgorithmType.Diffie_Hellman_Merkle_with_BrainpoolP256r1;
		boolean isKEM = this.algorithmType == AlgorithmType.MLKEM512 || this.algorithmType == AlgorithmType.MLKEM768 || this.algorithmType == AlgorithmType.MLKEM1024
				|| this.algorithmType == AlgorithmType.FrodoKEM976 || this.algorithmType == AlgorithmType.FrodoKEM1344;

		boolean isPhase0 = this.phase == ExchangePhase.Init;
		boolean isPhase1 = this.phase == ExchangePhase.Compute;
		boolean isPhase2 = this.phase == ExchangePhase.Final;

		boolean dhParametersRequired = isDH && isPhase0;
		boolean saltRequired = (isDH && isPhase1) || ((isECDH || isKEM) && isPhase0);
		boolean publicDataRequired = (isDH && isPhase1) || (isECDH && (isPhase0 || isPhase1)) || (isKEM && (isPhase0 || isPhase1));

		// AESKeyLength
		if (message.hasNext()) {
			value = message.next();
			if ("128".equals(value)) {
				this.aesKeyLength = AESKeyLength.KEY_LENGTH_128_BITS;
				if (!isPhase0) {
					SEDAPExpressMessage.logger.logp(Level.SEVERE, "KEYEXCHANGE", "KEYEXCHANGE(Iterator<String> message)", "Field \"AESKeyLength\" is only permitted in phase 0!", value);
				}
			} else if ("256".equals(value)) {
				this.aesKeyLength = AESKeyLength.KEY_LENGTH_256_BITS;
				if (!isPhase0) {
					SEDAPExpressMessage.logger.logp(Level.SEVERE, "KEYEXCHANGE", "KEYEXCHANGE(Iterator<String> message)", "Field \"AESKeyLength\" is only permitted in phase 0!", value);
				}
			} else if (isPhase0) {
				SEDAPExpressMessage.logger.logp(Level.SEVERE, "KEYEXCHANGE", "KEYEXCHANGE(Iterator<String> message)",
						value.isBlank() ? "Mandatory (phase 0) field \"AESKeyLength\" is empty!" : "Mandatory (phase 0) field \"AESKeyLength\" contains not a valid number!", value);
			} else if (!value.isBlank()) {
				SEDAPExpressMessage.logger.logp(Level.SEVERE, "KEYEXCHANGE", "KEYEXCHANGE(Iterator<String> message)", "Field \"AESKeyLength\" is not permitted outside phase 0!", value);
			} else {
				SEDAPExpressMessage.logger.logp(Level.INFO, "KEYEXCHANGE", "KEYEXCHANGE(Iterator<String> message)", "Optional (not in phase 0) field \"AESKeyLength\" is empty!");
			}
		} else if (isPhase0) {
			SEDAPExpressMessage.logger.logp(Level.SEVERE, "KEYEXCHANGE", "KEYEXCHANGE(Iterator<String> message)", "Mandatory (phase 0) field \"AESKeyLength\" is missing!");
		} else if (isDH || isECDH || isKEM) {
			SEDAPExpressMessage.logger.logp(Level.INFO, "KEYEXCHANGE", "KEYEXCHANGE(Iterator<String> message)", "Optional (not in phase 0) field \"AESKeyLength\" is empty!");
		}

		// DHParameterLength
		if (message.hasNext()) {
			value = message.next();
			if ("2048".equals(value) || "3072".equals(value) || "4096".equals(value)) {
				this.dhParameterLength = switch (value) {
				case "2048" -> DHParameterLength.Parameter_LENGTH_2048_BITS;
				case "3072" -> DHParameterLength.Parameter_LENGTH_3072_BITS;
				default -> DHParameterLength.Parameter_LENGTH_4096_BITS;
				};
				if (!dhParametersRequired) {
					SEDAPExpressMessage.logger.logp(Level.SEVERE, "KEYEXCHANGE", "KEYEXCHANGE(Iterator<String> message)",
							"Field \"DHParameterLength\" is only permitted in phase 0 with Diffie-Hellman-Merkle!", value);
				}
			} else if (dhParametersRequired) {
				SEDAPExpressMessage.logger.logp(Level.SEVERE, "KEYEXCHANGE", "KEYEXCHANGE(Iterator<String> message)",
						value.isBlank() ? "Mandatory (phase 0, DH) field \"DHParameterLength\" is empty!" : "Mandatory (phase 0, DH) field \"DHParameterLength\" contains not a valid number!", value);
			} else if (!value.isBlank()) {
				SEDAPExpressMessage.logger.logp(Level.SEVERE, "KEYEXCHANGE", "KEYEXCHANGE(Iterator<String> message)",
						"Field \"DHParameterLength\" is only permitted in phase 0 with Diffie-Hellman-Merkle!", value);
			} else {
				SEDAPExpressMessage.logger.logp(Level.INFO, "KEYEXCHANGE", "KEYEXCHANGE(Iterator<String> message)",
						"Optional field \"DHParameterLength\" is not required for this algorithm and phase!");
			}
		} else if (dhParametersRequired) {
			SEDAPExpressMessage.logger.logp(Level.SEVERE, "KEYEXCHANGE", "KEYEXCHANGE(Iterator<String> message)", "Mandatory (phase 0, DH) field \"DHParameterLength\" is missing!");
		}

		// PrimeNumber
		if (message.hasNext()) {
			value = message.next();
			if (SEDAPExpressMessage.matchesPattern(SEDAPExpressMessage.BIGINTEGER_MATCHER, value)) {
				this.primeNumber = new BigInteger(value, 16);
				if (!dhParametersRequired) {
					SEDAPExpressMessage.logger.logp(Level.SEVERE, "KEYEXCHANGE", "KEYEXCHANGE(Iterator<String> message)",
							"Field \"PrimeNumber\" is only permitted in phase 0 with Diffie-Hellman-Merkle!", value);
				}
			} else if (dhParametersRequired) {
				SEDAPExpressMessage.logger.logp(Level.SEVERE, "KEYEXCHANGE", "KEYEXCHANGE(Iterator<String> message)",
						value.isBlank() ? "Mandatory (phase 0, DH) field \"PrimeNumber\" is empty!" : "Mandatory (phase 0, DH) field \"PrimeNumber\" contains not a valid hexadecimal number!", value);
			} else if (!value.isBlank()) {
				SEDAPExpressMessage.logger.logp(Level.SEVERE, "KEYEXCHANGE", "KEYEXCHANGE(Iterator<String> message)", "Field \"PrimeNumber\" is only permitted in phase 0 with Diffie-Hellman-Merkle!",
						value);
			} else {
				SEDAPExpressMessage.logger.logp(Level.INFO, "KEYEXCHANGE", "KEYEXCHANGE(Iterator<String> message)", "Optional field \"PrimeNumber\" is not required for this algorithm and phase!");
			}
		} else if (dhParametersRequired) {
			SEDAPExpressMessage.logger.logp(Level.SEVERE, "KEYEXCHANGE", "KEYEXCHANGE(Iterator<String> message)", "Mandatory (phase 0, DH) field \"PrimeNumber\" is missing!");
		}

		// NaturalNumber (Generator)
		if (message.hasNext()) {
			value = message.next();
			if (SEDAPExpressMessage.matchesPattern(SEDAPExpressMessage.BIGINTEGER_MATCHER, value)) {
				this.naturalNumber = new BigInteger(value, 16);
				if (!dhParametersRequired) {
					SEDAPExpressMessage.logger.logp(Level.SEVERE, "KEYEXCHANGE", "KEYEXCHANGE(Iterator<String> message)",
							"Field \"NaturalNumber\" is only permitted in phase 0 with Diffie-Hellman-Merkle!", value);
				}
			} else if (dhParametersRequired) {
				SEDAPExpressMessage.logger.logp(Level.SEVERE, "KEYEXCHANGE", "KEYEXCHANGE(Iterator<String> message)",
						value.isBlank() ? "Mandatory (phase 0, DH) field \"NaturalNumber\" is empty!" : "Mandatory (phase 0, DH) field \"NaturalNumber\" contains not a valid hexadecimal number!",
						value);
			} else if (!value.isBlank()) {
				SEDAPExpressMessage.logger.logp(Level.SEVERE, "KEYEXCHANGE", "KEYEXCHANGE(Iterator<String> message)",
						"Field \"NaturalNumber\" is only permitted in phase 0 with Diffie-Hellman-Merkle!", value);
			} else {
				SEDAPExpressMessage.logger.logp(Level.INFO, "KEYEXCHANGE", "KEYEXCHANGE(Iterator<String> message)", "Optional field \"NaturalNumber\" is not required for this algorithm and phase!");
			}
		} else if (dhParametersRequired) {
			SEDAPExpressMessage.logger.logp(Level.SEVERE, "KEYEXCHANGE", "KEYEXCHANGE(Iterator<String> message)", "Mandatory (phase 0, DH) field \"NaturalNumber\" is missing!");
		}

		// Check consistency of the DH group parameters
		if (dhParametersRequired && this.primeNumber != null && this.dhParameterLength != null && this.primeNumber.bitLength() != this.dhParameterLength.getIntValue()) {
			SEDAPExpressMessage.logger.logp(Level.SEVERE, "KEYEXCHANGE", "KEYEXCHANGE(Iterator<String> message)",
					"Field \"PrimeNumber\" bit length (" + this.primeNumber.bitLength() + ") does not match \"DHParameterLength\" (" + this.dhParameterLength.getIntValue() + ")!");
		}
		if (dhParametersRequired && this.primeNumber != null && this.naturalNumber != null
				&& (this.naturalNumber.compareTo(BigInteger.ONE) <= 0 || this.naturalNumber.compareTo(this.primeNumber) >= 0)) {
			SEDAPExpressMessage.logger.logp(Level.SEVERE, "KEYEXCHANGE", "KEYEXCHANGE(Iterator<String> message)", "Field \"NaturalNumber\" must be greater than 1 and smaller than \"PrimeNumber\"!",
					this.naturalNumber);
		}

		// Salt
		if (message.hasNext()) {
			value = message.next();
			if (saltRequired) {
				if (value.isBlank()) {
					SEDAPExpressMessage.logger.logp(Level.SEVERE, "KEYEXCHANGE", "KEYEXCHANGE(Iterator<String> message)",
							"Mandatory (" + (isDH ? "phase 1, DH" : "phase 0") + ") field \"Salt\" is empty!");
				} else {
					try {
						this.salt = Base64.decode(value);
						if (this.salt.length != 32) {
							SEDAPExpressMessage.logger.logp(Level.SEVERE, "KEYEXCHANGE", "KEYEXCHANGE(Iterator<String> message)",
									"Mandatory field \"Salt\" must contain exactly 32 bytes after Base64 decoding!", this.salt.length);
							this.salt = null;
						}
					} catch (DecoderException e) {
						SEDAPExpressMessage.logger.logp(Level.SEVERE, "KEYEXCHANGE", "KEYEXCHANGE(Iterator<String> message)",
								"Mandatory field \"Salt\" could not be decoded from Base64 -> \"" + value + "\"!");
					}
				}
			} else if (!value.isBlank()) {
				SEDAPExpressMessage.logger.logp(Level.SEVERE, "KEYEXCHANGE", "KEYEXCHANGE(Iterator<String> message)", "Field \"Salt\" is not permitted for this algorithm and phase!", value);
			} else {
				SEDAPExpressMessage.logger.logp(Level.INFO, "KEYEXCHANGE", "KEYEXCHANGE(Iterator<String> message)", "Optional field \"Salt\" is not required for this algorithm and phase!");
			}
		} else if (saltRequired) {
			SEDAPExpressMessage.logger.logp(Level.SEVERE, "KEYEXCHANGE", "KEYEXCHANGE(Iterator<String> message)", "Mandatory (" + (isDH ? "phase 1, DH" : "phase 0") + ") field \"Salt\" is missing!");
		}

		// PublicData (DH public variable, ECDH/KEM public key or KEM ciphertext)
		if (message.hasNext()) {
			value = message.next();
			if (publicDataRequired) {
				if (value.isBlank()) {
					String requiredData = isDH ? "DH public variable" : isECDH ? "ECDH public key" : isPhase0 ? "KEM public key" : "KEM encapsulation ciphertext";
					SEDAPExpressMessage.logger.logp(Level.SEVERE, "KEYEXCHANGE", "KEYEXCHANGE(Iterator<String> message)",
							"Mandatory (phase " + this.phase.getIntValue() + ") field \"PublicData\" (" + requiredData + ") is empty!");
				} else {
					try {
						this.publicData = Base64.decode(value);
						if (this.publicData.length == 0) {
							SEDAPExpressMessage.logger.logp(Level.SEVERE, "KEYEXCHANGE", "KEYEXCHANGE(Iterator<String> message)", "Mandatory field \"PublicData\" is empty after Base64 decoding!");
							this.publicData = null;
						}
					} catch (DecoderException e) {
						SEDAPExpressMessage.logger.logp(Level.SEVERE, "KEYEXCHANGE", "KEYEXCHANGE(Iterator<String> message)",
								"Mandatory field \"PublicData\" could not be decoded from Base64 -> \"" + value + "\"!");
					}
				}
			} else if (!value.isBlank()) {
				SEDAPExpressMessage.logger.logp(Level.SEVERE, "KEYEXCHANGE", "KEYEXCHANGE(Iterator<String> message)", "Field \"PublicData\" is not permitted for this algorithm and phase!", value);
			} else {
				SEDAPExpressMessage.logger.logp(Level.INFO, "KEYEXCHANGE", "KEYEXCHANGE(Iterator<String> message)", "Optional field \"PublicData\" is not required for this algorithm and phase!");
			}
		} else if (publicDataRequired) {
			String requiredData = isDH ? "DH public variable" : isECDH ? "ECDH public key" : isPhase0 ? "KEM public key" : "KEM encapsulation ciphertext";
			SEDAPExpressMessage.logger.logp(Level.SEVERE, "KEYEXCHANGE", "KEYEXCHANGE(Iterator<String> message)",
					"Mandatory (phase " + this.phase.getIntValue() + ") field \"PublicData\" (" + requiredData + ") is missing!");
		}

		// Phase 2 always requires a 128-bit HMAC-SHA-256 key-confirmation value.
		if (isPhase2) {
			String mac = getMAC();
			if (mac == null) {
				SEDAPExpressMessage.logger.logp(Level.SEVERE, "KEYEXCHANGE", "KEYEXCHANGE(Iterator<String> message)", "Mandatory (phase 2) field \"MAC\" is empty!", mac);
			} else if (!mac.matches("(?i)[0-9a-f]{32}")) {
				SEDAPExpressMessage.logger.logp(Level.SEVERE, "KEYEXCHANGE", "KEYEXCHANGE(Iterator<String> message)",
						"Mandatory (phase 2) field \"MAC\" must contain exactly 32 hexadecimal characters (128-bit confirmation tag)!", mac);
			}
			if (mac == null || !mac.matches("(?i)[0-9a-f]{32}")) {
				throw new IllegalArgumentException("KEYEXCHANGE phase 2 requires an exact 128-bit HMAC-SHA-256 tag.");
			}

		}

		// Reject malformed or algorithm-inconsistent KEYEXCHANGE messages instead of
		// returning partially populated objects that appear to be valid.
		if (this.algorithmType == null || this.phase == null) {
			throw new IllegalArgumentException("KEYEXCHANGE requires a supported AlgorithmType and Phase.");
		}

		String[] fields = rawFields.toArray(String[]::new);
		String[] expected = new String[9];
		for (int i = 0; i < expected.length; i++) {
			expected[i] = i < fields.length ? fields[i] : "";
		}
		for (int i = 9; i < fields.length; i++) {
			if (!fields[i].isBlank()) {
				throw new IllegalArgumentException("Unexpected non-empty KEYEXCHANGE field at position " + i + ".");
			}
		}

		boolean dhPhase0 = isDH && isPhase0;
		boolean ecdhOrKemPhase0 = (isECDH || isKEM) && isPhase0;
		boolean phase1 = isPhase1;
		boolean phase2 = isPhase2;
		if (dhPhase0) {
			if (this.aesKeyLength == null || this.dhParameterLength == null || this.primeNumber == null || this.naturalNumber == null
					|| this.salt != null || this.publicData != null || !expected[7].isBlank() || !expected[8].isBlank()) {
				throw new IllegalArgumentException("DH phase 0 requires AESKeyLength, DHParameterLength, PrimeNumber and NaturalNumber only.");
			}
			DHParameters group = switch (this.dhParameterLength) {
			case Parameter_LENGTH_2048_BITS -> DHStandardGroups.rfc7919_ffdhe2048;
			case Parameter_LENGTH_3072_BITS -> DHStandardGroups.rfc7919_ffdhe3072;
			case Parameter_LENGTH_4096_BITS -> DHStandardGroups.rfc7919_ffdhe4096;
			};
			if (!group.getP().equals(this.primeNumber) || !group.getG().equals(this.naturalNumber)) {
				throw new IllegalArgumentException("DH parameters MUST exactly match the selected RFC 7919 FFDHE group.");
			}
		} else if (isDH && phase1) {
			if (this.aesKeyLength != null || this.dhParameterLength != null || this.primeNumber != null || this.naturalNumber != null
					|| this.salt == null || this.publicData == null || !expected[3].isBlank() || !expected[4].isBlank()
					|| !expected[5].isBlank() || !expected[6].isBlank()) {
				throw new IllegalArgumentException("DH phase 1 requires Salt and PublicData only.");
			}
		} else if (ecdhOrKemPhase0) {
			if (this.aesKeyLength == null || this.dhParameterLength != null || this.primeNumber != null || this.naturalNumber != null
					|| this.salt == null || this.publicData == null || !expected[4].isBlank() || !expected[5].isBlank() || !expected[6].isBlank()) {
				throw new IllegalArgumentException("ECDH/KEM phase 0 requires AESKeyLength, Salt and PublicData.");
			}
		} else if ((isECDH || isKEM) && phase1) {
			if (this.aesKeyLength != null || this.dhParameterLength != null || this.primeNumber != null || this.naturalNumber != null
					|| this.salt != null || this.publicData == null || !expected[3].isBlank() || !expected[4].isBlank()
					|| !expected[5].isBlank() || !expected[6].isBlank() || !expected[7].isBlank()) {
				throw new IllegalArgumentException("ECDH/KEM phase 1 requires PublicData only.");
			}
		} else if (phase2) {
			if (this.aesKeyLength != null || this.dhParameterLength != null || this.primeNumber != null || this.naturalNumber != null
					|| this.salt != null || this.publicData != null || !expected[3].isBlank() || !expected[4].isBlank()
					|| !expected[5].isBlank() || !expected[6].isBlank() || !expected[7].isBlank() || !expected[8].isBlank()) {
				throw new IllegalArgumentException("KEYEXCHANGE phase 2 must not contain algorithm parameter or public data fields.");
			}
		} else {
			throw new IllegalArgumentException("Unsupported algorithm/phase combination.");
		}

	}

	/**
	 * Compares this instance with the specified object.
	 * @param obj value for obj
	 * @return {@code true} if the condition is satisfied; otherwise {@code false}.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		String thisKey = equalityKey();
		String otherKey = ((SEDAPExpressMessage) obj).equalityKey();
		return thisKey != null && thisKey.equals(otherKey);
	}

	/**
	 * Returns the hash code for this instance.
	 * @return the result of the check
	 */
	@Override
	public int hashCode() {
		String key = equalityKey();
		return (key != null) ? key.hashCode() : System.identityHashCode(this);
	}

	/**
	 * Returns the textual representation of this instance.
	 * @return the result of the operation
	 */
	@Override
	public String toString() {

		return SEDAPExpressMessage.removeSemicolons(serializeHeader()

				.append((this.recipient != null) ? this.recipient : "").append(";")
				.append((this.algorithmType != null) ? this.algorithmType : "").append(";").append((this.phase != null) ? this.phase : "").append(";")
				.append((this.aesKeyLength != null) ? this.aesKeyLength : "").append(";").append((this.dhParameterLength != null) ? this.dhParameterLength : "").append(";")
				.append((this.primeNumber != null) ? this.primeNumber.toString(16) : "").append(";")
				.append((this.naturalNumber != null) ? SEDAPExpressMessage.HexFormater.formatHex(this.naturalNumber.toByteArray()) : "").append(";")
				.append((this.salt != null) ? Base64.toBase64String(this.salt) : "").append(";").append((this.publicData != null) ? Base64.toBase64String(this.publicData) : "").toString());
	}
}
