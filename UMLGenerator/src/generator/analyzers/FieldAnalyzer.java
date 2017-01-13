package generator.analyzers;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import generator.Graph;
import generator.INode;
import generator.commands.CMDParams;
import generator.factories.IGraphFactory;
import generator.links.AssociationLink;
import generator.links.AssociationManyLink;
import generator.nodes.JavaClassNode;

public class FieldAnalyzer implements IAnalyzer {

	@SuppressWarnings("unchecked")
	@Override
	public void analyze(Graph graph, CMDParams params, IGraphFactory factory) {
//		for (String node : graph.getNodes().keySet()) {
//			System.out.println(node);
//		}

		HashMap<String, Set<String>> fields = new HashMap<String, Set<String>>();
		HashMap<String, Set<String>> multi_fields = new HashMap<String, Set<String>>();
		
		String className;
		
		for (INode node : graph.getNodes().values()) {
			if (node instanceof JavaClassNode) {
				ClassNode classNode = ((JavaClassNode) node).getClassNode();
				HashSet<String> currentList = new HashSet<String>();
				HashSet<String> currentMultiList = new HashSet<String>();
				for (FieldNode fn : (List<FieldNode>) classNode.fields) {
					switch (fn.desc.charAt(0)) {
						case '[': // array
							className = fn.desc.substring(fn.desc.lastIndexOf("["));
							if (!graph.getNodes().containsKey(className)) {
								continue;
							} 
							// one to many
							currentMultiList.add(className);
							break;
						case 'L': // a class
							className = fn.desc.substring(1).replaceAll("/", ".").substring(0, fn.desc.length()-2);
							if (!graph.getNodes().containsKey(className)) {
								continue;
							}
							currentList.add(className);
						default:
							//primitives will be ignored
					}
				}
				
				fields.put(classNode.name.replaceAll("/", "."), currentList);
			}
		}
		

		for (String name : multi_fields.keySet()) {
			INode node = graph.getNodes().get(name);
			for (String field : multi_fields.get(name)) {
				node.addLink(new AssociationManyLink(node, graph.getNodes().get(field)));
			}
		}
		
		for (String name : fields.keySet()) {
			INode node = graph.getNodes().get(name);
			for (String field : fields.get(name)) {
				if (multi_fields.get(name).contains(field)) // case: already added as multi
					continue;
				node.addLink(new AssociationLink(node, graph.getNodes().get(field)));
			}
		}
	}
	

}
