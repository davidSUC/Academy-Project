import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Main {

    static Scanner inFile1, inFile2;
    static int numProcessors;
    static FileWriter outFile1, deBugFile;

    static class listNode{
        int jobID;
        int jobTime;
        listNode next;

        listNode(int jobID, int jobTime, listNode next){
            this.jobID = jobID;
            this.jobTime = jobTime;
            this.next = next;
        }
    }

    static int numNodes;
    static int numProcs, procUsed;
    static int currentTime = 0;
    static int totalJobTimes;
    static int[] jobTimeAry;

    static int[][] Matrix;
    static int[][] Table;

    static listNode Open;

    public static void main(String[] args) throws IOException {
        inFile1 = new Scanner(new FileReader(args[0]));
        inFile2 = new Scanner(new FileReader(args[1]));
        numProcessors = Integer.parseInt(args[2]);
        outFile1 = new FileWriter(args[3]);
        deBugFile = new FileWriter(args[4]);

        numNodes = inFile1.nextInt();;
        numProcs = numProcessors;
        if(numProcs <= 0){
            System.out.println("Need 1 or more processors");
            System.exit(0);
        }
        if(numProcs > numNodes){
            numProcs = numNodes;
        }

        //Step 2
        Matrix = new int[numNodes+1][numNodes+1];
        loadMatrix(inFile1, Matrix);
        outFile1.write("In main() after loadMatrix().\n");
        printMatrix(outFile1);
        setMatrix(Matrix, deBugFile);
        outFile1.write("\nIn main() after setMatrix().\n");
        printMatrix(outFile1);

        //Step 3
        Open = new listNode(-99, 0, null);
        currentTime = 0;
        procUsed = 0;

        //Step 4
        jobTimeAry = new int[numNodes + 1];
        totalJobTimes = loadJobTimeAry(inFile2, jobTimeAry, deBugFile);
        Table = new int[numProcs + 1][totalJobTimes + 1];
        outFile1.write( "\nin main() after allocate and initialize table.\n");
        printTable(outFile1, currentTime);

        //Step 5
        scheduling(outFile1, deBugFile);

        //Step 6
        outFile1.write("In main() printing Table and Open\n");
        printTable(outFile1, currentTime);
        printOpen(outFile1, currentTime);

        inFile1.close();
        inFile2.close();
        outFile1.close();
        deBugFile.close();
    }

    public static void loadMatrix(Scanner inFile1, int[][] matrix) {
        while(inFile1.hasNext()){
            matrix[inFile1.nextInt()][inFile1.nextInt()] = 1;
        }
    }

    public static void printMatrix(FileWriter outFile) throws IOException {
        for(int i = 0; i < numNodes + 1; i++){
            for (int j = 0; j < numNodes + 1; j++) {
                        outFile.write(Matrix[i][j] + " ");
            }
            outFile.write("\n");
        }
    }

    public static void setMatrix(int[][] matrix, FileWriter deBugFile) throws IOException {
        deBugFile.write("Entering setMatrix method!\n");
        Matrix[0][0] = numNodes;
        for (int j = 1; j <= numNodes; j++) {
            matrix[0][j] = countParent(matrix, j, deBugFile);
            matrix[j][0] = countDependent(matrix, j, deBugFile);
        }
        for (int i = 1; i <= numNodes; i++) {
            matrix[i][i] = 1;
        }
        deBugFile.write("Before leaving setMatrix method, the Matrix below\n");
        printMatrix(deBugFile);
        deBugFile.write("Leaving serMatrix().\n");
    }

    public static int countParent(int[][] matrix, int j, FileWriter deBugFile) throws IOException {
        deBugFile.write("Entering countParent() method!\n");
        int count = 0;
        for(int i = 1; i <= numNodes; i++){
            if(matrix[i][j] > 0){
                count++;
            }
        }
        deBugFile.write("In countParent() parent count is " + count);
        deBugFile.write("\nLeaving countParent() method.\n");
        return count;
    }

    public static int countDependent(int[][] matrix, int j, FileWriter deBugFile) throws IOException{
        deBugFile.write("Entering countDependent() method!\n");
        int count = 0;
        for(int i = 1; i <= numNodes; i++){
            if(matrix[j][i] > 0){
                count++;
            }
        }
        deBugFile.write("In countDependent() dependent count is " + count);
        deBugFile.write("\nLeaving countDependent() method.\n");
        return count;
    }

    public static int loadJobTimeAry(Scanner inFile, int[] jobTimeAry, FileWriter deBugFile) throws IOException {
        deBugFile.write("Entering loadJobTimeAry() method!\n");
        int jobTime = 0;
        int num = inFile.nextInt();
        for(int i = 1; i <= num; i++){
            jobTimeAry[inFile.nextInt()] = inFile.nextInt();
            jobTime += jobTimeAry[i];
            deBugFile.write(jobTime + "\n");
        }
        deBugFile.write("Leaving loadJobTimeAry() method.\n");
        return jobTime;
    }

    public static void printTable(FileWriter outFile, int currentTime) throws IOException {
            for(int n = 0; n < 15+totalJobTimes*8; n++) {
                outFile.write("=");
            }

            outFile.write("\nProcUsed:" + procUsed + "  currentTime:" + currentTime + "\n");
            outFile.write("Time:\t ");
            for(int i = 0; i <= totalJobTimes; i++) {
                outFile.write("   " + i + "\t");
            }

            outFile.write("\n");
            for(int n = 0; n < 15+totalJobTimes*8; n++) {
                outFile.write("-");
            }
            outFile.write("\n");

            for(int i = 1; i <= numProcs; i++){
                outFile.write("Proc:" + i + "\t|");
                for(int j = 0; j <= currentTime; j++){
                    outFile.write("   " + Table[i][j] + "\t|");
                }

                outFile.write("\n");
                for(int n = 0; n < 15+totalJobTimes*8; n++) {
                    outFile.write("-");
                }
                outFile.write("\n");

            }
            printOpen(outFile, currentTime);
    }

    public static void printOpen(FileWriter outFile, int currentTime) throws IOException {
        listNode tmp = Open.next;
        outFile.write("Open(Time_" + currentTime + "): ");
        while(tmp!=null){
            outFile.write("(" + tmp.jobID + ", " + tmp.jobTime + ")");
            if(tmp.next!=null){
                outFile.write(" --> ");
            }
            tmp = tmp.next;
        }
        outFile.write("\n");
    }

    public static void scheduling(FileWriter outFile1, FileWriter deBugFile) throws IOException {
        deBugFile.write("Entering scheduling() method!\n");

        while(!isGraphEmpty()) {
            loadOpen();
            outFile1.write("In scheduling() after loadOpen currentTime is " + currentTime + "\n");
            printOpen(outFile1, currentTime);

            loadTable();
            outFile1.write("after loadTable currentTime is " + currentTime + "\n");
            printTable(outFile1, currentTime);

            currentTime++;
            printOpen(outFile1, currentTime);

            deleteDoneJobs(currentTime, deBugFile);

            if (checkCycle()) {
                outFile1.write("\n***There is cycle in the graph!!!***\n\n");
//                System.exit(0);
                return;
            }
        }
        deBugFile.write("Leaving scheduling method.\n");
    }

    public static void loadOpen() throws IOException {
        listNode spot;
        deBugFile.write("Entering loadOpen() method!\n");
        for(int j = 1; j <= numNodes; j++){
            if(Matrix[0][j] == 0 && Matrix[j][j] == 1){
                Matrix[j][j] = 2;
                deBugFile.write("Find an orphan, the orphan is " + j + "\n");
                listNode newNode = new listNode(j, jobTimeAry[j], null);
                spot = findSpot(newNode);
                insertOneNode(spot, newNode);
            }
        }
        deBugFile.write("Before leaving loadOpen().\n");
        printOpen(deBugFile, currentTime);
        deBugFile.write("Leaving loadOpen() method.\n");
    }

    public static listNode findSpot(listNode newNode) {
        listNode spot = Open;
        while(spot.next != null && spot.next.jobTime < newNode.jobTime){
            spot = spot.next;
        }
        return spot;
    }

    public static void insertOneNode(listNode spot, listNode newNode) {
        newNode.next = spot.next;
        spot.next = newNode;
    }

    public static listNode popNode(listNode open){
        listNode tmp = open.next;
        open.next = tmp.next;
        tmp.next = null;
        return tmp;
    }
    public static void loadTable() throws IOException{
        deBugFile.write("Entering loadTable() method!\n");
        printOpen(deBugFile, currentTime);
        int availProc = getNextProc(currentTime, deBugFile);
        while(availProc >= 0 && Open.next!=null && procUsed <= numProcs){
                listNode newNode = popNode(Open);
                putJobOnTable(availProc, currentTime, newNode.jobID, newNode.jobTime);
                if(availProc > procUsed){
                    procUsed++;
                }
                availProc = getNextProc(currentTime, deBugFile);
        }
        deBugFile.write("Before leaving loadTable().\n");
        printTable(deBugFile, currentTime);
        printOpen(deBugFile, currentTime);
        deBugFile.write("Leaving loadTable() method.\n");
    }

    public static int getNextProc(int currentTime, FileWriter deBugFile) throws IOException {
        deBugFile.write("Entering getNextProc() method!\n");
        int i = 1;
        boolean notFound = true;
        while(notFound && i <= numNodes && i <= numProcs){
            if(Table[i][currentTime] == 0){
                deBugFile.write("in getNextProc() found an available proc#" + i + "\n");
                notFound = false;
                return i;
            }
            i++;
        }
        deBugFile.write("Leaving getNextProc() method. No available proc can be found.\n");
        return -1;
    }

    public static void putJobOnTable(int availProc, int currentTime, int jobID, int jobTime) {
        int Time = currentTime;
        int EndTime = Time + jobTime;
        while(Time < EndTime){
            Table[availProc][Time] = jobID;
            Time++;
        }
    }

    public static void deleteDoneJobs(int currentTime, FileWriter deBugFile) throws IOException {
        deBugFile.write("Entering deleteDoneJobs() method!\n");
        int proc = 1;
        while(proc <= procUsed) {
            if (Table[proc][currentTime] <= 0 && Table[proc][currentTime - 1] > 0) {
                int jobID = Table[proc][currentTime - 1];
                deleteOneJob(jobID, deBugFile);
            }
            proc++;
        }
        deBugFile.write("Leaving deleteDoneJobs() method below is the updated matrix.\n");
        printMatrix(deBugFile);
    }

    public static void deleteOneJob(int jobID, FileWriter deBugFile) throws IOException {
        deBugFile.write("Entering deleteOneJob() method!\n");

        Matrix[jobID][jobID] = 0;
        Matrix[0][0]--;
        for (int j = 1; j <= numNodes; j++) {
            if(Matrix[jobID][j] > 0){
                Matrix[0][j]--;
            }
        }
        deBugFile.write("Leaving deleteOneJob() method.\n");
    }

    public static boolean checkCycle() {
        if(Open.next == null && Matrix[0][0] > 0){
            for(int i = 1; i <= numProcs; i++){
                if(Table[i][currentTime-1] > 0){
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public static boolean isGraphEmpty() {
        if(Matrix[0][0] == 0){
            return true;
        }
        return false;
    }
}