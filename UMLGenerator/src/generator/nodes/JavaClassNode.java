package generator.nodes;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

import generator.ILink;
import generator.INode;

public class JavaClassNode implements INode {

	private List<ILink> links;
	private ClassNode classNode;
	
	public JavaClassNode(ClassNode node) {
		this.classNode = node;
		this.links = new ArrayList<ILink>();
	}
	
	@Override
	public String getQualifiedName() {
		return Type.getObjectType(classNode.name).getClassName();
	}

	@Override
	public String getLabel() {
		// just label
		String text = "";
		
		return null;
	}

	@Override
	public void addLink(ILink link) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<ILink> getLinks() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getStylingText() {
		// TODO Auto-generated method stub
		return null;
	}

}
