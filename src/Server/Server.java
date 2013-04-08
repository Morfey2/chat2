package Server;


import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;     //������ ���������

public class Server extends Thread{   // ������ �����
	LinkedList<ClientHandler> users; //��������� ������ � ��������������
    static ServerSocket chatServerSocket; //����������� ��� ������-�����
	static Server chatServerThread; //����� ��������� 

	public Server() throws IOException {   //������� �������� �������
		try {
			chatServerSocket = new ServerSocket(0); //������ ������-����� �� ����� ��������� �����
			users = new LinkedList<ClientHandler>(); //������ ������ �������������
			System.out.println(chatServerSocket.getLocalPort()); //����� �� ������� ����� ����� 
		} catch (IOException e) { e.printStackTrace(); System.out.println("No free server port"); //����� ���������
		}
	}
	
	
	public static void main(String[] args){
		try {
			chatServerThread = new Server();       //������ �����
			chatServerThread.start();               //������������ ������ �������
		} catch (IOException e) { e.printStackTrace(); } //����� ���������
	}
	

	public void start(){         //���� �������������, �������� ��������
		try {
			while(true){  //����������� ����
				Socket newUser = chatServerSocket.accept();  //�������� ������ �����������
				System.out.println("new accept");
				chatServerThread.userConnected(newUser);	//��������� ����	
			}
		} catch (IOException e) { e.printStackTrace();}  //����� ���������
	}
		
	void userConnected(Socket newUser) throws IOException{ //���������� ������������
		boolean flag = false; 
		System.out.println("new user start");
			ClientHandler userThread = new ClientHandler(this, newUser); //����������� ������ � ������������ �� ��������� ������� ������
			for(ClientHandler user:users){ // ���� ������� �� ���� �������������
					if (user.username.equals(userThread.username)) flag=true; // ���� �������� ������� ��������� � �����-�� �� ���������, �� flag=true
			}
			if (flag==true){
				userThread.out.write("Name is already exist. Please restart application and enter other name"); //���� flag=true ����� ��������� ��� ������� �����
				userThread.out.flush(); //���������� ���
			}
			else{                          //���� ���
				userThread.setDaemon(true); //������������� �����-����� (��� �� �� ����� ��������)
				users.add(userThread);     //���������� ������������ � ������
				userThread.start();	       //��������� ����������
			}
	}
		
		
	
	
	void userDisconnected(ClientHandler u, String username){   //������ ������������
			users.remove(u);         //�������� �� ������
			sendMessage(null, "User disconnected: "+username); //�������� ��������� ���� �������������
	}
	
	public void sendMessage(String senderName, String message){     //�������� ���������
			String dt=new java.text.SimpleDateFormat("hh:mm aaa").format(java.util.Calendar.getInstance ().getTime());   //��������� ������� �������� ���������
			StringBuilder m = new StringBuilder();           // ����������� �����
			m.append('[');
			if(senderName==null){     // ���� �� ������� ��� ����������� ����� Server
				m.append("Server");
			}else{                   //����� ����� ��� �����������
				m.append(senderName);
			}
			m.append("]\t");
			m.append(message);       //��������� ���������
			m.append("\n");         // ��������� ������� �� ����� ������
			System.out.print(dt+" "+"to "+users.size()+" user(s): "+m.toString()); // ������� �� �������
			for(ClientHandler user:users){      //���� ������� �� ���� �������������
				try {
					user.sendMessage(m.toString());        //���������� ������
				} catch (IOException e) { e.printStackTrace();	}  //����� ���������
			}
		}
	}
