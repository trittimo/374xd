package generator.analyzers.violation;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;

import generator.Graph;
import generator.INode;
import generator.Link;
import generator.StyleAttribute;
import generator.analyzers.IAnalyzer;
import generator.commands.CMDParams;
import generator.commands.ExternalClassLoader;
import generator.factories.IGraphFactory;
import generator.links.AssociationLink;
import generator.links.AssociationManyLink;
import generator.links.DependencyLink;
import generator.links.DependencyManyLink;
import generator.nodes.JavaAbstractNode;
import generator.nodes.JavaAnnotationNode;
import generator.nodes.JavaInterfaceNode;

/**
 * Detects violations in dependency inversion principle. 
 * 
 * "Depend on abstractions. Do not depend on concrete classes."
 * 
 * If we have a dependency arrow to a concrete class -> violation
 * 
 * @author AMcKee
 *
 */
public class DependencyInversionAnalyzer implements IAnalyzer {
	
	private boolean SHOW_WARNINGS = false;
	
	@Override
	public boolean analyze(Graph graph, CMDParams params, IGraphFactory factory) {
		final StyleAttribute fillColor = new StyleAttribute("fillcolor", "cyan", 5);
		final StyleAttribute fillStyle = new StyleAttribute("style", "filled", 5);
		
		SHOW_WARNINGS = params.getFlags().contains("show-design-warnings") || SHOW_WARNINGS;
		
		for (INode violator : graph.getNodes().values()) {
			for (Link link : violator.getLinks()) {
				if ((link instanceof DependencyLink) || (link instanceof DependencyManyLink)
						|| (link instanceof AssociationLink) || (link instanceof AssociationManyLink)) {
					
					INode end = graph.getNodes().get(link.getEnd());
					if (!isViolation(end))
						continue;
					String name = end.getQualifiedName();
					if (name.lastIndexOf('.') > -1) {
						name = name.substring(name.lastIndexOf('.')+1);
					}
					
					violator.addStereotype("Depends on " + name);
					end.addStereotype("Concrete Dependency");
					violator.setAttribute(fillColor);
					violator.setAttribute(fillStyle);
					end.setAttribute(fillColor);
					end.setAttribute(fillStyle);
				}
			}
		}
		
		// never make structural changes to graph, no need to rerun analyzers
		return false;
	}

	private boolean isViolation(INode end) {		
		// depends on abstract is OK
		if (end instanceof JavaAbstractNode) {
//			System.out.println(end.getQualifiedName() + " not violation because abstract");
			return false;
		}
		// depends on interface is OK
		if (end instanceof JavaInterfaceNode) {
//			System.out.println(end.getQualifiedName() + " not violation because interface");
			return false;
		}
		// depends on annotation is probably OK, more likely to be a false positive
		if (end instanceof JavaAnnotationNode) {
//			System.out.println(end.getQualifiedName() + " not violation because annotation");
			return false;
		}
		// check to see if child has abstract parent available. if so, determine it to be a violation
		if (hasAbstractParent(end.getQualifiedName())) {
			if (SHOW_WARNINGS)
				System.out.printf("DependencyInversion: VIOLATION: %s has an abstract parent.%n", end.getQualifiedName());
			return true;
		}
		
//		System.out.println(end.getQualifiedName() + " not violation because ?");
		return false;
	}
	
	private Map<String, ClassNode> cache = new HashMap<String, ClassNode>();

	private boolean hasAbstractParent(String className) {
		ClassNode superNode, classNode = loadClass(className);
		if (classNode == null)
			return false;
		
		if (classNode.superName != null) {
			
			// if super is object -> not a violation
			if (classNode.superName.equals("java/lang/Object"))
				return false;
			
			superNode = loadClass(classNode.superName);
			if (((superNode.access & Opcodes.ACC_ABSTRACT) > 0) || ((superNode.access & Opcodes.ACC_INTERFACE) > 0)) {
				if (SHOW_WARNINGS)
					System.out.printf("DependencyInversion: PRE-VIOLATION: %s has abstract parent %s%n", classNode.name, superNode.name);
				return true;
			} else {
//				System.out.println(classNode.name + " is not abstract!!!");
			}
			
			return hasAbstractParent(classNode.superName);
		}
		
		return false;
		
	}
	
	private ClassNode loadClass(String className) {
		if (cache.containsKey(className))
			return cache.get(className);
		ClassReader reader = null;
		try {
			reader = ExternalClassLoader.getClassReader(className);
		} catch (IOException e) {
			System.out.println("Unable to find class: " + className);
			return null;
		}
		ClassNode classNode = new ClassNode();
		reader.accept(classNode, ClassReader.EXPAND_FRAMES);
		cache.put(className, classNode);
		return classNode;
	}
	
}
