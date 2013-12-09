package marsbot;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Vector;

import apltk.interpreter.data.LogicBelief;
import apltk.interpreter.data.LogicGoal;
import apltk.interpreter.data.Message;
import eis.iilang.Action;
import eis.iilang.Percept;
import massim.javaagents.Agent;
import massim.javaagents.agents.MarsUtil;

public class InspectorAgent extends AgentWithMap{

	public InspectorAgent(String name, String team) {
		super(name, team);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Action step() {
		// TODO Auto-generated method stub
		handleMessages();
		handlePercepts();

		Action act = null;

		// 1. recharging
		act = planRecharge();
		if ( act != null ) return act;
		
		// 2 buying battery
		act = planBuyBattery();
		if ( act != null ) return act;

		// 3. inspecting if necessary
		act = planInspect();
		if ( act != null ) return act;
		
		// 4. (almost) random walking
		act = planWalk();
		if ( act != null ) return act;

		return MarsUtil.skipAction();
	}

	@Override
	public void handlePercept(Percept p) {
		// TODO Auto-generated method stub
		
	}
	
private void handleMessages() {
		
		// handle messages... believe everything the others say
		Collection<Message> messages = getMessages();
		for ( Message msg : messages ) {
			;
		}
		
	}

	private void handlePercepts() {

		String position = null;
		//Vector<String> neighbors = new Vector<String>();
		
		// check percepts
		Collection<Percept> percepts = getAllPercepts();
		//if ( gatherSpecimens ) processSpecimens(percepts);
		removeBeliefs("visibleEntity");
		removeBeliefs("visibleEdge");
		for ( Percept p : percepts ) {
			if ( p.getName().equals("step") ) {
				println(p);
			}
			else if ( p.getName().equals("visibleEntity") ) {
				LogicBelief b = MarsUtil.perceptToBelief(p);
				if ( containsBelief(b) == false ) {
					println("I perceive an entity I have not known before");
					addBelief(b);
					broadcastBelief(b);
				}
				else {
					println("I already knew entity:" + b);
				}
			}
			else if ( p.getName().equals("visibleEdge") ) 
			{
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
			else if ( p.getName().equals("probedVertex") ) {
				LogicBelief b = MarsUtil.perceptToBelief(p);
				if ( containsBelief(b) == false ) {
					println("I perceive the value of a vertex that I have not known before");
					addBelief(b);
					broadcastBelief(b);
				}
				else {
					//println("I already knew " + b);
				}
			}
			else if ( p.getName().equals("surveyedEdge") ) {
				LogicBelief b = MarsUtil.perceptToBelief(p);
				if ( containsBelief(b) == false ) {
					println("I perceive the weight of an edge that I have not known before");
					addBelief(b);
					broadcastBelief(b);
				}
				else {
					//println("I already knew " + b);
				}
			}
			else if ( p.getName().equals("inspectedEntity") ) {
				println("I have perceived an inspected entity " + p);
			}
			else if ( p.getName().equals("health")) {
				Integer health = new Integer(p.getParameters().get(0).toString());
				println("my health is " +health );
				if ( health.intValue() == 0 ) {
					println("my health is zero. asking for help");
					broadcastBelief(new LogicBelief("iAmDisabled"));
				}
			}
			else if ( p.getName().equals("position") ) {
				position = p.getParameters().get(0).toString();
				removeBeliefs("position");
				addBelief(new LogicBelief("position",position));
				// update the visited status of this node
				Node n = worldMap.add(new Node(position));
				n.markVisited();
				// TODO: update location map
				worldMap.setAgentLocation(name, n.getId());
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
			else if ( p.getName().equals("money") ) {
				Integer money = new Integer(p.getParameters().get(0).toString());
				removeBeliefs("money");
				addBelief(new LogicBelief("money",money.toString()));
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

	
	/**
	 * Buy a battery with a given probability
	 * @return
	 */
	private Action planBuyBattery() {
		
		LinkedList<LogicBelief> beliefs = this.getAllBeliefs("money");
		if ( beliefs.size() == 0 ) {
			println("strangely I do not know our money.");
			return null;
		}
		
		LogicBelief moneyBelief = beliefs.get(0);
		int money = new Integer(moneyBelief.getParameters().get(0)).intValue();
		
		if ( money < 10 ) {
			println("we do not have enough money.");
			return null;
		}
		println("we do have enough money.");
		
		//double r = Math.random();
		//if ( r > 0.1 ) {
		//	println("I am not going to buy a battery");
		//	return null;
		//}
		println("I am going to buy a battery");
		
		return MarsUtil.buyAction("battery");
		
	}
	
	private Action planInspect() {

		LinkedList<LogicBelief> beliefs = null;

		// determine adjacent vertices including the current position
		Vector<String> vertices = new Vector<String>();
		beliefs =  getAllBeliefs("position");
		String position = beliefs.getFirst().getParameters().firstElement();
		vertices.add(position);
		beliefs = getAllBeliefs("neighbor");
		for ( LogicBelief b : beliefs ) {
			vertices.add(b.getParameters().firstElement());
		}
		
		int adjacentNum = 0;
		
		String myTeam = getTeam();
		
		LinkedList<LogicBelief> visible = getAllBeliefs("visibleEntity");
		for ( LogicBelief v : visible ) {
		
			String pos = v.getParameters().get(1);
			String team = v.getParameters().get(2);
			
			// ignore same team
			if ( myTeam.equals(team) ) continue;
			
			// not adjacent
			if ( vertices.contains(pos) == false ) continue;
			adjacentNum ++;
						
		}

		if ( adjacentNum == 0 ) {
			println("there are no opponents to inspect");
			return null;
		}
		
		println("there are " + adjacentNum + " visible opponents that I could inspect");
		
		if ( Math.random() < 0.5 ) {
			println("I will inspect");
			return MarsUtil.inspectAction();
		}

		println("I won't inspect");
		return null;
		
	}

	private Action planWalk() {

		LinkedList<LogicBelief> beliefs = getAllBeliefs("neighbor");
		Vector<String> neighbors = new Vector<String>();
		for ( LogicBelief b : beliefs ) {
			neighbors.add(b.getParameters().firstElement());
		}
		for (String ne : neighbors){
			Node n = worldMap.getNode(ne);
			if ( n == null){
				
				return MarsUtil.gotoAction(ne);
			}
			
		}
		
		if ( neighbors.size() == 0 ) {
			println("strangely I do not know any neighbors");
			return MarsUtil.skipAction();
		}
		
		// goto neighbors
		Collections.shuffle(neighbors);
		String neighbor = neighbors.firstElement();
		println("I will go to " + neighbor);
		return MarsUtil.gotoAction(neighbor);
		
	}

}