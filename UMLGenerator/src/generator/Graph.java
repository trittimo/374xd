package generator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Graph {
	private Map<String, INode> nodes;
	private ArrayList<String> nodeOrdered;
	
	public Graph() {
		nodes = new HashMap<String, INode>();
		nodeOrdered = new ArrayList<String>();
	}
	
	public INode addNode(INode n) {
		this.nodes.put(n.getQualifiedName(), n);
		nodeOrdered.add(n.getQualifiedName());
		return n;
	}
	
	public Map<String, INode> getNodes() {
		return nodes;
	}
}
