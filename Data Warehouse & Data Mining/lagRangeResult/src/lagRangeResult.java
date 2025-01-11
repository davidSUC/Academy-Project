import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class lagRangeResult {

    static Scanner inFile;

    public static void main(String[] args) throws IOException {

       double inputX = Double.parseDouble(args[0]);

        inFile = new Scanner(new FileReader("input.txt"));
        int n = inFile.nextInt();
        double[] x = new double[n];
        double[] y = new double[n];
        for (int i = 0; i < n; i++) {
            x[i] = inFile.nextFloat();
            y[i] = inFile.nextFloat();
        }
        inFile.close();

        double result = 0.0;
        double term;
        for(int i = 0; i < n; i++){
            term = y[i];
            for(int j = 0; j < n; j++){
                if(i != j){
                    term *= (inputX - x[j]) / (x[i] - x[j]);
                }
            }
            result += term;
        }
        System.out.println(result);
    }
}