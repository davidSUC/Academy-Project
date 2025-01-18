import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Main {

    static Scanner inFile;
    static FileWriter outFile1, deBugFile;

    static class listNode{
        String data;
        listNode next;

        listNode(){
            data = "dummy";
            next = null;
        }

        listNode(String data, listNode next){
            this.data = data;
            this.next = next;
        }
    }

    static class LLQ{
        listNode head;
        listNode tail;

        LLQ(){
            head = new listNode();
            tail = head;
        }
    }
    public static void insertQ(LLQ llq, listNode newNode) {
        llq.tail.next = newNode;
        llq.tail = newNode;
    }

    public static listNode deleteQ(LLQ llq){
        if(llq.head.next != null){
            listNode tmp = llq.head.next;
            if(tmp == llq.tail){
                llq.tail = llq.head;
            }
            llq.head.next = tmp.next;
            tmp.next = null;
            return tmp;
        }
        return null;
    }

    public static boolean isEmpty(LLQ llq){
        return llq.head == llq.tail;
    }

    static int tableSize = 256;
    static LLQ[][] hashTable;
    static String data;
    static int currentTable, nextTable;
    static int longestStringLength;
    static int currentIndex;

    public static void main(String[] args) throws IOException {
        inFile = new Scanner(new FileReader(args[0]));
        outFile1 = new FileWriter(args[1]);
        deBugFile = new FileWriter(args[2]);

        hashTable = new LLQ[2][tableSize];
        for(int i = 0; i < 2; i++){
            for(int j = 0; j < tableSize; j++){
                hashTable[i][j] = new LLQ();
            }
        }

        firstReading(inFile, deBugFile);
        inFile.close();

        inFile = new Scanner(new FileReader(args[0]));
        RSort(inFile, outFile1, deBugFile);

        inFile.close();
        outFile1.close();
        deBugFile.close();
    }

    public static void firstReading(Scanner inFile, FileWriter deBugFile) throws IOException {
        deBugFile.write("Performing first Reading to find the longest data string in the input file");
        longestStringLength = 0;
        while(inFile.hasNext()) {
            data = inFile.next();
            if(longestStringLength < data.length()){
                longestStringLength = data.length();
            }
        }
    }

    public static void RSort(Scanner inFile, FileWriter outFile1, FileWriter deBugFile) throws IOException {
        deBugFile.write("Entering RSort, Performing Radix Sort.\n");
        populateFirstTable(inFile, outFile1, deBugFile);

        nextTable = currentTable;
        while(currentIndex >= 0){
            currentTable = (++currentTable) % 2;
            deBugFile.write("In RSort, after swap tables, currentIndex = " + currentIndex + "; currentTable = " + currentTable + ", nextTable = " + nextTable + "\n");
            int tableIndex = 0;
            listNode newNode;
            String tempData;
            int hashIndex;
            while(tableIndex < tableSize){
                while(!isEmpty(hashTable[nextTable][tableIndex])) {
                    newNode = deleteQ(hashTable[nextTable][tableIndex]);
                    if (newNode != null) {
                        tempData = newNode.data;
                        hashIndex = tempData.charAt(currentIndex);
                        insertQ(hashTable[currentTable][hashIndex], newNode);
                    }
                }
                tableIndex++;
            }
            printTable(hashTable[currentTable], deBugFile);
            currentIndex--;
            nextTable = currentTable;
        }
        printSortedData(nextTable, outFile1);
        deBugFile.write("Leaving RSort, Performing Radix Sort.\n");
    }


    public static void populateFirstTable(Scanner inFile, FileWriter outFile1, FileWriter deBugFile) throws IOException {
        deBugFile.write("Entering populateFirstTable().\n");
        currentIndex = longestStringLength - 1;
        currentTable = 0;

        String paddedData;
        listNode newNode;
        int hashIndex;
        while(inFile.hasNext()){
            data = inFile.next();
            paddedData = padString(data);
            newNode = new listNode(paddedData, null);
            hashIndex = paddedData.charAt(currentIndex);
            deBugFile.write("In RSort, paddedData is " + paddedData + "; currentIndex = " + currentIndex + "; hashIndex = " + hashIndex + "; currentTable = " + currentTable + "\n");
            insertQ(hashTable[currentTable][hashIndex], newNode);
        }
        deBugFile.write("Finish insert all paddedData into the hashTable[0], the hashTable shown below");
        printTable(hashTable[0], deBugFile);
        printTable(hashTable[0], outFile1);
        deBugFile.write("Leaving populateFirstTable().\n");
    }

    public static String padString(String data) {
        StringBuilder str = new StringBuilder(data);
        while(str.length() < longestStringLength){
            str.append(" ");
        }
        return str.toString();
    }

    public static void printTable(LLQ[] llq, FileWriter outFile) throws IOException {
        listNode tmp;
        for(int i = 0; i < tableSize; i++){
            if(!isEmpty(llq[i])){
                tmp = llq[i].head;
                outFile.write("Table[" + currentTable + "][" + i + "]: ");
                while(tmp.next != null){
                    outFile.write("(" + tmp.data + ", " + tmp.next.data + ")");
                    outFile.write(" -> ");
                    tmp = tmp.next;
                }
                outFile.write("(" + tmp.data + ", NULL) --> NULL\n");
            }
        }
        outFile.write("\n");
    }

    public static void printSortedData(int nextTable, FileWriter outFile) throws IOException {
        listNode tmp;
        StringBuilder str;
        for(int i = 0; i < tableSize; i++){
            if(!isEmpty(hashTable[nextTable][i])){
                tmp = hashTable[nextTable][i].head.next;
                while(tmp != null){
                    str = new StringBuilder("");
                    for(int j = 0; j < tmp.data.length()-1; j++){
                        if(tmp.data.charAt(j) != ' ') {
                            str.append(tmp.data.charAt(j));
                        }
                    }
                    outFile.write(str + " ");
                    tmp = tmp.next;
                }
            }
        }
    }

}