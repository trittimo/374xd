package generator.links;

import generator.INode;

public class AssociationManyLink extends OneToManyLink {

	public AssociationManyLink(INode from, INode to) {
		super(from, to);
	}

	@Override
	public String getAttributes() {
		return "arrowhead=\"vee\", xlabel=\"1,,,*\"";
	}

	@Override
	public int getPriority() {
		return 25;
	}

}
