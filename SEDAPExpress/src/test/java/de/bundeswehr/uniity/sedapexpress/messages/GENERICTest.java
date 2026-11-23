package de.bundeswehr.uniity.sedapexpress.messages;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class GENERICTest {

    @Test
    void testConstructorValues() {
        GENERIC generic = new GENERIC((byte) 0x5E, 0x0195238E25ADL, "66A3",
                SEDAPExpressMessage.Classification.Restricted, SEDAPExpressMessage.Acknowledgement.TRUE, null,
                GENERIC.ContentType.JSON, SEDAPExpressMessage.DataEncoding.NONE, "{\"x\":1}");

        assertEquals(GENERIC.ContentType.JSON, generic.getContentType());
        assertEquals(SEDAPExpressMessage.DataEncoding.NONE, generic.getEncoding());
        assertEquals("{\"x\":1}", generic.getContent());
    }

    @Test
    void testStringConstructorParsesFields() {
        GENERIC generic = new GENERIC("GENERIC;5E;0195238E25AD;66A3;R;;;JSON;;{\"x\":1}");
        assertEquals(GENERIC.ContentType.JSON, generic.getContentType());
        assertEquals("{\"x\":1}", generic.getContent());
    }

    @Test
    void testConstructorIteratorAndDefaultSetters() {
        GENERIC parsed = new GENERIC(SEDAPExpressMessage.splitMessage(
                "5E;0195238E25AD;66A3;R;;;JSON;;{\"x\":1}").iterator());
        assertEquals(GENERIC.ContentType.JSON, parsed.getContentType());
        assertEquals("{\"x\":1}", parsed.getContent());

        GENERIC generic = new GENERIC();
        assertNull(generic.getContentType());
        assertNull(generic.getEncoding());
        assertNull(generic.getContent());
        generic.setContentType(GENERIC.ContentType.XML);
        generic.setEncoding(SEDAPExpressMessage.DataEncoding.BASE64);
        generic.setContent("PHhtbD4=");
        assertEquals(GENERIC.ContentType.XML, generic.getContentType());
        assertEquals(SEDAPExpressMessage.DataEncoding.BASE64, generic.getEncoding());
        assertEquals("PHhtbD4=", generic.getContent());
    }

    @Test
    void testSamplesFromDocu() {
        String[] messages = {
            "GENERIC;5E;0195238E25AD;66A3;R;;;JSON;;{\"object\": {\"x\": \"1\",\"y\": \"2\"},\"string\": \"Hello World\"}",
            "GENERIC;5E;0195238E25AD;66A3;R;TRUE;;BINARY;BASE64;U2FtcGxlIGJpbmFyeSBkYXRhIEdyZWV0aW5ncyA6RA==",
            "GENERIC;5E;0195238E25AD;66A3;R;;;NMEA;NONE;$RATTM,11,11.4,13.6,T,7.0,20.0,T,0.0,0.0,N,,Q,,154125.82,A,*17"
        };
        for (String message : messages) {
            assertEquals(message, new GENERIC(message).toString());
        }
    }
}
