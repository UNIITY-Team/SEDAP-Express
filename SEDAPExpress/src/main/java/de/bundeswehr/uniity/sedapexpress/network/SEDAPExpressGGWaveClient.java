/**
 * Note: This license has also been called the “Simplified BSD License” and the “FreeBSD License”.
 *
 * Copyright 2024-2026 UNIITY POC: Volker Voß, Federal Armed Forces of Germany
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of
 *    conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of
 *    conditions and the following disclaimer in the documentation and/or other materials provided with
 *    the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS “AS IS” AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGE.
 */
package de.bundeswehr.uniity.sedapexpress.network;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.ThreadLocalRandom;
import java.util.zip.CRC32;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Structure;

import de.bundeswehr.uniity.sedapexpress.messages.SEDAPExpressMessage;

/**
 * Acoustic SEDAP-Express communicator using GGWave, JNA and Java Sound.
 *
 * The GGWave native library is loaded by GGWaveLib. Make sure that the matching
 * native library (architecture and operating system) is available through
 * java.library.path / jna.library.path before this class is initialized.
 *
 * @author Volker Voß
 */
public class SEDAPExpressGGWaveClient extends SEDAPExpressCommunicator {

/**
 * Represents or provides the sedapexpress ggwave client functionality.
 */
	public interface GGWaveLib extends Library {

		// DLL laden: "ggwave.dll" muss im Projektverzeichnis oder System-Pfad liegen
		GGWaveLib INSTANCE = Native.load("ggwave", GGWaveLib.class);

		// --- Enums ---
		int GGWAVE_SAMPLE_FORMAT_U8 = 1;
		int GGWAVE_SAMPLE_FORMAT_I16 = 4;
		int GGWAVE_SAMPLE_FORMAT_F32 = 5;

		int GGWAVE_TX_PROTOCOL_AUDIBLE_NORMAL = 0;
		int GGWAVE_TX_PROTOCOL_AUDIBLE_FAST = 1;

		// --- Strukturen ---
		@Structure.FieldOrder({ "payloadLength", "sampleRateInp", "sampleRateOut", "samplesPerFrame", "soundMarkerThreshold", "sampleFormatInp", "sampleFormatOut" })
		class ggwave_Parameters extends Structure {
			public int payloadLength;
			public float sampleRateInp;
			public float sampleRateOut;
			public int samplesPerFrame;
			public float soundMarkerThreshold;
			public int sampleFormatInp; // enum als int
			public int sampleFormatOut; // enum als int

			// Wichtig für Rückgabewerte "by value"
/**
 * Represents or provides the sedapexpress ggwave client functionality.
 */
			public static class ByValue extends ggwave_Parameters implements Structure.ByValue {
			}
		}

		// --- Methoden ---

		// Gibt Struktur direkt zurück (ByValue)
		ggwave_Parameters.ByValue ggwave_getDefaultParameters();

		// Nimmt Struktur als Wert entgegen
		int ggwave_init(ggwave_Parameters.ByValue parameters);

		void ggwave_free(int instance);

		/**
		 * Encodes an audio payload into the supplied output buffer.
		 *
		 * @param instance initialized ggwave instance identifier
		 * @param dataBuffer encoded data to transmit
		 * @param dataSize number of valid bytes in {@code dataBuffer}
		 * @param txProtocolId transmission protocol identifier
		 * @param volume output volume level
		 * @param outputBuffer buffer receiving the encoded audio samples
		 * @param query whether the operation is a query
		 * @return number of bytes written to {@code outputBuffer}, or the native error code
		 */
		int ggwave_encode(int instance, byte[] dataBuffer, int dataSize, int txProtocolId, int volume, byte[] outputBuffer, int query);

		/**
		 * Decodes audio samples from the supplied input buffer.
		 *
		 * @param instance initialized ggwave instance identifier
		 * @param dataBuffer input audio samples
		 * @param dataSize number of valid bytes in {@code dataBuffer}
		 * @param outputBuffer buffer receiving decoded payload bytes
		 * @return number of bytes written to {@code outputBuffer}, or the native error code
		 */
		int ggwave_decode(int instance, byte[] dataBuffer, int dataSize, byte[] outputBuffer);
	}

	private static final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

	static {
		logger.setLevel(Level.ALL);
	}

	private static final int DEFAULT_AUDIO_BUFFER_SAMPLES = 20_480;
	private static final int DEFAULT_DECODE_BUFFER_SIZE = 65_536;

