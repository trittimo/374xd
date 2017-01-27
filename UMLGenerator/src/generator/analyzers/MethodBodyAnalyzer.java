package generator.analyzers;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodNode;
import generator.Graph;
import generator.INode;
import generator.commands.CMDParams;
import generator.factories.IGraphFactory;
import generator.links.DependencyLink;
import generator.nodes.JavaClassNode;

public class MethodBodyAnalyzer  implements IAnalyzer {

	private HashMap<String, HashSet<String>> map;
	
	public MethodBodyAnalyzer() {
		map = new HashMap<String, HashSet<String>>();
	}
	
	@Override
	public boolean analyze(Graph graph, CMDParams params, IGraphFactory factory) {
		
		boolean recursive = params.getFlags().contains("r");
		HashSet<String> addClasses = new HashSet<String>();
	
		// loop over the nodes
		for (INode node : graph.getNodes().values()) {
			if (node instanceof JavaClassNode) {
				ClassNode classNode = ((JavaClassNode) node).getClassNode();
				analyzeClass(graph, addClasses, classNode, recursive);
			}
		}
		
		for (String s : addClasses) {
			factory.addNodeToGraph(graph, s);
		}
		
		// Add new classes, etc 
		INode start;
		INode end;
		for (String s : map.keySet()) {
			for (String t: map.get(s)) {
				start = graph.getNodes().get(s);
				end = graph.getNodes().get(t);
				if (start == null || end == null) {
					continue;
				}
				start.addLink(new DependencyLink(start, end));
			}
		}
		
		// return true if added a class
		return (addClasses.size() > 0);
	}
	
	@SuppressWarnings("unchecked")
	private void analyzeClass(Graph g, HashSet<String> newClasses, ClassNode classNode, boolean recursive) {
		String classname = classNode.name.replace('/', '.');		
		
		// if already analyzed, skip
		if (map.containsKey(classname))
			return;
		
		//System.out.println("Analyzing Method Bodies of Class: " + classname);
		
		// declare some vars to use
		String toparse;
		HashSet<String> toLink = new HashSet<String>();
		
		
		// for every method in a class node...
		for (MethodNode m : ((List<MethodNode>) classNode.methods)) {
			
			// skip if no local variables for some reason
			if (m.localVariables == null)
				continue;
			
			/* Iterate over LocalVariableNodes of the method
			 *
			 * This gets all locally declared variables, like ArrayList<String> arrayList = new ArrayList<String>(4);
			 * However, it will miss cases where we don't allocate a variable, like "new HashSet<Integer>().add(5);"
			 * Since those cases are mostly useless, it's probably OK for now? Other strategies involve observers 
			 * (which we can't use) or decompiling the byte-code (too much work for little gain)
			 */
			for (LocalVariableNode v : (List<LocalVariableNode>) m.localVariables) {
				if (v.signature == null)
					toparse = v.desc;
				else
					toparse = v.signature;
				parseSignature(toLink, toparse);
			}
		}

		// asm always picks up the class as a local variable of itself
		// we should remove that since UML doesn't really consider it that way
		toLink.remove(classname);
		
		// check to see if we have nodes not in the graph
		for (String s : toLink) {
			if (!g.getNodes().containsKey(s)) {
				// add node to graph
				newClasses.add(s);
			}
		}
		
		// put info in the map
		map.put(classname, toLink);
	}

	public void parseSignature(Set<String> list, String analyze) {
		// ignore array bits
		while (analyze.startsWith("["))
			analyze = analyze.substring(1);
		
		// skip non-objects
		if (!analyze.startsWith("L"))
			return;
		
		analyze = analyze.substring(1); // skip the L
		
		
		int index_brace = analyze.indexOf('<');
		int index_semic = analyze.indexOf(';');
		
		// Handle case where a ; comes before a < 
		// e.g. Lsome/class/here;Ljava/util/ArrayList<Ljava/lang/String;>;
		if (index_semic < index_brace) {
			list.add(analyze.substring(0, index_semic));
			analyze = analyze.substring(index_semic + 1);
		}
		
		
		// if signature has a generic
		if (index_brace > 0) {
			list.add(analyze.substring(0, index_brace).replace('/', '.'));
			// parse generic
			try {
				parseSignature(list, analyze.substring(index_brace + 1, analyze.indexOf('>')));
			} catch (Exception e) {
				System.out.println("Malformed signature: " + analyze);
			}
		} else {
			// without generic
			list.add(analyze.substring(0, index_semic).replace('/', '.'));
			analyze = analyze.substring(index_semic + 1);
			if (analyze.length() > 0)
				parseSignature(list, analyze);
		}
	} 
	

}
