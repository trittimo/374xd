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
		if (params.getNamedLists().containsKey("whitelist")) {
			List<String> whitelist = params.getNamedLists().get("whitelist");
			List<String> toRemove = new ArrayList<String>();
			
			for (String node : graph.getNodes().keySet()) {
				if (!whitelist.contains(node)) {
					toRemove.add(node);
				}
			}
			
			List<ILink> links;
			for (INode node : graph.getNodes().values()) {
				links = new ArrayList<ILink>();
				for (ILink link : node.getLinks()) {
					if (!whitelist.contains(link.getStart()) || !whitelist.contains(link.getEnd())) {
						links.add(link);
					} else {
						System.out.println("Keeping" + link);
					}
				}
				for (ILink link : links) {
					node.removeLink(link);
				}
			}
			
			for (String node : toRemove) {
				System.out.printf("Removing: %s\n", node);
				graph.getNodes().remove(node);
			}
			
			return false;
			
		}
		
		if (!params.getNamedLists().containsKey("blacklist")) {
			return false;			
		}
		
		List<String> blacklist = params.getNamedLists().get("blacklist");
		
		if (blacklist.isEmpty()) {
			return false;
		}
		
		List<String> toRemove = new ArrayList<String>();
		
		
		for (String node : graph.getNodes().keySet()) {
			for (String blacklisted : blacklist) {
				if (node.matches(blacklisted)) {
					toRemove.add(node);
				}
			}
		}
		
		List<ILink> links;
		for (INode node : graph.getNodes().values()) {
			links = new ArrayList<ILink>();
			for (ILink link : node.getLinks()) {
				if (toRemove.contains(link.getEnd())) {
					links.add(link);
				}
			}
			for (ILink link : links) {
				node.removeLink(link);
			}
		}
		
		for (String node : toRemove) {
			graph.getNodes().remove(node);
		}
		
		
		
		return false;
	}
	
}
