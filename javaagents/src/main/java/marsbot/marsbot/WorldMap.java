package marsbot;

import java.util.HashMap;

public class WorldMap 
{
	public HashMap<Integer, Node> nodeMap;
	public HashMap<Edge, Integer> edgeMap;
	
	public WorldMap()
	{
		nodeMap = new HashMap<Integer, Node>();
		edgeMap = new HashMap<Edge, Integer>();
	}
}
