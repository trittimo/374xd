package generator.links;

import generator.INode;
import generator.Link;
import generator.StyleAttribute;

public class AssociationManyLink extends Link {

	public AssociationManyLink(String from, String to) {
		super(from, to);
		this.setAttribute(new StyleAttribute("arrowhead", "vee", 1));
		this.setAttribute(new StyleAttribute("label", "1,,,*", 1));
	}
	
	public AssociationManyLink(INode from, INode to) {
		this(from.getQualifiedName(), to.getQualifiedName());
	}

	@Override
	public int getPriority() {
		return 25;
	}

}
