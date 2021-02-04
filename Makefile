JAVA_HOME = /usr/lib/jvm/java-8-openjdk-amd64/
# JAVA_HOME = /usr/java/latest


java:
	javac edu/cs606/*.java
	javac CtCILibrary/*.java
	javac -h . edu/cs606/MessageJNI.java
	gcc -c -fPIC -I${JAVA_HOME}/include -I${JAVA_HOME}/include/linux system5_msg.c -o edu_cs606_MessageJNI.o
	gcc -shared -o libsystem5msg.so edu_cs606_MessageJNI.o -lc

test:
	gcc -std=c99 -D_GNU_SOURCE msgsnd_pr.c -o msgsnd
	gcc -std=c99 -D_GNU_SOURCE msgrcv_lwr.c -o msgrcv
	./msgsnd con
	java -cp . -Djava.library.path=. edu.cs606.MessageJNI
	./msgrcv