	/*
	 * Fragment header: magic(4), version(1), transferId(8), index(2),
	 * count(2), totalLength(4), CRC32(4) = 25 bytes.
	 * 96 data bytes + 25 header bytes = 121 bytes per GGWave frame.
	 */
	private static final byte[] FRAGMENT_MAGIC = { 'S', 'G', 'W', 'F' };
	private static final byte FRAGMENT_VERSION = 1;
	private static final int FRAGMENT_HEADER_SIZE = 25;
	private static final int DEFAULT_FRAGMENT_DATA_SIZE = 96;
	private static final int MAX_REASSEMBLED_MESSAGE_SIZE = 16 * 1024 * 1024;
	private static final int MAX_CONCURRENT_REASSEMBLIES = 64;
	private static final long REASSEMBLY_TIMEOUT_NANOS = 60_000_000_000L;
	private static final long ACK_TIMEOUT_MILLIS = 5_000L;
	private static final int MAX_ACK_ATTEMPTS = 3;
	private static final long COMPLETED_MESSAGE_CACHE_NANOS = 120_000_000_000L;
	private static final int MAX_COMPLETED_MESSAGES = 512;

	private final int txProtocolId;
	private final int txVolume;
	private final int audioBufferSamples;
	private final int fragmentDataSize;
	private final Map<Long, FragmentAssembly> fragmentAssemblies = new ConcurrentHashMap<>();
	private final Map<String, CompletableFuture<Void>> pendingAcknowledgements = new ConcurrentHashMap<>();
	private final Map<String, Long> completedAcknowledgedMessages = new ConcurrentHashMap<>();
	private final String localSenderId;
	private final String localClassification;
	private volatile byte acknowledgeSequence;

	private volatile boolean status;
	private volatile Exception lastException;

	private int ggwaveInstance = -1;
	private GGWaveLib.ggwave_Parameters.ByValue parameters;

	private TargetDataLine microphone;
	private SourceDataLine speaker;

	private Thread receiverThread;

	/*
	 * GGWave instances are shared by encoder and decoder. Serialize native calls
	 * unless the native library explicitly guarantees concurrent access.
	 */
	private final Object codecLock = new Object();
	private final Object transmitLock = new Object();

	/**
	 * Creates a GGWave communicator using the audible-fast transmit protocol.
	 */
	public SEDAPExpressGGWaveClient() {
		this(GGWaveLib.GGWAVE_TX_PROTOCOL_AUDIBLE_FAST, 100, DEFAULT_AUDIO_BUFFER_SAMPLES, DEFAULT_FRAGMENT_DATA_SIZE);
	}

	/**
 * Creates a sedapexpress ggwave client instance.
	 * @param txProtocolId       GGWave transmit protocol, e.g. AUDIBLE_FAST or AUDIBLE_NORMAL
	 * @param txVolume           transmit volume (0..100)
	 * @param audioBufferSamples number of mono audio samples read per microphone operation
	 */
	public SEDAPExpressGGWaveClient(int txProtocolId, int txVolume, int audioBufferSamples) {
		this(txProtocolId, txVolume, audioBufferSamples, DEFAULT_FRAGMENT_DATA_SIZE);
	}

	/**
 * Creates a sedapexpress ggwave client instance.
	 * @param txProtocolId       GGWave transmit protocol
	 * @param txVolume           transmit volume (0..100)
	 * @param audioBufferSamples microphone samples per read operation
	 * @param fragmentDataSize   application data bytes per GGWave fragment
	 */
	public SEDAPExpressGGWaveClient(int txProtocolId, int txVolume, int audioBufferSamples, int fragmentDataSize) {
		this(txProtocolId, txVolume, audioBufferSamples, fragmentDataSize, System.getProperty("sedap.sender", "GGWAVE"), System.getProperty("sedap.classification", "U"));
	}

	/**
	 * Creates a communicator with an explicit local SEDAP sender identifier.
	 * The classification defaults to the system property {@code sedap.classification}
	 * or {@code U}.
 * @param txProtocolId value for tx protocol id
 * @param txVolume value for tx volume
 * @param audioBufferSamples value for audio buffer samples
 * @param fragmentDataSize value for fragment data size
 * @param localSenderId value for local sender id
	 */
	public SEDAPExpressGGWaveClient(int txProtocolId, int txVolume, int audioBufferSamples, int fragmentDataSize, String localSenderId) {
		this(txProtocolId, txVolume, audioBufferSamples, fragmentDataSize, localSenderId, System.getProperty("sedap.classification", "U"));
	}

