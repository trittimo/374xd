package generator.nodes;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.ParameterNode;

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
		String label = "";
		// Name
				
		// Fields
		
		// Methods
		
		return label;
	}

	protected String getLabelName() {
		return classNode.name;
	}
	
	protected String getFieldSection() {
		String section = "";
		@SuppressWarnings("unchecked")
		List<FieldNode> fields = (List<FieldNode>) classNode.fields;
		for(FieldNode field : fields) {
			//newline char, accessiblity , field name, field type
			
			section += String.format("%s%s %s: %s", 
					(section.equals(""))?"|":"\\l",
					getAccessibility(field.access),
					field.name,
					Type.getType(field.desc));
		}
	}
	
	protected String getAccessibility(int access) {
		if ((access & Opcodes.ACC_PUBLIC) > 0) {
			return "+";
		}
		else if ((access & Opcodes.ACC_PRIVATE) > 0) {
			return "-";
		}
		return "#";
	}

	protected String getMethodSection() {
		String section = "";
		List<MethodNode> methods = (List<MethodNode>) classNode.methods;
		for(MethodNode method : methods) {
			//newline char, accessiblity , field name, field type
			section += String.format("%s%s %s: %s", 
					(section.equals(""))?"|":"\\l",
					getAccessibility(method.access),
					method.name,
					getMethodArguments(method),
					Type.getReturnType(method.desc));
		}
	}
	

	private Object getMethodArguments(MethodNode m) {
		List<ParameterNode> params = m.parameters;
		Type[] types = Type.getArgumentTypes(m.desc);
		String stringify = "";
		for (int i = 0; i < params.size(); i++) {
			stringify += (i == 0)?"":", ";
			stringify += String.format("%s: %s", params.get(i).name, types[i].getClassName());
		}
		return stringify;
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
