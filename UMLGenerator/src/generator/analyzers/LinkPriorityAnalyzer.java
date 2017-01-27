package generator.analyzers;

import java.util.HashMap;

import generator.Graph;
import generator.ILink;
import generator.INode;
import generator.commands.CMDParams;
import generator.factories.IGraphFactory;

public class LinkPriorityAnalyzer implements IAnalyzer {

	@Override
	public boolean analyze(Graph graph, CMDParams params, IGraphFactory factory) {
		HashMap<String, ILink> local;
		String target;
		for (INode node : graph.getNodes().values()) {
			local = new HashMap<String, ILink>();
			for (ILink link : node.getLinks()) {
				target = link.getRelationship().split("->")[1].trim();
				if (!local.containsKey(target))
					local.put(target, link);
				else {
					if (supercedes(link, local.get(target))) {
						node.removeLink(local.get(target));
						local.put(target, link);
					} else {
						node.removeLink(link);
					}
				}
			}
		}
		return false;
	}

	/**
	 * Return true when link1 is more important than link2
	 * 
	 * @param link1
	 * @param link2
	 * @return
	 */
	private boolean supercedes(ILink link1, ILink link2) {
		return (link1.getPriority() > link2.getPriority());
	}

}