	/**
 * Creates a sedapexpress ggwave client instance.
	 * @param localSenderId       SEDAP-Express sender identifier used for ACKNOWLEDGE messages
	 * @param localClassification SEDAP classification (P/U/R/C/S/T)
 * @param txProtocolId value for tx protocol id
 * @param txVolume value for tx volume
 * @param audioBufferSamples value for audio buffer samples
 * @param fragmentDataSize value for fragment data size
	 */
	public SEDAPExpressGGWaveClient(int txProtocolId, int txVolume, int audioBufferSamples, int fragmentDataSize, String localSenderId, String localClassification) {
		this.txProtocolId = txProtocolId;
		this.txVolume = Math.max(0, Math.min(100, txVolume));
		this.audioBufferSamples = Math.max(1_024, audioBufferSamples);
		if (fragmentDataSize < 1 || fragmentDataSize > 115) {
			throw new IllegalArgumentException("fragmentDataSize must be between 1 and 115 bytes " + "(GGWave variable-length payload limit: 140 bytes including transport header)");
		}
		this.fragmentDataSize = fragmentDataSize;
		if (localSenderId == null || !localSenderId.matches("[\\x21-\\x3A\\x3C-\\x7E]{1,64}")) {
			throw new IllegalArgumentException("localSenderId must be 1..64 printable ASCII characters (except ';')");
		}
		if (localClassification == null || !localClassification.matches("[PpUuRrCcSsTt]")) {
			throw new IllegalArgumentException("localClassification must be one of P/U/R/C/S/T");
		}
		this.localSenderId = localSenderId;
		this.localClassification = localClassification.toUpperCase(java.util.Locale.ROOT);
	}

	/**
	 * Establishes the connection to the configured endpoint.
	 * @return {@code true} if the condition is satisfied; otherwise {@code false}.
	 */
	@Override
	public synchronized boolean connect() {
		if (this.status) {
			return true;
		}

		try {
			this.parameters = GGWaveLib.INSTANCE.ggwave_getDefaultParameters();
			this.parameters.sampleFormatInp = GGWaveLib.GGWAVE_SAMPLE_FORMAT_I16;
			this.parameters.sampleFormatOut = GGWaveLib.GGWAVE_SAMPLE_FORMAT_I16;

			if (this.parameters.payloadLength > 0) {
				throw new IllegalStateException("GGWave fixed-length payload mode is not supported; payloadLength must be <= 0");
			}

			if (this.parameters.sampleRateInp <= 0 || this.parameters.sampleRateOut <= 0) {
				throw new IllegalStateException("GGWave returned invalid sample rates");
			}

			this.ggwaveInstance = GGWaveLib.INSTANCE.ggwave_init(this.parameters);
			if (this.ggwaveInstance < 0) {
				throw new IllegalStateException("ggwave_init failed: " + this.ggwaveInstance);
			}

			AudioFormat inputFormat = new AudioFormat(this.parameters.sampleRateInp, 16, 1, true, false);
			AudioFormat outputFormat = new AudioFormat(this.parameters.sampleRateOut, 16, 1, true, false);

			DataLine.Info inputInfo = new DataLine.Info(TargetDataLine.class, inputFormat);
			DataLine.Info outputInfo = new DataLine.Info(SourceDataLine.class, outputFormat);

			if (!AudioSystem.isLineSupported(inputInfo)) {
				throw new IllegalStateException("No microphone supports GGWave input format: " + inputFormat);
			}
			if (!AudioSystem.isLineSupported(outputInfo)) {
				throw new IllegalStateException("No speaker supports GGWave output format: " + outputFormat);
			}

			this.microphone = (TargetDataLine) AudioSystem.getLine(inputInfo);
			this.speaker = (SourceDataLine) AudioSystem.getLine(outputInfo);

			int inputBufferBytes = this.audioBufferSamples * inputFormat.getFrameSize();
			this.microphone.open(inputFormat, inputBufferBytes * 2);
			this.speaker.open(outputFormat);

			this.microphone.start();
			this.speaker.start();

			this.status = true;
			this.lastException = null;

			this.receiverThread = new Thread(this::receiveLoop, "SEDAPExpress-GGWave-Receiver");
			this.receiverThread.setDaemon(true);
			this.receiverThread.start();

			String statusMessage = "GGWave communicator started (input=" + this.parameters.sampleRateInp + " Hz, output=" + this.parameters.sampleRateOut + " Hz, I16 mono)";
			logger.logp(Level.INFO, "SEDAPExpressGGWaveClient", "connect()", statusMessage);
			logInput(statusMessage);
			return true;

		} catch (Exception | UnsatisfiedLinkError e) {
			this.lastException = (e instanceof Exception) ? (Exception) e : new IllegalStateException("Could not load or initialize ggwave native library", e);
			logger.logp(Level.SEVERE, "SEDAPExpressGGWaveClient", "connect()", "Could not start GGWave communicator", e);
			logInput("Could not start GGWave communicator: " + e.getMessage());
			releaseResources();
			return false;
		}
	}

