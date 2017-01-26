package generator.commands;

import java.util.List;
import generator.Graph;
import generator.analyzers.FieldAnalyzer;
import generator.analyzers.IAnalyzer;
import generator.analyzers.MethodBodyAnalyzer;
import generator.analyzers.RecursiveClassAnalyzer;
import generator.analyzers.SignatureAnalyzer;
import generator.exporters.IExporter;
import generator.factories.IGraphFactory;

public class DefaultCMDHandler implements ICMDHandler {
	
	@Override
	public void execute(CMDParams params, List<IAnalyzer> analyzers, List<IExporter> exporters, IGraphFactory factory) {
		
		
		if (params.getFlags().contains("r")) {
			analyzers.add(new RecursiveClassAnalyzer());
		}
		
		if (params.getFlags().contains("f")) {
			analyzers.add(new FieldAnalyzer());
		}
		
		if (params.getFlags().contains("m")) {
			analyzers.add(new SignatureAnalyzer());
		}
		
		analyzers.add(new MethodBodyAnalyzer());
		
		Graph graph = new Graph();
		try {
			for (String inputClass : params.getArgs()) {
				factory.addNodeToGraph(graph, inputClass); 
			}
			
			boolean updateRequired = true;
			while (updateRequired) {
				updateRequired = false;
				for (IAnalyzer analyzer : analyzers) {
					//System.err.printf("DEBUG: Analyzing with %s%n", analyzer.getClass().getName());
					updateRequired |= analyzer.analyze(graph, params, factory);
				}
			}
			
			factory.linkGraph(graph);
			
			for (IExporter exporter : exporters) {
				exporter.export(graph, params);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
