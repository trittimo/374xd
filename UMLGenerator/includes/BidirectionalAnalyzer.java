package analyzers;

import generator.Graph;
import generator.ILink;
import generator.INode;
import generator.StyleAttribute;
import generator.analyzers.IAnalyzer;
import generator.commands.CMDParams;
import generator.factories.IGraphFactory;

public class BidirectionalAnalyzer implements IAnalyzer {

	@Override
	public boolean analyze(Graph graph, CMDParams params, IGraphFactory factory) {

		for (INode a : graph.getNodes().values()) {
			for (INode b : graph.getNodes().values()) {
				boolean aToB = false;
				boolean bToA = false;
				
				for (ILink linkA : a.getLinks()) {
					aToB |= linkA.getEnd().equals(b.getQualifiedName());
				}
				for (ILink linkB : b.getLinks()) {
					bToA |= linkB.getEnd().equals(a.getQualifiedName());
				}
				
				if (aToB && bToA) {
					StyleAttribute red = new StyleAttribute("color","red",10);
					a.setAttribute(red);
					b.setAttribute(red);
					for (ILink link : a.getLinks()) {
						if (link.getEnd().equals(b.getQualifiedName())) {
							link.setAttribute(red);
						}
					}
					for (ILink link : b.getLinks()) {
						if (link.getEnd().equals(a.getQualifiedName())) {
							link.setAttribute(red);
						}
					}
				}
			}
		}
		
		return false;
	}

}
