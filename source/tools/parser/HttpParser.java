import java.io.*;
import java.util.*;
import java.text.*;
import java.net.URLDecoder;

public class HttpParser {
  private static final String[][] HttpReplies = {{"100", "Continue"},
                                                 {"101", "Switching Protocols"},
                                                 {"200", "OK"},
                                                 {"201", "Created"},
                                                 {"202", "Accepted"},
                                                 {"203", "Non-Authoritative Information"},
                                                 {"204", "No Content"},
                                                 {"205", "Reset Content"},
                                                 {"206", "Partial Content"},
                                                 {"300", "Multiple Choices"},
                                                 {"301", "Moved Permanently"},
                                                 {"302", "Found"},
                                                 {"303", "See Other"},
                                                 {"304", "Not Modified"},
                                                 {"305", "Use Proxy"},
                                                 {"306", "(Unused)"},
                                                 {"307", "Temporary Redirect"},
                                                 {"400", "Bad Request"},
                                                 {"401", "Unauthorized"},
                                                 {"402", "Payment Required"},
                                                 {"403", "Forbidden"},
                                                 {"404", "Not Found"},
                                                 {"405", "Method Not Allowed"},
                                                 {"406", "Not Acceptable"},
                                                 {"407", "Proxy Authentication Required"},
                                                 {"408", "Request Timeout"},
                                                 {"409", "Conflict"},
                                                 {"410", "Gone"},
                                                 {"411", "Length Required"},
                                                 {"412", "Precondition Failed"},
                                                 {"413", "Request Entity Too Large"},
                                                 {"414", "Request-URI Too Long"},
                                                 {"415", "Unsupported Media Type"},
                                                 {"416", "Requested Range Not Satisfiable"},
                                                 {"417", "Expectation Failed"},
                                                 {"500", "Internal Server Error"},
                                                 {"501", "Not Implemented"},
                                                 {"502", "Bad Gateway"},
                                                 {"503", "Service Unavailable"},
                                                 {"504", "Gateway Timeout"},
                                                 {"505", "HTTP Version Not Supported"}};

  private BufferedReader reader;
  private String method, url;
  private Hashtable <String,String> headers;
  private int[] ver;
  private String content;


  public HttpParser(InputStream is) {
    reader = new BufferedReader(new InputStreamReader(is));
    method = "";
    url = "";
    headers = new Hashtable<String,String>();
    //version
    ver = new int[2];
  }

  public int parseRequest() throws IOException {
    String initial, prms[], cmd[], temp[];
    int ret, idx, i;

    ret = 200; // default is OK now
    initial = reader.readLine();
    if (initial == null || initial.length() == 0) return 0;
    if (Character.isWhitespace(initial.charAt(0))) {
      // starting whitespace, return bad request
      return 400;
    }
    
    //catch bad req
    cmd = initial.split("\\s");
    if (cmd.length != 3) {
      return 400;
    }
    
    //only works with RPC2
    if (!cmd[1].equals("/RPC2")) ret = 400;
    
    if (cmd[2].indexOf("HTTP/") != 0) ret = 400;
    else {
        //TODO
        ver[0] = 1;
        ver[1] = 0;
    }

    //parse req
    if (cmd[0].equals("POST")) {
      parseHeaders();
      putContent();
      if (headers == null) ret = 400;
    }
    else {
      ret = 400;
    }

    if (ver[0] != 1 || ver[1] != 0 || getHeader("Host") == null) {
      ret = 400;
    }

    return ret;
  }

  private void parseHeaders() throws IOException {
    String line;
    int idx;

    line = reader.readLine();
    while (!line.equals("")) {
      idx = line.indexOf(':');
      if (idx < 0) {
        headers = null;
        break;
      }
      else {
        headers.put(line.substring(0, idx).toLowerCase(), line.substring(idx+1).trim());
      }
      line = reader.readLine();
    }
  }

  //this put the rest of reader to content ready to be extracted
  private void putContent() throws IOException {
    String line;

    line = reader.readLine();

    line = reader.readLine();
    content = line;
    while (line != null){
        content = content + line + "\n";
        line = reader.readLine();
    }
  }


  //a bunch of helpers to communicate outside
  public String getMethod() {
    return method;
  }

  public String getHeader(String key) {
    if (headers != null)
      return (String) headers.get(key.toLowerCase());
    else return null;
  }

  public Hashtable getHeaders() {
    return headers;
  }

  public String getContent(){
    return content;
  }
    
  public String getVersion() {
    return ver[0] + "." + ver[1];
  }

  public int compareVersion(int major, int minor) {
    if (major < ver[0]) return -1;
    else if (major > ver[0]) return 1;
    else if (minor < ver[1]) return -1;
    else if (minor > ver[1]) return 1;
    else return 0;
  }

  public static String getHttpReply(int codevalue) {
    String key, ret;
    int i;

    ret = null;
    key = "" + codevalue;
    for (i=0; i<HttpReplies.length; i++) {
      if (HttpReplies[i][0].equals(key)) {
        ret = codevalue + " " + HttpReplies[i][1];
        break;
      }
    }

    return ret;
  }

  public static String getDateHeader() {
    SimpleDateFormat format;
    String ret;

    format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss", Locale.US);
    format.setTimeZone(TimeZone.getTimeZone("GMT"));
    ret = "Date: " + format.format(new Date()) + " GMT";

    return ret;
  }
}
