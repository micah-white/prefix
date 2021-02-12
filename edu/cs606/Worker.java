package edu.cs606;
import CtCILibrary.*;
import java.util.concurrent.*;

class Worker extends Thread{

  Trie textTrieTree;
  ArrayBlockingQueue prefixRequestArray;
  ArrayBlockingQueue resultsOutputArray;
  int id;
  String passageName;

  public Worker(String[] words,int id,ArrayBlockingQueue prefix, ArrayBlockingQueue results){
    this.textTrieTree=new Trie(words);
    this.prefixRequestArray=prefix;
    this.resultsOutputArray=results;
    this.id=id;
    this.passageName="Passage-"+Integer.toString(id)+".txt";//put name of passage here
  }

  public void run() {
    System.out.println("Worker-"+this.id+" ("+this.passageName+") thread started ...");
    //while (true){
      try {
        String prefix=(String)this.prefixRequestArray.take();
        String longest = textTrieTree.longestWord(prefix);

        if(longest == "")
          resultsOutputArray.put(passageName+":"+prefix+" not found");
        //System.out.println("Worker-"+this.id+" "+req.requestID+":"+ prefix+" ==> "+word);
        else
          resultsOutputArray.put(passageName+":"+prefix+" lw = " + longest);
        
      } catch(InterruptedException e){
        System.out.println(e.getMessage());
      }
    //}
  }

}
