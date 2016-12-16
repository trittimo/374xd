package generator;

import generator.commands.ICMDHandler;
import generator.exporters.DOTFileExporter;
import generator.factories.DefaultNodeFactory;
import generator.commands.DefaultCMDHandler;

public class UMLGeneratorApp {
	public static void main(String[] args) {
		UMLGenerator uml = new UMLGenerator();
		ICMDHandler handler = new DefaultCMDHandler();
		uml.setCMDHandler(handler);
		uml.setGraphFactory(new DefaultNodeFactory());
		uml.addExporter(new DOTFileExporter());
		uml.execute(args);
	}
}
