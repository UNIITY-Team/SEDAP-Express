package de.bundeswehr.uniity.sedapexpress.utils;

import static org.junit.jupiter.api.Assertions.*;

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

import de.bundeswehr.uniity.sedapexpress.messages.SEDAPExpressMessage;
import de.bundeswehr.uniity.sedapexpress.messages.SEDAPExpressMessage.DataEncoding;
import de.bundeswehr.uniity.sedapexpress.messages.TEXT;
import de.bundeswehr.uniity.sedapexpress.messages.CONTACT;
import de.bundeswehr.uniity.sedapexpress.messages.POINT;
import de.bundeswehr.uniity.sedapexpress.messages.GRAPHIC;
import de.bundeswehr.uniity.sedapexpress.messages.STATUS;

class WireEncodingTest {
    private static final String CONTENT = "München £ÿ";

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
        // Fixed external vector: FC, A3 and FF each occupy one Latin-1 byte.
        assertTrue(message.toString().contains("TfxuY2hlbiCj/w=="));
        TEXT decoded = (TEXT) SEDAPExpressMessage.deserialize("TEXT;;;;;;;;;BASE64;TfxuY2hlbiCj/w==");
        assertNotNull(decoded);
        assertEquals(CONTENT, decoded.getTextContent());
    }

    @Test
    void otherBase64TextFieldsUseLatin1() {
        CONTACT contact = new CONTACT();
        contact.setComment(CONTENT);
        assertTrue(contact.toString().endsWith(";TfxuY2hlbiCj/w=="));
        assertEquals(CONTENT, new CONTACT(contact.toString()).getComment());

        POINT point = new POINT();
        point.setComment(CONTENT);
        assertTrue(point.toString().endsWith(";TfxuY2hlbiCj/w=="));
        assertEquals(CONTENT, new POINT(point.toString()).getComment());

        GRAPHIC graphic = new GRAPHIC();
        graphic.setGraphicID("test-point");
        graphic.setGraphicType(GRAPHIC.GraphicType.Point);
        graphic.setGraphicObject(new GRAPHIC.Point(53.0, 8.0, 0.0));
        graphic.setEncoding(DataEncoding.BASE64);
        graphic.setAnnotation(CONTENT);
        assertEquals("TfxuY2hlbiCj/w==", graphic.toString().split(";", -1)[15]);
        assertEquals(CONTENT, new GRAPHIC(graphic.toString()).getAnnotation());

        STATUS status = new STATUS();
        status.setHostname(CONTENT);
        status.setMediaUrls(List.of(CONTENT));
        status.setFreeText(CONTENT);
        assertTrue(status.toString().endsWith(";TfxuY2hlbiCj/w==;TfxuY2hlbiCj/w==;TfxuY2hlbiCj/w=="));
        STATUS decoded = new STATUS(status.toString());
        assertEquals(CONTENT, decoded.getHostname());
        assertEquals(List.of(CONTENT), decoded.getMediaUrls());
        assertEquals(CONTENT, decoded.getFreeText());
    }

    @Test
    void compressedMessagePreservesLatin1WireBytes() throws Exception {
        TEXT message = text();
        String compressed = CompressUtils.compressMessage(message);
        Inflater inflater = new Inflater(true);
        try (InflaterInputStream input = new InflaterInputStream(new ByteArrayInputStream(Base64.getDecoder().decode(compressed)), inflater)) {
            assertArrayEquals(message.toString().getBytes(StandardCharsets.ISO_8859_1), input.readAllBytes());
        } finally {
            inflater.end();
        }
        TEXT decoded = (TEXT) CompressUtils.decompressMessage(compressed);
        assertEquals(CONTENT, decoded.getTextContent());
    }

    @Test
    void encryptionMatchesIndependentLatin1CipherInput() throws Exception {
        byte[] key = new byte[16];
        byte[] iv = new byte[16];
        Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "AES"), new IvParameterSpec(iv));
        String expected = Base64.getEncoder().encodeToString(cipher.doFinal(CONTENT.getBytes(StandardCharsets.ISO_8859_1)));
        assertEquals(expected, EncryptionUtils.encrypt_AES_CTR(CONTENT, key, iv));
        assertEquals(CONTENT, EncryptionUtils.decrypt_AES_CTR(expected, key, iv));
    }

    @Test
    void hmacUsesLatin1SerializedMessage() throws Exception {
        TEXT message = text();
        byte[] key = new byte[16];
        message.setMAC("0000");
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(key, "HmacSHA256"));
        String expected = HexFormat.of().withUpperCase().formatHex(mac.doFinal(message.toString().getBytes(StandardCharsets.ISO_8859_1)));
        assertEquals(expected, MACUtils.calcHMAC(key, message));
    }
}
