package generator.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CMDParams {
	
	private List<String> flags;
	private Map<String, String> optionPair;
	private List<String> args;
	private Map<String, List<String>> namedLists;
	
	public CMDParams() {
		flags = new ArrayList<String>();
		optionPair = new HashMap<String, String>();
		args = new ArrayList<String>();
		namedLists = new HashMap<String, List<String>>();
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
	
	public void addNamedList(String namedList, List<String> list) {
		namedLists.put(namedList, list);
	}
	
	public List<String> getFlags() {
		return this.flags;
	}
	public Map<String, String> getOptionPairs() {
		return this.optionPair;
	}
	public List<String> getArgs() {
		return this.args;
	}
	public Map<String, List<String>> getNamedLists() {
		return this.namedLists;
	}
}
