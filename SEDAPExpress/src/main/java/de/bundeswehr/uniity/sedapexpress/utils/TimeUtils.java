/*******************************************************************************
 * Copyright (C)2012-2024, German Federal Armed Forces - All rights reserved.
 *
 * MUKdo II
 * Wibbelhofstraße 3
 * 26384 Wilhelmshaven
 * Germany
 *
 * This source code is part of the MEDAS/SEDAP Project.
 * Person of contact (POC): Volker Voß, MUKdo II A, Wilhelmshaven
 *
 * Unauthorized use, modification, redistributing, copying, selling and
 * printing of this file in source and binary form including accompanying
 * materials is STRICTLY prohibited.
 *
 * This source code and it's parts is classified as OFFEN / NATO UNCLASSIFIED!
 *******************************************************************************/
package de.bundeswehr.uniity.sedapexpress.utils;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * Represents or provides the time utils functionality.
 */
public class TimeUtils {

	/**
	 * Sets the local system time, provided the required user permissions are available.
	 * No sudo or password prompt is issued. If setting the system time fails due to
	 * insufficient permissions, the Java process must be granted the required privileges.
	 *
	 * @param timestampInMillis Unix timestamp in milliseconds
	 * @return true if the system time was successfully set; false otherwise
	 */
	public static boolean setLocalTime(final long timestampInMillis) {

		final String os = System.getProperty("os.name").toLowerCase();
		final Instant instant = Instant.ofEpochMilli(timestampInMillis);

		System.out.println("Old system time: \"" + new SimpleDateFormat("MMM dd yyyy HH:mm:ss.SSS").format(new Date()) + "\"");

		final ProcessBuilder processBuilder;

		if (os.contains("windows")) {

			processBuilder = new ProcessBuilder("powershell.exe", "-NoProfile", "-NonInteractive", "-Command",
					"$ErrorActionPreference = 'Stop'; " + "$dt = [DateTimeOffset]::FromUnixTimeMilliseconds(" + timestampInMillis + ").LocalDateTime; " + "Set-Date -Date $dt");

		} else if (os.contains("mac")) {

			final String formattedDate = instant.atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("MMddHHmmyy.ss"));

			processBuilder = new ProcessBuilder("date", formattedDate);

		} else if (os.contains("nix") || os.contains("nux") || os.contains("aix") || os.contains("sunos") || os.contains("bsd")) {

			// Linux: Set system time directly using the Unix timestamp in UTC.
			// The local time zone is automatically applied when displaying the time.
			final String unixTimestamp = BigDecimal.valueOf(timestampInMillis, 3).toPlainString();

			processBuilder = new ProcessBuilder("date", "-s", "@" + unixTimestamp);

		} else {

			System.err.println("Unsupported OS: " + os);
			return false;
		}

		try {

			processBuilder.redirectErrorStream(true);

			final Process process = processBuilder.start();

			final StringBuilder output = new StringBuilder();

			try (java.util.Scanner scanner = new java.util.Scanner(process.getInputStream())) {

				while (scanner.hasNextLine()) {
					output.append(scanner.nextLine()).append(System.lineSeparator());
				}
			}

			final int exitCode = process.waitFor();

			if (exitCode != 0) {

				System.err.println("Could not set system time!");
				System.err.println("Possible cause: insufficient permissions.");

				if (os.contains("nix") || os.contains("nux")) {
					System.err.println("On Linux, you can grant the required capability to the date command using:");
					System.err.println("sudo setcap cap_sys_time+ep \"$(readlink -f \"$(which date)\")\"");
				} else if (os.contains("windows")) {
					System.err.println("On Windows, start the Java application with administrator privileges or give the current user the set time rights.");
				} else if (os.contains("mac")) {
					System.err.println("On macOS, the Java process must have the appropriate system privileges.");
				}

				System.err.println(output);
				return false;
			}

			System.out.println("New system time: \"" + new SimpleDateFormat("MMM dd yyyy HH:mm:ss.SSS").format(new Date()) + "\"");

			return true;

		} catch (final IOException e) {

			System.err.println("Could not set time: " + e.getMessage());
			return false;

		} catch (final InterruptedException e) {

			Thread.currentThread().interrupt();
			System.err.println("Setting system time was interrupted.");
			return false;
		}
	}
}
