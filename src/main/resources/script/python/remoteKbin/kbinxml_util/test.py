from kbinxml_util import KBinXML
# python 2/3 cross compat
from io import open

with open(r'E:\torii_websocket\testcases.xml', 'rb') as f:
    xml_in = f.read()
with open(r'E:\torii_websocket\testcases_out.xml', 'r', encoding='UTF-8') as f:
    expected_xml = f.read()
with open(r'E:\torii_websocket\testcases_out.kbin', 'rb') as f:
    expected_bin = f.read()
print(xml_in)
k = KBinXML(xml_in)
print(k)
kbin = k.to_binary()
print(kbin)
if kbin != expected_bin:
    with open('failed_test.kbin', 'wb') as f:
        f.write(kbin)
    raise AssertionError('Binary output does not match, check failed_test.kbin')
else:
    print('XML -> Binary correct!')

backwards = KBinXML(kbin)
btext = backwards.to_text()
if btext != expected_xml:
    with open('failed_test.xml', 'w', encoding='UTF-8') as f:
        f.write(btext)
    raise AssertionError('XML putput does not match, check failed_test.xml')
else:
    print('Binary -> XML correct!')
