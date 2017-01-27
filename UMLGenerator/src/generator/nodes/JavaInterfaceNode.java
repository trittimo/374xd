package generator.nodes;

import org.objectweb.asm.tree.ClassNode;

public class JavaInterfaceNode extends JavaClassNode {

	public JavaInterfaceNode(ClassNode node) {
		super(node);
		stereotype = "interface";
	}

	@Override
	public String getFieldSection() {
		return "";
	}
}
