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
import java.util.List;
import java.util.logging.Level;

import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.DecoderException;

/**
 * 
 * @author Volker Voß
 *
 */
public class GRAPHIC extends SEDAPExpressMessage {

    private static final long serialVersionUID = 341163682644789657L;

    public enum GraphicType {

	Point(0), Path(1), Polygon(2), Rectangle(3), Square(4), Circle(5), Ellipse(6), Block(7), Sphere(8), Ellipsoid(9);

	int type;

	public int getType() {
	    return this.type;
	}

	private GraphicType(int type) {
	    this.type = type;
	}

	public static GraphicType valueOfGraphicType(int type) {

	    return switch (type) {
	    case 0 -> Point;
	    case 1 -> Path;
	    case 2 -> Polygon;
	    case 3 -> Rectangle;
	    case 4 -> Circle;
	    case 5 -> Ellipse;
	    case 6 -> Block;
	    case 7 -> Sphere;
	    case 8 -> Ellipsoid;
	    default -> Point;
	    };
	}

	@Override
	public String toString() {
	    return String.valueOf(this.type);
	}
    }

    public interface GraphicObject {
    };

    public record Point(double latitude, double longitude, double laltitude) implements GraphicObject {
    };

    public record Path(double latitude, double longitude, double altitude) implements GraphicObject {
    };

    public record Polygon(double latitude, double longitude, double altitude) implements GraphicObject {
    };

    public record Rectangle(double rotationAngle, List<Double[]> latLonAlt) implements GraphicObject {
    };

    public record Square(double latitude, double longitude, double altitude, double radiusX, double radiusY) implements GraphicObject {
    };

    public record Circle(double radius, double latitude, double longitude, double altitude) implements GraphicObject {
    };

    public record Ellipse(double radiusX, double radiusY, double centerLatitude, double centerLongitude, double centerAltitude) implements GraphicObject {
    };

    public record Block(double latitude, double longitude, double altitude, double xRadius, double yRadius, double zRadius) implements GraphicObject {
    };

    public record Sphere(double latitude, double longitude, double altitude) implements GraphicObject {
    };

    public record Ellipsoid(double centerLatitude, double centerLongitude, double centerAltitude) implements GraphicObject {
    };

    private GraphicType graphicType;

    private Double lineWidth;

    private Integer lineColor;

    private Integer fillColor;

    private Integer textColor;

    private DataEncoding encoding;

    private String annotation;

    private GraphicObject graphicObject;

    public GraphicType getGraphicType() {
	return this.graphicType;
    }

    public void setGraphicType(GraphicType graphicType) {
	this.graphicType = graphicType;
    }

    public Double getLineWidth() {
	return this.lineWidth;
    }

    public void setLineWidth(Double lineWidth) {
	this.lineWidth = lineWidth;
    }

    public Integer getLineColor() {
	return this.lineColor;
    }

    public void setLineColor(Integer lineColor) {
	this.lineColor = lineColor;
    }

    public Integer getFillColor() {
	return this.fillColor;
    }

    public void setFillColor(Integer fillColor) {
	this.fillColor = fillColor;
    }

    public Integer getTextColor() {
	return this.textColor;
    }

    public void setTextColor(Integer textColor) {
	this.textColor = textColor;
    }

    public DataEncoding getEncoding() {
	return this.encoding;
    }

    public void setEncoding(DataEncoding encoding) {
	this.encoding = encoding;
    }

    public String getAnnotation() {
	return this.annotation;
    }

    public void setAnnotation(String annotation) {
	this.annotation = annotation;
    }

    public GraphicObject getGraphicObject() {
	return this.graphicObject;
    }

    public void setGraphicObject(GraphicObject graphicObject) {
	this.graphicObject = graphicObject;
    }

