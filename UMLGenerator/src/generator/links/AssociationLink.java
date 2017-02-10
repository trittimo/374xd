
package generator.links;

import generator.INode;
import generator.Link;
import generator.StyleAttribute;

public class AssociationLink extends Link {
	
	public AssociationLink(String from, String to) {
		super(from, to);
		this.setAttribute(new StyleAttribute("arrowhead", "vee", 1));
	}
	
	public AssociationLink(INode from, INode to) {
		this(from.getQualifiedName(), to.getQualifiedName());
	}

	@Override
	public int getPriority() {
		return 20;
	}

}
