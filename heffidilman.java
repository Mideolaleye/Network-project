public class heffidilman {
    public static void main(String[] args) {
        // Here are the hard coded variables
        long pk1 = 6; // First private key
        long pk2 = 4; // Second Private key
        long p = 29; // Shared public key this has to be larger then g and has to be a prime number
        long g = 5; // Shared public key this has to be a modulo operator of p
        long total = 1;
        //Calculating the first public key
        for (long i = 0; i < pk1; i++){
             total = total * g;
        }
        long remaindera = total % p; // Result of g^pk1 mod p
        System.out.println(remaindera); //Here is our first combined key

        //calculating the second public key
        long totalb = 1;
        for (long i = 0; i < pk2; i++) {
            totalb = totalb * g;
        }
        long remainderb = totalb % p; // Result of g^pk2 mod p
        System.out.println(remainderb); //Here is our second Combined key

        // decrypting the first private key
        long finaltotala = 1;
        for (long i = 0; i < pk1; i++){
            finaltotala = finaltotala * remainderb;
        }
        long finalremaindera = finaltotala % p; //Final secret (remainderb^pk1) mod p
        //============================================
        System.out.println(finalremaindera); //First Secret key

        // decrypting the second private key
        long finaltotalb = 1;
        for (long i = 0; i < pk2; i++){
            finaltotalb = finaltotalb * remaindera;
        }
        long finalremainderb = finaltotalb % p; //Final secret (remaindera^pk2) mod p
        System.out.println(finalremainderb); //Second Secret key finally can now be sent/shown to the other party to decrypt the information

    }
}