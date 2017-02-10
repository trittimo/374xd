package generator.analyzers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

import generator.Graph;
import generator.Link;
import generator.INode;
import generator.commands.CMDParams;
import generator.factories.IGraphFactory;
import generator.links.AssociationLink;
import generator.links.AssociationManyLink;
import generator.nodes.JavaClassNode;

public class FieldAnalyzer implements IAnalyzer {

	ArrayList<String> done;
	
	public FieldAnalyzer() {
		done = new ArrayList<String>();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean analyze(Graph graph, CMDParams params, IGraphFactory factory) {
		
		HashMap<String, Set<String>> fields = new HashMap<String, Set<String>>();
		HashMap<String, Set<String>> multiFields = new HashMap<String, Set<String>>();
		
		String className;
		
		for (INode node : graph.getNodes().values()) {
			if (node instanceof JavaClassNode) {
				ClassNode classNode = ((JavaClassNode) node).getClassNode();
				if (done.contains(classNode.name))
					continue;
				done.add(classNode.name);
				className = classNode.name.replaceAll("/", ".");
				HashSet<String> currentList = new HashSet<String>();
				HashSet<String> currentMultiList = new HashSet<String>();
				for (FieldNode fn : (List<FieldNode>) classNode.fields) {
					parseSignature(currentList, currentMultiList, 
						(fn.signature == null)?fn.desc:fn.signature);
				}
				multiFields.put(className, currentMultiList);
				fields.put(className, currentList);
			}
		}

		Link link;
		for (String name : multiFields.keySet()) {
			INode node = graph.getNodes().get(name);
			for (String field : multiFields.get(name)) {
				if (!graph.getNodes().containsKey(field))
					factory.addNodeToGraph(graph, field);
				if (node != null && graph.getNodes().get(field) != null) {
					link = new AssociationManyLink(node, graph.getNodes().get(field));
					//System.out.println("FieldAnalyzer: NewLink: " + link);
					node.addLink(link);
				}
			}
		}
		
		for (String name : fields.keySet()) {
			INode node = graph.getNodes().get(name);
			for (String field : fields.get(name)) {
				if (multiFields.get(name).contains(field)) // case: already added as multi
					continue;
				if (!graph.getNodes().containsKey(field))
					factory.addNodeToGraph(graph, field);
				link = new AssociationLink(node, graph.getNodes().get(field));
				//System.out.println("FieldAnalyzer: NewLink: " + link);
				node.addLink(link);
			}
		}
		
		// Only changes links, does not add nodes. Other analyzers shouldn't need to update
		return false;
	}
	

	public void parseSignature(Set<String> list, Set<String> multilist, String analyze) {
		// ignore array bits
		boolean oneToMany = false;
		while (analyze.startsWith("[")) {
			analyze = analyze.substring(1);
			oneToMany = true;
		}
		
		if (oneToMany)
			list = multilist;
		
		int index_brace = analyze.indexOf('<');
		int index_semic = analyze.indexOf(';');
		
		// Handle case where a ; comes before a < 
		// e.g. Lsome/class/here;Ljava/util/ArrayList<Ljava/lang/String;>;
		while (index_semic < index_brace) {
			while (analyze.startsWith("[")) {
				analyze = analyze.substring(1);
				oneToMany = true;
			}
			if (analyze.equals("Ljava/lang/Class<*>;")) {
				return;
			}
			if (analyze.charAt(0) == 'L')
				list.add(analyze.substring(1, index_semic));
			analyze = analyze.substring(index_semic + 1);
			index_semic = analyze.indexOf(';');
			index_brace = analyze.indexOf('<');
			oneToMany = false;
		}
		
		while (analyze.startsWith("[")) {
			analyze = analyze.substring(1);
			oneToMany = true;
		}
		
		// skip non-objects
		if (!analyze.startsWith("L"))
			return;
		
		// if signature has a generic
		if (index_brace > 0) {
			// parse list part
			list.add(analyze.substring(1, index_brace).replace('/', '.'));
			// parse generic
			parseSignature(multilist, multilist, analyze.substring(index_brace + 1, analyze.lastIndexOf('>')));
		} else {
			// without generic
			list.add(analyze.substring(1, index_semic).replace('/', '.'));
			analyze = analyze.substring(index_semic + 1);
			if (analyze.length() > 0)
				parseSignature((oneToMany)?multilist:list, multilist, analyze);
		}
	}

}
