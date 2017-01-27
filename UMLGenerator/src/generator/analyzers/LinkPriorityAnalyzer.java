package generator.analyzers;

import java.util.ArrayList;
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
		ArrayList<ILink> toRemove;
		String target;
		boolean changed = false;
		for (INode node : graph.getNodes().values()) {
			local = new HashMap<String, ILink>();
			toRemove = new ArrayList<ILink>();
			for (ILink link : node.getLinks()) {
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
			for (ILink link : toRemove) {
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
	private boolean supercedes(ILink link1, ILink link2) {
		return (link1.getPriority() > link2.getPriority());
	}

}
