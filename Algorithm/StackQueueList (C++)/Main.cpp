#include <iostream>
#include <fstream>

using namespace std;

class listNode
{
public:
    int data;
    listNode* next;

    listNode(int data)
    {
        this->data = data;
        this->next = NULL;
    }

};
void printNode(listNode node);
void printNode(listNode node, ofstream& outFile);

class LLStack
{
public:
	listNode* top;

    LLStack()
    {
        top = new listNode(-9999);
    }

    void push(listNode newNode)
    {
        listNode* tmp = new listNode(newNode.data);
        tmp->next = top->next;
        top->next = tmp;
    }

    bool isEmpty()
    {
	    if(top->next==NULL)
	    {
            return true;
	    }
        return false;
    }

    listNode* pop()
    {
	    if(!this->isEmpty())
	    {
            listNode* tmp = top->next;
            top->next = tmp->next;
            tmp->next = NULL;
            return tmp;
	    }
        cout << "stack is empty";
        return nullptr;
    }
};
void buildStack(LLStack myStack, ifstream &inFile, ofstream &deBugFile);

class LLQueue
{
public:
    listNode* head;
    listNode* tail;

    LLQueue()
    {
        head = new listNode(-9999);
        tail = head;
    }

    void insertQ(listNode newNode)
    {
        listNode* tmp = new listNode(newNode.data);
        tail->next = tmp;
        tail = tmp;
    }

    bool isEmpty()
    {
        if (tail == head) {
            return true;
        }
            return false;
    }

};
void buildQueue(LLStack S, LLQueue& Q, ofstream& outFile1, ofstream& deBugFile);
listNode* deleteQ(LLQueue Q, ofstream& outFile2);

class LLlist
{
public:
    listNode* listHead;

    LLlist()
    {
        listHead = new listNode(-9999);
    }
};
listNode* findSpot(LLlist LL, listNode* newNode);
void insertOneNode(listNode* spot, listNode* newNode);
void buildList(LLQueue& Q, LLlist LL, ofstream& outFile2, ofstream& deBugFile);
void printList(listNode* listHead, ofstream& outFile3);

ifstream inFile;
ofstream outFile1;
ofstream outFile2;
ofstream outFile3;
ofstream deBugFile;

LLStack S;
LLQueue Q;
LLlist LL;

int main(int argc, const char* argv[])
{
	inFile.open(argv[1]);
    outFile1.open(argv[2]);
    outFile2.open(argv[3]);
    outFile3.open(argv[4]);
    deBugFile.open(argv[5]);

    S = LLStack();
	buildStack(S, inFile, deBugFile);

    Q = LLQueue();
    buildQueue(S, Q, outFile1, deBugFile);

    LL = LLlist();
    buildList(Q, LL, outFile2, deBugFile);
    printList(LL.listHead, outFile3);

    inFile.close();
    outFile1.close();
    outFile2.close();
    outFile3.close();
    deBugFile.close();
    return 0;
}

void printNode(listNode node)
{
    cout << "(" << node.data << ", " << node.next->data << ") -> ";
}

void printNode(listNode node, ofstream& outFile)
{
	if(node.next==NULL){
		outFile << "(" << node.data << ", NULL) -> ";
	}
	else{
		outFile << "(" << node.data << ", " << node.next->data << ") -> ";
	}
}

void printList(listNode* listHead, ofstream& outFile3)
{
    outFile3 << "listHead -> ";
    listNode* tmp = listHead;
    while (tmp != NULL)
    {
        printNode(*tmp, outFile3);
        tmp = tmp->next;
    }
    outFile3 << "NULL";
}

listNode* deleteQ(LLQueue Q, ofstream& outFile2)
{
    deBugFile << "entering deleteQ method!" << endl;
	listNode* tmp = Q.head->next;
    Q.head->next = tmp->next;
    tmp->next = NULL;
    deBugFile << tmp->data << endl;
    deBugFile << "leaving deleteQ method!" << endl;
    return tmp;
}

void buildStack(LLStack S, ifstream &inFile, ofstream &deBugFile)
{

	int data;
    deBugFile << "entering buildStack!" << endl;
    while (inFile >> data) {
        deBugFile << data << endl;
        listNode newNode = listNode(data);
        S.push(newNode);
    }
    deBugFile << "leaving buildStack!" << endl;
}

void buildQueue(LLStack S, LLQueue& Q, ofstream& outFile1, ofstream& deBugFile)
{
    deBugFile << "entering buildQueue!" << endl;
    while (!S.isEmpty()) {
        listNode newNode = listNode(S.pop()->data);
        deBugFile << newNode.data << endl;
        outFile1 << newNode.data << endl;
        Q.insertQ(newNode);
    }
    deBugFile << "leaving buildQueue!" << endl;
}

void buildList(LLQueue& Q, LLlist LL, ofstream& outFile2, ofstream& deBugFile)
{
    deBugFile << "entering buildList!" << endl;
    while (Q.head->next != NULL) {
        listNode* newNode = deleteQ(Q, outFile2);
        outFile2 << newNode->data << endl;
        deBugFile << newNode->data << endl;
        listNode* spot = findSpot(LL, newNode);
        insertOneNode(spot, newNode);
    }
    deBugFile << "leaving buildList!" << endl;
}

listNode* findSpot(LLlist LL, listNode* newNode)
{
    listNode* spot = LL.listHead;
    while (spot->next != NULL && spot->next->data < newNode->data)
    {
        spot = spot->next;
    }
    return spot;
}

void insertOneNode(listNode* spot, listNode* newNode)
{
    newNode->next = spot->next;
    spot->next = newNode;
}
