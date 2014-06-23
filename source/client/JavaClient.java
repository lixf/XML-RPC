/*!Xiaofan Li
 *
 *
 */

import java.util.ArrayList;
import java.io.*;

public class JavaClient {
 public static void main (String [] args) {
  try {
     //read config file and parse part and IP
     String configPath = "../config.txt";
     String serverIP = getServerIP(configPath);
     int port = getPort(configPath);

     xmlRpcClient server = new xmlRpcClient(serverIP,port);
     
     //use for a specific functionality
     ArrayList<Object> params = new ArrayList<Object>();
     ArrayList<String> types = new ArrayList<String>();
     params.add(new Integer(17));
     params.add(new Integer(13));

     types.add("int");
     types.add("int");

     //execute will cause the client to call the client stub
     Object result = server.execute("sample.sum", params,types);

     //This is returned by the stub
     int sum = ((Integer) result).intValue();
     System.out.println("The sum is: "+ sum);

   } catch (Exception exception) {
     System.err.println("JavaClient: " + exception);
   }
  }
  
  private static String getServerIP(String path){
    InputStream toParser = null;
    String serverIP = "";
    try {
        File config = new File(path);
        toParser = new FileInputStream(path);
        parser p = new parser((InputStream)toParser,true);
        serverIP = p.findServerIP();
    } catch (IOException e){
        System.out.println("server ip error");    
    }
    return serverIP;
  }

  private static int getPort(String path){
    InputStream toParser = null;
    int port = 0;
    try {
        File config = new File(path);
        toParser = new FileInputStream(path);
        parser p = new parser((InputStream)toParser,true);
        port = p.findPort();
    } catch (IOException e){
        System.out.println("port error");
    }
    return port;
  }

}
