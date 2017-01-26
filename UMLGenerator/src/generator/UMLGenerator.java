package generator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import generator.analyzers.IAnalyzer;
import generator.commands.CMDParams;
import generator.commands.CMDParser;
import generator.commands.ICMDHandler;
import generator.exporters.IExporter;
import generator.factories.IGraphFactory;

public class UMLGenerator {
	
	private ICMDHandler cmdHandler;
	private List<IAnalyzer> analyzers;
	private List<IExporter> exporters;
	private IGraphFactory factory;
	
	public UMLGenerator() {
		this.analyzers = new ArrayList<IAnalyzer>();
		this.exporters = new ArrayList<IExporter>();
	}
	
	public void setCMDHandler(ICMDHandler handler) {
		this.cmdHandler = handler;
	}
	
	public void setGraphFactory(IGraphFactory factory) {
		this.factory = factory;
	}
	
	public void addAnalyzer(IAnalyzer analyzer) {
		this.analyzers.add(analyzer);
	}
	
	public void addExporter(IExporter exporter) {
		this.exporters.add(exporter);
	}
	
	public void execute(String[] args, File defaultConfigurationFile) {
		CMDParams params = null;
		try {
			params = CMDParser.parse(args, defaultConfigurationFile);
		} catch (IOException e) {
			System.err.println("Unable to read configuration file");
			e.printStackTrace();
		}
		cmdHandler.execute(params, analyzers, exporters, factory);
	}
	
	public IGraphFactory getGraphFactory() {
		return this.factory;
	}
	
	
}