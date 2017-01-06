package dynamo;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMarshaller;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMarshalling;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Model representing an item of the CoffeeCupDataItem table in DynamoDB for CoffeCup Counter skill.
 */

@DynamoDBTable(tableName = "CoffeCupUserData")
public class CoffeeCupDataItem {
	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
	private String playerId; //TODO customerID instead??
	private CoffeeCupRoundData roundData;
	
	@DynamoDBHashKey(attributeName = "PlayerId")
	public String getPlayerId(){
		return playerId;
	}
	
	public void setPlayerId(String playerId){
		this.playerId = playerId;
	}
	
	@DynamoDBAttribute(attributeName = "Data")
	@DynamoDBMarshalling(marshallerClass = CoffeeCupDataMarshaller.class)
	public CoffeeCupRoundData getRoundData(){
		return roundData;
	}
	
	public void setRoundData(CoffeeCupRoundData roundData){
		this.roundData = roundData;
	}
	
	public static class CoffeeCupDataMarshaller implements 
	 DynamoDBMarshaller<CoffeeCupRoundData>{

		@Override
		public String marshall(CoffeeCupRoundData roundData) {
			try{
				return OBJECT_MAPPER.writeValueAsString(roundData);
			} catch (JsonProcessingException e){
				throw new IllegalStateException("Unable to marshall this round's data", e);
			}
		}

		@Override
		public CoffeeCupRoundData unmarshall(Class<CoffeeCupRoundData> clazz, String value) {
			try{
				return OBJECT_MAPPER.readValue(value, new TypeReference<CoffeeCupRoundData>(){});
			} catch (Exception e){
				throw new IllegalStateException("Unable to unmarshall round data value", e);
			}
		}
		
	}
}