	private void receiveLoop() {
		byte[] audioBuffer = new byte[this.audioBufferSamples * 2]; // mono I16 = 2 bytes/sample
		byte[] decodedBuffer = new byte[DEFAULT_DECODE_BUFFER_SIZE];

		while (this.status && !Thread.currentThread().isInterrupted()) {
			try {
				int bytesRead = this.microphone.read(audioBuffer, 0, audioBuffer.length);
				if (bytesRead <= 0) {
					continue;
				}

				int decodedSize;
				synchronized (this.codecLock) {
					if (!this.status || this.ggwaveInstance < 0) {
						break;
					}
					decodedSize = GGWaveLib.INSTANCE.ggwave_decode(this.ggwaveInstance, audioBuffer, bytesRead, decodedBuffer);
				}

				if (decodedSize > 0) {
					processReceivedMessage(decodedBuffer, decodedSize);
				}
			} catch (Exception | UnsatisfiedLinkError e) {
				if (this.status) {
					this.lastException = (e instanceof Exception) ? (Exception) e : new IllegalStateException("GGWave decode failed", e);
					logger.logp(Level.WARNING, "SEDAPExpressGGWaveClient", "receiveLoop()", "Error while receiving acoustic data", e);
					logInput("GGWave receive error: " + e.getMessage());
				}
				break;
			}
		}
	}

	private void processReceivedMessage(byte[] payload, int payloadLength) {
		if (payloadLength <= 0 || payloadLength > payload.length) {
			return;
		}

		if (isFragment(payload, payloadLength)) {
			acceptFragment(payload, payloadLength);
			return;
		}

		// Backward compatibility for ordinary, unfragmented GGWave payloads.
		processCompleteMessage(Arrays.copyOf(payload, payloadLength));
	}

	private boolean isFragment(byte[] payload, int length) {
		return length >= FRAGMENT_HEADER_SIZE && payload[0] == FRAGMENT_MAGIC[0] && payload[1] == FRAGMENT_MAGIC[1] && payload[2] == FRAGMENT_MAGIC[2] && payload[3] == FRAGMENT_MAGIC[3]
				&& payload[4] == FRAGMENT_VERSION;
	}

