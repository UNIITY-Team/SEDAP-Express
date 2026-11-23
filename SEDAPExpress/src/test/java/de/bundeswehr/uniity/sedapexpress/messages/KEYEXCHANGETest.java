package de.bundeswehr.uniity.sedapexpress.messages;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HexFormat;

import org.bouncycastle.crypto.agreement.DHStandardGroups;
import org.bouncycastle.crypto.params.DHParameters;

import org.junit.jupiter.api.Test;

import de.bundeswehr.uniity.sedapexpress.messages.KEYEXCHANGE.AESKeyLength;
import de.bundeswehr.uniity.sedapexpress.messages.KEYEXCHANGE.AlgorithmType;
import de.bundeswehr.uniity.sedapexpress.messages.KEYEXCHANGE.DHParameterLength;
import de.bundeswehr.uniity.sedapexpress.messages.KEYEXCHANGE.ExchangePhase;
import de.bundeswehr.uniity.sedapexpress.messages.SEDAPExpressMessage.Acknowledgement;
import de.bundeswehr.uniity.sedapexpress.messages.SEDAPExpressMessage.Classification;
import de.bundeswehr.uniity.sedapexpress.utils.MACUtils;

class KEYEXCHANGETest {

	private static final String HEADER = "KEYEXCHANGE;10;0195238E25AD;NODE;U;;;PEER;";
	private static final byte[] SALT = "01234567890123456789012345678901".getBytes(StandardCharsets.US_ASCII);
	private static final byte[] PUBLIC_DATA = { 0x01, 0x23, 0x45, 0x67 };
	private static final String SALT_BASE64 = Base64.getEncoder().encodeToString(SALT);
	private static final String PUBLIC_DATA_BASE64 = Base64.getEncoder().encodeToString(PUBLIC_DATA);

	@Test
	void defaultConstructorInitializesAllFieldsToNull() {
		KEYEXCHANGE message = new KEYEXCHANGE();

		assertNull(message.getRecipient());
		assertNull(message.getAlgorithmType());
		assertNull(message.getPhase());
		assertNull(message.getAesKeyLength());
		assertNull(message.getDhParameterLength());
		assertNull(message.getPrimeNumber());
		assertNull(message.getNaturalNumber());
		assertNull(message.getSalt());
		assertNull(message.getPublicData());
	}

	@Test
	void algorithmTypeMappingsCoverAllDefinedValuesAndRejectUnknownValues() {
		AlgorithmType[] expected = { AlgorithmType.Diffie_Hellman_Merkle, AlgorithmType.Diffie_Hellman_Merkle_with_BrainpoolP256r1, AlgorithmType.MLKEM512, AlgorithmType.MLKEM768,
				AlgorithmType.MLKEM1024, AlgorithmType.FrodoKEM976, AlgorithmType.FrodoKEM1344 };

		for (int i = 0; i < expected.length; i++) {
			assertEquals(i, expected[i].getAlgorithmTypeValue());
			assertEquals(expected[i], AlgorithmType.valueOfAlgorithmType(i));
			assertEquals(Integer.toString(i), expected[i].toString());
		}

		assertThrows(IllegalArgumentException.class, () -> AlgorithmType.valueOfAlgorithmType(-1));
		assertThrows(IllegalArgumentException.class, () -> AlgorithmType.valueOfAlgorithmType(99));
	}

