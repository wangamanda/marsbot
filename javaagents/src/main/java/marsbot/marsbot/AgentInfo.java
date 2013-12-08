package marsbot;

public class AgentInfo 
{
	private String id;
	public boolean disabled;
	private Teams team;
	
	public AgentInfo(String id, boolean disabled, Teams team)
	{
		this.id = id;
		this.disabled = disabled;
		this.team = team;
	}
	
	public String getId()
	{
		return id;
	}
	
	public Teams getTeam()
	{
		return team;
	}
	
}