	private void acceptFragment(byte[] frame, int frameLength) {
		ByteBuffer header = ByteBuffer.wrap(frame, 5, FRAGMENT_HEADER_SIZE - 5).order(ByteOrder.BIG_ENDIAN);

		long transferId = header.getLong();
		int fragmentIndex = Short.toUnsignedInt(header.getShort());
		int fragmentCount = Short.toUnsignedInt(header.getShort());
		int totalLength = header.getInt();
		long expectedCrc = Integer.toUnsignedLong(header.getInt());
		int fragmentLength = frameLength - FRAGMENT_HEADER_SIZE;

		if (fragmentCount < 1 || fragmentIndex >= fragmentCount || totalLength < 1 || totalLength > MAX_REASSEMBLED_MESSAGE_SIZE || fragmentLength < 1) {
			logger.logp(Level.WARNING, "SEDAPExpressGGWaveClient", "acceptFragment()", "Discarding invalid GGWave fragment header");
			return;
		}

		cleanupExpiredAssemblies();

		FragmentAssembly assembly = this.fragmentAssemblies.get(transferId);
		if (assembly == null) {
			if (this.fragmentAssemblies.size() >= MAX_CONCURRENT_REASSEMBLIES) {
				logger.logp(Level.WARNING, "SEDAPExpressGGWaveClient", "acceptFragment()", "Maximum number of concurrent GGWave reassemblies reached");
				return;
			}

			FragmentAssembly candidate = new FragmentAssembly(fragmentCount, totalLength, expectedCrc);
			FragmentAssembly existing = this.fragmentAssemblies.putIfAbsent(transferId, candidate);
			assembly = existing == null ? candidate : existing;
		}

		if (assembly.fragmentCount != fragmentCount || assembly.totalLength != totalLength || assembly.expectedCrc != expectedCrc) {
			this.fragmentAssemblies.remove(transferId, assembly);
			logger.logp(Level.WARNING, "SEDAPExpressGGWaveClient", "acceptFragment()", "Inconsistent fragments; discarded transfer " + Long.toUnsignedString(transferId));
			return;
		}

		synchronized (assembly) {
			assembly.lastUpdatedNanos = System.nanoTime();

			if (assembly.fragments[fragmentIndex] == null) {
				byte[] data = Arrays.copyOfRange(frame, FRAGMENT_HEADER_SIZE, frameLength);
				if (assembly.receivedBytes + data.length > assembly.totalLength) {
					this.fragmentAssemblies.remove(transferId, assembly);
					return;
				}
				assembly.fragments[fragmentIndex] = data;
				assembly.receivedBytes += data.length;
				assembly.receivedFragments++;
			}

			if (assembly.receivedFragments != assembly.fragmentCount) {
				return;
			}

			if (assembly.receivedBytes != assembly.totalLength) {
				this.fragmentAssemblies.remove(transferId, assembly);
				logger.logp(Level.WARNING, "SEDAPExpressGGWaveClient", "acceptFragment()", "Reassembled length mismatch; discarded transfer " + Long.toUnsignedString(transferId));
				return;
			}

			byte[] complete = new byte[assembly.totalLength];
			int offset = 0;
			for (byte[] fragment : assembly.fragments) {
				if (fragment == null || offset + fragment.length > complete.length) {
					this.fragmentAssemblies.remove(transferId, assembly);
					return;
				}
				System.arraycopy(fragment, 0, complete, offset, fragment.length);
				offset += fragment.length;
			}

			this.fragmentAssemblies.remove(transferId, assembly);

			CRC32 crc = new CRC32();
			crc.update(complete);
			if (crc.getValue() != assembly.expectedCrc) {
				logger.logp(Level.WARNING, "SEDAPExpressGGWaveClient", "acceptFragment()", "CRC32 mismatch; discarded transfer " + Long.toUnsignedString(transferId));
				logInput("GGWave fragment reassembly failed: CRC32 mismatch");
				return;
			}

			processCompleteMessage(complete);
		}
	}

	private void cleanupExpiredAssemblies() {
		long now = System.nanoTime();
		this.fragmentAssemblies.forEach((id, assembly) -> {
			if (now - assembly.lastUpdatedNanos > REASSEMBLY_TIMEOUT_NANOS) {
				this.fragmentAssemblies.remove(id, assembly);
			}
		});
	}

	private void processCompleteMessage(byte[] payload) {
		String serialized = new String(payload, StandardCharsets.ISO_8859_1);
		for (String message : serialized.split("\\n")) {
			String candidate = message.trim();
			if (candidate.isEmpty()) {
				continue;
			}

			try {
				String[] fields = candidate.split(";", -1);
				if (fields.length > 0 && "ACKNOWLEDGE".equalsIgnoreCase(fields[0])) {
					processAcknowledgement(fields);
					logInput(candidate);
					continue;
				}

				SEDAPExpressMessage parsed = SEDAPExpressMessage.deserialize(candidate);
				if (parsed != null) {
					logInput(candidate);

					if (parsed.getAcknowledgement() == SEDAPExpressMessage.Acknowledgement.TRUE && parsed.getNumber() != null) {
						String duplicateKey = messageKey(parsed.getSender(), parsed.getMessageType().name(), parsed.getNumber());
						if (isRecentlyCompleted(duplicateKey)) {
							sendAcknowledgement(parsed);
							continue;
						}

						// ACK is sent only after the complete message was accepted by SEDAP.
						distributeReceivedSEDAPExpressMessage(parsed);
						rememberCompleted(duplicateKey);
						sendAcknowledgement(parsed);
					} else {
						distributeReceivedSEDAPExpressMessage(parsed);
					}
				}
			} catch (Exception e) {
				this.lastException = e;
				logger.logp(Level.WARNING, "SEDAPExpressGGWaveClient", "processCompleteMessage()", "Could not process received GGWave payload", e);
				logInput("Could not process GGWave payload: " + e.getMessage());
			}
		}
	}

