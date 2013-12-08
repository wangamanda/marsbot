package marsbot;

import massim.javaagents.Agent;

public abstract class AgentWithMap extends Agent 
{
	static WorldMap worldMap; 
	protected int step = 0;
	protected String name;
	protected String team;
	
	public AgentWithMap(String name, String team) 
	{
		super(name, team);
		if (worldMap == null)
		{
			worldMap = new WorldMap();
		}
		this.name = name;
		this.team = team;
	}

}
