package coffecup;

import java.util.Map;
import java.util.Map.Entry;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.LaunchRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.Card;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.Reprompt;
import com.amazon.speech.ui.SimpleCard;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;

import dynamo.*;

public class CoffeeManager {
	private static final String SLOT_PLAYER_NAME = "PlayerName";
	private static final String SLOT_CUP_NUMBER = "NumberOfCups";
	private static final int MAX_PLAYERS_FOR_SPEECH = 10; //TODO maybe we don't need this. 
	
	private final CoffeeCupDao coffeeCupDao;
	
	public CoffeeManager(final AmazonDynamoDBClient amazonDynamoDbClient){
		CoffeeCupDynamoDbClient dynamoDbClient = 
				new CoffeeCupDynamoDbClient(amazonDynamoDbClient);
		coffeeCupDao = new CoffeeCupDao(dynamoDbClient); 
	}
	
	public SpeechletResponse getLaunchResponse(LaunchRequest request, Session session){
		String speechText, repromptText;
		CoffeeCupCounter counter = coffeeCupDao.getCoffeeCounter(session);
		
		if(counter == null || !counter.hasPlayers()){
			speechText = "Coffee cup counter, let's start a new count. "
					+ "Who's going to be counting how much coffee they had today?";
			repromptText = "Please tell me the name of whom you want keep track "
					+ "coffee consumption for.";
		} else {
			speechText = "This is your coffee cup counter, what can I do for you?";
			repromptText = CoffeeCupTextUtil.NEXT_HELP;
		}
		
		return getAskSpeechletResponse(speechText, repromptText);
	}
	
	public SpeechletResponse getNewRoundIntentResponse(Session session, SkillContext skillContext){
		CoffeeCupCounter counter = coffeeCupDao.getCoffeeCounter(session);
		
		if(counter == null){
			return getAskSpeecletResponse("New round started. Who's your first player?", 
					"Please tell me who\'s your first player?");
		}
		
		counter.resetCounts();
		coffeeCupDao.saveCoffeeCupCounter(counter);
		
		String speechText = "New round started with " + counter.getNumberOfPlayers() + " existing player"
				+ (counter.getNumberOfPlayers() != 1 ? "" : "s") + ".";
		
		if(skillContext.needsMoreHelp()){
			String repromptText = "You can add the coffee count for an existing player, "
					+ "add another player, reset all players, or exit." +
					" What would you like?";
			speechText += repromptText;
			return getAskSpeechletResponse(speechText, repromptText);
		} else {
			return getTellSpeechletResponse(speechText);
		}
	}
	
	private SpeechletResponse getAskSpeecletResponse(String string, String string2) {
		// TODO Auto-generated method stub
		return null;
	}

	public SpeechletResponse getAddPlayerIntentResponse(Intent intent, Session session, 
			SkillContext skillContext){
		String newPlayerName = 
				CoffeeCupTextUtil.getPlayerName(intent.getSlot(SLOT_PLAYER_NAME).getValue());
		if (newPlayerName == null){
			String speechText = "OK. Who do you want to add?";
			return getAskSpeechletResponse(speechText, speechText);
		}
		
		//Load the previous round
		CoffeeCupCounter counter = coffeeCupDao.getCoffeeCounter(session);
		if (counter == null){
			counter = CoffeeCupCounter.newInstance(session, CoffeeCupRoundData.newInstance());
		}
		
		counter.addPlayer(newPlayerName);
		
		//Save updated round
		coffeeCupDao.saveCoffeeCupCounter(counter);
		
		String speechText = newPlayerName + " has been added to the tracker. ";
		String repromptText = null;
		
		if (skillContext.needsMoreHelp()){
			if (counter.getNumberOfPlayers() == 1){
				speechText += "You can say, I am done adding players. Now who's your next player?";
			} else {
				speechText += "Who is your next player?";
			}
			repromptText = CoffeeCupTextUtil.NEXT_HELP; 
		}
		
		if (repromptText != null){
			return getAskSpeechletResponse(speechText, repromptText);
		} else {
			return getTellSpeechletResponse(speechText);
		}
	}
	
