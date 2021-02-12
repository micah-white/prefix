JAVA_HOME = /usr/lib/jvm/java-11-openjdk-amd64
EXEC = searchmanager
DELAY = 3
PREFIXES = con pre
# JAVA_HOME = /usr/java/latest

searchmanager: searchmanager.c
	gcc -std=c99 -D_GNU_SOURCE searchmanager.c -o searchmanager

passageprocessor: 	

all: java msgsnd.exe searchmanager

java: 
	javac edu/cs606/*.java
	javac CtCILibrary/*.java
	javac -h . edu/cs606/MessageJNI.java
	gcc -c -fPIC -I$(JAVA_HOME)/include -I$(JAVA_HOME)/include/linux system5_msg.c -o edu_cs606_MessageJNI.o
	gcc -shared -o libsystem5msg.so edu_cs606_MessageJNI.o -lc

msgsnd.exe : msgsnd_pr.c msgrcv_lwr.c
	gcc -std=c99 -D_GNU_SOURCE msgsnd_pr.c -o msgsnd.exe
	gcc -std=c99 -D_GNU_SOURCE msgrcv_lwr.c -o msgrcv.exe
	
threading: java
	java edu.cs606.ParallelTextSearch con

test: searchmanager passageprocessor
	./searchmanager $(DELAY) $(PREFIXES)

remove_queues: 
	ipcrm -Q 0x031000ed

view_queues:
	ipcs -a	

clean:
	rm -f *.exe
	rm -f *.o
	rm -f *.class
	rm $(EXEC)