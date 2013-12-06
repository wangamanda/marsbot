#!/bin/bash

path=$( date +%s | sha256sum | base64 | head -c 6 )

mkdir $path

cd output
for i in *; do
  if [ ! -f $i.tar.bz2 -a ! -f $i ]
  then
    tar cvfj $i.tar.bz2 $i
  fi
done

cd ..

cd statistics
for i in *; do
  if [ ! -f statistics-$i.tar.bz2 -a ! -f $i ]
  then
    tar cvfj statistics-$i.tar.bz2 $i
  fi
done

cd ..

cd xmls
for i in *; do
  if [ ! -f MarsFileViewer-$i.tar.bz2 -a ! -f $i ]
  then
    tar cvfj MarsFileViewer-$i.tar.bz2 $i
  fi
done

cd ..

mv output/*.bz2 $path/
mv statistics/*.bz2 $path/
mv xmls/*.bz2 $path/

grep -v "password" /home/massim/trunk/massim/scripts/backup/*.log | grep -v DEBUG > /home/massim/www/webapps/massim/$path/rb02.log
