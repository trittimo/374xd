package generator.commands;

import generator.UMLGenerator;

public class DefaultCMDHandler implements ICMDHandler {
	
	private UMLGenerator api;
	public DefaultCMDHandler(UMLGenerator api) {
		this.api = api;
	}
	
	@Override
	public void execute(CMDParams params) {
		
	}

}
