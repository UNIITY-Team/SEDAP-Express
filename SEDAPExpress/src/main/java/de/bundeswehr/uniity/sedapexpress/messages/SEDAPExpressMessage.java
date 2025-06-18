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

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HexFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * @author Volker Voß
 */
public abstract class SEDAPExpressMessage implements Comparable<SEDAPExpressMessage>, Serializable {

    private static final long serialVersionUID = 6080930577547088687L;

    protected static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    static {
	SEDAPExpressMessage.logger.setLevel(Level.ALL);
    }

    public enum MessageType {

	ACKNOWLEDGE, COMMAND, CONTACT, EMISSION, GENERIC, GRAPHIC, HEARTBEAT, KEYEXCHANGE, METEO, OWNUNIT, RESEND, STATUS, TEXT, TIMESYNC;

	public static MessageType valueOfMessageType(String type) {
	    return MessageType.valueOf(type.toUpperCase());
	}
    }

    public static final NumberFormat numberFormatter = new DecimalFormat("##.############", DecimalFormatSymbols.getInstance(Locale.ENGLISH));

    public enum DeleteFlag {

	FALSE, TRUE;

	public static DeleteFlag getValueOfDeleteFlag(boolean flag) {

	    if (flag)
		return TRUE;
	    else
		return FALSE;
	}
    }

    public enum DataEncoding {

	NONE, BASE64;
    }

    public enum Classification {

	None('-'), Public('P'), Unclas('U'), Restricted('R'), Confidential('C'), Secret('S'), Top_Secret('T');

	char classification;

	public char getClassification() {
	    return this.classification;
	}

	Classification(char classification) {
	    this.classification = classification;
	}

	public static Classification getValueOfClassification(char classification) {

	    return switch (classification) {
	    case '-' -> Classification.None;
	    case 'P', 'p' -> Classification.Public;
	    case 'U', 'u' -> Classification.Unclas;
	    case 'R', 'r' -> Classification.Restricted;
	    case 'C', 'c' -> Classification.Confidential;
	    case 'S', 's' -> Classification.Secret;
	    case 'T', 't' -> Classification.Top_Secret;
	    default -> Classification.None;
	    };
	}

	@Override
	public String toString() {
	    return String.valueOf(this.classification);
	}

    }

    public static final Classification NATO_Restricted = Classification.Restricted;
    public static final Classification NATO_Confidential = Classification.Confidential;
    public static final Classification NATO_Secret = Classification.Secret;
    public static final Classification COSMIC_Top_Secret = Classification.Top_Secret;

    public static final Classification EU_Restricted = Classification.Restricted;
    public static final Classification EU_Confidential = Classification.Confidential;
    public static final Classification EU_Secret = Classification.Secret;
    public static final Classification EU_Top_Secret = Classification.Top_Secret;

    public static final Classification Restreint_UE = Classification.Restricted;
    public static final Classification Confidentiel_UE = Classification.Confidential;
    public static final Classification Secret_UE = Classification.Secret;
    public static final Classification Tres_Secret_UE = Classification.Top_Secret;

    public static final Classification Keine = Classification.None;
    public static final Classification Oeffentlich = Classification.Public;
    public static final Classification Offen = Classification.Unclas;
    public static final Classification VS_NFD = Classification.Restricted;
    public static final Classification VS_Vertraulich = Classification.Confidential;
    public static final Classification Geheim = Classification.Secret;
    public static final Classification Streng_Geheim = Classification.Top_Secret;

    public enum Acknowledgement {

	FALSE, TRUE;
    }

    public static final HexFormat HexFormater = HexFormat.of().withUpperCase();

    public static final Pattern NAME_MATCHER = Pattern.compile("^[a-zA-Z]+$"); // Name
    public static final Pattern NUMBER_MATCHER = Pattern.compile("^[0-7][0-9A-F]$"); // Number 00-7F
    public static final Pattern TIME_MATCHER = Pattern.compile("^[A-Fa-f0-9]{8,16}$"); // Time
    public static final Pattern MAC_MATCHER = Pattern.compile("^[A-Fa-f0-9]{1,32}$"); // HexNumber
    public static final Pattern CLASSIFICATION_MATCHER = Pattern.compile("^[P,U,R,C,S,T]{1}$");
    public static final Pattern HEXNUMBER_MATCHER = SEDAPExpressMessage.MAC_MATCHER;
    public static final Pattern TEXTTYPE_MATCHER = Pattern.compile("^[0-4]$"); // Text type

