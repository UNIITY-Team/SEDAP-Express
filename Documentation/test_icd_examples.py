"""Validate literal published examples, not corrected copies in a separate fixture."""
import base64
import zlib
import json
from pathlib import Path
import unittest
import re


class ICDExamplesTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.text = Path(__file__).with_name('SEDAP-Express-ICD-for-AI-v1.4.6.md').read_text()

    def sample_after(self, label):
        return self.text.split(label, 1)[1].split('```', 2)[1].strip()

    def json_after(self, label):
        sample = self.sample_after(label)
        raw = sample[sample.index('{'):]
        def reject_duplicates(pairs):
            result = {}
            for key, value in pairs:
                self.assertNotIn(key, result, 'Repeated JSON object key')
                result[key] = value
            return result
        value = json.loads(raw, object_pairs_hook=reject_duplicates)
        return raw, [item['message'].split(';') for item in value['messages']]

    def test_get_preserves_four_messages_and_current_field_layout(self):
        raw, messages = self.json_after('Sample GET answer:')
        self.assertEqual(['CONTACT', 'METEO', 'TEXT', 'GRAPHIC'], [m[0] for m in messages])
        self.assertIn('Content-Length: '+str(len(raw.encode('utf-8'))), self.sample_after('Sample GET answer:'))
        contact, meteo, text, graphic = messages
        self.assertEqual(['', '102', 'TRUE', '53.32', '8.11'], contact[6:11])
        self.assertEqual(['33', 'RefPoint1'], meteo[-2:])
        self.assertEqual(['', 'E4F1', '4', 'NONE', 'This is a chat message!'], text[6:])
        self.assertEqual(['AreaA', '', '08', '1', 'FF8000FF', '', '', '', 'Area A'], graphic[7:16])
        self.assertEqual(['53.43', '9.45', '0', '10000'], graphic[16:])

    def test_post_json_and_ownunit_field_layout(self):
        _, messages = self.json_after('Sample POST request:')
        ownunit, text = messages
        self.assertEqual('OWNUNIT', ownunit[0])
        self.assertEqual(['', '33.3', '-0.15', 'Aircraft', 'SFAPMF---------'], ownunit[12:])
        self.assertEqual(15, len(ownunit[-1]))
        self.assertEqual(['E4F1', '4', 'NONE', 'This is a chat message!'], text[7:])
        post_header = self.sample_after('Sample POST request:').split('\n\n', 1)[0]
        self.assertIn('Content-Type: application/json', post_header)

    def test_compression_example_has_valid_text_layout_and_matching_bytes(self):
        section = self.text.split('### 3.3 Compression\n', 1)[1].split('\n## ', 1)[0]
        reference = re.search(r'Sample plain: `([^`]+)`', section).group(1)
        encoded = re.search(r'Sample compressed: `([^`]+)`', section).group(1)
        decoded = zlib.decompress(base64.b64decode(encoded, validate=True), -15).decode('ascii')
        self.assertEqual(reference, decoded)
        self.assertEqual(['TEXT', '53'], decoded.split(';')[:2])
        self.assertEqual(['', '', '1', 'NONE', '"This is an alert!"'], decoded.split(';')[6:])

    def test_meteo_cloud_cover_and_reference_are_separate_fields(self):
        section = self.text.split('### 6.5 METEO\n', 1)[1].split('### 6.6 TEXT', 1)[0]
        sample = re.search(r'^METEO;1C;.*$', section, re.MULTILINE).group(0)
        self.assertEqual(['33', 'RefPoint1'], sample.split(';')[-2:])

    def test_command_examples_follow_fixed_header_and_variant_fields(self):
        section = self.text.split('### 6.8 COMMAND\n', 1)[1].split('### 6.9 STATUS', 1)[0]
        samples = re.findall(r'^COMMAND;.*$', section.split('Samples:', 1)[1], re.MULTILINE)
        first, second = (sample.split(';') for sample in samples[:2])
        self.assertEqual(['ORKA', '1111', '01', '', '24', '53.32', '8.11', '1000', '5'], first[7:])
        self.assertEqual(['Drone1', '', '00', '', 'FF', 'OPEN_BAY'], second[7:])


if __name__ == '__main__':
    unittest.main()
