package chucknorris;

import java.util.HashSet;
import java.util.Set;

import com.amazon.speech.speechlet.Speechlet;
import com.amazon.speech.speechlet.lambda.SpeechletRequestStreamHandler;

/**
 * To compile and assemble use: mvn assembly:assembly -DdescriptorId=jar-with-dependencies package
 * @author delianaescobari
 *
 */
public class ChuckNorrisSpeechletRequestStreamHandler extends SpeechletRequestStreamHandler {
	
	private static final Set<String> supportedApplicationIds;
	
	static {
		supportedApplicationIds = new HashSet<String>();
		// supportedApplicationIds.add("amzn1.echo-sdk-ams.app.[unique-value-here]");
	}

	public ChuckNorrisSpeechletRequestStreamHandler(){
		super(new ChuckNorrisSpeechlet(), supportedApplicationIds);
	}
	
	public ChuckNorrisSpeechletRequestStreamHandler(Speechlet speechlet, Set<String> supportedApplicationIds) {
		super(speechlet, supportedApplicationIds);
	}

}
