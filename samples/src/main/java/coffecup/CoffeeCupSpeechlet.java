package coffecup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazon.speech.json.SpeechletRequestEnvelope;
import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.LaunchRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SessionEndedRequest;
import com.amazon.speech.speechlet.SessionStartedRequest;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.speechlet.Speechlet;
import com.amazon.speech.speechlet.SpeechletException;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;

public class CoffeeCupSpeechlet implements Speechlet {
	private static final Logger log = LoggerFactory.getLogger(CoffeeCupSpeechlet.class);
	
	private AmazonDynamoDBClient amazonDynamoDBClient;
	private CoffeeManager coffeeManager;
	private SkillContext skillContext;
	
	@Override
	public void onSessionStarted(SessionStartedRequest request, Session session) throws SpeechletException {
        log.info("onSessionStarted requestId={}, sessionId={}", request.getRequestId(),
                session.getSessionId());

        initializeComponents();

        // if user said a one shot command that triggered an intent event,
        // it will start a new session, and then we should avoid speaking too many words.
        skillContext.setNeedsMoreHelp(false);
		
	}

	@Override
	public SpeechletResponse onLaunch(LaunchRequest request, Session session) throws SpeechletException {
		log.info("onLaunch requestId={}, sessionId={}", request.getRequestId(),
                session.getSessionId());
		
		skillContext.setNeedsMoreHelp(true);

		return coffeeManager.getLaunchResponse(request, session);
	}
	
	@Override
	public SpeechletResponse onIntent(IntentRequest request, Session session) throws SpeechletException {
		log.info("onIntent requestId={}, sessionId={}", request.getRequestId(),
                session.getSessionId());
        initializeComponents();
        
        Intent intent = request.getIntent();
        
        if("NewTallyIntent".equals(intent.getName())){
        	return coffeeManager.getNewRoundIntentResponse(session, skillContext);
        } else if ("AddPlayerIntent".equals(intent.getName())){
        	return coffeeManager.getAddPlayerIntentResponse(intent, session, skillContext);
        } else if ("AddCupIntent".equals(intent.getName())){
        	return coffeeManager.getAddCupIntentResponse(intent, session, skillContext);
        } else if ("TellTallyIntent".equals(intent.getName())){
        	return coffeeManager.getTellCountIntentResponse(intent, session);
        } else if ("ResetCountIntent".equals(intent.getName())){
        	return coffeeManager.getResetPlayersIntentResponse(intent, session);
        } else if ("AMAZON.HelpIntent".equals(intent.getName())){
        	return coffeeManager.getHelpIntentResponse(intent, session, skillContext);
        } else if ("AMAZON.CancelIntent".equals(intent.getName())){
        	return coffeeManager.getExitIntentResponse(intent, session, skillContext);
        } else if ("AMAZON.StopIntent".equals(intent.getName())){
        	return coffeeManager.getExitIntentResponse(intent, session, skillContext);
        } else {
        	throw new IllegalArgumentException("Unrecognized intent: " + intent.getName());
        }
	}
	
	
	@Override
	public void onSessionEnded(SessionEndedRequest request, Session session) throws SpeechletException {
		log.info("onSessionEnded requestId={}, sessionId={}", request.getRequestId(),
                session.getSessionId());
		
	}

	
	private void initializeComponents() {
		if(amazonDynamoDBClient == null){
			amazonDynamoDBClient = new AmazonDynamoDBClient();
			coffeeManager = new CoffeeManager(amazonDynamoDBClient);
			skillContext = new SkillContext();
		}	
	}

}
