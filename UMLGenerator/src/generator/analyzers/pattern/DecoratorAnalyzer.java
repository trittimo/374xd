package generator.analyzers.pattern;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import generator.Graph;
import generator.INode;
import generator.Link;
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
	private boolean hasRun = false;
	@Override
	public boolean analyze(Graph graph, CMDParams params, IGraphFactory factory) {
		if (hasRun) {
			return false;
		}
		hasRun = true;
		for (INode inode : graph.getNodes().values()) {
			if (!(inode instanceof JavaClassNode)) {
				continue;
			}
			
			Map<Link, INode> possibleComponents = new HashMap<Link, INode>();
			// Step 1: determine if the node we're on extends or implements another class 
			for (Link link : inode.getLinks()) {
				if (link instanceof ImplementsLink || link instanceof ExtendsLink) {
					if (graph.getNodes().containsKey(link.getEnd())) {
						INode component = graph.getNodes().get(link.getEnd());
						possibleComponents.put(link, component);
					}
				}
			}
			
			if (possibleComponents.isEmpty()) {
				continue;
			}
			
			Class<?> cnode = null;
			try {
				cnode = Class.forName(inode.getQualifiedName());
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			
			// Step 2: ensure that the node has a field of the class it extends/implements
			List<Link> toRemove = new ArrayList<Link>();
			toRemove.addAll(possibleComponents.keySet());
			for (Link extended : possibleComponents.keySet()) {
				for (Field f : cnode.getDeclaredFields()) {
					if (f.getType().getName().equals(extended.getEnd())) {
						toRemove.remove(extended);
					}
				}
			}
			for (Link l : toRemove) {
				possibleComponents.remove(l);
			}
			
			if (possibleComponents.isEmpty()) {
				continue;
			}
			
			toRemove.clear();
			toRemove.addAll(possibleComponents.keySet());
			// Step 3: ensure that the node contains a constructor with the component
			for (Link extended : possibleComponents.keySet()) {
				for (Constructor<?> constructor : cnode.getDeclaredConstructors()) {
					for (Class<?> parameter : constructor.getParameterTypes()) {
						if (parameter.getName().equals(extended.getEnd())) {
							toRemove.remove(extended);
						}
					}
				}
			}
			for (Link l : toRemove) {
				possibleComponents.remove(l);
			}
			
			if (possibleComponents.isEmpty()) {
				continue;
			}
			
			// Confirmed that we're a decorator: could still be a bad decorator though
			System.out.print(inode.getQualifiedName() + " -> ");
			System.out.print("[");
			for (INode node : possibleComponents.values()) {
				System.out.print(node.getQualifiedName() + ",");
			}
			System.out.println("]");
			String color = "chartreuse";
			String description = "decorator";
			Class<?> excnode = null;
			// It's a bad decorator if the class does not override every method in its component
			for (INode extended : possibleComponents.values()) {
				try {
					excnode = Class.forName(extended.getQualifiedName());
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
				
				
				// Get all the methods from the node that are actually declared in the node's class
				List<Method> cmethods = new ArrayList<Method>();
				for (int i = 0; i < cnode.getMethods().length; i++) {
					Method m = cnode.getMethods()[i];
					if (m.getDeclaringClass().getName().equals(cnode.getName())) {
						cmethods.add(m);
					}
				}
				
				// For every method in the extended node
				for (Method m1 : excnode.getMethods()) {
					if (!m1.getReturnType().equals(void.class)) {
						// Check every method in the current node
						boolean contains = false;
						for (Method m2 : cmethods) {
							if (m1.getName().equals(m2.getName())) {
								for (int i = 0; i < m1.getParameterTypes().length; i++) {
									if (m2.getParameterTypes().length <= i) {
										contains = false;
										continue;
									}
									Class<?> c1 = m1.getParameterTypes()[i];
									Class<?> c2 = m2.getParameterTypes()[i];
									contains &= c1.getName().equals(c2.getName());
								}
							}
							if (!contains) {
								System.out.println(m1.getName());
							}
						}
					}
				}
			}
			
			
			
		}
		
		return false;
	}

//	private Map<INode, List<INode>> getSuperClasses(Graph graph) {
//		Map<INode, List<INode>> supers = new HashMap<INode, List<INode>>();
//		Collection<INode> nodes = graph.getNodes().values();
//		for (INode node : nodes) {
//			
//		}
//	}
		
		
//		
//		Stack<INode> decorators = new Stack<INode>();
//		List<String> components = new ArrayList<String>();
//		
//		for (INode inode : graph.getNodes().values()) {
//			if (!(inode instanceof JavaClassNode)) {
//				continue;
//			}
//			
//			List<Link> links = inode.getLinks();
//			
//			// If the maybdecorator implements/extends something
//			List<String> subclassed = new ArrayList<String>();
//			for (Link link : links) {
//				if (link instanceof ExtendsLink || link instanceof ImplementsLink) {
//					String end = link.getEnd();
//					subclassed.add(end);
//				}
//			}
//			
//			JavaClassNode node = (JavaClassNode) inode;
//			ClassNode classNode = node.getClassNode();
//			List<String> fieldTypes = new ArrayList<String>();
//			
//			// If the maybedecorator contained fields it implements/extends
//			if (subclassed.size() > 0) {
//				
//				
//				String type;
//				List<FieldNode> fields = (List<FieldNode>) classNode.fields;
//				for(FieldNode field : fields) {
//					type = Type.getType(field.desc).getClassName();
//					if (subclassed.contains(type)) {
//						fieldTypes.add(type);
//					}
//				}
//			}
//			
//			// If the maybedecorator has a constructor that takes one of the fieldTypes as a parameter
//			if (fieldTypes.size() > 0) {
//				try {
//					Class<?> clazz = Class.forName(classNode.name.replaceAll("/", "."));
//					for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
//						for (Class<?> param : constructor.getParameterTypes()) {
//							String name = param.getName();
//							if (subclassed.contains(name)) {
//								// It's a decorator
//								components.add(name);
//								decorators.add(inode);
//							}
//						}
//					}
//				} catch (ClassNotFoundException e) {
//					// Handle?
//					e.printStackTrace();
//				}
//			}
//		}
//		
//		
//		
//		while (!decorators.isEmpty()) {
//			INode decorator = decorators.pop();
//			String color = "chartreuse";
//			String name = "decorator";
//			if (isBadDecorator(decorator)) {
//				color = "maroon1";
//				name = "BAD DECORATOR";
//			}
//			decorator.setAttribute(new StyleAttribute("fillcolor",color,10));
//			decorator.setAttribute(new StyleAttribute("style","filled",10));
//			JavaClassNode classNode = (JavaClassNode) decorator;
//			classNode.addStereotype(name);
//			
//			
//			
//			for (Link link : decorator.getLinks()) {
//				if (link instanceof ExtendsLink) {
//					link.setAttribute(new StyleAttribute("label", "<<decorates>>", 15));
//				} else if (link instanceof ImplementsLink) {
//					if (components.contains(link.getEnd())) {
//						link.setAttribute(new StyleAttribute("label", "<<decorates>>", 15));
//					}
//				}
//			}
//			
//			for (INode inode : graph.getNodes().values()) {
//				for (Link link : inode.getLinks()) {
//					if (link instanceof ExtendsLink) {
//						if (link.getEnd().equals(decorator.getQualifiedName())) {
//							decorators.add(inode);
//							link.setAttribute(new StyleAttribute("label", "<<decorates>>", 15));
//							break;
//						}
//					}
//				}
//			}
//		}
//		
//		for (String component : components) {
//			for (INode inode : graph.getNodes().values()) {
//				String name = inode.getQualifiedName();
//				if (name.equals(component)) {
//					JavaClassNode classNode = (JavaClassNode) inode;
//					classNode.addStereotype("component");
//					inode.setAttribute(new StyleAttribute("fillcolor", "chartreuse", 10));
//					inode.setAttribute(new StyleAttribute("style", "filled", 10));
//				}
//			}
//		}
//		return false;
//	}
//
//	private boolean isBadDecorator(INode d) {
//		Class<?> decorator = null;
//		try {
//			decorator = Class.forName(d.getQualifiedName());
//		} catch (ClassNotFoundException e) {
//			e.printStackTrace();
//		}
//		for (Method m : decorator.getMethods()) {
//			
//		}
//		
//		return false;
//	}

}
