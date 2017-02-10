package generator.links;

import java.util.Map;

import generator.Link;
import generator.StyleAttribute;

public class BidirectionalLink extends Link {
	private Link directionalLink;

	public BidirectionalLink(final Link link) {
		super();
		this.directionalLink = link;
		for (StyleAttribute sa : link.getStyleAttributes().values()) {
			this.setAttribute(sa);
		}
	}
	
	@Override
	public Link getBidirectional() {
		return this;
	}

	@Override
	public String getRelationship() {
		return this.directionalLink.getRelationship();
	}

	@Override
	public String getStart() {
		return this.directionalLink.getStart();
	}

	@Override
	public String getEnd() {
		return this.directionalLink.getEnd();
	}
	
	@Override
	public String getAttributes() {
		return super.getAttributes();
	}

	@Override
	public void setStyleAttributes() {
		this.setAttribute(new StyleAttribute("dir", "both", Integer.MAX_VALUE));
	}
	
	@Override
	public void setAttribute(StyleAttribute attribute) {
		super.setAttribute(attribute);
	}
	
	@Override
	public boolean equals(Object o) {
		return super.equals(o);
	}
	
	@Override
	public String toString() {
		return super.toString();
	}
	
	@Override
	public int hashCode() {
		return super.hashCode();
	}
	
	@Override
	public Map<String, StyleAttribute> getStyleAttributes() {
		return super.getStyleAttributes();
	}

}
