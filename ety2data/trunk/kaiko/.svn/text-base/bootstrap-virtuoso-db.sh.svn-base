#!/bin/bash

## Test if bash version 4 as we need associative arrays.
if [[ $BASH_VERSION != 4.* ]]
then
    echo "Need bash 4 version. Exiting."
    exit -1
fi

## Bootstrapping a virtuoso db.

PREFIX=/home/serasset/dev
if [[ ! $# -eq 0 ]]
then
    PREFIX=$1
fi

VIRTUOSOINITMPL=./virtuoso.ini.tmpl
BOOTSTRAPSQL=./bootstrap.sql

DBFOLDER=$PREFIX/virtuoso/db.bootstrap
DATASETDIR=$PREFIX/virtuoso/dataset
SERVERPORT=1112
SSLSERVERPORT=2112
WEBSERVERPORT=8899

DBNARYLATEST=/home/serasset/dev/wiktionary/extracts/lemon/latest

# Virtuoso installation variables
PATH=/sbin:/bin:/usr/sbin:/usr/bin:/opt/virtuoso-opensource/bin
DAEMON=/opt/virtuoso-opensource/bin/virtuoso-t
NAME=virtuoso

test -x $DAEMON || (echo "Could not find virtuoso-t bin" && exit 0)

## Converting language codes
declare -A iso3Lang
iso3Lang[bg]=bul
iso3Lang[de]=deu
iso3Lang[el]=ell
iso3Lang[en]=eng
iso3Lang[es]=spa
iso3Lang[fi]=fin
iso3Lang[fr]=fra
iso3Lang[it]=ita
iso3Lang[ja]=jpn
iso3Lang[pl]=pol
iso3Lang[pt]=por
iso3Lang[ru]=rus
iso3Lang[tr]=tur
iso3Lang[nl]=nld
iso3Lang[sh]=shr
iso3Lang[sv]=swe
iso3Lang[lt]=lit


if [ ! -d $DBNARYLATEST ]
then
	echo "Latest turtle data not available."
	exit -1
fi

if [ ! -d "$DBFOLDER" ] ; then
	mkdir -p "$DBFOLDER"
	sed "s|@@DBFOLDER@@|$DBFOLDER|g" < $VIRTUOSOINITMPL | \
	sed "s|@@DATASETDIR@@|$DATASETDIR|g" | \
	sed "s|@@SERVERPORT@@|$SERVERPORT|g" | \
	sed "s|@@SSLSERVERPORT@@|$SSLSERVERPORT|g" | \
	sed "s|@@WEBSERVERPORT@@|$WEBSERVERPORT|g" > "$DBFOLDER"/virtuoso.ini
    cp $BOOTSTRAPSQL "$DBFOLDER"
elif [[ -f virtuoso.db ]]; then
    echo "Virtuoso database file already exists, please clean up the db.bootstrap dir."
    exit -1
fi

if [ ! -d "$DATASETDIR" ]
then
	mkdir -p "$DATASETDIR"
fi


## Prepare the dataset directory
(
  shopt -s nullglob
  files=($DATASETDIR/*.ttl)
  if [[ "${#files[@]}" -gt 0 ]] ; then
    echo "Dataset already exists and is not empty, assuming its content is up to date."
  else
    echo "Copying and expanding latest extracts."
	cp $DBNARYLATEST/*.ttl.bz2 "$DATASETDIR"
	pushd "$DATASETDIR"
	bunzip2 ./*.ttl.bz2
  fi
)

## create the .graph files for all files in datasetdir
langRegex2='(..)_(.*)'
langRegex3='(...)_(.*)'
for f in $DATASETDIR/*.ttl
do
    if [[ $f =~ $langRegex2 ]]
    then
        lg2=${BASH_REMATCH[1]}
        lg3=${iso3Lang[$lg2]}
        echo "http://kaiko.getalp.org/dbnary/$lg3" > "$f.graph"
    elif [[ $f =~ $langRegex3 ]]
    then
        lg3=${BASH_REMATCH[1]}
        echo "http://kaiko.getalp.org/dbnary/$lg3" > "$f.graph"
    fi
done

## Launch virtuoso to create the new DB
echo "Launching daemon."
pushd "$DBFOLDER" || exit -1
$DAEMON -c $NAME +wait &
daemon_pid=$!
wait
### RECUPERER LE BON PID...

# exit 0

## connect to isql to load the different configurations
isql $SERVERPORT dba dba $BOOTSTRAPSQL


## This would be a good time to stop server and backup the empty database

## connect to isql and load all the data

isql $SERVERPORT dba dba <<END
ld_dir ('$DATASETDIR', '*.ttl', 'http://kaiko.getalp.org/dbnary');

-- do the following to see which files were registered to be added:
SELECT * FROM DB.DBA.LOAD_LIST;
-- if unsatisfied use:
-- delete from DB.DBA.LOAD_LIST;
rdf_loader_run();

-- do nothing too heavy while data is loading
checkpoint;
commit WORK;
checkpoint;
END

## (TODO: create the virtlabels for correct facetted browsing)

## index facetted browsing
isql $SERVERPORT dba dba <<END
sparql SELECT COUNT(*) WHERE { ?s ?p ?o } ;
sparql SELECT ?g COUNT(*) { GRAPH ?g {?s ?p ?o.} } GROUP BY ?g ORDER BY DESC 2;

-- Build Full Text Indexes by running the following commands using the Virtuoso isql program
RDF_OBJ_FT_RULE_ADD (null, null, 'All');
VT_INC_INDEX_DB_DBA_RDF_OBJ ();
-- Run the following procedure using the Virtuoso isql program to populate label lookup tables periodically and activate the Label text box of the Entity Label Lookup tab:
urilbl_ac_init_db();
-- Run the following procedure using the Virtuoso isql program to calculate the IRI ranks. Note this should be run periodically as the data grows to re-rank the IRIs.
s_rank();
END

echo "Now would be a good time to connect to http://localhost:$WEBSERVERPORT/ to change dav and dba passwords."

## Change the dba and sparql password

## Kill bootstrap server

echo -n "Waiting for daemon process to terminate: "
while kill -0 "$daemon_pid"
do
    echo -n "."
    sleep 1
done
echo 'Done.'

## change .ini file to production settings

## kill production server, move old db folder and substitute by new one, relaunch...
