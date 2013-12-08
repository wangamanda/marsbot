package marsbot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

public class WorldMap 
{
	private HashMap<String, Node> nodeMap;
	private ArrayList<Edge> edgeList;
	private HashMap<String, AgentInfo> agentMap;
	private HashMap<AgentInfo, Node> allyLocations;
	private HashMap<AgentInfo, Node> enemyLocations;
	private HashMap<AgentInfo, Node> desiredLocations;
	private int lastSynch = -1; // this might need to go too
	
	public WorldMap()
	{
		nodeMap = new HashMap<String, Node>();
		edgeList = new ArrayList<Edge>();
		agentMap = new HashMap<String, AgentInfo>();
		allyLocations = new HashMap<AgentInfo, Node>();
		desiredLocations = new HashMap<AgentInfo, Node>();
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
	
	public AgentInfo add(AgentInfo a)
	{
		if (!agentMap.containsKey(a.getId()))
		{
			agentMap.put(a.getId(), a);
		}
		else
		{
			a = agentMap.get(a.getId());
		}
		return a;
	}
	
	public void setAgentLocation(String agentId, String nodeId)
	{
		
	}
	
	public void setAgentDesiredLocation(String agentId, String nodeId)
	{
		
	}
	
	public HashMap<AgentInfo, Node> getAlliedPositions()
	{
		return null;
	}
	
	public HashMap<AgentInfo, Node> getEnemyPositions()
	{
		return null;
	}
	
	public HashMap<AgentInfo, Node> getDesiredPositions()
	{
		return null;
	}
	
	// synch the desired positions with the actual positions
	// I'm not convinced we need this
	private void synch(int step)
	{
		if (lastSynch < step)
		{
			desiredLocations = new HashMap<AgentInfo, Node>();
			for (Entry<AgentInfo, Node> s : allyLocations.entrySet())
			{
				desiredLocations.put(s.getKey(), s.getValue());
			}
			lastSynch = step;
		}
	}
}
