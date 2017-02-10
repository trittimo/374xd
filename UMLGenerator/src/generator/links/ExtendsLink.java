package generator.links;

import generator.INode;
import generator.Link;
import generator.StyleAttribute;

public class ExtendsLink extends Link {

	public ExtendsLink(String from, String to) {
		super(from, to);
		this.setAttribute(new StyleAttribute("arrowhead", "onormal", 1));
	}
	
	public ExtendsLink(INode from, INode to) {
		this(from.getQualifiedName(), to.getQualifiedName());
	}
	

	@Override
	public int getPriority() {
		return 30;
	}

}
