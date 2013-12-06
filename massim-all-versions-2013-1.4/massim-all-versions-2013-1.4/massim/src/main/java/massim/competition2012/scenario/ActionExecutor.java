package massim.competition2012.scenario;


import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Vector;


import massim.competition2012.GraphSimulationAgentState;
import massim.competition2012.GraphSimulationWorldState;

/**
 * This class in the one in charge of executing the agents actions.
 */
public class ActionExecutor  {
	
	private static int VALID_ACTION = 0;
	private static int WRONG_ROLE = 1;
	private static int INSUFFICIENT_RESOURCES = 2;
	private static int WRONG_STATUS = 3;
	
	private static Random random;
	
	/**
	 * Executes the actions of all agents.
	 * @param world
	 */
	public static void execute(GraphSimulationWorldState world) {
		clearFlags(world);
		executeAttacksAndParrys(world);
		executeRest(world);
		//updateStatus(world);
	}
	
	/**
	 * Clears the information about the actions executed in the previous step.
	 * Also determines random failure of actions.
	 * @param world
	 */
	protected static void clearFlags(GraphSimulationWorldState world) {
		for (GraphSimulationAgentState agentState : world.getAgents()){
			agentState.lastAction = agentState.action;
			agentState.lastActionParam = agentState.param;
			agentState.lastActionResult = "";
			agentState.attacked = false;
			if (randomFail()) {
				agentState.action = "randomFail";
				agentState.param = "";
			}
			
		}		
	}
	
	
	/**
	 * Returns true if the action should fail.
	 * @return
	 */
	private static boolean randomFail() {
		if (random == null){
			random = new Random();
		}		
		return random.nextInt(100) < 1; //probability of failure is 1%
	}

	/**
	 * Executes all the <code>attack</code> actions simultaneously, meaning that all
	 * the costs calculations to corroborate that an attack can actually be performed, are made
	 * before updating the internal states of the attacked agents.<br>
	 * <br>
	 * Parries are taking into account to determine the outcome of an attack, but the state of the parrying 
	 * agents is not update regarding the <code>parry</code> action costs
	 * @param world
	 */
	protected static void executeAttacksAndParrys(GraphSimulationWorldState world) {
		Map<GraphSimulationAgentState,Integer> damageMap = new HashMap<GraphSimulationAgentState, Integer>();
		for (GraphSimulationAgentState agentState : world.getAgents()){
			if (agentState.action != null && agentState.action.equalsIgnoreCase("attack")){
				int check = checkResources(agentState, "attack", world);
				if(check == VALID_ACTION){
					
					String param = agentState.param;						
					GraphSimulationAgentState victim = world.getAgent(param);
					
					if (victim != null){						
						if (validAttack(agentState, victim, world)){
							int checkVictim = checkResources(victim, "parry", world);
							if (!("parry".equalsIgnoreCase(victim.action)
									&& checkResources(victim, "parry", world) == VALID_ACTION)) {
								int damage = agentState.strength;
								if (damageMap.containsKey(victim)) {
									damageMap.put(victim, damageMap.get(victim)	+ damage);
								} else {
									damageMap.put(victim, damage);
								}
								agentState.lastActionResult = "successful";
								if(!victim.team.equals(agentState.team) && !(victim.health == 0)){ //make sure that disabled victims can't be exploited to get the achievement
									world.getTeamState(agentState.team).succsefullAttack(); // Increase counter for achievements
								}
							} else {
								agentState.lastActionResult = "failed_parried";
							}
							
							victim.attacked = true;							
							updateResources(agentState, "attack", world, true);
						} else {
							agentState.lastActionResult = "failed_away";
							updateResources(agentState, "attack", world, false);
						}
					} else {
						agentState.lastActionResult = "failed_wrong_param";
						updateResources(agentState, "attack", world, false);
					}
				} else {
					agentState.lastActionResult = getResultMessage(check);
					updateResources(agentState, "attack", world, false);	
				}
			}
		}
		for (GraphSimulationAgentState victim : damageMap.keySet()){		
			victim.health = Math.max(victim.health - damageMap.get(victim), 0);
		}
	}
	
