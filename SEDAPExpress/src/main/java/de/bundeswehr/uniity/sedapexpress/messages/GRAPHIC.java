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
 * FITNESS FOR A PARTICULAR PURPOSEnARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGE.
 */
package de.bundeswehr.uniity.sedapexpress.messages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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

	Point(0x00),
	Path(0x01),
	Polygon(0x02),
	Rectangle(0x03),
	Square(0x04),
	Circle(0x05),
	Ellipse(0x06),
	Block(0x07),
	Sphere(0x08),
	Ellipsoid(0x09),
	SensorFieldOfView(0x0A),
	WeaponFieldOfFire(0x0B);

	private final int type;
	private static final Map<Integer, GraphicType> LOOKUP = new HashMap<>();

	static {
	    for (GraphicType gt : GraphicType.values()) {
		GraphicType.LOOKUP.put(gt.type, gt);
	    }
	}

	GraphicType(int type) {
	    this.type = type;
	}

	public int getType() {
	    return this.type;
	}

	/**
	 * Sucht den GraphicType anhand des Integer-Werts.
	 * 
	 * @return Der passende Typ oder Point als Standardwert.
	 */
	public static GraphicType valueOfGraphicType(int type) {
	    return GraphicType.LOOKUP.getOrDefault(type, Point);
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

    public record Path(List<double[]> latLonAlt) implements GraphicObject {
    };

    public record Polygon(List<double[]> latLonAlt) implements GraphicObject {
    };

    public record Rectangle(double latitude, double longitude, double altitude,
	    double width, double length, double rotation) implements GraphicObject {
    };

    public record Square(double latitude, double longitude, double altitude,
	    double width, double rotation) implements GraphicObject {
    };

    public record Circle(double latitude, double longitude, double altitude,
	    double radius, double startAngle, double endAngle) implements GraphicObject {
    };

    public record Ellipse(double centerLatitude, double centerLongitude, double centerAltitude,
	    double radiusX, double radiusY, double rotation) implements GraphicObject {
    };

    public record Block(double latitude, double longitude, double altitude,
	    double width, double length, double height,
	    double rotationX, double rotationY, double rotationZ) implements GraphicObject {
    };

    public record Sphere(double latitude, double longitude, double altitude,
	    double radius) implements GraphicObject {
    };

    public record Ellipsoid(double centerLatitude, double centerLongitude, double centerAltitude,
	    double radiusX, double radiusY, double radiusZ,
	    double rotationX, double rotationY, double rotationZ) implements GraphicObject {
    };

    public record SensorFieldOfView(double azimuth, double elevation, List<double[]> latLonAlt) implements GraphicObject {
    };

    public record WeaponFieldOfFire(double azimuth, double elevation, List<double[]> latLonAlt) implements GraphicObject {
    };

    private String graphicID;

    private DeleteFlag deleteFlag;

    private GraphicType graphicType;

    private GraphicObject graphicObject;

    private Double lineWidth;

    private Long lineColor;

    private Long fillColor;

    private Long textColor;

    private DataEncoding encoding;

    private String annotation;

    public String getGraphicID() {
	return this.graphicID;
    }

    public void setGraphicID(String graphicID) {
	this.graphicID = graphicID;
    }

    public DeleteFlag getDeleteFlag() {
	return this.deleteFlag;
    }

    public void setDeleteFlag(DeleteFlag deleteFlag) {
	this.deleteFlag = deleteFlag;
    }

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

    public Long getLineColor() {
	return this.lineColor;
    }

    public void setLineColor(Long lineColor) {
	this.lineColor = lineColor;
    }

    public Long getFillColor() {
	return this.fillColor;
    }

    public void setFillColor(Long fillColor) {
	this.fillColor = fillColor;
    }

    public Long getTextColor() {
	return this.textColor;
    }

    public void setTextColor(Long textColor) {
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
     * Instantiate a new default GRAPHIC message
     */
    public GRAPHIC() {

	super(null, null, null, null, null, null);

	this.graphicType = null;
	this.lineWidth = null;
	this.lineColor = null;
	this.fillColor = null;
	this.textColor = null;
	this.encoding = null;
	this.annotation = null;
	this.graphicObject = null;
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
	    GraphicType graphicType, Double lineWidth, Long lineColor, Long fillColor, Long textColor, DataEncoding encoding, String annotation) {

	super(number, time, sender, classification, acknowledgement, mac);

	this.graphicType = graphicType;
	this.lineWidth = lineWidth;
	this.lineColor = lineColor;
	this.fillColor = fillColor;
	this.textColor = textColor;
	this.encoding = encoding;
	this.annotation = annotation;
	this.graphicObject = null;
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
     * @param lineWidth
     * @param lineColor
     * @param fillColor
     * @param textColor
     * @param encoding
     * @param annotation
     * @param graphicObject
     */
    public GRAPHIC(Byte number, Long time, String sender, Classification classification, Acknowledgement acknowledgement, String mac,
	    Double lineWidth, Long lineColor, Long fillColor, Long textColor, DataEncoding encoding, String annotation, GraphicObject graphicObject) {

	super(number, time, sender, classification, acknowledgement, mac);

	this.lineWidth = lineWidth;
	this.lineColor = lineColor;
	this.fillColor = fillColor;
	this.textColor = textColor;
	this.encoding = encoding;
	this.annotation = annotation;
	this.graphicObject = graphicObject;

	if (graphicObject instanceof Point) {
	    this.graphicType = GraphicType.Point;
	} else if (graphicObject instanceof Path) {
	    this.graphicType = GraphicType.Path;
	} else if (graphicObject instanceof Polygon) {
	    this.graphicType = GraphicType.Polygon;
	} else if (graphicObject instanceof Rectangle) {
	    this.graphicType = GraphicType.Rectangle;
	} else if (graphicObject instanceof Square) {
	    this.graphicType = GraphicType.Square;
	} else if (graphicObject instanceof Circle) {
	    this.graphicType = GraphicType.Circle;
	} else if (graphicObject instanceof Ellipse) {
	    this.graphicType = GraphicType.Ellipse;
	} else if (graphicObject instanceof Block) {
	    this.graphicType = GraphicType.Block;
	} else if (graphicObject instanceof Sphere) {
	    this.graphicType = GraphicType.Sphere;
	} else if (graphicObject instanceof Ellipsoid) {
	    this.graphicType = GraphicType.Ellipsoid;
	} else if (graphicObject instanceof SensorFieldOfView) {
	    this.graphicType = GraphicType.SensorFieldOfView;
	} else if (graphicObject instanceof WeaponFieldOfFire) {
	    this.graphicType = GraphicType.WeaponFieldOfFire;
	} else {
	    this.graphicType = GraphicType.Point; // Fallback for unknown objects
	}

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

	// GraphicID
	if (message.hasNext()) {
	    this.graphicID = message.next();
	    if (this.graphicID.isBlank()) {
		SEDAPExpressMessage.logger.logp(Level.SEVERE, "GRAPHIC", "GRAPHIC(Iterator<String> message)", "Mandatory field \"graphicID\" is empty!");
	    }
	} else {
	    SEDAPExpressMessage.logger.logp(Level.SEVERE, "GRAPHIC", "GRAPHIC(Iterator<String> message)", "Incomplete message!");
	}

	// DeleteFlag
	if (message.hasNext()) {
	    value = message.next();
	    if (SEDAPExpressMessage.matchesPattern(SEDAPExpressMessage.YES_NO_FLAG_MATCHER, value)) {
		this.deleteFlag = DeleteFlag.valueOf(value);
	    } else if (value.isBlank()) {
		this.deleteFlag = DeleteFlag.FALSE;
	    } else {
		SEDAPExpressMessage.logger.logp(Level.SEVERE, "GRAPHIC", "GRAPHIC(Iterator<String> message)", "Mandatory field \"deleteFlag\" invalid value: \"" + value + "\"");
	    }
	} else {
	    SEDAPExpressMessage.logger.logp(Level.SEVERE, "GRAPHIC", "GRAPHIC(Iterator<String> message)", "Incomplete message!");
	}

	// GraphicType
	if (message.hasNext()) {
	    value = message.next();
	    if (SEDAPExpressMessage.matchesPattern(SEDAPExpressMessage.GRAPHICTYPE_MATCHER, value)) {
		this.graphicType = GraphicType.valueOfGraphicType(Integer.parseInt(value));
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
		this.lineColor = Long.parseLong(value, 16);
	    } else if (!value.isBlank()) {
		SEDAPExpressMessage.logger.logp(Level.WARNING, "GRAPHIC", "GRAPHIC(Iterator<String> message)", "Optional field \"lineColor\" contains invalid value!", value);
	    }
	}

	// FillColor
	if (message.hasNext()) {
	    value = message.next();
	    if (SEDAPExpressMessage.matchesPattern(SEDAPExpressMessage.RGBA_MATCHER, value)) {
		this.fillColor = Long.parseLong(value, 16);
	    } else if (!value.isBlank()) {
		SEDAPExpressMessage.logger.logp(Level.WARNING, "GRAPHIC", "GRAPHIC(Iterator<String> message)", "Optional field \"fillColor\" contains invalid value!", value);
	    }
	}

	// TextColor
	if (message.hasNext()) {
	    value = message.next();
	    if (SEDAPExpressMessage.matchesPattern(SEDAPExpressMessage.RGBA_MATCHER, value)) {
		this.textColor = Long.parseLong(value, 16);
	    } else if (!value.isBlank()) {
		SEDAPExpressMessage.logger.logp(Level.WARNING, "GRAPHIC", "GRAPHIC(Iterator<String> message)", "Optional field \"textColor\" contains invalid value!", value);
	    }
	}

	// Encoding
	if (message.hasNext()) {
	    value = message.next();
	    if (value.isBlank()) {
		this.encoding = DataEncoding.NONE;
	    } else if (DataEncoding.valueOf(value) == DataEncoding.BASE64) {
		this.encoding = DataEncoding.BASE64;
	    } else if (DataEncoding.valueOf(value) == DataEncoding.NONE || value.isBlank()) {
		this.encoding = DataEncoding.NONE;
	    } else {
		this.encoding = DataEncoding.NONE;
		SEDAPExpressMessage.logger.logp(Level.SEVERE, "GRAPHIC", "GRAPHIC(Iterator<String> message)", "Optional field \"encoding\" contains invalid value!", value);
	    }
	}

	// Annotation
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
	if (!message.hasNext()) {
	    SEDAPExpressMessage.logger.logp(Level.SEVERE, "GRAPHIC", "GRAPHIC(Iterator<String> message)", "Incomplete message!");
	} else {

	    List<String> strList = new ArrayList<String>();
	    message.forEachRemaining(strList::add);
	    String[] parts = strList.toArray(String[]::new);

	    switch (this.graphicType) {

	    case Point -> {
		if (parts.length == 3) {
		    this.graphicObject = new Point(Double.parseDouble(parts[0]), Double.parseDouble(parts[1]), Double.parseDouble(parts[2]));
		} else {
		    SEDAPExpressMessage.logger.logp(Level.SEVERE, "GRAPHIC", "GRAPHIC(Iterator<String> message)", "Incomplete Point values!");
		}
	    }

	    case Path -> {
		if (parts.length == 1) {
		    this.graphicObject = new Path(SEDAPExpressMessage.splitDoubleArrayDataHashTag(parts[0]));
		} else {
		    SEDAPExpressMessage.logger.logp(Level.SEVERE, "GRAPHIC", "GRAPHIC(Iterator<String> message)", "Incomplete Path values!");
		}
	    }

	    case Polygon -> {
		if (parts.length == 1) {
		    this.graphicObject = new Polygon(SEDAPExpressMessage.splitDoubleArrayDataHashTag(parts[0]));
		} else {
		    SEDAPExpressMessage.logger.logp(Level.SEVERE, "GRAPHIC", "GRAPHIC(Iterator<String> message)", "Incomplete additional Polygon values!");
		}
	    }

	    case Rectangle -> {
		if (parts.length == 6) {
		    this.graphicObject = new Rectangle(Double.parseDouble(parts[0]), Double.parseDouble(parts[1]), Double.parseDouble(parts[2]),
			    Double.parseDouble(parts[3]), Double.parseDouble(parts[4]), Double.parseDouble(parts[5]));
		} else {
		    SEDAPExpressMessage.logger.logp(Level.SEVERE, "GRAPHIC", "GRAPHIC(Iterator<String> message)", "Incomplete additional Rectangle values!");
		}
	    }

	    case Square -> {
		if (parts.length == 5) {
		    this.graphicObject = new Square(Double.parseDouble(parts[0]), Double.parseDouble(parts[1]), Double.parseDouble(parts[2]),
			    Double.parseDouble(parts[3]), Double.parseDouble(parts[4]));
		} else {
		    SEDAPExpressMessage.logger.logp(Level.SEVERE, "GRAPHIC", "GRAPHIC(Iterator<String> message)", "Incomplete additional Square values!");
		}
	    }

	    case Circle -> {
		if (parts.length == 6) {
		    this.graphicObject = new Circle(Double.parseDouble(parts[0]), Double.parseDouble(parts[1]), Double.parseDouble(parts[2]),
			    Double.parseDouble(parts[3]), Double.parseDouble(parts[4]), Double.parseDouble(parts[5]));
		} else {
		    SEDAPExpressMessage.logger.logp(Level.SEVERE, "GRAPHIC", "GRAPHIC(Iterator<String> message)", "Incomplete additional Circle values!");
		}
	    }

	    case Ellipse -> {
		if (parts.length == 6) {
		    this.graphicObject = new Ellipse(Double.parseDouble(parts[0]), Double.parseDouble(parts[1]), Double.parseDouble(parts[2]),
			    Double.parseDouble(parts[3]), Double.parseDouble(parts[4]), Double.parseDouble(parts[5]));
		} else {
		    SEDAPExpressMessage.logger.logp(Level.SEVERE, "GRAPHIC", "GRAPHIC(Iterator<String> message)", "Incomplete additional Ellipse values!");
		}
	    }

	    case Block -> {
		if (parts.length == 9) {
		    this.graphicObject = new Block(Double.parseDouble(parts[0]), Double.parseDouble(parts[1]), Double.parseDouble(parts[2]),
			    Double.parseDouble(parts[3]), Double.parseDouble(parts[4]), Double.parseDouble(parts[5]),
			    Double.parseDouble(parts[6]), Double.parseDouble(parts[7]), Double.parseDouble(parts[8]));
		} else {
		    SEDAPExpressMessage.logger.logp(Level.SEVERE, "GRAPHIC", "GRAPHIC(Iterator<String> message)", "Incomplete additional Block values!");
		}
	    }

	    case Sphere -> {
		if (parts.length == 4) {
		    this.graphicObject = new Sphere(Double.parseDouble(parts[0]), Double.parseDouble(parts[1]), Double.parseDouble(parts[2]),
			    Double.parseDouble(parts[3]));
		} else {
		    SEDAPExpressMessage.logger.logp(Level.SEVERE, "GRAPHIC", "GRAPHIC(Iterator<String> message)", "Incomplete additional Sphere values!");
		}
	    }

	    case Ellipsoid -> {
		if (parts.length == 9) {
		    this.graphicObject = new Ellipsoid(Double.parseDouble(parts[0]), Double.parseDouble(parts[1]), Double.parseDouble(parts[2]),
			    Double.parseDouble(parts[3]), Double.parseDouble(parts[4]), Double.parseDouble(parts[5]),
			    Double.parseDouble(parts[6]), Double.parseDouble(parts[7]), Double.parseDouble(parts[8]));
		} else {
		    SEDAPExpressMessage.logger.logp(Level.SEVERE, "GRAPHIC", "GRAPHIC(Iterator<String> message)", "Incomplete additional Ellipsoid values!");
		}
	    }

	    case SensorFieldOfView -> {
		if (parts.length == 3) {
		    this.graphicObject = new SensorFieldOfView(Double.parseDouble(parts[0]), Double.parseDouble(parts[1]),
			    SEDAPExpressMessage.splitDoubleArrayDataHashTag(parts[2]));
		} else {
		    SEDAPExpressMessage.logger.logp(Level.SEVERE, "GRAPHIC", "GRAPHIC(Iterator<String> message)", "Incomplete additional SensorFieldOfView values!");
		}
	    }

	    case WeaponFieldOfFire -> {
		if (parts.length == 3) {
		    this.graphicObject = new WeaponFieldOfFire(Double.parseDouble(parts[0]), Double.parseDouble(parts[1]),
			    SEDAPExpressMessage.splitDoubleArrayDataHashTag(parts[2]));
		} else {
		    SEDAPExpressMessage.logger.logp(Level.SEVERE, "GRAPHIC", "GRAPHIC(Iterator<String> message)", "Incomplete additional WeaponFieldOfFire values!");
		}
	    }
	    }
	}
    }

    @Override
    public boolean equals(Object obj) {

	if (obj == null) {
	    return false;
	} else if (!(obj instanceof GRAPHIC)) {
	    return false;
	} else {
	    return super.equals(obj) &&

		    (((this.graphicID == null) && (((GRAPHIC) obj).graphicID == null)) || ((this.graphicID != null) && this.graphicID.equals(((GRAPHIC) obj).graphicID))) &&

		    (this.deleteFlag == ((GRAPHIC) obj).deleteFlag) &&

		    (this.graphicType == (((GRAPHIC) obj).graphicType)) && (this.lineWidth == (((GRAPHIC) obj).lineWidth))
		    && (this.lineColor == (((GRAPHIC) obj).lineColor))
		    && (this.fillColor == (((GRAPHIC) obj).fillColor))
		    && (this.textColor == (((GRAPHIC) obj).textColor)) &&
		    (((this.encoding == null) && (((GRAPHIC) obj).encoding == null)) || ((this.encoding != null) && this.encoding.equals(((GRAPHIC) obj).encoding))) &&

		    (((this.annotation == null) && (((GRAPHIC) obj).annotation == null)) || ((this.annotation != null) && this.annotation.equals(((GRAPHIC) obj).annotation)) &&

			    (((this.graphicObject == null) && (((GRAPHIC) obj).graphicObject == null))
				    || ((this.graphicObject != null) && this.graphicObject.equals(((GRAPHIC) obj).graphicObject))));

	}
    }

    @Override
    public int hashCode() {
	return super.hashCode();
    }

    @Override
    public String toString() {

	return SEDAPExpressMessage.removeSemicolons(
		serializeHeader()

			.append(this.graphicID).append(";")

			.append((this.deleteFlag != null && this.deleteFlag != DeleteFlag.FALSE) ? this.deleteFlag : "").append(";")

			.append((this.graphicType != null) ? this.graphicType : "").append(";")
			.append((this.lineWidth != null) ? SEDAPExpressMessage.NumberFormatter.format(this.lineWidth) : "").append(";")
			.append((this.lineColor != null) ? Long.toHexString(this.lineColor).toUpperCase() : "").append(";")
			.append((this.fillColor != null) ? Long.toHexString(this.fillColor).toUpperCase() : "").append(";")
			.append((this.textColor != null) ? Long.toHexString(this.textColor).toUpperCase() : "").append(";")
			.append((this.encoding != null && this.encoding == DataEncoding.BASE64) ? this.encoding : "").append(";")
			.append((this.annotation != null) ? ((this.encoding == DataEncoding.BASE64) ? Base64.toBase64String(this.annotation.getBytes()) : this.annotation) : "").append(";")
			.append((this.graphicObject != null) ? SEDAPExpressMessage.objectToCSV(this.graphicObject) : "").toString());

    }

}
