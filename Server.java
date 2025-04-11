import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import javax.swing.*;
import java.awt.*;

public class Server extends JFrame {
    // Text area for displaying contents
    private JTextArea jta = new JTextArea();

    // Thread-safe map to store name and phone number pairs
    private ConcurrentHashMap<String, String> contacts = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        new Server();
    }

    public Server() {
        // Place text area on the frame
        setLayout(new BorderLayout());
        add(new JScrollPane(jta), BorderLayout.CENTER);

        setTitle("Server");
        setSize(500, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

        try {
            // Create a server socket
            ServerSocket serverSocket = new ServerSocket(8000);
            jta.append("Server started at " + new Date() + '\n');

            while (true) {
                // Accept a new client connection
                Socket socket = serverSocket.accept();
                jta.append("Client connected: " + socket.getInetAddress() + '\n');

                // Start a new thread for the client
                new Thread(new ClientHandler(socket)).start();
            }
        } catch (IOException ex) {
            jta.append("Error: " + ex.getMessage() + '\n');
        }
    }

    // Inner class to handle each client
    class ClientHandler implements Runnable {
        private Socket socket;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                // Create input and output streams
                DataInputStream inputFromClient = new DataInputStream(socket.getInputStream());
                DataOutputStream outputToClient = new DataOutputStream(socket.getOutputStream());

                while (true) {
                    // Receive the name and phone number from the client
                    String name = inputFromClient.readUTF();
                    String phoneNumber = inputFromClient.readUTF();

                    // Store them in the contacts map
                    contacts.put(name, phoneNumber);

                    // Send acknowledgment back to client
                    outputToClient.writeUTF("Contact added: " + name + " - " + phoneNumber);

                    // Display the contact in the JTextArea
                    jta.append("Received contact: " + name + " - " + phoneNumber + '\n');
                    jta.append("Current Contacts: " + contacts + '\n');
                }
            } catch (IOException ex) {
                jta.append("Client disconnected: " + socket.getInetAddress() + '\n');
            }
        }
    }
}
