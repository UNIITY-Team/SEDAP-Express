"""Compare every Markdown ICD COMMAND code with the Java reference enum."""
from pathlib import Path
import re
import unittest


class ICDCommandCodesTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        root = Path(__file__).resolve().parents[1]
        markdown = (root / 'Documentation/SEDAP-Express-ICD-for-AI-v1.4.6.md').read_text()
        table = markdown.split('CmdType + parameters:\n', 1)[1].split('\nSamples:', 1)[0]
        cls.rows = re.findall(r'^\| ([0-9A-F]{2}) \| ([^|]+) \|', table, re.MULTILINE)
        java = (root / 'SEDAPExpress/src/main/java/de/bundeswehr/uniity/sedapexpress/messages/COMMAND.java').read_text()
        cls.enum_codes = dict(re.findall(r'(\w+)\(\(byte\) 0x([0-9A-F]{2})\)', java))

    def test_codes_are_unique_and_cover_the_reference_enum(self):
        codes = [code for code, _ in self.rows]
        self.assertGreater(len(codes), 40)
        self.assertEqual(len(codes), len(set(codes)))
        self.assertEqual(set(self.enum_codes.values()), set(codes))

    def test_corrected_commands_match_reference(self):
        expected = {'Test engine': 'TestEngine', 'Set engine power': 'SetEnginePower',
                    'Stop engine': 'StopEngine', 'Stop movement': 'StopMovement',
                    'Toggle lights': 'ToggleLights', 'Deploy parachute': 'DeployParachute',
                    'StartEngagement': 'StartEngagement'}
        codes = {label.strip(): code for code, label in self.rows}
        for label, enum_name in expected.items():
            with self.subTest(command=label):
                self.assertEqual(self.enum_codes[enum_name], codes[label])


if __name__ == '__main__':
    unittest.main()
