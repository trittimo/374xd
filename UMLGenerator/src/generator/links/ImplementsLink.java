package generator.links;

import generator.INode;
import generator.StyleAttribute;

public class ImplementsLink extends OneToOneLink {

	public ImplementsLink(INode from, INode to) {
		super(from, to);
		this.setAttribute(new StyleAttribute("arrowhead", "onormal", 1));
		this.setAttribute(new StyleAttribute("style", "dashed", 1));
	}

	public ImplementsLink(String from, String to) {
		super(from, to);
		this.setAttribute(new StyleAttribute("arrowhead", "onormal", 1));
		this.setAttribute(new StyleAttribute("style", "dashed", 1));
	}

	@Override
	public int getPriority() {
		return 30;
	}

}
