package marsbot;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

import massim.javaagents.agents.MarsUtil;
import apltk.interpreter.data.LogicBelief;
import apltk.interpreter.data.LogicGoal;
import eis.iilang.Action;
import eis.iilang.Percept;

public class ExplorerAgent extends AgentWithMap {

	public ExplorerAgent(String name, String team) 
	{
		super(name, team);
	}

	@Override
	public Action step() 
	{
		handlePercepts();
		 
		Action act = planRecharge();
		
		if (act == null)
		{
			act = planNextNodeToVisit();
		}
		
		return act;
	}

	@Override
	public void handlePercept(Percept p) 
	{
		// TODO Auto-generated method stub

	}
	
	@SuppressWarnings("deprecation")
	private void handlePercepts() 
	{
		String position = null;
		
		// check percepts
		Collection<Percept> percepts = getAllPercepts();
		removeBeliefs("visibleEntity");
		removeBeliefs("visibleEdge");
		for ( Percept p : percepts ) {
			if ( p.getName().equals("step") ) {
				println(p);
			}
			else if ( p.getName().equals("visibleEdge") ) {
				LogicBelief b = MarsUtil.perceptToBelief(p);
				if (containsBelief(b) == false)
				{
					addBelief(b);
				}
				
				// add newly discovered edges and nodes to the graph
				Node node1 = new Node(p.getParameters().get(0).toString());
				Node node2 = new Node(p.getParameters().get(1).toString());
				Edge e = new Edge(node1.getId(), node2.getId());
				worldMap.add(e);
				
				// add the nodes to the world map, if they are already there, this will return the existing object
				node1 = worldMap.add(node1);
				node2 = worldMap.add(node2);
				
				// update the neighbor list of the nodes
				node1.addNeighbor(node2);
				node2.addNeighbor(node1);
			}
			else if ( p.getName().equals("position") ) 
			{
				position = p.getParameters().get(0).toString();
				removeBeliefs("position");
				addBelief(new LogicBelief("position",position));
				
				// update the visited status of this node
				Node n = worldMap.add(new Node(position));
				n.markVisited();
			}
			else if ( p.getName().equals("energy") ) {
				Integer energy = new Integer(p.getParameters().get(0).toString());
				removeBeliefs("energy");
				addBelief(new LogicBelief("energy",energy.toString()));
			}
			else if ( p.getName().equals("maxEnergy") ) {
				Integer maxEnergy = new Integer(p.getParameters().get(0).toString());
				removeBeliefs("maxEnergy");
				addBelief(new LogicBelief("maxEnergy",maxEnergy.toString()));
			}
		}
		
		// again for checking neighbors
		this.removeBeliefs("neighbor");
		for ( Percept p : percepts ) {
			if ( p.getName().equals("visibleEdge") ) {
				String vertex1 = p.getParameters().get(0).toString();
				String vertex2 = p.getParameters().get(1).toString();
				if ( vertex1.equals(position) ) 
					addBelief(new LogicBelief("neighbor",vertex2));
				if ( vertex2.equals(position) ) 
					addBelief(new LogicBelief("neighbor",vertex1));
			}
		}	
	}
	
	// recharge if necessary
	private Action planRecharge() {

		LinkedList<LogicBelief> beliefs = null;
		
		beliefs =  getAllBeliefs("energy");
		if ( beliefs.size() == 0 ) {
				println("strangely I do not know my energy");
				return MarsUtil.skipAction();
		}		
		int energy = new Integer(beliefs.getFirst().getParameters().firstElement()).intValue();

		beliefs =  getAllBeliefs("maxEnergy");
		if ( beliefs.size() == 0 ) {
				println("strangely I do not know my maxEnergy");
				return MarsUtil.skipAction();
		}		
		int maxEnergy = new Integer(beliefs.getFirst().getParameters().firstElement()).intValue();

		// if has the goal of being recharged...
		if ( goals.contains(new LogicGoal("beAtFullCharge")) ) {
			if ( maxEnergy == energy ) {
				println("I can stop recharging. I am at full charge");
				removeGoals("beAtFullCharge");
			}
			else {
				println("recharging...");
				return MarsUtil.rechargeAction();
			}
		}
		// go to recharge mode if necessary
		else {
			if ( energy < maxEnergy / 3 ) {
				println("I need to recharge");
				goals.add(new LogicGoal("beAtFullCharge"));
				return MarsUtil.rechargeAction();
			}
		}	
		
		return null;
		
	}
	
	// Get the next node to visit
	private Action planNextNodeToVisit() 
	{
		LinkedList<LogicBelief> beliefs = null;
		
		beliefs =  getAllBeliefs("position");
		if ( beliefs.size() == 0 ) {
				println("strangely I do not know my position");
				return MarsUtil.skipAction();
		}
		String position = beliefs.getFirst().getParameters().firstElement();
		
		Node currentNode = worldMap.getNode(position);
		for (Node n : currentNode.getNeighbors())
		{
			if (!n.getVisited())
			{
				return MarsUtil.gotoAction(n.getId());
			}
		}

		return MarsUtil.skipAction();
		
	}

}
