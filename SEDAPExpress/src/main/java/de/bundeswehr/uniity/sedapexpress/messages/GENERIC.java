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

import java.util.Iterator;
import java.util.logging.Level;

/**
 * Represents or provides the generic functionality.
 * 
 * @author Volker Voß
 *
 */
public class GENERIC extends SEDAPExpressMessage {

	private static final long serialVersionUID = 3541277880391552711L;

/**
 * Represents or provides the generic functionality.
 */
	public enum ContentType {

		SEDAP, ASCII, NMEA, XML, JSON, BINARY
	}

	private ContentType contentType;

	private DataEncoding encoding;

	private String content;

	/**
	 * Returns the content type.
	 * @return the content type
	 */
	public ContentType getContentType() {
		return this.contentType;
	}

	/**
	 * Sets the content type.
	 * @param contentType value for content type
	 */
	public void setContentType(ContentType contentType) {
		this.contentType = contentType;
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
	 * Returns the content.
	 * @return the content
	 */
	public String getContent() {
		return this.content;
	}

	/**
	 * Sets the content.
	 * @param content value for content
	 */
	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * Instantiate a new default GENERIC message
	 */
	public GENERIC() {

		super(null, null, null, null, null, null);

		this.contentType = null;
		this.encoding = null;
		this.content = null;
	}

	/**
	 * Instantiate a new GENERIC message
	 *
	 * @param number value for number
	 * @param time value for time
	 * @param sender value for sender
	 * @param classification value for classification
	 * @param acknowledgement value for acknowledgement
	 * @param mac value for mac
	 * @param contentType value for content type
	 * @param encoding value for encoding
	 * @param content value for content
	 */
	public GENERIC(Byte number, Long time, String sender, Classification classification, Acknowledgement acknowledgement, String mac, ContentType contentType, DataEncoding encoding, String content) {

		super(number, time, sender, classification, acknowledgement, mac);

		this.contentType = contentType;
		this.encoding = encoding;
		this.content = content;
	}

	/**
 * Creates a generic instance.
	 *
	 * @param message value for message
	 */
	public GENERIC(String message) {

		this(SEDAPExpressMessage.splitMessage(message.substring(message.indexOf(';') + 1)).iterator());
	}

	/**
 * Creates a generic instance.
	 *
	 * @param message value for message
	 */
	public GENERIC(Iterator<String> message) {

		super(message);

		String value;

		// ContentType
		if (message.hasNext()) {
			value = message.next();
			if (value.isBlank()) {
				SEDAPExpressMessage.logger.logp(Level.INFO, "GENERIC", "GENERIC(Iterator<String> message)", "Optional field \"contentType\" is empty!");
			} else if (SEDAPExpressMessage.matchesPattern(SEDAPExpressMessage.CONTENT_TYPE_MATCHER, value)) {
				this.contentType = ContentType.valueOf(value);
				if (this.contentType == null) {
					SEDAPExpressMessage.logger.logp(Level.INFO, "GENERIC", "GENERIC(Iterator<String> message)", "Optional field \"contentType\" has an invalid value > " + this.contentType);
				}
			} else {
				SEDAPExpressMessage.logger.logp(Level.SEVERE, "GENERIC", "GENERIC(Iterator<String> message)", "Optional field \"contentType\" invalid value: \"" + value + "\"");
			}
		} else {
			SEDAPExpressMessage.logger.logp(Level.SEVERE, "GENERIC", "GENERIC(Iterator<String> message)", "Incomplete message!");
		}

		// Encoding
		if (message.hasNext()) {
			value = message.next();
			if (value.isBlank()) {
				SEDAPExpressMessage.logger.logp(Level.INFO, "GENERIC", "GENERIC(Iterator<String> message)", "Optional field \"encoding\" is empty!");
			} else if (SEDAPExpressMessage.matchesPattern(SEDAPExpressMessage.DATA_ENCODING_MATCHER, value)) {
				this.encoding = DataEncoding.valueOf(value);
			} else {
				SEDAPExpressMessage.logger.logp(Level.SEVERE, "GENERIC", "GENERIC(Iterator<String> message)", "Mandatory field \"encoding\" invalid value: \"" + value + "\"");
			}
		} else {
			SEDAPExpressMessage.logger.logp(Level.SEVERE, "GENERIC", "GENERIC(Iterator<String> message)", "Incomplete message!");
		}

		// Content
		if (message.hasNext()) {
			value = message.next();
			if (value.isEmpty()) {
				SEDAPExpressMessage.logger.logp(Level.SEVERE, "GENERIC", "GENERIC(Iterator<String> message)", "Mandatory field \"content\" is empty!");
			} else if (value.isBlank()) {
				SEDAPExpressMessage.logger.logp(Level.SEVERE, "GENERIC", "GENERIC(Iterator<String> message)", "Mandatory field \"content\" contains invalid value!", value);
			} else {
				this.content = value;
			}
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

		return SEDAPExpressMessage.removeSemicolons(serializeHeader().append((this.contentType != null) ? this.contentType : "").append(";").append((this.encoding != null) ? this.encoding : "")
				.append(";").append((this.content != null) ? this.content : "").toString());
	}

}
