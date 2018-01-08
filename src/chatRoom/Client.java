package chatRoom;
//Mark Fitzgerald 15456198
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Client extends javax.swing.JFrame {
	private static final long serialVersionUID = 1L;
	//Initialise local variables for GUI
	private JButton upload;
	private JButton settings;
	private JButton image;
	private JButton send;
	private JPanel sendPanel;
	private JPanel chatPanel;
	private JPanel bottomPanel;
	private JPanel bottomButtons;
	private JTextArea chatArea;
	private JTextField enterChat;
	private Container container;
	private JList<String> colorList;
	private JButton connect;
	private JScrollPane scrollPane;
	private ButtonHandler handler;
	private JLabel profilePic;
	//initialise variables for sending image
	private byte[] imageArray;
	private File chosenFile;
	// create an array with color names
	private String colorNames[] = { "White", "Black", "Blue", "Cyan", 
			"Dark Gray", "Gray", "Green", "Light Gray", "Magenta", 
			"Orange", "Pink", "Red", "Yellow" };
	// create an array with different colors
	private Color colors[] = { Color.white, Color.black, Color.blue, 
			Color.cyan, Color.darkGray, Color.gray, Color.green, 
			Color.lightGray, Color.magenta, Color.orange, Color.pink, 
			Color.red, Color.yellow };
	//create variable username
	private String username;
	//create local address
	private String address = "localhost";
	//create an arrayList of users
	//private ArrayList<String> users = new ArrayList<String>();
	//store the port value
	int port = 2222;
	//set isConnected to false 
	private Boolean isConnected = false;
	//create socket, BufferedReader and PrintWriter
	private Socket sock;
	private BufferedReader reader;
	private PrintWriter writer;

	//constructor
	public Client() throws IOException {
		//create a input dialog JOptionPane
		//store the result to username
		username = JOptionPane.showInputDialog(null, null, "Please Enter a username", 
				JOptionPane.INFORMATION_MESSAGE);
		//create new connect button and buttonhandler
		connect = new JButton("Connect");
		handler = new ButtonHandler();
		//add the handler to the button
		connect.addActionListener(handler);
		//create an object array with the connect button
		Object[] options1 = { connect, "Quit" };
		//give the user a chance to choose their own profile pic
		int input = JOptionPane.showOptionDialog(null, "Select okay to choose a "
				+ "profile picture.\nSelect cancel for anonymous profile picture", 
				"Profile Picture", JOptionPane.OK_CANCEL_OPTION, 
				JOptionPane.INFORMATION_MESSAGE, null, null, null);
		//check if ok was selected
		if(input == JOptionPane.OK_OPTION) {
			//create a new file chooser
			JFileChooser fileChooser = new JFileChooser();
			//only allow jpg
			FileNameExtensionFilter filter = new FileNameExtensionFilter("JPG Images", "jpg");
			fileChooser.setFileFilter(filter);
			//check if the selected option is valid
			int returnVal = fileChooser.showOpenDialog(null);
			if(returnVal == JFileChooser.APPROVE_OPTION) {
				//create a new JLabel and set the size
				profilePic = new JLabel();
				profilePic.setPreferredSize(new Dimension(50,50));
				//create a new file from the selected image
				File file = fileChooser.getSelectedFile();
				//create a new bufferedImage with the file
				BufferedImage image = ImageIO.read(file);
				//call the resize method to resize the image
				BufferedImage resizedImage = resize(image,50,50);
				//create a new icon out of the resized image
				Icon icon = new ImageIcon(resizedImage);
				//put the icon on the JLabel
				profilePic.setIcon(icon);
			}
		} else {
			//this repeats the steps as if the choose an image
			//the only difference is it chooses an image for them
			//this is called "question mark.jpg"
			profilePic = new JLabel();
			profilePic.setPreferredSize(new Dimension(50,50));
			File file = new File("question mark.jpg");
			BufferedImage image = ImageIO.read(file);
			BufferedImage resizedImage = resize(image,50,50);
			Icon icon = new ImageIcon(resizedImage);
			profilePic.setIcon(icon);
		}
		//display the connect button
		JOptionPane.showOptionDialog(null, "Almost there.\nHit connect to connect to the chatroom", 
				"Connect to chatroom", JOptionPane.DEFAULT_OPTION, 
				JOptionPane.PLAIN_MESSAGE, null, options1, null);
	}
	
	//resize an image
	public static BufferedImage resize(BufferedImage image, int width, int height) {
		//create a new buffered image with width and height that we want
	    BufferedImage bi = new BufferedImage(width, height, BufferedImage.TRANSLUCENT);
	    //create a 2D graphics version of bi
	    Graphics2D g2d = (Graphics2D) bi.createGraphics();
	    //allow rendering to occur
	    g2d.addRenderingHints(new RenderingHints(RenderingHints.KEY_RENDERING, 
	    		RenderingHints.VALUE_RENDER_QUALITY));
	    //draw the image using 2D graphics
	    g2d.drawImage(image, 0, 0, width, height, null);
	    //dispose the 2D graphics
	    g2d.dispose();
	    //return the resized image
	    return bi;
	}

	//create the gui
	private void createGUI() {
		//create values for everything to be displayed
		container = getContentPane();
		chatArea = new JTextArea(31, 35);
		enterChat = new JTextField();
		send = new JButton("Send");
		bottomPanel = new JPanel();
		bottomButtons = new JPanel();
		image = new JButton("Image");
		upload = new JButton("Upload");
		settings = new JButton("Settings");
		//create a new scrollPane passing chat text area
		scrollPane = new JScrollPane(chatArea);
		//give the scroll pane a vertical scrollpane
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		//set the size of the buttons
		image.setPreferredSize(new Dimension(130, 25));
		upload.setPreferredSize(new Dimension(130, 25));
		settings.setPreferredSize(new Dimension(130, 25));
		//add to handler to each button
		send.addActionListener(handler);
		image.addActionListener(handler);
		upload.addActionListener(handler);
		settings.addActionListener(handler);
		//set the size of the bottomPanel
		bottomPanel.setPreferredSize(new Dimension(450, 120));
		//add the buttons to a JPanel
		bottomButtons.add(settings);
		bottomButtons.add(image);
		bottomButtons.add(upload);
		//create a JPanel and add the scrollPane to it
		chatPanel = new JPanel();
		chatPanel.add(scrollPane);
		//set the size of JTextField
		enterChat.setPreferredSize(new Dimension(250, 25));
		//prevent the user from editing the JTextArea
		chatArea.setEditable(false);
		//create JPanel and add the profilepic, textfield and send button
		sendPanel = new JPanel();
		sendPanel.add(profilePic);
		sendPanel.add(enterChat);
		sendPanel.add(send);
		//add the send panel and bottomButtons panel to one panel
		bottomPanel.add(sendPanel, BorderLayout.NORTH);
		bottomPanel.add(bottomButtons, BorderLayout.SOUTH);
		//add the chatPanel and bottomPanel to the container
		container.add(chatPanel, BorderLayout.CENTER);
		container.add(bottomPanel, BorderLayout.SOUTH);
		//set the size of the container and make it visible
		setSize(450, 670);
		setVisible(true);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setTitle("Chat Window for " + username);
		setResizable(false);
	}

	//Listen for server writing back to client
		public void ListenThread() {
			//create thread for incoming data and start it
			Thread IncomingReader = new Thread(new IncomingReader());
			IncomingReader.start();
		}
		
	//runs if the user attempts to connect
	private void connect(ActionEvent evt) {
		if (isConnected == false) {
			try {
				//create a new socket
				sock = new Socket(address, port);
				//create a new input stream
				InputStreamReader streamreader = new InputStreamReader(sock.getInputStream());
				//create a reader and writer
				reader = new BufferedReader(streamreader);
				writer = new PrintWriter(sock.getOutputStream());
				//welcome them to the chatroom
				chatArea.append("Welcome to the chat room ^_^\n");
				//tell the server who has connected
				writer.println(username + ":has connected.:Connect");
				//flush in case of failure
				writer.flush();
				//tell the client it is connected
				isConnected = true;
			} catch (Exception ex) {
				//prevent the chat area from displaying
				setVisible(false);
				//remove all components from chat area
				container.removeAll();
				//tell them there was an issue
				JOptionPane.showMessageDialog(null, "Sorry " + username 
						+ " we had an issue connecting you.\nPlease try again.");
				Object[] options1 = { connect, "Quit" };
				//allow them to try connect again
				JOptionPane.showOptionDialog(null, "Hit connect to connect to the chatroom", 
						"Connect to chatroom", JOptionPane.DEFAULT_OPTION, 
						JOptionPane.PLAIN_MESSAGE, null, options1, null);
			}
			//listen for activity
			ListenThread();
		} 
	}

	//used if the user hits send
	private void send(ActionEvent evt) {
		//check if nothing is in the textField
		if ((enterChat.getText()).equals("")) {
			//make sure there is nothing in 
			//text field
			enterChat.setText("");
			enterChat.requestFocus();
		} else { //otherwise send the message
			try {
				//send the message to the server
				writer.println(username + ":" + enterChat.getText() + ":" + "Chat");
				//flushes the buffer
				writer.flush(); 
			} catch (Exception ex) {
				//catch an exception of the message not sending
				chatArea.append("Message failed to send. \n");
			}
			//get rid of the typed message from text field
			//after it is sent
			enterChat.setText("");
			enterChat.requestFocus();
		}
		//make sure there is nothing in
		//text field
		enterChat.setText("");
		enterChat.requestFocus();
	}
	
	//used if the user hits upload
	private void upload(ActionEvent event) throws IOException {
		//tell the server an image is coming
		writer.println("sending:image:");
		writer.flush();
		//create a fileinputstream with the file chosen
		FileInputStream in = new FileInputStream(chosenFile);
		//create data output stream from the socket
		DataOutputStream out = new DataOutputStream(sock.getOutputStream());
		//write the byte array to the server
		int count;
		while ((count = in.read(imageArray)) > 0) {
			out.write(imageArray, 0, count);
		}
		//close the streams
		out.close();
		in.close();
		chatArea.append("Image has been uploaded to server.\n");
	}

	//main method to run client
	public static void main(String args[]) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					//try create a new client
					new Client();
				} catch (IOException e) {
					//throw IOException in case profile pic fails
					System.out.println("Failed to select profile Pic\n");
				}
			}
		});
	}

	//thread for incoming data from server
	public class IncomingReader implements Runnable {
		@Override
		public void run() {
			//create a string array
			String[] splitMessage;
			//create a string to read the data
			String message;
			
			try {
				//read each line of the data
				while ((message = reader.readLine()) != null) {
					//split it up to see check what we are reading
					splitMessage = message.split(":");
					if (splitMessage[2].equals("Chat")) {
						//if its a chat append the chatArea with the message
						chatArea.append(splitMessage[0] + ": " + splitMessage[1] + "\n");
						//force text area to scroll to the bottom 
						chatArea.setCaretPosition(chatArea.getDocument().getLength());
					} 
				}
			} catch (Exception ex) {
			}
		}
	}

	//buttonHandler class
	private class ButtonHandler implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			// check which button was pressed
			if (event.getActionCommand().equals("Settings")) {
				//if it was settings display JOptionPane with color options
				//create a JList which passes in the color names
				colorList = new JList<String>(colorNames);
				//make 5 rows visible at a time
				colorList.setVisibleRowCount(5);
				//only allow a user to select one option
				colorList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
				//create a list selection listener for the JList
				colorList.addListSelectionListener(new ListSelectionListener() {
					public void valueChanged(ListSelectionEvent event2) {
						//change the background colors of the panels to the
						//selected option
						bottomPanel.setBackground(colors[colorList.getSelectedIndex()]);
						chatPanel.setBackground(colors[colorList.getSelectedIndex()]);
					}
				});
				JOptionPane.showMessageDialog(null, new JScrollPane(colorList), "Change background color",
						JOptionPane.INFORMATION_MESSAGE);
			} else if (event.getActionCommand().equals("Image")) {
				//if it was image allow the user to choose an image
				//create fileChooser
				JFileChooser fileChooser = new JFileChooser();
				//only allow jpgs
				FileNameExtensionFilter filter = new FileNameExtensionFilter("JPG Images", "jpg");
				fileChooser.setFileFilter(filter);
				//create int to check if the selection is valid
				int returnVal = fileChooser.showOpenDialog(null);
				//store the file as a new file
				chosenFile = fileChooser.getSelectedFile();
				//create a byte array 
				imageArray = new byte[16 * 1024];
				//make sure choice is valid
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					try {
						//create a buffered image with chosen file
						BufferedImage bufferedImage = ImageIO.read(chosenFile);
						//use the bi to get a writableRaster
						WritableRaster raster = bufferedImage.getRaster();
						//use this to get the data in bytes
						DataBufferByte data = (DataBufferByte) raster.getDataBuffer();
						//store this in the array
						imageArray = data.getData();
					} catch (IOException e) {
						//catch exception
						chatArea.append("Failed to select image \n");
					}
					//tell the user the selected file
					chatArea.append("You chose to open this file: " +
		                    fileChooser.getSelectedFile().getName() + "\n");
				}
			} else if (event.getActionCommand().equals("Upload")) {
				try {
					//if its the upload button call the upload method
					upload(event);
				} catch (IOException e) {
					//catch exception
					chatArea.append("Failed to upload your image");
				}
			} else if (event.getActionCommand().equals("Send")) {
				//if it is send call the send action performed method
				send(event);
			} else if (event.getActionCommand().equals("Connect")) {
				//if it is connect dispose of the connect JOptionPane
				JOptionPane.getRootFrame().dispose();
				//create the gui
				createGUI();
				//call the connect action performed method
				connect(event);
			}
		}
	}
}