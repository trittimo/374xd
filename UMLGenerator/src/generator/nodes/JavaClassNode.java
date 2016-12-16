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
		label += getLabelName();		
		// Fields
		label += getFieldSection();
		// Methods
		label += getMethodSection();
		
		//label = label.replaceAll("\\#", "\\#");
		//label = label.replaceAll("$", "\\$");
		
		return label;
	}

	protected String getLabelName() {
		return classNode.name.substring(classNode.name.lastIndexOf('/') + 1);
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
		return section;
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
			if (method.name.equals("<init>")) {
				continue;
			}
			//newline char, accessiblity , field name, field type
			String methodArgs = getMethodArguments(method);
			String retType = Type.getReturnType(method.desc).toString();
			
			section += String.format("%s%s %s(%s): %s", 
					(section.equals(""))?"|":"\\l",
					getAccessibility(method.access),
					method.name,
					methodArgs,
					retType.equals("V") ? "void" : retType);
		}
		return section;
	}
	

	private String getMethodArguments(MethodNode m) {
		List<ParameterNode> params = m.parameters;
		if (params == null) {
			return "";
		}
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
		this.links.add(link);
	}

	@Override
	public List<ILink> getLinks() {
		return this.links;
	}

}
