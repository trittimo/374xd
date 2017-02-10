package generator.links;

import generator.INode;
import generator.Link;
import generator.StyleAttribute;

public class DependencyLink extends Link {

	public DependencyLink(INode start, INode end) {
		super(start, end);
	}
	
	public DependencyLink(String start, String end) {
		super(start, end);
	}
	
	@Override
	public void setStyleAttributes() {
		this.setAttribute(new StyleAttribute("arrowhead", "vee", 1));
		this.setAttribute(new StyleAttribute("style", "dashed", 1));
	}


}
