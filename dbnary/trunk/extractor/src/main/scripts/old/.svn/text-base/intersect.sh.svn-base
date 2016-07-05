#!/bin/bash

LIST1=$1
LIST2=$2

for m in `cat $LIST1`
do
  x=`grep -w $m $LIST2 | head -1`
  if [ x$x == x ]
  then
    echo $m pas dans $LIST2
  fi
done