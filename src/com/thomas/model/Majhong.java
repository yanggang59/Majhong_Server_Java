package com.thomas.model;

public class Majhong {
	private int id;
	private String name;
	private boolean isOut;  //是否已经打出
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isOut() {
		return isOut;
	}
	public void setOut(boolean isOut) {
		this.isOut = isOut;
	}
	
	public Majhong()
	{
		
	}
	
	public Majhong(int id,String name) 
	{
		this.id= id;
		this.name = name;
	}
	
	public Majhong(int id,String name,boolean isOut) 
	{
		this.id= id;
		this.name = name;
		this.isOut = isOut;
	}
}