    /**
     * Instantiate a new GRAPHIC message
     * 
     * @param number
     * @param time
     * @param sender
     * @param classification
     * @param acknowledgement
     * @param mac
     * @param graphicType
     * @param lineWidth
     * @param lineColor
     * @param fillColor
     * @param textColor
     * @param encoding
     * @param annotation
     * @param graphicObject
     */
    public GRAPHIC(Byte number, Long time, String sender, Classification classification, Acknowledgement acknowledgement, String mac,
	    GraphicType graphicType, Double lineWidth, Integer lineColor, Integer fillColor, Integer textColor, DataEncoding encoding, String annotation, GraphicObject graphicObject) {

	super(number, time, sender, classification, acknowledgement, mac);

	this.graphicType = graphicType;
	this.lineWidth = lineWidth;
	this.lineColor = lineColor;
	this.fillColor = fillColor;
	this.textColor = textColor;
	this.encoding = encoding;
	this.annotation = annotation;
	this.graphicObject = graphicObject;
    }

    /**
     * Instantiate a new GRAPHIC message
     * 
     * @param number
     * @param time
     * @param sender
     * @param classification
     * @param acknowledgement
     * @param mac
     * @param graphicType
     * @param lineWidth
     * @param lineColor
     * @param fillColor
     * @param textColor
     * @param encoding
     * @param annotation
     */
    public GRAPHIC(Byte number, Long time, String sender, Classification classification, Acknowledgement acknowledgement, String mac,
	    GraphicType graphicType, Double lineWidth, Integer lineColor, Integer fillColor, Integer textColor, DataEncoding encoding, String annotation) {

	this(number, time, sender, classification, acknowledgement, mac,
		graphicType, lineWidth, lineColor, fillColor, textColor, encoding, annotation, null);
    }

    /**
     *
     * @param message
     */
    public GRAPHIC(String message) {

	this(SEDAPExpressMessage.splitMessage(message.substring(message.indexOf(';') + 1)).iterator());
    }

    public GRAPHIC(Iterator<String> message) {

	super(message);

	String value;

	// GraphicType
	if (message.hasNext()) {
	    value = message.next();
	    if (SEDAPExpressMessage.matchesPattern(SEDAPExpressMessage.GRAPHICTYPE_MATCHER, value)) {
		this.graphicType = GraphicType.valueOf(value);
	    } else if (value.isBlank()) {
		SEDAPExpressMessage.logger.logp(Level.SEVERE, "GRAPHIC", "GRAPHIC(Iterator<String> message)", "Mandatory field \"graphicType\" is empty!", value);
	    } else {
		SEDAPExpressMessage.logger.logp(Level.SEVERE, "GRAPHIC", "GRAPHIC(Iterator<String> message)", "Mandatory field \"graphicType\" contains invalid value!", value);
	    }
	} else {
	    SEDAPExpressMessage.logger.logp(Level.SEVERE, "GRAPHIC", "GRAPHIC(Iterator<String> message)", "Incomplete message!");
	}

	// LineWidth
	if (message.hasNext()) {
	    value = message.next();
	    if (SEDAPExpressMessage.matchesPattern(SEDAPExpressMessage.INTEGER_MATCHER, value)) {
		this.lineWidth = Double.parseDouble(value);
	    } else if (!value.isBlank()) {
		SEDAPExpressMessage.logger.logp(Level.WARNING, "GRAPHIC", "GRAPHIC(Iterator<String> message)", "Optional field \"lineWidth\" contains invalid value!", value);
	    }
	}

	// LineColor
	if (message.hasNext()) {
	    value = message.next();
	    if (SEDAPExpressMessage.matchesPattern(SEDAPExpressMessage.RGBA_MATCHER, value)) {
		this.lineColor = Integer.parseInt(value, 16);
	    } else if (!value.isBlank()) {
		SEDAPExpressMessage.logger.logp(Level.WARNING, "GRAPHIC", "GRAPHIC(Iterator<String> message)", "Optional field \"lineColor\" contains invalid value!", value);
	    }
	}

	// FillColor
	if (message.hasNext()) {
	    value = message.next();
	    if (SEDAPExpressMessage.matchesPattern(SEDAPExpressMessage.RGBA_MATCHER, value)) {
		this.fillColor = Integer.parseInt(value, 16);
	    } else if (!value.isBlank()) {
		SEDAPExpressMessage.logger.logp(Level.WARNING, "GRAPHIC", "GRAPHIC(Iterator<String> message)", "Optional field \"fillColor\" contains invalid value!", value);
	    }
	}

	// TextColor
	if (message.hasNext()) {
	    value = message.next();
	    if (SEDAPExpressMessage.matchesPattern(SEDAPExpressMessage.RGBA_MATCHER, value)) {
		this.textColor = Integer.parseInt(value, 16);
	    } else if (!value.isBlank()) {
		SEDAPExpressMessage.logger.logp(Level.WARNING, "GRAPHIC", "GRAPHIC(Iterator<String> message)", "Optional field \"textColor\" contains invalid value!", value);
	    }
	}

	// Encoding
	if (message.hasNext()) {
	    value = message.next();
	    if (DataEncoding.valueOf(value) == DataEncoding.BASE64) {
		this.encoding = DataEncoding.BASE64;
	    } else if (DataEncoding.valueOf(value) == DataEncoding.NONE || value.isBlank()) {
		this.encoding = DataEncoding.NONE;
	    } else {
		this.encoding = DataEncoding.NONE;
		SEDAPExpressMessage.logger.logp(Level.SEVERE, "GRAPHIC", "GRAPHIC(Iterator<String> message)", "Optional field \"encoding\" contains invalid value!", value);
	    }
	}

	// Text
	if (message.hasNext()) {
	    value = message.next();
	    if (value.isBlank()) {
		SEDAPExpressMessage.logger.logp(Level.WARNING, "GRAPHIC", "GRAPHIC(Iterator<String> message)", "Optional field \"text\" is empty!");
	    } else {
		if (this.encoding == DataEncoding.BASE64) {
		    try {
			this.annotation = new String(Base64.decode(value));
		    } catch (DecoderException e) {
			SEDAPExpressMessage.logger.logp(Level.SEVERE, "GRAPHIC", "GRAPHIC(Iterator<String> message)", "Optional field \"annotation\" could not be decoded from Base64!");
		    }

		} else {
		    this.annotation = value;
		}
	    }
	}

	// Variable part

    }

