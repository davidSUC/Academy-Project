#include <iostream>
#include <fstream>

using namespace std;

ifstream inFile;
ofstream SSSfile, deBugFile;

int numNodes, sourceNode, minNode, currentNode, newCost;
int** costMatrix;
int* Parent;
int* ToDo;
int* Best;

void loadCostMatrix(ifstream& in_file);

void initBest(int srcNode);
void initParent(int srcNode);
void initToDo(int srcNode);

int findMinNode();

void printToDo();
void printParent();
void printBest();
void printShortestPath(int curNode, int srcNode, ofstream& SSS_file);

bool checkToDo();


int main(int argc, const char* argv[])
{
    inFile.open(argv[1]);
    SSSfile.open(argv[2]);
    deBugFile.open(argv[3]);

    inFile >> numNodes;
    costMatrix = new int* [numNodes + 1];
    for (int i = 0; i < numNodes + 1; i++) {
        costMatrix[i] = new int[numNodes];
    }
    Parent = new int[numNodes + 1];
    ToDo = new int[numNodes + 1];
    Best = new int[numNodes + 1];
    loadCostMatrix(inFile);
    sourceNode = 1;

    SSSfile << "======================================================\n";
    SSSfile << "There are " << numNodes << " nodes in the input graph. Below are all pairs of shortest paths:\n";

    while (sourceNode <= numNodes) {
        deBugFile << "Source Node is " << sourceNode << endl;
        SSSfile << "======================================================\nThe Source node = " << sourceNode << "\n\n";

        initBest(sourceNode);
        initParent(sourceNode);
        initToDo(sourceNode);

        int childNode;
        while (!checkToDo()) {
            minNode = findMinNode();
            ToDo[minNode] = 0;
            printToDo();
            printParent();
            printBest();

            childNode = 1;
            while (childNode <= numNodes) {
                deBugFile << "childNode is " << childNode << endl;
                if (ToDo[childNode] == 1) {
                    newCost = Best[minNode] + costMatrix[minNode][childNode];
                    if (newCost < Best[childNode]) {
                        deBugFile << "newCost:" << newCost << " < Best[" << childNode << "]:" << Best[childNode] << endl;
                        Best[childNode] = newCost;
                        Parent[childNode] = minNode;
                        deBugFile << "Update Parent Array and Best Array:\n";
                        printParent();
                        printBest();
                    }
                }
                childNode++;
            }
        }
        currentNode = 1;
        while (currentNode <= numNodes) {
            printShortestPath(currentNode, sourceNode, SSSfile);
            currentNode++;
        }
        sourceNode++;
    }


    inFile.close();
    SSSfile.close();
    deBugFile.close();
}

void loadCostMatrix(ifstream& in_file) {
    for (int i = 0; i < numNodes + 1; i++) {
        for (int j = 0; j < numNodes + 1; j++) {
            if (i == j) {
                costMatrix[i][j] = 0;
            }
            else {
                costMatrix[i][j] = 9999;
            }
        }
    }
    int i, j;
    while (!in_file.eof()) {
        in_file >> i >> j;
        in_file >> costMatrix[i][j];
    }
}

void initBest(int srcNode) {
    for (int i = 0; i <= numNodes; i++) {
        Best[i] = costMatrix[srcNode][i];
    }
}

void initParent(int srcNode) {
    for (int i = 0; i <= numNodes; i++) {
        Parent[i] = srcNode;
    }
}

void initToDo(int srcNode) {
    for (int i = 0; i <= numNodes; i++) {
        ToDo[i] = 1;
    }
    ToDo[srcNode] = 0;
}

int findMinNode() {
    deBugFile << "Entering findMinNode() method!\n";
    int minCost = 9999;
    minNode = 0;
    for (int i = 1; i <= numNodes; i++) {
        if (ToDo[i] == 1 && Best[i] < minCost) {
            minCost = Best[i];
            minNode = i;
        }
    }
    deBugFile << "Leaving findMinNode(): minNode is " << minNode << endl;
    return minNode;
}

void printToDo() {
    deBugFile << "Below is ToDo Array:\n";
    for (int i = 1; i <= numNodes; i++) {
        deBugFile << i << "\t";
    }
    deBugFile << endl;
    for (int i = 1; i <= numNodes; i++) {
        deBugFile << ToDo[i] << "\t";
    }
    deBugFile << endl;
}

void printParent() {
    deBugFile << "Below is Parent Array:\n";
    for (int i = 1; i <= numNodes; i++) {
        deBugFile << i << "\t";
    }
    deBugFile << endl;
    for (int i = 1; i <= numNodes; i++) {
        deBugFile << Parent[i] << "\t";
    }
    deBugFile << endl;
}

void printBest() {
    deBugFile << "Below is Best Array:\n";
    for (int i = 1; i <= numNodes; i++) {
        deBugFile << i << "\t";
    }
    deBugFile << endl;
    for (int i = 1; i <= numNodes; i++) {
        deBugFile << Best[i] << "\t";
    }
    deBugFile << endl;
}

bool checkToDo() {
    for (int i = 1; i <= numNodes; i++) {
        if (ToDo[i] == 1) {
            return false;
        }
    }
    return true;
}

void printShortestPath(int curNode, int srcNode, ofstream& SSS_file) {
    int totalCost = Best[curNode];
    SSS_file << "The path from " << srcNode << " to " << curNode << " : " << curNode << " <-- ";
    while (Parent[curNode] != srcNode) {
        curNode = Parent[curNode];
        SSS_file << curNode << " <-- ";
    }
    SSS_file << srcNode << " : cost = " << totalCost << endl;
}