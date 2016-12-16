package generator.factories;

import org.objectweb.asm.tree.ClassNode;

import generator.INode;
import generator.nodes.JavaClassNode;

public class JavaAbstractNode extends JavaClassNode implements INode {

	public JavaAbstractNode(ClassNode node) {
		super(node);
	}

}
