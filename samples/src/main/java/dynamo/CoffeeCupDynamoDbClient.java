package dynamo;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

/**
 * Methods to interact with the persistence layer for CoffeeCup in DynamoDB
 * @author delianaescobari
 */

public class CoffeeCupDynamoDbClient {
	private final AmazonDynamoDBClient dynamoDBClient;
	
	public CoffeeCupDynamoDbClient(final AmazonDynamoDBClient dynamoDBClient){
		this.dynamoDBClient = dynamoDBClient;
	}
	
	/**
	 * Loads item from DynamoDB by the primary hash key. 
	 * @param tableItem
	 * @return
	 */
	public CoffeeCupDataItem loadItem(final CoffeeCupDataItem tableItem){
		DynamoDBMapper mapper = createDynamoDBMapper();
		CoffeeCupDataItem item = mapper.load(tableItem);
		
		return item;
	}
	
	public void saveItem(final CoffeeCupDataItem tableItem){
		DynamoDBMapper mapper = createDynamoDBMapper();
		mapper.save(tableItem);
	}
	
	private DynamoDBMapper createDynamoDBMapper(){
		return new DynamoDBMapper(dynamoDBClient);
	}
}
