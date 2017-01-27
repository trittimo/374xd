package generator.analyzers.violation;

import java.io.IOException;
import java.util.List;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import generator.Graph;
import generator.INode;
import generator.StyleAttribute;
import generator.analyzers.IAnalyzer;
import generator.commands.CMDParams;
import generator.factories.IGraphFactory;
import generator.nodes.JavaClassNode;

public class SingletonAnalyzer implements IAnalyzer {

	@SuppressWarnings("unchecked")
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
			
			for (MethodNode method : (List<MethodNode>) classNode.methods) {
				boolean isSingleton = (method.access & Opcodes.ACC_STATIC) > 0;
				String retType = Type.getReturnType(method.desc).toString();
				isSingleton &= retType.startsWith("L") &&
						retType.substring(1, retType.indexOf(';')).equals(classNode.name);
				if (isSingleton) {
					JavaClassNode javanode = (JavaClassNode) node;
					javanode.addStereotype("Singleton");
					javanode.setAttribute(new StyleAttribute("color","blue",20));
					
					break;
				}
			}
		}
		return false;
	}
	
}
