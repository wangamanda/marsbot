#!/bin/bash

actions="attack buy goto inspect parry probe recharge repair skip survey"

teams=$( grep -e "team=" *_0.xml | sed -e "s/^.*team=\"//g" -e "s/\".*$/ /g" | head -n 2 | tr -d "\n" )
simulation=$( grep -e "simulation=" *_0.xml | sed -e "s/^.*simulation=\"//g" -e "s/\".*$//g")
echo "### $teams - $simulation ###"

grep -h -e "<entity.*>" *.xml | sed -e "s/^.*lastAction=/lastAction=/g" -e "s/ visRange.*$//g" | awk '{print $1, $3, $7, $11}' > temp

for t in $teams
do
  grep -h -e "team=\"$t\"" temp > temp1
  for n in $( seq 1 10 )
  do
    grep -h -e "name=\"$t$n\"" temp1 > temp2
    checkedrole=0
    echo -n "$t$n"
    for a in $actions
    do
      if [ $a = "skip" ]
      then
        quantity=-1
	quantity1=-1
      else
        quantity=0
	quantity1=0
      fi
      if [ $checkedrole -lt 1 ]
      then
        role=$( grep -e "name=\"$t$n\" .* roleName" *_0.xml | sed -e "s/^.*roleName=\"//g" -e "s/\".*$//g" )
        checkedrole=1
        echo "-$role"
      fi
      quantity=$(($quantity+ `grep -h -c -e "lastAction=\"$a\".*name=\"$t$n\"" temp2`))
      quantity1=$(($quantity1+ `grep -h -c -e "lastAction=\"$a\".*lastActionResult=\"successful\".*name=\"$t$n\"" temp2`))
      if [ $quantity -gt 0 ]
      then
        echo -e " $a: $quantity1 of $quantity ($( echo "scale=2; $quantity1*100/$quantity" | bc -l )%)"
      fi
    done
  done
done
