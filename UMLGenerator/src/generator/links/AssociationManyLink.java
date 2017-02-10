package generator.links;

import generator.INode;
import generator.Link;
import generator.StyleAttribute;

public class AssociationManyLink extends Link {

	public AssociationManyLink(INode start, INode end) {
		super(start, end);
	}
	
	public AssociationManyLink(String start, String end) {
		super(start, end);
	}
	
	@Override
	public void setStyleAttributes() {
		this.setAttribute(new StyleAttribute("arrowhead", "vee", 1));
		this.setAttribute(new StyleAttribute("label", "1,,,*", 1));
	}

}
