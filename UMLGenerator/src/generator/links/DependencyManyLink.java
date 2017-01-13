package generator.links;

import generator.INode;

public class DependencyManyLink extends OneToManyLink {

	public DependencyManyLink(INode from, INode to) {
		super(from, to);
	}

	@Override
	public String getAttributes() {
		return "arrowhead=\"vee\", style=\"dashed\", label=\"1...*\"";
	}

}
