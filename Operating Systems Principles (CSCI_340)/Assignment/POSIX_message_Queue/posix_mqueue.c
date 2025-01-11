#include <stdio.h>
#include <sys/types.h>     
#include <fcntl.h>
#include <unistd.h>
#include <sys/wait.h>
#include <mqueue.h>
#include <time.h>

const int MSG_SIZE = 128;
const int MSG_CAPACITY = 50;

int main(int argc, const char *argv[]) { 
 
 struct mq_attr attr;

 attr.mq_maxmsg = MSG_CAPACITY;
 attr.mq_msgsize = MSG_SIZE;

 write(STDOUT_FILENO, "Parent: Before mq_open...\n", 26);

// Initialize the message queue.
 mqd_t mqd = mq_open(argv[1],  O_CREAT | O_RDWR, 0644, &attr);
 if (mqd == (mqd_t) -1)
      {
      printf("Parent:  mq_open error");
      mq_unlink(argv[1]);
      return 2;
      }

write(STDOUT_FILENO, "Parent: After mq_open...\n", 25);

// List the message queue properties.
struct mq_attr ret_attr;
 if (mq_getattr(mqd, &ret_attr) == -1)
      {
      printf("Parent: mq_getattr error");
      return 2;
      }

 printf("Parent: Maximum # of messages on queue: %ld\n", ret_attr.mq_maxmsg);
 printf("Parent: Maximum message size: %ld\n", ret_attr.mq_msgsize);
 printf("Parent: # of messages currently on queue: %ld\n\n", ret_attr.mq_curmsgs);


// Parent is producer and child is consumer.
pid_t pid = fork();

if (pid < 0) 
	{
	fprintf(stderr, "Parent: Fork Failed");
	return 2;	
	}

else if (pid == 0) 
	{  // Child reads from message queue.

	char buf[MSG_SIZE];
	unsigned int prio;
	struct timespec timeout = {0, 0};

// Get the first message.
	ssize_t numRead = mq_timedreceive(mqd, buf, attr.mq_msgsize, &prio, &timeout);

	if (numRead == -1) 
		{
  		printf("Child: mq_read error");
  		return 5;
		}

	write(STDOUT_FILENO, "Child: Message 1 content is ***", 31); 
	write(STDOUT_FILENO, buf, (size_t) numRead);
	write(STDOUT_FILENO, "***\n", 4);


// Get the second message.
	numRead = mq_timedreceive(mqd, buf, attr.mq_msgsize, &prio, &timeout);

	if (numRead == -1) 
		{
  		printf("Child: mq_read error");
  		return 5;
		}

	write(STDOUT_FILENO, "Child: Message 2 content is ***", 31); 
	write(STDOUT_FILENO, buf, (size_t) numRead);
	write(STDOUT_FILENO, "***\n", 4);

// Get the third message.
	numRead = mq_timedreceive(mqd, buf, attr.mq_msgsize, &prio, &timeout);

	if (numRead == -1) 
		{
  		printf("Child: mq_read error\n");
  		return 5;
		}

// Close and release the queue.
	if (mq_close(mqd) == -1) 
		{ 
  		printf("Child: close error");
   		mq_unlink(argv[1]);
  		return 3;
		}

	if (mq_unlink(argv[1]) == -1) 
		{ 
  		printf("Child: mq_unlink error");
   		mq_unlink(argv[1]);
  		return 3;
		}

	write(STDOUT_FILENO,  "Child: Done...\n", 15);

	return 0;
	}  // End of Child branch  

else { // Parent writes to the message queue. 

	struct timespec timeout = {0, 0};

	// Write the 1st message.
	if (mq_timedsend(mqd, "Hello World", 11, 1, &timeout) == -1)
  		{ 
	  	printf("Parent: mq_send error");
  		return 4;
  		}

	write(STDOUT_FILENO, "Parent: msg 1 sent\n", 19);

// Write the 2nd message. 
	if (mq_timedsend(mqd, "How are you?", 12, 1, &timeout) == -1)
  		{ 
 	 	printf("Parent: mq_send error");
  		return 4;
  		}

	write(STDOUT_FILENO, "Parent: msg 2 sent\n", 19);

// Release the message queue.
	if (mq_close(mqd) == -1) 
		{ 
  		printf("Parent: close error");
   		mq_unlink(argv[1]);
  		return 3;
		}

	if (mq_unlink(argv[1]) == -1)
	  	{ 
  		printf("Parent: mq_unlink error");
  		return 3;
		}

// Parent checks for child termination.
	wait(NULL);
	write(STDOUT_FILENO,  "Parent: Done...\n", 16);
	return 0;
	}   // End of parent branch

}   // End of main()  