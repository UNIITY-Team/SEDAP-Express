# GRAPHIC type compatibility

The `GraphicType` field is emitted as the ICD's two-digit hexadecimal code,
`00` through `0B`. Input accepts these codes case-insensitively.

For compatibility with previous Java output and ICD examples, input also accepts
decimal `0` through `11`. The legacy values `10` and `11` identify
SensorFieldOfView and WeaponFieldOfFire respectively; they cannot conflict with
canonical codes because the defined hexadecimal range ends at `0B`.
Reserializing legacy input emits the canonical hexadecimal code.

Unknown codes raise `IllegalArgumentException` in the concrete GRAPHIC parser;
the general `SEDAPExpressMessage.deserialize` API retains its existing behavior
of returning null when a concrete parser rejects a message. Unknown numeric
enum lookups are rejected rather than substituted with Point.
