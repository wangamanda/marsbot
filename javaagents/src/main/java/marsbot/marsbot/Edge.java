package marsbot;

public class Edge
{
	Node node1;
	Node node2;
	int weight;
	
	public Edge(Node node1, Node node2, int weight)
	{
		this.node1 = node1;
		this.node2 = node2;
		this.weight = weight;
	}
	
	public Edge(Node node1, Node node2)
	{
		this.node1 = node1;
		this.node2 = node2;
		weight = -1;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof Edge))
		{
			return false;
		}
		
		if (this == obj)
		{
			return true;
		}
		
		return ((Edge)obj).node1.id == this.node1.id && ((Edge)obj).node2.id == this.node2.id;
	}
}
