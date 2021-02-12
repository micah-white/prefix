package edu.cs606;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import CtCILibrary.*;
import java.util.concurrent.*;

public class PassageProcessor{

  public static void main(){

    String[] prefixes;
    List<String> passageList = new ArrayList<String>();
    String[][] passages;
    ArrayBlockingQueue[] workers = new ArrayBlockingQueue[numPassages];
    ArrayBlockingQueue resultsOutputArray=new ArrayBlockingQueue(numPassages*10);

    for (int i=0;i<numPassages;i++) {
       workers[i]=new ArrayBlockingQueue(10);
    }
	try {
		File pList = new File("passsages.txt");
		Scanner scanner = new Scanner(pList);
		while (scanner.hasNextLine()){
			passageList.add(scanner.nextLine());
		}
		scanner.close();
	} catch(FileNotFoundException e){
		System.out.println("file not found dummy\n");
	}
	
	int numPassages = passageList.size();

    for (int i = 0; i < numPassages; i++)
    	new Worker(passages[i],i,workers[0],resultsOutputArray).start();
    

    for(int n = 0; n < prefixes.length; n++){
        for(int i = 0; i < numPassages; i++){
            try {
                workers[i].put(prefixes[n]);
            } catch (InterruptedException e) {};
        }
    }    

    int counter=0;

    while (counter<numPassages){
      try {
        String results = (String)resultsOutputArray.take();
        System.out.println("results:"+results);
        counter++;
      } catch (InterruptedException e) {};
    }
  }

}
