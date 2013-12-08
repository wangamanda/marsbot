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
	
	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof AgentInfo))
		{
			return false;
		}
		
		if (this == obj)
		{
			return true;
		}
		
		return ((AgentInfo)obj).id.equals(this.id);
	}
	
}
