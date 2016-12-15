package chucknorris;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazon.speech.slu.Intent;
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
import com.amazonaws.util.json.JSONException;
import com.amazonaws.util.json.JSONObject;

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
		log.info("onIntent RequestId={}, sessionId={}", request.getRequestId(), session.getSessionId());
		
		Intent intent = request.getIntent();
		String intentName = intent.getName();
		
		if ("HitMe".equals(intentName)){
			return getJoke();
		} else if ("AMAZON.HelpIntent".equals(intentName)) {
			String help = "I give you an ass kicking joke if you say... ask Chuck Norris to give me a joke";
			return generateAskResponse(help, help);
		} else {
			throw new SpeechletException("Invalid intent");
		}
	}

	@Override
	public void onSessionEnded(SessionEndedRequest request, Session session) throws SpeechletException {
		log.info("onSessionEnded ReauestId={}, SessionId{}", session.getSessionId(), request.getRequestId());
		
	}
	
	private SpeechletResponse getJoke() {
		String joke = parseJson(makeAPIRequest());
		String reprompt ="Don't be shy ask for another joke";
		return generateAskResponse(joke, reprompt);
	}
	
	private String makeAPIRequest(){
		InputStreamReader inputStream = null;
		BufferedReader bufferedReader = null;
		String text = "";
		
		try {
			String line;
			URL url = new URL(URL_PREFIX);
			inputStream = new InputStreamReader(url.openStream(), Charset.forName("US-ASCII"));
			bufferedReader = new BufferedReader(inputStream);
			StringBuilder builder = new StringBuilder();
			
			while ((line = bufferedReader.readLine()) != null){
				builder.append(line);
			}
			text = builder.toString();
		} catch (IOException e){
			e.printStackTrace();
			text = "";
		} finally {
			IOUtils.closeQuietly(inputStream);
			IOUtils.closeQuietly(bufferedReader);
		}
		
		return text;
	}
	
	private String parseJson(String text){
		String joke = "";
		try {
			JSONObject response = new JSONObject(text);
			JSONObject value = new JSONObject(response.getJSONObject("value"));
			joke = value.getString("joke");
		} catch (JSONException e){
			joke = "No jokes for you, just a kick on the valuables.";
			e.printStackTrace();
		}
		
		return joke;
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
