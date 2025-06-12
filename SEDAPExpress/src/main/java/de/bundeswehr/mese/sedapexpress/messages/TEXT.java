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

import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.DecoderException;

public class TEXT extends SEDAPExpressMessage {

    private static final long serialVersionUID = 1140074068439618568L;

    public enum TextType {

	Undefined(0), Alert(1), Warning(2), Notice(3), Chat(4);

	int type;

	public int getType() {
	    return this.type;
	}

	TextType(int type) {
	    this.type = type;
	}

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

	@Override
	public String toString() {
	    return String.valueOf(this.type);
	}
    }

    private TextType type = TextType.Undefined;

    private DataEncoding encoding = DataEncoding.NONE;

    private String textContent;

    private String recipient;

    private String reference;

    public TextType getType() {
	return this.type;
    }

    public void setType(TextType type) {
	this.type = type;
    }

    public DataEncoding getEncoding() {
	return this.encoding;
    }

    public void setEncoding(DataEncoding encoding) {
	this.encoding = encoding;
    }

    public String getTextContent() {
	return this.textContent;
    }

    public void setTextContent(String text) {
	this.textContent = text;
    }

    public String getRecipient() {
	return this.recipient;
    }

    public void setRecipient(String recipient) {
	this.recipient = recipient;
    }

    public String getReference() {
	return this.reference;
    }

    public void setReference(String reference) {
	this.reference = reference;
    }

    /**
     * Instantiate a new TEXT message
     * 
     * @param number
     * @param time
     * @param sender
     * @param classification
     * @param acknowledgement
     * @param mac
     * @param recipient
     * @param type
     * @param encoding
     * @param textContent
     * @param reference
     */
    public TEXT(Short number, Long time, String sender, Classification classification, Acknowledgement acknowledgement, String mac, String recipient,
	    TextType type, DataEncoding encoding, String textContent, String reference) {

	super(number, time, sender, classification, acknowledgement, mac);

	this.recipient = recipient;
	this.type = type;
	this.encoding = encoding;
	this.textContent = textContent;
	this.reference = reference;

    }

    /**
     *
     * @param message
     */
    public TEXT(String message) {

	this(SEDAPExpressMessage.splitMessage(message.substring(message.indexOf(';') + 1)).iterator());
    }

    /**
     *
     * @param message
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
			this.textContent = new String(Base64.decode(value));
		    } catch (DecoderException e) {
			SEDAPExpressMessage.logger.logp(Level.SEVERE, "TEXT", "TEXT(Iterator<String> message)", "Mandatory field \"text\" could not be decoded from Base64!");
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

    @Override
    public boolean equals(Object obj) {

	if (obj == null) {
	    return false;
	} else if (!(obj instanceof TEXT)) {
	    return false;
	} else {
	    return super.equals(obj) && (this.type == ((TEXT) obj).type) &&

		    (((this.encoding == null) && (((TEXT) obj).encoding == null)) || ((this.encoding != null) && this.encoding.equals(((TEXT) obj).encoding))) &&

		    (((this.textContent == null) && (((TEXT) obj).textContent == null)) || ((this.textContent != null) && this.textContent.equals(((TEXT) obj).textContent))) &&

		    (((this.recipient == null) && (((TEXT) obj).recipient == null)) || ((this.recipient != null) && this.recipient.equals(((TEXT) obj).recipient))) &&

		    (((this.reference == null) && (((TEXT) obj).reference == null)) || ((this.reference != null) && this.reference.equals(((TEXT) obj).reference)));
	}
    }

    @Override
    public int hashCode() {
	return super.hashCode();
    }

    @Override
    public String toString() {

	return SEDAPExpressMessage
		.removeSemicolons(serializeHeader()
			.append((this.recipient != null) ? this.recipient : "").append(";")
			.append((this.type != null) ? this.type : "").append(";")
			.append((this.encoding != null) ? this.encoding : "").append(";")
			.append((this.textContent != null) ? ((this.encoding == DataEncoding.BASE64) ? Base64.toBase64String(this.textContent.getBytes()) : this.textContent) : "")
			.append((this.reference != null) ? ";" + this.reference : "").toString());
    }

}
