package generator.links;

import generator.INode;
import generator.StyleAttribute;

public class ExtendsLink extends OneToOneLink {

	public ExtendsLink(INode from, INode to) {
		super(from, to);
		this.setAttribute(new StyleAttribute("arrowhead", "onormal", 1));
	}
	
	public ExtendsLink(String from, String to) {
		super(from, to);
		this.setAttribute(new StyleAttribute("arrowhead", "onormal", 1));
	}

	@Override
	public int getPriority() {
		return 30;
	}

}
