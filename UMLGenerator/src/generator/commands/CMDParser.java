package generator.commands;

public class CMDParser {
	
	public static CMDParams parse(String[] args) {
		CMDParams cmd = new CMDParams();
		for (int i = 0; i < args.length; i++) {
			if (args[i].startsWith("--")) {
				cmd.addPair(args[i].substring(2), args[++i]);
			}
			else if (args[i].startsWith("-")) {
				cmd.addFlag(args[i].substring(1));
			}
			else {
				cmd.addArgument(args[i]);
			}
		}
		return cmd;
	}
}
