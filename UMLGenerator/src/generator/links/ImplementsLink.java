package generator.links;

import generator.INode;

public class ImplementsLink extends OneToOneLink {

	public ImplementsLink(INode from, INode to) {
		super(from, to);
	}

	public ImplementsLink(String from, String to) {
		super(from, to);
	}

	@Override
	public String getAttributes() {
		return "arrowhead=\"onormal\", style=\"dashed\"";
	}

}
