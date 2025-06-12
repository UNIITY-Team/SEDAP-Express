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

import java.util.Iterator;
import java.util.logging.Level;

public class GENERIC extends SEDAPExpressMessage {

    private static final long serialVersionUID = 3541277880391552711L;

    public enum ContentType {

	SEDAP, ASCII, NMEA, XML, JSON, BINARY
    }

    private ContentType contentType;

    private DataEncoding encoding;

    private String content;

    public ContentType getContentType() {
	return this.contentType;
    }

    public void setContentType(ContentType contentType) {
	this.contentType = contentType;
    }

    public DataEncoding getEncoding() {
	return this.encoding;
    }

    public void setEncoding(DataEncoding encoding) {
	this.encoding = encoding;
    }

    public String getContent() {
	return this.content;
    }

    public void setContent(String content) {
	this.content = content;
    }

    /**
     * Instantiate a new GENERIC message
     *
     * @param number
     * @param time
     * @param sender
     * @param classification
     * @param acknowledgement
     * @param mac
     * @param contentType
     * @param encoding
     * @param content
     */
    public GENERIC(Short number, Long time, String sender, Classification classification, Acknowledgement acknowledgement, String mac, ContentType contentType, DataEncoding encoding, String content) {

	super(number, time, sender, classification, acknowledgement, mac);

	this.contentType = contentType;
	this.encoding = encoding;
	this.content = content;
    }

    /**
     *
     * @param message
     */
    public GENERIC(String message) {

	this(SEDAPExpressMessage.splitMessage(message.substring(message.indexOf(';') + 1)).iterator());
    }

    /**
     *
     * @param message
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
	    } else if (SEDAPExpressMessage.matchesPattern(SEDAPExpressMessage.NUMBER_MATCHER, value)) {
		this.content = value;
	    } else if (!value.isBlank()) {
		SEDAPExpressMessage.logger.logp(Level.SEVERE, "GENERIC", "GENERIC(Iterator<String> message)", "Mandatory field \"content\" contains invalid value!", value);
	    }
	}
    }

    @Override
    public boolean equals(Object obj) {

	if (obj == null) {
	    return false;
	} else if (!(obj instanceof RESEND)) {
	    return false;
	} else {
	    return super.equals(obj) &&

		    (((this.contentType == null) && (((GENERIC) obj).contentType == null)) || ((this.contentType != null) && this.contentType.equals(((GENERIC) obj).contentType))) &&

		    (((this.encoding == null) && (((GENERIC) obj).encoding == null)) || ((this.encoding != null) && this.encoding.equals(((GENERIC) obj).encoding))) &&

		    (((this.content == null) && (((GENERIC) obj).content == null)) || ((this.content != null) && this.content.equals(((GENERIC) obj).content)));
	}
    }

    @Override
    public int hashCode() {
	return super.hashCode();
    }

    @Override
    public String toString() {

	return SEDAPExpressMessage.removeSemicolons(
		serializeHeader().append((this.contentType != null) ? this.contentType : "").append(";").append((this.encoding != null) ? this.encoding : "").append(";").append((this.content != null) ? this.content : "").toString());
    }

}
