package marsbot;

import java.util.ArrayList;

public class Node 
{
	int id;
	int value;
	boolean visited;
	ArrayList<Node> neighbors;
	
	public Node(int id)
	{
		this.id = id;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof Node))
		{
			return false;
		}
		
		if (this == obj)
		{
			return true;
		}
		
		return ((Node)obj).id == this.id;
	}
}
