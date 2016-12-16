package generator.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CMDParams {
	
	private List<String> flags;
	private Map<String, String> optionPair;
	private List<String> args;
	
	public CMDParams() {
		flags = new ArrayList<String>();
		optionPair = new HashMap<String, String>();
		args = new ArrayList<String>();
	}
	
	public void addFlag(String s) {
		if (!flags.contains(s)) {
			flags.add(s);
		}
	}
	
	public void addPair(String key, String val) {
		optionPair.put(key, val);
	}
	
	public void addArgument(String s) {
		args.add(s);
	}
}
