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
import javax.swing.JTextField;  //������ ���������

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
	static JButton sendButton;  //���������� ����������
	
	public static void main(String[] args) throws UnknownHostException, IOException {
		ip = JOptionPane.showInputDialog("Enter sever ip: ");   //���������� ���� ��� ����� ip �������
		port = Integer.parseInt(JOptionPane.showInputDialog("Enter sever port: "));	//���������� ���� ��� ����� ����� �������
		username = JOptionPane.showInputDialog("Enter your name"); //���������� ���� ��� ����� ����� ������������
		clientSocket = new Socket(ip,port);  // �������� ������ � ����������� �������
		out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), "cp1251"));  //�������� �����
		in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), "cp1251"));	//������� �����
		String receivedMessage = null; //������������� ���������� ���������
		out.write(username); //������ �� ������� ����� ����� ������������
		out.write("\n");
		out.flush(); // ��������
		JFrame chatScene = getMainScene(); //������������� �������� ����
		chatScene.setVisible(true); //����������� �������� ����
		while(true){
			try{
				receivedMessage=in.readLine();} //��������� �������� ���������
			catch (IOException e){receivedMessage="Server stoped. Please reconnect";chatArea.append(receivedMessage); //���� ������ - ������ ����������.
			chatArea.append("\n");
			chatPane.revalidate();
			break;}

			if(receivedMessage==null){     
				break;
			}else
			{
				chatArea.append(receivedMessage);  //������ ��������� �� �����
				chatArea.append("\n");
				chatPane.revalidate();
			}
			}
		

	}
	
	public static JFrame getMainScene(){   //������������� �������� ����
		JFrame mainScene = new JFrame("MiniChat"); //��������� ����
		mainScene.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //�������� �� ������ �������
		mainScene.setLayout(new BorderLayout()); 
		mainScene.setPreferredSize(new Dimension(500,500));  //������ ����
		chatArea = new JTextArea(); //������������� ���� ����
		chatArea.setEditable(false); //������ �� ��������������
		
		chatPane = new JScrollPane(chatArea); //������ ���������
		mainScene.add(chatPane, BorderLayout.CENTER); 
		
		JPanel inputAndSendPanel = new JPanel(); //������������� ������ ������ 
		inputAndSendPanel.setLayout(new BorderLayout());
		inputTextField = new JTextField(); // ���� ����� 
		sendButton = new JButton("Send"); //������ ��������
		sendButton.addActionListener(new ActionListener() { //������� �� �������
			
			public void actionPerformed(ActionEvent arg0) {
				Pattern pattern1 = Pattern.compile("\\s*");  //���������� ��������� ��� ������� ������� ������� ��������� ��� ������ ���������� ��������
				Matcher m = pattern1.matcher(inputTextField.getText());
				if(!m.matches()){
					try {
						out.write(inputTextField.getText());   //������ �� ���� ����� � �������� �����
						out.write("\n");
						out.flush();			//��������
						inputTextField.setText("");   //������� ���� �����
					} catch (IOException e) { e.printStackTrace(); }  //����� ���������
				}
			}
		});
		inputAndSendPanel.add(inputTextField, BorderLayout.CENTER); 
		inputAndSendPanel.add(sendButton, BorderLayout.EAST);
		mainScene.add(inputAndSendPanel, BorderLayout.SOUTH); //���������� ������ ������
		mainScene.pack();   //���������� ���� ��������� ����
		
		return mainScene;
	}

}