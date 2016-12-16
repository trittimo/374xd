package generator;

import generator.commands.ICMDHandler;
import generator.commands.DefaultCMDHandler;

public class UMLGeneratorApp {
	public static void main(String[] args) {
		UMLGenerator uml = new UMLGenerator();
		ICMDHandler handler = new DefaultCMDHandler(uml);
		// todo
	}
}