    public static final Pattern DOUBLE_MATCHER = Pattern.compile("^-?\\d+.?\\d*$"); // Double
    public static final Pattern POSITIVE_DOUBLE_MATCHER = Pattern.compile("^\\d+.?\\d*$"); // Double
    public static final Pattern DOUBLE_LIST_MATCHER = Pattern.compile("^[\\d.?\\d?#]*$"); // Double
    public static final Pattern POS_INTEGER_MATCHER = Pattern.compile("^\\d+$"); // Positive Integer
    public static final Pattern INTEGER_MATCHER = Pattern.compile("^-?\\d+$"); // Integer
    public static final Pattern BIGINTEGER_MATCHER = Pattern.compile("^([A-Fa-f0-9][A-Fa-f0-9])+$"); // Hexadecimal BigInteger
    public static final Pattern YES_NO_FLAG_MATCHER = Pattern.compile("^TRUE$|^FALSE$");
    public static final Pattern ON_OFF_FLAG_MATCHER = Pattern.compile("^ON$|^OFF$");

    public static final Pattern BEARING_MATCHER = Pattern.compile("^(\\d+\\.?\\d*|[012]\\d\\d\\.\\d*|3[0-5]\\d\\\\.\\d*)$"); // 000.00-359.999
    public static final Pattern SIDC_MATCHER = Pattern.compile("^[a-zA-Z-]{15}$"); // SIDC
    public static final Pattern MMSI_MATCHER = SEDAPExpressMessage.INTEGER_MATCHER; // Integer
    public static final Pattern ICAO_MATCHER = Pattern.compile("^[A-F0-9]{1,6}$"); // ICAO
    public static final Pattern SOURCE_MATCHER = Pattern.compile("^[R,A,I,S,E,O,Y,M]+$"); // Source type
    public static final Pattern CMDTYPE_MATCHER = Pattern.compile("^[A-Fa-f0-9]+$"); // Command type
    public static final Pattern GRAPHICTYPE_MATCHER = Pattern.compile("^[0-9]$"); // Graphic type
    public static final Pattern RGBA_MATCHER = Pattern.compile("^[0-9A-F]{8}$"); // RGBA Format
    public static final Pattern TECSTATUS_MATCHER = Pattern.compile("^[0-5]$"); // TecStatus
    public static final Pattern OPSSTATUS_MATCHER = Pattern.compile("^[0-4]$"); // OpsStatus
    public static final Pattern PERCENT_MATCHER = Pattern.compile("^([A-Za-z0-9]*#(100(\\\\.0+)?|(\\d{1,2}(.\\d+)*))#*)+$"); // Percent
    public static final Pattern DATA_ENCODING_MATCHER = Pattern.compile("^BASE64$|^NONE$");
    public static final Pattern CONTENT_TYPE_MATCHER = Pattern.compile("^SEDAP$|^ASCII$|^NMEA$|^XML$|^JSON$|^BINARY$");

    public static boolean matchesPattern(Pattern pattern, String value) {
	return pattern.matcher(value).matches();
    }

    private Byte number;

    private Long time;

    private String sender;

    private Classification classification;

    private Acknowledgement acknowledgement;

    private String mac;

    public Byte getNumber() {
	return this.number;
    }

    public void setNumber(Byte number) {
	this.number = number;
    }

    public Long getTime() {
	return this.time;
    }

    public void setTime(Long time) {
	this.time = time;
    }

    public String getSender() {
	return this.sender;
    }

    public void setSender(String sender) {
	this.sender = sender;
    }

    public Classification getClassification() {
	return this.classification;
    }

    public void setClassification(Classification classification) {
	this.classification = classification;
    }

    public Acknowledgement getAcknowledgement() {
	return this.acknowledgement;
    }

    public void setAcknowledgement(Acknowledgement acknowledgement) {
	this.acknowledgement = acknowledgement;
    }

    public String getMAC() {
	return this.mac;
    }

    public void setMAC(String mac) {
	this.mac = mac;
    }

    public MessageType getMessageType() {
	return MessageType.valueOfMessageType(this.getClass().getSimpleName());
    }

    @Override
    public int compareTo(final SEDAPExpressMessage otherMessage) {

	return this.getClass().getSimpleName().compareTo(otherMessage.getClass().getSimpleName());
    }

