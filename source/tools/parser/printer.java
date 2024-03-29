/*!\brief This is a printer interface for parsing 
 *        XML wrapped in HTTP POST requests
 * \author Xiaofan Li
 */

import java.io.InputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.ArrayList;
import java.nio.charset.StandardCharsets;
import java.io.ByteArrayInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class printer {
    //raw inputs from parent prog
    private ArrayList<Object> rawInput; 
    
    //for http
    private String version;
    private String userAgent;
    private String host;
    private int xmlLength;
    private int respCode;
    private String serverName;
    
    //for xml
    private String method;
    private String fault;
    private ArrayList<Object> params;
    private ArrayList<Object> result;

    private String xmlContent;

    //am i parsing a request?
    private boolean request;

/*!\brief Public constructor 
 */
    public printer (ArrayList<Object> rawInput,boolean request) {
        this.rawInput = rawInput;
        this.request = request;
    }

/*!\brief This function prints the HTTP POST request
 * \require printer class with rawInput initialized
 * \returns List of Strings including:
 *          version, user-agent, host, length, and the content
 * \exception If length != length(content)
 *            Or content-type != text/xml
 */
    public String printHTTP () throws IOException{
        String content = "";
        if (this.request){
            String first = "POST /RPC2 HTTP/1.0\nUser-Agent: kali\nHost:\nContent-Type: text/xml\nContent-length:";
            content = first + this.xmlLength + "\n\n\n\n" + this.xmlContent;
        }
        else {
            String first = "HTTP/1.1 200 OK\nConnection: close\nContent-Length:";
            String third = "Content-Type: text/xml\n\n\n";
            content = first + this.xmlLength + "\n" + third + this.xmlContent;
        }
        
        return content;
        
    }


    //Start the XML printer:
    //Takes in a types and match the one got from params
    public void printXML(String methodName, ArrayList<String> types) throws IOException {
        //hard code these
        String xmlHeader = "<?xml version='1.0'?>\n<methodCall>\n<params>\n";
        String name = "<methodName>"+methodName+"</methodName>\n";
        int numArgs = types.size();
        String xmlFooter = "</params>\n</methodCall>";
        String content = xmlHeader + name;

        for (int i=0;i<numArgs;i++) {
            String paramXML = printOneParam(types.get(i),this.params.get(i));
            //append the next parameter
            content = content + paramXML;
        }
        //append footer
        content = content + xmlFooter;
        this.xmlContent = content;
        this.xmlLength = content.length();
    }
    
    public void printXML(ArrayList<String> types) throws IOException{
        
        String xmlHeader = "<?xml version='1.0'?>\n<methodResponse>\n<params>\n";
        String xmlFooter = "</params>\n</methodResponse>";
        int numArgs = types.size();
        String content = xmlHeader;

        for (int i=0;i<numArgs;i++) {
            String paramXML = printOneParam(types.get(i),this.params.get(i));
            //append the next parameter
            content = content + paramXML;
        }
        //append footer
        content = content + xmlFooter;
        this.xmlContent = content;
        this.xmlLength = content.length();
    }
   

    private String printOneParam (String type, Object param){
        String stuff = "";
        switch (type) {
          case "int" : 
                stuff = ((Integer)param).toString();
                return "<param>\n<value><i4>"+stuff+"</i4></value>\n</param>";
          case "string" :
                stuff = (String)param;
                return "<param>\n<value><string>"+stuff+"</string></value>\n</param>";
          case "boolean" : 
                stuff = ((Boolean)param).toString();
                return "<param>\n<value><boolean>"+stuff+"</boolean></value>\n</param>";
          default : return "";
        }
    }


    
    public ArrayList<Object> getParams(){
        return params;
    }
   
}
