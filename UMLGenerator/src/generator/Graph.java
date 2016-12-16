package generator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Graph {
	
	private Map<String, INode> nodes;
	public ArrayList<ILink> links;
	
	public Graph() {
		nodes = new HashMap<String, INode>();
		links = new ArrayList<ILink>();
	}
	
	public INode addNode(INode n) {
		this.nodes.put(n.getQualifiedName(), n);
		return n;
	}
	
	public Map<String, INode> getNodes() {
		return nodes;
	}
}
