// David ChenSalas 23960073

#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <fcntl.h>
#include <sys/types.h>     
#include <sys/wait.h>
#include <mqueue.h>
#include <time.h>
#include <sys/utsname.h>

#define MQ_NAME "/chensalas_Q" //name of message queue

const int MSG_SIZE = 128;
const int MSG_CAPACITY = 50;

int optc = 0;

int getBufferSize(char *num);
void setTo0(int *a, int *b, int *c, int *d);
int lenOf(char *str);
int numofDigit(int num);
void getProcessInfo();
void intToChar(char target[], int size, int num);

int main(int argc, char *argv[]){

    struct mq_attr attr;
    attr.mq_maxmsg = MSG_CAPACITY;
    attr.mq_msgsize = MSG_SIZE;

    int n_Opt=0, w_Opt=0, c_Opt=0, I_Opt=0;
    // nF=line count, wF=word count, cF=character count, IF=maxium #character in one line.
    int nF=0, wF=0, cF=0, IF=0, lineCharCount=0;
    int bufferSize;
    char c;

    char *inputFile;

    int fd[2]; //Pipe read and write end
    pid_t pid;
    int f1; //File descriptor

//Check OPTION command line.
//Program can take total 4 OPTION comman line only.
//Thus the loop to check OPTION will loop only 4 times maximum.
//Any OPTIONS are specified on the command line will be assgined its vaule to 1, which mean trigger.
//Variable optc stand for OPTION COUNT, how many OPTIONS are specified on command line will be count in.
//At last, start in line 63, if none of OPTIONS are trigger during the checking loop, then all OPTIONS will be trigger as default.
//Impossible to have all OPTIONS untrigger.(which their vaulue all == 0)
    if(argv[1][0]=='-'){
        for(int i=1; i<lenOf(argv[1]); i++){
            switch (argv[1][i])
            {
            case 'n':
                n_Opt=1;
                break;
            case 'w':
                w_Opt=1;
                break;
            case 'c':
                c_Opt=1;
                break;
            case 'I':
                I_Opt=1;
                break;
            }
        }
        optc=1;
    }
    if(n_Opt==0 && w_Opt==0 && c_Opt==0 && I_Opt==0){
        n_Opt=1;
        w_Opt=1;
        c_Opt=1;
        I_Opt=1;
    }

//Buffer size argument is required to start program.
bufferSize = getBufferSize(argv[optc+1]);//Check and validation done in setBufferSize() function.
char readBuff[bufferSize], writeBuff[bufferSize];

int nRead_pipe, nRead_file; //nRead stand for "number of bit read from".

//Determine #loop to open file.
//No input file on command line, prj1inp.txt by default.
//In the case no input file, argc-optc==2 always true and using this condition to determine if any input file on command line. 
//Increment variable "end" in the loop to ensure the loop will process prj1inp.txt as default.
//Every loop will fork() once, and child process in each loop always return at the end of loop. At most one child exist only during program processing.
int end;
if(optc+2==argc){
    end = argc+1;
}
else{
    end = argc;
}
for(int i=optc+2; i<end; i++){

    //Initialize Pipe
    if( pipe(fd) < 0){
        write(STDERR_FILENO, "Pipe failed.", 12);
        return -1;
    }

    pid = fork();
    if (pid < 0) {
        write(STDERR_FILENO, "Fork Failed.", 12);
        return -1;
    }
    else if(pid == 0){
        close(fd[1]);//Close children write end of pipe
    }
    else{
        close(fd[0]);//Close parent read end of pipe
        if(i==optc+2){
            struct utsname uts;
            uname(&uts);
            write(STDOUT_FILENO, "OS name is: ", 12);
            write(STDOUT_FILENO, uts.sysname, sizeof(uts.sysname));
            write(STDOUT_FILENO, "\nOS Release is ", 15);
            write(STDOUT_FILENO, uts.release, sizeof(uts.release));
            write(STDOUT_FILENO, "\nOS Version is ", 15);
            write(STDOUT_FILENO, uts.version, sizeof(uts.version));
            write(STDOUT_FILENO, "\n\n", 2);
        }//print information about the OS.
        else{
            write(STDOUT_FILENO, "\n", 1); //line for organizing output structure only.
        }
    }

    //Initialize the message queue.
    mqd_t mqd = mq_open(MQ_NAME,  O_CREAT | O_RDWR, 0644, &attr);
    if (mqd == (mqd_t) -1){
        write(STDERR_FILENO, "Parent:  mq_open error.", 23);
        mq_unlink(MQ_NAME);
        return -1;
    }
        //This condition indicate no input file on comman line, so "prj1inp.txt" set as default to read.
        if(argc-optc==2){
            inputFile = "prj1inp.txt";
        }
        else{
            inputFile = argv[i];
        }
        f1 = open(inputFile, O_RDONLY, 0);
        if(f1 == -1){
            write(STDERR_FILENO, "File \"", 6);
            write(STDERR_FILENO, inputFile, lenOf(inputFile));
            write(STDERR_FILENO, "\" can't open.\n", 14);
            continue; //Skip the file can't not open and continue to read the rest file. (skip and go to next loop instead terminate program)
        }

    //Using a while loop to read input file chunk by chunk by the buffersize provide on the command line.
    //Parent write each chunk into pipe for child read.
    //Child process will loop over each chunk and check every character of it.
    //cF character count will be the sum of success read during the while loop (nRead_pipe) which is return by the read() call.
    //Word and word always split by "space" or "\t", every ecounter of these two character imply one new word.
    //Character "\n" also imply one new word and also a new line. Every ecounter of "\n", compare #character in current line with the previous longest line to determine the best suit for IF.
    while((nRead_file = read(f1, writeBuff, bufferSize))>0){
        if(pid>0){
                write(fd[1], writeBuff, nRead_file);
        }
        else if((nRead_pipe = read(fd[0], readBuff, bufferSize))>0){
            cF+=nRead_pipe;
            for(int i=0; i<nRead_pipe; i++){
                c = readBuff[i];
                lineCharCount++;
                if(c==' ' || c=='\t'){
                    wF++;
                }
                if(c=='\n'){
                    wF++;
                    nF++;
                    if(IF<lineCharCount){
                        IF=lineCharCount;
                        lineCharCount = 0;
                    }
                }
            }
        }
    }
    
    //Since the end character of file might not are "space", "\t" or "\n", the last line and word will be miss count.
    //So line count and word count need to increment 1 if there is more than 1 character in file.
    if(pid==0){
        if(cF>0){
            nF++;
            wF++;
        }
        if(IF<lineCharCount){
            IF=lineCharCount;
        }

        //Child send only the message is related to the trigger OPTIONS.
        //Messages are placed on the queue in decreasing order of priority, with newer messages of the same priority being placed after older messages with the same priority.(First in First out)
        char str[MSG_SIZE];
        struct timespec timeout = {0, 0};
        write(STDOUT_FILENO, "***Child: start send msg***\n", 29);
        getProcessInfo();
        if(n_Opt==1){
            char nFC[numofDigit(nF)];
            intToChar(nFC, sizeof(nFC), nF);
            mq_timedsend(mqd, inputFile, lenOf(inputFile), 1, &timeout);
            mq_timedsend(mqd, ": newline count is: ", 20, 1, &timeout);
            mq_timedsend(mqd, nFC, sizeof(nFC), 1, &timeout);
            mq_timedsend(mqd, "\n", 1, 1, &timeout);
        }
        if(w_Opt==1){
            char wFC[numofDigit(wF)];
            intToChar(wFC, sizeof(wFC), wF);
            mq_timedsend(mqd, inputFile, lenOf(inputFile), 1, &timeout);
            mq_timedsend(mqd, ": word count is: ", 17, 1, &timeout);
            mq_timedsend(mqd, wFC, sizeof(wFC), 1, &timeout);
            mq_timedsend(mqd, "\n", 1, 1, &timeout);
        }
        if(c_Opt==1){
            char cFC[numofDigit(cF)];
            intToChar(cFC, sizeof(cFC), cF);
            mq_timedsend(mqd, inputFile, lenOf(inputFile), 1, &timeout);
            mq_timedsend(mqd, ": character count is: ", 22, 1, &timeout);
            mq_timedsend(mqd, cFC, sizeof(cFC), 1, &timeout);
            mq_timedsend(mqd, "\n", 1, 1, &timeout);
        }
        if(I_Opt==1){
            char IFC[numofDigit(IF)];
            intToChar(IFC, sizeof(IFC), IF);
            mq_timedsend(mqd, inputFile, lenOf(inputFile), 1, &timeout);
            mq_timedsend(mqd, ": maximum line length is: ", 26, 1, &timeout);
            mq_timedsend(mqd, IFC, sizeof(IFC), 1, &timeout);
            mq_timedsend(mqd, "\n", 1, 1, &timeout);
        }
        write(STDOUT_FILENO, "***Child: msg sent.***\n", 24);

        //reset all counting value.
        setTo0(&nF, &wF, &cF, &IF);

        //Release the message queue.
            if (mq_close(mqd) == -1) { 
                mq_unlink(MQ_NAME);
                return -1;
            }
            if (mq_unlink(MQ_NAME) == -1){ 
                return -1;
            }
            
            //Child job not done untill end of loop.
            if(end==argc+1 || i==argc-1){
                write(STDOUT_FILENO,  "\nChild: Terminating.\n\n", 22);
            }
            return 0; //alert parent start process msg in msg queue.
    }
    else{
        wait(NULL); //Parent wait child write msg into msg queue.

        write(STDOUT_FILENO, "***Parent: start receive msg.***\n", 34);
        getProcessInfo();

        char buf[MSG_SIZE];
	    unsigned int prio;
	    struct timespec timeout = {0, 0};

        //Loop over to read the message queue untill an error read which return -1 to numRead.
        //Message print out will follow the order of "First in First out".
        ssize_t numRead;
        while(numRead = mq_timedreceive(mqd, buf, attr.mq_msgsize, &prio, &timeout)){
            if (numRead == -1){
                if (mq_close(mqd) == -1) { 
                    write(STDERR_FILENO, "Parent: close error.", 20);
                    mq_unlink(MQ_NAME);
                    return -1;
                }
                break; 
            }
            write(STDOUT_FILENO, buf, (size_t) numRead);
        }
        write(STDOUT_FILENO, "***Parent: msg recieved.***\n", 29);
        if(end==argc+1 || i==argc-1){
            write(STDOUT_FILENO,  "\nParent: Terminating.", 22);
        }
    }// End of parent branch
}

return 0;

} //main() end



