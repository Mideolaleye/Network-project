public class TextDuplex {

    public static void main(String[] args) {
        // Create sender thread
        Thread senderThread = new Thread(() -> TextSender.main(null));

        // Create receiver thread
        Thread receiverThread = new Thread(() -> TextReceiver.main(null));

        // Start both threads
        senderThread.start();
        receiverThread.start();

        try {
            // Wait for both threads to finish
            senderThread.join();
            receiverThread.join();
        } catch (InterruptedException e) {
            System.out.println("ERROR: TextDuplex: Thread interrupted.");
            e.printStackTrace();
        }
    }
}
