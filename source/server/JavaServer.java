/*!Xiaofan Li
 *\brief XMLRPC server side runnable Blocks until control-C
 *\require Pre-exchanged IP addr and parsing of config file
 */

import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.IOException;

public class JavaServer {

  public static void main (String [] args) {
     try {
       System.out.println("Attempting to start XML-RPC Server...");
       int port = getPort("../config.txt");
       xmlRpcServer server = new xmlRpcServer(port);
       server.run();//this will block
     } catch (Exception exception) {
       System.err.println("JavaServer: " + exception);
     }
  }
  

  private static int getPort(String path){
    int port;
    try {
        File config = new File(path);
        InputStream toParser = new FileInputStream(path);
        parser p = new parser(toParser,false);
        port = p.findPort();
    } catch (IOException e){
        System.out.println(e);
        return 0;
    }
        return port;
  }
}
