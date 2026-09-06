# SEDAP-Express ICD v1.4.6.1 (AI-optimized reference)

Source: "SEDAP-Express ICD v1.4.6.1.docx". Content reproduced completely; formatting condensed for machine consumption.

## 1. Scope

SEDAP-Express is a fast, intentionally simple path to integrate applications, sensors, effectors etc. into the UNIITY ecosystem (also used standalone for tactical/telemetry data exchange). If demands grow, the "bigger" SEDAP API / UNIITY interface can be used. License: Simplified BSD (BSD-2-Clause). Resources/SDK: https://SEDAP.Express (also in common repositories).

Glossary:
- UNIITY = Unified, Networked Integration of Innovative TechnologY
- SEDAP = Safety critical Environment for Data exchange And Process scheduling
- CSV = Comma-Separated-Values
- SEC = SEDAP-Express-Connector (part of UNIITY)
- MockUp/TestTool = simulation of the real SEC + C2-like system (log, map, message creator)
- SIDC = Symbol identification code (APP-6A/B / MIL-STD-2525B/C / STANAG 2019)
- ASCII = here: ISO-8859-1 table
- BASE64 = binary-to-text encoding, 64-char alphabet

## 2. Common conventions

- Format: CSV, separator `;` (0x3B); excess trailing semicolons may be truncated.
- List values: elements separated by `#` (0x23); list fields are marked `*` in the spec.
- Message terminator: `\n` (0x0A).
- Mandatory fields marked `(M)` (message name always mandatory).
- Max length of untyped/text fields: 256 bytes.
- Messages are human-readable ASCII.
- Fields with (binary) data possibly containing special chars (0x0A, 0x23, 0x3B, …) MUST be BASE64-encoded.
- Unknown/invalid values must NOT be transmitted — leave field empty.
- All timestamps: 64-bit integer, UNIX epoch in **milliseconds**, written as **hex string without `0x` prefix**.
- IPv4 and IPv6 supported (except serial).
- SEC/MockUp/applications can send and receive at any time.
- Heartbeat: application sends ≤1 Hz (±100 ms; may vary if required); SEC/MockUp answers with a HEARTBEAT message.

Units/geodesy (apply to all messages):
- SI units unless deviation noted; unit given in `[...]` per field.
- Altitude = above sea level; 0 = exactly on ground (on land).
- Lat/Lon in decimal degrees; positive = N/E, negative = S/W.
- Relative position: x-axis → east, y-axis → north, z-axis = height above the unit.
- Speed/course relative to ground. Course/heading: 0–359.999°, 0° = geographic north, clockwise.
- All values optional unless marked `(M)`. Numeric values are floating point unless defined otherwise.

## 3. Authentication, encryption, compression

### 3.1 Authentication (MAC field)
- If auth/encryption is required, prefer VPN instead.
- Messages authenticated by filling the MAC header field using a shared password.
- Password: pre-defined, or exchanged via Diffie-Hellman using KEYEXCHANGE (recommended: authenticate even KEYEXCHANGE with a pre-shared password).
- MAC calculation: over the complete message incl. header, with MAC field temporarily set to `"0000"`.
- MAC may be truncated to first 4 bytes / 32 bits (saves data, reduces security).
- Minimum: FIPS 140-2. Preferred:
  - 32/256-bit HMAC with SHA256 (FIPS 198-1 / RFC 2104)
  - 32/128-bit CMAC with AES128 (NIST SP 800-38B)
  - 32/128-bit GMAC with AES128 (NIST SP 800-38D)
- Recommended: MAC DRBG (NIST SP 800-90A/B).
- Sample (32-bit CMAC, password `expressexpressex`):
  `OWNUNIT;5E;0195236D151A;66A3;R;;089A01E7;53.32;8.11;0;5.5;21;22;;;FGS Bayern`

### 3.2 Encryption
- Optional; prefer VPN. Minimum FIPS 140-2. Preferred:
  - AES128/256 CFB/NoPadding (NIST SP 800-38A)
  - AES128/256 CTR/NoPadding (NIST SP 800-38A)
  - AES128/256 ECB/PKCS7Padding (NIST SP 800-38A)
  - XOR (pseudo-encryption, ONLY testing/debugging/light obfuscation)
- Recommended: MAC DRBG (NIST SP 800-90A/B).
- Encrypted data must be BASE64-encoded; the **whole message incl. header** is encrypted.
- If a password is set, EVERY message must be encrypted — no mixing plain/encrypted.
- Sample plain: `OWNUNIT;5E;01952384BD8D;66A3;R;;;53.32;8.11;0;5.5;21;22;;;FGS Bayern;sfspclff-------`
- Sample encrypted (AES128 CFB, password `expressexpressex`):
  `UdlsDIB4oeKiuU4PXtV9qCPrrk10yPFg38Bakm7oGVOOC3siuczsyg37+Q9eiDE1Z0qyaXQl4puRGdB0mpb1Vf6cfhj7n7270Gv/VjW5Ol8IAFJh`

