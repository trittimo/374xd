package generator.analyzers;

import java.util.List;

import generator.Graph;
import generator.INode;
import generator.Link;
import generator.commands.CMDParams;
import generator.factories.IGraphFactory;

public class BidirectionalLinkReplacementAnalyzer implements IAnalyzer {
	
	@Override
	public boolean analyze(Graph graph, CMDParams params, IGraphFactory factory) {
		// TODO check if bidirectional links, if so replace
		boolean changed = false;
		for (INode a : graph.getNodes().values()) {
			for (INode b : graph.getNodes().values()) {
				// Make sure nodes aren't the same
				if (!a.equals(b)) {
					changed |= replaceWithBidirectionals(a, b);
				}
			}
		}
		return changed;
	}

	private boolean replaceWithBidirectionals(INode a, INode b) {
		List<Link> aLinks = a.getLinks();
		List<Link> bLinks = b.getLinks();
		
		boolean changed = false;
		for (int i = 0; i < aLinks.size(); i++) {
			for (int j = 0; j < bLinks.size(); j++) {
				Link aL = aLinks.get(i);
				Link bL = bLinks.get(j);
				if (!aL.getClass().equals(bL.getClass())) {
					continue;
				}
				
				if (aL.getStart().equals(bL.getEnd()) && aL.getEnd().equals(bL.getStart())) {
					Link newLink = aL.getBidirectional();
					aLinks.set(i, newLink);
					bLinks.set(j, newLink);
					changed = true;
				}
			}
		}
		return changed;
	}

	
}
