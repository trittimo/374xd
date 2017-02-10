package generator.analyzers.pattern;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;

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

//all of methods you override, you make a call to an inner object
public class AdapterAnalyzer implements IAnalyzer {
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
			
			Map<Link, INode> possibleTargets = new HashMap<Link, INode>();
			// Step 1: determine if the node we're on extends or implements another class 
			for (Link link : inode.getLinks()) {
				if (link instanceof ImplementsLink || link instanceof ExtendsLink) {
					if (graph.getNodes().containsKey(link.getEnd())) {
						INode component = graph.getNodes().get(link.getEnd());
						possibleTargets.put(link, component);
					}
				}
			}
			
			if (possibleTargets.isEmpty()) {
				continue;
			}
			

			Class<?> excnode = null;
			Class<?> cnode = null;
			try {
				cnode = Thread.currentThread().getContextClassLoader().loadClass(inode.getQualifiedName());
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			
			List<Link> toRemove = new ArrayList<Link>();
			
			// Step 2: Ensure that every method in the adapter is in the thing its overriding
			for (Link l : possibleTargets.keySet()) {
				INode extended = possibleTargets.get(l);
				try {
					excnode = Thread.currentThread().getContextClassLoader().loadClass(extended.getQualifiedName());
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
				
				// Get all the methods from the node that are actually declared in the node's class
				Set<String> cmethods = new HashSet<String>();
				for (int i = 0; i < cnode.getMethods().length; i++) {
					Method m = cnode.getMethods()[i];
					if (m.getDeclaringClass().getName().equals(cnode.getName())) {
						cmethods.add(m.getName());
					}
				}
				
				// Get all the methods from the node that are actually declared in the node's class
				Set<String> excmethods = new HashSet<String>();
				for (int i = 0; i < excnode.getDeclaredMethods().length; i++) {
					Method m = excnode.getDeclaredMethods()[i];
					if (m.getDeclaringClass().getName().equals(excnode.getName())) {
						excmethods.add(m.getName());
					}
				}
				// If the adapter doesn't override all of the methods in the class its extending, it's not an adapter				
				if (!excmethods.equals(cmethods)) {
					toRemove.add(l);
				}
			}
			
			for (Link l : toRemove) {
				possibleTargets.remove(l);
			}
			
			if (possibleTargets.isEmpty()) {
				continue;
			}
			
			
			// Step 3: for every method, check that the method makes a call to the same inner object
			ClassNode classNode = ((JavaClassNode) inode).getClassNode();
			HashSet<String> possibleObjects = new HashSet<String>();
			List<MethodNode> methods = (List<MethodNode>) classNode.methods;
			
			for (FieldNode field : (List<FieldNode>) classNode.fields) {
				possibleObjects.add(field.name);
			}
			
			List<String> toRemove2 = new ArrayList<String>();
			
			for (MethodNode method : methods) {
				HashSet<String> seen = new HashSet<String>();
				InsnList instructions = method.instructions;
				Iterator it = instructions.iterator();
				while (it.hasNext()) {
					Object o = it.next();
					if (o instanceof FieldInsnNode) {
						FieldInsnNode insn = (FieldInsnNode) o;
						seen.add(insn.name);
					}
					
				}
				
				for (String s : possibleObjects) {
					if (!seen.contains(s)) {
						toRemove2.add(s);
					}
				}
			}
			
			possibleObjects.removeAll(toRemove2);
			
			// It's not an adapter
			if (possibleObjects.isEmpty()) {
				continue;
			}
			
			
			// Get what it's adapting			
			String adapting = possibleObjects.iterator().next();
			String qual = null;
			for (FieldNode field : (List<FieldNode>) classNode.fields) {
				if (field.name.equals(adapting)) {
					qual = field.desc;
					qual = qual.substring(1);
					qual = qual.replace('/', '.');
					qual = qual.substring(0, qual.length() - 1);
				}
			}
			
			// Add styles to adaptee
			for (INode node : graph.getNodes().values()) {
				if (node.getQualifiedName().equals(qual)) {
					JavaClassNode adaptee = (JavaClassNode) node;
					adaptee.setAttribute(new StyleAttribute("fillcolor", "firebrick", 15));
					adaptee.setAttribute(new StyleAttribute("style", "filled", 15));
					adaptee.addStereotype("adaptee");
				}
			}
			
			// Add styles to adapter
			JavaClassNode adapter = (JavaClassNode) inode;
			adapter.setAttribute(new StyleAttribute("fillcolor", "firebrick", 15));
			adapter.setAttribute(new StyleAttribute("style", "filled", 15));
			adapter.addStereotype("adapter");
			
			// Add styles to target
			JavaClassNode target = (JavaClassNode) possibleTargets.values().iterator().next();
			target.setAttribute(new StyleAttribute("fillcolor", "firebrick", 15));
			target.setAttribute(new StyleAttribute("style", "filled", 15));
			target.addStereotype("target");
			
			// Add adapts to arrow
			Link link = possibleTargets.keySet().iterator().next();
			link.setAttribute(new StyleAttribute("label", "<<adapts>>", 15));
			
		}
		return false;
	}
}
//			
//			if (possibleComponents.isEmpty()) {
//				continue;
//			}
//			
//			toRemove.clear();
//			toRemove.addAll(possibleComponents.keySet());
//			// Step 3: ensure that the node contains a constructor with the component
//			for (Link extended : possibleComponents.keySet()) {
//				for (Constructor<?> constructor : cnode.getDeclaredConstructors()) {
//					for (Class<?> parameter : constructor.getParameterTypes()) {
//						if (parameter.getName().equals(extended.getEnd())) {
//							toRemove.remove(extended);
//						}
//					}
//				}
//			}
//			for (Link l : toRemove) {
//				possibleComponents.remove(l);
//			}
//			
//			if (possibleComponents.isEmpty()) {
//				continue;
//			}
//			
//			// Confirmed that we're a decorator: could still be a bad decorator though
//			String color = "lawngreen";
//			String description = "decorator";
//			Class<?> excnode = null;
//			
//			// It's a bad decorator if the class does not override every method in its component
//			for (INode extended : possibleComponents.values()) {
//				try {
//					excnode = Thread.currentThread().getContextClassLoader().loadClass(extended.getQualifiedName());
//				} catch (ClassNotFoundException e) {
//					e.printStackTrace();
//				}
//				
//				
//				// Get all the methods from the node that are actually declared in the node's class
//				// and are not void
//				Set<String> cmethods = new HashSet<String>();
//				for (int i = 0; i < cnode.getMethods().length; i++) {
//					Method m = cnode.getMethods()[i];
//					if (m.getDeclaringClass().getName().equals(cnode.getName())) {
//						if (!m.getReturnType().equals(void.class)) {
//							cmethods.add(m.getName());
//						}
//					}
//				}
//				
//				// Get all the methods from the node that are actually declared in the node's class
//				// and are not void
//				Set<String> excmethods = new HashSet<String>();
//				for (int i = 0; i < excnode.getDeclaredMethods().length; i++) {
//					Method m = excnode.getMethods()[i];
//					if (m.getDeclaringClass().getName().equals(excnode.getName())) {
//						if (!m.getReturnType().equals(void.class)) {
//							excmethods.add(m.getName());
//						}
//					}
//				}
//				
//				// If the decorator doesn't override all of the methods in the class its extending, it's bad!
//				if (!cmethods.equals(excmethods)) {
//					color = "maroon1";
//					description = "bad decorator";
//				}
//				
//			}
//			
//			
//			// Color/add descriptions to the components and links
//			for (Link link : possibleComponents.keySet()) {
//				link.setAttribute(new StyleAttribute("label", "<<decorates>>", 15));
//				JavaClassNode component = (JavaClassNode) possibleComponents.get(link);
//				
//				component.setAttribute(new StyleAttribute("fillcolor", color, 15));
//				component.setAttribute(new StyleAttribute("style", "filled", 15));
//				
//				component.addStereotype("component");
//			}
//			
//			// Create a stack so we can color/describe anything that extends the inode too
//			Stack<INode> toColor = new Stack<INode>();
//			toColor.add(inode);
//			
//			int limit = RUN_LIMIT;
//			while (!toColor.isEmpty() && limit-- > 0) {
//				JavaClassNode decorator = (JavaClassNode) toColor.pop();
//				
//				// Add the styles to indicate that it's a decorator
//				decorator.setAttribute(new StyleAttribute("fillcolor", color, 15));
//				decorator.setAttribute(new StyleAttribute("style", "filled", 15));
//				
//				decorator.addStereotype(description);
//				
//				for (INode node : graph.getNodes().values()) {
//					if (node.getQualifiedName().equals(decorator.getQualifiedName())) {
//						continue;
//					} else if (node.getQualifiedName().indexOf('$') >= 0) {
//						continue;
//					}
//					for (Link link : node.getLinks()) {
//						if (link.getEnd().equals(decorator.getQualifiedName()) && (link instanceof ExtendsLink)) {
//							toColor.push(node);
//						}
//					}
//				}
//			}
//		}
//		
//		return false;