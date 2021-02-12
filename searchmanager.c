#include <sys/types.h>
#include <sys/ipc.h>
#include <sys/msg.h>
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <errno.h>
#include <ctype.h>
#include <time.h>
#include <signal.h>
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
void sigintHandler();
int* counts;
char** prefixes;
int numPrefixes;
int started;
int numPassages;

int main(int argc, char**argv)
{
	started = 0;
	numPassages = -1;
	signal(SIGINT, sigintHandler);
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
	
	counts = malloc(sizeof(int) * (argc-2));
	prefixes = argv;
	numPrefixes = argc-2;
	started = 1;
	for(int i = 2; i < argc; i++){
		
		if(strlen(argv[i]) < 3 || strlen(argv[i]) > 20){
			printf("Prefix %s is not 3-20 characters long. Skipping\n", argv[i]);
		}
		else {
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
				fprintf(stdout,"\nMessage(%d): \"%s\" Sent (%d bytes)\n\nReport \"%s\"\n", sbuf.id, sbuf.prefix,(int)buf_length, sbuf.prefix);

			response_buf **outputs = NULL;
			response_buf* rbuf;
			int receivedCount = 0;
			do {
				int ret;
				rbuf = malloc(sizeof(response_buf));
				do {
					ret = msgrcv(msqid, rbuf, sizeof(response_buf), 2, 0);//receive type 2 message
					int errnum = errno;
					if (ret < 0 && errno !=EINTR){
						fprintf(stderr, "Value of errno: %d\n", errno);
						perror("Error printed by perror");
						fprintf(stderr, "Error receiving msg: %s\n", strerror( errnum ));
					}
				} while ((ret < 0 ) && (errno == 4));
				numPassages = rbuf->count;
				receivedCount++;
				counts[i-2]++;
				//fprintf(stderr,"msgrcv error return code --%d:$d--",ret,errno);
				if(!outputs)
					outputs = (response_buf**) malloc(numPassages * sizeof(response_buf*));
				outputs[rbuf->index] = rbuf;
			} while (receivedCount < numPassages);

			for(int i = 0; i < numPassages; i++){
				rbuf = outputs[i];
				if (rbuf->present == 1)
					printf("Passage %d - %s - %s\n", rbuf->index,rbuf->location_description,rbuf->longest_word);
				else
					printf("Passage %d - %s - no word found\n", rbuf->index,rbuf->location_description);
				free(outputs[i]);
			}

			free(outputs);

			if(i != argc-1)
				delay(atoi(argv[1]));
		}
	}

	//terminate passage processor
	prefix_buf sbuf;
	sbuf.mtype = 1;
    sbuf.id = 0;
	strlcpy(sbuf.prefix,"   ",WORD_LENGTH);
    if((msgsnd(msqid, &sbuf, sizeof(int)+1, IPC_NOWAIT)) < 0){
			int errnum = errno;
			fprintf(stderr,"%d, %ld, %s, %d\n", msqid, sbuf.mtype, sbuf.prefix, (int)buf_length);
			perror("(msgsnd)");
			fprintf(stderr, "Error sending msg: %s\n", strerror( errnum ));
			exit(1);
	}
	fprintf(stdout,"\nMessage(%d): \"%s\" Sent (%d bytes)\n\n", sbuf.id, sbuf.prefix,(int)buf_length);

	printf("Exiting...\n");
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

void sigintHandler(){
	printf("\n");
	if(started == 0)
		return;
	for(int i = 0; i < numPrefixes; i++){
		if(counts[i] == 0)
			printf("%s - pending\n", prefixes[i+2]);
		else if(counts[i] == numPassages)
			printf("%s - done\n", prefixes[i+2]);
		else
			printf("%s - %d of %d\n", prefixes[i+2], counts[i], numPassages);
	}
}
