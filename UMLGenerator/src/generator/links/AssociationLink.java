
package generator.links;

import generator.INode;
import generator.Link;
import generator.StyleAttribute;

public class AssociationLink extends Link {

	public AssociationLink(INode start, INode end) {
		super(start, end);
	}
	
	public AssociationLink(String start, String end) {
		super(start, end);
	}
	
	@Override
	public void setStyleAttributes() {
		this.setAttribute(new StyleAttribute("arrowhead", "vee", 1));
	}

}
