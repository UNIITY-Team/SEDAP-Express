# MAC wire-format compatibility

The reference implementation follows the MAC sizes and truncation rule in ICD v1.4.6, section III.2:

| API | Tag | Hexadecimal digits |
| --- | --- | --- |
| `calcHMAC` | Full HMAC-SHA-256 | 64 |
| `calcCMAC` | Full AES-CMAC | 32 |
| `calcGMAC` | Full AES-GMAC | 32 |
| `calc32BitHMAC`, `calc32BitCMAC`, `calc32BitGMAC` | First four bytes of the respective full tag | 8 |

The authenticated input is the serialized message with its MAC field set to `0000`.
The header parser accepts 8-, 32- and 64-digit hexadecimal tags, in either case.
An empty MAC remains optional. The exact value `0000` remains accepted as the
construction placeholder; it is not an authentication tag. Other tag lengths are
invalid. Generic hexadecimal fields such as command IDs keep their existing
independent grammar.

Earlier reference implementations returned only four bytes from `calcCMAC` and
`calcGMAC`, then computed an Adler-32 checksum of the hexadecimal text for the
`calc32Bit*` helpers. Those checksums are incompatible with the ICD rule. Both old
checksums and correct truncated tags have eight hexadecimal digits, so this
implementation does not automatically detect or fall back to the old format.
Peers must agree on the corrected format before exchanging authenticated
messages. The public `calcAdler32Checksum` utility remains available for source
compatibility but is not used to generate MACs.

The published ICD CMAC example's `089A01E7` is the old Adler-32 result. With the
published example password interpreted as ASCII key bytes and the unchanged
message with `MAC=0000`, the full AES-CMAC is
`BD194FE70149EC51D9A9B758C4AD55CD`; the corrected four-byte tag is `BD194FE7`.

`MACUtilsTest` fixes independent full and shortened tag expectations for all
three algorithms, verifies the ICD example, and checks header parsing at each
supported length. The vectors use public fixture keys and OpenSSL 3.6.3 as an
independent implementation. These changes concern tag calculation and wire
format; they do not certify or change the existing authentication verifier.
