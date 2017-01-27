package generator.commands;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class CMDParser {
	public static CMDParams parse(String[] args, File defaultConfigurationFile) throws IOException {
		return parse(args, new CMDParams(), defaultConfigurationFile, false);
	}
	
	private static CMDParams parse(String[] args, CMDParams cmd, File defaultConfigurationFile, boolean hasParsedDefaultConfig) throws IOException {
		File config = null;
		for (int i = 0; i < args.length; i++) {
			if (args[i].startsWith("--config")) {
				config = new File(args[++i]);
			} else if (args[i].startsWith("--")) {
				cmd.addPair(args[i].substring(2), args[++i]);
			}
			else if (args[i].startsWith("-")) {
				cmd.addFlag(args[i].substring(1));
			}
			else {
				cmd.addArgument(args[i]);
			}
		}
		
		if (config == null && !hasParsedDefaultConfig) {
			// Use default
			return parsePropertiesStream(defaultConfigurationFile, defaultConfigurationFile, cmd);
		} else if (config != null) {
			return parsePropertiesStream(config, defaultConfigurationFile, cmd);
		} else {
			return cmd;
		}
	}
	
	public static CMDParams parsePropertiesStream(File input, File defaultConfig, CMDParams current) throws IOException {
		Properties settings = new Properties();
		settings.loadFromXML(new FileInputStream(input));
		
		ArrayList<String> newArgs = new ArrayList<String>();
		
		String[] whitelisted = settings.getProperty("whitelist").split(",");
		String[] blacklisted = settings.getProperty("blacklist").split(",");
		String[] pairs = settings.getProperty("pairs").split(",");
		String[] args = settings.getProperty("arguments").split(",");
		String[] flags = settings.getProperty("flags").split(",");
		
		List<String> theWhitelist = new ArrayList<String>();
		List<String> theBlacklist = new ArrayList<String>();
		
		if (current.getNamedLists().containsKey("blacklist")) {
			theBlacklist = current.getNamedLists().get("blacklist");
		}
		if (current.getNamedLists().containsKey("whitelist")) {
			theWhitelist = current.getNamedLists().get("whitelist");
		}
		
		for (String s : whitelisted) {
			if (s.isEmpty()) {
				continue;
			}
			theWhitelist.add(s.trim());
		}
		for (String s : blacklisted) {
			if (s.isEmpty()) {
				continue;
			}
			theBlacklist.add(s.trim());
		}
		
		
		current.addNamedList("blacklist", theBlacklist);
		current.addNamedList("whitelist", theWhitelist);
		
		for (String s : pairs) {
			if (s.isEmpty()) {
				continue;
			}
			String pair[] = s.split("=");
			String left = pair[0].trim();
			String right = pair[1].trim();
			if (!current.getOptionPairs().containsKey(left)) {
				newArgs.add("--" + left);
				newArgs.add(right);
			}
		}
		
		for (String s : args) {
			if (s.isEmpty()) {
				continue;
			}
			if (!current.getArgs().contains(s.trim()))
				newArgs.add(s.trim());
		}
		
		for (String s : flags) {
			if (s.isEmpty()) {
				continue;
			}
			if (!current.getFlags().contains(s.trim()))
				newArgs.add("-" + s.trim());
		}
		
		return parse(newArgs.toArray(new String[newArgs.size()]), current, defaultConfig, input.equals(defaultConfig));
	}
}
