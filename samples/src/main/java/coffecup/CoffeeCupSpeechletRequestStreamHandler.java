package coffecup;

import java.util.*;

import com.amazon.speech.speechlet.SpeechletV2;
import com.amazon.speech.speechlet.lambda.SpeechletRequestStreamHandler;

public class CoffeeCupSpeechletRequestStreamHandler extends SpeechletRequestStreamHandler {
	private static final Set<String> supportedApplicationIds;
	
	static {
		supportedApplicationIds = new HashSet<String>();
		// TODO supportedApplicationIds.add("amzn1.echo-sdk-ams.app.[unique-value-here]");
	}

	public CoffeeCupSpeechletRequestStreamHandler(SpeechletV2 speechlet, Set<String> supportedApplicationIds) {
		super(new CoffeeCupSpeechlet(), supportedApplicationIds);
	}

}
