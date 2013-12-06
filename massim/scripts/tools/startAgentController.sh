#! /bin/bash
package=$( ls ../target/agentcontest-*.jar | grep -v javadoc | grep -v sources )
conf=../conf/

echo "Please choose a number and then press enter:"
count=0
for i in $( ls $conf )
do
  if [ -f $conf/$i ]
  then
    echo $count: $i
    count=`expr $count + 1`
  fi
done

read number

count=0
for i in $( ls $conf )
do
  if [ -f $conf/$i ]
  then
    if [ $number -eq $count ]
    then
      conf+=$i
    fi
    count=`expr $count + 1`
  fi
done

echo "Starting server: $conf"

java -DentityExpansionLimit=2000000 -DelementAttributeLimit=1000000 -Xss20000k -cp $package massim.monitor.AgentController $conf
