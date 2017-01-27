package generator.exporters;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
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
		String digraph = "digraph " + outFileName.substring(0, outFileName.indexOf('.')) + "{\nconcentrate=true;\nrankdir=\"BT\";\n";
		
		for (INode node : graph.getNodes().values() ) {
			digraph += String.format("%s [label = \"%s\"%s ];\n",
					node.getQualifiedName(),
					node.getLabel(),
					node.getAttributeString());
		}
		
		
		for (INode node : graph.getNodes().values()) {
			List<ILink> links = node.getLinks();
			for (ILink link : links) {
				digraph += String.format("%s [%s];\n", link.getRelationship(), link.getAttributes());
			}
		}
		
		digraph += "}";
		
		
		digraph = sanitize(digraph);
		
		if (params.getFlags().contains("y")) {
			digraph = digraph.replaceAll("#[^\\\\l]*DOLLAR.*?\\\\l", "");
		}
		
		Path file = Paths.get(outFileName);
		byte data[] = digraph.getBytes();
		try {
			Files.write(file, data);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	private String sanitize(String string) {
		return string.replaceAll("\\$", "_DOLLAR_").replaceAll("\\.", "_").replaceAll("#", "\\#").replaceAll(",,,", "...");
	}

}