	@Test
	void valueConstructorPreservesAllFieldsIncludingPublicData() {
		byte[] salt = SALT.clone();
		byte[] publicData = PUBLIC_DATA.clone();
		BigInteger prime = new BigInteger("D5BBB96D30086EC484EBA3D7F9CAEB07", 16);
		BigInteger generator = BigInteger.TWO;

		KEYEXCHANGE message = new KEYEXCHANGE((byte) 0x10, 0x0195238E25ADL, "NODE", Classification.Secret, Acknowledgement.TRUE, "AABB", "PEER", AlgorithmType.Diffie_Hellman_Merkle,
				ExchangePhase.Init, AESKeyLength.KEY_LENGTH_256_BITS, DHParameterLength.Parameter_LENGTH_2048_BITS, prime, generator, salt, publicData);

		assertEquals("PEER", message.getRecipient());
		assertEquals(AlgorithmType.Diffie_Hellman_Merkle, message.getAlgorithmType());
		assertEquals(ExchangePhase.Init, message.getPhase());
		assertEquals(AESKeyLength.KEY_LENGTH_256_BITS, message.getAesKeyLength());
		assertEquals(DHParameterLength.Parameter_LENGTH_2048_BITS, message.getDhParameterLength());
		assertEquals(prime, message.getPrimeNumber());
		assertEquals(generator, message.getNaturalNumber());
		assertArrayEquals(salt, message.getSalt());
		assertArrayEquals(publicData, message.getPublicData());
	}

	@Test
	void settersUpdateAllCurrentFields() {
		KEYEXCHANGE message = new KEYEXCHANGE();
		message.setRecipient("PEER");
		message.setAlgorithmType(AlgorithmType.MLKEM768);
		message.setPhase(ExchangePhase.Final);
		message.setAesKeyLength(AESKeyLength.KEY_LENGTH_128_BITS);
		message.setDhParameterLength(DHParameterLength.Parameter_LENGTH_3072_BITS);
		message.setPrimeNumber(BigInteger.valueOf(17));
		message.setNaturalNumber(BigInteger.TWO);
		message.setSalt(SALT.clone());
		message.setPublicData(PUBLIC_DATA.clone());

		assertEquals("PEER", message.getRecipient());
		assertEquals(AlgorithmType.MLKEM768, message.getAlgorithmType());
		assertEquals(ExchangePhase.Final, message.getPhase());
		assertEquals(AESKeyLength.KEY_LENGTH_128_BITS, message.getAesKeyLength());
		assertEquals(DHParameterLength.Parameter_LENGTH_3072_BITS, message.getDhParameterLength());
		assertEquals(BigInteger.valueOf(17), message.getPrimeNumber());
		assertEquals(BigInteger.TWO, message.getNaturalNumber());
		assertArrayEquals(SALT, message.getSalt());
		assertArrayEquals(PUBLIC_DATA, message.getPublicData());
	}

	@Test
	void parserSupportsEveryAlgorithmIdentifier() {
		String confirmationMac = "0123456789ABCDEF0123456789ABCDEF";

		for (int algorithm = 0; algorithm <= 6; algorithm++) {
			String wire = "KEYEXCHANGE;10;0195238E25AD;NODE;U;;" + confirmationMac + ";PEER;" + algorithm + ";2;;;;;;";
			KEYEXCHANGE parsed = new KEYEXCHANGE(wire);

			assertEquals(AlgorithmType.valueOfAlgorithmType(algorithm), parsed.getAlgorithmType(), "Algorithm identifier " + algorithm + " must be parsed");
			assertEquals(ExchangePhase.Final, parsed.getPhase());
			assertEquals(confirmationMac, parsed.getMAC());
		}
	}

	@Test
	void parsesDhPhaseZeroParametersWithoutSaltOrPublicData() {
		DHParameters group = DHStandardGroups.rfc7919_ffdhe2048;
		String primeHex = HexFormat.of().withUpperCase().formatHex(group.getP().toByteArray());
		String generatorHex = HexFormat.of().withUpperCase().formatHex(group.getG().toByteArray());
		String wire = HEADER + "0;0;256;2048;" + primeHex + ";" + generatorHex;

		KEYEXCHANGE parsed = new KEYEXCHANGE(wire);

		assertEquals(AlgorithmType.Diffie_Hellman_Merkle, parsed.getAlgorithmType());
		assertEquals(ExchangePhase.Init, parsed.getPhase());
		assertEquals(AESKeyLength.KEY_LENGTH_256_BITS, parsed.getAesKeyLength());
		assertEquals(DHParameterLength.Parameter_LENGTH_2048_BITS, parsed.getDhParameterLength());
		assertEquals(group.getP(), parsed.getPrimeNumber());
		assertEquals(group.getG(), parsed.getNaturalNumber());
		assertEquals("PEER", parsed.getRecipient());
		assertNull(parsed.getSalt());
		assertNull(parsed.getPublicData());
	}

