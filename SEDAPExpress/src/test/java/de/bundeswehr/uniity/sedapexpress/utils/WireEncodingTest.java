package de.bundeswehr.uniity.sedapexpress.utils;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HexFormat;
import java.util.List;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.junit.jupiter.api.Test;

import de.bundeswehr.uniity.sedapexpress.messages.CONTACT;
import de.bundeswehr.uniity.sedapexpress.messages.GRAPHIC;
import de.bundeswehr.uniity.sedapexpress.messages.POINT;
import de.bundeswehr.uniity.sedapexpress.messages.SEDAPExpressMessage;
import de.bundeswehr.uniity.sedapexpress.messages.SEDAPExpressMessage.DataEncoding;
import de.bundeswehr.uniity.sedapexpress.messages.STATUS;
import de.bundeswehr.uniity.sedapexpress.messages.TEXT;

class WireEncodingTest {
    private static final String CONTENT = "München £ÿ";
    private static final byte[] CONTENT_LATIN1 = CONTENT.getBytes(StandardCharsets.ISO_8859_1);
    private static final String CONTENT_BASE64 = "TfxuY2hlbiCj/w==";

    private TEXT text() {
        TEXT message = new TEXT();
        message.setTextContent(CONTENT);
        message.setEncoding(DataEncoding.NONE);
        return message;
    }

    @Test
    void textBase64UsesLatin1BytesInBothDirections() {
        TEXT message = text();
        message.setEncoding(DataEncoding.BASE64);

        // Fixed wire vector: ü = FC, £ = A3 and ÿ = FF in ISO-8859-1.
        assertTrue(message.toString().contains(CONTENT_BASE64));

        TEXT decoded = (TEXT) SEDAPExpressMessage.deserialize(
                "TEXT;;;;;;;;;BASE64;" + CONTENT_BASE64);

        assertNotNull(decoded);
        assertEquals(CONTENT, decoded.getTextContent());
    }

    @Test
    void otherBase64TextFieldsUseLatin1() {
        CONTACT contact = new CONTACT();
        contact.setComment(CONTENT);
        assertTrue(contact.toString().endsWith(";" + CONTENT_BASE64));
        assertEquals(CONTENT, new CONTACT(contact.toString()).getComment());

        POINT point = new POINT();
        point.setComment(CONTENT);
        assertTrue(point.toString().endsWith(";" + CONTENT_BASE64));
        assertEquals(CONTENT, new POINT(point.toString()).getComment());

        GRAPHIC graphic = new GRAPHIC();
        graphic.setGraphicID("test-point");
        graphic.setGraphicType(GRAPHIC.GraphicType.Point);
        graphic.setGraphicObject(new GRAPHIC.Point(53.0, 8.0, 0.0));
        graphic.setEncoding(DataEncoding.BASE64);
        graphic.setAnnotation(CONTENT);
        assertEquals(CONTENT_BASE64, graphic.toString().split(";", -1)[15]);
        assertEquals(CONTENT, new GRAPHIC(graphic.toString()).getAnnotation());

        STATUS status = new STATUS();
        status.setHostname(CONTENT);
        status.setMediaUrls(List.of(CONTENT));
        status.setFreeText(CONTENT);

        assertTrue(status.toString().endsWith(
                ";" + CONTENT_BASE64 + ";" + CONTENT_BASE64 + ";" + CONTENT_BASE64));

        STATUS decoded = new STATUS(status.toString());
        assertEquals(CONTENT, decoded.getHostname());
        assertEquals(List.of(CONTENT), decoded.getMediaUrls());
        assertEquals(CONTENT, decoded.getFreeText());
    }

    @Test
    void compressedMessagePreservesLatin1WireBytes() throws Exception {
        TEXT message = text();
        String compressed = CompressUtils.compressMessage(message);

        byte[] compressedBytes = Base64.getDecoder().decode(compressed);
        try (Inflater inflater = new Inflater(true);
             InflaterInputStream input = new InflaterInputStream(
                     new ByteArrayInputStream(compressedBytes), inflater)) {
            assertArrayEquals(
                    message.toString().getBytes(StandardCharsets.ISO_8859_1),
                    input.readAllBytes());
        }

        TEXT decoded = (TEXT) CompressUtils.decompressMessage(compressed);
        assertEquals(CONTENT, decoded.getTextContent());
    }

