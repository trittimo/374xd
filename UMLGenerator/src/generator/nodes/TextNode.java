package generator.nodes;

import java.util.List;

import generator.ILink;
import generator.INode;

public class TextNode implements INode {
	
	private String qualifiedName;
	private String text;
	private List<ILink> links;
	
	public TextNode(String nameOf, String textContent) {
		qualifiedName = nameOf;
		text = textContent;
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
	public void addLink(ILink l) {
		links.add(l);
	}

	@Override
	public List<ILink> getLinks() {
		return links;
	}

}
