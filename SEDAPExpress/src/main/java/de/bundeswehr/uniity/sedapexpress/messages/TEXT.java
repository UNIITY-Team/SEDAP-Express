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

import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.logging.Level;

import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.DecoderException;

/**
 * Represents a SEDAP-Express TEXT message with optional text encoding and recipient metadata.
 * 
 * @author Volker Voß
 *
 */
public class TEXT extends SEDAPExpressMessage {

	private static final long serialVersionUID = 1140074068439618568L;

/**
 * Represents a SEDAP-Express TEXT message with optional text encoding and recipient metadata.
 */
	public enum TextType {

		Undefined(0), Alert(1), Warning(2), Notice(3), Chat(4);

		int type;

	/**
	 * Returns the type.
	 * @return the type
	 */
		public int getType() {
			return this.type;
		}

		TextType(int type) {
			this.type = type;
		}

	/**
	 * Resolves the corresponding value from the supplied representation.
	 * @param type value for type
	 * @return the result of the operation
	 */
		public static TextType valueOfTextType(int type) {

			return switch (type) {
			case 0 -> Undefined;
			case 1 -> Alert;
			case 2 -> Warning;
			case 3 -> Notice;
			case 4 -> Chat;
			default -> Undefined;
			};
		}

	/**
	 * Returns the textual representation of this instance.
	 * @return the result of the operation
	 */
		@Override
		public String toString() {
			return String.format(java.util.Locale.ROOT, "%02d", this.type);
		}
	}

	private TextType type = TextType.Undefined;

	private DataEncoding encoding = DataEncoding.NONE;

	private String textContent;

	private String recipient;

	private String reference;

	/**
	 * Returns the type.
	 * @return the type
	 */
	public TextType getType() {
		return this.type;
	}

	/**
	 * Sets the type.
	 * @param type value for type
	 */
	public void setType(TextType type) {
		this.type = type;
	}

	/**
	 * Returns the encoding.
	 * @return the encoding
	 */
	public DataEncoding getEncoding() {
		return this.encoding;
	}

	/**
	 * Sets the encoding.
	 * @param encoding value for encoding
	 */
	public void setEncoding(DataEncoding encoding) {
		this.encoding = encoding;
	}

	/**
	 * Returns the text content.
	 * @return the text content
	 */
	public String getTextContent() {
		return this.textContent;
	}

	/**
	 * Sets the text content.
	 * @param text value for text
	 */
	public void setTextContent(String text) {
		this.textContent = text;
	}

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
	 * Returns the reference.
	 * @return the reference
	 */
	public String getReference() {
		return this.reference;
	}

	/**
	 * Sets the reference.
	 * @param reference value for reference
	 */
	public void setReference(String reference) {
		this.reference = reference;
	}

	/**
	 * Instantiate a new default TEXT message
	 */
	public TEXT() {

		super(null, null, null, null, null, null);

		this.recipient = null;
		this.type = null;
		this.encoding = null;
		this.textContent = null;
		this.reference = null;
	}

	/**
	 * Instantiate a new TEXT message
	 * 
	 * @param number value for number
	 * @param time value for time
	 * @param sender value for sender
	 * @param classification value for classification
	 * @param acknowledgement value for acknowledgement
	 * @param mac value for mac
	 * @param recipient value for recipient
	 * @param type value for type
	 * @param encoding value for encoding
	 * @param textContent value for text content
	 * @param reference value for reference
	 */
	public TEXT(Byte number, Long time, String sender, Classification classification, Acknowledgement acknowledgement, String mac, String recipient, TextType type, DataEncoding encoding,
			String textContent, String reference) {

		super(number, time, sender, classification, acknowledgement, mac);

		this.recipient = recipient;
		this.type = type;
		this.encoding = encoding;
		this.textContent = textContent;
		this.reference = reference;

	}

	/**
 * Creates a text instance.
	 *
	 * @param message value for message
	 */
	public TEXT(String message) {

		this(SEDAPExpressMessage.splitMessage(message.substring(message.indexOf(';') + 1)).iterator());
	}

