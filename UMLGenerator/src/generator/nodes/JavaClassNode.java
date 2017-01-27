package generator.nodes;

import java.util.ArrayList;
import java.util.List;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.ParameterNode;

import generator.Graph;
import generator.ILink;
import generator.INode;
import generator.links.ExtendsLink;
import generator.links.ImplementsLink;

public class JavaClassNode implements INode {

	private ArrayList<ILink> links;
	protected ClassNode classNode;
	
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
		String label = "{";
		// Name
		label += getLabelName();		
		// Fields
		label += getFieldSection();
		// Methods
		label += getMethodSection();
		
		return label + "}";
	}

	protected String getLabelName() {
		return classNode.name.substring(classNode.name.lastIndexOf('/') + 1);
	}
	
	protected String getFieldSection() {
		String section = "|", type;
		@SuppressWarnings("unchecked")
		List<FieldNode> fields = (List<FieldNode>) classNode.fields;
		for(FieldNode field : fields) {
			//newline char, accessiblity , field name, field type
			
			// Can't seem to get parametized types working
			type = Type.getType(field.desc).getClassName();
			if (type.lastIndexOf(".") > 0)
				type = type.substring(type.lastIndexOf(".") + 1);
			
			section += String.format("%s %s: %s\\l",
					getAccessibility(field.access),
					field.name,
					type);
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

	public String getMethodSection() {
		@SuppressWarnings("unchecked")
		List<MethodNode> methods = (List<MethodNode>) classNode.methods;
		String section = "|";
		for(MethodNode method : methods) {
			if (method.name.startsWith("<")) {
				continue;
			}
			//newline char, accessiblity , field name, field type
			String methodArgs = getMethodArguments(method);
			String retType = Type.getReturnType(method.desc).getClassName();
			if (retType.lastIndexOf(".") > 0)
				retType = retType.substring(retType.lastIndexOf(".") + 1);
			
			section += String.format("%s %s(%s): %s\\l", 
					getAccessibility(method.access),
					method.name,
					methodArgs,
					retType);
		}
		return section;
	}

	private String getMethodArguments(MethodNode m) {
		@SuppressWarnings("unchecked")
		List<ParameterNode> params = m.parameters;
		Type[] types = Type.getArgumentTypes(m.desc);
		String stringify = "";
		String name, type;
		for (int i = 0; i < types.length; i++) {
			type = types[i].getClassName();
			if (type.lastIndexOf(".") > 0)
				type = type.substring(type.lastIndexOf(".") + 1);
			name = (params != null)?params.get(i).name:("arg"+i);
			stringify += (i == 0)?"":", ";
			stringify += String.format("%s: %s", name, type);
		}
		return stringify;
	}

	@Override
	public void addLink(ILink link) {
		if (!this.links.contains(link))
			this.links.add(link); // handles duplicates since it's a set 
	}

	@Override
	public List<ILink> getLinks() {
		return this.links;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void createLinks(Graph g) {
		// inherits
		if (classNode.superName != null) {
			if (!classNode.superName.equals("java/lang/Object")) {
				this.addLink(new ExtendsLink(this.getQualifiedName(), classNode.superName.replaceAll("/", ".")));
			}
		}
		// impl
		if (classNode.interfaces != null) {
			for (String name : ((List<String>) classNode.interfaces))
				this.addLink(new ImplementsLink(this.getQualifiedName(), name.replaceAll("/", ".")));
		}
	}

	public ClassNode getClassNode() {
		return this.classNode;
	}

	@Override
	public void removeLink(ILink link) {
		this.links.remove(link);
	}

}
