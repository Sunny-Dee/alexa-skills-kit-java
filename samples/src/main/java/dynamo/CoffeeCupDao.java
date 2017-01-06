package dynamo;

public class CoffeeCupDao {
	private final CoffeeCupDynamoDbClient dynamoDbClient;
	
	public CoffeeCupDao(CoffeeCupDynamoDbClient dynamoDbClient){
		this.dynamoDbClient = dynamoDbClient;
	}
}