	/**
	 * Returns true iff the victim is in the same node of the attacker.
	 * @param agent
	 * @param victim
	 * @param world
	 * @return
	 */
	protected static boolean validAttack(GraphSimulationAgentState agent,
			GraphSimulationAgentState victim, GraphSimulationWorldState world) {
		return victim != null && victim.node.equals(agent.node);
	}	
	
	/**
	 * Executes the rest of the actions (excluding attacks) in a random order. Random ordering can affect
	 * the outcome of the actions, for example when an agent is trying to <code>repair</code> another one
	 * that is trying to move to a different node, or when two agents in the same team
	 * attempt to <code>buy</code> and the team only has enough resources for one buy.
	 * @param world
	 */
	protected static void executeRest(GraphSimulationWorldState world) {
		
		Vector<GraphSimulationAgentState> agents = 
				new Vector<GraphSimulationAgentState>(world.getAgents());
		
		Collections.shuffle(agents);
		
		int check;
		
		for (GraphSimulationAgentState agentState : agents){
			
			
			if ( agentState.action.equalsIgnoreCase("goto")){
				try {
					GraphNode nodeTo = null;
					int edgeCost = Integer.MAX_VALUE;
					
					if (agentState.node.name.equals(agentState.param)){
						throw new Exception("Invalid Move");
					}
					for (GraphEdge edge : world.getConnectedEdges(agentState.node)){
						if( edge.node1.name.equals(agentState.param)){
							edgeCost = edge.weight;
							nodeTo = edge.node1;
							break;
						} else if(edge.node2.name.equals(agentState.param)){
							edgeCost = edge.weight;
							nodeTo = edge.node2;
							break;
						}
					}
					if (nodeTo == null){
						throw new Exception("Invalid Move");
					}
					
					check = checkResourcesGoto(agentState, edgeCost, world);
					if(check == VALID_ACTION){										
						agentState.node.agents.remove(agentState);
						agentState.setNode(nodeTo);
						nodeTo.agents.add(agentState);
						updateGotoResources(agentState, edgeCost, world, true);
					} else {
						updateGotoResources(agentState, edgeCost, world, false);				
					}
					agentState.lastActionResult = getResultMessage(check);
					
				} catch (Exception e) {
					updateGotoResources(agentState, 1, world, false);
					agentState.lastActionResult = "failed_wrong_param";
				}
			}
			else if (agentState.action.equalsIgnoreCase("parry")) {
				check = checkResources(agentState, agentState.action, world);
				if (check == VALID_ACTION){
					if (agentState.attacked){
						agentState.lastActionResult = "successful";
						world.getTeamState(agentState.team).succsefullParry();
					} else {
						agentState.lastActionResult = "useless";
					}
					updateResources(agentState, agentState.action, world, true);
				} else {
					agentState.lastActionResult = getResultMessage(check);
					updateResources(agentState, agentState.action, world, false);
				}
			}
			else if (agentState.action.equalsIgnoreCase("probe") || agentState.action.equalsIgnoreCase("survey")
					|| agentState.action.equalsIgnoreCase("inspect")) {
				check = checkResources(agentState, agentState.action, world);
				if ((check == VALID_ACTION) && !agentState.attacked){
					agentState.lastActionResult = "successful";
					updateResources(agentState, agentState.action, world, true);

					if (agentState.action.equalsIgnoreCase("probe")){
						world.getTeamState(agentState.team).addProbedNodes(agentState.node);
					} else if (agentState.action.equalsIgnoreCase("survey")){
						List<GraphEdge> connectedEdges = world.getConnectedEdges(agentState.node);
						for (GraphEdge edge : connectedEdges) {
							world.getTeamState(agentState.team).addSurveyedEdge(edge);
						}
					} else if (agentState.action.equalsIgnoreCase("inspect")){
						for (GraphSimulationAgentState inspectedAgent : agentState.node.agents) {
							if (!agentState.team.equals(inspectedAgent.team)) {
								world.getTeamState(agentState.team).addInspectedAgent(inspectedAgent);

							}
						}
						for (GraphNode neighborNode: world.getNeighborNodes(agentState.node)){
							for (GraphSimulationAgentState inspectedAgent : neighborNode.agents) {
								if (!agentState.team.equals(inspectedAgent.team)) {
									world.getTeamState(agentState.team).addInspectedAgent(inspectedAgent);									
								}
							}
						}
					}
				} else {
					if (agentState.attacked){
						agentState.lastActionResult = "failed_attacked";
					} else {
						agentState.lastActionResult = getResultMessage(check);
					}
					updateResources(agentState, agentState.action, world, false);
				}
			}
			else if (agentState.action.equalsIgnoreCase("buy")) {
				check = checkResources(agentState, agentState.action, world);
				if (check == VALID_ACTION){
					if ("battery".equalsIgnoreCase(agentState.param) 
							&& agentState.maxEnergy + world.getConfig().getRoleConf(agentState.roleName).rateBuyEnergy <= 
							   world.getConfig().getRoleConf(agentState.roleName).maxBuyEnergy ){
						
						agentState.maxEnergy += world.getConfig().getRoleConf(agentState.roleName).rateBuyEnergy;
						agentState.maxEnergyDisabled += world.getConfig().getRoleConf(agentState.roleName).rateBuyEnergyDisabled;
						if (agentState.health > 0){
							agentState.energy += world.getConfig().getRoleConf(agentState.roleName).rateBuyEnergy;;
						} else {
							agentState.energy += world.getConfig().getRoleConf(agentState.roleName).rateBuyEnergyDisabled;
						}						
						agentState.lastActionResult = "successful";
						updateResources(agentState, agentState.action, world, true);
					}
					else if ("sensor".equalsIgnoreCase(agentState.param)
							&& agentState.visRange + world.getConfig().getRoleConf(agentState.roleName).rateBuyVisRange <= 
							   world.getConfig().getRoleConf(agentState.roleName).maxBuyVisRange ){
						
						agentState.visRange += world.getConfig().getRoleConf(agentState.roleName).rateBuyVisRange;
						agentState.lastActionResult = "successful";
						updateResources(agentState, agentState.action, world, true);
					}
					else if ("shield".equalsIgnoreCase(agentState.param)
							&& agentState.maxHealth + world.getConfig().getRoleConf(agentState.roleName).rateBuyHealth <= 
							   world.getConfig().getRoleConf(agentState.roleName).maxBuyHealth ){
						
						agentState.maxHealth += world.getConfig().getRoleConf(agentState.roleName).rateBuyHealth;
						if (agentState.health > 0){
							agentState.health += world.getConfig().getRoleConf(agentState.roleName).rateBuyHealth;
						}
						agentState.lastActionResult = "successful";
						updateResources(agentState, agentState.action, world, true);
					}
					else if ("sabotageDevice".equalsIgnoreCase(agentState.param)
							&& agentState.strength + world.getConfig().getRoleConf(agentState.roleName).rateBuyStrength <= 
							   world.getConfig().getRoleConf(agentState.roleName).maxBuyStrength ){
						
						agentState.strength += world.getConfig().getRoleConf(agentState.roleName).rateBuyStrength;
						agentState.lastActionResult = "successful";
						updateResources(agentState, agentState.action, world, true);
					}
					else {
						if ("battery".equalsIgnoreCase(agentState.param) || "sensor".equalsIgnoreCase(agentState.param)
								|| "shield".equalsIgnoreCase(agentState.param) || "sabotageDevice".equalsIgnoreCase(agentState.param)){
							agentState.lastActionResult = "failed_limit";
						} else {
							agentState.lastActionResult = "failed_wrong_param";
						}
						updateResources(agentState, agentState.action, world, false);
						
					}
				} else {
					agentState.lastActionResult = getResultMessage(check);
					updateResources(agentState, agentState.action, world, false);
				}
			}
			else if (agentState.action.equalsIgnoreCase("repair")) {
				check = checkResources(agentState, agentState.action, world);
				if (check == VALID_ACTION){
					try{
						GraphSimulationAgentState receiver = world.getAgent(agentState.param);
						if (receiver != null && !agentState.equals(receiver)
								&& receiver.node.equals(agentState.node)){
							
							// TODO maybe define how much health to recover in config.					
							receiver.health = receiver.maxHealth;
							
							agentState.lastActionResult = "successful";
							updateResources(agentState, agentState.action, world, true);							
						} else {
							if (receiver != null && !receiver.node.equals(agentState.node)) {
								agentState.lastActionResult = "failed_away";
							} else {
								agentState.lastActionResult = "failed_wrong_param";
							}
							updateResources(agentState, agentState.action, world, false);
						}						
					}
					catch (Exception e) {
						agentState.lastActionResult = "failed_wrong_param";
						updateResources(agentState, agentState.action, world, false);
					}
				} else {
					agentState.lastActionResult = getResultMessage(check);
					updateResources(agentState, agentState.action, world, false);
				}
			}
			else if (agentState.action.equalsIgnoreCase("recharge")) {
				updateResourcesRecharge(agentState, world, !agentState.attacked);
				agentState.lastAction = "recharge";
				agentState.lastActionResult = agentState.attacked?"failed_attacked":"successful";
			}
			else if ("randomFail".equalsIgnoreCase(agentState.action)){
				agentState.lastActionResult = "failed_random";				
			}
			else if ("skip".equalsIgnoreCase(agentState.action)){
				agentState.lastActionResult = "successful";				
			}
			else if ("noAction".equalsIgnoreCase(agentState.action)){
				agentState.lastActionResult = "failed";				
			}
			else if (!"attack".equalsIgnoreCase(agentState.action)){
				agentState.lastAction = "unknownAction";
				agentState.lastActionResult = "failed";
			}
			agentState.action = "";
			agentState.param = "";
		}
	}
	

	
	private static String getResultMessage(int code) {
		if (code == VALID_ACTION){
			return "successful";
		} else if (code == INSUFFICIENT_RESOURCES){
			return  "failed_resources";
		} else if (code == WRONG_ROLE){
			return "failed_role";
		} else if (code == WRONG_STATUS){
			return "failed_status";
		}
		return null;
	}

