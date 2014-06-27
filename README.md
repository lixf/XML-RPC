XML-RPC
=======
15440 P2 XML-RPC:
Team Member: Xiaofan Li -- xli2

-------------------------------------------------------------------------
I. Introduction:

This is a project for a RPC framework using XML and HTTP POST request for 
communication.
--------------------------------------------------------------------------
II. Notes to run:

To run the program, change directory to p2/XML-RPC and run the shell script
./setup.sh using bash shell. Note: bash is required!
This script will take care of compiling and running the project. All binary
files will be in ./bin/ and all communication record will be in ./data/
as <time>_<request/response>.mix since the packet contains both HTTP and 
XML format. 

1. compile: run ./setup.sh make
2. run server: run ./setup.sh server
3. run client: run ./setup.sh client 
   and then follow the instruction for currently supported rpc calls. 
4. clean: run ./setup.sh clean

You can see the result of the RPC call printed on screen or you can go in
./data/ to inspect individual .mix files. 
--------------------------------------------------------------------------
III. What is required from programmer

The framework takes care of communication including network, parsing, 
generating XML, HTTP, and dynamically locate the desired class/object. 
However, it's the programmer's responsibility to provoide the actual
remote object he/she wishes to run as well as the stub. 

Now, the stub only takes care of the type conversion and the argument placing. 
Thus, it is very possible to automatically generate the stub as rpcgen does 
in the C framework. However, I think this unnecessary: since we already need to
take in a file (.x in rpcgen), we could as well require the programmer to 
provide the stub itself. The baseline is, if I wanted to implement rpcgen, 
I could have done it, but I find it complicated for no apparent reasons. 

The programmer also needs to provide the remote object he/she wants to invoke. 
There is no restrictions on what the actaul format or content of the object. 
For example, it can take in any number of arguments and return any number of
objects of type String, Integer or Boolean. Higher level data structures 
should be constructed after the primitives are received on either end of the 
communication. Therefore, there is no reason to support higher level data types
by default. 
--------------------------------------------------------------------------------
IV. Design and runtime spec

The core of the framework resides in xmlRpcServer.java and xmlRpcClient.java
where communication takes place. 

On the client side, the xmlRpcClient object can be instantiated. It takes in 
a IP address and port to set up communication with the server. In this project,
the IP address and port are stored in ./config.txt, which is parsed by the main
function at runtime. However, in general, the framework does not care how those 
parameters are passed in. (Which is why findPort and findServerIP are in the 
JavaServer file instead of xmlRpcClient)

Then the main function will call execute three things:
1. the name of the remote method. this has the form "object.method"
2. a ArrayList<Object> of the arguments
3. a ArrayList<String> specifying the type of each argument

Then the client will be responsible to print the XML request, wrapping it in a 
HTTP POST request and send to the remote server. 

Upon receiving the request, the server will parse the request and extract 
important information such as the paramters, the name of method etc. At this
point, the hostname, the user-agent information are not being used while they
are extracted. But we can use it for logging purposes in the future version. 

Having the necessary information, the server will use java.reflect to
dynamically locate the stub object and fill in the parameters to prepare 
for the function call. This happens in xmlRpcServer.java. 

After the actual method returns, the result(s) is passed back in ArrayList 
with a list of its types. Then the server generates a response message 
based on the returned result and its type. 

The rest is easy to understand: the client get the response, parse it, cast it
to the right type and then print on screen. Then it closes the connection. 
-------------------------------------------------------------------------------
V. What I did not do (and probably should have done)

1. Right now the framework does not support higher level data structures such 
   as arrays, hashtables, structs.. 

2. Programmers need to generate the stub file.

3. Fault handling: a malicious user could crash the server. The framework does
   return a fault response under certain circumstances but the framework should
   be more robust.
--------------------------------------------------------------------------------
VI. Final thoughts 

Implementing RPC was really fun, while I did find java is kind of annoying 
in both its compoiling process and its runtime. I might do it again in C 
sometime later. Thanks for reading, if you are here. 
--------------------------------------------------------------------------------
VII. Appendix

Current test cases:
1. Sum: adds two numbers. Feel free to change the arguments in JavaClientSum
2. Mult: multiplies two numbers.
3. Fib: get the nth fibonacci number. 
4. Con: concatenate two strings. s1s2 if bool = true; s2s1 otherwise. 