    @Override
    public boolean equals(final Object obj) {

	return super.equals(obj);
    }

    @Override
    public int hashCode() {
	return super.hashCode();
    }

    /**
     * If one is using for instance a ComboBox with the available classifications, this method can be used to get the matching classification depending on the selected index.
     *
     * @param selectedIndex of for instance a List or a ComboBox
     * @return classification code
     */
    public static Classification getClassificationFromIndex(final int selectedIndex) {

	return switch (selectedIndex) {
	case 0 -> Classification.Public;
	case 1 -> Classification.Unclas;
	case 2 -> Classification.Restricted;
	case 3 -> Classification.Confidential;
	case 4 -> Classification.Secret;
	case 5 -> Classification.Top_Secret;
	default -> Classification.None;
	};
    }

    /**
     * Returns the higher classification of two given classifications.
     *
     * @param classification1 first classification code
     * @param classification2 second classification code
     * @return the higher classification
     */
    public static Classification getMaxClassification(final Classification classification1, final Classification classification2) {

	return switch (classification1) {
	case None, Public -> classification2;
	case Unclas -> switch (classification2) {
	case Public -> classification1;
	default -> classification2;
	};
	case Restricted -> switch (classification2) {
	case Public, Unclas -> classification1;
	default -> classification2;
	};
	case Confidential -> switch (classification2) {
	case Public, Unclas, Restricted -> classification1;
	default -> classification2;
	};
	case Secret -> switch (classification2) {
	case Public, Unclas, Restricted, Confidential -> classification1;
	default -> classification2;
	};
	case Top_Secret -> classification1;
	default -> classification1;
	};
    }

    /**
     * Returns the lower classification of two given classifications.
     *
     * @param classification1 first classification code
     * @param classification2 second classification code
     * @return the lower classification
     */
    public static Classification getMinClassification(final Classification classification1, final Classification classification2) {

	return switch (classification1) {
	case Public -> classification1;
	case Unclas -> switch (classification2) {
	case Public -> classification2;
	default -> classification1;
	};
	case Restricted -> switch (classification2) {
	case Public, Unclas -> classification2;
	default -> classification1;
	};
	case Confidential -> switch (classification2) {
	case Public, Unclas, Restricted -> classification2;
	default -> classification1;
	};
	case Secret -> switch (classification2) {
	case Public, Unclas, Restricted, Confidential -> classification2;
	default -> classification1;
	};
	case Top_Secret -> classification2;
	default -> classification2;
	};
    }

    /**
     * Returns the textual representation of the given classification code.
     *
     * @param classification
     * @return the matching textual representation
     */
    public static String getClassificationFullNameFromClassification(final Classification classification) {

	return switch (classification) {
	case Public -> "public";
	case Unclas -> "unclas";
	case Restricted -> "restricted";
	case Confidential -> "confidential";
	case Secret -> "secret";
	case Top_Secret -> "top secret";
	default -> "-";
	};
    }

    /**
     * Returns the classification code from a given textual representation
     *
     * @param classificationFullName Textual classification
     * @return the matching classification code
     */
    public static Classification getClassificationFromFullName(final String classificationFullName) {

	return switch (classificationFullName.trim().toUpperCase().replace("NATO", "").replace("EU", "").replace(" ", "")) {
	case "OEFFENTLICH", "PUBLIC" -> Classification.Public;
	case "OFFEN", "UNCLAS" -> Classification.Unclas;
	case "VS-NFD", "RESTRICTED" -> Classification.Restricted;
	case "VS-VERTRAULICH", "CONFIDENTIAL" -> Classification.Confidential;
	case "GEHEIM", "VS-GEHEIM", "SECRET" -> Classification.Secret;
	case "STRENGGEHEIM", "VS-STRENGGEHEIM", "TOP SECRET" -> Classification.Top_Secret;
	default -> Classification.None;
	};
    }

