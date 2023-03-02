package com.thomas.view;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import com.thomas.model.Player;
import com.thomas.util.GameUtil.ServerGameState;
import com.thomas.model.Majhong;
import com.thomas.model.Message;

public class MainFrame {
	
	//创建玩家列表，玩家的手牌也在里面
	public List<Player> players = new ArrayList<Player>();
	
	public int playerIndex=0;
	
	//存放所有的麻将的列表
	public List<Majhong> allMajhongs = new ArrayList<Majhong>();
	
	public int currentDealer=0; //本局庄家
	
	//public int currentWinner[] = {-1,-1,-1};
	
	public int nextDealer;    //下一局的庄家
	
	//Server端牌局的进展
	public ServerGameState serverGameState = ServerGameState.INITIAL;  
	
	public int majhongIndex=0; //下一个派发的麻将牌
	
	public int getMajhongPlayer=0; //现在要给发牌的玩家
	
	public MainFrame() 
	{
		//创建麻将列表
		createMajhongs();
		
		try {
			//1.创建服务器端socket
			ServerSocket serverSocket = new ServerSocket(8838);
			while(true)
			{
				//2.接收客户端的socket,阻塞在这里
				Socket socket = serverSocket.accept();
				//3.开启线程,处理客户端的socket
				AcceptThread acceptThread = new AcceptThread(socket);
				acceptThread.start();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("服务器端异常");
		}
	}
	
	private void createMajhongs() {
		// TODO Auto-generated method stub
		for(int i = 0;i<4;i++)
		{
			for(int j = 1;j<10;j++)
			{	
				//条 1 - 9
				Majhong majhong_tiao = new Majhong(j,j+"tiao");
				allMajhongs.add(majhong_tiao);
				
				//筒 11 - 19
				Majhong majhong_tong = new Majhong(10+j,j+"tong");
				allMajhongs.add(majhong_tong);
				
				//万  21 - 29
				Majhong majhong_wan = new Majhong(20+j,j+"wan");
				allMajhongs.add(majhong_wan);
			}
			//东 31
			Majhong majhong_dong = new Majhong(31,"dong");
			allMajhongs.add(majhong_dong);
			
			//南 32 
			Majhong majhong_nan = new Majhong(32,"nan");
			allMajhongs.add(majhong_nan);
			
			//西 33
			Majhong majhong_xi = new Majhong(33,"xi");
			allMajhongs.add(majhong_xi);
			
			//北 34
			Majhong majhong_bei = new Majhong(34,"bei");
			allMajhongs.add(majhong_bei);
			
			//中 35
			Majhong majhong_zhong = new Majhong(35,"hongzhong");
			allMajhongs.add(majhong_zhong);
			
			//发 36
			Majhong majhong_fa = new Majhong(36,"facai");
			allMajhongs.add(majhong_fa);
			
			//白 37
			Majhong majhong_bai = new Majhong(37,"baiban");
			allMajhongs.add(majhong_bai);
		}
		
		//洗牌
		Collections.shuffle(allMajhongs);
		
	}

	//创建一个接受线程处理客户端的信息
	class AcceptThread extends Thread
	{
		private Socket socket;
		public AcceptThread(Socket socket) 
		{
			this.socket = socket;
		}
		
		public void run() 
		{
			try {
				System.out.println("*** Server Running ***");	
				//从socket获得输入流,阻塞,接收到Client端有消息发送过来才会跑到下一句			
				DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
				while(true)
				{
					String msg = dataInputStream.readUTF();	
					if(serverGameState == serverGameState.INITIAL)
					{
						Player player;
						//创建玩家对象
						if(playerIndex == 0)  //开局的时候，先进来的为庄家
						{
							player = new Player(playerIndex++,msg,true);	
						}
						else
						{
							player = new Player(playerIndex++,msg,false);				
						}			
						player.setSocket(socket);		
						//存入玩家列表
						players.add(player);				
						System.out.println(msg+"上线了");				
						System.out.println("当前上线人数："+players.size());
						
						//玩家人数到齐，发牌给三个玩家
						if(players.size()==4)
						{
							System.out.println("玩家到齐了，发牌");
							distributeMajhongs();
							serverGameState = serverGameState.PLAYING;
						}
					}
					else if(serverGameState == serverGameState.PLAYING)
					{
						System.out.println("*** 游戏进行中 ***");
						//1.首先解析从客户端接收到的消息
						JSONObject msgJsonObject = JSON.parseObject(msg);
						int typeID = msgJsonObject.getInteger("typeID");
						int playerID = msgJsonObject.getInteger("playerID");
						String content = msgJsonObject.getString("content");
						if(typeID == 0 && playerID == currentDealer) //庄家准备好,牌局开始,给庄家发牌
						{
							System.out.println("[Server]Received Message from Dealer,typeID is 0");
							//发牌和进行每一次的牌局的判断
							List<Majhong> sendMajhongs = new ArrayList<Majhong>();
							sendMajhongs.add(allMajhongs.get(majhongIndex++));
							Message sendMessage = new Message(0,currentDealer,"FirstFapai",sendMajhongs);
							String sendMsg = JSON.toJSONString(sendMessage);
							System.out.println("[Server]Send Message is"+sendMsg);
							sendMessageToClient(sendMsg);
						}

					}
					
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		
		}	
	}
		
	//群发消息到客户端
	public void sendMessageToClient(String msg)
	{
		for(int i=0;i<players.size();i++)
		{
			try {
				DataOutputStream dataOutputStream = new DataOutputStream(players.get(i).getSocket().getOutputStream());
				dataOutputStream.writeUTF(msg);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private void distributeMajhongs() {
		// TODO Auto-generated method stub
		for(int i =0;i<13;i++)  // 先发13张给每个玩家
		{
			
			for(int j = 0;j<4;j++)
			{
				players.get(j).getMajhongs().add(allMajhongs.get(majhongIndex));
				majhongIndex++;
			}
		}
		//给庄家发多一张牌
		//players.get(currentDealer).getMajhongs().add(allMajhongs.get(majhongIndex));
		//majhongIndex++;
		
		//将玩家信息发送到客户端
		for(int i = 0;i<players.size();i++)
		{
			try {
				DataOutputStream dataOutputStream = new DataOutputStream(players.get(i).getSocket().getOutputStream());
				String jsonString = JSON.toJSONString(players);
				dataOutputStream.writeUTF(jsonString);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}

}
