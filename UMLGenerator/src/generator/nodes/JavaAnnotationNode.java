package generator.nodes;

import org.objectweb.asm.tree.ClassNode;

import generator.INode;

public class JavaAnnotationNode extends JavaClassNode implements INode {

	public JavaAnnotationNode(ClassNode node) {
		super(node);
	}

}
