package simpledb.server;

import simpledb.buffer.BufferAbortException;
import simpledb.remote.*;
import java.rmi.registry.*;

public class Startup {
   public static void main(String args[]) throws Exception {
       // CS4432-Project1: configure and initialize the database
       if (args.length == 1) {
           SimpleDB.init(args[0], null);
       } else if (args.length == 2) {
           if (args[1].equals("-l") || args[1].equals("-c")) {
               SimpleDB.init(args[0], args[1]);
           } else {
               throw new Exception("invalid command arguments: option not supported");
           }
       } else {
           throw new Exception("invalid command arguments: incorrect number of arguments");
       }
      // create a registry specific for the server on the default port
      Registry reg = LocateRegistry.createRegistry(1099);
      
      // and post the server entry in it
      RemoteDriver d = new RemoteDriverImpl();
      reg.rebind("simpledb", d);
      
      System.out.println("database server ready");


   }
}
