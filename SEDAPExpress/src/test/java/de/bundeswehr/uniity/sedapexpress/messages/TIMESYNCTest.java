package de.bundeswehr.uniity.sedapexpress.messages;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import de.bundeswehr.uniity.sedapexpress.messages.SEDAPExpressMessage.Acknowledgement;
import de.bundeswehr.uniity.sedapexpress.messages.SEDAPExpressMessage.Classification;

class TIMESYNCTest {

    @Test
    void defaultAndRequestConstructorsHaveNoResponseTimestamp() {
        assertNull(new TIMESYNC().getTimestamp());
        TIMESYNC request = new TIMESYNC((byte) 1, 0x0195238E25ADL, "NODE",
                Classification.Unclas, Acknowledgement.FALSE, null);
        assertNull(request.getTimestamp());
    }

    @Test
    void responseConstructorPreservesTimestamp() {
        TIMESYNC response = new TIMESYNC((byte) 2, 0x0195238E25ADL, "NODE",
                Classification.Unclas, Acknowledgement.TRUE, null, 0x0195238E35ADL);
        assertEquals(0x0195238E35ADL, response.getTimestamp());
    }

    @Test
    void parsesTimestampAsHexadecimalAndSerializesIt() {
        String wire = "TIMESYNC;02;0195238E25AD;NODE;U;TRUE;;0195238E35AD";
        TIMESYNC parsed = new TIMESYNC(wire);

        assertEquals(0x0195238E35ADL, parsed.getTimestamp());
        assertEquals(wire, parsed.toString());
        assertEquals(parsed, new TIMESYNC(parsed.toString()));
    }

    @Test
    void iteratorConstructorParsesTimestamp() {
        TIMESYNC parsed = new TIMESYNC(SEDAPExpressMessage.splitMessage(
                "02;0195238E25AD;NODE;U;TRUE;;0195238E35AD").iterator());
        assertEquals(0x0195238E35ADL, parsed.getTimestamp());
    }

    @Test
    void timestampCanBeCleared() {
        TIMESYNC message = new TIMESYNC();
        message.setTimestamp(123L);
        assertEquals(123L, message.getTimestamp());
        message.setTimestamp(null);
        assertNull(message.getTimestamp());
    }
}
