package marsbot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

public class Utility {
	
	//returns the end-of-round score value for a given team and a given arrangement of friendly units.
	public int expectedStepScore(HashMap<AgentInfo, Node> allyLocations, Teams team){
		HashMap<String, Teams> control = null;
		phase1();
		phase2();
		phase3();
		
		
		return calculateValue(control);
	}
	
	//calculates which nodes are dominated. A team with a majority of agents at a node dominates that node.
	private void phase1(){
		
	}
	
	//
	private void phase2(){
		
	}
	
	private void phase3(){
		
	}
	
	private int calculateValue(HashMap<String, Teams> control){
		return 0;
	}
	
}
