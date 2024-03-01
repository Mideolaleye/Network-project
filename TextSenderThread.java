import java.net.*;
import java.nio.ByteBuffer;

import CMPC3M06.AudioRecorder;

public class TextSenderThread implements Runnable{

    static DatagramSocket sending_socket;

    public void start(){
        Thread thread = new Thread(this);
        thread.start();
    }

    public void run (){
        int PORT = 55555;
        InetAddress clientIP = null;
        int key = 15;
        try {
            clientIP = InetAddress.getByName("localhost");  // Adjust to the receiver's IP address
        } catch (UnknownHostException e) {
            System.out.println("ERROR: AudioSender: Could not find client IP");
            e.printStackTrace();
            System.exit(0);
        }

        try{
            sending_socket = new DatagramSocket();
        } catch (SocketException e){
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

        boolean running = true;
        while (running && recorder != null){
            try{
                byte[] audioBlock = recorder.getBlock();

                byte[] encryptedBlock = xorOperation(audioBlock, key);

                DatagramPacket packet = new DatagramPacket(encryptedBlock, encryptedBlock.length, clientIP, PORT);
                sending_socket.send(packet);

                // Implement a proper mechanism to stop the recording and sending process

            } catch (Exception e){
                System.out.println("ERROR: AudioSender: Some error occurred while capturing or sending audio!");
                e.printStackTrace();
            }
        }
        if (recorder != null) {
            recorder.close();
        }
        sending_socket.close();
    }
    private static byte[] xorOperation(byte[] block, int key) {
        ByteBuffer buffer = ByteBuffer.wrap(block);
        ByteBuffer resultBuffer = ByteBuffer.allocate(block.length);

        for (int j = 0; j < block.length / 4; j++) {
            int fourByte = buffer.getInt();
            fourByte = fourByte ^ key; //XOR operation being used here
            resultBuffer.putInt(fourByte);
        }

        return resultBuffer.array();
    }
}