    @Test
    void encryptionUsesLatin1InputAndEmbedsCtrIv() throws Exception {
        byte[] key = new byte[16];

        // The new EncryptionUtils API returns IV || ciphertext.
        byte[] encrypted = EncryptionUtils.encrypt_AES_CTR(CONTENT_LATIN1, key);
        assertTrue(encrypted.length >= 16);

        byte[] iv = new byte[16];
        byte[] ciphertext = new byte[encrypted.length - iv.length];
        System.arraycopy(encrypted, 0, iv, 0, iv.length);
        System.arraycopy(encrypted, iv.length, ciphertext, 0, ciphertext.length);

        // Independently verify that the ciphertext was produced from the Latin-1 bytes.
        Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "AES"), new IvParameterSpec(iv));
        assertArrayEquals(cipher.doFinal(CONTENT_LATIN1), ciphertext);

        // The complete IV || ciphertext payload must survive Base64 transport.
        String encoded = EncryptionUtils.encodeBase64(encrypted);
        byte[] decodedPayload = EncryptionUtils.decodeBase64(encoded);
        assertArrayEquals(encrypted, decodedPayload);

        assertArrayEquals(CONTENT_LATIN1, EncryptionUtils.decrypt_AES_CTR(decodedPayload, key));
        assertEquals(CONTENT,
                new String(EncryptionUtils.decrypt_AES_CTR(decodedPayload, key),
                        StandardCharsets.ISO_8859_1));
    }

    @Test
    void hmacUsesLatin1SerializedMessage() throws Exception {
        TEXT message = text();
        byte[] key = new byte[16];
        message.setMAC("");

        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(key, "HmacSHA256"));

        String expected = HexFormat.of().withUpperCase().formatHex(
                mac.doFinal(message.toString().getBytes(StandardCharsets.ISO_8859_1)));

        String calculatedMac = MACUtils.setMACFieldWithHMAC(key, message).getMAC();
        assertEquals(expected, calculatedMac);
    }


    @Test
    void everyIcdMessageTypeCanBeDeserializedAndSerializedAgain() {
        // Message names and representative wire examples are taken from
        // SEDAP-Express ICD v1.5.0, section 6.
        String[][] examples = {
                {"OWNUNIT", "OWNUNIT;11;0191C643A8AF;22AA;U;FALSE;4389F10D;77.88;-10.12;5577.0;33.44;55.66;1.1;-2.2;3.3;Ownunit;SFGPIB----H----"},
                {"CONTACT", "CONTACT;5E;0191C643A8AF;83C5;R;;;100;FALSE;53.32;8.11;0;;;;120;275;;;;;;;FGS Bayern;AR;SFSPCLFF-------;;;;" + CONTENT_BASE64},
                {"POINT", "POINT;5E;0000661D4410;66A3;R;;;100;FALSE;53.32;8.11;0;;;;120;275;;;;;;;Target Alpha;gffppt---------;;" + CONTENT_BASE64},
                {"EMISSION", "EMISSION;5E;0195238E15AD;66A3;R;;;100;;53.32;8.11;0;;;;20;8725000.0#8735000.0;20000;3;00;02;06;10233;;" + CONTENT_BASE64},
                {"METEO", "METEO;1C;0195238E25AD;74BE;U;;;15.4;15.5;;;;;10.2;72;1005;25;111;50;2500;33;RefPoint1"},
                {"TEXT", "TEXT;7B;0195238E285B;324E;U;;;ORKA;04;BASE64;" + CONTENT_BASE64},
                {"GRAPHIC", "GRAPHIC;79;0195238E35AD;910E;U;;;FFDA;;08;1;FF800000;;;BASE64;" + CONTENT_BASE64 + ";53.43;9.45;0;1000"},
                {"COMMAND", "COMMAND;55;0195238E25AD;5BCD;S;TRUE;;ORKA;1111;01;;24;53.32;8.11;1000;5"},
                {"STATUS", "STATUS;15;0195238E25AD;75DA;U;;;4;2;MLG#20;;Accu1#50;;443D;01;MTAuMC4wLjEzMg==;;" + CONTENT_BASE64},
                {"ACKNOWLEDGE", "ACKNOWLEDGE;18;0195238E25AD;129E;R;;;LASSY;COMMAND;2B"},
                {"RESEND", "RESEND;20;0195238E25AD;129E;R;;;FE2A;TEXT;5D"},
                {"GENERIC", "GENERIC;5E;0195238E25AD;66A3;R;TRUE;;BINARY;BASE64;" + CONTENT_BASE64},
                {"HEARTBEAT", "HEARTBEAT;42;0195238E25AD;89AD;U;;;ORKA"},
                {"TIMESYNC", "TIMESYNC;12;;FE2A;U;;;0191C643A8AF"},
                {"KEYEXCHANGE", "KEYEXCHANGE;00;0191C643A8AF;89AD;U;;;FE2A;0;128;1024;7FFFFFFF;822460DE"}
        };

        for (String[] example : examples) {
            String expectedType = example[0];
            SEDAPExpressMessage parsed = SEDAPExpressMessage.deserialize(example[1]);

            assertNotNull(parsed, "ICD message could not be deserialized: " + expectedType);
            assertEquals(expectedType, parsed.getClass().getSimpleName());

            String serialized = parsed.toString();
            assertTrue(serialized.startsWith(expectedType + ";"),
                    "Serialized message has unexpected type: " + expectedType);

            SEDAPExpressMessage reparsed = SEDAPExpressMessage.deserialize(serialized);
            assertNotNull(reparsed, "Serialized ICD message could not be deserialized again: " + expectedType);
            assertEquals(expectedType, reparsed.getClass().getSimpleName());
        }
    }

    @Test
    void base64TextPayloadsInIcdMessageTypesPreserveLatin1Characters() {
        // These ICD fields are explicitly BASE64 encoded text/data fields.
        // The BASE64 payload represents CONTENT encoded as ISO-8859-1.
        String[] examples = {
                "CONTACT;5E;0191C643A8AF;83C5;R;;;100;FALSE;53.32;8.11;0;;;;120;275;;;;;;;FGS Bayern;AR;SFSPCLFF-------;;;;" + CONTENT_BASE64,
                "POINT;5E;0000661D4410;66A3;R;;;100;FALSE;53.32;8.11;0;;;;120;275;;;;;;;Target Alpha;gffppt---------;;" + CONTENT_BASE64,
                "EMISSION;5E;0195238E15AD;66A3;R;;;100;;53.32;8.11;0;;;;20;8725000.0#8735000.0;20000;3;00;02;06;10233;;" + CONTENT_BASE64,
                "TEXT;7B;0195238E285B;324E;U;;;ORKA;04;BASE64;" + CONTENT_BASE64,
                "GRAPHIC;79;0195238E35AD;910E;U;;;FFDA;;08;1;FF800000;;;BASE64;" + CONTENT_BASE64 + ";53.43;9.45;0;1000",
                "STATUS;15;0195238E25AD;75DA;U;;;4;2;MLG#20;;Accu1#50;;443D;01;MTAuMC4wLjEzMg==;;" + CONTENT_BASE64,
                "GENERIC;5E;0195238E25AD;66A3;R;TRUE;;BINARY;BASE64;" + CONTENT_BASE64
        };

        for (String wireMessage : examples) {
            SEDAPExpressMessage parsed = SEDAPExpressMessage.deserialize(wireMessage);
            assertNotNull(parsed, "Could not deserialize BASE64 payload message: " + wireMessage);

            String serialized = parsed.toString();
            assertTrue(serialized.contains(CONTENT_BASE64),
                    "BASE64 payload was not preserved by serialization: " + parsed.getClass().getSimpleName());

            SEDAPExpressMessage reparsed = SEDAPExpressMessage.deserialize(serialized);
            assertNotNull(reparsed);
            assertTrue(reparsed.toString().contains(CONTENT_BASE64));
        }
    }

}
