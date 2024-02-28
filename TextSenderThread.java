import java.net.*;

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

                DatagramPacket packet = new DatagramPacket(audioBlock, audioBlock.length, clientIP, PORT);
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
}
