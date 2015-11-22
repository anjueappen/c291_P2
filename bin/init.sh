#!/bin/bash

echo "This script sets the classpath (for ssh into ohaton) and does other setup stuff."
export CLASSPATH=$CLASSPATH:.:/usr/share/java/db.jar
export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:/oracle/lib