	public SpeechletResponse getAddCupIntentResponse(Intent intent, Session session, 
			SkillContext skillContext){
		String playerName = CoffeeCupTextUtil.getPlayerName(intent.getSlot(SLOT_PLAYER_NAME).getValue());
		
		if(playerName == null){
			String speechText = "Sorry , I did not get the player name. Please say it again.";
			return getAskSpeechletResponse(speechText, speechText);
		}
		
		int cups = 0; 
		try {
			cups = Integer.parseInt(intent.getSlot(SLOT_CUP_NUMBER).getValue());
		} catch (NumberFormatException e) {
			String speechText = "Sorry, I did not hear that. How many cups you want to add?";
			return getAskSpeechletResponse(speechText, speechText);
		}
		
		CoffeeCupCounter counter = coffeeCupDao.getCoffeeCounter(session);
		if (counter == null){
			return getTellSpeechletResponse("A counter has not been started. Please say New Count to "
					+ "start a new coffee count before adding the cups.");
		}
		
		if (counter.getNumberOfPlayers() == 0){
			String speechText = "I'm not tracking coffee for anyone yet. Should I start a tally?";
			String cardText = "Say, start a new tally.";
			return getAskSpeechletResponse(speechText, cardText);
					
		}
		
		if (!counter.addCountForPlayer(playerName, cups)) {
			String speechText = "Sorry, " + playerName + " has not joined the game. What else?";
			return getAskSpeechletResponse(speechText, speechText);
		}
		
		coffeeCupDao.saveCoffeeCupCounter(counter);
		
		String speechText = playerName + " had " + cups + " cup" 
		+ (cups > 1 ? "s" : "") + " of coffee so far.";
		if(counter.getNumberOfPlayers() > MAX_PLAYERS_FOR_SPEECH){
			speechText += playerName + " had " + counter.getCountForPlayer(playerName)
			+ " in total.";
		} else {
			speechText += getAllScoresAsSpeechText(counter.getAllCounts());
		}
		
		return getTellSpeechletResponse(speechText);
	}
	
	public SpeechletResponse getTellCountIntentResponse(Intent intent, Session session){
		CoffeeCupCounter counter = coffeeCupDao.getCoffeeCounter(session);
		
		if (counter == null || !counter.hasPlayers()){
			return getTellSpeechletResponse("Nobody tracking their coffee yet.");
		}
		
		Map<String, Integer> tally = counter.getAllCounts();
		String speechText = getAllScoresAsSpeechText(tally);
		Card coffeeDrinkerCard = getCoffeeScoreCard(tally);
		
		PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
		speech.setText(speechText);
		
		return SpeechletResponse.newTellResponse(speech, coffeeDrinkerCard);
	}
	
	public SpeechletResponse getResetPlayersIntentResponse(Intent intent, Session session){
		CoffeeCupCounter counter = CoffeeCupCounter.newInstance(session, CoffeeCupRoundData.newInstance());
		coffeeCupDao.saveCoffeeCupCounter(counter);
		
		String speechText = "New tally started. Who do you want to add first?";
		return getAskSpeechletResponse(speechText, speechText);
	}
	
	public SpeechletResponse getHelpIntentResponse(Intent intent, Session session, 
			SkillContext skillcontext){
		return skillcontext.needsMoreHelp() ? getAskSpeechletResponse(
				CoffeeCupTextUtil.COMPLETE_HELP + " So, how can I help you?", 
				CoffeeCupTextUtil.NEXT_HELP) 
				: getTellSpeechletResponse(CoffeeCupTextUtil.COMPLETE_HELP);
	}
	
	public SpeechletResponse getExitIntentResponse(Intent intent, Session session, 
			SkillContext skillContext){
		return skillContext.needsMoreHelp() ? getTellSpeechletResponse("Okay. Whenever you're "
				+ "ready you can start adding coffee cups to the people in your tally.")
				: getTellSpeechletResponse("");			
	}

	private Card getCoffeeScoreCard(Map<String, Integer> tally) {
		StringBuilder scoreBoard = new StringBuilder();
		int index = 0;
		for (Entry<String, Integer> entry : tally.entrySet()){
			index++;
			scoreBoard.append("No. ")
            .append(index)
            .append(" - ")
            .append(entry.getKey())
            .append(" : ")
            .append(entry.getValue())
            .append("\n");
		}
		
		SimpleCard card = new SimpleCard();
		card.setTitle("Total Count");
		card.setContent(scoreBoard.toString());
		return card;
	}

	private SpeechletResponse getTellSpeechletResponse(String speechText) {
		SimpleCard card = new SimpleCard();
		card.setTitle("Session");
		card.setContent(speechText);
		
		PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
		speech.setText(speechText);
		
		return SpeechletResponse.newTellResponse(speech, card);
	}

	private SpeechletResponse getAskSpeechletResponse(String speechText, String repromptText) {
		SimpleCard card = new SimpleCard();
		card.setTitle("Session");
		card.setContent(speechText);
		
		PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
		speech.setText(speechText);
		
		PlainTextOutputSpeech repromptSpeech = new PlainTextOutputSpeech();
		repromptSpeech.setText(repromptText);
		Reprompt reprompt = new Reprompt();
		reprompt.setOutputSpeech(repromptSpeech);
		
		return SpeechletResponse.newAskResponse(speech, reprompt, card);
	}

	private String getAllScoresAsSpeechText(Map<String, Integer> allCounts) {
		StringBuilder speechText = new StringBuilder();
		int index = 0;
		
		for (Entry<String, Integer> entry : allCounts.entrySet()){
			if(allCounts.size() > 1 && index == allCounts.size() - 1){
				speechText.append(" and ");
			}
			
			String singularOrPluralPoints = entry.getValue() == 1 ? " cup, " : " cups ";
			speechText.append(entry.getKey())
			.append(" had ")
			.append(entry.getValue())
			.append(singularOrPluralPoints);
			index++;
		}
		return speechText.toString();
	}
}
