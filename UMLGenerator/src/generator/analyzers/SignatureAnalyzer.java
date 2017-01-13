package generator.analyzers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import generator.Graph;
import generator.ILink;
import generator.INode;
import generator.commands.CMDParams;
import generator.factories.IGraphFactory;
import generator.links.DependencyLink;
import generator.links.OneToOneLink;
import generator.nodes.JavaClassNode;

public class SignatureAnalyzer implements IAnalyzer {

	@Override
	public void analyze(Graph graph, CMDParams params, IGraphFactory factory) {
		HashMap<String, Set<String>> fields = new HashMap<String, Set<String>>();
		
		for (INode node : graph.getNodes().values()) {
			if (node instanceof JavaClassNode) {
				ClassNode classNode = ((JavaClassNode) node).getClassNode();
				HashSet<String> currentList = new HashSet<String>();
				
				
				if (classNode.methods != null) {
					//System.out.println("Parsing methods...");
					for (MethodNode method : (List<MethodNode>) classNode.methods) {
						List<String> clazzes = parseSignature(method.desc);
						for (String s : clazzes) {
							String className = s.replaceAll("/", ".");
							if (graph.getNodes().containsKey(className)) {
								currentList.add(className);
							}
						}
					}
				}
				fields.put(classNode.name.replaceAll("/", "."), currentList);
			}
		}
		
		for (String name : fields.keySet()) {
			INode node = graph.getNodes().get(name);
			for (String field : fields.get(name)) {
				boolean add = true;
				INode other = graph.getNodes().get(field);
				for (ILink link : node.getLinks()) {
					if (link instanceof OneToOneLink) {
						OneToOneLink theLink = (OneToOneLink) link;
						if (theLink.getEnd().equals(other.getQualifiedName().replaceAll("/", "."))) {
							add = false;
						}
					}
				}
				if (add) {
					node.addLink(new DependencyLink(node, other));
				}
			}
		}
	}
	
	
	private List<String> parseSignature(String sig) {
		if (sig.contains("ITest")) {
			System.out.println(sig);
		}
		List<String> list = new ArrayList<String>();
		String[] res = sig.split(";");
		if (res.length == 0) {
			return list;
		}
		
		for (int i = 0; i < res.length; i++) {
			String current = res[i];
			if (current.startsWith("(")) {
				current = res[i].substring(1,res[i].length());
			}
			
			if (current.startsWith(")")) {
				current = current.substring(1);
			}
			
			if (current.startsWith("L")) {
				list.add(current.substring(1));
			}
		}
		return list;
	}
	
}
