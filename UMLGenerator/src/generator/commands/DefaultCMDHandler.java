package generator.commands;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.InvalidPropertiesFormatException;
import java.util.List;
import java.util.Properties;

import generator.Graph;
import generator.analyzers.BidirectionalLinkReplacementAnalyzer;
import generator.analyzers.BlacklistAnalyzer;
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
		List<IAnalyzer> lastPass = new ArrayList<IAnalyzer>();
		List<String> flags = params.getFlags();
		
		// Load from External Class Index
		String eciName = "eci.xml";
		if (params.getOptionPairs().containsKey("ECI")) {
			eciName = params.getOptionPairs().get("ECI");
		}
		try {
			Properties toLoad = loadExternalClassIndex(eciName);
			ClassLoader cl = ExternalClassLoader.getClassLoader(toLoad);
			Thread.currentThread().setContextClassLoader(cl);
			
			for (Object key : toLoad.keySet()) {
				System.out.printf("%s: %s%n",
						key.toString(),
						Thread.currentThread().getContextClassLoader().loadClass(key.toString()).getName());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// get flags
		
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
		
		lastPass.add(new BidirectionalLinkReplacementAnalyzer());
		
		if (params.getOptionPairs().containsKey("analyzers")) {
			String[] analyzerNames = params.getOptionPairs().get("analyzers").split(";");
			for (String name : analyzerNames) {
				if (name.isEmpty())
					continue;
				try {
					@SuppressWarnings("unchecked")
					Class<IAnalyzer> analyzer = (Class<IAnalyzer>) Class.forName(name);
					analyzers.add(analyzer.newInstance());
				} catch (Exception e) {
					System.err.println("Could not load custom analyzer");
					e.printStackTrace();
				}
			}
		}

		if (params.getOptionPairs().containsKey("lastpass")) {
			String[] analyzerNames = params.getOptionPairs().get("lastpass").split(";");
			for (String name : analyzerNames) {
				if (name.isEmpty())
					continue;
				try {
					@SuppressWarnings("unchecked")
					Class<IAnalyzer> analyzer = (Class<IAnalyzer>) Class.forName(name);
					lastPass.add(analyzer.newInstance());
				} catch (Exception e) {
					System.err.println("Could not load custom analyzer");
					e.printStackTrace();
				}
			}
		}
		
		// always add the LinkPriorityAnalyzer last so we can deal with that
		lastPass.add(new LinkPriorityAnalyzer());
		
		// always remove any nodes that don't belong last
		lastPass.add(new BlacklistAnalyzer());
		
		
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
			
			updateRequired = true;
			while (updateRequired) {
				updateRequired = false;
				for (IAnalyzer analyzer : lastPass) {
					//System.err.printf("DEBUG: Analyzing with %s%n", analyzer.getClass().getName());
					updateRequired |= analyzer.analyze(graph, params, factory);
				}
			}
			
			for (IExporter exporter : exporters) {
				exporter.export(graph, params);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	

	protected Properties loadExternalClassIndex(String eciName) throws InvalidPropertiesFormatException, FileNotFoundException, IOException {
		Properties classes = new Properties();
		classes.loadFromXML(new FileInputStream(eciName));
		return classes;
	}

}
