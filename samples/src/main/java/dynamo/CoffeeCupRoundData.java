package dynamo;

import java.util.*;

/**
 * Contains player and coffee count data to represent the count so far. 
 * @author delianaescobari
 *
 */
public class CoffeeCupRoundData {
	private List<String> players;
	private Map<String, Integer> counts;
	
	public CoffeeCupRoundData(){}
	
	public static CoffeeCupRoundData newInstance(){
		CoffeeCupRoundData newInstance = new CoffeeCupRoundData();
		newInstance.setPlayers(new ArrayList<String>());
		newInstance.setCount(new HashMap<String, Integer>());
		return newInstance;
	}
	
	public List<String> getPlayers(){
		return players;
	}
	
	public void setPlayers(List<String> players){
		this.players = players;
	}
	
	public Map<String, Integer> getCounts(){
		return counts;
	}
	
	
	public void setCount(Map<String, Integer> counts){
		this.counts = counts;
	}
	
	public String toString(){
		return "[CoffeeCupRoundData players: " + players + "] counts: " + counts + "]"; 
	}

}
