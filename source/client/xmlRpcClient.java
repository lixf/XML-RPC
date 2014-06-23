/*!Xiaofan Li
 * 15440 P2 XML-RPC
 * Summer 2014
 *
 * Client side rpc
 */

import java.io.ServerSocket;
import java.io.IOException;
import java.lang.RuntimeException;
import java.lang.Class;
import java.lang.relect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class xmlRpcClient implements Runnable {

    private ServerSocket ss;    //server
    private Socket cs;          //client
    private String serverIP;
    private int port;
    private boolean stopped;
    private int serial;
    private parser p;
    
    //I might need the params and the results stored in here
    private Hashtable<String,String> params;
    private Hashtable<String,String> results;

    //only has one constructor which takes IP and port
    //the constructor establishes connection
    public xmlRpcClient(String serverIP, int port){
        this.port = port;
        this.serverIP = serverIP;
        
        //establish connection and handshake
        try {
            this.cs = new Socket(this.serverIP,this.port);
        } catch (IOException e){
            System.out.println("client socket error "+e);
        }
        System.out.println("got a socket to server at "+this.serverIP);
    }
    
    //execute does the following:
    //      1. get object+method name being called, 
    //      2. calls the appropriate stub to convert the request to a XML stream
    //      3. sends the stream to server
    //      4. wait to get a response back
    //      5. return the response as object

    public Object execute(String name, Hashtable<String,Object> params){
        //parse the object and method to call
        int index = name.indexOf('.');
        String obj = name.substring(0,index);
        String method = name.substring(index+1,name.length());

        //now I need to instantiate a object of objStub to convert params
        //to XML format
        xmlRpcClientStub stub = rpcGen(obj,method);

        String request = stub.genXML(params);
        
        //send request to server on socket this.cs
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(this.cs.getInputStream()));
            PrintWriter out = new PrintWriter(this.cs.getOutputStream(), true);
        ) {
            out.write(request);
        } catch (IOException e){
            System.out.println(e);
        }
        
        //wait for a reply
        
        //parse the XML reply and get a return object
    }



}
