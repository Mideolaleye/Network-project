import java.net.*;
import java.io.*;
import java.nio.ByteBuffer;

import CMPC3M06.AudioPlayer;
import uk.ac.uea.cmp.voip.DatagramSocket2;

public class TextReceiverThread implements Runnable {

    static DatagramSocket receiving_socket;

    public void start() {
        Thread thread = new Thread(this);
        thread.start();
    }

    public void run() {
        int PORT = 55555;
        int key = 15;

        try {
            receiving_socket = new DatagramSocket2(PORT);
        } catch (SocketException e) {
            System.out.println("ERROR: AudioReceiver: Could not open UDP socket to receive from.");
            e.printStackTrace();
            System.exit(0);
        }

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
}


