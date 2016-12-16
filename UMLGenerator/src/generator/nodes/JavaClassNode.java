package generator.nodes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
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

	private List<ILink> links;
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
		
		label = label.replaceAll("#", Matcher.quoteReplacement("\\#"));
		label = label.replaceAll("\\$", Matcher.quoteReplacement("\\$"));
		//label = REPL_DOLLARS.matcher(label).replaceAll(Matcher.quoteReplacement("\\$"));
		
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

	protected String getMethodSection() {
		String section = "|";
		List<MethodNode> methods = (List<MethodNode>) classNode.methods;
		for(MethodNode method : methods) {
			if (method.name.startsWith("<")) {
				continue;
			}
			//newline char, accessiblity , field name, field type
			String methodArgs = getMethodArguments(method);
			String retType = Type.getReturnType(method.desc).getClassName();
			
			section += String.format("%s %s(%s): %s\\l", 
					getAccessibility(method.access),
					method.name,
					methodArgs,
					retType);
		}
		return section;
	}
	

	private String getMethodArguments(MethodNode m) {
		List<ParameterNode> params = m.parameters;
		Type[] types = Type.getArgumentTypes(m.desc);
		String stringify = "";
		String name, type;
		for (int i = 0; i < types.length; i++) {
			type = types[i].getClassName();
			name = (params != null)?params.get(i).name:("arg"+i);
			stringify += (i == 0)?"":", ";
			stringify += String.format("%s: %s", name, type);
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

	@SuppressWarnings("unchecked")
	@Override
	public void createLinks(Graph g) {		
		// inherits
		if (classNode.superName != null)
			this.addLink(new ExtendsLink(this.getQualifiedName(), classNode.superName.replaceAll("/", "_")));
		// impl
		if (classNode.interfaces != null) {
			for (String name : ((List<String>) classNode.interfaces))
				this.addLink(new ImplementsLink(this.getQualifiedName(), name.replaceAll("/", "_")));
		}
	}

}