    @Override
    public boolean equals(Object obj) {

	if (obj == null) {
	    return false;
	} else if (!(obj instanceof GRAPHIC)) {
	    return false;
	} else {
	    return super.equals(obj) && (this.graphicType == (((GRAPHIC) obj).graphicType)) && (this.lineWidth == (((GRAPHIC) obj).lineWidth))
		    && (this.lineColor == (((GRAPHIC) obj).lineColor))
		    && (this.fillColor == (((GRAPHIC) obj).fillColor))
		    && (this.textColor == (((GRAPHIC) obj).textColor)) &&
		    (((this.encoding == null) && (((GRAPHIC) obj).encoding == null)) || ((this.encoding != null) && this.encoding.equals(((GRAPHIC) obj).encoding))) &&

		    (((this.annotation == null) && (((GRAPHIC) obj).annotation == null)) || ((this.annotation != null) && this.annotation.equals(((GRAPHIC) obj).annotation)));

	}
    }

    @Override
    public int hashCode() {
	return super.hashCode();
    }

    @Override
    public String toString() {

	return SEDAPExpressMessage.removeSemicolons(
		serializeHeader().append((this.graphicType != null) ? this.graphicType : "").append(";").append((this.lineWidth != null) ? SEDAPExpressMessage.numberFormatter.format(this.lineWidth) : "")
			.append(";").append((this.lineColor != null) ? this.lineColor : "").append(";").append((this.fillColor != null) ? this.fillColor : "").append(";")
			.append((this.textColor != null) ? this.textColor : "").append(";")
			.append((this.encoding != null) ? this.encoding : "").append(";")
			.append((this.annotation != null) ? ((this.encoding == DataEncoding.BASE64) ? Base64.toBase64String(this.annotation.getBytes()) : this.annotation) : "").toString());
    }

}
