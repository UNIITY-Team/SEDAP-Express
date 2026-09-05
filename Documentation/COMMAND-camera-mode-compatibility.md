# COMMAND camera mode compatibility

SetCameraParameters emits the ICD names `DayLight`, `InfraRed`, and
`LightIntensifier`. The Java enum constants remain `DL`, `IR`, and `LI`.

The parser accepts both the documented names and these legacy aliases,
case-insensitively. Reserializing an alias emits its documented name. Other
names are rejected with `IllegalArgumentException` by the concrete COMMAND
parser; the general `SEDAPExpressMessage.deserialize` API retains its existing
behavior of returning null when a concrete parser rejects a message.
