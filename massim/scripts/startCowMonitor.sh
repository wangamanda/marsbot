#! /bin/bash

package=$( ls ../target/agentcontest-*.jar | grep -v javadoc | grep -v sources )

if [ -z $1 ]; then
	host=localhost
else
	host=$1
fi

if [ -z $2 ];then
	port=1099
else
	port=$2
fi

java -Xss20000k -cp $package massim.monitor.CowMonitor -rmihost $host -rmiport $port 
