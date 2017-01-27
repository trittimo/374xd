package generator;

public interface ILink {
	public String getRelationship();
	public String getAttributes();
	public int getPriority();
	
	// Overridden by object, but should be implemented by all implementing classes
	public String toString();
	public boolean equals(Object o);
}
