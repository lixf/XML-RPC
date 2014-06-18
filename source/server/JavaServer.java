/*!Xiaofan Li
 *\brief XMLRPC server side runnable Blocks until control-C
 *\require Pre-exchanged IP addr and parsing of config file
 */


public class JavaServer {

  public Integer sum(int x, int y) {
     return new Integer(x+y);
  }

  public static void main (String [] args) {
     try {
       System.out.println("Attempting to start XML-RPC Server...");
       int port = getPort("../config.txt");
       xmlRpcServer server = new xmlRpcServer(port);
       server.start();//this will block
     } catch (Exception exception) {
       System.err.println("JavaServer: " + exception);
     }
  }
  

  private int getPort(String path){
    try (
        File config = new File(path);
        InputStream toParser = new FileInputStream(path);
        parser p = new parser((InputStream)toParser);
        return p.findPort();
  }
}
