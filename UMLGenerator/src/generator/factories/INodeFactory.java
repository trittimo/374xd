package generator.factories;

import java.io.IOException;

import generator.Graph;
import generator.INode;

public interface INodeFactory {
	
	public INode createNode(Graph g, String qualifiedName) throws IOException;
}
