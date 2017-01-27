package generator.analyzers;

import java.util.ArrayList;
import java.util.List;

import generator.Graph;
import generator.ILink;
import generator.INode;
import generator.commands.CMDParams;
import generator.factories.IGraphFactory;

public class BlacklistAnalyzer implements IAnalyzer {

	@Override
	public boolean analyze(Graph graph, CMDParams params, IGraphFactory factory) {

		if (!params.getNamedLists().containsKey("blacklist")) {
			return false;			
		}
		
		List<String> blacklist = params.getNamedLists().get("blacklist");
		
		if (blacklist.isEmpty()) {
			return false;
		}
		
		List<String> nodes = new ArrayList<String>();
		
		
		for (String node : graph.getNodes().keySet()) {
			for (String blacklisted : blacklist) {
				if (node.matches(blacklisted)) {
					nodes.add(node);
				}
			}
		}
		
		List<ILink> links;
		for (INode node : graph.getNodes().values()) {
			links = new ArrayList<ILink>();
			for (ILink link : node.getLinks()) {
				if (nodes.contains(link.getEnd())) {
					links.add(link);
				}
			}
			for (ILink link : links) {
				node.removeLink(link);
			}
		}
		
		for (String node : nodes) {
			graph.getNodes().remove(node);
		}
		
		
		
		return false;
	}
	
}
