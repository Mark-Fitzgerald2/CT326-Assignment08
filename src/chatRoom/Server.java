package chatRoom;
//Mark Fitzgerald 15456198
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;

public class Server extends JFrame {
	private static final long serialVersionUID = 1L;
	//create variables for gui
	private Container container;
	private JPanel textAreaPanel;
	private JPanel startPanel;
	private JButton start;
	private JTextArea chatArea;
	private JScrollPane scrollPane;
	//create an arraylist of printwriters
	private ArrayList<PrintWriter> clientOutputStreams;
	//create arraylist of users
	private ArrayList<String> users;
	//create clientSocket and input stream reader
	private Socket clientSock;
	private InputStreamReader isr;

	//constructor
	public Server() {
		//create the server gui
		createGUI();
	}

	private void createGUI() {
		//create the various objects needed for the gui
		container = getContentPane();
		chatArea = new JTextArea(35,35);
		start = new JButton("Start");
		textAreaPanel = new JPanel();
		startPanel = new JPanel();
		//prevent somebody editing the server chat area
		chatArea.setEditable(false);
		//give the scrollpane the chat area
		scrollPane = new JScrollPane(chatArea);
		//set the size of the start button
		start.setPreferredSize(new Dimension(250, 25));
		//set the verticalscrollbar
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		//add the scrollpane to a JPanel
		textAreaPanel.add(scrollPane);
		//add the start button to a JPanel
		startPanel.add(start);
		//create a button handler
		ButtonHandler handler = new ButtonHandler();
		//give the start button a button handler
		start.addActionListener(handler);
		//add the two JPanels to the container
		container.add(textAreaPanel, BorderLayout.CENTER);
		container.add(startPanel, BorderLayout.SOUTH);
		//set the size and set the container visible
		setSize(450, 670);
		setVisible(true);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setTitle("Server window for Chatroom");
		setResizable(false);
	}

	//called if start button pressed
	private void start(ActionEvent evt) {
		//create a thread for the server and start it
		Thread starter = new Thread(new ServerStart());
		starter.start();
		//tell the chat area it is started
		chatArea.append("Server Started\n");
	}

	//main to run the server
	public static void main(String args[]) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				//create a new server
				new Server();
			}
		});
	}

	public void userAdd(String data) {
		//add the users and pass in their names
		users.add(data);
		//tell everyone the users were added
		displayMessage("Server: :Done");
	}

	//tell all clients the message
	public void displayMessage(String message) {
		//create an iterator of print writers
		//this uses the local arrayList of print writers
		Iterator<PrintWriter> it = clientOutputStreams.iterator();
		//while there is another print writer
		while (it.hasNext()) {
			try {
				//create a new print writer from the next iterator
				PrintWriter writer = (PrintWriter) it.next();
				//print the message to the printWriter
				writer.println(message);
				//tell the server the message was went
				chatArea.append("Sending: " + message + "\n");
				//flush in case computer fails
				writer.flush();
				//move the chatArea to the bottom
				chatArea.setCaretPosition(chatArea.getDocument().getLength());
			} catch (Exception ex) {
				//catch exception while sending message
				chatArea.append("Error distributing the message.\n");
			}
		}
	}
	
	//class for server thread
	public class ServerStart implements Runnable {
		@Override
		public void run() {
			//create the arraylists
			clientOutputStreams = new ArrayList<PrintWriter>();
			users = new ArrayList<String>();
			try {
				//create a serverSocket
				@SuppressWarnings("resource")
				ServerSocket serverSock = new ServerSocket(2222);
				while (true) {
					//connect the two sockets
					clientSock = serverSock.accept();
					//create a print writer from the client sock
					PrintWriter writer = new PrintWriter(clientSock.getOutputStream());
					//add this to the arrayList
					clientOutputStreams.add(writer);
					//add a listener thread for messages sent and start it 
					Thread listener = new Thread(new ClientHandler(clientSock, writer));
					listener.start();
					//tell the server window a connection has found
					chatArea.append("Connection obtained\n");
					//print the local address of the connected user
					chatArea.append(clientSock.getInetAddress().toString() + " has connected\n");
				}
			} catch (Exception ex) {
				//catch an exception while connecting 
				chatArea.append("Error connecting client.\n");
			}
		}
	}
	
	//create a buttonHandler class
	private class ButtonHandler implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			//make sure the button start was pressed
			if(event.getActionCommand().equals("Start")) {
				//call the start method
				start(event);
			} 
		}
}
	
	//class to handle info from the client
	public class ClientHandler implements Runnable {
		BufferedReader reader;
		Socket sock;
		PrintWriter client;

		//pass in a socket and printwriter
		public ClientHandler(Socket clientSocket, PrintWriter user) {
			//store the printwriter locally
			client = user;
			try {
				//store the socket locally
				sock = clientSocket;
				//create isr with the socket
				isr = new InputStreamReader(sock.getInputStream());
				//use this to create buffered reader
				reader = new BufferedReader(isr);
			} catch (Exception ex) {
				//catch exception
				chatArea.append("There was an unexpected error.\n");
			}
		}
		@Override
		public void run() {
			//create a string and a string array
			String message;
			String[] splitMessage;
			try {
				//read the message as it's received
				while ((message = reader.readLine()) != null) {
					//show on the server the message received
					chatArea.append("Received: " + message + "\n");
					//split the message
					splitMessage = message.split(":");
					//check if the message is an image
					if(splitMessage[1].equals("image")) {
						//create a fileoutput stream and pass in where to store the image
			            FileOutputStream fout = new FileOutputStream("C:\\Users\\I342039\\eclipse-workspace\\CT326-Assignment08\\image.jpg");
			            //read the byte array
			            //use inputstream reader to do this
			            int i;
			            while ( (i = isr.read()) > -1) {
			                fout.write(i);
			            }
			            //close the file output stream
			            fout.flush();
			            fout.close();
			            //tell the server that an image was received and stored
						chatArea.append("Image received. Stored at\nC:\\Users\\I342039\\eclipse-workspace\\CT326-Assignment08\\image.jpg\n");
						chatArea.setCaretPosition(chatArea.getDocument().getLength());
					} else {
						//check if it was a connect or send
						if (splitMessage[2].equals("Connect")) {
							//show who was connected
							displayMessage((splitMessage[0] + ":" + splitMessage[1] + ":Chat"));
							//add the user to array
							userAdd(splitMessage[0]);
						} else if (splitMessage[2].equals("Chat")) {
							//display the message
							displayMessage(message);
						}
					}
				}
			} catch (Exception ex) {
				//catch exception
				chatArea.append("Lost connection to one of the clients.\n");
				clientOutputStreams.remove(client);
			}
		}
	}
}