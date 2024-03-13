import java.net.*;
import java.io.*;
import java.nio.ByteBuffer;

import CMPC3M06.AudioPlayer;

public class TextReceiverThread implements Runnable {
    static DatagramSocket sending_socket;
    static DatagramSocket receiving_socket;


    public void start() {
        Thread thread = new Thread(this);
        thread.start();
    }

    public void run() {
        int PORT = 55555;
        int key = 0;

        try {
            receiving_socket = new DatagramSocket(PORT);
        } catch (SocketException e) {
            System.out.println("ERROR: AudioReceiver: Could not open UDP socket to receive from.");
            e.printStackTrace();
            System.exit(0);
        }

        key = performKeyExchange();
        System.out.println(key+" receiver side key");

        AudioPlayer player = null;
        try {
            player = new AudioPlayer();
        } catch (Exception e) {
            System.out.println("ERROR: AudioReceiver: Could not initialize the audio player.");
            e.printStackTrace();
            System.exit(0);
        }

        byte[] lastPacket = null; // Store the last successfully received packet
        try {
            performKeyExchange(); // Perform Diffie-Hellman key exchange

            while (true && player != null) {
                byte[] buffer = new byte[512]; // Adjust buffer size as per audio block size
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                receiving_socket.receive(packet);

                byte[] decryptedBlock = xorOperation(packet.getData(), key);

                // Play the audio block
                player.playBlock(decryptedBlock);

                lastPacket = decryptedBlock; // Update the last received packet
            }
        } catch (Exception e) {
            System.out.println("ERROR: AudioReceiver: Some error occurred while receiving or playing audio!");
            e.printStackTrace();
            // If an error occurs, try to replay the last packet
            if (lastPacket != null && player != null) {
                try {
                    // Assuming playBlock can handle null or empty data gracefully
                    player.playBlock(lastPacket);
                } catch (Exception exception) {
                    System.out.println("ERROR: AudioReceiver: Failed to replay the last packet.");
                    exception.printStackTrace();
                }
            }
        } finally {
            if (player != null) {
                player.close();
            }
            receiving_socket.close();
        }
    }

    private static byte[] xorOperation(byte[] block, int key) {
        ByteBuffer buffer = ByteBuffer.wrap(block);
        ByteBuffer resultBuffer = ByteBuffer.allocate(block.length);

        for (int j = 0; j < block.length / 4; j++) {
            int fourByte = buffer.getInt();
            fourByte = fourByte ^ key; // XOR operation being used here
            resultBuffer.putInt(fourByte);
        }

        return resultBuffer.array();
    }
    private static int performKeyExchange(){
        int RECEIVE_PORT = 55557;
        int SEND_PORT = 55556;
        int privateKey = 3;
        int p = 23;
        int g = 5;

        InetAddress clientIP = null;
        try {
            clientIP = InetAddress.getByName("localhost");  // Adjust to the Sender's IP address
        } catch (UnknownHostException e) {
            System.out.println("ERROR: AudioSender: Could not find client IP");
            e.printStackTrace();
            System.exit(0);
        }

        try {
            sending_socket = new DatagramSocket();
        } catch (SocketException e) {
            System.out.println("ERROR: TextSender: Could not open UDP socket to send from.");
            e.printStackTrace();
            System.exit(0);
        }

        try {
            receiving_socket = new DatagramSocket(RECEIVE_PORT);
        } catch (SocketException e) {
            System.out.println("ERROR: TextSender: Could not open UDP socket to receive from.");
            e.printStackTrace();
            System.exit(0);
        }

        try{
//            byte[] PreceiveBuffer = new byte[Integer.BYTES];
//            DatagramPacket PreceivePacket = new DatagramPacket(PreceiveBuffer, PreceiveBuffer.length);
//            receiving_socket.receive(PreceivePacket);
//            int p = ByteBuffer.wrap(PreceivePacket.getData()).getInt();
//            System.out.println("P="+p);
//
//            byte[] GreceiveBuffer = new byte[Integer.BYTES];
//            DatagramPacket GreceivePacket = new DatagramPacket(GreceiveBuffer, GreceiveBuffer.length);
//            receiving_socket.receive(GreceivePacket);
//            int g = ByteBuffer.wrap(GreceivePacket.getData()).getInt();
//            System.out.println("G="+p);


            byte[] C1receiveBuffer = new byte[Integer.BYTES];
            DatagramPacket C1receivePacket = new DatagramPacket(C1receiveBuffer, C1receiveBuffer.length);
            receiving_socket.receive(C1receivePacket);
            int C1 = ByteBuffer.wrap(C1receivePacket.getData()).getInt();

            int C2 = hellman(privateKey,p,g);
            byte[] C2buffer = ByteBuffer.allocate(Integer.BYTES).putInt(C2).array();
            DatagramPacket C2Packet = new DatagramPacket(C2buffer, C2buffer.length, clientIP, SEND_PORT);
            sending_socket.send(C2Packet);

            receiving_socket.close();
            sending_socket.close();
            System.out.println("hellman-receiver success ");
            System.out.println("secret key reciver:"+hellman(privateKey,p,C1));
            return hellman(privateKey,p,C1);

        }catch (IOException e){
            System.out.println("ERROR: TextSender: Some random IO error occurred!");
            e.printStackTrace();
        }


        return -1;
    }
    private static int hellman(int k, int p, int g){//k = raise/key/ // p = p // g = g
        int total =1;
        for(int i = 0; i<k;i++){
            total = total * g;
        }
        System.out.println(total%p+"receiver");
        return total % p;
    }

}


