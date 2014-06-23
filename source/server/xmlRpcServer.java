/*! Xiaofan Li
 *  15440 Summer 2014
 * \brief This is an implementation of xmlRpcServer
 *        start() is main loop, will block
 *        Only parses requests, does not know about config.txt
 */
import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;
import java.lang.RuntimeException;
import java.lang.Class;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.Hashtable;
import java.util.ArrayList;


public class xmlRpcServer implements Runnable {

    private ServerSocket ss;    //server
    private Socket cs;          //client
    private int port;
    private boolean stopped;
    private String serial;
    private parser p;
    
    //I might need the params and the results stored in here
    private ArrayList<Object> params;
    private ArrayList<String> types;
    private ArrayList<Object> result;

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
        params = new ArrayList<Object>();
        result = null;
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
        params = new ArrayList<Object>();
        result = null;
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
            String path = "../data/"+serial+"_server"+".mix";
            File log = new File(path);
            //try (with resources) get input stream
            try (
                PrintWriter save = new PrintWriter(log);
                //get a writer
                PrintWriter out = new PrintWriter(cs.getOutputStream(),true);
                BufferedReader in = new BufferedReader(
                    new InputStreamReader (cs.getInputStream()));
            ) {
                String temp;
                //for debugging and logging, write the stream to a file
                while((temp = in.readLine()) != null){
                    save.println(temp);
                }
                System.out.println("save an request at "+path);
                System.out.println("parsing request");

                InputStream toParser = new FileInputStream(path);
                p = new parser((InputStream)toParser,true);
                p.parseHTTP();
                p.parseXML();
                System.out.println("finished parsing request");

                this.params = p.getParams();
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
        try { 
           cs.close();
        } catch (IOException e){
            System.out.println("socket problem");
        }
      }
    }

    //parses and handles the request
    //uses the stub to handle the conversion of arguments
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
      String methodName = method.substring(index,method.length());
      
      //get stub and call the stub
      String stubName = objName + "ServerStub";
      
      Class<?> procClass = null;
      Constructor<?> procCon = null;
      SumServerStub H = null; //TODO

      //for dynamically determining the class
      //find class with string
      try {
          procClass = Class.forName(stubName);
      } catch (ClassNotFoundException e) {
          //but actually need to use rpcgen
          System.out.println("cannot find "+objName + " stub error: " + e);
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
          //then how to invocate the specified method?? and put back to a hashtable
          //TODO
          H =  (SumServerStub)procCon.newInstance();
      } catch (InstantiationException e) {
          e.printStackTrace();
      } catch (IllegalArgumentException e) {
          e.printStackTrace();
      } catch (InvocationTargetException e) {
          e.printStackTrace();
      } catch (IllegalAccessException e) {
          e.printStackTrace();
      }
          
          //now pass in the arguments
      try {
          H.putArgs(params);
          //and call the method
          //this returns the xml result
          this.result = H.execute(methodName);
          this.types = H.getTypes();
      } catch (Exception e){
          handleException(e);
      }
    }
    
    //send back results from a object
    private void handleSendBack() {
        //convert the result to InputStream
        printer p = new printer(this.result,false);
        BufferedReader buffedIn = null;
        try {
            p.printXML(this.types);
            String resultXML = p.printHTTP();
            InputStream stream = new ByteArrayInputStream(resultXML.getBytes(StandardCharsets.UTF_8));
            buffedIn = new BufferedReader (new InputStreamReader(stream));
        } catch (IOException e){
            System.out.println("handle back IO error");
        }
        String path = "../data/result_"+this.serial+".mix";

        //send back ../data/result_<serial>.mix
        //get writer and wirte back a bunch 
        //try (with resources) get input stream
        File log = new File(path);
        try (
            PrintWriter save = new PrintWriter(log);
            //get a writer
            PrintWriter sockout = new PrintWriter(this.cs.getOutputStream(),true);
        ) {
            //for debugging and logging, write the stream to a file
            String temp;
            while((temp = buffedIn.readLine()) != null){
                save.println(temp);
                sockout.println(temp);
            }
            System.out.println("save an result at "+path);

            System.out.println("finished sending back result");
            System.out.println("request done");
        } catch (IOException e){
            System.out.println("save result error");
        }
    }
    private void handleException(Exception e){
        System.out.println(e);
    }
}
