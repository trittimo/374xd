package generator;

import java.util.ArrayList;
import java.util.List;

import generator.analyzers.IAnalyzer;
import generator.commands.CMDParams;
import generator.commands.CMDParser;
import generator.commands.ICMDHandler;
import generator.exporters.IExporter;
import generator.factories.INodeFactory;

public class UMLGenerator {
	
	private ICMDHandler cmdHandler;
	private List<IAnalyzer> analyzers;
	private List<IExporter> exporters;
	private INodeFactory factory;
	
	public UMLGenerator() {
		this.analyzers = new ArrayList<IAnalyzer>();
		this.exporters = new ArrayList<IExporter>();
	}
	
	public void setCMDHandler(ICMDHandler handler) {
		this.cmdHandler = handler;
	}
	
	public void setGraphFactory(INodeFactory factory) {
		this.factory = factory;
	}
	
	public void addAnalyzer(IAnalyzer analyzer) {
		this.analyzers.add(analyzer);
	}
	
	public void addExporter(IExporter exporter) {
		this.exporters.add(exporter);
	}
	
	public void execute(String[] args) {
		CMDParams params = CMDParser.parse(args);
		cmdHandler.execute(params);
	}
	
	public INodeFactory getGraphFactory() {
		return this.factory;
	}
	
	
}