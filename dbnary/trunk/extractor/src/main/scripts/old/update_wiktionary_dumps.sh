#!/bin/bash

dumpserver="dumps.wikimedia.org"	# default default dump server
languages="fr"	# default languages
wdate=latest # default date
outfolder="$HOME/dev/wiktionary/latest"
foldersuffix="wiktionary"
fileprefix="wiktionary-"
filesuffix="-pages-articles.xml.bz2"
force=0

ICONV="iconv"
if [ -z `command -v ${ICONV} ]
then
	# assumes CLASSPATH contains dbnary.jar
	ICONV="java org.getalp.dbnary.cli.IConv"
fi

while getopts s:o:d:f o
do	case "$o" in
	s)	dumpserver="$OPTARG";;
	o)	outfolder="$OPTARG";;
	d)  wdate="$OPTARG";;
	f)	force=1;;
	[?])	echo >&2 "Usage: $0 [-s server] [-o outputfolder] [-d dumpdate] [-f] lg ..."
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

cd $outfolder

for lg in $languages
do
  wfile="${lg}${fileprefix}${wdate}${filesuffix}"
  url="http://${dumpserver}/${lg}${foldersuffix}/${wdate}/${wfile}"
  if [ -f ./${wfile} -a $force -eq 0 ]
  then
    echo Ignoring already existing ./${wfile} file.
    break
  elif [ -f ./${wfile} ]
  then
    rm -f ./${wfile}
  fi
  wget $url
  echo Uncompressing and converting the downloaded file...
  bzcat ./${wfile} | ${ICONV} -f UTF-8 -t UTF-16 > ${lg}wkt.xml
done