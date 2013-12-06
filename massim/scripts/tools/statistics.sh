#!/bin/bash

#date=$( date -I )

cd conf/helpers/2012
for i in $( ls accounts-* )
do
  namewithsuffix=${i#accounts-}
  name=${namewithsuffix%\.xml}
  echo -n "$name: "
  echo "$( grep "Returning InvalidAction to $name" /home/massim/trunk/massim/scripts/backup/no-valid-action | wc -l )/1" | bc -l
done
