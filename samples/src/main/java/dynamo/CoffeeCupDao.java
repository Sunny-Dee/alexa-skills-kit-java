package dynamo;

import com.amazon.speech.speechlet.Session;

public class CoffeeCupDao {
	private final CoffeeCupDynamoDbClient dynamoDbClient;
	
	public CoffeeCupDao(CoffeeCupDynamoDbClient dynamoDbClient){
		this.dynamoDbClient = dynamoDbClient;
	}
	
	public CoffeeCupCounter getCoffeeCounter(Session session){
		CoffeeCupDataItem item = new CoffeeCupDataItem();
		item.setPlayerId(session.getUser().getUserId());
		item = dynamoDbClient.loadItem(item);
		
		if (item == null){
			return null;
		}
		
		return CoffeeCupCounter.newInstance(session, item.getRoundData());
	}
	
	public void saveCoffeeCupCounter(CoffeeCupCounter round){
		CoffeeCupDataItem item = new CoffeeCupDataItem();
		item.setPlayerId(round.getSession().getUser().getUserId());
		item.setRoundData(round.getRoundData());
		
		dynamoDbClient.saveItem(item);
	}
}
