"""Validate the sequence contract and literal samples in the Markdown ICD."""
import base64
from pathlib import Path
import re
import unittest
import zlib


class ICDSequenceNumbersTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.text = Path(__file__).with_name('SEDAP-Express-ICD-for-AI-v1.4.6.md').read_text()

    def test_literal_message_examples_use_two_digit_7bit_numbers(self):
        numbers = re.findall(r'\b(?:OWNUNIT|CONTACT|POINT|EMISSION|METEO|TEXT|GRAPHIC|COMMAND|STATUS|ACKNOWLEDGE|RESEND|GENERIC|HEARTBEAT|TIMESYNC|KEYEXCHANGE);([0-9A-F]+);', self.text)
        self.assertGreater(len(numbers), 30)
        for number in numbers:
            with self.subTest(number=number):
                self.assertRegex(number, r'^[0-7][0-9A-F]$')
        self.assertIn('two-digit uppercase hexadecimal string (00-7F)', self.text)

    def test_compressed_sample_matches_corrected_reference(self):
        section = self.text.split('### 3.3 Compression\n', 1)[1].split('\n## ', 1)[0]
        reference = re.search(r'Sample plain: `([^`]+)`', section).group(1)
        encoded = re.search(r'Sample compressed: `([^`]+)`', section).group(1)
        decoded = zlib.decompress(base64.b64decode(encoded, validate=True), -15).decode('ascii')
        self.assertEqual(reference, decoded)
        self.assertEqual('53', decoded.split(';')[1])


if __name__ == '__main__':
    unittest.main()
