package edu.cs606;
import CtCILibrary.*;
import java.util.concurrent.*;

public class PassageProcessor{

  public static void main(String[] args){

    const int numPassages = 5;
    String[] prefixes;
    // String[][]  samples = {{"conspicuous", "parallel", "withering"},{"coping", "figure", "parachute"}};
    ArrayBlockingQueue[] workers = new ArrayBlockingQueue[treeCount];
    ArrayBlockingQueue resultsOutputArray=new ArrayBlockingQueue(treeCount*10);

    if (args.length == 0 || args[0].length() <= 2 ){
        System.out.println("Provide prefix (min 3 characters) for search i.e. con\n");
        System.exit(0);
    }

     for (int i=0;i<numPassages;i++) {
       workers[i]=new ArrayBlockingQueue(10);
    }

    new Worker(samples[0],0,workers[0],resultsOutputArray).start();
    new Worker(samples[1],1,workers[1],resultsOutputArray).start();

    for(int n = 0; n < prefixes.length; n++){
        for(int i = 0; i < numPassages; i++){
            try {
                workers[i].put(prefixes[n]);
            } catch (InterruptedException e) {};
        }
    }    

    int counter=0;

    while (counter<treeCount){
      try {
        String results = (String)resultsOutputArray.take();
        System.out.println("results:"+results);
        counter++;
      } catch (InterruptedException e) {};
    }
  }

}
