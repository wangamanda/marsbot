#! /bin/bash
#Author: Chu, Viet Hung vhc@tu-clausthal.de

TEAM1=Argonauts
PASS1=TESTNYPRY8qE

TEAM2=Brainbug
PASS2=TESTYyj19nWo

TEAM3=CowRaiders
PASS3=TESTtKhwsBD

TEAM4=Galoan
PASS4=TESTffKHd6h

TEAM5=Jason-DTU
PASS5=TESTYQvDNA9

TEAM6=PauLo
PASS6=TESTxpMHBD

TEAM7=UCDBogtrotters
PASS7=TESTUbra1f

TEAM8=USPFarmers
PASS8=TESTQ3bQawRT



HOST=$1
NUMBER_OF_AGENT=20

 startAgent ()
{
	i=1
	while [ $i -le $4 ]
		do 
		echo "start: "$1$i":"$2		
		java -Xms8m -Xmx8M  -cp ../target/agentcontest-2010-1.0-jar-with-dependencies.jar massim.agent.DemoGridAgent -username $1$i -password $2 -host $3 -logpath AgentsLog &		
		i=`expr $i + 1`
		done
}

if [ -z $1 ]
	then
	echo "to start this shell script, please type:"
	echo "sh startAgents [host of massim server]"
	echo "example: sh startAgent.sh agentcontest1.in.tu-clausthal.de"
	exit
fi

echo "Please choose teams to start or type 0 to create your own team.
Example: 1 2 \n"

echo "1> to start team "$TEAM1
echo "2> to start team "$TEAM2
echo "3> to start team "$TEAM3
echo "4> to start team "$TEAM4
echo "5> to start team "$TEAM5
echo "6> to start team "$TEAM6
echo "7> to start team "$TEAM7
echo "8> to start team "$TEAM8
echo "0> to create your own team"

echo "your choice: "
read  team1 team2 team3 team4 team5 team6 team7 team8 team9

for team in $team1 $team2 $team3 $team4 $team5 $team6 $team7 $team8 $team9;
do 

	if [ $team -eq 0 ];
		then
		echo "team name: "
		read teamname
		echo "password: "
		read password
		echo "number of agents: "
		read numberofagent
		startAgent $teamname $password $HOST $numberofagent
	 
	fi
	if [ $team -eq 1 ];
		then
		echo "starting team: "$TEAM1 "......."
		startAgent $TEAM1 $PASS1 $HOST	$NUMBER_OF_AGENT
	fi
	if [ $team -eq 2 ];
		then
		echo "starting team: "$TEAM2 "......."
		startAgent $TEAM2 $PASS2 $HOST	$NUMBER_OF_AGENT
	fi
	if [ $team -eq 3 ];
		then
		echo "starting team: "$TEAM3 "......."
		startAgent $TEAM3 $PASS3 $HOST	$NUMBER_OF_AGENT
	fi
	if [ $team -eq 4 ];
		then
		echo "starting team: "$TEAM2 "......."
		startAgent $TEAM4 $PASS4 $HOST	$NUMBER_OF_AGENT
	fi
	if [ $team -eq 5 ];
		then
		echo "starting team: "$TEAM2 "......."
		startAgent $TEAM5 $PASS5 $HOST	$NUMBER_OF_AGENT
	fi
	if [ $team -eq 6 ];
		then
		echo "starting team: "$TEAM2 "......."
		startAgent $TEAM6 $PASS6 $HOST	$NUMBER_OF_AGENT
	fi
	if [ $team -eq 7 ];
		then
		echo "starting team: "$TEAM2 "......."
		startAgent $TEAM7 $PASS7 $HOST	$NUMBER_OF_AGENT
	fi
	if [ $team -eq 8 ];
		then
		echo "starting team: "$TEAM2 "......."
		startAgent $TEAM8 $PASS8 $HOST	$NUMBER_OF_AGENT
	fi
done



