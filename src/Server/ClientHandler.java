package Server;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;   //импорт библиотек

public class ClientHandler extends Thread{
	Server chatServer;		//имя сервера
	BufferedWriter out;    //выходной поток
	BufferedReader in;    //входной поток
	String username;     //имя пользователя
	
	ClientHandler(Server cS, Socket client) throws IOException{  //инициализация клиента
		chatServer = cS;   //инициализация сервера
		out = new BufferedWriter(new OutputStreamWriter(client.getOutputStream(), "cp1251"));  //инициализация входного потока
		in = new BufferedReader(new InputStreamReader(client.getInputStream(), "cp1251"));  //инициализация выходного потока
		String mess;
		mess = in.readLine();
		username = mess;   //инициализация имени пользователя введеного им самим
		
	}
	public void run(){   //добавление клиента
		String message;
			chatServer.sendMessage(null, "New user connected: "+username);   //отправка сообщения всем пользователям, что клиент присоеденился
			while(true){
				try{
					message = in.readLine(); // ожидание сообщения
				}catch (IOException e) {;chatServer.userDisconnected(this, username);break;}; // если клиент отключился вызываем userDisconnected
			    chatServer.sendMessage(username, message);
				}		
	}
	
	public void sendMessage(String message) throws IOException{         //отправка сообщения
		String time=new java.text.SimpleDateFormat("hh:mm aaa").format(java.util.Calendar.getInstance ().getTime()); //получение даты отправки
		out.write(time +" "+ message);  //запись сообщения во выходной поток
		out.flush(); //отправка сообщения
	}
}