package generator.factories;

import java.io.IOException;

import generator.Graph;

public interface IGraphFactory {
	
	public void addNodeToGraph(Graph g, String qualifiedName);
	public void linkGraph(Graph g);
}
