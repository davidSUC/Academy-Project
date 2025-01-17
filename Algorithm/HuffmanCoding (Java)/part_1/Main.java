import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.lang.String;

public class Main{

    static Scanner inFile;
    static FileWriter outFile1;
    static FileWriter deBugFile;
    static HuffmanCode huffmanCode;
    static LLlist LL;
    static binTree HuffmanTree;

//    static BufferedReader inFile;

    public static void main(String[] args) throws IOException {

        inFile = new Scanner(new FileReader(args[0]));
        inFile.useDelimiter("");
//        inFile = new BufferedReader(new FileReader("HuffmanCodingData.txt"));
        outFile1 = new FileWriter(args[1]);
        deBugFile = new FileWriter(args[2]);
        huffmanCode = new HuffmanCode();

        computeCharCounts(inFile, huffmanCode.charCountAry, deBugFile);
        printCountAry(huffmanCode.charCountAry, outFile1);
        LL = new LLlist();
        constructHuffmanLList(LL.listHead, huffmanCode.charCountAry, deBugFile);
        printList(LL.listHead, outFile1);
        constructHuffmanBinTree(LL.listHead, deBugFile);
        HuffmanTree = new binTree(LL.listHead.next);

        outFile1.write("\n**Below is preOrder of the Huffman Binary Tree**\n");
        preOrder(HuffmanTree.Root, outFile1);
        outFile1.write("\n**Below is inOrder of the Huffman Binary Tree**\n");
        inOrder(HuffmanTree.Root, outFile1);
        outFile1.write("\n**Below is postOrder of the Huffman Binary Tree**\n");
        postOrder(HuffmanTree.Root, outFile1);
        constructCharCode(HuffmanTree.Root, "", huffmanCode.codeTable);

        inFile.close();
        outFile1.close();
        deBugFile.close();
    }

    public static void printNode(treeNode T, FileWriter file) throws IOException {
        String chStr, leftChr, rightChr, nextChr;

        switch (T.chStr){
            case " ":
                chStr = "\'space\'";
                break;
            case "\r":
                chStr = "\\r";
                break;
            case "\n":
                chStr = "NL";
                break;
            default:
                chStr = T.chStr;
        }

        if(T.left == null) leftChr = "null";
        else {
            switch (T.left.chStr){
                case " ":
                    leftChr = "\'space\'";
                    break;
                case "\r":
                    leftChr = "\\r";
                    break;
                case "\n":
                    leftChr = "NL";
                    break;
                default:
                    leftChr = T.left.chStr;
            }
        }

        if(T.right == null) rightChr = "null";
        else {
            switch (T.right.chStr){
                case " ":
                    rightChr = "\'space\'";
                    break;
                case "\r":
                    rightChr = "\\r";
                    break;
                case "\n":
                    rightChr = "NL";
                    break;
                default:
                    rightChr = T.right.chStr;
            }
        }

        if(T.next == null) nextChr = "null";
        else {
            switch (T.next.chStr) {
                case " ":
                    nextChr = "\'space\'";
                    break;
                case "\r":
                    nextChr = "\\r";
                    break;
                case "\n":
                    nextChr = "NL";
                    break;
                default:
                    nextChr = T.next.chStr;
            }
        }

        file.write("(");
        file.write(chStr+",  " + T.frequency+",  " + T.code+",  " + leftChr+",  " + rightChr+",  " + nextChr);
        file.write(")\n");
    }

    public static void printList(treeNode listHead, FileWriter file) throws IOException {
        if(file.toString()=="outFile1"){
            file.write("Below is the ordered Huffman ordered Linked list.\n");
        }
        else {
            file.write("Printing list in constructHuffmanLList method.\n");
        }
        treeNode pNode = listHead;
        while(pNode!=null){
            printNode(pNode, file);
            pNode = pNode.next;
        }
    }

    public static treeNode findSpot(treeNode listHead, treeNode newNode, FileWriter deBugFile) throws IOException {
        deBugFile.write("Entering findSpot method!\n");
        treeNode spot = listHead;
        while (spot.next != null) {
            deBugFile.write("In findSpot: Spot.next's frequency is " + spot.next.frequency +
                " and newNode's frequency is " + newNode.frequency + "\n");
            if (spot.next.frequency < newNode.frequency) {
                spot = spot.next;
            }
            else{
                break;
            }
        }
        deBugFile.write("Leaving findSpot method!\n");
        return spot;
    }

    public static void insertOneNode(treeNode spot, treeNode newNode){
        newNode.next = spot.next;
        spot.next = newNode;
    }

    public static void preOrder(treeNode rootNode, FileWriter file) throws IOException {
        if(rootNode.left==null && rootNode.right==null){
            printNode(rootNode, file);
        }
        else {
            printNode(rootNode,file);
            preOrder(rootNode.left, file);
            preOrder(rootNode.right, file);
        }
    }

