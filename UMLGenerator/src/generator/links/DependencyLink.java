package generator.links;

import generator.INode;

public class DependencyLink extends OneToOneLink {

	public DependencyLink(INode from, INode to) {
		super(from, to);
	}

	@Override
	public String getAttributes() {
		return "arrowhead=\"vee\", style=\"dashed\"";
	}

	@Override
	public int getPriority() {
		return 10;
	}

}
