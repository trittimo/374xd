package generator.commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import generator.Graph;
import generator.INode;
import generator.UMLGenerator;
import generator.analyzers.IAnalyzer;
import generator.exporters.IExporter;
import generator.factories.IGraphFactory;

public class DefaultCMDHandler implements ICMDHandler {
	
	@Override
	public void execute(CMDParams params, List<IAnalyzer> analyzers, List<IExporter> exporters, IGraphFactory factory) {
		Graph graph = new Graph();
		try {
			for (String inputClass : params.getArgs()) {
				factory.addNodeToGraph(graph, inputClass);
			}
			
			factory.linkGraph(graph);
			
			for (IAnalyzer analyzer : analyzers) {
				// analyze
				// TODO
			}
			
			for (IExporter exporter : exporters) {
				exporter.export(graph, params);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
