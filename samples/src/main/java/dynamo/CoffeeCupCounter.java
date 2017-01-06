package dynamo;

import java.util.Map;

import com.amazon.speech.speechlet.Session;

/**
 * Represents a cup score count round 
 * @author delianaescobari
 *
 */

public class CoffeeCupCounter { //TODO equivalent too ScoreKeeperGame.java 
	private Session session;
	private CoffeeCupRoundData roundData ;
	
	private CoffeeCupCounter(){};
	
	public static CoffeeCupCounter newInstance(Session session, CoffeeCupRoundData roundData){
		CoffeeCupCounter round = new CoffeeCupCounter();
		round.setSession(session);
		round.setRoundData(roundData);
		return round;
	}
	
	protected void setSession(Session session){
		this.session = session;
	}
	
	protected Session getSession(){
		return session;
	}
	
	protected CoffeeCupRoundData getRoundData(){
		return roundData;
	}
	
	protected void setRoundData(CoffeeCupRoundData roundData){
		this.roundData = roundData;
	}
	
	public boolean hasPlayers(){
		return !roundData.getPlayers().isEmpty();
	}
	
	public int getNumberOfPlayers(){
		return roundData.getPlayers().size();
	}
	
	public void addPlayer(String playerName){
		roundData.getPlayers().add(playerName);
	}
	
	public boolean hasPlayer(String playerName){
		return roundData.getPlayers().contains(playerName);
	}
	
	public boolean hasScores(){
		return !roundData.getCounts().isEmpty();
	}
	
	public int getCountForPlayer(String playerName){
		return roundData.getCounts().get(playerName);
	}
	
	public boolean addCountForPlayer(String playerName, int count){
		if(!hasPlayer(playerName)){
			return false;
		}
		
		int currentCount = 0;
		if (roundData.getCounts().containsKey(playerName)){
			currentCount = roundData.getCounts().get(playerName);
		}
		
		roundData.getCounts().put(playerName, currentCount + count);
		return true;
	}
	
	/**
	 * resets coffee count for everyone
	 */
	public void resetCounts(){
		for (String playerName : roundData.getPlayers()){
			roundData.getCounts().put(playerName, 0);
		}
	}
	
	public Map<String, Integer> getAllCounts(){
		for (String playerName : roundData.getPlayers()){
			if (!roundData.getCounts().containsKey(playerName)){
				roundData.getCounts().put(playerName, 0);
			}
		}
		
		return roundData.getCounts();
	}
}
