package generator.analyzers;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodNode;

import generator.Graph;
import generator.INode;
import generator.commands.CMDParams;
import generator.factories.IGraphFactory;
import generator.nodes.JavaClassNode;

public class MethodBodyAnalyzer  implements IAnalyzer {

	@SuppressWarnings("unchecked")
	@Override
	public void analyze(Graph graph, CMDParams params, IGraphFactory factory) {
		String toparse;
		HashSet<String> toLink = null;
		HashMap<String, HashSet<String>> map = new HashMap<String, HashSet<String>>();
		
		// loop over the nodes
		for (INode node : graph.getNodes().values()) {
			if (node instanceof JavaClassNode) {
				ClassNode classNode = ((JavaClassNode) node).getClassNode();
				
				// for every method in a class node...
				for (MethodNode m : ((List<MethodNode>) classNode.methods)) {
					toLink = new HashSet<String>();
					// Iterate over LocalVariableNodes
					//
					// Gets all locally declared variables, like ArrayList<String> arrayList = new ArrayList<String>(4);
					// However, it will miss cases where we don't allocate a variable, like "new HashSet<Integer>().add(5);"
					// Since those cases are mostly useless, it's probably OK for now?
					List<LocalVariableNode> vars = m.localVariables;
					for (LocalVariableNode v : vars) {
						if (v.signature == null)
							toparse = v.desc;
						else
							toparse = v.signature;
						parseSignature(toLink, toparse);
					}
					// asm always picks up the class as a local variable of itself
					// we should remove that since UML doesn't really consider it that way
					toLink.remove(node.getQualifiedName());
				}
				map.put(node.getQualifiedName(), toLink);
			}
		}
		
		// Add new classes, etc 
		// This is just a temporary proof of concept
		System.out.println("Method body analyzer picked up the following classes to link: ");
		for (String s : map.keySet()) {
			for (String t: map.get(s)) {
				System.out.printf("%s >> %s%n", s, t);
			}
		}
	}
	
	public void parseSignature(Set<String> list, String analyze) {
		// ignore array bits
		while (analyze.startsWith("["))
			analyze = analyze.substring(1);
		
		// skip non-objects
		if (!analyze.startsWith("L"))
			return;
		
		analyze = analyze.substring(1); // skip the L
		
		
		int index_brace = analyze.indexOf('<');
		int index_semic = analyze.indexOf(';');
		
		// Handle case where a ; comes before a < 
		// e.g. Lsome/class/here;Ljava/util/ArrayList<Ljava/lang/String;>;
		if (index_semic < index_brace) {
			list.add(analyze.substring(0, index_semic));
			analyze = analyze.substring(index_semic + 1);
		}
		
		
		// if signature has a generic
		if (index_brace > 0) {
			list.add(analyze.substring(0, index_brace).replace('/', '.'));
			// parse generic
			parseSignature(list, analyze.substring(index_brace + 1, analyze.indexOf('>')));
		} else {
			// without generic
			list.add(analyze.substring(0, index_semic).replace('/', '.'));
			analyze = analyze.substring(index_semic + 1);
			if (analyze.length() > 0)
				parseSignature(list, analyze);
		}
	} 
	

}
