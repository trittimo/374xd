package generator.exporters;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;

import generator.Graph;
import generator.ILink;
import generator.INode;
import generator.commands.CMDParams;
import generator.nodes.JavaClassNode;

public class DOTFileExporter implements IExporter {

	@Override
	public void export(Graph graph, CMDParams params) {
		
		String outFileName = params.getOptionPairs().get("out");
		String digraph = "digraph " + outFileName.substring(0, outFileName.indexOf('.')) + "{\n";
		
		
		
		
		
		for (String nodeName : graph.getNodes().keySet()) {
			INode node = graph.getNodes().get(nodeName);
			digraph += String.format("%s [ shape=\"record\", label = \"%s\" ];\n", nodeName.replaceAll("\\.", "_"), node.getLabel());
		}
		
		for (INode node : graph.getNodes().values()) {
			List<ILink> links = node.getLinks();
			for (ILink link : links) {
				digraph += String.format("%s [%s];\n", link.getRelationship(), link.getAttributes());
			}
		}
		
		digraph += "}";
		
		Path file = Paths.get(outFileName);
		byte data[] = digraph.getBytes();
		try {
			Files.write(file, data);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
