package generator.links;

import generator.INode;

public class ExtendsLink extends OneToOneLink {

	public ExtendsLink(INode from, INode to) {
		super(from, to);
	}
	
	public ExtendsLink(String from, String to) {
		super(from, to);
	}

	@Override
	public String getAttributes() {
		return "arrowhead=\"onormal\"";
	}

}
