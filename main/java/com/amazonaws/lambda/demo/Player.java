package com.amazonaws.lambda.demo;

import java.util.ArrayList;

import com.amazonaws.services.dynamodbv2.datamodeling.*;

@DynamoDBTable(tableName = "PlayersData")
public class Player{
	
	private int playerId;
	private String playerName;
	private ArrayList<ScoreObject> scoreObjectList;
	@DynamoDBHashKey
	public int getPlayerId() {
		return playerId;
	}
	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}
	 @DynamoDBAttribute(attributeName = "playerName")
	 public String getPlayerName() {
		return playerName;
	}
	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}
	 @DynamoDBAttribute(attributeName = "scores")
	 public ArrayList<ScoreObject> getScoreObjectList() {
			return scoreObjectList;
		}
	 @DynamoDBAttribute(attributeName ="scores")
		public void setScoreObjectList(ArrayList<ScoreObject> scoreObjectList) {
			this.scoreObjectList = scoreObjectList;
		}
}


@DynamoDBDocument
class ScoreObject {
	private long datetime;
	private String datetimeString;
    private int maxscore;
    private int quizId;
	private int score;
	private int clientId;
    public int getClientId() {
		return clientId;
	}
	public void setClientId(int clientId) {
		this.clientId = clientId;
	}
	public long getDatetime() {
		return datetime;
	}
	public void setDatetime(long datetime) {
		this.datetime = datetime;
	}
	public String getDatetimeString() {
		return datetimeString;
	}
	public void setDatetimeString(String datetimeString) {
		this.datetimeString = datetimeString;
	}
	public int getMaxscore() {
		return maxscore;
	}
	public void setMaxscore(int maxscore) {
		this.maxscore = maxscore;
	}
	public int getQuizId() {
		return quizId;
	}
	public void setQuizId(int quizId) {
		this.quizId = quizId;
	}
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}
	
	
}