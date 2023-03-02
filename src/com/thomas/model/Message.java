package com.thomas.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Message implements Serializable{
	private int typeID;
	private int playerID;
	private String content;
	private List<Majhong> majhongs = new ArrayList<Majhong>();
	public int getTypeID() {
		return typeID;
	}
	public void setTypeID(int typeID) {
		this.typeID = typeID;
	}
	public int getPlayerID() {
		return playerID;
	}
	public void setPlayerID(int playerID) {
		this.playerID = playerID;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public List<Majhong> getMajhongs() {
		return majhongs;
	}
	public void setMajhongs(List<Majhong> majhongs) {
		this.majhongs = majhongs;
	}
	
	public Message()
	{
		
	}
	
	public Message(int typeID,int playerID,String content,List<Majhong> majhongs)
	{
		this.typeID = typeID;
		this.playerID = playerID;
		this.content = content;
		this.majhongs = majhongs;
	}
}
