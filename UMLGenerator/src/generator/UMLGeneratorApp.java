package generator;

import generator.commands.ICMDHandler;
import generator.exporters.DOTFileExporter;
import generator.exporters.PNGExporter;
import generator.factories.DefaultGraphFactory;
import generator.commands.DefaultCMDHandler;

public class UMLGeneratorApp {
	public static void main(String[] args) {
		UMLGenerator uml = new UMLGenerator();
		ICMDHandler handler = new DefaultCMDHandler();
		uml.setCMDHandler(handler);
		uml.setGraphFactory(new DefaultGraphFactory());
		uml.addExporter(new DOTFileExporter());
		uml.addExporter(new PNGExporter("\"C:\\Program Files (x86)\\Graphviz2.38\\bin\\dot\""));
		uml.execute(args);
	}
}