    /**
     * Compares two classifications with each other and returns whether the given classification is within the maximum permissible classification range.
     *
     * @param classification    classification to text
     * @param maxClassification Maximal permitted classification
     * @return true=classification permitted, false=classification NOT permitted
     */
    public static boolean isClassificationIncluded(final Classification classification, final Classification maxClassification) {

	return switch (classification) {
	case None -> false;
	case Public -> true; // Always permitted
	case Unclas -> (maxClassification == Classification.Unclas) || (maxClassification == Classification.Restricted) || (maxClassification == Classification.Confidential) || (maxClassification == Classification.Secret)
		|| (maxClassification == Classification.Top_Secret);
	case Restricted -> (maxClassification == Classification.Restricted) || (maxClassification == Classification.Confidential) || (maxClassification == Classification.Secret) || (maxClassification == Classification.Top_Secret);
	case Confidential -> (maxClassification == Classification.Confidential) || (maxClassification == Classification.Secret) || (maxClassification == Classification.Top_Secret);
	case Secret -> (maxClassification == Classification.Secret) || (maxClassification == Classification.Top_Secret);
	case Top_Secret -> maxClassification == Classification.Top_Secret;
	default -> throw new IllegalArgumentException("Unexpected value: " + classification);
	};
    }

    /**
     * Split a serialized SEDAP-Express message into a list if values.
     *
     * @param message SEDAP-Express message
     * @return Array with the content of the SEDAP-Express message
     */
    public static List<String> splitMessage(final String message) {

	final var sb = new StringBuilder();
	final var words = new ArrayList<String>();
	words.ensureCapacity(6); // Minimal size
	final var strArray = message.toCharArray();
	for (final char c : strArray) {
	    if (c == ';') {
		if (sb.length() != 0) {
		    words.add(sb.toString());
		    sb.delete(0, sb.length());
		} else {
		    words.add("");
		}
	    } else {
		sb.append(c);
	    }
	}

	if (sb.length() != 0) {
	    words.add(sb.toString());
	} else {
	    words.add("");
	}

	return words;
    }

    /**
     * Splits a #-list in a serialized SEDAP-Express message into a list if string values.
     *
     * @param list #-list
     * @return Array with the content of the #-list
     */
    public static List<String> splitStringDataHashTag(final String list) {

	final var sb = new StringBuilder();
	final var words = new ArrayList<String>();
	final var strArray = list.toCharArray();
	for (final char c : strArray) {
	    if (c == '#') {
		if (sb.length() != 0) {
		    words.add(sb.toString());
		    sb.delete(0, sb.length());
		} else {
		    words.add(null);
		}
	    } else {
		sb.append(c);
	    }
	}

	if (sb.length() != 0) {
	    words.add(sb.toString());
	} else {
	    words.add(null);
	}

	return words;
    }

    /**
     * Splits a #-list in a serialized SEDAP-Express message into a list if integer values.
     *
     * @param list #-list
     * @return Array with the content of the #-list
     */
    public static List<Integer> splitIntegerDataHashTag(final String list) {

	final var sb = new StringBuilder();
	final var words = new ArrayList<Integer>();
	final var strArray = list.toCharArray();
	for (final char c : strArray) {
	    if (c == '#') {
		if (sb.length() != 0) {
		    words.add(Integer.valueOf(sb.toString()));
		    sb.delete(0, sb.length());
		} else {
		    words.add(null);
		}
	    } else {
		sb.append(c);
	    }
	}

	if (sb.length() != 0) {
	    words.add(Integer.valueOf(sb.toString()));
	} else {
	    words.add(null);
	}

	return words;
    }

    /**
     * Splits a #-list in a serialized SEDAP-Express message into a list if double values.
     *
     * @param list #-list
     * @return Array with the content of the #-list
     */
    public static List<Double> splitDoubleDataHashTag(final String list) {

	final var sb = new StringBuilder();
	final var words = new ArrayList<Double>();
	final var strArray = list.toCharArray();
	for (final char c : strArray) {
	    if (c == '#') {
		if (sb.length() != 0) {
		    words.add(Double.valueOf(sb.toString()));
		    sb.delete(0, sb.length());
		} else {
		    words.add(null);
		}
	    } else {
		sb.append(c);
	    }
	}

	if (sb.length() != 0) {
	    words.add(Double.valueOf(sb.toString()));
	} else {
	    words.add(null);
	}

	return words;
    }

    /**
     *
     * @param list List of elements
     * @return String with #-separated list elements
     */
    public String getStringFromList(List<?> list) {

	StringBuilder str = new StringBuilder();
	list.forEach(freq -> str.append('#').append(freq));
	str.deleteCharAt(0);

	return str.toString();
    }

