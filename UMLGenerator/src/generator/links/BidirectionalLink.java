package generator.links;

import java.util.HashMap;
import java.util.Map;

import generator.Link;
import generator.StyleAttribute;

public class BidirectionalLink extends Link {
	private Link directionalLink;
	Map<String, StyleAttribute> attributes;

	public BidirectionalLink(final Link link) {
		this.directionalLink = link;
		this.attributes = new HashMap<String, StyleAttribute>();
		this.attributes.putAll(link.getStyleAttributes());
		this.setAttribute(new StyleAttribute("dir", "both", Integer.MAX_VALUE));
	}
	
	@Override
	public Link getBidirectional() {
		return this;
	}

	@Override
	public int getPriority() {
		return this.directionalLink.getPriority();
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

}
