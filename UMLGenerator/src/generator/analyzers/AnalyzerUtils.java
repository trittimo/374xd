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
		//System.out.println("method = " + m.name);
		return parseSignature(m.desc);
		
	}
	
	public static List<String> parseSignature(String sig) {
		System.out.println("parseSig: " + sig);
		List<String> list = new ArrayList<String>();
		String[] res = sig.split(";");
		if (res.length == 0) {
			return list;
		}
		
		for (int i = 0; i < res.length; i++) {
			String current;
			if (res[i].startsWith("(")) {
				current = res[i].substring(1,res[i].length());
			} else if (res[i].startsWith(")")) {
				current = res[i].substring(1);
			} else {
				current = res[i];
			}
			System.out.println("current=" + current);
			
			if (current.startsWith("L")) {
				//System.out.println(current);
				list.add(current.substring(1));
			}
		}
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
