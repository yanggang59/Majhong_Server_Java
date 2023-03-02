package com.thomas.model;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

//玩家类
public class Player {
	private int id;
	private String name;
	private Socket socket;
	private boolean isDealer;  //是否是庄家
	private List<Majhong> majhongs = new ArrayList<Majhong>(); //玩家的麻将列表
	
	public boolean isDealer() {
		return isDealer;
	}
	public void setDealer(boolean isDealer) {
		this.isDealer = isDealer;
	}
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
	public Socket getSocket() {
		return socket;
	}
	public void setSocket(Socket socket) {
		this.socket = socket;
	}
	public List<Majhong> getMajhongs() {
		return majhongs;
	}
	public void setMajhongs(List<Majhong> majhongs) {
		this.majhongs = majhongs;
	}

	public Player()
	{
		
	}
	
	public Player(int id,String name,Socket socket,List<Majhong> majhongs)
	{
		this.id = id;
		this.name  = name;
		this.socket = socket;
		this.majhongs = majhongs;
	}
	
	public Player(int id) 
	{
		this.id = id;
	}
	
	public Player(int id,String name,boolean isDealer) 
	{
		this.id = id;
		this.name = name;
		this.isDealer = isDealer;
	}
	
	public Player(int id,String name,List<Majhong> majhongs) 
	{
		this.id = id;
		this.name = name;
		this.majhongs = majhongs;
	}
}
