import java.nio.ByteBuffer;
import java.util.Vector;
import java.util.Iterator;

import CMPC3M06.AudioPlayer;
import CMPC3M06.AudioRecorder;

public class AudioTest {
    public static void main(String args[]) throws Exception {
        Vector<byte[]> encryptedVoiceVector = new Vector<>();
        Vector<byte[]> decryptedVoiceVector = new Vector<>();
        AudioRecorder recorder = new AudioRecorder();
        AudioPlayer player = new AudioPlayer();

        // Encryption and the Decryption key
        int key = 15;

        int recordTime = 10; // the recording time our audio (sender)

        System.out.println("Recordin and Encrypting and Decrypting the Audio...");

        //here we are recording and encrypting our audio
        for (int i = 0; i < Math.ceil(recordTime / 0.032); i++) {
            byte[] block = recorder.getBlock();

            //we are encrypting our block
            byte[] encryptedBlock = xorOperation(block, key);
            encryptedVoiceVector.add(encryptedBlock);


            // we are instantly decrypting the block
            byte[] decryptedBlock = xorOperation(encryptedBlock, key);
            decryptedVoiceVector.add(decryptedBlock);
            //this was just a test to see if the encryption would work
        }

        recorder.close();


        System.out.println("Playing Decrypted Audio -");
        Iterator<byte[]> voiceItr = decryptedVoiceVector.iterator();
        while (voiceItr.hasNext()) {
            player.playBlock(voiceItr.next());
        }

        player.close();
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
