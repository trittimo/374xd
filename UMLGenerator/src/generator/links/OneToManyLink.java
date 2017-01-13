package generator.links;

import generator.ILink;
import generator.INode;

public abstract class OneToManyLink implements ILink {
	
	protected String start;
	protected String end;
	
	public OneToManyLink(String from, String to) {
		start = from;
		end = to;
	}
	
	public OneToManyLink(INode from, INode to) {
		this(from.getQualifiedName(), to.getQualifiedName());
	}
	
	public String getStart() {
		return this.start;
	}
	
	public String getEnd() {
		return this.end;
	}
	
	public String getRelationship() {
		return start + " -> " + end;
	}
	
	public abstract String getAttributes();
}
