#include <iostream>
#include <fstream>

using namespace std;

class QtNode {
public:
    int color;
    int upperR;
    int upperC;
    int size;
    QtNode* NW = NULL;
    QtNode* NE = NULL;
    QtNode* SW = NULL;
    QtNode* SE = NULL;
    QtNode(int color, int upperR, int upperC, int size, QtNode* NW, QtNode* NE, QtNode* SW, QtNode* SE) {
        this->color = color;
        this->upperR = upperR;
        this->upperC = upperC;
        this->size = size;
        this->NW = NW;
        this->NE = NE;
        this->SW = SW;
        this->SE = SE;
    }
};
void printQtNode(QtNode* qtNode, ofstream& deBugFile);

class QuadTree {
public:
    QtNode* QtRoot;
    int numRows, numCols, minVal, maxVal;
    int** imgAry;
    int** newImgAry;
    QuadTree(){}
};

int computerPower2(int numRows, int numCols, ofstream& deBugFile);
void zero2DAry(int** ary, int aryLength);
void loadImage(ifstream& inFile, int** imgAry, int imgR, int imgC);
QtNode* buildQuadTree(int** imgAry, int upR, int upC, int size, ofstream& deBugFile);
int addKidsColor(QtNode node);
bool isLeaf(QtNode* node);
void preOrder(QtNode* QtRoot, ofstream& outFile1);
void postOrder(QtNode* QtRoot, ofstream& outFile1);
void getLeaf(QtNode* QtRoot, int** newImgAry, ofstream& deBugFile);
void fillnewImgAry(QtNode* QtRoot, int** newImgAry, ofstream& deBugFile);

ifstream inFile;
ofstream outFile1, outFile2, deBugFile;
QuadTree myQT = QuadTree();

int main(int argc, const char* argv[])
{
    inFile.open(argv[1]);
    outFile1.open(argv[2]);
    outFile2.open(argv[3]);
    deBugFile.open(argv[4]);

    inFile >> myQT.numRows >> myQT.numCols >> myQT.minVal >> myQT.maxVal;
    int power2Size = computerPower2(myQT.numRows, myQT.numCols, deBugFile);
    outFile2 << "power2Size = " << power2Size << endl;

    myQT.imgAry = new int*[power2Size];
    for (int i = 0; i < power2Size; i++) {
        myQT.imgAry[i] = new int[power2Size];
    }
    zero2DAry(myQT.imgAry, power2Size);

    myQT.newImgAry = new int* [power2Size];
    for (int i = 0; i < power2Size; i++) {
        myQT.newImgAry[i] = new int[power2Size];
    }
    zero2DAry(myQT.newImgAry, power2Size);

    loadImage(inFile, myQT.imgAry, myQT.numRows, myQT.numCols);
    
    myQT.QtRoot = buildQuadTree(myQT.imgAry, 0, 0, power2Size, deBugFile);
    outFile1 << "**Below is preOrder**\n";
    preOrder(myQT.QtRoot, outFile1);
    outFile1 << "\n**Below is postOrder**\n";
    postOrder(myQT.QtRoot, outFile1);
    deBugFile << "Calling getLeaf() method to recreate the image from quadtree!\n";
    getLeaf(myQT.QtRoot, myQT.newImgAry, deBugFile);
    
    outFile2 << "***Below is imgAry***\n";
    for (int i = 0; i < power2Size; i++)
    {
        for (int j = 0; j < power2Size; j++) {
            outFile2 << myQT.imgAry[i][j] << " ";
        }
        outFile2 << endl;
    }

    outFile2 << "***Here is the recreated image***\n";
    outFile2 << myQT.numRows << " " << myQT.numCols << " " << myQT.minVal << " " << myQT.maxVal << endl;
    for (int i = 0; i < myQT.numRows; i++)
    {
        for (int j = 0; j < myQT.numCols; j++) {
            outFile2 << myQT.newImgAry[i][j] << " ";
        }
        outFile2 << endl;
    }

    inFile.close();
    outFile1.close();
    outFile2.close();
    deBugFile.close();
}

void printQtNode(QtNode* node, ofstream& outFile) {
    deBugFile << "Entering printQtNode method!\n";
    if (node->color == 5) {
        int nwColor = -1, neColor = -1, swColor = -1, seColor = -1;
        if (node->NW != NULL) { nwColor = node->NW->color; }
        if (node->NE != NULL) { neColor = node->NE->color; }
        if (node->SW != NULL) { swColor = node->SW->color; }
        if (node->SE != NULL) { seColor = node->SE->color; }
        outFile << node->color << ", " << node->upperR << ", " << node->upperC << ", " << node->size << ", "
            << nwColor << ", " << neColor << ", " << swColor << ", " << seColor << endl;
    }
    else {
        outFile << node->color << ", " << node->upperR << ", " << node->upperC << ", " << node->size << ", -1, -1, -1, -1\n";
    }
    deBugFile << "Leaving printQtNode method!\n";
}

int computerPower2(int numRows, int numCols, ofstream& deBugFile) {
    deBugFile << "Entering computerPower2 method!\n";
    int size = max(numRows, numCols);
    int power2 = 2;
    while(size > power2){
        power2 *= 2;
    }
    deBugFile << "Leaving computerPower2 method, power2 is " << power2 << endl;
    return power2;
}

