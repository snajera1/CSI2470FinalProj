import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Client extends JFrame {
    // Text fields for name and phone number
    private JTextField jtfName = new JTextField();
    private JTextField jtfPhoneNumber = new JTextField();

    // Text area to display contents
    private JTextArea jta = new JTextArea();

    // Button to export the data to a text file
    private JButton jb = new JButton("Export to File");

    // IO streams
    private DataOutputStream toServer;
    private DataInputStream fromServer;

    public static void main(String[] args) {
        new Client();
    }

    public Client() {
        // Panel p to hold the labels and text fields
        JPanel p = new JPanel();
        p.setLayout(new GridLayout(3, 2));
        p.add(new JLabel("Enter Name:"));
        p.add(jtfName);
        p.add(new JLabel("Enter Phone Number:"));
        p.add(jtfPhoneNumber);
        p.add(jb);

        setLayout(new BorderLayout());
        add(p, BorderLayout.NORTH);
        add(new JScrollPane(jta), BorderLayout.CENTER);

        jb.addActionListener(new ButtonListener()); // Register listener
        jtfPhoneNumber.addActionListener(new ButtonListener()); // Register listener

        setTitle("Client");
        setSize(500, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true); // It is necessary to show the frame here!

    }

    private class ButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            try (Socket socket = new Socket("localhost", 8000);
                 DataOutputStream toServer = new DataOutputStream(socket.getOutputStream());
                 DataInputStream fromServer = new DataInputStream(socket.getInputStream())) {

                // Get the name and phone number from the text fields
                String name = jtfName.getText().trim();
                String phoneNumber = jtfPhoneNumber.getText().trim();

                // Send the name and phone number to the server
                toServer.writeUTF(name);
                toServer.writeUTF(phoneNumber);
                toServer.flush();
                // Get the server response
                String serverResponse = fromServer.readUTF();

                // Display to the text area
                jta.append("Sent contact: " + name + " - " + phoneNumber + "\n");
                jta.append("Server response: " + serverResponse + "\n");
                
                socket.close();
            } catch (IOException ex) {
                jta.append(ex.toString() + '\n');
            }
        }
    }

}
