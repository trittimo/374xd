package generator.links;

import generator.INode;
import generator.Link;
import generator.StyleAttribute;

public class DependencyManyLink extends Link {

	public DependencyManyLink(String from, String to) {
		super(from, to);
		this.setAttribute(new StyleAttribute("arrowhead", "vee", 1));
		this.setAttribute(new StyleAttribute("style", "dashed", 1));
	}
	
	public DependencyManyLink(INode from, INode to) {
		this(from.getQualifiedName(), to.getQualifiedName());
	}

	@Override
	public int getPriority() {
		return 15;
	}


}
