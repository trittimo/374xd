package generator.analyzers;

import generator.Graph;
import generator.commands.CMDParams;
import generator.factories.IGraphFactory;

public interface IAnalyzer {

	/**
	 * Analyze the graph and make changes if appropriate
	 * 
	 * @param graph
	 * @param params
	 * @param factory
	 * 
	 * @return Returns a boolean indicating whether or not changes were made that other IAnalyzers should care about
	 *  (e.g. Making lines red is not something other IAnalyzers would care about, but adding a new node is something they should know about) 
	 */
	boolean analyze(Graph graph, CMDParams params, IGraphFactory factory);
	
	
	/* TODO Better solution
	 * Pass in another argument to analyze(...) which is a new class AnalyzerUpdateFlag. 
	 * It can record if changes were made to links, nodes, or style, and by who.
	 * This way we can more intelligently decide if we need to update stuff
	 */

}
