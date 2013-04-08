package Server;


import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;     //импорт библиотек

public class Server extends Thread{   // создаЄм поток
	LinkedList<ClientHandler> users; //структура данных с пользовател€ми
    static ServerSocket chatServerSocket; //сообственно сам сервер-сокет
	static Server chatServerThread; //поток обработки 

	public Server() throws IOException {   //функци€ создани€ сервера
		try {
			chatServerSocket = new ServerSocket(0); //создаЄм сервер-сокет на любом свободном порте
			users = new LinkedList<ClientHandler>(); //создаЄм список пользователей
			System.out.println(chatServerSocket.getLocalPort()); //вывод на дисплей номер порта 
		} catch (IOException e) { e.printStackTrace(); System.out.println("No free server port"); //ловим ексепшены
		}
	}
	
	
	public static void main(String[] args){
		try {
			chatServerThread = new Server();       //создаЄм поток
			chatServerThread.start();               //сообсственно запуск сервера
		} catch (IOException e) { e.printStackTrace(); } //ловим ексепшены
	}
	

	public void start(){         //цикл прослушивани€, ожидани€ коннекта
		try {
			while(true){  //бесконечный цикл
				Socket newUser = chatServerSocket.accept();  //ожидание нового подключени€
				System.out.println("new accept");
				chatServerThread.userConnected(newUser);	//пополн€ем нить	
			}
		} catch (IOException e) { e.printStackTrace();}  //ловим ексепшены
	}
		
	void userConnected(Socket newUser) throws IOException{ //соединение пользовател€
		boolean flag = false; 
		System.out.println("new user start");
			ClientHandler userThread = new ClientHandler(this, newUser); //копирование данных о пользователе во временный елемент списка
			for(ClientHandler user:users){ // цикл прохода по всем пользовател€м
					if (user.username.equals(userThread.username)) flag=true; // если введеный никнейм совпадает с каким-то из имеющихс€, то flag=true
			}
			if (flag==true){
				userThread.out.write("Name is already exist. Please restart application and enter other name"); //если flag=true пишем ссобщение что никнейм зан€т
				userThread.out.flush(); //отправл€ем его
			}
			else{                          //если нет
				userThread.setDaemon(true); //устанавливаем поток-демон (что бы не ждать закрыти€)
				users.add(userThread);     //добавлени€ пользовател€ в список
				userThread.start();	       //выполн€ем соединение
			}
	}
		
		
	
	
	void userDisconnected(ClientHandler u, String username){   //потер€ пользовател€
			users.remove(u);         //удалени€ из списка
			sendMessage(null, "User disconnected: "+username); //отправка сообщени€ всем пользовател€м
	}
	
	public void sendMessage(String senderName, String message){     //отправка сообщени€
			String dt=new java.text.SimpleDateFormat("hh:mm aaa").format(java.util.Calendar.getInstance ().getTime());   //получение времени отправки сообщени€
			StringBuilder m = new StringBuilder();           // составитель строк
			m.append('[');
			if(senderName==null){     // если не указано им€ отправител€ пишем Server
				m.append("Server");
			}else{                   //иначе пишем им€ отправител€
				m.append(senderName);
			}
			m.append("]\t");
			m.append(message);       //добавл€ем сообщение
			m.append("\n");         // добавл€ем переход на новую строку
			System.out.print(dt+" "+"to "+users.size()+" user(s): "+m.toString()); // выводим на дисплей
			for(ClientHandler user:users){      //цикл прохода по всем пользовател€м
				try {
					user.sendMessage(m.toString());        //отправл€ем строку
				} catch (IOException e) { e.printStackTrace();	}  //ловим ексепшены
			}
		}
	}
