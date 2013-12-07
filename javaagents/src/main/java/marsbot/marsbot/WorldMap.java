package marsbot;

import java.util.ArrayList;
import java.util.HashMap;

public class WorldMap 
{
	private HashMap<String, Node> nodeMap;
	private ArrayList<Edge> edgeList;
	
	public WorldMap()
	{
		nodeMap = new HashMap<String, Node>();
		edgeList = new ArrayList<Edge>();
	}
	
	// if the node is already in the list, the existing node will be returned
	public Node add(Node n)
	{
		if (!nodeMap.containsKey(n.getId()))
		{
			nodeMap.put(n.getId(), n);
		}
		else
		{
			n = nodeMap.get(n.getId()); // if we already know about it, get the right object
		}
		
		return n;
	}
	
	// gets the specified node out of the map
	public Node getNode(String nodeId)
	{
		if (!nodeMap.containsKey(nodeId))
		{
			return null;
		}
		else
		{
			return nodeMap.get(nodeId);
		}
	}
	
	// if the edge is already in the list, the existing edge will be returned
	public Edge add(Edge e)
	{
		if (!edgeList.contains(e))
		{
			edgeList.add(e);
		}
		else
		{
			e = edgeList.get(edgeList.indexOf(e));
		}
		return e;
	}
}
