package generator.links;

import generator.INode;

public class AssociationLink extends OneToOneLink {

	public AssociationLink(INode from, INode to) {
		super(from, to);
	}

	@Override
	public String getAttributes() {
		return "arrowhead=\"vee\"";
	}

}