	/**
 * Creates a text instance.
	 *
	 * @param message value for message
	 */
	public TEXT(Iterator<String> message) {

		super(message);

		String value;

		// Recipient
		if (message.hasNext()) {
			value = message.next();
			if (value.isBlank()) {
				SEDAPExpressMessage.logger.logp(Level.INFO, "TEXT", "TEXT(Iterator<String> message)", "Optional field \"recipient\" is empty!");
			} else {
				this.recipient = value;
			}
		} else {
			SEDAPExpressMessage.logger.logp(Level.SEVERE, "TEXT", "TEXT(Iterator<String> message)", "Incomplete message!");
		}

		// Type
		if (message.hasNext()) {
			value = message.next();
			if (SEDAPExpressMessage.matchesPattern(SEDAPExpressMessage.TEXTTYPE_MATCHER, value)) {
				this.type = TextType.valueOfTextType(Integer.parseInt(value));
			} else if (value.isBlank()) {
				SEDAPExpressMessage.logger.logp(Level.INFO, "TEXT", "TEXT(Iterator<String> message)", "Optional field \"type\" is empty!");
			} else {
				SEDAPExpressMessage.logger.logp(Level.SEVERE, "TEXT", "TEXT(Iterator<String> message)", "TEXT field \"type\" invalid value: \"" + value + "\"");
			}
		} else {
			SEDAPExpressMessage.logger.logp(Level.SEVERE, "TEXT", "TEXT(Iterator<String> message)", "Incomplete message!");
		}

		// Encoding
		if (message.hasNext()) {
			value = message.next();
			if (SEDAPExpressMessage.matchesPattern(SEDAPExpressMessage.DATA_ENCODING_MATCHER, value)) {
				this.encoding = DataEncoding.valueOf(value);
			} else if (value.isBlank()) {
				this.encoding = DataEncoding.NONE;
				SEDAPExpressMessage.logger.logp(Level.INFO, "TEXT", "TEXT(Iterator<String> message)", "Optional TEXT field \"encoding\" is empty!");
			} else {
				SEDAPExpressMessage.logger.logp(Level.SEVERE, "TEXT", "TEXT(Iterator<String> message)", "Optional TEXT field \"encoding\" invalid value: \"" + value + "\"");
			}
		} else {
			SEDAPExpressMessage.logger.logp(Level.SEVERE, "TEXT", "TEXT(Iterator<String> message)", "Incomplete message!");
		}

		// Text
		if (message.hasNext()) {
			value = message.next();
			if (value.isBlank()) {
				SEDAPExpressMessage.logger.logp(Level.SEVERE, "TEXT", "TEXT(Iterator<String> message)", "Mandatory field \"text\" is empty!");
			} else {
				if (this.encoding == DataEncoding.BASE64) {
					try {
						this.textContent = new String(Base64.decode(value), StandardCharsets.ISO_8859_1);
					} catch (DecoderException e) {
						SEDAPExpressMessage.logger.logp(Level.SEVERE, "TEXT", "TEXT(Iterator<String> message)", "Mandatory field \"text\" could not be decoded from Base64 - > \"" + value + "\"!");
					}

				} else {
					this.textContent = value;
				}
			}
		} else {
			SEDAPExpressMessage.logger.logp(Level.SEVERE, "TEXT", "TEXT(Iterator<String> message)", "Incomplete message!");
		}

		// Reference
		if (message.hasNext()) {
			value = message.next();
			if (value.isBlank()) {
				SEDAPExpressMessage.logger.logp(Level.INFO, "TEXT", "TEXT(Iterator<String> message)", "Optional field \"reference\" is empty!");
			} else {
				this.reference = value;
			}
		} else
			SEDAPExpressMessage.logger.logp(Level.INFO, "TEXT", "TEXT(Iterator<String> message)", "Optional field \"reference\" is empty!");

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

		return SEDAPExpressMessage.removeSemicolons(serializeHeader().append((this.recipient != null) ? this.recipient : "").append(";").append((this.type != null) ? this.type : "").append(";")
				.append((this.encoding != null) ? this.encoding : "").append(";")
				.append((this.textContent != null) ? ((this.encoding == DataEncoding.BASE64) ? Base64.toBase64String(this.textContent.getBytes(StandardCharsets.ISO_8859_1)) : this.textContent) : "")
				.append((this.reference != null) ? ";" + this.reference : "").toString());
	}

}