	@Test
	void parsesDhPhaseOneSaltAndPublicVariable() {
		String wire = HEADER + "0;1;;;;;" + SALT_BASE64 + ";" + PUBLIC_DATA_BASE64;

		KEYEXCHANGE parsed = new KEYEXCHANGE(wire);

		assertEquals(AlgorithmType.Diffie_Hellman_Merkle, parsed.getAlgorithmType());
		assertEquals(ExchangePhase.Compute, parsed.getPhase());
		assertNull(parsed.getAesKeyLength());
		assertNull(parsed.getDhParameterLength());
		assertArrayEquals(SALT, parsed.getSalt());
		assertArrayEquals(PUBLIC_DATA, parsed.getPublicData());
	}

	@Test
	void parsesEcdhPhaseZeroAndPhaseOnePublicKeys() {
		String phaseZeroWire = HEADER + "1;0;128;;;;" + SALT_BASE64 + ";" + PUBLIC_DATA_BASE64;
		KEYEXCHANGE phaseZero = new KEYEXCHANGE(phaseZeroWire);

		assertEquals(AlgorithmType.Diffie_Hellman_Merkle_with_BrainpoolP256r1, phaseZero.getAlgorithmType());
		assertEquals(ExchangePhase.Init, phaseZero.getPhase());
		assertEquals(AESKeyLength.KEY_LENGTH_128_BITS, phaseZero.getAesKeyLength());
		assertArrayEquals(SALT, phaseZero.getSalt());
		assertArrayEquals(PUBLIC_DATA, phaseZero.getPublicData());

		String phaseOneWire = HEADER + "1;1;;;;;;" + PUBLIC_DATA_BASE64;
		KEYEXCHANGE phaseOne = new KEYEXCHANGE(phaseOneWire);

		assertEquals(ExchangePhase.Compute, phaseOne.getPhase());
		assertNull(phaseOne.getSalt());
		assertArrayEquals(PUBLIC_DATA, phaseOne.getPublicData());
	}

	@Test
	void parsesKemPublicKeyInPhaseZeroAndCiphertextInPhaseOneIntoPublicData() {
		for (int algorithm = 2; algorithm <= 6; algorithm++) {
			String phaseZeroWire = HEADER + algorithm + ";0;256;;;;" + SALT_BASE64 + ";" + PUBLIC_DATA_BASE64;
			KEYEXCHANGE phaseZero = new KEYEXCHANGE(phaseZeroWire);

			assertEquals(AlgorithmType.valueOfAlgorithmType(algorithm), phaseZero.getAlgorithmType());
			assertEquals(ExchangePhase.Init, phaseZero.getPhase());
			assertArrayEquals(SALT, phaseZero.getSalt());
			assertArrayEquals(PUBLIC_DATA, phaseZero.getPublicData(), "Phase 0 carries the KEM public key");

			String phaseOneWire = HEADER + algorithm + ";1;;;;;;" + PUBLIC_DATA_BASE64;
			KEYEXCHANGE phaseOne = new KEYEXCHANGE(phaseOneWire);

			assertEquals(ExchangePhase.Compute, phaseOne.getPhase());
			assertNull(phaseOne.getSalt());
			assertArrayEquals(PUBLIC_DATA, phaseOne.getPublicData(), "Phase 1 carries the KEM encapsulation ciphertext");
		}
	}

