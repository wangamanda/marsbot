package marsbot;

public class Edge
{
	private String node1;
	private String node2;
	int weight;
	
	public Edge(String node1, String node2, int weight)
	{
		this.node1 = node1;
		this.node2 = node2;
		this.weight = weight;
	}
	
	public Edge(String node1, String node2)
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
		
		return (((Edge)obj).node1.equals(this.node1) && ((Edge)obj).node2.equals(this.node2))
				|| (((Edge)obj).node1.equals(this.node2) && ((Edge)obj).node2.equals(this.node1)); // this way order doesn't matter
	}
}
