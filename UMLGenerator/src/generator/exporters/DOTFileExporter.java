package generator.exporters;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import generator.Graph;
import generator.INode;
import generator.Link;
import generator.commands.CMDParams;

public class DOTFileExporter implements IExporter {

	@Override
	public void export(Graph graph, CMDParams params) {
		String outFileName = params.getOptionPairs().get("out");
		String digraph = "digraph " + outFileName.substring(0, outFileName.indexOf('.')) + "{\nrankdir=\"BT\";\n";
		
		for (INode node : graph.getNodes().values() ) {
			digraph += String.format("%s [label = \"%s\"%s ];\n",
					node.getQualifiedName(),
					node.getLabel(),
					node.getAttributeString());
		}
		
		Set<Link> drawn = new HashSet<Link>();
		for (INode node : graph.getNodes().values()) {
			List<Link> links = node.getLinks();
			for (Link link : links) {
				if (!drawn.contains(link)) {
					digraph += String.format("%s [%s];\n", link.getRelationship(), link.getAttributes());
					drawn.add(link);
				}
				
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
