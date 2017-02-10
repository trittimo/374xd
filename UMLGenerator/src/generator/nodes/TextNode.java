package generator.nodes;

import java.util.HashMap;
import java.util.List;

import generator.Graph;
import generator.Link;
import generator.INode;
import generator.StyleAttribute;

public class TextNode implements INode {
	
	private String qualifiedName;
	private String text;
	private List<Link> links;
	
	protected HashMap<String, StyleAttribute> attributes;
	
	public TextNode(String nameOf, String textContent) {
		qualifiedName = nameOf;
		text = textContent;
		attributes = new HashMap<String, StyleAttribute>();
		this.setAttribute(new StyleAttribute("shape", "record", 10));
	}
	
	@Override
	public String getQualifiedName() {
		return qualifiedName;
	}

	@Override
	public String getLabel() {
		return text;
	}

	@Override
	public void addLink(Link l) {
		links.add(l);
	}

	@Override
	public List<Link> getLinks() {
		return links;
	}

	@Override
	public void createLinks(Graph g) {
		return;
	}

	@Override
	public void removeLink(Link link) {
		links.remove(link);
	}

	@Override
	public String getAttributeString() {
		String str = "";
		for (StyleAttribute sa : this.attributes.values()) {
			str += String.format(", %s=\"%s\"", sa.getIndentifier(), sa.getValue());
		}
		return str;
	}

	@Override
	public void setAttribute(StyleAttribute sa) {
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

}
