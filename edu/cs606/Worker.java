package edu.cs606;
import CtCILibrary.*;
import java.util.concurrent.*;

class Worker extends Thread{

  Trie textTrieTree;
  ArrayBlockingQueue prefixRequestArray;
  ArrayBlockingQueue resultsOutputArray;
  int id;
  String passageName;

  public Worker(String[] words,int id, String pName, ArrayBlockingQueue prefix, ArrayBlockingQueue results){
    this.textTrieTree=new Trie(words);
    this.prefixRequestArray=prefix;
    this.resultsOutputArray=results;
    this.id=id;
    this.passageName=pName;//put name of passage here
  }

  public void run() {
    System.out.println("Worker-"+this.id+" ("+this.passageName+") thread started ...");
    while (true){
      try {
        SearchRequest req=(SearchRequest)this.prefixRequestArray.take();
        String prefix = req.prefix;
        if(prefix == "")
          break;
        String longest = textTrieTree.longestWord(prefix);

        if(longest == ""){
          System.out.println("Worker-"+this.id+" "+req.requestID+":"+ prefix + " ==> not found");
          longest = "----";
        }  
        else
          System.out.println("Worker-"+this.id+" "+req.requestID+":"+ prefix + " ==> "+ longest);

        resultsOutputArray.put(longest + " " + this.id);
      } catch(InterruptedException e){
        System.out.println(e.getMessage());
      }
    }
  }

}
