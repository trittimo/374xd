package generator.analyzers.pattern;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

import generator.Graph;
import generator.INode;
import generator.Link;
import generator.StyleAttribute;
import generator.analyzers.IAnalyzer;
import generator.commands.CMDParams;
import generator.factories.IGraphFactory;
import generator.links.ExtendsLink;
import generator.links.ImplementsLink;
import generator.nodes.JavaClassNode;

/**
 * 
 * Detects decorators which:<br>
 * 1. Subclass the original Component class into a Decorator class<br>
 * 2. In the Decorator class, add a Component pointer as a field<br>
 * 3. In the Decorator class, pass a Component to the Decorator constructor to initialize the Component pointer<br>
 * 4. In the Decorator class, forward all Component methods to the Component pointer<br>
 * 5. In the ConcreteDecorator class, override any Component method(s) whose behavior needs to be modified.<br>
 *
 */

public class DecoratorAnalyzer implements IAnalyzer {

	@Override
	public boolean analyze(Graph graph, CMDParams params, IGraphFactory factory) {		
		Stack<INode> decorators = new Stack<INode>();
		List<String> components = new ArrayList<String>();
		
		for (INode inode : graph.getNodes().values()) {
			if (!(inode instanceof JavaClassNode)) {
				continue;
			}
			
			List<Link> links = inode.getLinks();
			
			// If the maybdecorator implements/extends something
			List<String> subclassed = new ArrayList<String>();
			for (Link link : links) {
				if (link instanceof ExtendsLink || link instanceof ImplementsLink) {
					String end = link.getEnd();
					if (end.lastIndexOf('.') > -1) {
						end = end.substring(end.lastIndexOf('.')+1);
					}
					subclassed.add(end);
				}
			}
			
			JavaClassNode node = (JavaClassNode) inode;
			ClassNode classNode = node.getClassNode();
			List<String> fieldTypes = new ArrayList<String>();
			
			// If the maybedecorator contained fields it implements/extends
			if (subclassed.size() > 0) {
				
				
				String type;
				List<FieldNode> fields = (List<FieldNode>) classNode.fields;
				for(FieldNode field : fields) {
					type = Type.getType(field.desc).getClassName();
					if (type.lastIndexOf(".") > -1) {
						type = type.substring(type.lastIndexOf(".") + 1);
					}
					
					if (subclassed.contains(type)) {
						fieldTypes.add(type);
					}
				}
			}
			
			// If the maybedecorator has a constructor that takes one of the fieldTypes as a parameter
			if (fieldTypes.size() > 0) {
				try {
					Class<?> clazz = Class.forName(classNode.name.replaceAll("/", "."));
					for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
						for (Class<?> param : constructor.getParameterTypes()) {
							String name = param.getName();
							if (name.lastIndexOf('.') > -1) {
								name = name.substring(name.lastIndexOf('.')+1);
							}
							if (subclassed.contains(name)) {
								// It's a decorator
								components.add(name);
								decorators.add(inode);
							}
						}
					}
				} catch (ClassNotFoundException e) {
					// Handle?
					e.printStackTrace();
				}
			}
		}
		
		
		
		while (!decorators.isEmpty()) {
			INode decorator = decorators.pop();
			decorator.setAttribute(new StyleAttribute("fillcolor","chartreuse",10));
			decorator.setAttribute(new StyleAttribute("style","filled",10));
			JavaClassNode classNode = (JavaClassNode) decorator;
			classNode.addStereotype("decorator");
			for (Link link : decorator.getLinks()) {
				if (link instanceof ExtendsLink) {
					link.setAttribute(new StyleAttribute("label", "<<decorates>>", 15));
				} else if (link instanceof ImplementsLink) {
					if (components.contains(link.getEnd())) {
						link.setAttribute(new StyleAttribute("label", "<<decorates>>", 15));
					}
				}
			}
			
			for (INode inode : graph.getNodes().values()) {
				for (Link link : inode.getLinks()) {
					if (link instanceof ExtendsLink) {
						if (link.getEnd().equals(decorator.getQualifiedName())) {
							decorators.add(inode);
							link.setAttribute(new StyleAttribute("label", "<<decorates>>", 15));
							break;
						}
					}
				}
			}
		}
		
		for (String component : components) {
			for (INode inode : graph.getNodes().values()) {
				String name = inode.getQualifiedName();
				if (name.lastIndexOf('.') > 0) {
					name = name.substring(name.lastIndexOf('.')+1);
				}
				if (name.equals(component)) {
					JavaClassNode classNode = (JavaClassNode) inode;
					classNode.addStereotype("component");
					inode.setAttribute(new StyleAttribute("fillcolor", "chartreuse", 10));
					inode.setAttribute(new StyleAttribute("style", "filled", 10));
				}
			}
		}
		return false;
	}

}
