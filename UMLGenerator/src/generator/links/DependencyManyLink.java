package generator.links;

import generator.INode;
import generator.Link;
import generator.StyleAttribute;

public class DependencyManyLink extends Link {

	public DependencyManyLink(INode start, INode end) {
		super(start, end);
	}
	
	public DependencyManyLink(String start, String end) {
		super(start, end);
	}
	@Override
	public void setStyleAttributes() {
		this.setAttribute(new StyleAttribute("arrowhead", "vee", 1));
		this.setAttribute(new StyleAttribute("style", "dashed", 1));
	}

}
