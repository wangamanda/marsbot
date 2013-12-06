#! /bin/bash

TEAM1=TUCBot
PASS1=4FdbWnE

HOST=localhost

NUMBER_OF_AGENT=20

NUM=0
QUEUE=""

function queue {
        QUEUE="$QUEUE
$1"
        NUM=$(($NUM+1))
}

function dequeue {
        OLDDEQUEUE=$QUEUE
        QUEUE=""
        for PID in $OLDDEQUEUE
        do
                if [ ! "$PID" = "$1" ] ; then
                        QUEUE="$QUEUE
$PID"
                fi
        done
        NUM=$(($NUM-1))
}

function checkqueue {
        OLDCHQUEUE=$QUEUE
        for PID in $OLDCHQUEUE
        do
                if [ ! -d /proc/$PID ] ; then
                        dequeue $PID
                fi
        done
}



function startAgent {
	i=1
	while [ $i -le $4 ]
		do 
		echo "start: "$1$i":"$2		
		java -Xms8m -Xmx8M  -cp ../target/agentcontest-2010-1.0-jar-with-dependencies.jar massim.agent.DemoGridAgent -username $1$i -password $2 -host $3 -logpath AgentsLog &
		queue $!
		i=`expr $i + 1`
		done
}

while (true)
do
  startAgent $TEAM1 $PASS1 $HOST $NUMBER_OF_AGENT
  while [ $NUM -gt 0 ] 
  do
    checkqueue
    sleep 15
  done
  echo "restarting agents"
  sleep 15
done

