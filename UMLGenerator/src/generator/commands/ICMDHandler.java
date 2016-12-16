package generator.commands;

import java.util.List;

import generator.analyzers.IAnalyzer;
import generator.exporters.IExporter;
import generator.factories.IGraphFactory;

public interface ICMDHandler {
	public abstract void execute(CMDParams params, List<IAnalyzer> analyzers, List<IExporter> exporters, IGraphFactory factory);

}