	private void processAcknowledgement(String[] fields) {
		// ACKNOWLEDGE;<Number>;<Time>;<Sender>;<Classification>;;<MAC>;<Recipient>;<Type>;<NumberOfMessage>
		if (fields.length < 10 || fields[7].isBlank() || fields[8].isBlank() || fields[9].isBlank()) {
			return;
		}
		if (!this.localSenderId.equals(fields[7])) {
			return;
		}
		String key = fields[8].toUpperCase(java.util.Locale.ROOT) + ":" + fields[9].toUpperCase(java.util.Locale.ROOT);
		CompletableFuture<Void> pending = this.pendingAcknowledgements.get(key);
		if (pending != null) {
			pending.complete(null);
		}
	}

	private void sendAcknowledgement(SEDAPExpressMessage received) throws IOException {
		if (received.getNumber() == null || received.getSender() == null || received.getSender().isBlank()) {
			return;
		}

		String number = String.format(java.util.Locale.ROOT, "%02X", received.getNumber() & 0x7F);
		String ackNumber = String.format(java.util.Locale.ROOT, "%02X", this.acknowledgeSequence++ & 0x7F);
		String time = String.format(java.util.Locale.ROOT, "%012X", System.currentTimeMillis());
		String type = received.getMessageType().name();
		String ack = "ACKNOWLEDGE;" + ackNumber + ";" + time + ";" + this.localSenderId + ";" + this.localClassification + ";;;" + received.getSender() + ";" + type + ";" + number + "\n";
		byte[] bytes = ack.getBytes(StandardCharsets.ISO_8859_1);

		synchronized (this.transmitLock) {
			if (!this.status || this.ggwaveInstance < 0) {
				return;
			}
			transmitMessageBytes(bytes);
		}
		logOutput(ack.trim());
	}

	private static String messageKey(String sender, String type, byte number) {
		return (sender == null ? "" : sender) + "|" + type + "|" + String.format(java.util.Locale.ROOT, "%02X", number & 0x7F);
	}

	private boolean isRecentlyCompleted(String key) {
		Long completedAt = this.completedAcknowledgedMessages.get(key);
		return completedAt != null && System.nanoTime() - completedAt < COMPLETED_MESSAGE_CACHE_NANOS;
	}

