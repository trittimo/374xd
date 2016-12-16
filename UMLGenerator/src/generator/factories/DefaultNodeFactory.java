package generator.factories;

import java.io.IOException;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

import generator.Graph;
import generator.INode;
import generator.nodes.JavaClassNode;

public class DefaultNodeFactory implements INodeFactory {
	
	@Override
	public void addNodeToGraph(Graph g, String qualifiedName) throws IOException {
		ClassReader reader = new ClassReader(qualifiedName);
		ClassNode classNode = new ClassNode();
		reader.accept(classNode, ClassReader.EXPAND_FRAMES);
		
		JavaClassNode node = new JavaClassNode(classNode);
		g.addNode(node);
	}
	
}
