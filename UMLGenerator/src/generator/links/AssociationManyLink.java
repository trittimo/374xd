package generator.links;

import generator.INode;

public class AssociationManyLink extends OneToManyLink {

	public AssociationManyLink(INode from, INode to) {
		super(from, to);
	}

	@Override
	public String getAttributes() {
		return "arrowhead=\"vee\", label=\"1,,,*\"";
	}

}
