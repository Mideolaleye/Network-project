import java.net.*;
import java.io.*;
import java.nio.ByteBuffer;

import CMPC3M06.AudioRecorder;
import uk.ac.uea.cmp.voip.DatagramSocket2;

public class TextSenderThread implements Runnable {

    static DatagramSocket sending_socket;
    static DatagramSocket receiving_socket;

    public void start() {
        Thread thread = new Thread(this);
        thread.start();
    }

    public void run() {
        int PORT = 55555;
        int key = 0;

        InetAddress clientIP = null;
        try {
            clientIP = InetAddress.getByName("localhost");  // Adjust to the receiver's IP address
        } catch (UnknownHostException e) {
            System.out.println("ERROR: AudioSender: Could not find client IP");
            e.printStackTrace();
            System.exit(0);
        }

        key = performKeyExchangeA(clientIP, PORT);

        try {
            sending_socket = new DatagramSocket2();
        } catch (SocketException e) {
            System.out.println("ERROR: AudioSender: Could not open UDP socket to send from.");
            e.printStackTrace();
            System.exit(0);
        }

        AudioRecorder recorder = null;
        try {
            recorder = new AudioRecorder();
        } catch (Exception e) {
            System.out.println("ERROR: AudioSender: Could not initialize the audio recorder.");
            e.printStackTrace();
            System.exit(0);
        }

        byte[] lastPacket = null; // Store the last successfully sent packet
        try {
            while (true && recorder != null) {
                byte[] audioBlock = recorder.getBlock();

                byte[] encryptedBlock = xorOperation(audioBlock, key);

                DatagramPacket packet = new DatagramPacket(encryptedBlock, encryptedBlock.length, clientIP, PORT);
                sending_socket.send(packet);

                lastPacket = encryptedBlock; // Update the last sent packet

                // Add delay to control the sending rate if necessary
                // Thread.sleep(10); // Adjust the delay as needed
            }
        } catch (Exception e) {
            System.out.println("ERROR: AudioSender: Some error occurred while capturing or sending audio!");
            e.printStackTrace();
            // If an error occurs, try to resend the last packet
            if (lastPacket != null) {
                try {
                    DatagramPacket resendPacket = new DatagramPacket(lastPacket, lastPacket.length, clientIP, PORT);
                    sending_socket.send(resendPacket);
                } catch (IOException ioException) {
                    System.out.println("ERROR: AudioSender: Failed to resend the last packet.");
                    ioException.printStackTrace();
                }
            }
        } finally {
            if (recorder != null) {
                recorder.close();
            }
            sending_socket.close();
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

    private static int performKeyExchangeA(InetAddress clientIP, int PORT){

        int RECEIVE_PORT = 55556;
        int privateKey = 15;
        int p = 23;
        int g = 5;

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


        try {
            //============== P packet
            byte[] Pbuffer = ByteBuffer.allocate(Integer.BYTES).putInt(p).array();
            DatagramPacket pPacket = new DatagramPacket(Pbuffer, Pbuffer.length, clientIP, PORT);
            //============== G packet

            byte[] Gbuffer = ByteBuffer.allocate(Integer.BYTES).putInt(g).array();
            DatagramPacket gPacket = new DatagramPacket(Gbuffer, Gbuffer.length, clientIP, PORT);

            //============== first combined
            int C1 = hellman(privateKey, p, g);
            byte[] C1buffer = ByteBuffer.allocate(Integer.BYTES).putInt(C1).array();
            DatagramPacket C1Packet = new DatagramPacket(C1buffer, C1buffer.length, clientIP, PORT);

            sending_socket.send(pPacket);
            sending_socket.send(gPacket);
            sending_socket.send(C1Packet);

            byte[] receiveBuffer = new byte[Integer.BYTES];
            DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
            receiving_socket.receive(receivePacket);
            int C2 = ByteBuffer.wrap(receivePacket.getData()).getInt();

            receiving_socket.close();
            sending_socket.close();

            return hellman(privateKey, p , C2);

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
        return total % p;
    }

}
