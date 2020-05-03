package com.amazonaws.lambda.demo;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import java.awt.List;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;
import java.util.logging.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;

public class QuizRequestHandler implements RequestHandler<Object, String> {
        static Context context;
        JSONObject error=new JSONObject("{\"error\":\"Invalied request\"}");
	  static AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();
	    static DynamoDB dynamoDB = new DynamoDB(client);
	    JSONObject json;
	    static long count;
	    static boolean flag = true;
	    
    @Override
    public String handleRequest(Object input, Context cxt) {
     context=cxt;
     context.getLogger().log("Input"+input);
     try {
    	 json=new JSONObject(""+input);
    	 return getData(json).toString();
    	 
     }catch(JSONException e) {
    	 
     }
	return "Invalied Request";
   
    }


	private Object getData(JSONObject input) {
		if(input==null) {
			return error;
		}else {
			try {
				switch(input.getString("name")) {
				case "getQuiz": return getQuiz(1);
				//case "getHistory":return getHistory(input.getNumber("playerId"));
			     case "getPlayer" : return getPlayer("Mounika");
				case "setQuizScore" : return setQuizScore(2,1,5,5,1);
				}
			} catch (JSONException e) {
				//TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		return null;
	}


	private Object getHistory(int playerId) {
		// TODO Auto-generated method stub
		Table table = dynamoDB.getTable("PlayersData");
    	try {
    		//"{\"name\":\"getQuizHistory\,\"playerId\":1"}"
    		Item item = table.getItem("playerId",playerId,"scores",null);
    		return new JSONObject("" + item.toJSONPretty());
    	}catch(Exception e) {
    		context.getLogger().log("GetItem Failed");
    		context.getLogger().log(e.getMessage());
    	}
    	return null;
	}


	private boolean setQuizScore(int playerId,int quizId,int score,int maxscore,int clientId){
		Table table=dynamoDB.getTable("QuizData");
		try {
			/*
			 * DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
			 * LocalDateTime now = LocalDateTime.now(); DateTimeFormatter f =
			 * DateTimeFormatter.ofPattern("MMMM dd, yyyy, hh:mm"); String
			 * formattedDate=now.format(f); Instant instant = Instant.now(); long
			 * timeStampMillis = instant.toEpochMilli();
			 */
	
			DynamoDBMapper mapper = new DynamoDBMapper(client);
			Player player = mapper.load(Player.class, playerId);
			ScoreObject so=new ScoreObject();
			so.setClientId(clientId);
			so.setDatetime(10001232);
			so.setDatetimeString("asdfdsf");
			so.setQuizId(quizId);
			so.setScore(maxscore);
			ArrayList<ScoreObject> al=player.getScoreObjectList();
			al.add(so);
			context.getLogger().log("===================================");
			player.setScoreObjectList(al);
			mapper.save(player);
			return true;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			context.getLogger().log("getItem failed");
			context.getLogger().log(e.getMessage());
		}		
		return false;
	}


	
	 private  Object getPlayer(String playerName){
		 Table table = dynamoDB.getTable("PlayersData");
			Item item=getItemHelper(playerName, table);
		  if(item==null)  {
			      int pid=createPlayer(playerName);
		           if(pid!=-1) {
		        	   try {
						Item i=table.getItem("playerId",pid,"playerId",null);
						context.getLogger().log(i.toJSONPretty());
						return new JSONObject(""+i.toJSONPretty());
					} catch (JSONException e) {
						context.getLogger().log("can't get the"+pid);
						e.printStackTrace();
					}
		           }
		  }else {
			  try {
				 
				  	Item i=getItemHelper(playerName, table);
					context.getLogger().log(i.toJSONPretty());
					return new JSONObject(""+i.toJSONPretty());
				} catch (JSONException e) {
					context.getLogger().log("can't get the player");
					e.printStackTrace();
				}
		  }
		return null; 
		
		  }
	 
	 
	 private Item getItemHelper(String playerName,Table table) {
		 HashMap<String, String> nameMap = new HashMap<String, String>();
	        nameMap.put("#name", "playerName");
	        HashMap<String, String> valueMap = new HashMap<String, String>();
	        valueMap.put(":nm", playerName);
	        QuerySpec querySpec = new QuerySpec().withKeyConditionExpression("#name = :nm")
	        		                              .withNameMap(nameMap).withNameMap(valueMap);
	        ItemCollection<QueryOutcome> items = null;
	        Iterator<Item> iterator = null;
	        Item i = null;

	        try {
	            items = table.query(querySpec);
	            iterator = items.iterator();
	                i = iterator.next();
	        }catch (Exception e) {
				// TODO: handle exception
	        	e.printStackTrace();
			}
	        return i;

	 }
	 
	 
     private int createPlayer(String playerName) {
    	 Table table = dynamoDB.getTable("PlayersData");
    	 try {
			DynamoDBMapper mapper = new DynamoDBMapper(client);
			 ArrayList<ScoreObject> al=new ArrayList<>();
			 Player p=new Player();
			 p.setPlayerId(3);
			 p.setPlayerName(playerName);
			 p.setScoreObjectList(al);
			 mapper.save(p);
			 return p.getPlayerId();
		}catch(Exception e) {
			e.printStackTrace();
		}
    	 return -1;
     }



	private Object getQuiz(int quizId) {
		Table table=dynamoDB.getTable("QuizData");
		try {
			Item item=table.getItem("quizId",quizId);
			
			context.getLogger().log(item.toJSONPretty());
			return new JSONObject(""+item.toJSONPretty());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			context.getLogger().log("getItem failed");
			context.getLogger().log(e.getMessage());
		}		
		// TODO Auto-generated method stub
		return null;	
	}	
}

//"{\"name\":\"fetchQuiz\",\"quizId\":1}"

/*
 * "{\"name\":\"${input.params('name')}\",\"playerid\":\"${input.params('pid')\"}}"
 * 
 * 
 */
