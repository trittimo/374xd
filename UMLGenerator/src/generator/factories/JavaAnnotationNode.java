package generator.factories;

import org.objectweb.asm.tree.ClassNode;

import generator.INode;
import generator.nodes.JavaClassNode;

public class JavaAnnotationNode extends JavaClassNode implements INode {

	public JavaAnnotationNode(ClassNode node) {
		super(node);
	}

}
