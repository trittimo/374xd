package generator.links;

import generator.INode;
import generator.Link;
import generator.StyleAttribute;

public class ImplementsLink extends Link {

	public ImplementsLink(String from, String to) {
		super(from, to);
		this.setAttribute(new StyleAttribute("arrowhead", "onormal", 1));
		this.setAttribute(new StyleAttribute("style", "dashed", 1));
	}
	
	public ImplementsLink(INode from, INode to) {
		this(from.getQualifiedName(), to.getQualifiedName());
	}

	@Override
	public int getPriority() {
		return 30;
	}

}
