package Client;


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.regex.*;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;  //импорт библиотек

public class Client {
	static Socket clientSocket;
	static String username;
	static int port;
	static String ip;
	static BufferedWriter out;
	static BufferedReader in;
	static JTextArea chatArea;
	static JScrollPane chatPane;
	static JTextField inputTextField;
	static JButton sendButton;  //визуальные компоненты
	
	public static void main(String[] args) throws UnknownHostException, IOException {
		ip = JOptionPane.showInputDialog("Enter sever ip: ");   //диалоговое окно для ввода ip сервера
		port = Integer.parseInt(JOptionPane.showInputDialog("Enter sever port: "));	//диалоговое окно для ввода порта сервера
		username = JOptionPane.showInputDialog("Enter your name"); //диалоговое окно для ввода имени пользователя
		clientSocket = new Socket(ip,port);  // создание сокета с параметрами сервера
		out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), "cp1251"));  //выходной поток
		in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), "cp1251"));	//входной поток
		String receivedMessage = null; //инициализация полученого сообщения
		out.write(username); //запись во входной поток имени пользователя
		out.write("\n");
		out.flush(); // отправка
		JFrame chatScene = getMainScene(); //инициализация главного окна
		chatScene.setVisible(true); //отображение главного окна
		while(true){
			try{
				receivedMessage=in.readLine();} //получение входного сообщения
			catch (IOException e){receivedMessage="Server stoped. Please reconnect";chatArea.append(receivedMessage); //если ошибка - Сервер остановлен.
			chatArea.append("\n");
			chatPane.revalidate();
			break;}

			if(receivedMessage==null){     
				break;
			}else
			{
				chatArea.append(receivedMessage);  //выводи сообщение на экран
				chatArea.append("\n");
				chatPane.revalidate();
			}
			}
		

	}
	
	public static JFrame getMainScene(){   //инициализация главного окна
		JFrame mainScene = new JFrame("MiniChat"); //заголовок окна
		mainScene.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //действие по кнопке закрыть
		mainScene.setLayout(new BorderLayout()); 
		mainScene.setPreferredSize(new Dimension(500,500));  //размер окна
		chatArea = new JTextArea(); //инициализация поля чата
		chatArea.setEditable(false); //запрет на редактирование
		
		chatPane = new JScrollPane(chatArea); //панель прокрутки
		mainScene.add(chatPane, BorderLayout.CENTER); 
		
		JPanel inputAndSendPanel = new JPanel(); //инициализация нижней панели 
		inputAndSendPanel.setLayout(new BorderLayout());
		inputTextField = new JTextField(); // поле ввода 
		sendButton = new JButton("Send"); //кнопка отправки
		sendButton.addActionListener(new ActionListener() { //функция по нажатию
			
			public void actionPerformed(ActionEvent arg0) {
				Pattern pattern1 = Pattern.compile("\\s*");  //регулярное выражение для запрета отпраки пустого сообщения или любого количества пробелов
				Matcher m = pattern1.matcher(inputTextField.getText());
				if(!m.matches()){
					try {
						out.write(inputTextField.getText());   //запись из поля ввода в выходной поток
						out.write("\n");
						out.flush();			//отправка
						inputTextField.setText("");   //очистка поля ввода
					} catch (IOException e) { e.printStackTrace(); }  //ловим ексепшены
				}
			}
		});
		inputAndSendPanel.add(inputTextField, BorderLayout.CENTER); 
		inputAndSendPanel.add(sendButton, BorderLayout.EAST);
		mainScene.add(inputAndSendPanel, BorderLayout.SOUTH); //добавление нижней панели
		mainScene.pack();   //компоновка всех элементов окна
		
		return mainScene;
	}

}