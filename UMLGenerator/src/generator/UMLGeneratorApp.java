package generator;

import java.io.File;

import generator.commands.DefaultCMDHandler;
import generator.commands.ICMDHandler;
import generator.exporters.DOTFileExporter;
import generator.exporters.PNGExporter;
import generator.factories.DefaultGraphFactory;

public class UMLGeneratorApp {
	public static void main(String[] args) {
		UMLGenerator uml = new UMLGenerator();
		ICMDHandler handler = new DefaultCMDHandler();
		uml.setCMDHandler(handler);
		uml.setGraphFactory(new DefaultGraphFactory());
		uml.addExporter(new DOTFileExporter());
		uml.addExporter(new PNGExporter("\"C:\\Program Files (x86)\\Graphviz2.38\\bin\\dot\""));
		uml.execute(args, new File("configs\\default.xml"));
		System.out.println("Finished");
	}
}
