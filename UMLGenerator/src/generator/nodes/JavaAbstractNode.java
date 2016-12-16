package generator.nodes;

import org.objectweb.asm.tree.ClassNode;

import generator.INode;

public class JavaAbstractNode extends JavaClassNode implements INode {

	public JavaAbstractNode(ClassNode node) {
		super(node);
	}

}
