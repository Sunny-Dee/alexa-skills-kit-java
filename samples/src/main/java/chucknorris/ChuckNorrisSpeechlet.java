package chucknorris;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.LaunchRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SessionEndedRequest;
import com.amazon.speech.speechlet.SessionStartedRequest;
import com.amazon.speech.speechlet.Speechlet;
import com.amazon.speech.speechlet.SpeechletException;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.Reprompt;
import com.amazon.speech.ui.SsmlOutputSpeech;

public class ChuckNorrisSpeechlet implements Speechlet {
	private static final Logger log = LoggerFactory.getLogger(ChuckNorrisSpeechlet.class);
	private static final String URL_PREFIX = "http://api.icndb.com/jokes/random/";
	@Override
	public void onSessionStarted(SessionStartedRequest request, Session session) throws SpeechletException {
		log.info("onSessionStarted requestId={}, sessionId={}", request.getRequestId(), session.getSessionId());
		
	}

	@Override
	public SpeechletResponse onLaunch(LaunchRequest request, Session session) throws SpeechletException {
		log.info("onLaunch requestId={}, sessionId={}", request.getRequestId(), session.getSessionId());
		return getWelcomeResponse();
	}


	@Override
	public SpeechletResponse onIntent(IntentRequest request, Session session) throws SpeechletException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onSessionEnded(SessionEndedRequest request, Session session) throws SpeechletException {
		log.info("onSessionEnded ReauestId={}, SessionId{}", session.getSessionId(), request.getRequestId());
		
	}
	
	private SpeechletResponse getWelcomeResponse() {
		String text = "This is the Chuck Norris app. Welcome."; //TODO
		String reprompt = "You can ask for a joke if you say... Hit me.";	
				
		return generateAskResponse(text, reprompt);
	}
	
	private SpeechletResponse generateAskResponse(String speechText, String repromptText){
		String opentag = "<speak>";
		String closetag = "</speak>";
		
		//add Ssml tags
		StringBuilder sb = new StringBuilder();
		sb.append(opentag);
		sb.append(speechText);
		sb.append(closetag);
		speechText = sb.toString();
		sb = new StringBuilder();
		sb.append(opentag);
		sb.append(repromptText);
		sb.append(closetag);
		
		SsmlOutputSpeech initialSpeech = new SsmlOutputSpeech();
		initialSpeech.setSsml(speechText);
		
		SsmlOutputSpeech repromptSpeech = new SsmlOutputSpeech();
		repromptSpeech.setSsml(repromptText);
		
		Reprompt reprompt = new Reprompt();
		reprompt.setOutputSpeech(repromptSpeech);
		
		return SpeechletResponse.newAskResponse(initialSpeech, reprompt);
	}

}