	/**
	 * Updates the internal status of an agent after performing an action.
	 * @param agent
	 * @param action
	 * @param world
	 * @param successful
	 */
	private static void updateResources(GraphSimulationAgentState agent,
			String action, GraphSimulationWorldState world, boolean successful) {
		ActionConfiguration ac = world.getConfig().getActionConf(action);
		if (successful){
			if (agent.health > 0){				
				agent.energy -= ac.energyCost;
				agent.health -= ac.healthCost;
				world.getTeamState(agent.team).currAchievementPoints -= ac.pointsCost;
				world.getTeamState(agent.team).usedAchievementPoints += ac.pointsCost;
				
				if (agent.energy > agent.maxEnergy) { 
					agent.energy = agent.maxEnergy ;
				}		
			} else {
				agent.energy -= ac.energyCostDisabled;
				agent.health -= ac.healthCostDisabled;
				world.getTeamState(agent.team).currAchievementPoints -= ac.pointsCostDisabled;
				world.getTeamState(agent.team).usedAchievementPoints += ac.pointsCostDisabled;
				
				if (agent.energy > agent.maxEnergyDisabled) { 
					agent.energy = agent.maxEnergyDisabled ;
				}
			}
		} else {
			
			if (agent.health > 0){
				agent.energy -= ac.energyCostFailed;
				agent.health -= ac.healthCostFailed;
				world.getTeamState(agent.team).currAchievementPoints -= ac.pointsCostFailed;
				world.getTeamState(agent.team).usedAchievementPoints += ac.pointsCostFailed;
				
				if (agent.energy > agent.maxEnergy) { 
					agent.energy = agent.maxEnergy ;
				}						
			} else {
				
				agent.energy -= ac.energyCostFailedDisabled;
				agent.health -= ac.healthCostFailedDisabled;
				world.getTeamState(agent.team).currAchievementPoints -= ac.pointsCostFailedDisabled;
				world.getTeamState(agent.team).usedAchievementPoints += ac.pointsCostFailedDisabled;
				if (agent.energy > agent.maxEnergyDisabled) { 
					agent.energy = agent.maxEnergyDisabled ;
				}
			}	
			
		}
		if (agent.health > agent.maxHealth) { 
			agent.health = agent.maxHealth ;
		}
		if (agent.energy < 0) { 
			agent.energy = 0 ;
		}
		if (agent.health < 0) { 
			agent.health = 0 ;
		}
		
	}
	
