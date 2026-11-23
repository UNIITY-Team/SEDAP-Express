package de.bundeswehr.uniity.sedapexpress.messages;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import de.bundeswehr.uniity.sedapexpress.messages.SEDAPExpressMessage.Acknowledgement;
import de.bundeswehr.uniity.sedapexpress.messages.SEDAPExpressMessage.Classification;

class HEARTBEATTest {

    @Test
    void defaultConstructorInitializesOptionalFieldsToNull() {
        HEARTBEAT message = new HEARTBEAT();
        assertNull(message.getNumber());
        assertNull(message.getTime());
        assertNull(message.getSender());
        assertNull(message.getClassification());
        assertNull(message.getAcknowledgement());
        assertNull(message.getMAC());
        assertNull(message.getRecipient());
    }

    @Test
    void constructorsPreserveHeaderAndRecipient() {
        HEARTBEAT message = new HEARTBEAT((byte) 0x2A, 0x0195238E25ADL, "A1B2",
                Classification.Restricted, Acknowledgement.TRUE, "AABBCCDD", "NODE-2");

        assertEquals((byte) 0x2A, message.getNumber());
        assertEquals(0x0195238E25ADL, message.getTime());
        assertEquals("A1B2", message.getSender());
        assertEquals(Classification.Restricted, message.getClassification());
        assertEquals(Acknowledgement.TRUE, message.getAcknowledgement());
        assertEquals("AABBCCDD", message.getMAC());
        assertEquals("NODE-2", message.getRecipient());
    }

    @Test
    void parsesAndSerializesDocumentedMessage() {
        String wire = "HEARTBEAT;2A;0195238E25AD;A1B2;R;TRUE;AABBCCDD;NODE-2";
        HEARTBEAT parsed = new HEARTBEAT(wire);

        assertEquals((byte) 0x2A, parsed.getNumber());
        assertEquals("NODE-2", parsed.getRecipient());
        assertEquals(wire, parsed.toString());
        assertEquals(parsed, new HEARTBEAT(parsed.toString()));
    }

    @Test
    void iteratorConstructorParsesRecipient() {
        HEARTBEAT parsed = new HEARTBEAT(SEDAPExpressMessage.splitMessage(
                "2A;0195238E25AD;A1B2;R;TRUE;AABBCCDD;NODE-2").iterator());
        assertEquals("NODE-2", parsed.getRecipient());
    }

    @Test
    void generatedAnswerAddressesOriginalSenderAndCopiesClassification() {
        HEARTBEAT request = new HEARTBEAT((byte) 1, 123L, "SENDER", Classification.Secret,
                Acknowledgement.FALSE, null, "RECIPIENT");

        long before = System.currentTimeMillis();
        HEARTBEAT answer = HEARTBEAT.generateAnswerHEARTBEAT(request);
        long after = System.currentTimeMillis();

        assertEquals("SENDER", answer.getRecipient());
        assertEquals(Classification.Secret, answer.getClassification());
        assertNotNull(answer.getTime());
        assertTrue(answer.getTime() >= before && answer.getTime() <= after);
    }

    @Test
    void recipientCanBeChanged() {
        HEARTBEAT message = new HEARTBEAT();
        message.setRecipient("DEST");
        assertEquals("DEST", message.getRecipient());
        message.setRecipient(null);
        assertNull(message.getRecipient());
    }
}
