package de.bundeswehr.uniity.sedapexpress.network;

import static org.junit.jupiter.api.Assertions.*;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import de.bundeswehr.uniity.sedapexpress.messages.SEDAPExpressMessage;
import de.bundeswehr.uniity.sedapexpress.messages.SEDAPExpressMessage.MessageType;
import de.bundeswehr.uniity.sedapexpress.messages.TEXT;

class WireTransportEncodingTest {
    @Test
    @Timeout(5)
    void tcpWritesLatin1BytesToLoopbackPeer() throws Exception {
        // Bind only loopback on an ephemeral port; no external service is used.
        try (ServerSocketChannel listener = ServerSocketChannel.open()) {
            listener.bind(new InetSocketAddress(InetAddress.getLoopbackAddress(), 0));
            try (SocketChannel sender = SocketChannel.open(listener.getLocalAddress());
                    SocketChannel receiver = listener.accept()) {
                receiver.socket().setSoTimeout(2000);
                SEDAPExpressTCPClient client = new SEDAPExpressTCPClient(sender, new ConcurrentLinkedDeque<>(), new ConcurrentHashMap<>());
                TEXT message = new TEXT();
                message.setTextContent("München");
                byte[] expected = SEDAPExpressMessage.serialize(message).getBytes(StandardCharsets.ISO_8859_1);
                assertTrue(client.sendSEDAPExpressMessage(message));
                assertArrayEquals(expected, receiver.socket().getInputStream().readNBytes(expected.length));
            }
        }
    }

    @Test
    void mqttDecodesLatin1PayloadWithoutConnecting() throws Exception {
        SEDAPExpressMQTTClient client = new SEDAPExpressMQTTClient("tcp://localhost", "test-sender");
        AtomicReference<TEXT> received = new AtomicReference<>();
        client.subscribeMessages(message -> received.set((TEXT) message), MessageType.TEXT);
        byte[] payload = "TEXT;;;;;;;;;NONE;München".getBytes(StandardCharsets.ISO_8859_1);
        client.messageArrived("test/sender/TEXT", new MqttMessage(payload));
        assertNotNull(received.get());
        assertEquals("München", received.get().getTextContent());
    }
}
