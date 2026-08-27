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
package de.bundeswehr.sedap.express.tool.msgcreator;

import java.util.function.UnaryOperator;

import javafx.scene.control.TextFormatter;

public abstract class SEDAPExpressFormatter {

    private static final String ALLOWED_CHARACTERS = "[a-zA-Z]*";
    private static final String ALLOWED_HEX = "[a-FA-F]0-9*";

    protected static UnaryOperator<TextFormatter.Change> getDigitFilter(int max) {

	return change -> {
	    String newText = change.getControlNewText();
	    if (newText.matches("\\d*")) {
		if ((newText.length() <= max) || (max == 0)) {
		    return change;
		} else {
		    return null;
		}
	    }
	    return null;
	};
    }

    /**
     * 
     * @param max = Anzahl der max erlaubten Stellen, bei 0 = unbegrenzt
     * @return Formatter, der nur Ziffern zulässt bis zur max Anzahl
     */
    public static TextFormatter<String> getDigitFormatter(int max) {
	return new TextFormatter<>(SEDAPExpressFormatter.getDigitFilter(max));
    }

    protected static UnaryOperator<TextFormatter.Change> getCharFilter(int max) {

	return change -> {
	    String newText = change.getControlNewText();
	    if (newText.matches(SEDAPExpressFormatter.ALLOWED_CHARACTERS)) {
		if ((newText.length() <= max) || (max == 0)) {
		    return change;
		} else {
		    return null;
		}
	    }
	    return null;
	};
    }

    /**
     * 
     * @param max = Anzahl der max erlaubten Stellen, bei 0 = unbegrenzt
     * @return Formatter, der nur hexadezimale Werte zulässt bis zur max Anzahl
     */
    public static TextFormatter<String> getCharFormatter(int max) {
	return new TextFormatter<>(SEDAPExpressFormatter.getCharFilter(max));
    }

    protected static UnaryOperator<TextFormatter.Change> getHexFilter(int max) {

	return change -> {
	    String newText = change.getControlNewText();
	    if (newText.matches(SEDAPExpressFormatter.ALLOWED_HEX)) {
		if ((newText.length() <= max) || (max == 0)) {
		    change.setText(change.getText().toUpperCase());
		    return change;
		} else {
		    return null;
		}
	    }
	    return null;
	};
    }

    /**
     * 
     * @param max = Anzahl der max erlaubten Stellen, bei 0 = unbegrenzt
     * @return Formatter, der nur Ziffern zulässt bis zur max Anzahl
     */
    public static TextFormatter<String> getHexFormatter(int max) {
	return new TextFormatter<>(SEDAPExpressFormatter.getHexFilter(max));
    }

}
