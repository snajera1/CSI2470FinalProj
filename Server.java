import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import javax.swing.*;
import java.awt.*;
import java.io.FileWriter;
import java.io.File;

public class Server extends JFrame {
    // Text area for displaying contents
    private JTextArea jta = new JTextArea();

    // HashMap to store name and phone number pairs
    private ConcurrentHashMap<String, String> contacts = new ConcurrentHashMap<>();

    // Text file to store the contact info
    public static final File contactBook = new File("contactbook.txt");

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

        try (ServerSocket serverSocket = new ServerSocket(8000)) {
            // Create a server socket
            //ServerSocket serverSocket = new ServerSocket(8000);
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

                    // Writes the contact to a text file
                    try {
                        FileWriter writer = new FileWriter("contactBook.txt", true);
                        writer.write(name + " - " + phoneNumber + "\n");
                        writer.close();
                        outputToClient.writeUTF("Successfully written to file.");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            } catch (IOException ex) {
                jta.append("Client disconnected: " + socket.getInetAddress() + '\n');
            }
        }
    }
}
