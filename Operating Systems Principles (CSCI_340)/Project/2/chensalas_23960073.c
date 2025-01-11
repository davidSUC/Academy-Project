// David ChenSalas 23960073

#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>
#include <unistd.h>
#include <math.h>
#include <string.h>
#include <fcntl.h>

    int InpArray[12000], sum[12], sqSum[12]; //sqSum = Root of the Sum of Squares
    float gAvg[12]; // gAVG = geometric average
    int pthreadNum[12]; //Pthread number indentifier.
    int of; //output file descriptor.

    int numofDigit(int num); //Count the number of digits in an integer.
    void intToChar(char target[], int num); //Covert an integer to char type.
    void pOutput(int n); //write pthread computation output.

//Grandchild pthread instruction.
    void *compute_Sum(void *ptr);
    void *compute_sqSum(void *ptr);
    void *compute_gAvg(void *ptr);

//Child pthread instruction.
    void *runner(void *ptr){
        int n = (*(int *)ptr); //get the pthread number.

        // each child pthread generate 3 more child and each compute different task.
        pthread_t GrandChild[3];
        pthread_create(&GrandChild[0], NULL, compute_Sum, &n);
        pthread_create(&GrandChild[1], NULL, compute_sqSum, &n);
        pthread_create(&GrandChild[2], NULL, compute_gAvg, &n);
        
        for(int i=0; i<3; i++){
            pthread_join(GrandChild[i], NULL);
        }
    }

int main(int argc, char *argv[]){

    FILE *fl;
    char line[5];

    char *inputFile = argv[1];
    fl = fopen(inputFile, "r");
    if(fl == NULL){
        write(STDOUT_FILENO, "File open error.", 16);
        return 1;
    }

//Creat outout file with mode 00777 = S_IRWXU | S_IRWXG_ | S_IRWXO. (User(file owner), group and others have read, write and excute permission)
    char *outFile = argv[2];
    of = creat(outFile, 00777);
    
//Read all 12000 data from input file and store each data into InpArray.
//First line of data store in InpArray[0]; second = InpArray[1]; last = InpArray[11999];
    int n = 0;
    while(fgets(line, sizeof(line), fl)){
        InpArray[n++] = atoi(line);
    }

//Creat 12 child pthread.
    pthread_t tid[12];
    for(int i=0; i<12; i++){
        pthreadNum[i] = i; //pthread identifier and use as the offset to assgine data for each task.
        if(pthread_create(&tid[i], NULL, runner, &pthreadNum[i]) != 0) {
            write(STDERR_FILENO, "Failed to create thread,", 24);
            return -1;
        }
    }

    int max_Sum=0, max_sqSum=0, max_gAvg=0;
    for(int i=0; i<12; i++){
        if(pthread_join(tid[i], NULL) != 0){
            write(STDERR_FILENO, "Failed to join thread.", 22);
            return -1;
        }
        if(max_Sum<sum[i]){
            max_Sum=sum[i];
        }
        if(max_sqSum<sqSum[i]){
            max_sqSum=sqSum[i];
        }
        if(max_gAvg<gAvg[i]){
            max_gAvg=gAvg[i];
        }
    }// Main program wait all pthread terminate and do comparision for getting max result from each pthread computation.

    // Write all 12 child pthread computation information.
    for(int i=0; i<12; i++){
        pOutput(i);
    }

    // Convert needed data to char type for write output purpose.
    char c_Sum[numofDigit(max_Sum)], c_sqSum[numofDigit(max_Sum)], c_gAvg[numofDigit(max_Sum)];
    intToChar(c_Sum, max_Sum);
    intToChar(c_sqSum, max_sqSum);
    intToChar(c_gAvg, max_gAvg);
    
    // Write all three largest result information fromn three major computataion to output file.
    write(of, "Main program thread: Max of the Sums=", 37);
    write(of, c_Sum, strlen(c_Sum));
    write(of, "\nMain program thread: Max of the root of the sum of the squares=", 64);
    write(of, c_sqSum, strlen(c_sqSum)); 
    write(of, "\nMain program thread: Max of the Geometric Averages=", 52);
    write(of, c_gAvg, strlen(c_gAvg));
    write(of, "\nMain program thread: Terminating. \n", 37);
        
    close(of); // Release creat file descriptor.

    return 0;
}// End of Main program



void pOutput(int n){
    int totalDigit = numofDigit(n)+numofDigit(sum[n])+numofDigit(sqSum[n])+numofDigit((int)gAvg[n]);
    char buf[82+totalDigit];
    sprintf(buf,"Worker Child Pthread Number=%d:\tSum=%d\tRoot of the Sum of Squares=%d\tGeometric Average=%d\n", n, sum[n], sqSum[n], (int)gAvg[n]);
    write(of, buf, strlen(buf));
}

void intToChar(char target[], int num){
    char c;
    int size = numofDigit(num);
    while(size>=0){
        c = num%10+'0';
        target[--size] = c;
        num = num/10;
    }
}

int numofDigit(int num){
    int count = 0;
    while(num>0){
        count++;
        num=num/10;
    }
    return count;
}


// pthread access data set range is between the offset*1000 and (offset+1)*1000.
void *compute_Sum(void *ptr){
    int n = (*(int *)ptr);
    for(int i = n*1000; i<(n+1)*1000; i++){
        sum[n] += InpArray[i];
    }
}
void *compute_sqSum(void *ptr){
    int n = (*(int *)ptr);
    for(int i = n*1000; i<(n+1)*1000; i++){
        sqSum[n] += (InpArray[i]*InpArray[i]);
    }
    sqSum[n] = sqrt(sqSum[n]); 
}
void *compute_gAvg(void *ptr){
    int n = (*(int *)ptr);
    gAvg[n] = 1.0; //Initialize default value to 1, since defalut value 0 is trival in multiplcation.
    for(int i = n*1000; i<(n+1)*1000; i++){
        gAvg[n] *= pow(InpArray[i], (float) 1/1000);
    }
}