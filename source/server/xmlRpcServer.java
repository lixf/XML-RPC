/*! Xiaofan Li
 *  15440 Summer 2014
 * \brief This is an implementation of xmlRpcServer
 *        start() is main loop, will block
 *        Only parses requests, does not know about config.txt
 */

import java.io.ServerSocket;
import java.io.IOException;
import java.lang.RuntimeException;
import java.lang.Class;
import java.lang.relect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class xmlRpcServer implements Runnable {

    private ServerSocket ss;    //server
    private Socket cs;          //client
    private int port;
    private boolean stopped;
    private int serial;
    private parser p;
    
    //I might need the params and the results stored in here
    private Hashtable<String,String> params;
    private Hashtable<String,String> results;

    //default constructor listens on 80, might throw error
    public xmlRpcServer() {
        this.port = 80;
        try {
            this.ss = new ServerSocket(this.port);
        } catch (IOException e) {
            throw new RuntimeException("cannot open port 80");
        }
        this.stopped = false;
        System.out.println("server up with port 80");
        params = new Hashtable<String,String>();
        results = new Hashtable<String,String>();
        System.out.println("server ready");
    }

    //constructor with port specified by main
    public xmlRpcServer(int port) {
        this.port = port;
        try {
            this.ss = new ServerSocket(this.port);
        } catch (IOException e) {
            throw new RuntimeException("cannot open port "+port);
        }
        this.stopped = false;
        System.out.println("server up with port "+ port);
        params = new Hashtable<String,String>();
        results = new Hashtable<String,String>();
        System.out.println("server ready");
    }

    //supports threading
    public void run() {
        while (!this.stopped) {
            try {
                cs = this.ss.accept();
            } catch (IOException e) {
                if (this.stopped) {
                    System.out.println("Server Stopped");
                    return;
                }
                throw new RuntimeException ("cannot connect to client");
            }
            System.out.println("new client connected");

            //generate a log file
            long millis = System.currentTimeMillis() % 1000;
            this.serial = String.valueOf(millis);
            String path = "../data/"+serial+".mix";
            //try (with resources) get input stream
            try (
                File log = new File(path);
                PrintWriter save = new PrintWriter(log);
                //get a writer
                PrintWriter out = new PrintWriter(cs.getOutputStream(),true);
                BufferedReader in = new BufferedReader(
                    new InputStreamReader (cs.getInputStream()));
            ) {
                //for debugging and logging, write the stream to a file
                while(in.hasNextLine()){
                    String temp = in.readLine();
                    save.println(temp);
                }
                System.out.println("save an request at "+path);
                System.out.println("parsing request");

                InputStream toParser = new FileInputStream(path);
                p = new parser((InputStream)toParser);
                p.parseHTTP();
                p.parseXML();
                System.out.println("finished parsing request");

                handleRequest();
                handleSendBack();
                //close client connection TODO
                cs.close();

            } catch (IOException e){
                System.out.println("reading/Writing problem "+e);
                return;
            }

        }

    }

    public void stop() {
      //close server port and free data structures TODO
      if (cs!=null){
        cs.close();
      }
    }

    //parses and handles the request
    private void handleRequest() {
      //we have parser and all its data structures
      //method should be object.method format
      String method = p.getMethod();
      System.out.println("serving: "+method);

      int index = method.indexOf('.');

      if (index <= 0) {
        System.out.println("cannot find obj info, aborting");
        return;
      }

      String objName = method.substring(0,index);
      String methName = method.substring(index,method.length());

      //assume we have this method and know about its class
      //dynamically find class constructor
      Class<?> procClass = null;
      Constructor<?> procCon = null;

      //for dynamically determining the class
      //find class with string
      try {
          procClass = Class.forName(objName);
      } catch (ClassNotFoundException e) {
          System.out.println("cannot find "+objName + " error: " + e);
          System.exit(1);
      }

      try {
          procCon = procClass.getConstructor(String[].class);
      } catch (SecurityException e) {
          e.printStackTrace();
      } catch (NoSuchMethodException e) {
          e.printStackTrace();
      }

      try {
          Object[] initArgs = new Object[1];
          //how to pass in args?? from a hashtable TODO
          initArgs[0] = args;
          //then how to invocate the specified method?? and put back to a hashtable
          H = new MPHelper((MigratableProcess) procCon.newInstance(initArgs));
      } catch (InstantiationException e) {
          e.printStackTrace();
      } catch (IllegalArgumentException e) {
          e.printStackTrace();
      } catch (InvocationTargetException e) {
          e.printStackTrace();
      } catch (IllegalAccessException e) {
          e.printStackTrace();
      }

      //return the results in a hashtable
      
      return;
    }

    //send back results from a hashtable
    private void handleSendBack() {
        pb = new parser(this.results);  //parser back
        String path = "../data/result_"+this.serial+".mix";
        pb.parseSendBack(path);         //saves the hashtable to a file
        
        //send back ../data/result_<serial>.mix
        //get writer and wirte back a bunch 
    }

}
