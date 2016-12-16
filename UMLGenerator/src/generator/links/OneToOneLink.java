package generator.links;

import generator.ILink;
import generator.INode;

public abstract class OneToOneLink implements ILink {
	String start;
	String end;
	
	public OneToOneLink(String from, String to) {
		start = from;
		end = to;
	}
	
	public OneToOneLink(INode from, INode to) {
		this(from.getQualifiedName(), to.getQualifiedName());
	}
	
	public String getRelationship() {
		return start + " -> " + end;
	}
	
	public abstract String getAttributes();
}
