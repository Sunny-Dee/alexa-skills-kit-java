package coffecup;

import java.util.*;

public class CoffeeCupTextUtil {
	
	private CoffeeCupTextUtil(){}
	
	private static final List<String> NAME_BLACKLIST = Arrays.asList("player", "players");
	
	public static final String COMPLETE_HELP = "Here's some things you can say. "
			+ "Add Shawn, add one cup to Saul, tell me the count, new count, reset, and exit";
	
	public static final String NEXT_HELP = "You can add cups of coffee to a player points, "
			+ "add a player, get the current coffee tally, or say help. " 
			+ "What would you like?";
	
	public static String getPlayerName(String recognizedPlayerName){
		 if (recognizedPlayerName == null || recognizedPlayerName.isEmpty()) {
	            return null;
	        }
		 
		 String cleanedName;
		
		if (recognizedPlayerName.contains(" ")){
			cleanedName = recognizedPlayerName.substring(recognizedPlayerName.indexOf(" "));
		} else {
			cleanedName = recognizedPlayerName;
		}		
		
		if(NAME_BLACKLIST.contains(cleanedName)){
			return null;
		}
		
		return cleanedName; 
	}
}
