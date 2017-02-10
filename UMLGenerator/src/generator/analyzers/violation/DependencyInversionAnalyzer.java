package generator.analyzers.violation;

import generator.Graph;
import generator.analyzers.IAnalyzer;
import generator.commands.CMDParams;
import generator.factories.IGraphFactory;

/**
 * Detects violations in dependency inversion principle. 
 * 
 * "Depend on abstractions. Do not depend on concrete classes."
 * 
 * @author AMcKee
 *
 */
public class DependencyInversionAnalyzer implements IAnalyzer {

	@Override
	public boolean analyze(Graph graph, CMDParams params, IGraphFactory factory) {
		boolean changed = false;
		
		 
		
		return changed;
	}
	
}
