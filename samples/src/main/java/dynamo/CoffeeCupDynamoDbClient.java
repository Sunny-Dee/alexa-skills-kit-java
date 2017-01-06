package dynamo;

import com.amazon.speech.speechlet.Session;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;

/**
 * Methods to interact with the persistence layer for CoffeeCup in DynamoDB
 * @author delianaescobari
 */

public class CoffeeCupDynamoDbClient {
	private final AmazonDynamoDBClient dynamoDBClient;
	
	public CoffeeCupDynamoDbClient(final AmazonDynamoDBClient dynamoDBClient){
		this.dynamoDBClient = dynamoDBClient;
	}
	

}