//Function print out the process' pid, ppid, wcd and host_name.
void getProcessInfo(){
    char p_id[numofDigit(getpid())], 
         pp_id[numofDigit(getppid())],
         hostName[256], 
         workDir[256];

        intToChar(p_id, sizeof(p_id), getpid());
        intToChar(pp_id, sizeof(pp_id), getppid());
        getcwd(workDir, sizeof(workDir));
        gethostname(hostName, sizeof(hostName));

        //Print Process ID.
        write(STDOUT_FILENO, "Process ID is: ", 15);
        write(STDOUT_FILENO, p_id, sizeof(p_id));
        //Print Parent process ID.
        write(STDOUT_FILENO, "\nParent process ID is ", 22);
        write(STDOUT_FILENO, pp_id, sizeof(pp_id));
        //Print Current working directory.
        write(STDOUT_FILENO, "\nProcess current working directory is: ", 40);
        write(STDOUT_FILENO, workDir, lenOf(workDir));
        //Print Hostname.
        write(STDOUT_FILENO, "\nHostname is: ", 15);
        write(STDOUT_FILENO, hostName, lenOf(hostName));
        write(STDOUT_FILENO, "\n", 1);
}


//This function create for checking the buff size, that is required to run this program.
//All validate and condition checking of the correct buff size is process in this function.
//Any false validation will result in ending the program.
//Otherwise return buff size as an integer.
int getBufferSize(char *num){
    int n=0;
    int digitN = lenOf(num);
    if(digitN<2 || digitN>3){
        write(STDERR_FILENO, "An integer number between 64 and 256 is required for buffer size as first argument.", 83);
        exit(-1);
    }
    //Check each character is number and convert them in as a single integer.
    //Any character is not a number between 0-9 found in input buffer size will lead to program terminate.
    for(int i=0; i<digitN; i++){
        if(num[i]>='0' && num[i]<='9'){
            n = n*10 + (num[i]-'0');
        }
        else{
            write(STDERR_FILENO, "An integer number between 64 and 256 is required for buffer size as first argument.", 83);
            exit(-1);
        }
    }
    //limit buffer size.
    if(n<64 || n>256){
        write(STDERR_FILENO, "Buffer size has to be between 64 and 256.", 41);
        exit(-1);
    }
    return n;
}

//Set 4 Integer variable's value to 0;
void setTo0(int *a, int *b, int *c, int *d){
    *a=0;
    *b=0;
    *c=0;
    *d=0;
}

//This function count and return the # of character of a characte array (OR string).
//Require argument: 1) A character array OR pointer char variable.
int lenOf(char *str){
    int count = 0;
    while(str[count]!='\0'){
        count++;
    }
    return count;
}

//This function count and return the # of digit of an integer number.
//Require argument: 1) An integer number.
int numofDigit(int num){
    int count = 0;
    while(num>0){
        count++;
        num=num/10;
    }
    return count;
}

//This function covert each digit of an Integer number into individual character.
//Each character coverted from the number assign into "target" character array.
//Require argument: 1) Fixed size char array.
//                  2) Size of argument 1. 
//                  3) An integer number.
void intToChar(char target[], int size, int num){
    char c;
    while(size>=0){
        c = num%10+'0';
        target[--size] = c;
        num = num/10;
    }
}