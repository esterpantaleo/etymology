#!/bin/bash

CLASSPATH=$CLASSPATH:$HOME/.m2/repository/org/getalp/blexisma/blexisma-core/1.1-SNAPSHOT/blexisma-core-1.1-SNAPSHOT-jar-with-dependencies.jar
BLX=blx.sh


language="eng"	# default language
servlet="http://getalp.imag.fr/blexisma"
force=0


function process () {
  local bn=`basename $1`
  result=$vdir/$bn.v

  if [ -f $result -a $force -eq 0 ]
  then
    echo $result already exists. Use -f to force
  else 
    cat $1 | blx.sh sem $language $servlet > $result
  fi
}

while getopts s:l:f o
do	case "$o" in
	s)	language="$OPTARGS";;
	o)  servlet="$OPTARG";;
	f)	force=1;;
	[?])	echo >&2 "Usage: $0 [-s servlet-url] [-l language] [-f] text_directory vectors_directory"
		exit 1;;
	esac
done
shift `expr $OPTIND - 1`


if [ $# -ne 2 ]
then
  echo >&2 "Usage: $0 [-s servlet-url] [-l language] [-f] text_directory vectors_directory"
  exit -1
fi

tdir=$1
vdir=$2
shift 2

BLX_IS_INSTALLED=`which blx.sh`
if [ -z $BLX_IS_INSTALLED ]
then
  echo >&2 "the script blx.sh should be available in your PATH."
  exit -1
fi

if [ -f $tdir ]
then
  # Only one file to process
  process $tdir
  exit -1
elif [ ! -d $tdir ]
then
  echo >&2 "text directory does not exist"
  exit -1
fi

if [ ! -d $vdir ]
then
  mkdir -p $vdir
fi

for f in $tdir/*
do
 process $f
done
