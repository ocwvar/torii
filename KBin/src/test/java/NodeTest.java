import com.ocwvar.kbin.KBinXml;
import com.ocwvar.utils.IO;
import com.ocwvar.xml.node.Node;
import com.ocwvar.xml.node.NodeHelper;

public class NodeTest {

	public static void main( String[] args ) throws Exception {
		final byte[] bytes = IO.loadFile( "H:\\node_test.xml" );
		final Node node = NodeHelper.xml2Node( new String( bytes ) );
		final String text = node.toXmlText();
		IO.outputFile( true, "H:\\note_output.kbin", KBinXml.encode( text ) );
	}

}