    /**
     *
     */
    private static final ConcurrentHashMap<String, Constructor<?>> messageNameToConstructor = new ConcurrentHashMap<>();
    static {

	final Class<?>[] paramTypes = new Class[] { Iterator.class };

	Arrays.asList(

		ACKNOWLEDGE.class, COMMAND.class, CONTACT.class, EMISSION.class, GENERIC.class, GRAPHIC.class, HEARTBEAT.class, TIMESYNC.class, KEYEXCHANGE.class, METEO.class, OWNUNIT.class, RESEND.class, STATUS.class, TEXT.class)
		.forEach(clazz -> {
		    try {
			SEDAPExpressMessage.messageNameToConstructor.put(clazz.getSimpleName(), clazz.getConstructor(paramTypes));
		    } catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		    }
		});
    }

    static {
	System.out.println("""
			\n
		***************************************************
		***************************************************
		** SEDAP-Express v1.0 (Juni 12th, 2025)          **
		** Licensed under the BSD-2-clause License       **
		** (C)2024-2025, Federal Armed Forces of Germany **
		***************************************************
		***************************************************\n""");
    }

    /**
     *
     * @param number
     * @param time
     * @param sender
     * @param classification
     * @param acknowledgement
     * @param mac             Message Authentification Code
     */
    protected SEDAPExpressMessage(Byte number, Long time, String sender, Classification classification, Acknowledgement acknowledgement, String mac) {
	super();
	this.number = number;
	this.time = time;
	this.sender = sender;
	this.classification = classification;
	this.acknowledgement = acknowledgement;
	this.mac = mac;
    }

    /**
     * Deserializes the header of a serialized SEDAP-Express message.
     *
     * @param message
     */
    protected SEDAPExpressMessage(Iterator<String> message) {

	try {

	    String value;
	    if (message.hasNext()) {
		value = message.next();
		if (value.isEmpty()) {
		    SEDAPExpressMessage.logger.logp(Level.INFO, "SEDAPExpressMessage", "SEDAPExpressMessage(Iterator<String> message)", "Optional field \"number\" is empty!", value);
		} else if (SEDAPExpressMessage.matchesPattern(SEDAPExpressMessage.NUMBER_MATCHER, value)) {
		    this.number = Byte.parseByte(value, 16);
		} else if (!value.isBlank()) {
		    SEDAPExpressMessage.logger.logp(Level.SEVERE, "SEDAPExpressMessage", "SEDAPExpressMessage(Iterator<String> message)", "Optional field \"number\" contains invalid value!", value);
		}
	    } else {
		SEDAPExpressMessage.logger.logp(Level.SEVERE, "SEDAPExpressMessage", "SEDAPExpressMessage(Iterator<String> message)", "Incomplete message!");
	    }

	    if (message.hasNext()) {
		value = message.next();
		if (value.isEmpty()) {
		    SEDAPExpressMessage.logger.logp(Level.INFO, "SEDAPExpressMessage", "SEDAPExpressMessage(Iterator<String> message)", "Optional field \"time\" is empty!", value);
		} else if (SEDAPExpressMessage.matchesPattern(SEDAPExpressMessage.TIME_MATCHER, value)) {
		    this.time = Long.parseLong(value, 16);
		} else if (!value.isBlank()) {
		    SEDAPExpressMessage.logger.logp(Level.SEVERE, "SEDAPExpressMessage", "SEDAPExpressMessage(Iterator<String> message)", "Optional field \"time\" contains invalid value!", value);
		}
	    } else {
		SEDAPExpressMessage.logger.logp(Level.SEVERE, "SEDAPExpressMessage", "SEDAPExpressMessage(Iterator<String> message)", "Incomplete message!");
	    }

	    if (message.hasNext()) {
		value = message.next();
		if (value.isBlank()) {
		    SEDAPExpressMessage.logger.logp(Level.INFO, "SEDAPExpressMessage", "SEDAPExpressMessage(Iterator<String> message)", "Optional field \"sender\" is empty!");
		} else {
		    this.sender = value;
		}
	    } else {
		SEDAPExpressMessage.logger.logp(Level.SEVERE, "SEDAPExpressMessage", "SEDAPExpressMessage(Iterator<String> message)", "Incomplete message!");
	    }

	    if (message.hasNext()) {
		String classChar = message.next().trim();
		if (!classChar.isBlank()) {
		    this.classification = Classification.getValueOfClassification(classChar.charAt(0));
		}
	    } else {
		SEDAPExpressMessage.logger.logp(Level.SEVERE, "SEDAPExpressMessage", "SEDAPExpressMessage(Iterator<String> message)", "Incomplete message!");
	    }

	    if (message.hasNext()) {
		value = message.next();
		if (SEDAPExpressMessage.matchesPattern(SEDAPExpressMessage.YES_NO_FLAG_MATCHER, value)) {
		    this.acknowledgement = Acknowledgement.valueOf(value);
		} else if (value.isBlank()) {
		    this.acknowledgement = Acknowledgement.FALSE;
		} else {
		    SEDAPExpressMessage.logger.severe("Optional field \"acknowledgement\" invalid value: \"" + value + "\"");
		}
	    } else if (this instanceof HEARTBEAT) {
		// incomplete message allowed
	    } else {
		SEDAPExpressMessage.logger.logp(Level.SEVERE, "SEDAPExpressMessage", "SEDAPExpressMessage(Iterator<String> message)", "Incomplete message!");
	    }

	    if (message.hasNext()) {
		value = message.next();
		if (SEDAPExpressMessage.matchesPattern(SEDAPExpressMessage.MAC_MATCHER, value)) {
		    this.mac = value;
		} else if (!value.isBlank()) {
		    SEDAPExpressMessage.logger.logp(Level.INFO, "SEDAPExpressMessage", "SEDAPExpressMessage(Iterator<String> message)", "Optional field \"mac\" contains not a valid 32bit mac number!", value);
		}
	    } else if (this instanceof HEARTBEAT) {
		// incomplete message allowed
	    } else {
		SEDAPExpressMessage.logger.logp(Level.SEVERE, "SEDAPExpressMessage", "SEDAPExpressMessage(Iterator<String> message)", "Incomplete message!");
	    }

	} catch (Exception e) {
	    e.printStackTrace();

	}
    }

    /*
     *
     */
    protected SEDAPExpressMessage() {
	// abstract, hide constructor
    }

    /**
     * Deserializes a SEDAP-Express message and return the matching concrete implementation object.
     *
     * @param receivedMessage serialized SEDAP-Message
     *
     * @return deserialized SEDAP-Message object
     */
    public static SEDAPExpressMessage deserialize(String receivedMessage) {

	final Iterator<String> messageArray = SEDAPExpressMessage.splitMessage(receivedMessage).iterator();

	if (messageArray.hasNext()) {

	    try {

		Constructor<?> constructor = SEDAPExpressMessage.messageNameToConstructor.get(messageArray.next());

		if (constructor != null) {
		    return (SEDAPExpressMessage) constructor.newInstance(messageArray);
		} else {
		    return null;
		}

	    } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
		SEDAPExpressMessage.logger.throwing("SEDAPExpressMessage", "deserialize(String receivedMessage)", e);
		return null;
	    }

	} else {
	    return null;
	}
    }

    /**
     * Serializes a SEDAP-Express message and return a String object.
     *
     * @param message SEDAP-Message object
     *
     * @return SEDAP-Express message serialized as String object
     */
    public static String serialize(SEDAPExpressMessage message) {
	return message.toString() + '\n';
    }

    protected StringBuilder serializeHeader() {

	StringBuilder result = new StringBuilder(this.getClass().getSimpleName()).append(';');

	if (this.number != null) {
	    result.append(SEDAPExpressMessage.HexFormater.toHexDigits(this.number));
	}
	result.append(';');

	if (this.time != null) {
	    result.append(SEDAPExpressMessage.HexFormater.toHexDigits(this.time, 12));
	}
	result.append(';');

	if (this.sender != null) {
	    result.append(this.sender);
	}
	result.append(';');

	if (this.classification != null) {
	    result.append(this.classification.toString());
	}
	result.append(';');

	if ((this.acknowledgement != null) && (this.acknowledgement == Acknowledgement.TRUE)) {
	    result.append(this.acknowledgement);
	}
	result.append(';');

	if (this.mac != null) {
	    result.append(this.mac);
	}

	result.append(';');

	return result;
    }

    /**
     * Remove useless semicolons
     *
     * @param message Original message
     * @return Shortend message
     */
    public static String removeSemicolons(String message) {
	while (message.endsWith(";")) {
	    message = message.substring(0, message.length() - 1);
	}
	return message;
    }
}
