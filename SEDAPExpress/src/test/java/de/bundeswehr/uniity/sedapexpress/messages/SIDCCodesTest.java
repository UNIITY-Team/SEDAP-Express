package de.bundeswehr.uniity.sedapexpress.messages;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class SIDCCodesTest {

    @Test
    void eachDomainListReferencesItsCorrespondingArray() {
        assertEquals(java.util.Arrays.asList(SIDCCodes.spaceCodesArray), SIDCCodes.spaceCodesList);
        assertEquals(java.util.Arrays.asList(SIDCCodes.airCodesArray), SIDCCodes.airCodesList);
        assertEquals(java.util.Arrays.asList(SIDCCodes.surfaceCodesArray), SIDCCodes.surfaceCodesList);
        assertEquals(java.util.Arrays.asList(SIDCCodes.subsurfaceCodesArray), SIDCCodes.subsurfaceCodesList);
        assertEquals(java.util.Arrays.asList(SIDCCodes.groundCodesArray), SIDCCodes.groundCodesList);
    }

    @Test
    void domainListsArePopulatedAndContainExpectedRootEntries() {
        assertFalse(SIDCCodes.spaceCodesList.isEmpty());
        assertFalse(SIDCCodes.airCodesList.isEmpty());
        assertFalse(SIDCCodes.surfaceCodesList.isEmpty());
        assertFalse(SIDCCodes.subsurfaceCodesList.isEmpty());
        assertFalse(SIDCCodes.groundCodesList.isEmpty());

        assertTrue(SIDCCodes.spaceCodesList.contains("s-----|Satellite"));
        assertTrue(SIDCCodes.airCodesList.contains("m-----|MILITARY"));
        assertTrue(SIDCCodes.surfaceCodesList.contains("c-----|Combatant"));
        assertTrue(SIDCCodes.subsurfaceCodesList.contains("s-----|Submarine"));
        assertTrue(SIDCCodes.groundCodesList.contains("u-----|Unit"));
    }

    @Test
    void codeEntriesUseExpectedDelimiterAndDescription() {
        for (String[] entries : new String[][] {
                SIDCCodes.spaceCodesArray, SIDCCodes.airCodesArray, SIDCCodes.surfaceCodesArray,
                SIDCCodes.subsurfaceCodesArray, SIDCCodes.groundCodesArray }) {
            for (String entry : entries) {
                assertNotNull(entry);
                assertTrue(entry.contains("|"), () -> "Missing delimiter in entry: " + entry);
                String[] parts = entry.split("\\|", -1);
                assertEquals(2, parts.length);
                assertFalse(parts[0].isEmpty());
                assertFalse(parts[1].isEmpty());
            }
        }
    }
}
