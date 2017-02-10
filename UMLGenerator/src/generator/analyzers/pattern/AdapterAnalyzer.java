package generator.analyzers.pattern;

import java.util.HashSet;

import generator.Graph;
import generator.INode;
import generator.Link;
import generator.StyleAttribute;
import generator.analyzers.IAnalyzer;
import generator.commands.CMDParams;
import generator.factories.IGraphFactory;
import generator.links.AssociationLink;
import generator.links.AssociationManyLink;
import generator.links.ExtendsLink;
import generator.links.ImplementsLink;
import generator.nodes.JavaClassNode;

//for all of the methods you override, you make a call to the same inner object
public class AdapterAnalyzer implements IAnalyzer {
	
	private boolean hasRun = false;
	private static final int RUN_LIMIT = 500;
	@Override
	public boolean analyze(Graph graph, CMDParams params, IGraphFactory factory) {
		if (hasRun) {
			return false;
		}
		hasRun = true;
		
		HashSet<Link> links = new HashSet<Link>();
		for (INode node : graph.getNodes().values()) {
			System.out.println(node.getQualifiedName());
			for (Link edge : node.getLinks()) {
				links.add(edge);
			}
		}
		
		for (Link a : links) {
			for (Link b : links) {
				// Check if tail the same
				if (a.getStart().equals(b.getStart())) {
					// Check if end different
					if (!a.getEnd().equals(b.getEnd())) {
						if (isInherits(a) && isAssociation(b)) {
							setColors(a, b, graph);
						}
					}
				}
			}
		}
		return false;
	}
	private void setColors(Link targetLink, Link adapteeLink, Graph g) {
		JavaClassNode adapter = (JavaClassNode) g.getNodes().get(targetLink.getStart());
		JavaClassNode target = (JavaClassNode) g.getNodes().get(targetLink.getEnd());
		JavaClassNode adaptee = (JavaClassNode) g.getNodes().get(adapteeLink.getEnd());
		adapter.setAttribute(new StyleAttribute("fillcolor", "firebrick", 15));
		adapter.setAttribute(new StyleAttribute("style", "filled", 15));
		adapter.addStereotype("adapter");
		target.setAttribute(new StyleAttribute("fillcolor", "firebrick", 15));
		target.setAttribute(new StyleAttribute("style", "filled", 15));
		target.addStereotype("target");
		adaptee.setAttribute(new StyleAttribute("fillcolor", "firebrick", 15));
		adaptee.setAttribute(new StyleAttribute("style", "filled", 15));
		adaptee.addStereotype("adaptee");
		adapteeLink.setAttribute(new StyleAttribute("label", "<<adapts>>", 15));
		
	}
	private boolean isInherits(Link l) {
		return l instanceof ImplementsLink || l instanceof ExtendsLink;
	}
	private boolean isAssociation(Link l) {
		return l instanceof AssociationLink || l instanceof AssociationManyLink;
	}
}