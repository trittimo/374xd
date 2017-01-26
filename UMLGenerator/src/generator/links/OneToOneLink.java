package generator.links;

import generator.ILink;
import generator.INode;

public abstract class OneToOneLink implements ILink {
	private int hashCode;
	
	protected String start;
	protected String end;
	
	public OneToOneLink(String from, String to) {
		start = from;
		end = to;
		// precompute hashCode for speed. used to uniquely identify links
		hashCode = toString().hashCode();
	}
	
	public OneToOneLink(INode from, INode to) {
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
	
	public String toString() {
		return String.format("<%s, %s>", 
				getClass().getName(), getRelationship());
	}
	
	public int hashCode() {
		return this.hashCode;
	}
	
	public boolean equals(Object o) {
		if (!(o instanceof ILink))
			return false;
		return o.toString().equals(this.toString());
	}
}
