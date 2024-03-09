public class heffidilman {
    public static void main(String[] args) {
        // Here are the hard coded variables
        int pk1 = 4; // First private key
        int pk2 = 3; // Second Private key
        int p = 98; // Shared public key
        int g = 40; // Shared public key
        int total = 1;
        //Calulating the first public key
        for (int i = 0; i < pk1; i++){
            total = total * g;
        }
        int remaindera = total % p; // Result of g^pk1 mod p
        System.out.println(remaindera); //Here is our first combined key

        //calculating the second public key
        int totalb = 1;
        for (int i = 0; i <pk2; i++) {
            totalb = totalb * g;
        }
        int remainderb = totalb % p; // Result of g^pk2 mod p
        System.out.println(remainderb); //Here is our second Combined key

        // decrypting the first private key
        int finaltotala = 1;
        for (int i = 0; i < pk1; i++){
            finaltotala = finaltotala * remainderb;
        }
        int finalremaindera = finaltotala % p; //Final secret (remainderb^pk1) mod p
        //============================================
        System.out.println(finalremaindera); //First Secret key

        // decrypting the second private key
        int finaltotalb = 1;
        for (int i = 0; i < pk2; i++){
            finaltotalb = finaltotalb * remaindera;
        }
        int finalremainderb = finaltotalb % p; //Final secret (remaindera^pk2) mod p
        System.out.println(finalremainderb); //Second Secret key


    }
}