	/**
	 * Updates the internal status of an agent after performing the <code>recharge</code> action.
	 * @param agent
	 * @param world
	 * @param successful
	 */
	private static void updateResourcesRecharge(GraphSimulationAgentState agent,
			GraphSimulationWorldState world, boolean successful) {
		ActionConfiguration ac = world.getConfig().getActionConf("recharge");		
		if (agent.health > 0){
			if (successful){
				agent.energy += Math.round( ((float)(ac.energyCost * agent.maxEnergy))/ 100f );
				agent.health += Math.round( ((float)(ac.healthCost * agent.maxHealth))/ 100f );
			} else {
				agent.energy += Math.round( ((float)(ac.energyCostFailed * agent.maxEnergy))/ 100f );
				agent.health += Math.round( ((float)(ac.healthCostFailed * agent.maxHealth))/ 100f );
			}
			if (agent.energy > agent.maxEnergy) { 
				agent.energy = agent.maxEnergy ;
			}
		} else {
			if (successful){
				agent.energy += Math.round( ((float)(ac.energyCostDisabled * agent.maxEnergyDisabled))/ 100f );
			} else {
				agent.energy += Math.round( ((float)(ac.energyCostFailedDisabled * agent.maxEnergyDisabled))/ 100f );
			}			
			if (agent.energy > agent.maxEnergyDisabled) { 
				agent.energy = agent.maxEnergyDisabled ;
			}
		}
		if (agent.health > agent.maxHealth) { 
			agent.health = agent.maxHealth ;
		}
		if (agent.energy < 0) { 
			agent.energy = 0 ;
		}
		if (agent.health < 0) { 
			agent.health = 0 ;
		}
	}
	