	@Test
	void rejectsSaltWhoseDecodedLengthIsNotExactlyThirtyTwoBytes() {
		byte[] shortSalt = new byte[16];
		String shortSaltBase64 = Base64.getEncoder().encodeToString(shortSalt);
		String wire = HEADER + "1;0;128;;;;" + shortSaltBase64 + ";" + PUBLIC_DATA_BASE64;

		assertThrows(IllegalArgumentException.class, () -> new KEYEXCHANGE(wire));
	}

	@Test
	void rejectsDhParametersThatDoNotMatchTheSelectedRfc7919Group() {
		String customPrime = "F".repeat(512);
		String wire = HEADER + "0;0;128;2048;" + customPrime + ";02";
		assertThrows(IllegalArgumentException.class, () -> new KEYEXCHANGE(wire));
	}

	@Test
	void rejectsMissingPhaseInsteadOfReturningAnIncompleteMessage() {
		String wire = HEADER + "0";
		assertThrows(IllegalArgumentException.class, () -> new KEYEXCHANGE(wire));
	}

	@Test
	void rejectsPhaseTwoMacWithWrongLength() {
		String wire = "KEYEXCHANGE;10;0195238E25AD;NODE;U;;1234;PEER;3;2";
		assertThrows(IllegalArgumentException.class, () -> new KEYEXCHANGE(wire));
	}

	@Test
	void phaseTwoCarriesConfirmationInHeaderAndNoPublicData() {
		String confirmationMac = "0123456789ABCDEF0123456789ABCDEF";
		String wire = "KEYEXCHANGE;10;0195238E25AD;NODE;U;;" + confirmationMac + ";PEER;3;2;;;;;;";

		KEYEXCHANGE parsed = new KEYEXCHANGE(wire);

		assertEquals(AlgorithmType.MLKEM768, parsed.getAlgorithmType());
		assertEquals(ExchangePhase.Final, parsed.getPhase());
		assertEquals(confirmationMac, parsed.getMAC());
		assertNull(parsed.getSalt());
		assertNull(parsed.getPublicData());
	}

	@Test
	void phaseTwoHmac128CanBeGeneratedSerializedParsedAndVerified() {
		byte[] key = new byte[32];
		java.util.Arrays.fill(key, (byte) 0x5A);
		KEYEXCHANGE message = new KEYEXCHANGE((byte) 0x10, 0x0195238E25ADL, "NODE", Classification.Unclas, Acknowledgement.FALSE, null, "PEER",
				AlgorithmType.MLKEM768, ExchangePhase.Final, null, null, null, null, null, null);

		MACUtils.setMACFieldWith128BitHMAC(key, message);
		assertEquals(32, message.getMAC().length());

		KEYEXCHANGE parsed = new KEYEXCHANGE(message.toString());
		assertEquals(ExchangePhase.Final, parsed.getPhase());
		assertTrue(MACUtils.isMessageAuthenticWith128BitHMAC(key, parsed));
	}

	@Test
	void serializationPreservesRecipientAndPublicDataAndRoundTrips() {
		KEYEXCHANGE message = new KEYEXCHANGE((byte) 0x10, 0x0195238E25ADL, "NODE", Classification.Secret, Acknowledgement.FALSE, null, "PEER",
				AlgorithmType.MLKEM768, ExchangePhase.Init, AESKeyLength.KEY_LENGTH_256_BITS, null, null, null, SALT.clone(), PUBLIC_DATA.clone());

		String serialized = message.toString();
		assertTrue(serialized.contains(";PEER;3;0;256;"));
		assertTrue(serialized.endsWith(";" + SALT_BASE64 + ";" + PUBLIC_DATA_BASE64));

		KEYEXCHANGE parsed = new KEYEXCHANGE(serialized);
		assertEquals("PEER", parsed.getRecipient());
		assertEquals(AlgorithmType.MLKEM768, parsed.getAlgorithmType());
		assertArrayEquals(SALT, parsed.getSalt());
		assertArrayEquals(PUBLIC_DATA, parsed.getPublicData());
	}
}
