package edu.cs606;

import java.util.concurrent.*;

public class MessageJNI {

    static {
        System.loadLibrary("system5msg");
    }

    public static void main(String[] args) {
        //System.out.println(new MessageJNI().readStringMsg("anderson",65));
        System.out.println(new MessageJNI().readPrefixRequestMsg());
        new MessageJNI().writeLongestWordResponseMsg( 1, "and", 1, "Jane Austen","andulousa", 6, 1);
    }

    // Declare a native method sayHello() that receives no arguments and returns void
    private static native String readStringMsg();

    public static native SearchRequest readPrefixRequestMsg();

    public static native void writeLongestWordResponseMsg( int prefixID, String prefix, int passageIndex, String passageName,String longestWord, int passageCount, int present);

}
