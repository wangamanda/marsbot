package marsbot;

import java.util.ArrayList;

public class Node 
{
	private String id;
	public int value;
	private boolean visited;
	private ArrayList<Node> neighbors;
	
	public Node(String id)
	{
		this.id = id;
		neighbors = new ArrayList<Node>();
		visited = false;
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
		
		return ((Node)obj).id.equals(this.id);
	}
	
	public void addNeighbor(Node node)
	{
		if (!neighbors.contains(node))
		{
			neighbors.add(node);
		}
	}
	
	// Gets list of neighbors
	public ArrayList<Node> getNeighbors()
	{
		return neighbors; // technically this is a security flaw
						  // however, this access control is being done to prevent accidental removal of elements
						  // and hopefully the syntax required to accidentally abuse this hole is obscure enough to protect us from such bugs
	}
	
	public String getId()
	{
		return id;
	}
		
	public boolean hasBeenVisited()
	{
		return visited;
	}
	
	public void markVisited()
	{
		visited = true;
	}
}
