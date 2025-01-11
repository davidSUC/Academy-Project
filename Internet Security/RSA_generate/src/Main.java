import java.math.BigInteger;
import java.util.Scanner;

public class Main {
    static BigInteger P;
    static BigInteger Q;
    static BigInteger Z;
    static BigInteger D;
    static BigInteger E;
    static BigInteger M;
    static BigInteger encryptM;

    static BigInteger funEncrypt(BigInteger data){
        return (data.modPow(E, P.multiply(Q)));
    }
    static BigInteger funDecryption(BigInteger enData) {
        return (enData.modPow(D, P.multiply(Q)));
    }

    public static void main(String[] args) {

        //P = 6845923947547629349
        //Q = 8239739191398474919277
        //D = 535324159

        Scanner in = new Scanner(System.in);
        System.out.print("Entering two prime numbers for P and Q.\nP=");
        P = new BigInteger(in.next());
        System.out.print("Q=");
        Q = new BigInteger(in.next());
         if(!P.isProbablePrime(1)){
            while(!P.isProbablePrime(1)){
                P = P.add(BigInteger.ONE);
            }
            System.out.println("P is not a prime number.\nAutomatically replace P with the prime number:" + P);
        }
        if(!Q.isProbablePrime(1)){
            while(!Q.isProbablePrime(1)){
                Q = Q.add(BigInteger.ONE);
            }
            System.out.println("Q is not a prime number.\nAutomatically replace Q with the prime number:" + Q);
        }
        BigInteger p_1 = P.subtract(BigInteger.ONE);
        Z = p_1.multiply(Q.subtract(BigInteger.ONE));
        System.out.print("Pick a number D which is relatively prime to Z=" + Z + "\nD=");
        D = new BigInteger(in.next());
        if(!(Z.gcd(D).equals(BigInteger.ONE))){
            while(!(Z.gcd(D).equals(BigInteger.ONE))){
                D = D.add(BigInteger.ONE);
            }
            System.out.println("D is not a relatively prime to Z.\nAutomatically replace D=" + D);
        }
        E = D.modInverse(Z);
        System.out.println("Public key: (" + P.multiply(Q) + ", " + E + ")");
        System.out.println("Private key: " + D);
        BigInteger en = new BigInteger("24497488907111242701747835400689155560264209703194291457750733646634521106074");
        System.out.println(en.modPow(E, P.multiply(Q)));
        boolean flag = true;
        while(flag) {
            System.out.println("Want to Encrypt a number? (enter Y to process)");
            char ans = in.next().charAt(0);
            if(ans == 'Y' || ans == 'y'){
                System.out.print("Enter message(number less than " + P.multiply(Q) + ") to encrypt:");
                M = new BigInteger(in.next());
                encryptM = funEncrypt(M);
                System.out.println("Encrypted message = " + encryptM);
                System.out.println(funDecryption(encryptM));
            }
            else{
                flag = false;
            }
        }
    }
}