### 3.3 Compression
- Optional. Apply BEFORE encryption (entropy!). Algorithm: deflate.
- Only effective if message > ~140 chars (because of BASE64 overhead).
- Compressed data must be BASE64-encoded; the **whole message incl. header** is compressed.
- Receiver rule: if first bytes of a received message don't match a message name, test for compression.
- Sample plain: `TEXT;53;01952381E21B;324E;S;TRUE;;;;1;NONE;"This is an alert!"`
- Sample compressed: `C3GNCLE2NbY2MLQ0NTK2MHQ1MnSyNjYycbUOtg4JCnW1BgJDaz9/P1drpZCMzGIFIErMU0jMSS0qUVQCAA==`

## 4. Connection methods

| Method | Details |
|---|---|
| TCP | Standard port 50000 (customizable). SEC/MockUp can be server or client. Use ONE persistent connection for all message kinds. Most simple/reliable; no free-text/data field size limits. |
| UDP | Standard port 50000 (customizable). Uni-/Broad-/Multicast; multiple messages per UDP packet allowed. Multicast: 228.2.19.80 / ff02:8:2:19:80::1. Answer HEARTBEAT via UDP unicast if possible. Strongly recommended: use header `<Number>` + `<Acknowledgement>` (packet loss). |
| Serial | Standard 115200-8-N-1. Full-duplex preferred; half-duplex and simplex without acknowledge requests. Modes: (a) message never contains `\n` → may be sent with appended `\n\n\n\n`; (b) message contains `\n` → must be BASE64-encoded and sent with appended `\n\n\n\n`. |
| MQTT | Plain TCP/MQTT or SSL/certificates. Publish topic: `UNIITY-X/<senderid>/<messagetype>`. |
| REST-API | Standard ports HTTP 80 / HTTPS 443 (customizable). Deflate or GZip compression should be supported. Prefer using provided JSON schema + generated code (SEDAP-Express SDK). See §7. |
| Protocol Buffers | Same TCP/UDP parameters as above. Schema in §8. |

## 5. Common message header (TCP/UDP/Serial)

```
<Name>(M);<Number>;<Time>;<Sender>;<Classification>;<Acknowledgement>;<MAC>;<Content>
```
Everything except Name is basically optional; some messages require specific header fields (noted per message). Hex numbers have NO `0x` prefix.

| Field | Meaning |
|---|---|
| Name | Message purpose/topic. |
| Number | A two-digit uppercase hexadecimal string (00-7F) represents a 7-bit sequential counter (wraps to 0 after 127/0x7F). Each message TYPE has its own counter; reconnect does NOT reset counters. 7-bit limit avoids signed/unsigned byte issues. |
| Time | Hex string of 64-bit Unix timestamp in ms. |
| Sender | Free textual identifier (e.g. "OKRA"). Never changed on forward/relay. Chosen by participants or assigned centrally. When forwarding sub-system info (e.g. drones in a swarm), Sender = original source (the sub-system). |
| Classification | P=public, U=unclassified, R=restricted, C=confidential, S=secret, T=top secret |
| Acknowledgement | TRUE=request acknowledgement; FALSE/empty=none. Acknowledgement requires a set message Number. |
| MAC | 32/128/256-bit message authentication code (see §3.1). |
| Content | Message-specific content. |

## 6. Messages

Notes:
- Header part (`<Number>;<Time>;<Sender>;<Classification>;<Acknowledgement>;<MAC>`) abbreviated as `<HDR>` below.
- In samples, some fields are intentionally shortened (e.g. KEYEXCHANGE).

### 6.1 OWNUNIT
Position, movement, identification data of the own unit (base station, C2 center, drone, vehicle, person, or host device). Sent by a client → converted to a contact and distributed to the UNIITY network. Multiple own units: Sender field mandatory to distinguish them (exceptionally different Names; that option must be explicitly configured in the SEC).

```
OWNUNIT;<HDR with Sender(M?)>;
<Latitude>[°](M);<Longitude>[°](M);<Altitude>[m];
<SpeedOverGround>[m/s];<CourseOverGround>[°];
<Heading>[°];<Roll>[°];<Pitch>[°];
<Name>;<SIDC>
```
Samples:
```
OWNUNIT;5E;0191C643A8AF;DRONEONE;R;;;53.32;8.11;0;5.5;21;22;;;FGS Bayern;SFSPCLFF-------
OWNUNIT;11;0191C643A8AF;22AA;U;FALSE;4389F10D;77.88;-10.12;5577.0;33.44;55.66;1.1;-2.2;3.3;Ownunit;SFGPIB----H----
```

### 6.2 CONTACT
Position/kinematics/identification of a contact (e.g. sensor reports a recognized contact; also used to receive the tactical picture from UNIITY). Either Lat/Lon/Alt OR relative X/Y/Z distance is mandatory (one of the two). Relative distance requires an own OWNUNIT message — otherwise receiver position is used as reference. Implement Delete as well!

