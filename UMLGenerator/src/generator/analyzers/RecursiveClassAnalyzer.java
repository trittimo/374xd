package generator.analyzers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

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
				

				//System.out.println("methods...");
				if (classNode.methods != null) {
					//System.out.println("Parsing methods...");
					for (MethodNode method : (List<MethodNode>) classNode.methods) {
						List<String> clazzes = AnalyzerUtils.parseClassesFromMethod(method);
						for (String s : clazzes) {
							if (!graph.getNodes().containsKey(s.replaceAll("/", "."))) {
								toAdd.add(s);
							}
						}
					}
				}
			}
		}
		
		for (String name : toAdd) {
			try {
				//System.out.println("Adding:" + name);
				factory.addNodeToGraph(graph, name);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		if (!toAdd.isEmpty()) {
//			try {
//				Thread.sleep(1000);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			
			analyze(graph, params, factory);
			
		}
	}
}
