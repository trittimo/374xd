package generator.analyzers.pattern;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

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
	private boolean hasRun = false;
	private static final int RUN_LIMIT = 500;
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
				cnode = Thread.currentThread().getContextClassLoader().loadClass(inode.getQualifiedName());
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
			String color = "lawngreen";
			String description = "decorator";
			Class<?> excnode = null;
			
			// It's a bad decorator if the class does not override every method in its component
			for (INode extended : possibleComponents.values()) {
				try {
					excnode = Thread.currentThread().getContextClassLoader().loadClass(extended.getQualifiedName());
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
				
				
				// Get all the methods from the node that are actually declared in the node's class
				// and are not void
				Set<String> cmethods = new HashSet<String>();
				for (int i = 0; i < cnode.getMethods().length; i++) {
					Method m = cnode.getMethods()[i];
					if (m.getDeclaringClass().getName().equals(cnode.getName())) {
						if (!m.getReturnType().equals(void.class)) {
							cmethods.add(m.getName());
						}
					}
				}
				
				// Get all the methods from the node that are actually declared in the node's class
				// and are not void
				Set<String> excmethods = new HashSet<String>();
				for (int i = 0; i < excnode.getDeclaredMethods().length; i++) {
					Method m = excnode.getMethods()[i];
					if (m.getDeclaringClass().getName().equals(excnode.getName())) {
						if (!m.getReturnType().equals(void.class)) {
							excmethods.add(m.getName());
						}
					}
				}
				
				// If the decorator doesn't override all of the methods in the class its extending, it's bad!
				if (!cmethods.equals(excmethods)) {
					color = "maroon1";
					description = "bad decorator";
				}
				
			}
			
			
			// Color/add descriptions to the components and links
			for (Link link : possibleComponents.keySet()) {
				link.setAttribute(new StyleAttribute("label", "<<decorates>>", 15));
				JavaClassNode component = (JavaClassNode) possibleComponents.get(link);
				
				component.setAttribute(new StyleAttribute("fillcolor", color, 15));
				component.setAttribute(new StyleAttribute("style", "filled", 15));
				
				component.addStereotype("component");
			}
			
			// Create a stack so we can color/describe anything that extends the inode too
			Stack<INode> toColor = new Stack<INode>();
			toColor.add(inode);
			
			int limit = RUN_LIMIT;
			while (!toColor.isEmpty() && limit-- > 0) {
				JavaClassNode decorator = (JavaClassNode) toColor.pop();
				
				// Add the styles to indicate that it's a decorator
				decorator.setAttribute(new StyleAttribute("fillcolor", color, 15));
				decorator.setAttribute(new StyleAttribute("style", "filled", 15));
				
				decorator.addStereotype(description);
				
				for (INode node : graph.getNodes().values()) {
					if (node.getQualifiedName().equals(decorator.getQualifiedName())) {
						continue;
					} else if (node.getQualifiedName().indexOf('$') >= 0) {
						continue;
					}
					for (Link link : node.getLinks()) {
						if (link.getEnd().equals(decorator.getQualifiedName()) && (link instanceof ExtendsLink)) {
							toColor.push(node);
						}
					}
				}
			}
		}
		
		return false;
	}

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
//			decorator = Thread.currentThread.getContextClassLoader().loadClass(d.getQualifiedName());
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
