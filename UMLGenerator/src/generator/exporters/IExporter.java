package generator.exporters;

import generator.Graph;
import generator.commands.CMDParams;

public interface IExporter {
	public void export(Graph graph, CMDParams params);
}
