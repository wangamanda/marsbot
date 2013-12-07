package marsbot;

import massim.javaagents.Agent;

public abstract class AgentWithMap extends Agent 
{
	static WorldMap worldMap; 
	public AgentWithMap(String name, String team) 
	{
		super(name, team);
		if (worldMap == null)
		{
			worldMap = new WorldMap();
		}
	}

}
