package generator.links;

import generator.INode;
import generator.StyleAttribute;

public class AssociationManyLink extends OneToManyLink {

	public AssociationManyLink(INode from, INode to) {
		super(from, to);
		this.setAttribute(new StyleAttribute("arrowhead", "vee", 1));
		this.setAttribute(new StyleAttribute("label", "1,,,*", 1));
	}

	@Override
	public int getPriority() {
		return 25;
	}

}
