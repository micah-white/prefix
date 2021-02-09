#include <sys/types.h>
#include <sys/ipc.h>
#include <sys/msg.h>
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <errno.h>
#include <ctype.h>
#include <time.h>
#include "longest_word_search.h"
#include "queue_ids.h"

#ifndef mac
size_t                  /* O - Length of string */
strlcpy(char       *dst,        /* O - Destination string */
        const char *src,      /* I - Source string */
        size_t      size)     /* I - Size of destination string buffer */
{
    size_t    srclen;         /* Length of source string */

    size--;

    srclen = strlen(src);

    if (srclen > size)
        srclen = size;

    memcpy(dst, src, srclen);
    dst[srclen] = '\0';

    return (srclen);
}
#endif

int isNum(char* s);
void delay(int seconds);

int main(int argc, char**argv)
{
    //msgsnd
    int msqid;
    int msgflg = IPC_CREAT | 0666;
    key_t key;
    size_t buf_length;

    if (argc <= 2 || !isNum(argv[1])) {
        printf("Usage: %s <delay> <prefix>\n",argv[0]);
        exit(-1);
    }

    key = ftok(CRIMSON_ID,QUEUE_NUMBER);
    if ((msqid = msgget(key, msgflg)) < 0) {
        int errnum = errno;
        fprintf(stderr, "Value of errno: %d\n", errno);
        perror("(msgget)");
        fprintf(stderr, "Error msgget: %s\n", strerror( errnum ));
    }

		
	for(int i = 2; i < argc; i++){
		int numPassages = -1;

		prefix_buf sbuf;
		sbuf.mtype = 1;
		strlcpy(sbuf.prefix,argv[i],WORD_LENGTH);
		sbuf.id = i-1;
		buf_length = strlen(sbuf.prefix) + sizeof(int)+1;//struct size without long int type

		// Send a message.
		if((msgsnd(msqid, &sbuf, buf_length, IPC_NOWAIT)) < 0) {
			int errnum = errno;
			fprintf(stderr,"%d, %ld, %s, %d\n", msqid, sbuf.mtype, sbuf.prefix, (int)buf_length);
			perror("(msgsnd)");
			fprintf(stderr, "Error sending msg: %s\n", strerror( errnum ));
			exit(1);
		}
		else
			fprintf(stderr,"Message(%d): \"%s\" Sent (%d bytes)\n", sbuf.id, sbuf.prefix,(int)buf_length);


		if(i != argc-1)
			delay(atoi(argv[1]));
	}

	//terminate passage processor
	prefix_buf sbuf;
	sbuf.mtype = 1;
    sbuf.id = 0;
    if((msgsnd(msqid, &sbuf, sizeof(int)+1, IPC_NOWAIT)) < 0){
			int errnum = errno;
			fprintf(stderr,"%d, %ld, %s, %d\n", msqid, sbuf.mtype, sbuf.prefix, (int)buf_length);
			perror("(msgsnd)");
			fprintf(stderr, "Error sending msg: %s\n", strerror( errnum ));
			exit(1);
	}

	while(1){}

    exit(0);
}

int isNum(char* s){
	int n = 0;
	char c = s[n];
	while(c != '\0'){
		if(!isdigit(c))
			return 0;
		c = s[++n];
	}
	return 1;
}

void delay(int seconds){
    long pause;
    clock_t now,then;

    pause = seconds*(CLOCKS_PER_SEC);
    now = then = clock();
    while( (now-then) < pause )
        now = clock();
}