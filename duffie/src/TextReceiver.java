import java.net.*;
import java.io.*;
import java.nio.ByteBuffer;

public class TextReceiver {

    static DatagramSocket receiving_socket;
    static int p;
    static int g;

    public static void main (String[] args){

        // Port to listen on
        int PORT = 55555;

        // Create a buffer to store received data
        byte[] buffer = new byte[4]; // Assuming integers are 4 bytes

        // Create DatagramPacket to receive data
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

        try {
            // Create receiving socket
            receiving_socket = new DatagramSocket(PORT);

            // Receive packet containing the value of p
            receiving_socket.receive(packet);
            p = ByteBuffer.wrap(buffer).getInt();

            // Receive packet containing the value of g
            receiving_socket.receive(packet);
            g = ByteBuffer.wrap(buffer).getInt();

            // Close the socket after receiving both packets
            receiving_socket.close();

            // Print received values of p and g
            System.out.println("Received value of p: " + p);
            System.out.println("Received value of g: " + g);

            // Generate private key
            int privateKey = generatePrivateKey();

            // Calculate public key
            int publicKey = calculatePublicKey(privateKey);

            // Send public key to sender
            sendPublicKey(publicKey, packet.getAddress(), packet.getPort());

            // Calculate shared secret key
            int sharedSecretKey = calculateSharedSecretKey(publicKey);

            // Print shared secret key
            System.out.println("Shared secret key: " + sharedSecretKey);

        } catch (IOException e) {
            System.out.println("ERROR: TextReceiver: Some error occurred while receiving data!");
            e.printStackTrace();
        }
    }

    private static int generatePrivateKey() {
        // Generate a random private key (for demonstration, you can implement a proper method to generate a private key)
        return 10; // Example private key
    }

    private static int calculatePublicKey(int privateKey) {
        // Calculate public key using p, g, and the private key
        return (int) Math.pow(g, privateKey) % p;
    }

    private static void sendPublicKey(int publicKey, InetAddress clientIP, int PORT) {
        try {
            // Send the public key to sender
            byte[] publicKeyBuffer = ByteBuffer.allocate(4).putInt(publicKey).array();
            DatagramSocket sending_socket = new DatagramSocket();
            DatagramPacket publicKeyPacket = new DatagramPacket(publicKeyBuffer, publicKeyBuffer.length, clientIP, PORT);
            sending_socket.send(publicKeyPacket);
            sending_socket.close();
            System.out.println("Public key sent successfully.");
        } catch (IOException e) {
            System.out.println("ERROR: TextReceiver: Some random IO error occurred!");
            e.printStackTrace();
        }
    }

    private static int calculateSharedSecretKey(int publicKey) {
        // Calculate the shared secret key using the received public key and the private key
        return (int) Math.pow(publicKey, generatePrivateKey()) % p;
    }
}
