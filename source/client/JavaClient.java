/*!Xiaofan Li
 *
 *
 */


public class JavaClient {
 public static void main (String [] args) {
  try {
     //read config file and parse part and IP
     String configPath = "../config.txt";
     String serverIP = getServerIP(configPath);
     int port = getPort(configPath);

     XmlRpcClient server = new XmlRpcClient(serverIP,port);
     
     //use for a specific functionality
     Vector params = new Vector();
     params.addElement(new Integer(17));
     params.addElement(new Integer(13));

     //execute will cause the client to call the client stub
     Object result = server.execute("sample.sum", params);

     //This is returned by the stub
     int sum = ((Integer) result).intValue();
     System.out.println("The sum is: "+ sum);

   } catch (Exception exception) {
     System.err.println("JavaClient: " + exception);
   }
  }
  
  private int getServerIP(String path){
    try (
        File config = new File(path);
        InputStream toParser = new FileInputStream(path);
        parser p = new parser((InputStream)toParser);
        return p.findServerIP();
  }

  private int getPort(String path){
    try (
        File config = new File(path);
        InputStream toParser = new FileInputStream(path);
        parser p = new parser((InputStream)toParser);
        return p.findPort();
  }

}