void zero2DAry(int** ary, int aryLength)
{
    for (int i = 0; i < aryLength; i++)
    {
        for (int j = 0; j < aryLength; j++)
        {
            ary[i][j] = 0;
        }
    }
}

void loadImage(ifstream& inFile, int** imgAry, int imgR, int imgC) {
    for (int i = 0; i < imgR; i++) {
        for (int j = 0; j < imgC; j++)
        {
            inFile >> imgAry[i][j];
        }
    }
}

QtNode* buildQuadTree(int** imgAry, int upR, int upC, int size, ofstream& deBugFile) {
    deBugFile << "Entering buildQuadTree: upR=" << upR << " upC=" << upC << " and size=" << size << endl;
    QtNode* newQtNode = new QtNode(-1, upR, upC, size, NULL, NULL, NULL, NULL);
    int halfSize;
    int sumColor;
    if (size == 1) {
        newQtNode->color = imgAry[upR][upC];
    }
    else {
        halfSize = size / 2;
        newQtNode->NW = buildQuadTree(imgAry, upR, upC, halfSize, deBugFile);
        newQtNode->NE = buildQuadTree(imgAry, upR, (upC + halfSize), halfSize, deBugFile);
        newQtNode->SW = buildQuadTree(imgAry, (upR + halfSize), upC, halfSize, deBugFile);
        newQtNode->SE = buildQuadTree(imgAry, (upR + halfSize), (upC + halfSize), halfSize, deBugFile);
    
        sumColor = addKidsColor(*newQtNode);
        deBugFile << "The sumColor is " << sumColor << endl;
        if (sumColor == 0) {
            newQtNode->color = 0;
            delete newQtNode->NW;
            delete newQtNode->NE;
            delete newQtNode->SW;
            delete newQtNode->SE;
            newQtNode->NW = NULL;
            newQtNode->NE = NULL;
            newQtNode->SW = NULL;
            newQtNode->SE = NULL;
        }
        else if (sumColor == 4) {
            newQtNode->color = 1;
            delete newQtNode->NW;
            delete newQtNode->NE;
            delete newQtNode->SW;
            delete newQtNode->SE;
            newQtNode->NW = NULL;
            newQtNode->NE = NULL;
            newQtNode->SW = NULL;
            newQtNode->SE = NULL;
        }
        else {
            newQtNode->color = 5;
        }
    }
    printQtNode(newQtNode, deBugFile);
    deBugFile << "Leaving buildQuadTree!\n";
    return newQtNode;
}

int addKidsColor(QtNode node) {
    int a = 0, b = 0, c = 0, d = 0;
    if (node.NW != NULL) { a = node.NW->color; }
    if (node.NE != NULL) { b = node.NE->color; }
    if (node.SW != NULL) { c = node.SW->color; }
    if (node.SE != NULL) { d = node.SE->color; }
    return a+b+c+d;
}

bool isLeaf(QtNode* node) {
    if (node->color == 5) {
        return false;
    }
    return true;
}

void preOrder(QtNode* QtRoot, ofstream& outFile1) {
    if (isLeaf(QtRoot)) {
        printQtNode(QtRoot, outFile1);
    }
    else {
        printQtNode(QtRoot, outFile1);
        preOrder(QtRoot->NW, outFile1);
        preOrder(QtRoot->NE, outFile1);
        preOrder(QtRoot->SW, outFile1);
        preOrder(QtRoot->SE, outFile1);
    }
}

void postOrder(QtNode* QtRoot, ofstream& outFile1) {
    if (isLeaf(QtRoot)) {
        printQtNode(QtRoot, outFile1);
    }
    else {
        preOrder(QtRoot->NW, outFile1);
        preOrder(QtRoot->NE, outFile1);
        preOrder(QtRoot->SW, outFile1);
        preOrder(QtRoot->SE, outFile1);
        printQtNode(QtRoot, outFile1);
    }
}

void getLeaf(QtNode* QtRoot, int** newImgAry, ofstream& deBugFile) {
    deBugFile << "Entering getLeaf method!\n";
        if (isLeaf(QtRoot)) {
            fillnewImgAry(QtRoot, newImgAry, deBugFile);
        }
        else {
            getLeaf(QtRoot->NW, newImgAry, deBugFile);
            getLeaf(QtRoot->NE, newImgAry, deBugFile);
            getLeaf(QtRoot->SW, newImgAry, deBugFile);
            getLeaf(QtRoot->SE, newImgAry, deBugFile);
        }
        deBugFile << "Leaving getLeaf method.\n";
}

void fillnewImgAry(QtNode* QtRoot, int** newImgAry, ofstream& deBugFile) {
    deBugFile << "Entering fillnewImgAry method!\n";
    int color, R, C, sz;
    color = QtRoot->color;
    R = QtRoot->upperR;
    C = QtRoot->upperC;
    sz = QtRoot->size;
    for (int i = R; i < R + sz; i++) {
        for (int j = C; j < C + sz; j++) {
            newImgAry[i][j] = color;
        }
    }
    deBugFile << "Leaving fillnewImgAry method.\n";
}