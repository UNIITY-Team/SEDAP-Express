
package de.bundeswehr.uniity.sedapexpress.json;

import javax.annotation.processing.Generated;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class Message {

    @SerializedName("message")
    @Expose
    private String message;

    public String getMessage() {
	return this.message;
    }

    public void setMessage(String message) {
	this.message = message;
    }

    public Message(String message) {
	this.message = message;
    }

    @Override
    public String toString() {
	StringBuilder sb = new StringBuilder();
	sb.append(Message.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
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

}
