package generator.links;

import generator.ILink;
import generator.INode;

public abstract class OneToManyLink implements ILink {
	private int hashCode;
	
	protected String start;
	protected String end;
	
	public OneToManyLink(String from, String to) {
		start = from;
		end = to;
		// precompute hash so it only has to be done once
		hashCode = toString().hashCode();
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
	

	public String toString() {
		return String.format("<%s, %s>", 
			getClass().getName(), getRelationship())
			.replaceAll("Many", "");
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
