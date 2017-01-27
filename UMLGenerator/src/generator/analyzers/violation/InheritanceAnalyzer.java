package generator.analyzers.violation;

import java.io.IOException;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;

import generator.Graph;
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
				graph.getNodes().get(classNode.name.replace("/", ".")).setAttribute(new StyleAttribute("color","red",10));
			}
		}
		return false;
	}

}
