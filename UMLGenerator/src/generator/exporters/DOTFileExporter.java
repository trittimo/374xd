package generator.exporters;

import generator.Graph;
import generator.INode;
import generator.commands.CMDParams;
import generator.nodes.JavaClassNode;

public class DOTFileExporter implements IExporter {

	@Override
	public void export(Graph graph, CMDParams params) {
		
		for (String nodeName : graph.getNodes().keySet()) {
			
		}
	}

}
