package generator;

import java.util.List;

public interface INode {
	
	public String getQualifiedName();
	
	public String getLabel();
	
	public void addLink(Link link);
	
	public void createLinks(Graph g);
	
	public List<Link> getLinks();
	
	public void removeLink(Link link);
	
	public String getAttributeString();
	
	public void setAttribute(StyleAttribute sa);

}