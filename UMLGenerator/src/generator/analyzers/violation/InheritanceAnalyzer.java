package generator.analyzers.violation;

import java.io.IOException;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;

import generator.Graph;
import generator.Link;
import generator.INode;
import generator.StyleAttribute;
import generator.analyzers.IAnalyzer;
import generator.commands.CMDParams;
import generator.commands.ExternalClassLoader;
import generator.factories.IGraphFactory;

public class InheritanceAnalyzer implements IAnalyzer {

	private static final boolean PRINT_WARNINGS_TO_CONSOLE = false;
	
	@Override
	public boolean analyze(Graph graph, CMDParams params, IGraphFactory factory) {
		ClassReader reader = null;
		for (String name : graph.getNodes().keySet()) {
			INode node = graph.getNodes().get(name);
			try {
				reader = ExternalClassLoader.getClassReader(node.getQualifiedName());
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			ClassNode classNode = new ClassNode();
			reader.accept(classNode, ClassReader.EXPAND_FRAMES);
			
			if (classNode.superName == null)
				continue;
			
			ClassNode superNode = new ClassNode();
			
			try {
				reader = ExternalClassLoader.getClassReader(classNode.superName);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			reader.accept(superNode, ClassReader.EXPAND_FRAMES);
			
			// Ensure it is a regular class (i.e. not abstract or an interface or an annotation)
			if ((classNode.access & (Opcodes.ACC_INTERFACE | Opcodes.ACC_ANNOTATION | Opcodes.ACC_ABSTRACT)) > 0) {
				continue;
			}
			
			// Ensure super is a regular class (i.e. not abstract or an interface or an annotation)
			if ((superNode.access & (Opcodes.ACC_INTERFACE | Opcodes.ACC_ANNOTATION | Opcodes.ACC_ABSTRACT)) > 0) {
				continue;
			}
			
			// If there is a superclass that is not Object
			if (!superNode.name.equals("java/lang/Object")) {
				String childname = classNode.name.replace("/", ".");
				String supername = classNode.superName.replace("/", ".");
				if (PRINT_WARNINGS_TO_CONSOLE) {
					System.out.printf("InheritanceAnalyzer Warning: Bad inheritance @ %s >> %s %n", childname, supername);
				}
				//System.out.print("Nodes = " + graph.getNodes().keySet());
				INode child = graph.getNodes().get(childname);
				StyleAttribute color = new StyleAttribute("color","orange",10);
				child.setAttribute(color);
//				uncomment to also mark the parent 
//				INode parent = graph.getNodes().get(supername);
//				if (parent != null)
//					parent.setAttribute(color);
				// find link
				for (Link link : child.getLinks()) {
					if (link.getEnd().equals(supername)) {
						if (link instanceof generator.links.ExtendsLink)
							link.setAttribute(color);
					}
				}
			}
		}
		return false;
	}

}
