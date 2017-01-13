package generator.analyzers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

import generator.Graph;
import generator.INode;
import generator.commands.CMDParams;
import generator.factories.IGraphFactory;
import generator.links.ImplementsLink;
import generator.nodes.JavaClassNode;

public class RecursiveClassAnalyzer implements IAnalyzer {

	@SuppressWarnings("unchecked")
	@Override
	public void analyze(Graph graph, CMDParams params, IGraphFactory factory) {
		HashSet<String> toAdd = new HashSet<String>();
		for (INode node : graph.getNodes().values()) {
			if (node instanceof JavaClassNode) {
				ClassNode classNode = ((JavaClassNode) node).getClassNode();
				if (classNode.superName != null) {
					String superName = classNode.superName.replaceAll("/", ".");
					if (!graph.getNodes().containsKey(superName)) {
						toAdd.add(superName);
					}
				}
				
				
				if (classNode.interfaces != null) {
					for (String name : ((List<String>) classNode.interfaces)) {
						String intName = name.replaceAll("/", ".");
						if (!graph.getNodes().containsKey(intName)) {
							toAdd.add(intName);
						}
					}
				}
			}
		}
		
		for (String name : toAdd) {
			try {
				factory.addNodeToGraph(graph, name);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		if (!toAdd.isEmpty()) {
			analyze(graph, params, factory);
			
		}
	}

}
