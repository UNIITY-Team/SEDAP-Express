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
package de.bundeswehr.uniity.sedapexpress.messages;

import java.util.Iterator;
import java.util.logging.Level;

/**
 * 
 * @author Volker Voß
 *
 */
public class TIMESYNC extends SEDAPExpressMessage {

    private static final long serialVersionUID = -5109007783190777675L;

    private Long timestamp;

    public Long getTimestamp() {
	return this.timestamp;
    }

    public void setTimestamp(Long timestamp) {
	this.timestamp = timestamp;
    }

    /**
     * Instantiate the initial TIMESYNC message
     *
     * @param number
     * @param time
     * @param sender
     * @param classification
     * @param acknowledgement
     * @param mac
     */
    public TIMESYNC(Byte number, Long time, String sender, Classification classification, Acknowledgement acknowledgement, String mac) {
	super(number, time, sender, classification, acknowledgement, mac);
	this.timestamp = null;
    }

    /**
     * Instantiate the answer TIMESYNC message (used only by the SEC/SECMockUp
     * 
     * @param number
     * @param time
     * @param sender
     * @param classification
     * @param acknowledgement
     * @param mac
     * @param timestamp
     */
    public TIMESYNC(Byte number, Long time, String sender, Classification classification, Acknowledgement acknowledgement, String mac, Long timestamp) {
	super(number, time, sender, classification, acknowledgement, mac);
	this.timestamp = timestamp;
    }

    /**
     * Instantiate a new TIMESYNC message
     *
     * @param message
     */
    public TIMESYNC(String message) {

	this(SEDAPExpressMessage.splitMessage(message.substring(message.indexOf(';') + 1)).iterator());
    }

    /**
     *
     * @param message
     */
    public TIMESYNC(Iterator<String> message) {

	super(message);

	String value;

	// Timestamp
	if (message.hasNext()) {
	    value = message.next();
	    if (value.isEmpty()) {
		SEDAPExpressMessage.logger.logp(Level.SEVERE, "TIMESYNC", "TIMESYNC(Iterator<String> message)", "Mandatory field \"timestamp\" is empty!");
	    } else if (SEDAPExpressMessage.matchesPattern(SEDAPExpressMessage.TIME_MATCHER, value)) {
		this.timestamp = Long.parseLong(value, 16);
	    } else if (!value.isBlank()) {
		SEDAPExpressMessage.logger.logp(Level.SEVERE, "TIMESYNC", "TIMESYNC(Iterator<String> message)", "Mandatory field \"timestamp\" contains invalid value!", value);
	    }
	}
    }

    @Override
    public boolean equals(Object obj) {

	if (obj == null) {
	    return false;
	} else if (!(obj instanceof TIMESYNC)) {
	    return false;
	} else {
	    return super.equals(obj) &&

		    (((this.timestamp == null) && (((TIMESYNC) obj).timestamp == null)) || ((this.timestamp != null) && this.timestamp.equals(((TIMESYNC) obj).timestamp)));
	}
    }

    @Override
    public int hashCode() {
	return super.hashCode();
    }

    @Override
    public String toString() {

	return SEDAPExpressMessage.removeSemicolons(serializeHeader().append((this.timestamp != null) ? SEDAPExpressMessage.HexFormater.toHexDigits(this.timestamp) : "").toString());
    }

}
