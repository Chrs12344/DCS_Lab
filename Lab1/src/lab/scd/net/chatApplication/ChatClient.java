package lab.scd.net.chatApplication;
import java.net.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.IOException;
import java.util.ArrayList;

public class ChatClient {
    private DatagramSocket socket; // Socket to send and receive messages
    private InetAddress address; // IP Address of the server
    private int serverPort; // Port of the server
    private String username; // Username of the client

    private JFrame frame;
    private JTextArea chatArea;
    private JTextField messageField;


    public ChatClient(String serverAddress, int serverPort, String username) throws SocketException, UnknownHostException {
        this.serverPort = serverPort; // Sets the port of the server
        socket = new DatagramSocket(); // Creates a datagram socket for sending datagrams
        address = InetAddress.getByName(serverAddress); // Gets the IP Address of the server
        this.username = username; // Sets the username of the client


        // Code for the GUI
        frame = new JFrame("Chat App - " + username);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        frame.add(new JScrollPane(chatArea), BorderLayout.CENTER);

        messageField = new JTextField();
        messageField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage(messageField.getText());
                messageField.setText("");
            }
        });
        frame.add(messageField, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    public void sendMessage(String message){
        try{
            String fullMessage = "[" + username + "]: " + message; // Adds the username to the message
            byte[] sendData = fullMessage.getBytes(); // Converts the message to bytes
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, address, serverPort); // Creates a datagram packet to send the message
            socket.send(sendPacket); // Sends the message
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    public void start(){
        try {
            while (true) {
                byte[] receiveData = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                socket.receive(receivePacket);

                String message = new String(receivePacket.getData(), 0, receivePacket.getLength());

                System.out.println(username + " has received a message: " + message);
                SwingUtilities.invokeLater(() -> chatArea.append(message + "\n")); // Update the chat area on the event dispatch thread
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            ChatClient client1 = new ChatClient("10.132.68.92", 5000, "Andrei");
            client1.start();
        } catch (SocketException | UnknownHostException e) {
            e.printStackTrace();
        }
    }
}
