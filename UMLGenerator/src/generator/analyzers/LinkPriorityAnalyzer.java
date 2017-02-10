package generator.analyzers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import generator.Graph;
import generator.Link;
import generator.INode;
import generator.commands.CMDParams;
import generator.factories.IGraphFactory;

public class LinkPriorityAnalyzer implements IAnalyzer {

	@Override
	public boolean analyze(Graph graph, CMDParams params, IGraphFactory factory) {
		HashMap<String, Link> local;
		ArrayList<Link> toRemove;
		String target;
		boolean changed = false;
		for (INode node : graph.getNodes().values()) {
			local = new HashMap<String, Link>();
			toRemove = new ArrayList<Link>();
			for (Link link : node.getLinks()) {
				target = link.getEnd();
				if (!local.containsKey(target))
					local.put(target, link);
				else {
					if (supercedes(link, local.get(target))) {
						toRemove.add(local.get(target));
						local.put(target, link);
					} else {
						toRemove.add(link);
					}
				}
			}
			if (toRemove.size() > 0)
				changed = true;
			for (Link link : toRemove) {
				node.removeLink(link);
			}
		}
		
		return changed;
	}
	
	/**
	 * Return true when link1 is more important than link2
	 * 
	 * @param link1
	 * @param link2
	 * @return
	 */
	private boolean supercedes(Link link1, Link link2) {
		return (link1.getPriority() > link2.getPriority());
	}

}
