#include <stdio.h>
#include <sys/types.h>
#include <fcntl.h>
#include <unistd.h>

#define MAXLINE 128

int main(int argc, const char *argv[]) {
    int n, fd[2];
    pid_t pid;
    char line[MAXLINE];
// The pipe is created and its file descriptors returned.
    if ( pipe(fd) < 0) {
        fprintf(stderr, "Pipe failed");
        return 1;
    }

// Parent is producer and child is consumer.

pid = fork();
    if (pid < 0) {
        fprintf(stderr, "Fork Failed");
        return 2;
    }
    else if (pid == 0) { // Child reads from pipe
        close (fd[1]); // write end of pipe
        n = read (fd[0], line, MAXLINE);
        write (STDOUT_FILENO, line, n);
    }
    else { // Parent writes to pipe
        close (fd[0]); // read end of pipe
        write (fd[1], "hello world\n", 12);
    }
    return 0;
} 