	/**
	 * Updates the internal status of an agent after performing the <code>goto</code> action.
	 * @param agent
	 * @param action
	 * @param world
	 * @param successful
	 */
	private static void updateGotoResources(GraphSimulationAgentState agent,
			int edgeCost, GraphSimulationWorldState world, boolean successful) {
		ActionConfiguration ac = world.getConfig().getActionConf("goto");
		if (agent.health > 0){
			if (successful){
				agent.energy -= ac.energyCost * edgeCost;
				agent.health -= ac.healthCost;
				world.getTeamState(agent.team).currAchievementPoints -= ac.pointsCost;
				world.getTeamState(agent.team).usedAchievementPoints += ac.pointsCost;
			} else {
				agent.energy -= ac.energyCostFailed;
				agent.health -= ac.healthCostFailed;
				world.getTeamState(agent.team).currAchievementPoints -= ac.pointsCostFailed;
				world.getTeamState(agent.team).usedAchievementPoints += ac.pointsCostFailed;
			}
			if (agent.energy > agent.maxEnergy) {
				agent.energy = agent.maxEnergy ;
			}
		} else {
			if (successful){
				agent.energy -= ac.energyCostDisabled * edgeCost;
				agent.health -= ac.healthCostDisabled;
				world.getTeamState(agent.team).currAchievementPoints -= ac.pointsCostDisabled;
				world.getTeamState(agent.team).usedAchievementPoints += ac.pointsCostDisabled;
			} else {
				agent.energy -= ac.energyCostFailedDisabled;
				agent.health -= ac.healthCostFailedDisabled;
				world.getTeamState(agent.team).currAchievementPoints -= ac.pointsCostFailedDisabled;
				world.getTeamState(agent.team).usedAchievementPoints += ac.pointsCostFailedDisabled;
			}
			if (agent.energy > agent.maxEnergyDisabled) {
				agent.energy = agent.maxEnergyDisabled ;
			}
		}
		if (agent.health > agent.maxHealth) {
			agent.health = agent.maxHealth ;
		}
		if (agent.energy < 0) {
			agent.energy = 0 ;
		}
		if (agent.health < 0) {
			agent.health = 0 ;
		}
	}
	
	
	
	

	
	
