package generator;

import java.util.List;

public interface INode {
	
	public String getQualifiedName();
	
	public String getLabel();
	
	public void addLink(ILink link);
	
	public void createLinks(Graph g);
	
	public List<ILink> getLinks();
	
	public void removeLink(ILink link);
	
	public String getAttributeString();
	public void setAttribute(StyleAttribute sa);

}
