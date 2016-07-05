#!/bin/bash

mullingprefix="$HOME/dev/mulling/test"
languages="fr"	# default languages
outfolder="$HOME/dev/wiktionary/extracts"
force=0

while getopts p:o:f o
do	case "$o" in
	p)	mullingprefix="$OPTARGS";;
	o)  outfolder="$OPTARG";;
	f)	force=1;;
	[?])	echo >&2 "Usage: $0 [-p mulling-prefix] [-o outputfolder] [-f] stamp1 stamp2 [lg ...]"
		exit 1;;
	esac
done
shift `expr $OPTIND - 1`

if [ $# -lt 2 ]
then
  echo >&2 "Usage: $0 [-p mulling-prefix] [-o outputfolder] [-f] stamp1 stamp2 [lg ...]"
  exit 1
fi
 
stamp1=$1
stamp2=$2
shift 2

if [ $# -ne 0 ]
then
  languages=$@
fi

if [ ! -d $outfolder ]
then
  echo extraction folder does not exist. Aborting.
  exit 1
fi


if [ ! -d $outfolder/$stamp1 -o ! -d $outfolder/$stamp2 ]
then
  echo Both stamp folder should exist
  exit 1
fi

cd $outfolder/
result=./${stamp1}_${stamp2}

if [ -d $result -a ! $force -eq 0 ]
then
  echo $result already exists. Use -f to force
  exit 1
fi

mkdir -p $result

for lg in $languages
do
  compl1file="$result/${lg}_lost_elements_${stamp1}_compl_${stamp2}.graphml"
  compl2file="$result/${lg}_additional_elements_${stamp2}_compl_${stamp1}.graphml"
  symdifffile="$result/${lg}_symdiff_${stamp1}_${stamp2}.graphml"
  if [ -f ./${wfile} ]
  then
    rm -f ./${wfile}
  fi
  cmd1="$mullingprefix/mixgraphs compl ${stamp1}/${lg}_extract.graphml ${stamp2}/${lg}_extract.graphml > $compl1file"
  cmd2="$mullingprefix/mixgraphs compl ${stamp2}/${lg}_extract.graphml ${stamp1}/${lg}_extract.graphml > $compl2file"
  cmd3="$mullingprefix/mixgraphs symdiff ${stamp1}/${lg}_extract.graphml ${stamp2}/${lg}_extract.graphml > $symdifffile"
  
  echo "$cmd1 " 
  echo "$cmd2 "
  echo "$cmd3 " 
  
 ## $cmd1 & PIDS="$! $PIDS"
 ##   
 ## $cmd2 & PIDS="$! $PIDS"
 ## 
 ## $cmd3 & PIDS="$! $PIDS"
  
done

##echo $PIDS
##wait $PIDS

