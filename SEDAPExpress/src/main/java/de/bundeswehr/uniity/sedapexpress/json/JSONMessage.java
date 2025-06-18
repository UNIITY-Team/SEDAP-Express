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
package de.bundeswehr.uniity.sedapexpress.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 *
 * @author Volker Voß
 *
 */
public class JSONMessage {

    @SerializedName("message")
    @Expose
    private String message;

    public String getMessage() {
	return this.message;
    }

    public void setMessage(String message) {
	this.message = message;
    }

    @Override
    public String toString() {
	StringBuilder sb = new StringBuilder();
	sb.append(JSONMessage.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
	sb.append("message");
	sb.append('=');
	sb.append(((this.message == null) ? "<null>" : this.message));
	sb.append(',');
	if (sb.charAt((sb.length() - 1)) == ',') {
	    sb.setCharAt((sb.length() - 1), ']');
	} else {
	    sb.append(']');
	}
	return sb.toString();
    }

    @Override
    public int hashCode() {
	int result = 1;
	result = ((result * 31) + ((this.message == null) ? 0 : this.message.hashCode()));
	return result;
    }

    @Override
    public boolean equals(Object other) {
	if (other == this) {
	    return true;
	}
	if ((other instanceof JSONMessage) == false) {
	    return false;
	}
	JSONMessage rhs = ((JSONMessage) other);
	return ((this.message == rhs.message) || ((this.message != null) && this.message.equals(rhs.message)));
    }

}