```
CONTACT;<HDR>;
<ContactID>(M);<DeleteFlag>;<Latitude>[°](M);<Longitude>[°](M);<Altitude>[m];
<relX-Distance>[m](M);<relY-Distance>[m](M);<relZ-Distance>[m](M);
<SpeedOverGround>[m/s];<CourseOverGround>[°];
<Heading>[°];<Roll>[°];<Pitch>[°];
<Width>[m];<Length>[m];<Height>[m];
<Name>;<Source>;<SIDC>;<MMSI>;<ICAO>;<MediaData>;<Comment>
```
Note: Lat/Lon and relative X/Y/Z distances are each marked `(M)` in the ICD — the rule is: exactly ONE of the two position variants is mandatory, the other stays empty.

| Field | Type/Values | Meaning |
|---|---|---|
| ContactID | ASCII | Unique number or free-text id, chosen by sender; unique at least for the whole period (e.g. of the exercise). |
| DeleteFlag | TRUE / FALSE | TRUE=remove contact; FALSE=contact is current. |
| Name | ASCII | Contact name, max 64 bytes. |
| Source | chars, multiple allowed | R=Radar, A=AIS, I=IFF/ADS-B, S=Sonar, E=EW, O=Optical, Y=Synthetic, M=Manual |
| SIDC | SIDC | Identification code, lower or upper case (starts with "s"). |
| MMSI | MMSI | Maritime Mobile Service Identity. |
| ICAO | ICAO | International Civil Aviation Organization. |
| MediaData | BASE64 | Image/video/sound URL or data (JPG, PNG, TIF, MP4, TS, WAV, …); preferred length ≤65000 bytes when using UDP. |
| Comment | BASE64 | Free text, max 8192 bytes. |

Samples:
```
CONTACT;5E;0191C643A8AF;83C5;R;;;100;FALSE;53.32;8.11;0;;;;120;275;;;;;;;FGS Bayern;AR;SFSPCLFF-------;;;;VXNlIENIMjI=
CONTACT;5F;0191C643A8AF;83C5;U;;;101;;36.32;12.11;2000;;;;44;;;;;;;;Unknown;O;;221333201;;;UG9zcyBOZXRoZXJsYW5kcw==
CONTACT;;0191C643A8AF;4371;S;;;102;;53.32;8.11;;;;;;;;;;;;;PossTank;;;;;XFxzcnZcc25kXDU0aDJoLndhdg==;cGxzIGhlYXI=
```

### 6.3 POINT
Position/kinematics/identification of a (geographical) point (person overboard, bridge, hill, meeting point, …). Same Lat/Lon/Alt-OR-relative rule as CONTACT (relative requires own OWNUNIT, else receiver position is reference). Implement Delete as well!

```
POINT;<HDR>;
<PointID>(M);<DeleteFlag>;<Latitude>[°](M);<Longitude>[°](M);<Altitude>[m];
<relX-Distance>[m](M);<relY-Distance>[m](M);<relZ-Distance>[m](M);
<SpeedOverGround>[m/s];<CourseOverGround>[°];
<Heading>[°];<Roll>[°];<Pitch>[°];
<Width>[m];<Length>[m];<Height>[m];
<Name>;<SIDC>;<MediaData>;<Comment>
```
Note: Lat/Lon and relative X/Y/Z distances are each marked `(M)` in the ICD — the rule is: exactly ONE of the two position variants is mandatory, the other stays empty.

| Field | Type/Values | Meaning |
|---|---|---|
| PointID | ASCII | Unique number or free-text id, chosen by sender. |
| DeleteFlag | TRUE / FALSE | TRUE=remove point; FALSE=point is current. |
| Name | ASCII | Point name, max 64 bytes. |
| SIDC | SIDC | Identification code, lower or upper case (starts with "g"). |
| MediaData | BASE64 | Image/video/sound URL or data; preferred length ≤65000 bytes with UDP. |
| Comment | BASE64 | Free text, max 8192 bytes. |

Samples:
```
POINT;6A;0013E45956BE;59CE;U;;FFAA327B;1000;FALSE;;;;100;130;0;2;30;;;;;;;Person in water;gfopep---------
POINT;5E;0000661D4410;66A3;R;;;100;FALSE;53.32;8.11;0;;;;120;275;;;;;;;Target Alpha;gffppt---------;;VGFyZ2V0IHBvaW50
```

### 6.4 EMISSION
Positional data, emission attributes, identification info of an electromagnetic, optical or acoustic emission, or the bearing of visually recognizable objects.

```
EMISSION;<HDR>;
<EmissionID>(M);<DeleteFlag>;
<SensorLatitude>[°](M);<SensorLongitude>[°](M);<SensorAltitude>[m];
<EmitterLatitude>[°];<EmitterLongitude>[°];<EmitterAltitude>[m];
<Bearing>[°](M);<Frequencies>[Hz]*;<Bandwidth>[Hz];<Power>[db(A)];<FreqAgility>;<PRFAgility>;
<Function>;<SpotNumber>;<SIDC>;<Comment>
```