	private void rememberCompleted(String key) {
		if (this.completedAcknowledgedMessages.size() >= MAX_COMPLETED_MESSAGES) {
			long cutoff = System.nanoTime() - COMPLETED_MESSAGE_CACHE_NANOS;
			this.completedAcknowledgedMessages.entrySet().removeIf(entry -> entry.getValue() < cutoff);
			if (this.completedAcknowledgedMessages.size() >= MAX_COMPLETED_MESSAGES) {
				String oldest = this.completedAcknowledgedMessages.entrySet().stream().min(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElse(null);
				if (oldest != null) {
					this.completedAcknowledgedMessages.remove(oldest);
				}
			}
		}
		this.completedAcknowledgedMessages.put(key, System.nanoTime());
	}

	private static String setAcknowledgementRequested(String serialized) {
		int newline = serialized.indexOf('\n');
		String line = newline >= 0 ? serialized.substring(0, newline) : serialized;
		String suffix = newline >= 0 ? serialized.substring(newline) : "";
		String[] fields = line.split(";", -1);
		if (fields.length < 7) {
			throw new IllegalArgumentException("Invalid serialized SEDAP-Express message header");
		}
		fields[5] = "TRUE";
		return String.join(";", fields) + suffix;
	}

	private static final class FragmentAssembly {
		private final int fragmentCount;
		private final int totalLength;
		private final long expectedCrc;
		private final byte[][] fragments;
		private int receivedFragments;
		private int receivedBytes;
		private volatile long lastUpdatedNanos = System.nanoTime();

		private FragmentAssembly(int fragmentCount, int totalLength, long expectedCrc) {
			this.fragmentCount = fragmentCount;
			this.totalLength = totalLength;
			this.expectedCrc = expectedCrc;
			this.fragments = new byte[fragmentCount][];
		}
	}

	private byte[][] fragmentPayload(byte[] payload) throws IOException {
		if (payload.length == 0) {
			throw new IOException("Cannot transmit an empty SEDAP-Express payload");
		}
		if (payload.length > MAX_REASSEMBLED_MESSAGE_SIZE) {
			throw new IOException("SEDAP-Express payload exceeds maximum reassembly size of " + MAX_REASSEMBLED_MESSAGE_SIZE + " bytes");
		}

		int count = (payload.length + this.fragmentDataSize - 1) / this.fragmentDataSize;
		if (count > 0xFFFF) {
			throw new IOException("SEDAP-Express payload requires too many GGWave fragments");
		}

		CRC32 crc = new CRC32();
		crc.update(payload);
		long transferId = ThreadLocalRandom.current().nextLong();
		int crcValue = (int) crc.getValue();

		byte[][] frames = new byte[count][];
		for (int index = 0; index < count; index++) {
			int offset = index * this.fragmentDataSize;
			int length = Math.min(this.fragmentDataSize, payload.length - offset);

			ByteBuffer frame = ByteBuffer.allocate(FRAGMENT_HEADER_SIZE + length).order(ByteOrder.BIG_ENDIAN);
			frame.put(FRAGMENT_MAGIC);
			frame.put(FRAGMENT_VERSION);
			frame.putLong(transferId);
			frame.putShort((short) index);
			frame.putShort((short) count);
			frame.putInt(payload.length);
			frame.putInt(crcValue);
			frame.put(payload, offset, length);
			frames[index] = frame.array();
		}
		return frames;
	}

	private void transmitMessageBytes(byte[] payload) throws IOException {
		if (payload.length <= this.fragmentDataSize) {
			transmitPayload(payload);
			return;
		}
		for (byte[] fragment : fragmentPayload(payload)) {
			if (!this.status) {
				throw new IOException("GGWave communicator stopped during transmission");
			}
			transmitPayload(fragment);
		}
	}

	private void transmitPayload(byte[] payload) throws IOException {
		int waveformSize;
		synchronized (this.codecLock) {
			waveformSize = GGWaveLib.INSTANCE.ggwave_encode(this.ggwaveInstance, payload, payload.length, this.txProtocolId, this.txVolume, null, 1);
		}

		if (waveformSize <= 0) {
			throw new IOException("GGWave could not encode payload (size=" + payload.length + " bytes, result=" + waveformSize + ")");
		}

		byte[] waveform = new byte[waveformSize];
		int encodedSize;
		synchronized (this.codecLock) {
			encodedSize = GGWaveLib.INSTANCE.ggwave_encode(this.ggwaveInstance, payload, payload.length, this.txProtocolId, this.txVolume, waveform, 0);
		}

		if (encodedSize <= 0 || encodedSize > waveform.length) {
			throw new IOException("GGWave returned invalid waveform size: " + encodedSize);
		}

		int offset = 0;
		while (offset < encodedSize) {
			int written = this.speaker.write(waveform, offset, encodedSize - offset);
			if (written <= 0) {
				throw new IOException("Could not write GGWave waveform to audio output");
			}
			offset += written;
		}
		this.speaker.drain();
	}

	/**
	 * Sends the supplied message through the configured transport.
	 * @param message value for message
	 * @return {@code true} if the condition is satisfied; otherwise {@code false}.
	 * @throws IOException if the operation cannot be completed because of this condition
	 */
	@Override
	public boolean sendSEDAPExpressMessage(SEDAPExpressMessage message) throws IOException {
		if (message == null) {
			throw new IllegalArgumentException("message must not be null");
		}
		if (!this.status || this.ggwaveInstance < 0 || this.speaker == null) {
			throw new IOException("GGWave communicator is not connected");
		}

		String serialized = SEDAPExpressMessage.serialize(message);
		if (message.getAcknowledgement() == SEDAPExpressMessage.Acknowledgement.TRUE && message.getNumber() == null) {
			throw new IOException("SEDAP-Express acknowledgement requires a message number");
		}
		boolean requestAck = message.getAcknowledgement() == SEDAPExpressMessage.Acknowledgement.TRUE;
		String wireMessage = serialized;
		String ackKey = message.getMessageType().name() + ":" + String.format(java.util.Locale.ROOT, "%02X", message.getNumber() == null ? 0 : message.getNumber() & 0x7F);
		CompletableFuture<Void> acknowledgement = null;

		if (requestAck) {
			wireMessage = setAcknowledgementRequested(serialized);
			acknowledgement = new CompletableFuture<>();
			CompletableFuture<Void> previous = this.pendingAcknowledgements.putIfAbsent(ackKey, acknowledgement);
			if (previous != null) {
				throw new IOException("An acknowledgement for " + ackKey + " is already pending");
			}
		}

		try {
			int attempts = requestAck ? MAX_ACK_ATTEMPTS : 1;
			for (int attempt = 1; attempt <= attempts; attempt++) {
				synchronized (this.transmitLock) {
					if (!this.status) {
						throw new IOException("GGWave communicator stopped during transmission");
					}
					transmitMessageBytes(wireMessage.getBytes(StandardCharsets.ISO_8859_1));
				}

				if (!requestAck) {
					logOutput(serialized);
					return true;
				}

				try {
					acknowledgement.get(ACK_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
					logOutput(serialized);
					return true;
				} catch (TimeoutException e) {
					if (attempt == attempts) {
						throw new IOException("No SEDAP-Express ACKNOWLEDGE received for " + ackKey + " after " + attempts + " attempts", e);
					}
					logger.logp(Level.WARNING, "SEDAPExpressGGWaveClient", "sendSEDAPExpressMessage()", "ACK timeout for " + ackKey + "; retrying (" + (attempt + 1) + "/" + attempts + ")");
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					throw new IOException("Interrupted while waiting for SEDAP-Express acknowledgement", e);
				} catch (java.util.concurrent.ExecutionException e) {
					throw new IOException("Could not wait for SEDAP-Express acknowledgement", e);
				}
			}
			return false;
		} catch (IOException e) {
			this.lastException = e;
			throw e;
		} catch (RuntimeException | UnsatisfiedLinkError e) {
			this.lastException = (e instanceof Exception) ? (Exception) e : new IllegalStateException("GGWave encode failed", e);
			throw new IOException("Could not transmit SEDAP-Express message via GGWave", e);
		} finally {
			if (requestAck) {
				this.pendingAcknowledgements.remove(ackKey, acknowledgement);
			}
		}
	}

	/**
	 * Indicates whether the status condition is met.
	 * @return {@code true} if the condition is satisfied; otherwise {@code false}.
	 */
	public boolean isStatus() {
		return this.status;
	}

	/**
	 * Stops the communication resource and releases its active connection.
	 */
	@Override
	public synchronized void stopCommunicator() {
		if (!this.status && this.ggwaveInstance < 0) {
			return;
		}

		this.status = false;
		this.fragmentAssemblies.clear();
		this.pendingAcknowledgements.values().forEach(future -> future.completeExceptionally(new IOException("GGWave communicator stopped")));
		this.pendingAcknowledgements.clear();
		this.completedAcknowledgedMessages.clear();

		if (this.microphone != null) {
			this.microphone.stop();
			this.microphone.close();
			this.microphone = null;
		}
		if (this.speaker != null) {
			this.speaker.stop();
			this.speaker.flush();
			this.speaker.close();
			this.speaker = null;
		}

		if (this.receiverThread != null && this.receiverThread != Thread.currentThread()) {
			this.receiverThread.interrupt();
			try {
				this.receiverThread.join(1_000);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
			this.receiverThread = null;
		}

		synchronized (this.codecLock) {
			if (this.ggwaveInstance >= 0) {
				GGWaveLib.INSTANCE.ggwave_free(this.ggwaveInstance);
				this.ggwaveInstance = -1;
			}
		}

		logger.logp(Level.INFO, "SEDAPExpressGGWaveClient", "stopCommunicator()", "GGWave communicator stopped");
		logInput("GGWave communicator stopped");
	}

	private void releaseResources() {
		this.status = false;
		this.fragmentAssemblies.clear();
		this.pendingAcknowledgements.values().forEach(future -> future.completeExceptionally(new IOException("GGWave communicator stopped")));
		this.pendingAcknowledgements.clear();
		this.completedAcknowledgedMessages.clear();

		if (this.microphone != null) {
			try {
				this.microphone.close();
			} catch (Exception ignored) {
			}
			this.microphone = null;
		}
		if (this.speaker != null) {
			try {
				this.speaker.close();
			} catch (Exception ignored) {
			}
			this.speaker = null;
		}
		if (this.ggwaveInstance >= 0) {
			try {
				GGWaveLib.INSTANCE.ggwave_free(this.ggwaveInstance);
			} catch (RuntimeException | UnsatisfiedLinkError ignored) {
			}
			this.ggwaveInstance = -1;
		}
	}

	/**
	 * Returns the last exception.
	 * @return the last exception
	 */
	@Override
	public Exception getLastException() {
		return this.lastException;
	}
}
