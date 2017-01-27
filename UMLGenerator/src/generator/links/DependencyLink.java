package generator.links;

import generator.INode;
import generator.StyleAttribute;

public class DependencyLink extends OneToOneLink {

	public DependencyLink(INode from, INode to) {
		super(from, to);
		this.setAttribute(new StyleAttribute("arrowhead", "vee", 1));
		this.setAttribute(new StyleAttribute("style", "dashed", 1));
	}

	@Override
	public int getPriority() {
		return 10;
	}

}
