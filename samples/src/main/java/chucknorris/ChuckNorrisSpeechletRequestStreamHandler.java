package chucknorris;

import java.util.HashSet;
import java.util.Set;

import com.amazon.speech.speechlet.Speechlet;
import com.amazon.speech.speechlet.lambda.SpeechletRequestStreamHandler;

public class ChuckNorrisSpeechletRequestStreamHandler extends SpeechletRequestStreamHandler {
	
	private static final Set<String> supportedApplicationIds;
	
	static {
		supportedApplicationIds = new HashSet<String>();
	}

	public ChuckNorrisSpeechletRequestStreamHandler(){
		super(new ChuckNorrisSpeechlet(), supportedApplicationIds);
	}
	
	public ChuckNorrisSpeechletRequestStreamHandler(Speechlet speechlet, Set<String> supportedApplicationIds) {
		super(speechlet, supportedApplicationIds);
	}

}
