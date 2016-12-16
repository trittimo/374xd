package generator.factories;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;

import generator.Graph;
import generator.INode;
import generator.nodes.JavaAbstractNode;
import generator.nodes.JavaAnnotationNode;
import generator.nodes.JavaClassNode;
import generator.nodes.JavaInterfaceNode;

public class DefaultGraphFactory implements IGraphFactory {
	
	@Override
	public void addNodeToGraph(Graph g, String qualifiedName) throws IOException {
		ClassReader reader = new ClassReader(qualifiedName);
		ClassNode classNode = new ClassNode();
		reader.accept(classNode, ClassReader.EXPAND_FRAMES);
		
		if ((classNode.access & Opcodes.ACC_INTERFACE) > 0) {
			g.addNode(new JavaInterfaceNode(classNode));
		} else if ((classNode.access & Opcodes.ACC_ABSTRACT) > 0) {
			g.addNode(new JavaAbstractNode(classNode));
		} else if ((classNode.access & Opcodes.ACC_ANNOTATION) > 0) {
			g.addNode(new JavaAnnotationNode(classNode));
		} else {
			g.addNode(new JavaClassNode(classNode));
		}
	}

	@Override
	public void linkGraph(Graph g) {
		Collection<INode> nodes = g.getNodes().values();
		for (INode n : nodes)
			n.createLinks(g);
	}
	
}
