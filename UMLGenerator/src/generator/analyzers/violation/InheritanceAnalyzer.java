package generator.analyzers.violation;

import java.io.IOException;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;

import generator.Graph;
import generator.ILink;
import generator.INode;
import generator.StyleAttribute;
import generator.analyzers.IAnalyzer;
import generator.commands.CMDParams;
import generator.factories.IGraphFactory;

public class InheritanceAnalyzer implements IAnalyzer {

	@Override
	public boolean analyze(Graph graph, CMDParams params, IGraphFactory factory) {
		ClassReader reader = null;
		for (String name : graph.getNodes().keySet()) {
			INode node = graph.getNodes().get(name);
			try {
				reader = new ClassReader(node.getQualifiedName());
			} catch (IOException e) {
				e.printStackTrace();
			}
			ClassNode classNode = new ClassNode();
			reader.accept(classNode, ClassReader.EXPAND_FRAMES);
			
			// Ensure it is a regular class (i.e. not abstract or an interface or an annotation)
			if ((classNode.access & (Opcodes.ACC_INTERFACE | Opcodes.ACC_ANNOTATION | Opcodes.ACC_ABSTRACT)) > 0) {
				continue;
			}
			
			// If there is a superclass that is not Object
			if (classNode.superName != null && !classNode.superName.equals("java/lang/Object")) {
				String childname = classNode.name.replace("/", ".");
				String supername = classNode.superName.replace("/", ".");
				System.out.printf("InheritanceAnalyzer Warning: Bad inheritance @ %s >> %s %n", childname, supername);
				//System.out.print("Nodes = " + graph.getNodes().keySet());
				INode child = graph.getNodes().get(childname);
				INode parent = graph.getNodes().get(supername);
				StyleAttribute red = new StyleAttribute("color","red",10);
				child.setAttribute(red);
				if (parent != null)
					parent.setAttribute(red);
				// find link
				for (ILink link : child.getLinks()) {
					if (link.getEnd() == supername) {
						if (link instanceof generator.links.ExtendsLink)
							link.setAttribute(red);
					}
				}
			}
		}
		return false;
	}

}
