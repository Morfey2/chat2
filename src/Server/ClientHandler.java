package Server;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;   //������ ���������

public class ClientHandler extends Thread{
	Server chatServer;		//��� �������
	BufferedWriter out;    //�������� �����
	BufferedReader in;    //������� �����
	String username;     //��� ������������
	
	ClientHandler(Server cS, Socket client) throws IOException{  //������������� �������
		chatServer = cS;   //������������� �������
		out = new BufferedWriter(new OutputStreamWriter(client.getOutputStream(), "cp1251"));  //������������� �������� ������
		in = new BufferedReader(new InputStreamReader(client.getInputStream(), "cp1251"));  //������������� ��������� ������
		String mess;
		mess = in.readLine();
		username = mess;   //������������� ����� ������������ ��������� �� �����
		
	}
	public void run(){   //���������� �������
		String message;
			chatServer.sendMessage(null, "New user connected: "+username);   //�������� ��������� ���� �������������, ��� ������ �������������
			while(true){
				try{
					message = in.readLine(); // �������� ���������
				}catch (IOException e) {;chatServer.userDisconnected(this, username);break;}; // ���� ������ ���������� �������� userDisconnected
			    chatServer.sendMessage(username, message);
				}		
	}
	
	public void sendMessage(String message) throws IOException{         //�������� ���������
		String time=new java.text.SimpleDateFormat("hh:mm aaa").format(java.util.Calendar.getInstance ().getTime()); //��������� ���� ��������
		out.write(time +" "+ message);  //������ ��������� �� �������� �����
		out.flush(); //�������� ���������
	}
}