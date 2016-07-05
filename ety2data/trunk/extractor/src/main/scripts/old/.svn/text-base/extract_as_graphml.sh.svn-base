#!/bin/bash

wktextractjar="wiktionary-support-1.0-SNAPSHOT-jar-with-dependencies.jar"
wktextractprefix="$HOME/dev/blexisma/modules/wiktionary-support/target"
languages="fr"	# default languages
dumpfolder="$HOME/dev/wiktionary/latest"
outfolder="$HOME/dev/wiktionary/extracts"
stamp=`date +%Y%m%d%k%M`
format=graphml
force=0

while getopts j:p:d:o:s:f:F o
do	case "$o" in
	j)	wktextractjar="$OPTARG";;
	p)	wktextractprefix="$OPTARG";;
	d)	dumpfolder="$OPTARG";;
	o)  outfolder="$OPTARG";;
	s)	stamp="$OPTARG";;
	f)	format="$OPTARG";;
	F)	force=1;;
	[?])	echo >&2 "Usage: $0 [-j wiktionary-support-jar] [-p wiktionary-support-prefix] [-d dumpfolder] [-o outputfolder] [-s stamp] [-f raw] [-F] [lg ...]"
		exit 1;;
	esac
done
shift `expr $OPTIND - 1`

if [ $# -ne 0 ]
then
  languages=$@
fi

if [ ! -d $outfolder ]
then
  mkdir -p $outfolder
fi

if [ ! -d $dumpfolder ]
then
  echo Dump folder: $dumpfolder does not exist.
  exit 1
fi

if [ -d $outfolder/$stamp -a $force -eq 0 ]
then
  echo "Output folder ($outfolder/$stamp) already exist, use -f to force execution"
  exit 1
else
  mkdir -p $outfolder/$stamp
fi

if [ $format == "graphml" ]
then
    suffix=.grapml
else
    suffix=.raw
fi

cd $outfolder/$stamp

for lg in $languages
do
  dumpfile="$dumpfolder/${lg}wkt.xml"
  wfile="${lg}_extract${suffix}"
  if [ -f ./${wfile} ]
  then
    rm -f ./${wfile}
  fi
  cmd="java -Xmx2G -Dfile.encoding=UTF-8 -cp ${wktextractprefix}/${wktextractjar} org.getalp.dbnary.cli.ExtractWiktionary -l ${lg} -o ./${wfile} -f ${format} ${dumpfile}"
  echo Extracting data from ${lg} dump.
  echo $cmd
  $cmd
done
