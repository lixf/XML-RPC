######################################################################
## Xiaofan Li
## 15440 
## setup script
## I don't know Makefiles for Java, especially I don't like classpath
## because it's confusing. Please run this script to compile and run
######################################################################


#!/bin/bash
echo ""
echo "please make sure you use bash"
echo "current shell is: "$SHELL
echo ""

## fonts
blue='\e[0;34m'
green='\e[0;32m'
red='\e[0;31m'
NC='\e[0m' # No Color
echo -e "${blue}Hello User${NC}"
echo -e "${blue}Welcome to my RPC control${NC}"

## paths
SOURCE_PATH='/afs/andrew.cmu.edu/usr9/xli2/private/15440/p2/XML-RPC/source'
BIN_PATH='/afs/andrew.cmu.edu/usr9/xli2/private/15440/p2/XML-RPC/bin'
SERVER_SOURCE=$SOURCE_PATH'/server/*.java'
CLIENT_SOURCE=$SOURCE_PATH'/client/*.java'
TOOLS_SOURCE=$SOURCE_PATH'/tools'
BINARY_SOURCE=$BIN_PATH

## define some functions
function makefiles {
    # Compile all source code
    javac -Xlint -d $BINARY_SOURCE $TOOLS_SOURCE/*.java
    javac -Xlint -d $BINARY_SOURCE -cp $TOOLS_SOURCE $SERVER_SOURCE
    javac -Xlint -d $BINARY_SOURCE -cp $TOOLS_SOURCE $CLIENT_SOURCE
    echo -e "${green}all files compiled in ./bin/${NC}"
}

function cleanfiles {
    #delete all files 
    rm ./bin/*
    echo -e "${green}all class files removed${NC}"
}

function runserver {
    #run server blocks
    echo -e "${green}starting server${NC}"
    cd $BINARY_SOURCE
    java JavaServer
}

function runclient {
    #run client with command prompt
    echo -e "${green}starting client${NC}"
    echo -e "${green}please choose from sum/mult/fib/con${NC}"
    read choice
    cd $BINARY_SOURCE
    case "$choice" in
    sum)    java JavaClientSum; exit 0;;
    mult)   java JavaClientMult; exit 0;;
    fib)    java JavaClientFib; exit 0;;
    con)    java JavaClientCon; exit 0;;
    *)      echo ${red}please choose from sum/mult/fib/con${NC}; exit 0;;
    esac
}

function usage {
  echo -e "${red}options: make clean server client${NC}"
}

## start the program
if [ $# -lt 1 ]; then 
  echo -e "${red}you should at least have one argument!${NC}"
  echo -e "${red}options: make clean server client${NC}"
else 
  while [ "$1" != "" ]; do
    case $1 in
    make  )     makefiles;  exit 0;; 
    clean )     cleanfiles; exit 0;;
    server)     runserver;  exit 0;;
    client)     runclient;  exit 0;;
        * )     usage;      exit 0;;
    esac
  done
fi


