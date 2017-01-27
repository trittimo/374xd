package generator;

public class StyleAttribute {
	private int priority;
	private String id;
	private String val;
	
	public StyleAttribute(String identifier, String value, int rulePriority) {
		priority = rulePriority;
		id = identifier;
		val = value;
	}
	
	public int getPriority() {return priority;}
	public String getIndentifier() {return id;}
	public String getValue() {return val;}
}
