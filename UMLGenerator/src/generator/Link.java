package generator;

import java.util.HashMap;
import java.util.Map;

import generator.links.BidirectionalLink;

public abstract class Link {
	private Map<String, StyleAttribute> attributes;
	private int hashCode;
	protected String start;
	protected String end;
	
	public abstract int getPriority();
	
	public Link() {
		this.attributes = new HashMap<String, StyleAttribute>();
	}
	
	public Link(String from, String to) {
		this();
		start = from;
		end = to;
		// initialize attributes
		// precompute hashCode for speed. used to uniquely identify links
		hashCode = toString().hashCode();
	}
	
	public Link(INode from, INode to) {
		this(from.getQualifiedName(), to.getQualifiedName());
	}
	// attributes must be immuatable after being passed
	// prevents code from messing with our style by modifying it after passing the object
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
	
	public Map<String, StyleAttribute> getStyleAttributes() {
		return this.attributes;
	}
	
	public String toString() {
		return String.format("<%s, %s>", 
				getClass().getName(), getRelationship());
	}
	
	public boolean equals(Object o) {
		if (!(o instanceof Link))
			return false;
		return o.toString().equals(this.toString());
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
	
	public int hashCode() {
		return this.hashCode;
	}
	
	public Link getBidirectional() {
		return new BidirectionalLink(this);
	}
}