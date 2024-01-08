package lab.scd.net.chatApplication;

import java.io.*;
import java.net.*;
import java.util.*;
public class ChatServer extends Thread{
    private DatagramSocket socket = null;
    private ArrayList<Clients> clients = new ArrayList<Clients>(); // List that stores the clients
    private int port; // Port on which the server is listening

    public ChatServer(int port) throws SocketException{
        this.port = port;
        socket = new DatagramSocket(port); // Creates a data gram socket on that listens to the port
    }

    private boolean isClientInList(InetAddress address) {
        for (Clients client : clients) {
            if (client.address.equals(address)) {
                return true; // Client is already in the list
            }
        }
        return false; // Client is not in the list
    }

    public void run(){
        System.out.println("Server is running on port: " + port);

        while(true){
            try{
                byte[] receiveData = new byte[1024]; // Buffer to store incomming messages
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length); // Creates a datagram packet to receive the message
                socket.receive(receivePacket); // Receives the message

                String message = new String(receivePacket.getData(), 0, receivePacket.getLength()); // Converts the message to string
                InetAddress senderAddress = receivePacket.getAddress(); // Gets the address of the sender
                int senderPort = receivePacket.getPort(); // Gets the port of the sender

                if (!isClientInList(senderAddress)) {
                    clients.add(new Clients(senderAddress, senderPort)); // Add the client to the list
                }


                System.out.println("Message from " + senderAddress + ": " + message); // Prints the message

                // Broadcast the message to all the clients
                byte[] sendData = message.getBytes(); // Create a new byte array for sending
                for (Clients client : clients) {
                    InetAddress clientAddress = client.address;
                    int clientPort = client.port;

                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, clientAddress, clientPort); // Creates a datagram packet to send the message
                    socket.send(sendPacket); // Sends the message
                    System.out.println("Message sent to " + clientAddress + ":" + clientPort + ": " + message); // Prints the message
                }

            } catch(IOException e){
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws SocketException {
        // Start the server
        ChatServer server = new ChatServer(5000);
        server.start();
    }

    private class Clients{
        InetAddress address;
        int port;

        public Clients(InetAddress address, int port){
            this.address = address;
            this.port = port;
        }

    }
}