| Field | Values |
|---|---|
| EmissionID | ASCII: unique number/free-text id chosen by sender; must also be unique w.r.t. contact numbers and similar IDs. |
| DeleteFlag | TRUE=remove, FALSE=current. |
| FreqAgility | 00=Stable Fixed, 01=Agile, 02=Periodic, 03=Hopper, 04=Batch Hopper, 05=Unknown |
| PRFAgility | 00=Fixed periodic, 01=Staggered, 02=Jittered, 03=Wobbulated, 04=Sliding, 05=Dwell switch, 06=Unknown PRF, 07=CW |
| Function | 00=Unknown, 01=ESM Beacon/Transponder, 02=ESM Navigation, 03=ESM Voice Communication, 04=ESM Data Communication, 05=ESM Radar, 06=ESM IFF/ADS-B, 07=ESM Guidance, 08=ESM Weapon, 09=ESM Jammer, 0A=ESM Natural, 0B=ACOUSTIC Object, 0C=ACOUSTIC Submarine, 0D=ACOUSTIC Variable Depth Sonar, 0E=ACOUSTIC Array Sonar, 0F=ACOUSTIC Active Sonar, 10=ACOUSTIC Torpedo Sonar, 11=ACOUSTIC Sono Buoy, 12=ACOUSTIC Decoy Signal, 13=ACOUSTIC Hit Noise, 14=ACOUSTIC Propeller Noise, 15=ACOUSTIC Underwater Telephone, 16=ACOUSTIC Communication, 17=ACOUSTIC Noise, 18=LASER Range Finder, 19=LASER Designator, 1A=LASER Beam Rider, 1B=LASER Dazzler, 1C=LASER Lidar, 1D=LASER Weapon, 1E=VISUAL Object |
| SIDC | Identification code. |
| Comment | BASE64, max 65000 bytes. |

Samples:
```
EMISSION;5E;0195238E15AD;66A3;R;;;100;;53.32;8.11;0;;;;20;8725000.0#8735000.0;20000;3;0;2;6;10233;;SA-8
EMISSION;5F;0195238E25AD;66A3;R;;;101;;54.86;9.32;0;52.12;9.8;50;233;25725.0;4000;1;5;2;0;;sngpesr--------
```

### 6.5 METEO
Meteorological data of the environment. If OWNUNIT is also sent, UNIITY links the data to the last OWNUNIT position; alternative: give a Reference to a contact, point or graphic.

```
METEO;<HDR>;
<SpeedThroughWater>[m/s];<WaterSpeed>[m/s];<WaterDirection>[°];<WaterTemperature>[°C];<WaterDepth>[m];
<AirTemperature>[°C];<DewPoint>[°C];<HumidityRel>[%];<Pressure>[hPa];<WindSpeed>[m/s];<WindDirection>[°];
<Visibility>[km];<CloudHeight>[m];<CloudCover>[%];<Reference>
```
Samples:
```
METEO;1C;0195238E25AD;74BE;U;;;15.4;15.5;;;;;10.2;72;1005;25;111;50;2500;33;RefPoint1
METEO;;0195238E25AD;;U;;;23.2;100;;;;10.2;72;80;998;20;;;500;100;1000
```

### 6.6 TEXT
Human-readable text (alert or chat). If text may contain special characters (UTF-x, 0x0A, 0x23, …) it must be BASE64-encoded with Encoding indicator set; if indicator not set, no encoding is assumed. If linked to a contact, send the CONTACT message first. If the recipient field is left empty, this means everyone (as with a broadcast).

```
TEXT;<HDR>;<Recipient>;<Type>;<Encoding>;<Text>(M);<Reference>
```

| Field | Values |
|---|---|
| Recipient | ASCII, free textual identifier (like Sender, §5). |
| Type | 00=Undefined, 01=Alert, 02=Warning, 03=Notice, 04=Chat |
| Encoding | BASE64 / NONE |
| Text | ASCII, max 65000 bytes. |
| Reference | ASCII, reference to a contact, point or emission. |

Samples:
```
TEXT;78;0195238E25AD;324E;S;TRUE;;;1;NONE;"This is an alert!";1000
TEXT;79;0195238E25CC;324E;C;TRUE;;;2;NONE;"This is a warning!"
TEXT;7A;0195238E25EF;324E;R;;;;3;;"This is a notice!"
TEXT;7B;0195238E285B;324E;U;;;ORKA;4;BASE64;IlRoaXMgaXMgYSBjaGF0IG1lc3NhZ2UhIg==
```

### 6.7 GRAPHIC
Graphical plans: polygons, squares, routes; also camera field of view, sensor/weapon direction, weapon range area. In list fields (`#`, e.g. Path), the coordinates within one list element are separated by `,` — not `;`.

```
GRAPHIC;<HDR>;<GraphicID>(M);<DeleteFlag>;
<GraphicType>(M);<LineWidth>;<LineColor>;<FillColor>;<TextColor>;<Encoding>;<Annotation>;<GraphicType-dependent params>*
```

| Field | Values |
|---|---|
| GraphicID | ASCII, unique id (enables updates of an existing graphic). |
| DeleteFlag | TRUE=remove, FALSE=current. |
| LineWidth | ≥1, width of line or point. |
| LineColor | RGBA web notation (e.g. `800000FF` darker red). |
| FillColor | RGBA (e.g. `00FF0080` translucent green). |
| TextColor | RGBA (e.g. `32CD32FF` lime). |
| Encoding | BASE64 / NONE (for Annotation). |
| Annotation | ASCII, max 32 bytes. |

