package generator.analyzers;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.ParameterNode;


public class AnalyzerUtils {
	@SuppressWarnings("unchecked")
	public static List<String> parseClassesFromMethod(MethodNode m) {
		String s;
		System.out.println("method = " + m.name);
		return parseSignature(m.desc);
		
	}
	
	public static List<String> parseSignature(String sig) {
		System.out.println("parseSig: " + sig);
		Matcher m = Pattern.compile("\\((?:(L.*?);|\\[?.|)\\)((?:L.*?;|\\[?.|)*)").matcher(sig.replaceAll("/", "."));
		ArrayList<String> list =  new ArrayList<String>();
		if (m.matches()) {
			String args;
			if (m.groupCount() > 1) {
				System.out.println("Adding ret: " + m.group(1));
				list.add(m.group(1)); // add return value;
				args = m.group(2);
			} else
				args = m.group(1);
			m = Pattern.compile("(?:L(.*?);|\\[?.)").matcher(args);
			while (m.find()) {
				if (m.groupCount() > 0) {
					System.out.println("Adding param: " + m.group(1));
					list.add(m.group(1));
				}
			}
		}
		//throw new RuntimeException("Could not parse sig: " + sig);
		return list;
	}

	public static String parseDesc(String desc) {
		System.out.println("parseD: " + desc);
		Matcher m = Pattern.compile("[\\(\\)\\[]*L(.*);?").matcher(desc.replaceAll("/", "."));
		if (m.matches()) {
			return m.group(1);
		}
		//throw new RuntimeException("Could not parse desc: " + desc);
		return null;
	}
}
