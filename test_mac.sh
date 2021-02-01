rm *.o
rm *.dylib
rm edu_cs606_anderson_MessageJNI.h
javac CtCILibrary/*.java
javac -h . edu/cs606/MessageJNI.java
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk1.8.0_111.jdk/Contents/Home
gcc -c -fPIC -I${JAVA_HOME}/include -I${JAVA_HOME}/include/darwin -Dmac system5_msg.c -o edu_cs606_MessageJNI.o
gcc -dynamiclib -o libsystem5msg.dylib edu_cs606_MessageJNI.o -lc
gcc -std=c99 -D_GNU_SOURCE -Dmac msgsnd_pr.c -o msgsnd
gcc -std=c99 -D_GNU_SOURCE msgrcv_lwr.c -o msgrcv
./msgsnd con
java -cp . -Djava.library.path=. edu.cs606.MessageJNI
./msgrcv
java edu.cs606.ParallelTextSearch con
