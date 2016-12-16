package generator;

import java.util.List;

public interface INode {

	public String getQualifiedName();
	
	public String getLabel();
	
	public void addLink(ILink link);
	
	public List<ILink> getLinks();
	

}
