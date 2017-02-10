package generator.analyzers;

import java.util.ArrayList;
import java.util.HashMap;

import generator.Graph;
import generator.Link;
import generator.INode;
import generator.commands.CMDParams;
import generator.factories.IGraphFactory;
import generator.links.AssociationLink;
import generator.links.AssociationManyLink;
import generator.links.DependencyLink;
import generator.links.DependencyManyLink;

/**
 * 
 * This analyzer makes it so that there cannot be both dependency and association links with the same start and end.
 * It also makes sure there is only one Dependency/DependencyMany/Association/AssociationMany link between those nodes.
 *  
 * @author AMcKee
 *
 */
public class LinkPriorityAnalyzer implements IAnalyzer {

	@Override
	public boolean analyze(Graph graph, CMDParams params, IGraphFactory factory) {
		
		boolean changed = false;
		
		// assoc over deps
		
		for (INode node : graph.getNodes().values()) {
			HashMap<String, Link> localDeps = new HashMap<String, Link>();
			HashMap<String, Link> localAssoc = new HashMap<String, Link>();
			ArrayList<Link> toRemove = new ArrayList<Link>();
			for (Link link : node.getLinks()) {
				String target = link.getEnd();
				if (link instanceof DependencyLink) {
					// if we already have dep, ignore and remove this one
					if (localDeps.containsKey(target)) {
						toRemove.add(link);
					}
					// otherwise note dependency
					else {
						localDeps.put(target, link);
					}
				}
				else if (link instanceof DependencyManyLink) {
					// if we already have dep, remove it
					if (localDeps.containsKey(target)) {
						toRemove.add(localDeps.get(target));
					}
					// note dependency (always prioritize many over one)
					localDeps.put(target, link);
				}
				else if (link instanceof AssociationLink) {
					// if we already have dep, ignore and remove this one
					if (localAssoc.containsKey(target)) {
						toRemove.add(link);
					}
					// otherwise note dependency
					else {
						localAssoc.put(target, link);
					}
				}
				else if (link instanceof AssociationManyLink) {
					// if we already have assoc, remove it
					if (localAssoc.containsKey(target)) {
						toRemove.add(localAssoc.get(target));
					}
					// note assoc (this makes it always prioritize many over one)
					localAssoc.put(target, link);
				}
			}
			
			// finally remove all deps where we have assoc
			for (String target : localAssoc.keySet()) {
				if (localDeps.containsKey(target))
					toRemove.add(localDeps.get(target));
			}
			
			// actually do the removing
			if (toRemove.size() > 0)
				changed = true;
			for (Link link : toRemove) {
				node.removeLink(link);
			}
		}
		
		return changed;
	}

}