GraphicType + type-dependent parameters:

| Type | Shape | Parameters |
|---|---|---|
| 00 | Point | `<Lat>[°];<Lon>[°];<Alt>[m]` |
| 01 | Path | `<Lat>[°],<Lon>[°],<Alt>[m]# …` |
| 02 | Polygon | `<Lat>[°],<Lon>[°],<Alt>[m]# …` |
| 03 | Rectangle | `<Lat>[°],<Lon>[°],<Alt>[m];<Width>[m];<Length>[m];<Rotation>[°]` |
| 04 | Square | `<Lat>[°];<Lon>[°];<Alt>[m];<Width>[m];<Rotation>[°]` |
| 05 | Circle | `<CenterLat>[°];<CenterLon>[°];<CenterAlt>[m];<Radius>[m];<StartAngle>[°];<EndAngle>[°]` |
| 06 | Ellipse | `<CenterLat>[°];<CenterLon>[°];<CenterAlt>[m];<RadiusX>[m];<RadiusY>[m];<Rotation>[°]` |
| 07 | Block | `<Lat>[°];<Lon>[°];<Alt>[m];<Width>[m];<Length>[m];<Height>[m];<RotX>[°];<RotY>[°];<RotZ>[°]` |
| 08 | Sphere | `<Lat>[°];<Lon>[°];<Alt>[m];<Radius>[m]` |
| 09 | Ellipsoid | `<CenterLat>[°];<CenterLon>[°];<CenterAlt>[m];<RadiusX>[m];<RadiusY>[m];<RadiusZ>[m];<RotX>[°];<RotY>[°];<RotZ>[°]` |
| 0A | SensorFieldOfView | `<Azimuth>[°];<Elevation>[°];<Lat>[°],<Lon>[°],<Alt>[m]# …` |
| 0B | WeaponFieldOfFire | `<Azimuth>[°];<Elevation>[°];<Lat>[°],<Lon>[°],<Alt>[m]# …` |

Samples:
```
GRAPHIC;79;0195238E35AD;910E;U;;;FFDA;;8;1;FF800000;;;BASE64;QXJlYSBBbHBoYQ==;53.43;9.45;0;1000
GRAPHIC;78;0195238E45AD;910E;U;;;A327;;1;1;80808000;;FFFF0000;;Transit;54.23,12.86#54.30,12.9#54.55,13.30
```

### 6.8 COMMAND
Command for one specific or all possible recipients. Camera numbering/modes and Generic Action semantics are application/platform-specific (require a specific, user-defined connector — not the generic SEDAP-Express connector). Timestamps optional, in ms; no timestamp = execute instantly; Unix timestamps written as hex string. For "follow"/"engage" of e.g. a contact, the contact's position must be transmitted frequently via CONTACT. For submersibles, Depth ≡ Altitude and is always positive.

```
COMMAND;<HDR>;
<Recipient>(M);<CmdID>;<CmdFlag>(M);<CmdExTime>;<CmdType>(M);<CmdType-dependent params>*
```

| Field | Values |
|---|---|
| Recipient | ASCII, free textual identifier (§5). |
| CmdID | HexString, 16-bit command id (`0000` = all last commands). |
| CmdFlag | 00=Add, 01=Replace (last), 02=Cancel (last), 03=Cancel all (same as 02 if not a command sequence) |
| CmdExTime | Long, Unix timestamp when command shall be executed. |

CmdType + parameters:

