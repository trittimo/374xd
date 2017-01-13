package generator.analyzers;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

import generator.Graph;
import generator.INode;
import generator.commands.CMDParams;
import generator.factories.IGraphFactory;
import generator.nodes.JavaClassNode;

public class FieldAnalyzer implements IAnalyzer {

	@SuppressWarnings("unchecked")
	@Override
	public void analyze(Graph graph, CMDParams params, IGraphFactory factory) {
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
							String classname = fn.desc.substring(1).replaceAll("/", ".");
							if (!graph.getNodes().containsKey(classname))
								continue;
							currentList.add(classname);
						default:
							//primitives will be ignored
					}
				}
				fields.put(classNode.name.replaceAll("/", "."), currentList);
			}
		}
	}
	

}