    public static void inOrder(treeNode rootNode, FileWriter file) throws IOException{

        if(rootNode.left==null && rootNode.right==null){
            printNode(rootNode, file);
        }
        else {
            inOrder(rootNode.left, file);
            printNode(rootNode,file);
            inOrder(rootNode.right,file);
        }
    }

    public static void postOrder(treeNode rootNode, FileWriter file) throws IOException {

        if(rootNode.left==null && rootNode.right==null){
            printNode(rootNode, file);
        }
        else {
            postOrder(rootNode.left, file);
            postOrder(rootNode.right, file);
            printNode(rootNode,file);
        }
    }

    public static boolean isLeaf(treeNode node){
        if(node.left==null && node.right==null){
            return true;
        }
        return false;
    }

    public static void computeCharCounts(Scanner inFile, int[] charCountAry, FileWriter deBugFile) throws IOException {
        deBugFile.write("Entering computeCharCounts method!\n");
        char c;
        while(inFile.hasNext()) {
            c = (char)inFile.next().charAt(0);
            charCountAry[(int)c]++;
        }
        deBugFile.write("Leaving computeCharCounts method!\n");
    }

    public static void printCountAry(int[] charCountAry, FileWriter outFile1) throws IOException {
        outFile1.write("**Below is character counts**\n");
        outFile1.write("Index\tChar\tCount\n");
        outFile1.write("=====================\n");
        for(int i=0; i<charCountAry.length; i++){
            if(charCountAry[i]!=0) {
                if(i==10){
                    outFile1.write(i + "\tNL\t" + charCountAry[i] + "\n");
                }
                else if(i==13){
                    outFile1.write(i + "\t\\r\t" + charCountAry[i] + "\n");
                }
                else if(i==32){
                    outFile1.write(i + "\t'space'\t" + charCountAry[i] + "\n");
                }
                else{
                    outFile1.write(i + "\t" + (char)i + "\t" + charCountAry[i] + "\n");
                }
            }
        }
    }

    public static void constructHuffmanLList(treeNode listHead, int[] charCountAry, FileWriter deBugFile) throws IOException {
        deBugFile.write("Entering constructHuffmanLList method!\n");
        char chr;
        int frequency;
        for(int i=0; i<256; i++){
            if(charCountAry[i]>0){
                chr = (char)i;
                frequency = charCountAry[i];
                treeNode newNode = new treeNode(""+chr, frequency,"",null, null, null);
                printNode(newNode, deBugFile);
                treeNode spot = findSpot(listHead, newNode, deBugFile);
                insertOneNode(spot, newNode);
                printList(listHead, deBugFile);
            }
        }
        deBugFile.write("Leaving constructHuffmanLList method!\n");
    }

    public static void constructHuffmanBinTree(treeNode listHead, FileWriter deBugFile) throws IOException{
        deBugFile.write("Entering constructHuffmanBinTree method!\n");
        while(listHead.next.next!=null) {
            treeNode leftNode = listHead.next;
            treeNode rightNode = listHead.next.next;
            String cStr = leftNode.chStr + rightNode.chStr;
            StringBuilder s = new StringBuilder();
            for(char x: cStr.toCharArray()){
                if(x == ' ') s.append("\'space\'");
                else if(x == '\r') s.append("\\r");
                else if(x == '\n') s.append("NL");
                else s.append(x);
            }
            int frequency = leftNode.frequency + rightNode.frequency;
            treeNode newNode = new treeNode(s.toString(), frequency, "", leftNode, rightNode, null);
            printNode(newNode, deBugFile);
            treeNode spot = findSpot(listHead, newNode, deBugFile);
            insertOneNode(spot, newNode);
            listHead.next = listHead.next.next.next;
            printList(listHead, deBugFile);
        }
        deBugFile.write("Leaving constructHuffmanBinTree method!\n");
    }

    public static void constructCharCode(treeNode T, String code, String[] codeTable){
        if(isLeaf(T)){
            T.code = code;
            codeTable[(int)T.chStr.charAt(0)] = code;
        }
        else{
            constructCharCode(T.left, code+"0", codeTable);
            constructCharCode(T.right, code+"1", codeTable);
        }
    }

}

class treeNode{
    String chStr;
    int frequency;
    String code;
    treeNode left;
    treeNode right;
    treeNode next;

    treeNode(String chStr, int frequency, String code, treeNode left,
             treeNode right, treeNode next){
        this.chStr = chStr;
        this.frequency = frequency;
        this.code = code;
        this.left = left;
        this.right = right;
        this.next = next;
    }
}

class LLlist{
    treeNode listHead;
    LLlist(){
        listHead = new treeNode("dummy", 0, "", null, null, null);
    }
}

class binTree{
    treeNode Root;
    binTree(treeNode Root){
        this.Root = Root;
    }
}

class HuffmanCode{
    int[] charCountAry;
    String[] codeTable;

    HuffmanCode(){
        charCountAry = new int[256];
        codeTable = new String[256];
    }
}