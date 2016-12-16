package generator.analyzers;

import generator.Graph;
import generator.commands.CMDParams;
import generator.factories.IGraphFactory;

public interface IAnalyzer {

	void analyze(Graph graph, CMDParams params, IGraphFactory factory);

}
