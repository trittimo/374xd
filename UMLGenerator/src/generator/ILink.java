package generator;

public interface ILink {
	public String getRelationship();
	public int getPriority();
	public String getStart();
	public String getEnd();

	public String getAttributes();
	
	// attributes must be immuatable after being passed
	// prevents code from messing with our style by modifying it after passing the object
	public void setAttribute(final StyleAttribute style);
	
	// Overridden by object, but should be implemented by all implementing classes
	public String toString();
	public boolean equals(Object o);
}
