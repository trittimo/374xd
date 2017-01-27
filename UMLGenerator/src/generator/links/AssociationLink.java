package generator.links;

import generator.INode;
import generator.StyleAttribute;

public class AssociationLink extends OneToOneLink {

	public AssociationLink(INode from, INode to) {
		super(from, to);
		this.setAttribute(new StyleAttribute("arrowhead", "vee", 1));
	}

	@Override
	public int getPriority() {
		return 20;
	}

}
