package generator.links;

import java.util.HashMap;

import generator.ILink;
import generator.INode;
import generator.StyleAttribute;

public abstract class OneToManyLink implements ILink {
	private int hashCode;
	
	protected HashMap<String, StyleAttribute> attributes;
	
	protected String start;
	protected String end;
	
	public OneToManyLink(String from, String to) {
		start = from;
		end = to;
		// create attribute map
		attributes = new HashMap<String, StyleAttribute>();
		// precompute hash so it only has to be done once
		hashCode = toString().hashCode();
	}
	
	public OneToManyLink(INode from, INode to) {
		this(from.getQualifiedName(), to.getQualifiedName());
	}
	
	public void setAttribute(final StyleAttribute sa) {
		String id = sa.getIndentifier();
		// if already have this style
		if (attributes.containsKey(id)) {
			// return unless new style has higher priority.
			if (attributes.get(id).getPriority() >= sa.getPriority())
				return;
		}
		// otherwise, set the style
		attributes.put(id, sa);
	}

	
	public String getAttributes() {
		String str = "";
		boolean first = true;
		for (StyleAttribute sa : this.attributes.values()) {
			str += ((first)?"":", ") + sa.getIndentifier() + "=\"" + sa.getValue() + "\"";
		}
		return str;
	}
	
	public abstract int getPriority();
	
	public String getStart() {
		return this.start;
	}
	
	public String getEnd() {
		return this.end;
	}
	
	public String getRelationship() {
		return start + " -> " + end;
	}
	

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
