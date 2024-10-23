package com.amazonaws.lambda.mihai.bedrockinvoker.service;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.amazonaws.lambda.mihai.bedrockinvoker.aspect.TraceAll;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.RangeKeyCondition;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;

@TraceAll
public class DynamoService {

	//follow the ISO 8601 standard: 2024-10-15T08:30:25
	private static final SimpleDateFormat formatterISO8601 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	
	private Logger logger = LogManager.getLogger(DynamoService.class);
	
	private DynamoDB documentClient;
        
    public DynamoService() {}
    
    public static DynamoService build() {
    	
    	DynamoService srv = new DynamoService();
        
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withRegion("us-east-2").build();
        DynamoDB docClient = new DynamoDB(client);
        
        srv.setDocumentClient(docClient);
    	
    	return srv;
    }
    
    /**
     * return number of records from today, 00:00:00
     * @param apigatewayid
     * @param startDate
     * @return
     */
    public Integer countNewestItems(String apigatewayid) {

    	
        Table table = documentClient.getTable("RateBasedAuthorization");
    	QuerySpec spec = new QuerySpec()
    			.withHashKey("apigwid", apigatewayid)
    			.withRangeKeyCondition(new RangeKeyCondition("dt").beginsWith(formatterISO8601.format(Calendar.getInstance().getTime()).split("T")[0]));

    	ItemCollection<QueryOutcome> items = table.query(spec);
    	int counter = 0;
    	
    	if (items != null) {
        	Iterator<Item> iterator = items.firstPage().getLowLevelResult().getItems().listIterator();
        	
        	if (items.firstPage().hasNextPage()) 
        		iterator = items.iterator();
        	
        	while (iterator.hasNext()) {
        		
        		Item item = iterator.next();
        	    logger.debug("item : " + item); 
        	    counter++;
        	}
    	}
    	return Integer.valueOf(counter);
    }
    
    /**
     * 
     * @param apigatewayid
     * @param authorizedAccess
     */
    public void putRecord(String apigatewayid, Boolean authorizedAccess) {

        Table table = documentClient.getTable("RateBasedAuthorization");
        
        PrimaryKey itemKey = new PrimaryKey();
        itemKey.addComponent("apigwid", apigatewayid);
        itemKey.addComponent("dt", formatterISO8601.format(Calendar.getInstance().getTime()));
        		
        
        Item item = new Item()
        		.withPrimaryKey(itemKey)
        		.with("authorized", Boolean.toString(authorizedAccess));

        PutItemOutcome output = table.putItem(item);
        
    }

	public DynamoDB getDocumentClient() {
		return documentClient;
	}

	public void setDocumentClient(DynamoDB documentClient) {
		this.documentClient = documentClient;
	}
	
    
}
