import java.net.*;
import java.io.*;
import java.nio.ByteBuffer;

public class TextSender {

    static DatagramSocket sending_socket;
    static int p = 23; // Example value for p
    static int g = 5;  // Example value for g

    public static void main (String[] args){

        // Port to send to
        int PORT = 55555;

        // IP ADDRESS to send to
        InetAddress clientIP = null;
        try {
            clientIP = InetAddress.getByName("localhost");
        } catch (UnknownHostException e) {
            System.out.println("ERROR: TextSender: Could not find client IP");
            e.printStackTrace();
            System.exit(0);
        }

        // Open a socket to send from
        try {
            sending_socket = new DatagramSocket();
        } catch (SocketException e) {
            System.out.println("ERROR: TextSender: Could not open UDP socket to send from.");
            e.printStackTrace();
            System.exit(0);
        }

        // Send p and g to receiver
        sendPG(clientIP, PORT);
    }

    private static void sendPG(InetAddress clientIP, int PORT) {
        try {
            // Send the value of p
            byte[] pBuffer = ByteBuffer.allocate(4).putInt(p).array();
            DatagramPacket pPacket = new DatagramPacket(pBuffer, pBuffer.length, clientIP, PORT);
            sending_socket.send(pPacket);

            // Send the value of g
            byte[] gBuffer = ByteBuffer.allocate(4).putInt(g).array();
            DatagramPacket gPacket = new DatagramPacket(gBuffer, gBuffer.length, clientIP, PORT);
            sending_socket.send(gPacket);

            System.out.println("Values of p and g sent successfully.");
        } catch (IOException e) {
            System.out.println("ERROR: TextSender: Some random IO error occurred!");
            e.printStackTrace();
        } finally {
            // Close the socket after sending the packets
            sending_socket.close();
        }
    }
}
