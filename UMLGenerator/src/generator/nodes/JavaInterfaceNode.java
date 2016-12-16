package generator.nodes;

import org.objectweb.asm.tree.ClassNode;

public class JavaInterfaceNode extends JavaClassNode {

	public JavaInterfaceNode(ClassNode node) {
		super(node);
	}

	@Override
	public String getFieldSection() {
		return "";
	}
	
	@Override
	protected String getLabelName() {
		return "\\<\\<interface\\>\\>\\n" + classNode.name.substring(classNode.name.lastIndexOf('/') + 1);
	}
}
