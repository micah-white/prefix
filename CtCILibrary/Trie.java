package CtCILibrary;

import java.util.ArrayList;
import java.util.List;


/* Implements a trie. We store the input list of words in tries so
 * that we can efficiently find words with a given prefix. 
 */ 
public class Trie
{
    // The root of this trie.
    private TrieNode root;

    /* Takes a list of strings as an argument, and constructs a trie that stores these strings. */
    public Trie(ArrayList<String> list) {
        root = new TrieNode();
        for (String word : list) {
            root.addWord(word);
        }
    }  
    

    /* Takes a list of strings as an argument, and constructs a trie that stores these strings. */    
    public Trie(String[] list) {
        root = new TrieNode();
        for (String word : list) {
            root.addWord(word);
        }
    }    

    /* Checks whether this trie contains a string with the prefix passed
     * in as argument.
     */
    public boolean contains(String prefix, boolean exact) {
        TrieNode lastNode = root;
        int i = 0;
        for (i = 0; i < prefix.length(); i++) {
            lastNode = lastNode.getChild(prefix.charAt(i));
            if (lastNode == null) {
                return false;	 
            }
        }
        return !exact || lastNode.terminates();
        // if(!lastNode.terminates() && exact)
        //     return null;
        // return lastNode;
    }

    public String longestWord(String prefix){
        TrieNode n = this.prefixNode(prefix, false);
        if(n == null)
            return "";
        return longestSubTree(prefix.substring(0, prefix.length()-1), n);
    }
    
    public boolean contains(String prefix) {
    	return contains(prefix, false);
    }
    
    public TrieNode getRoot() {
    	return root;
    }

    public TrieNode prefixNode(String prefix, boolean exact) {
        TrieNode lastNode = root;
        int i = 0;
        for (i = 0; i < prefix.length(); i++) {
            lastNode = lastNode.getChild(prefix.charAt(i));
            if (lastNode == null) {
                return null;	 
            }
        }
        if(!lastNode.terminates() && exact)
            return null;
        return lastNode;
    }

    private String longestSubTree(String s, TrieNode node){
        if(!Character.isLetter(node.getChar()))
            return "";
        
        List<String> supraStrings = new ArrayList<String>();
        for(int i = 0; i < 26; i++){
            TrieNode lowerNode = node.getChild((char) ('a' + i));
            TrieNode upperNode = node.getChild((char) ('A' + i));
            if(lowerNode != null)
                supraStrings.add(longestSubTree(s + node.getChar(), lowerNode));
            if(upperNode != null)
                supraStrings.add(longestSubTree(s + node.getChar(), upperNode));
        }

        if(supraStrings.size() == 0){
            if(node.terminates())
                return s + node.getChar();
            return "";
        }

        int index = 0;
        for(int i = 1; i < supraStrings.size(); i++){
            if(supraStrings.get(i).length() > supraStrings.get(index).length())
                index = i;
        }

        return supraStrings.get(index);
    }
}
