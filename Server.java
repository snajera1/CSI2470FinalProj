import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;

public class Server extends JFrame {
    // Text area for displaying contents
    private JTextArea jta = new JTextArea();

    // Map to store name and phone number pairs
    private Map<String, String> contacts = new HashMap<>();

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
        setVisible(true); // It is necessary to show the frame here!

        try (ServerSocket serverSocket = new ServerSocket(8000)) {
            // Create a server socket
            //ServerSocket serverSocket = new ServerSocket(8000);
            jta.append("Server started at " + new Date() + '\n');

            // Listen for a connection request
            Socket socket = serverSocket.accept();

            // Create data input and output streams
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
            System.err.println(ex);
        }
    }
}
