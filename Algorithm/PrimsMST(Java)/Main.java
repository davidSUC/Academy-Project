import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Main {

    static class uEdge{
        int Ni;
        int Nj;
        int cost;
        uEdge next;

        uEdge(int Ni, int Nj, int cost, uEdge next){
            this.Ni = Ni;
            this.Nj = Nj;
            this.cost = cost;
            this.next = next;
        }
    }

    static Scanner inFile;
    static FileWriter outFile1;
    static FileWriter MSTfile;

    static int numNodes;
    static int nodeInSetA;
    static char[] whichSet;
    static uEdge edgeListHead;
    static uEdge MSTlistHead;
    static int totalMSTCost;

    public static void main(String[] args) throws IOException {
        inFile = new Scanner(new FileReader(args[0]));
        nodeInSetA = Integer.parseInt(args[1]);
        outFile1 = new FileWriter(args[2]);
        MSTfile= new FileWriter(args[3]);

        numNodes = inFile.nextInt();;
        whichSet = new char[numNodes + 1];
        for(int i = 1; i < numNodes + 1 ; i++){
            whichSet[i] = 'B';
        }
        whichSet[0] = 'A';
        whichSet[nodeInSetA] = 'A';
        outFile1.write("***Initial set print***\n");
        printSet(whichSet, outFile1);
        outFile1.write("\n");
        edgeListHead = new uEdge(0, 0, 0, null);
        MSTlistHead = new uEdge(0, 0, 0, null);
        totalMSTCost = 0;

        uEdge newEdge, Spot;
        while(inFile.hasNext()){
            newEdge = new uEdge(inFile.nextInt(), inFile.nextInt(), inFile.nextInt(), null);
            Spot = findSpot(edgeListHead, newEdge);
            insertOneNode(Spot, newEdge);
            outFile1.write("In main() print the list of edges");
            printEdgeList(edgeListHead, outFile1);
        }

        uEdge e;
        uEdge minEdge;
        while(!isDone(whichSet)){
            e = edgeListHead;
            minEdge = new uEdge(0, 0, 99, null);
            while(e.next != null){
                e = e.next;
                if( (whichSet[e.Ni] != whichSet[e.Nj]) && e.cost < minEdge.cost){
                        minEdge = e;
                        break;
                }
            }
            //e = removeEdge(edgeListHead);
            rmEdge(minEdge);

            outFile1.write("\n\n----------------------------------------------------------------------\n");
            outFile1.write("***Pick edge***\n");
            printEdge(e, outFile1);
            updateMST(MSTlistHead, e);
            outFile1.write("\n**Updated set**\n");
            printSet(whichSet, outFile1);
            outFile1.write("\n**Updated edgeListHead**\n");
            printEdgeList(edgeListHead, outFile1);
            outFile1.write("\n**Updated MSTList**\n");
            printMSTList(MSTlistHead, outFile1);
            outFile1.write("----------------------------------------------------------------------\n");
        }

        MSTfile.write("*** Prim’s MST of the input graph, G is: ***");
        MSTfile.write("\n" + numNodes + "\n");
        printMST(MSTlistHead, MSTfile);
        MSTfile.write("*** MST total cost = " + totalMSTCost +  " ***");

        inFile.close();
        outFile1.close();
        MSTfile.close();
    }

    public static void printSet(char[] whichSet, FileWriter outFile1) throws IOException {
        for(int i = 1; i < numNodes + 1; i++){
            outFile1.write(i + " ");
        }
        outFile1.write("\n");
        for(int i = 1; i < numNodes + 1; i++){
            outFile1.write(whichSet[i] + " ");
        }
        outFile1.write("\n");
    }

    public static void printEdgeList(uEdge edgeListHead, FileWriter outFile1) throws IOException {
        outFile1.write("edgelistHead");
        uEdge tmp = edgeListHead;
        while(tmp.next != null){
            tmp = tmp.next;
            outFile1.write(" --> <" + tmp.Ni + ", " + tmp.Nj + ", " + tmp.cost + ">");
        }
        outFile1.write("\n");
    }

    public static void printMSTList(uEdge MSTlistHead, FileWriter outFile1) throws IOException {
        outFile1.write("MSTlistHead");
        uEdge tmp = MSTlistHead;
        while(tmp.next != null){
            tmp = tmp.next;
            if(tmp.next == null) {
                outFile1.write(" --> <" + tmp.Ni + ", " + tmp.Nj + ", " + tmp.cost + ", null" + ">");
            }
            else {
                outFile1.write(" --> <" + tmp.Ni + ", " + tmp.Nj + ", " + tmp.cost + ", " + tmp.next.Ni + ">");
            }
        }
        outFile1.write("\n");
    }

    public static void printMST(uEdge MSTlistHead, FileWriter MSTfile) throws IOException {
        uEdge tmp = MSTlistHead;
        while(tmp.next != null){
            tmp = tmp.next;
            MSTfile.write(tmp.Ni + " " + tmp.Nj + " " + tmp.cost + "\n");
        }
    }

    public static uEdge findSpot(uEdge edgeListHead, uEdge newEdge) {
        uEdge spot = edgeListHead;
        while(spot.next != null && spot.next.cost < newEdge.cost){
            spot = spot.next;
        }
        return spot;
    }

    public static void insertOneNode(uEdge spot, uEdge newEdge) {
        newEdge.next = spot.next;
        spot.next = newEdge;
    }

    public static boolean isDone(char[] whichSet) {
        for(int i = 1; i < numNodes + 1; i++){
            if(whichSet[i] == 'B'){
                return false;
            }
        }
        return true;
    }

    public static uEdge removeEdge(uEdge head){
        uEdge tmp = head.next;
        head.next = tmp.next;
        tmp.next = null;
        return tmp;
    }

    public static void printEdge(uEdge edge, FileWriter outFile1) throws IOException {
        outFile1.write("<" + edge.Ni + ", " + edge.Nj + ", " + edge.cost + ">\n");
    }

    public static void updateMST(uEdge MSTlistHead, uEdge edge) {
        pushEdgeToMST(MSTlistHead, edge);
        totalMSTCost += edge.cost;
        if(whichSet[edge.Ni] == 'A'){
            whichSet[edge.Nj] = 'A';
        }
        else{
            whichSet[edge.Ni] = 'A';
        }
    }

    public static void pushEdgeToMST(uEdge MSTlistHead, uEdge edge) {
        edge.next = MSTlistHead.next;
        MSTlistHead.next = edge;
    }

    public static void rmEdge(uEdge rmEdge){
        uEdge tmp = edgeListHead;
        while(tmp.next != rmEdge){
            tmp = tmp.next;
        }
        tmp.next = rmEdge.next;
        rmEdge.next = null;
    }
}