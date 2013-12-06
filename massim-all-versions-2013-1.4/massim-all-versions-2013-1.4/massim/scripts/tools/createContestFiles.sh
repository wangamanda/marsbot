#! /bin/bash

# initialize certain values
package=$( ls ../target/agentcontest-*.jar | grep -v javadoc | grep -v sources )
conf=../conf
template=$conf/helpers/2010/template-contest.xml
pngs=../maps
date=`date +%Y`

mkdir -p $conf
cd $pngs
pwd
for i in $( ls *Two.png )
do
  j=${i%.*}
  java -ea -cp $package massim.mapmaker.App $i $template $conf/$date-contest-$j.xml
  sed -i 's#<?xml version="1.0" encoding="UTF-8"?>#<?xml version="1.0" encoding="UTF-8"?><!DOCTYPE conf SYSTEM "helpers/2010/config.dtd">#g' $conf/$date-contest-$j.xml
  sed -i "s#</conf>#\&accounts;\n</conf>#g" $conf/$date-contest-$j.xml
done
cd ..
