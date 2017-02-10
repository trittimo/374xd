package generator.links;

import generator.INode;
import generator.Link;
import generator.StyleAttribute;

public class ImplementsLink extends Link {

	public ImplementsLink(INode start, INode end) {
		super(start, end);
	}
	
	public ImplementsLink(String start, String end) {
		super(start, end);
	}

	@Override
	public void setStyleAttributes() {
		this.setAttribute(new StyleAttribute("arrowhead", "onormal", 1));
		this.setAttribute(new StyleAttribute("style", "dashed", 1));
	}


}
