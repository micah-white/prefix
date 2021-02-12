package edu.cs606;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import CtCILibrary.*;
import java.util.concurrent.*;

public class PassageProcessor{

  public static void main(String args[]){
    List<String> passageList = new ArrayList<String>();
	try {
		File pList = new File("passages.txt");
		Scanner scanner = new Scanner(pList);
		while (scanner.hasNextLine()){
			passageList.add(scanner.nextLine());
		}
		scanner.close();
	} catch(FileNotFoundException e){
		System.out.println("file not found dummy\n");
		return;
	}
	
	int numPassages = passageList.size();
	String[][] passages = new String[numPassages][];
	for(int i = 0; i < numPassages; i++){
		List<String> words = new ArrayList<String>();
		try {
			File passage = new File(passageList.get(i));
			Scanner scanner = new Scanner(passage);
			while (scanner.hasNext()){
				words.add(scanner.next());
			}
			scanner.close();
			passages[i] = new String[words.size()];
			passages[i] = words.toArray(passages[i]);
		} catch(FileNotFoundException e){
			System.out.println(passageList.get(i) + " not found dummy\n");
		}
	}

	ArrayBlockingQueue[] workers = new ArrayBlockingQueue[numPassages];
    ArrayBlockingQueue resultsOutputArray = new ArrayBlockingQueue(numPassages*10);

	for (int i=0;i<numPassages;i++) {
		workers[i]=new ArrayBlockingQueue(10);
	}

    for (int i = 0; i < numPassages; i++)
    	new Worker(passages[i],i, passageList.get(i), workers[i],resultsOutputArray).start();
		
	MessageJNI jni = new MessageJNI();
	
	int prefixCount = 0;
    while(true){
		SearchRequest msg = jni.readPrefixRequestMsg();
		System.out.println("**prefix(" + msg.requestID + ") " + msg.prefix + " received");
		if(msg.requestID == 0)
			break;

		prefixCount++;
		for(int i = 0; i < numPassages; i++){
			try{
				workers[i].put(msg);
			} catch (InterruptedException e) {};
		}

		int counter=0;

		while (counter<numPassages){
			try {
				String[] outputs = ((String)resultsOutputArray.take()).split(" ");
				String longestWord = outputs[0];
				int passageIndex = Integer.parseInt(outputs[1]);
				int present = 1;
				if(longestWord.equals("----"))
					present = 0;
				jni.writeLongestWordResponseMsg(prefixCount, msg.prefix, passageIndex, passageList.get(passageIndex), longestWord, numPassages, present);
				counter++;
			} catch (InterruptedException e) {};
		}
	}

	for(int i = 0; i < numPassages; i++){
		try {
			workers[i].put("");
		} catch (InterruptedException e) {};
	}

	System.out.println("Terminating...");
  }
}
