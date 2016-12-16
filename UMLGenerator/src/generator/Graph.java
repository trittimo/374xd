package generator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Graph {
	
	private Map<String, INode> nodes;
	
	public Graph() {
		nodes = new HashMap<String, INode>();
	}
	
	public INode addNode(INode n) {
		this.nodes.put(n.getQualifiedName(), n);
		return n;
	}
	
	public Map<String, INode> getNodes() {
		return nodes;
	}
}
