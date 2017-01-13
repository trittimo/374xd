package generator.commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.common.reflect.ClassPath;

import generator.Graph;
import generator.INode;
import generator.UMLGenerator;
import generator.analyzers.FieldAnalyzer;
import generator.analyzers.IAnalyzer;
import generator.analyzers.RecursiveClassAnalyzer;
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
		
		Graph graph = new Graph();
		try {
			for (String inputClass : params.getArgs()) {
				Thread.currentThread().getContextClassLoader().loadClass(inputClass);
				factory.addNodeToGraph(graph, inputClass); 
			}
			
			
			for (IAnalyzer analyzer : analyzers) {
				analyzer.analyze(graph, params, factory);
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
