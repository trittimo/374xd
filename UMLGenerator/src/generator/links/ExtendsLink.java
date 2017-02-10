package generator.links;

import generator.INode;
import generator.Link;
import generator.StyleAttribute;

public class ExtendsLink extends Link {

	public ExtendsLink(INode start, INode end) {
		super(start, end);
	}
	
	public ExtendsLink(String start, String end) {
		super(start, end);
	}
	@Override
	public void setStyleAttributes() {
		this.setAttribute(new StyleAttribute("arrowhead", "onormal", 1));
	}

}
