
package de.bundeswehr.uniity.sedapexpress.json;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import jakarta.annotation.Generated;
import jakarta.validation.Valid;

@Generated("jsonschema2pojo")
public class SEDAPExpressJSONMessage {

    @SerializedName("messages")
    @Expose
    @Valid
    private List<Message> messages = new ArrayList<Message>();

    public List<Message> getMessages() {
	return this.messages;
    }

    public void setMessages(List<Message> messages) {
	this.messages = messages;
    }

    @Override
    public String toString() {
	StringBuilder sb = new StringBuilder();
	sb.append(SEDAPExpressJSONMessage.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
	sb.append("messages");
	sb.append('=');
	sb.append(((this.messages == null) ? "<null>" : this.messages));
	sb.append(',');
	if (sb.charAt((sb.length() - 1)) == ',') {
	    sb.setCharAt((sb.length() - 1), ']');
	} else {
	    sb.append(']');
	}
	return sb.toString();
    }

}
