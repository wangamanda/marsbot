#!/bin/sh
cd output
for i in *; do
  if [ ! -f $i.tar.bz2 -a ! -f $i ]
  then
    tar cvfj $i.tar.bz2 $i
  fi
done
