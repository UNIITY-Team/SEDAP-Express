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

public class RESEND extends SEDAPExpressMessage {

    private static final long serialVersionUID = 906671527741355989L;

    private String recipient;

    private MessageType nameOfTheMissingMessage;

    private Short numberOfTheMissingMessage;

    public String getRecipient() {
	return this.recipient;
    }

    public void setRecipient(String recipient) {
	this.recipient = recipient;
    }

    public MessageType getNameOfTheMissingMessage() {
	return this.nameOfTheMissingMessage;
    }

    public void setNameOfTheMissingMessage(MessageType nameOfTheMissingMessage) {
	this.nameOfTheMissingMessage = nameOfTheMissingMessage;
    }

    public Short getNumberOfTheMissingMessage() {
	return this.numberOfTheMissingMessage;
    }

    public void setNumberOfTheMissingMessage(Short numberOfTheMissingMessage) {
	this.numberOfTheMissingMessage = numberOfTheMissingMessage;
    }

    /**
     * Instantiate a new RESEND message
     *
     * @param number
     * @param time
     * @param sender
     * @param classification
     * @param acknowledgement
     * @param mac
     * @param recipient
     * @param nameOfTheMissingMessage
     * @param numberOfTheMissingMessage
     */
    public RESEND(Short number, Long time, String sender, Classification classification, Acknowledgement acknowledgement, String mac, String recipient, MessageType nameOfTheMissingMessage, Short numberOfTheMissingMessage) {

	super(number, time, sender, classification, acknowledgement, mac);

	this.recipient = recipient;
	this.nameOfTheMissingMessage = nameOfTheMissingMessage;
	this.numberOfTheMissingMessage = numberOfTheMissingMessage;
    }

    /**
     *
     * @param message
     */
    public RESEND(String message) {

	this(SEDAPExpressMessage.splitMessage(message.substring(message.indexOf(';') + 1)).iterator());
    }

    /**
     *
     * @param message
     */
    public RESEND(Iterator<String> message) {

	super(message);

	String value;

	// Recipient
	if (message.hasNext()) {
	    value = message.next();
	    if (value.isBlank()) {
		SEDAPExpressMessage.logger.logp(Level.SEVERE, "RESEND", "RESEND(Iterator<String> message)", "Mandatory field \"recipient\" is empty!");
	    } else {
		this.recipient = value;
	    }
	} else {
	    SEDAPExpressMessage.logger.logp(Level.SEVERE, "RESEND", "RESEND(Iterator<String> message)", "Incomplete message!");
	}

	// Name
	if (message.hasNext()) {
	    value = message.next();
	    if (SEDAPExpressMessage.matchesPattern(SEDAPExpressMessage.NAME_MATCHER, value)) {
		this.nameOfTheMissingMessage = MessageType.valueOfMessageType(value);
	    } else {
		SEDAPExpressMessage.logger.logp(Level.SEVERE, "RESEND", "RESEND(Iterator<String> message)", "Mandatory field \"nameOfTheMissingMessage\" invalid value: \"" + value + "\"");
	    }
	} else {
	    SEDAPExpressMessage.logger.logp(Level.SEVERE, "RESEND", "RESEND(Iterator<String> message)", "Incomplete message!");
	}

	// Number
	if (message.hasNext()) {
	    value = message.next();
	    if (value.isEmpty()) {
		SEDAPExpressMessage.logger.logp(Level.SEVERE, "RESEND", "RESEND(Iterator<String> message)", "Mandatory field \"numberOfTheMissingMessage\" is empty!");
	    } else if (SEDAPExpressMessage.matchesPattern(SEDAPExpressMessage.NUMBER_MATCHER, value)) {
		this.numberOfTheMissingMessage = Short.parseShort(value, 16);
	    } else if (!value.isBlank()) {
		SEDAPExpressMessage.logger.logp(Level.SEVERE, "RESEND", "RESEND(Iterator<String> message)", "Mandatory field \"numberOfTheMissingMessage\" contains invalid value!", value);
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

		    (((this.recipient == null) && (((RESEND) obj).recipient == null)) || ((this.recipient != null) && this.recipient.equals(((RESEND) obj).recipient))) &&

		    (((this.nameOfTheMissingMessage == null) && (((RESEND) obj).nameOfTheMissingMessage == null)) || ((this.nameOfTheMissingMessage != null) && this.nameOfTheMissingMessage.equals(((RESEND) obj).nameOfTheMissingMessage))) &&

		    (this.numberOfTheMissingMessage == (((RESEND) obj).numberOfTheMissingMessage));
	}
    }

    @Override
    public int hashCode() {
	return super.hashCode();
    }

    @Override
    public String toString() {
	return SEDAPExpressMessage.removeSemicolons(serializeHeader().append((this.recipient != null) ? this.recipient : "").append(";").append((this.nameOfTheMissingMessage != null) ? this.nameOfTheMissingMessage : "").append(";")
		.append((this.numberOfTheMissingMessage != null) ? SEDAPExpressMessage.HEXFOMATER.toHexDigits(this.numberOfTheMissingMessage) : "").toString());
    }

}
