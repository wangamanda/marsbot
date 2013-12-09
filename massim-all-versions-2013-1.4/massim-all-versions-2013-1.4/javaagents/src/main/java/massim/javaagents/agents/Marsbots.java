package massim.javaagents.agents;

import eis.iilang.Action;
import eis.iilang.Percept;
import massim.javaagents.Agent;
import massim.javaagents.agents.MarsUtil;

public class Marsbots extends Agent{

	public Marsbots(String name, String team) {
		super(name, team);
		System.out.print("************************");
		// TODO Auto-generated constructor stub
	}

	@Override
	public Action step() {
		// TODO Auto-generated method stub
		System.out.print("************************");
		return MarsUtil.skipAction();
	}

	@Override
	public void handlePercept(Percept p) {
		System.out.print("************************");
		// TODO Auto-generated method stub
		
	}
	
}