	/**
	 * Checks if the <code>agent</code> can perform the <code>action</code> according to its role and current status
	 * @param agent
	 * @param action
	 * @param world
	 * @return ACTION_ALLOWED if the action is allowed, WRONG_ROLE if action is not part of the role, 
	 * UNSUFFICIENT_RESOURCES if resources are unsufficient, WRONG_STATUS if action belongs to role when enabled.
	 */	
	private static int checkResources(GraphSimulationAgentState agent,
			String action, GraphSimulationWorldState world) {
		if (agent.health > 0){		
			if (!world.getConfig().getRoleConf(agent.roleName).actions.contains(action)){
				return WRONG_ROLE;
			}
			ActionConfiguration ac = world.getConfig().getActionConf(action);
			if (((agent.energy >= ac.energyCost)
					&& (agent.health >= ac.healthCost)
					&& (world.getTeamState(agent.team).currAchievementPoints >= ac.pointsCost))){
				return VALID_ACTION;
			} else {
				return INSUFFICIENT_RESOURCES;
			}
			
		} else {
			if (!world.getConfig().getRoleConf(agent.roleName).actionsDisable.contains(action)){
				if (!world.getConfig().getRoleConf(agent.roleName).actions.contains(action)){
					return WRONG_ROLE;
				} else {
					return WRONG_STATUS;
				}
			}
			ActionConfiguration ac = world.getConfig().getActionConf(action);
			if ((agent.energy >= ac.energyCostDisabled)
				 && (agent.health >= ac.healthCostDisabled)
				 && (world.getTeamState(agent.team).currAchievementPoints >= ac.pointsCostDisabled)){
				return VALID_ACTION;
			} else {
				return INSUFFICIENT_RESOURCES;
			}
		}
	}
	
	/**
	 * Checks if the agent can perform the <code>goto</code> action according to its role and current status
	 * @param agent
	 * @param action
	 * @param world
	 * @return
	 */
	private static int checkResourcesGoto(GraphSimulationAgentState agent,
			int edgeCost, GraphSimulationWorldState world) {
		if (agent.health > 0){
			if (!world.getConfig().getRoleConf(agent.roleName).actions.contains("goto")){
				return WRONG_ROLE;
			}
			ActionConfiguration ac = world.getConfig().getActionConf("goto");
			if ((agent.energy >= ac.energyCost * edgeCost)
					&& (agent.health >= ac.healthCost)
					&& (world.getTeamState(agent.team).currAchievementPoints >= ac.pointsCost)){
				return VALID_ACTION;
			} else {
				return INSUFFICIENT_RESOURCES;
			}
		} else {
			if (!world.getConfig().getRoleConf(agent.roleName).actionsDisable.contains("goto")){
				if (!world.getConfig().getRoleConf(agent.roleName).actions.contains("goto")){
					return WRONG_ROLE;
				} else {
					return WRONG_STATUS;
				}
			}
			ActionConfiguration ac = world.getConfig().getActionConf("goto");
			if ((agent.energy >= ac.energyCostDisabled * edgeCost)
					&& (agent.health >= ac.healthCostDisabled)
					&& (world.getTeamState(agent.team).currAchievementPoints >= ac.pointsCostDisabled)){
				return VALID_ACTION;
			} else {
				return INSUFFICIENT_RESOURCES;
			}
		}
	}


	

}
