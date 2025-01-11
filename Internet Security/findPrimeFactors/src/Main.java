import java.math.BigInteger;
import java.util.ArrayList;

public class Main {

    static BigInteger P;
    static BigInteger Q;
    static BigInteger Z;
    static BigInteger D;
    static BigInteger E;
    static BigInteger M;
    static BigInteger encryptM;
    static class keyPair{
        BigInteger a, b;
        public keyPair(BigInteger a, BigInteger b){
            this.a = a;
            this.b = b;
        }
    }
    public static void main(String[] args) {
        //ArrayList<keyPair> keyPairArrayList = new ArrayList<keyPair>();
        E = new BigInteger("65537");
        BigInteger num = new BigInteger("70512589685011071777468625557639675445185881393816205481590365577900586136257268348265645517841184686760456019730532241441339882439711527803159514289970590325771894887635094091302079035259014999276167901125589604974152359989603259336768575113124793755429391590242496402371182116586301586892514841431895429561431893920369098389186658273345994953383123459089245335263873564633256545981622191324006728299051214482278266612912209662289983990494648954870278188557368860333464292409980258125254937200258562557646040430468509224972842471109857292524549866287452417557526147613515769951761127618848421152459295192728433887093397490078058336354078062755652917090658141789984974449421649945347592244471957311271394070634999821783248812970690199177116763038801722339171371989298713376631058074916832567677440229");
        BigInteger data = new BigInteger("32708545466541722415404007828691542221468392726864593506054223334664772623811318428559584972910205956479210801638887240523943926552644416343079319365192529590755408108482169133117600312895650938155887920221386213589186365183400897232728434577779083141895318130255708645301818124781602061108363909677408471303272003858657747056328779923185112948512376249479776679224764494373824243879776850003011284077352391620107431407003941181526396965033335352343283257928746339760230961649680973453193073956774419762828066279104755139461582892516236016988846069687329102889819709299240322802486940860446384048539173308118791134920926871075403965941114079317224678191278476840776724415640407697687541923766334494985972972850795862464212343780535360728003537024562023781359264751250462389453167089562161368108887681");
/*        BigInteger end = num.sqrt();
        BigInteger i = BigInteger.ONE;*/
        BigInteger i = num.sqrt();
        boolean flag = true;
        while(i.compareTo(BigInteger.ONE)>0 && flag){
            if(i.isProbablePrime(1) && num.divide(i).isProbablePrime(1) && (num.mod(i)).equals(BigInteger.ZERO)){
                //keyPairArrayList.add(new keyPair(i, num.divide(i)));
                P = i;
                Q = num.divide(i);
                Z = P.subtract(BigInteger.ONE).multiply(Q.subtract(BigInteger.ONE));
                D = E.modInverse(Z);
                if(Z.gcd(D).equals(BigInteger.ONE)){
                    System.out.println("P=" + P + ", Q=" + Q);
                    System.out.println("Z=" + Z);
                    System.out.println("E=" + E);
                    System.out.println("D=" + D);
                    System.out.println(data.modPow(E, P.multiply(Q)));
                    flag = false;
                }
            }
            i = i.subtract(BigInteger.TWO);
            if(i.toString().charAt(i.toString().length()-1) == '5'){
                i = i.subtract(BigInteger.TWO);
                i = i.subtract(BigInteger.TWO);
            }
        }
        //keyPairArrayList.forEach(keyPair -> System.out.println(keyPair.a + " " + keyPair.b ));
    }
}