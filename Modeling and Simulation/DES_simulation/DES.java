import java.io.*;
import java.util.LinkedList;
import java.util.Random;

public class DES {

    static double lambda = 0.1;
    static double mu = 0.125;
    static Random rand = new Random();
    static FileWriter pw;
    static {
        try {
            pw = new FileWriter("Trace30.txt");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static Customer serving;

    public static double getExp(Double p){
        return  Math.log(1-rand.nextDouble())/(-p);
    }

    public static void main(String[] args) throws IOException {

        LinkedList<Customer> waitList = new LinkedList<>();
        LinkedList<Customer> delayList = new LinkedList<>();

        boolean serverOn = true;
        boolean serverBusy = false;

        double clock_time = 0.0;
        double idle_time = 0.0;
        double last_idle = 0.0;
        double queue_length = 0.0;

        double arrive_time = getExp(lambda);
        double depart_time = Double.POSITIVE_INFINITY;

        int count = 1;
        int num_idle = 0;

        Customer newCustomer;

        arrive_time += getExp(lambda);
        newCustomer = new Customer(count++, arrive_time);
        waitList.add(newCustomer);
        pw.write( "Clock time: " + clock_time);
        pw.write("\nServer event: null");
        pw.write("\nDepart event: null");
        pw.write("\nFuture events:\n");
        for(Customer x : waitList){
            pw.write(x.printInfo());
        }
        pw.write("Delay list:\n");
        pw.write("\n");

        while(serverOn){

            if(arrive_time <= depart_time){
                clock_time = arrive_time;
                arrive_time += getExp(lambda);
                newCustomer = new Customer(count++, arrive_time);
                waitList.add(newCustomer);
                if(serverBusy){
                    delayList.add(waitList.pop());
                }
                else{
                    serving = waitList.pop();
                    depart_time = clock_time + getExp(mu);
                    num_idle++;
                    idle_time += (serving.arriveTime - last_idle);
                    queue_length += (depart_time - serving.arriveTime);
                    serverBusy = true;
                }
            }
            else{
                clock_time = depart_time;
                if(delayList.isEmpty()){
                    last_idle = clock_time;
                    depart_time = Double.POSITIVE_INFINITY;
                    serverBusy = false;
                }
                else{
                    serving = delayList.pop();
                    depart_time = clock_time + getExp(mu);
                    queue_length += depart_time-serving.arriveTime;
                }

            }

            if(serving.id <= 30){
                pw.write("Clock time: " + clock_time);
                if(depart_time==Double.POSITIVE_INFINITY) {
                    pw.write("\nServer event: idle");
                    pw.write("\nDepart event: null");
                }
                else{
                    pw.write("\nServer event: Serving Customer#" + serving.id + " arrive at " + serving.arriveTime);
                    pw.write("\nDepart event: Customer#" + serving.id + " at " + depart_time);
                }
                pw.write("\nFuture events:\n");
                for(Customer x : waitList){
                    pw.write(x.printInfo());
                }
                pw.write("Delay list:\n");
                for(Customer x : delayList){
                    pw.write(x.printInfo());
                }
                pw.write("\n");
            }
            if(count == 10000){
                serverOn = false;
            }
        }
        pw.write("Average server idle time is: " + idle_time/num_idle);
        pw.write("\nAverage queue length is: " + queue_length/count);
        pw.write("\nServer Idle%: " + idle_time/clock_time);
        pw.close();
    }
}