| Code | Command | Parameters |
|---|---|---|
| 00 | Power off | `<PowerOnUnixTimestamp>` (optional) |
| 01 | Restart | — |
| 02 | Standby | `<WakeupUnixTimestamp>` (optional) |
| 03 | Sync time | `<IP/Hostname of NTP server>` |
| 04 | Calibrate gyro | — |
| 05 | Calibrate compass | — |
| 06 | Send status | — |
| 07 | Set manual mode | — |
| 08 | Set semi-autonomous mode | — |
| 09 | Set autonomous mode | — |
| 0A | Set failsafe mode | — |
| 10 | Start engine | — |
| 11 | Test engine | — |
| 12 | Set engine power | `<PowerLevel>[0-100%]` |
| 13 | Stop engine | — |
| 14 | Stop movement | — |
| 15 | Toggle lights | `<Status>[ON\|OFF];<BrightnessLevel>[0-100%];<StrobePeriod>[s]` |
| 16 | Deploy parachute | — |
| 20 | Set heading | `<HeadingAngle>[°]` |
| 21 | Set altitude | `<Altitude>[m]` |
| 22 | Set speed | `<Speed>[m/s]` |
| 23 | Rotate | `<HeadingAngle>[°];<RollAngle>[°];<PitchAngle>[°]` |
| 24 | Move to | `<Lat>[°];<Lon>[°];<Alt>[m];<Tolerance>[m];<UnixTimestamp>` |
| 25 | Follow contact | `<ContactID>` |
| 26 | Return home | `<Tolerance>[m];<UnixTimestamp>` |
| 27 | Set home location | `<Lat>[°];<Lon>[°];<Alt>[m];<Tolerance>[m]` |
| 28 | Take off | `<Lat>[°];<Lon>[°];<Direction>[°]` |
| 29 | Land | `<Lat>[°];<Lon>[°];<Direction>[°]` |
| 2A | Submerge | `<Depth>[m]` |
| 2B | Surface | — |
| 2C | Dock | `<Lat>[°];<Lon>[°];<Direction>[°]` |
| 30 | Loiter/Orbiting | `<CenterLat>[°];<CenterLon>[°];<Alt>[m];<Radius>[m]` |
| 31 | Scan | `<Lat>[°];<Lon>[°];<Alt>[m]` |
| 32 | Scan area | `<Lat1>[°];<Lon1>[°];<Lat2>[°];<Lon2>[°];<Alt>[m];<RotationAngle>[°]` |
| 33 | Take photo | `<CameraID>` |
| 34 | Record video | `<CameraID>;<ON\|OFF>;<Duration>` |
| 35 | Stream video | `<CameraID>;<ON\|OFF>` |
| 36 | Set camera parameters | `<CameraID>;<Zoom>[0-100%];<Mode>[DayLight\|InfraRed\|LightIntensifier]` |
| 37 | Set orientation of camera | `<CameraID>;<Azimuth>[°];<Elevation>[°]` |
| 40 | Actuator check | `<ActuatorID>` |
| 41 | Set orientation of actuator | `<ActuatorID>;<Azimuth>[°];<Elevation>[°]` |
| 42 | Actuator pick up object | `<ActuatorID>` |
| 43 | Actuator release object | `<ActuatorID>` |
| 50 | Pre-arm check | `<WeaponID>` |
| 51 | Arm | `<WeaponID>` |
| 52 | Disarm | `<WeaponID>` |
| 53 | Set orientation of weapon | `<WeaponID>;<Azimuth>[°];<Elevation>[°]` |
| 54 | StartEngagement | `<WeaponID>;<ContactID\|PointID\|EmissionID\|GraphicID>(M)` |
| 55 | HoldEngagement | `<WeaponID>;<ContactID\|PointID\|EmissionID\|GraphicID>(M)` |
| 56 | StopEngagement | `<WeaponID>;<ContactID\|PointID\|EmissionID\|GraphicID>(M)` |
| EE | Sanitize system | (e.g. emergency / drone hijacking) |
| EF | Self destruction | — |
| FF | Generic Action | `<KindOfAction>[String]` — must be defined individually, incl. a custom UNIITY connector. |

