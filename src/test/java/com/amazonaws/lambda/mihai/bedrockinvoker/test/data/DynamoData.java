package com.amazonaws.lambda.mihai.bedrockinvoker.test.data;

import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hamcrest.Matchers;
import org.mockito.Mockito;
import org.mockito.hamcrest.MockitoHamcrest;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.amazonaws.lambda.mihai.bedrockinvoker.model.DynamoTable;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.KeyAttribute;
import com.amazonaws.services.dynamodbv2.document.Page;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.PutItemResult;
import com.amazonaws.services.dynamodbv2.model.QueryResult;

public class DynamoData {

	private static Logger logger = LogManager.getLogger(DynamoData.class);
	
	private static final String TAB_NAME_AUTHORIZER = "RateBasedAuthorization";
	
	public static Table tabRateBasedAuthorization;
	public static List<Item> tabRateBasedAuthorizationItemsList;
	public static Collection<Map<String, AttributeValue>> tabRateBasedAuthorizationItemsCollection;
	
	public static void resetDynamoData (DynamoDB documentClient) {
		tabRateBasedAuthorization = Mockito.mock(Table.class);
		tabRateBasedAuthorizationItemsList = new ArrayList<Item>();
		tabRateBasedAuthorizationItemsCollection = new ArrayList<Map<String, AttributeValue>>();
		
		when(documentClient.getTable(DynamoData.TAB_NAME_AUTHORIZER)).thenReturn(tabRateBasedAuthorization);
		
		when(tabRateBasedAuthorization.putItem(Mockito.any(Item.class))).thenAnswer(new Answer<PutItemOutcome>() {
			
		     public PutItemOutcome answer(InvocationOnMock invocation) throws Throwable {
		    	 
		    	 Map<String, String> jsonItemMap = new HashMap<String, String>();
		    	((Item)invocation.getArguments()[0]).asMap().entrySet().forEach(entry -> jsonItemMap.put(entry.getKey(), (String)entry.getValue()));
		    			    	 
		    	addRateBasedAuthorizationItem (jsonItemMap);
		    	 
		    	 return new PutItemOutcome(new PutItemResult());
		     }
		 });
	}
	
	public static void addRateBasedAuthorizationItem (Map<String, String> jsonItemMap) {
		
		logger.debug("START addRateBasedAuthorizationItem");
    	
    	DynamoTable tableDetails = new DynamoTable();
    	tableDetails.setTableName(TAB_NAME_AUTHORIZER);
    	tableDetails.setTablePKName("apigwid");
    	tableDetails.setTablePKValue(jsonItemMap.get("apigwid"));
    	tableDetails.setTableSKName("dt");
    	tableDetails.setTableSKValue(jsonItemMap.get("dt"));
    	
    	
    	// mock getItem
    	Item itm = Mockito.mock(Item.class);
    	Map<String, Object> item = new HashMap<String, Object>();
    	
    	jsonItemMap.entrySet().forEach(entry -> item.put(entry.getKey(), entry.getValue()));
//    	System.out.println(Arrays.toString(item.entrySet().toArray()));
    	PrimaryKey itemKey = new PrimaryKey();
        itemKey.addComponent(tableDetails.getTablePKName(), tableDetails.getTablePKValue());
        itemKey.addComponent(tableDetails.getTableSKName(), tableDetails.getTableSKValue());
    	when(tabRateBasedAuthorization.getItem(itemKey)).thenReturn(itm);
    	when(itm.asMap()).thenReturn(item);
    	
    	//mock query
    	
    	tabRateBasedAuthorizationItemsList.add(itm);
    	
    	
    	Map<String, AttributeValue> itemAV = new HashMap<String, AttributeValue>();
    	jsonItemMap.entrySet().forEach(entry -> itemAV.put(entry.getKey(), new AttributeValue(entry.getValue()) ));
    	tabRateBasedAuthorizationItemsCollection.add(itemAV);
    	
    	KeyAttribute key = new KeyAttribute(tableDetails.getTablePKName(), tableDetails.getTablePKValue());
    	    	
    	//System.out.println("tabBloodPressure key: " + key + " collection: " + Arrays.toString(tabBloodPressureItemsCollection.toArray()));
    	
//    	if (tabBloodPressure.query(new QuerySpec().withHashKey(key)) == null) {
    		
    		
        	QueryOutcome res = new QueryOutcome(new QueryResult());
        	res.getQueryResult().setItems(tabRateBasedAuthorizationItemsCollection);
            
        	Page<Item, QueryOutcome> page = new Page(tabRateBasedAuthorizationItemsList, res) {
    			
    			@Override
    			public Page<Item, QueryOutcome> nextPage() {
    				// TODO Auto-generated method stub
    				return null;
    			}
    			
    			@Override
    			public boolean hasNextPage() {
    				// TODO Auto-generated method stub
    				return false;
    			}
    		};
    		ItemCollection<QueryOutcome> items = Mockito.mock(ItemCollection.class);
        	when(items.firstPage()).thenReturn(page);
        	
        	        	
        	when(tabRateBasedAuthorization.query(new QuerySpec()
        			.withHashKey(
        					MockitoHamcrest.argThat( Matchers.hasProperty("hashKey", Matchers.equalTo(key))
        			)))).thenReturn(items);

//    	}
        	
        	logger.debug("END addRateBasedAuthorizationItem");
    }
}
