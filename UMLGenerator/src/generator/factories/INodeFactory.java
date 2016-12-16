package generator.factories;

import generator.Graph;
import generator.INode;

public interface INodeFactory {
	
	public INode createNode(Graph g, String qualifiedName);
}
