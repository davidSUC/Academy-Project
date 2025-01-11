import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.lang.Math;

public class Main {

    static int run = 500;
    static int[] a = new int[1000];

    public static void main(String[] args) throws IOException {

        FileWriter pw = new FileWriter("output.txt");

        Random rd = new Random();
        float p = 0.5F;
        float sample;
        int exp = 1;
        int success = 0;
        int limit = 0;
        float omu = (float) Math.sqrt(1000*p*(1-p));

        while(exp <= run) {
            for(int i=0; i<1000; i++){
                sample = rd.nextFloat();
                if(sample>=p) success++;
            }
            if( (success>=(500-(2*omu))) && (success<=(500+(2*omu))) ){
                limit++;
            }
            a[success]++;
            success = 0;
            exp++;
        }

        float percent = (float) limit/500;
        pw.write("Number of success in the limit range is " + limit);
        pw.write("\nPercent:" + percent*100 + "%\n");
        System.out.println("Number of success in the limit range is " + limit);
        System.out.println("Percent:" + percent*100 + "%");
        if(percent>=0.925 && percent<=0.975){
            System.out.println("The random generator is good");
            pw.write("The random generator is good");
        }
        else{
            System.out.println("The random generator is bad");
            pw.write("The random generator is bad");
        }
        pw.close();
    }
}