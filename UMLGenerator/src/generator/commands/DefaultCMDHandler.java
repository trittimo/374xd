package generator.commands;

import java.util.List;
import generator.Graph;
import generator.analyzers.FieldAnalyzer;
import generator.analyzers.IAnalyzer;
import generator.analyzers.LinkPriorityAnalyzer;
import generator.analyzers.MethodBodyAnalyzer;
import generator.analyzers.RecursiveClassAnalyzer;
import generator.analyzers.SignatureAnalyzer;
import generator.exporters.IExporter;
import generator.factories.IGraphFactory;

public class DefaultCMDHandler implements ICMDHandler {
	
	@Override
	public void execute(CMDParams params, List<IAnalyzer> analyzers, List<IExporter> exporters, IGraphFactory factory) {
		
		List<String> flags = params.getFlags();
		
		if (flags.contains("r")) {
			analyzers.add(new RecursiveClassAnalyzer());
		}
		
		if (flags.contains("f")) {
			analyzers.add(new FieldAnalyzer());
		}
		
		if (flags.contains("s")) {
			analyzers.add(new SignatureAnalyzer());
		}
		
		if (flags.contains("m")) {
			analyzers.add(new MethodBodyAnalyzer());
		}
		
		// always add the LinkPriorityAnalyzer last so we can deal with that
		analyzers.add(new LinkPriorityAnalyzer());
		
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
