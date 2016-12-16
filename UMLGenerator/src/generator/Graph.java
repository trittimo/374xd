package generator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Graph {
	private Map<String, INode> nodes;
	
	public Graph() {
		nodes = new HashMap<String, INode>();
	}
	
	public void addNode(INode n) {
		this.nodes.put(n.getQualifiedName(), n);
	}
	
	public Map<String, INode> getNodes() {
		return nodes;
	}
}
