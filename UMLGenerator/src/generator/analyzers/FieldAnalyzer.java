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
import generator.nodes.JavaClassNode;

public class FieldAnalyzer implements IAnalyzer {

	@SuppressWarnings("unchecked")
	@Override
	public void analyze(Graph graph, CMDParams params, IGraphFactory factory) {
//		for (String node : graph.getNodes().keySet()) {
//			System.out.println(node);
//		}
		
		HashMap<String, Set<String>> fields = new HashMap<String, Set<String>>();
		
		for (INode node : graph.getNodes().values()) {
			if (node instanceof JavaClassNode) {
				ClassNode classNode = ((JavaClassNode) node).getClassNode();
				HashSet<String> currentList = new HashSet<String>();
				for (FieldNode fn : (List<FieldNode>) classNode.fields) {
					switch (fn.desc.charAt(0)) {
						case '[': // array
							// one to many
							break;
						case 'L': // a class
							String className = fn.desc.substring(1).replaceAll("/", ".").substring(0, fn.desc.length()-2);
							if (!graph.getNodes().containsKey(className)) {
								//System.out.println("Skipping " + className);
								continue;
							}
							currentList.add(className);
							//System.out.println("Adding " + className);
						default:
							//primitives will be ignored
					}
				}
				
				fields.put(classNode.name.replaceAll("/", "."), currentList);
			}
		}
		
		for (String name : fields.keySet()) {
			INode node = graph.getNodes().get(name);
			for (String field : fields.get(name)) {
				node.addLink(new AssociationLink(node, graph.getNodes().get(field)));
			}
		}
	}
	

}
