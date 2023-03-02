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
	
	//��������б���ҵ�����Ҳ������
	public List<Player> players = new ArrayList<Player>();
	
	public int playerIndex=0;
	
	//������е��齫���б�
	public List<Majhong> allMajhongs = new ArrayList<Majhong>();
	
	public int currentDealer=0; //����ׯ��
	
	//public int currentWinner[] = {-1,-1,-1};
	
	public int nextDealer;    //��һ�ֵ�ׯ��
	
	//Server���ƾֵĽ�չ
	public ServerGameState serverGameState = ServerGameState.INITIAL;  
	
	public int majhongIndex=0; //��һ���ɷ����齫��
	
	public int getMajhongPlayer=0; //����Ҫ�����Ƶ����
	
	public MainFrame() 
	{
		//�����齫�б�
		createMajhongs();
		
		try {
			//1.������������socket
			ServerSocket serverSocket = new ServerSocket(8838);
			while(true)
			{
				//2.���տͻ��˵�socket,����������
				Socket socket = serverSocket.accept();
				//3.�����߳�,����ͻ��˵�socket
				AcceptThread acceptThread = new AcceptThread(socket);
				acceptThread.start();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("���������쳣");
		}
	}
	
	private void createMajhongs() {
		// TODO Auto-generated method stub
		for(int i = 0;i<4;i++)
		{
			for(int j = 1;j<10;j++)
			{	
				//�� 1 - 9
				Majhong majhong_tiao = new Majhong(j,j+"tiao");
				allMajhongs.add(majhong_tiao);
				
				//Ͳ 11 - 19
				Majhong majhong_tong = new Majhong(10+j,j+"tong");
				allMajhongs.add(majhong_tong);
				
				//��  21 - 29
				Majhong majhong_wan = new Majhong(20+j,j+"wan");
				allMajhongs.add(majhong_wan);
			}
			//�� 31
			Majhong majhong_dong = new Majhong(31,"dong");
			allMajhongs.add(majhong_dong);
			
			//�� 32 
			Majhong majhong_nan = new Majhong(32,"nan");
			allMajhongs.add(majhong_nan);
			
			//�� 33
			Majhong majhong_xi = new Majhong(33,"xi");
			allMajhongs.add(majhong_xi);
			
			//�� 34
			Majhong majhong_bei = new Majhong(34,"bei");
			allMajhongs.add(majhong_bei);
			
			//�� 35
			Majhong majhong_zhong = new Majhong(35,"hongzhong");
			allMajhongs.add(majhong_zhong);
			
			//�� 36
			Majhong majhong_fa = new Majhong(36,"facai");
			allMajhongs.add(majhong_fa);
			
			//�� 37
			Majhong majhong_bai = new Majhong(37,"baiban");
			allMajhongs.add(majhong_bai);
		}
		
		//ϴ��
		Collections.shuffle(allMajhongs);
		
	}

	//����һ�������̴߳���ͻ��˵���Ϣ
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
				//��socket���������,����,���յ�Client������Ϣ���͹����Ż��ܵ���һ��			
				DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
				while(true)
				{
					String msg = dataInputStream.readUTF();	
					if(serverGameState == serverGameState.INITIAL)
					{
						Player player;
						//������Ҷ���
						if(playerIndex == 0)  //���ֵ�ʱ���Ƚ�����Ϊׯ��
						{
							player = new Player(playerIndex++,msg,true);	
						}
						else
						{
							player = new Player(playerIndex++,msg,false);				
						}			
						player.setSocket(socket);		
						//��������б�
						players.add(player);				
						System.out.println(msg+"������");				
						System.out.println("��ǰ����������"+players.size());
						
						//����������룬���Ƹ��������
						if(players.size()==4)
						{
							System.out.println("��ҵ����ˣ�����");
							distributeMajhongs();
							serverGameState = serverGameState.PLAYING;
						}
					}
					else if(serverGameState == serverGameState.PLAYING)
					{
						System.out.println("*** ��Ϸ������ ***");
						//1.���Ƚ����ӿͻ��˽��յ�����Ϣ
						JSONObject msgJsonObject = JSON.parseObject(msg);
						int typeID = msgJsonObject.getInteger("typeID");
						int playerID = msgJsonObject.getInteger("playerID");
						String content = msgJsonObject.getString("content");
						if(typeID == 0 && playerID == currentDealer) //ׯ��׼����,�ƾֿ�ʼ,��ׯ�ҷ���
						{
							System.out.println("[Server]Received Message from Dealer,typeID is 0");
							//���ƺͽ���ÿһ�ε��ƾֵ��ж�
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
		
	//Ⱥ����Ϣ���ͻ���
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
		for(int i =0;i<13;i++)  // �ȷ�13�Ÿ�ÿ�����
		{
			
			for(int j = 0;j<4;j++)
			{
				players.get(j).getMajhongs().add(allMajhongs.get(majhongIndex));
				majhongIndex++;
			}
		}
		//��ׯ�ҷ���һ����
		//players.get(currentDealer).getMajhongs().add(allMajhongs.get(majhongIndex));
		//majhongIndex++;
		
		//�������Ϣ���͵��ͻ���
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