Samples:
```
COMMAND;55;0195238E25AD;5BCD;S;TRUE;;ORKA;1111;01;;24;53.32;8.11;1000;5
COMMAND;29;0195238E35AD;E4B3;C;TRUE;;Drone1;;00;;FF;OPEN_BAY
COMMAND;29;0195238F55AD;E4B3;C;TRUE;;Drone2;0000;03          (cancel all last commands)
```
Sample 4 — complex "move to" with loitering at the end (drone computes speed/course itself from the last move's timestamp):
```
COMMAND;29;0195238E25AD;E4B3;C;TRUE;;ORKA;0331;00;;24;53.5143;8.1574;50;5
COMMAND;2A;0195238E25AD;E4B3;C;TRUE;;ORKA;0331;00;;24;53.4897;8.1908;50;5
COMMAND;2B;0195238E25AD;E4B3;C;TRUE;;ORKA;0331;00;;24;53.4694;8.2131;50;5
COMMAND;2C;0195238E25AD;E4B3;C;TRUE;;ORKA;0331;00;;24;53.4397;8.2262;50;5;019D25300FC0   (approx. 15 min later)
COMMAND;2C;0195238E25AD;E4B3;C;TRUE;;ORKA;0331;01;019D75300FFF;34;CAM1;ON;3600
COMMAND;2E;0195238E25AD;E4B3;C;TRUE;;ORKA;0331;00;;30;53.4397;8.2262;1000;200
```

### 6.9 STATUS
Status variables (e.g. remaining battery) and optionally execution status of the last or a specific COMMAND. When forwarding sub-system status (e.g. swarm), Sender = the individual sub-system (e.g. concrete drone). StorageLevel can mean recording memory or physical storage room (picking up/transferring objects).

```
STATUS;<HDR>;<TecStatus>;<OpsStatus>;<AmmunitionLevels>*;
<FuelLevels>*;<BatteryLevels>*;<StorageLevels>*;<CmdID>;<CmdState>;<IP/Host>;<Media>*;<Text>
```

| Field | Values |
|---|---|
| TecStatus | 0=Off/Absent, 1=Initializing, 2=Degraded, 3=Operational, 4=Fault |
| OpsStatus | 0=Not operational, 1=Degraded, 2=Operational, 3=Operational (semi-autonomous), 4=Operational (autonomous) |
| AmmunitionLevels | `String#%` list: `<weaponName>#<level>#…` (relative remaining ammunition) |
| FuelLevels | `String#%` list: `<tankName>#<level>#…` |
| BatteryLevels | `String#%` list: `<batteryName>#<level>#…` |
| StorageLevels | `String#%` list: `<storageName>#<level>` |
| CmdID | HexString, id from the related COMMAND message. |
| CmdState | 00=Undefined, 01=Executed successfully, 02=Partially successfully executed, 03=Not successfully executed, 04=Execution not possible (yet), 05=Will be executed at `;<timestamp>` |
| IP/Host | BASE64, IP or hostname of the platform, max 64 bytes. |
| Media | BASE64, list of video stream/image URLs, max 4096 bytes. |
| Text | BASE64, human-readable status description. |

Samples:
```
STATUS;15;0195238E25AD;75DA;U;;;4;2;MLG#20;;Accu1#50;;443D;1;MTAuMC4wLjEzMg==;;RnVsbHkgb3BlcmF0aW9uYWw=
STATUS;16;0195238E25AD;129E;R;;;2;2;BMG#10;;;;ED32;3;;aHR0cDovLzEwLjAuMC4xL2ltYWdlLnBuZw==;T3V0IG9mIGZ1ZWwh
```

### 6.10 ACKNOWLEDGE
Sent when a client or the SEC requested an acknowledgement of a packet. Its own Acknowledgement flag is fixed FALSE. The awaiting side waits max 1 second before resending the original message with the acknowledgement flag set.

```
ACKNOWLEDGE;<Number>;<Time>;<Sender>;<Classification>;;<MAC>;
<Recipient>(M);<TypeOfMessage>(M);<NumberOfMessage>(M)
```

| Field | Meaning |
|---|---|
| Recipient | ASCII, free textual identifier (§5). |
| Type | ASCII, type of the message to acknowledge (e.g. CONTACT, RESEND, …). |
| Number | HexString of the 7-bit number of the message to acknowledge. |

Sample: `ACKNOWLEDGE;18;0195238E25AD;129E;R;;;LASSY;COMMAND;2B`

### 6.11 RESEND
Request missing messages (recognized by header message number, sender ID and message name).

```
RESEND;<HDR>;<Recipient>(M);<NameOfMissingMessage>(M);<NumberOfMissingMessage>(M)
```

| Field | Meaning |
|---|---|
| Recipient | ASCII, free textual identifier (§5). |
| Name | ASCII, name of the message to resend. |
| Number | HexString of the 7-bit number of the message to resend. |

Sample: `RESEND;20;0195238E25AD;129E;R;;;FE2A;TEXT;5D`

### 6.12 GENERIC
Empty container for any kind of data, defined per use case (e.g. original UNIITY/SEDAP messages or other proprietary protocol data — then use a self-defined type). If Encoding not set, no encoding assumed.

```
GENERIC;<HDR>;<ContentType>;<Encoding>;<Content>
```

| Field | Values |
|---|---|
| ContentType | SEDAP=original SEDAP message, ASCII=custom ASCII string, NMEA=NMEA0183 string, XML=XML structure, JSON=JSON formatted, BINARY=self-defined binary array |
| Encoding | BASE64 / NONE |
| Content | Printable ASCII or BASE64 data, max 8192 bytes. |

Samples:
```
GENERIC;5E;0195238E25AD;66A3;R;;;JSON;;{"object": {"x": "1","y": "2"},"string": "Hello World"}
GENERIC;5E;0195238E25AD;66A3;R;TRUE;;BINARY;BASE64;U2FtcGxlIGJpbmFyeSBkYXRhIEdyZWV0aW5ncyA6RA==
GENERIC;5E;0195238E25AD;66A3;R;;;NMEA;NONE;$RATTM,11,11.4,13.6,T,7.0,20.0,T,0.0,0.0,N,,Q,,154125.82,A,*17
```

### 6.13 HEARTBEAT
Connection check, primarily important for UDP/serial. Send ≤1 Hz (faster only if needed). Recipient optional: single recipient, list, or empty = all recipients in the network/serial net. Acknowledgement flag is always empty/FALSE (an acknowledgement cannot be requested for heartbeats). SEC/MockUp answers a heartbeat with a heartbeat.

```
HEARTBEAT;<HDR>;<Recipient>
```
Recipient: ASCII, free textual identifier (§5).

Samples:
```
HEARTBEAT;42;0195238E25AD;89AD;U;;;ORKA
HEARTBEAT;43;;1022
HEARTBEAT;43;
HEARTBEAT
```

### 6.14 TIMESYNC
Time synchronization when OS functions/NTP are unavailable. On receipt, the recipient answers with its current system time as timestamp. The original sender computes RTT = half the difference between response-receive time and send time, adds it to the transmitted timestamp, and adopts the result as new system time. Clients should re-sync themselves from time to time.

```
TIMESYNC;<HDR>;<Timestamp>
```
Timestamp: HexString, 64-bit Unix timestamp in ms.

Samples:
```
TIMESYNC;42;0191C643A8AF;89AD;U
TIMESYNC;12;;FE2A;U;;;0191C643A8AF
```

### 6.15 KEYEXCHANGE
Key exchange via Diffie-Hellman-Merkle or post-quantum Kyber/FrodoKEM when no other channel (mail, telco) is available. Prefer ECDH or standard DH with MAC DRBG. If possible use MAC authentication for these messages, otherwise plausibility checks. The current Phase defines which fields are mandatory. Either side can restart by sending Phase=0 again; the other side then restarts the whole process incl. generating new key pairs (ECDH/Kyber/FrodoKEM). ECDH has NO phase 1. The process always ends with a finalization confirmation. Key derivation from a larger secret: PBKDF2 with HMACSHA256, 1 iteration, no salt → 128/256-bit key.

```
KEYEXCHANGE;<HDR>;<Recipient>;<AlgorithmType>(M);<Phase>(M);
<KeyLengthSharedSecret>(M*);<KeyLengthKEM>(M*);<Prime>(M*);<NaturalNumber>(M*);<InitialisationVector>(M*);<PublicVariable/Key>(M*)
```
`(M*)` = whether it is required depends on the phase and the type of algorithm.

| Field | Values |
|---|---|
| AlgorithmType | 0=DH (1024/2048 bit, NIST SP 800-56A/B & 90A/B), 1=ECDH (Curve25519/X25519, 1024/2048/4096 bit), 2=Kyber-512, 3=Kyber-768, 4=Kyber-1024, 5=FrodoKEM-640 (AES), 6=FrodoKEM-976 (AES), 7=FrodoKEM-1344 (AES) |
| Phase | 0, 1, 2 — current step of the exchange. |
| KeyLengthSharedSecret | 128 or 256 (bits, Phase 0). |
| KeyLengthKEM | 1024, 2048 or 4096 (bits of DH/KEM key, Phase 0). |
| Prime (p) | HexString, public prime (>3000 bits / 375 bytes) (Phase 0, DH only). |
| NaturalNumber (g) | HexString, public natural number < p (Phase 0, DH only). |
| InitialisationVector (IV) | HexString, IV (for AES CFB/CTR encryption only). |
| PublicVariable/Key | BASE64, sender's public variable/key. |

Samples (fields shortened for readability):
```
KEYEXCHANGE;00;0191C643A8AF;89AD;U;;;FE2A;0;128;1024;7FFFFFFF;822460DE
KEYEXCHANGE;00;0191C643A8AF;FE2A;U;;;89AD;1;128;2048;;6E6026EFF9D9EBEB9D4A973CB5C287DBD77D75EDDD2
```
(The ICD additionally shows DH-, ECDH- and Kyber/FrodoKEM sequence diagrams — graphics, not reproduced here.)

## 7. REST-API (JSON)

Single endpoint: `http://<ip>:<port>/SEDAPEXPRESS`
- GET: client retrieves messages. POST: client sends messages. Updates of existing messages (e.g. contacts): POST or PUT.
- Different message types may be mixed in one request. JSON contains just a list of SEDAP-Express messages in their original (JSON-compatible) format — same message classes usable for parsing and generating.

Schema:
```json
{ "messages": [ { "message": "" } ] }
```

Sample GET request:
```
GET /SEDAPEXPRESS HTTP/1.1
Host: sample.host
Accept: application/json
```
Sample GET answer:
```
HTTP/1.1 200 OK
Content-Type: application/json
Content-Length: 399

{
   "messages":[
      {"message":"CONTACT;60;0191C643A8AF;66A3;S;TRUE;;102;TRUE;53.32;8.11"},
      {"message":"METEO;2C;0191C643A8AF;74BE;U;;;15.4;15.5;;;;;10.2;72;1005;25;111;50;2500;33;RefPoint1"},
      {"message":"TEXT;56;0191C643A8AF;324E;S;;;E4F1;4;NONE;This is a chat message!"},
      {"message":"GRAPHIC;79;0191C643A8AF;910E;U;;;AreaA;;08;1;FF8000FF;;;;Area A;53.43;9.45;0;10000"}
   ]
}
```
Sample POST request:
```
POST /SEDAPEXPRESS HTTP/1.1
Host: sample.host
Content-Type: application/json

{
"messages":[
{
"message":" OWNUNIT;5E;0191C643A8AF;66A3;R;TRUE;;42.32;-123.11;10000;50.23;297;;;33.3;-0.15;sfapmf---------"
"message":"TEXT;2E;0191C643A8AF;374E;S;;3;This is a chat message!;E4F1"
}
]
}
```
Sample POST answer:
```
HTTP/1.1 200 OK
Content-Type: application/json

{"success":"true"}
```

## 8. Protobuf definition

Same principle as JSON: wrapper carries original message strings.
```proto
syntax = "proto3";
message SomeMessage {
  message Messages {
    string message = 1;
  }
  repeated Messages messages = 1;
}
```

## 9. Contact

Report issues/suggestions via GitHub, email or phone.
- GitHub: https://github.com/UNIITY-Team
- Internet: https://www.linkedin.com/in/volker-voss
