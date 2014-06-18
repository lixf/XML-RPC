/*!\brief This is a parser interface for parsing 
 *        XML wrapped in HTTP POST requests
 * \author Xiaofan Li
 */

import java.io.InputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.nio.charset.StandardCharsets;
import java.io.ByteArrayInputStream;

public class parser {
    //raw input received on socket
    private InputStream rawInput; 
    private HttpParser hp;
    
    //for http
    private String version;
    private String userAgent;
    private String host;
    private int length;
    
    //for xml
    private String method;
    private Hashtable<String,String> params;

    private String xmlContent;

/*!\brief Public constructor 
 */
    public parser (InputStream rawInput) {
        this.rawInput = rawInput;
    }
/*!\brief For sending stuff back 
 * \require a hashtable of results
 */
    public parser (Hashtable<String,String> results){
        //TODO
    }

    public int findPort() {
        String line;
        int index;
        BufferedReader reader = new BufferedReader(new InputStreamReader(rawInput));
        //read line by line until see Port :
        while ((line = reader.readLine())!=null) {
            index = line.indexOf("Port");
            if (index < 0) {
              index = line.indexOf(':');
              String port = line.substring(index+1,line.length());
              return Integer.parseInt(port);
            }  
        }
        return 0;
    }
    
/*!\brief This function parses the HTTP POST request
 *        and populates the private variables
 * \require parser class with rawInput initialized
 * \returns List of Strings including:
 *          version, user-agent, host, length, and the content
 * \exception If length != length(content)
 *            Or content-type != text/xml
 */
    public void parseHTTP () throws IOException{
        hp = new HttpParser(rawInput);
        //this call should populate everything in HttpParser
        hp.parseRequest();
        
        //then we populate our own class with data
        //and prepare for parsing xml
        version = hp.getVersion();
        userAgent = hp.getHeader("User-Agent");
        host = hp.getHeader("host");
        length = Integer.parseInt(hp.getHeader("content-length"));
        xmlContent = hp.getContent();
    }
    
    //define helpers to return desired data
    public String getVersion(){
        return version;
    }

    public String getUserAgent(){
        return userAgent;
    }

    public String getHost(){
        return host;
    }

    public int getLength(){
        return length;
    }

    public String getXmlContent(){
        return xmlContent;
    }


    //Start the XML parser
    public void parseXML() throws IOException {
        if(xmlContent == null){
            throw new IOException();
        }
        else{
            InputStream stream = new ByteArrayInputStream(xmlContent.getBytes(StandardCharsets.UTF_8));
            XmlParser px = new XmlParser(stream);
            px.parseRequest();
            method = px.getMethod();
            params = px.getParams();
        }
    }
    
    public String getMethod(){
        return method;
    }
    
    public Hashtable<String,String> getParams(){
        return params;
    }
    

    //for sending back 
}
