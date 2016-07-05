#!/bin/bash

#Modify these parameters to fit your environment
BLEXISMA_CONFIG=./blexisma.conf
BLEXISMA_ROOT=/home/serasset/dev/blexisma/
SOCIETY=blexisma-training-society
VERSION=1.1-SNAPSHOT
SUFFIX=society.zip
COUGAAR_INSTALL_PATH=~/opt/local/Cougaar_12.4/
LAUNCHOPTS=""

compile=0
compileall=0


while getopts cCnb:s:v:p:i: o
do	case "$o" in
	c)	compile=1;;
	C)	compileall=1;;
	b)  BLEXISMA_ROOT="$OPTARG";;
	s)  SOCIETY="$OPTARG";;
	p)	BLEXISMA_CONFIG="$OPTARG";;
	i)	COUGAAR_INSTALL_PATH="$OPTARG";;
	v)  VERSION="$OPTARG";;
	n)  LAUNCHOPTS="$LAUNCHOPTS -n";;
	[?])	echo >&2 "Usage: $0 [-c | -C] [-b blexisma_root_folder] [-i cougar_install_path] [-p config_file] [-s society_name] [-v version_number]"
		exit 1;;
	esac
done
shift `expr $OPTIND - 1`

source ${BLEXISMA_CONFIG}

if [ $compileall == 1 ]
then
	pushd ${BLEXISMA_ROOT}
	mvn install
	popd
	unzip -o ${BLEXISMA_ROOT}/societies/${SOCIETY}/target/${SOCIETY}-${VERSION}-${SUFFIX}
elif [ $compile == 1 ]
then
	pushd ${BLEXISMA_ROOT}/societies/${SOCIETY}
	mvn install
	popd
	unzip -o ${BLEXISMA_ROOT}/societies/${SOCIETY}/target/${SOCIETY}-${VERSION}-${SUFFIX}
fi

cd ${SOCIETY}-${VERSION}

#Modify parameters
for soctmpl in configs/*.xml.tmpl
do
soc=${soctmpl%\.tmpl}
sed  '
s:@@sygfran.path@@:'"${sygfran_path}"':g
s:@@sygfran.data.path@@:'"${sygfran_data_path}"':g
s:@@syngfran.execdir@@:'"${syngfran_execdir}"':g
s:@@blexisma.vectorbase.path@@:'"${blexisma_vectorbase_path}"':g
s:@@blexisma.adj.path@@:'"${blexisma_adj_path}"':g
s:@@blexisma.adv.path@@:'"${blexisma_adv_path}"':g
s:@@blexisma.noun.path@@:'"${blexisma_noun_path}"':g
s:@@blexisma.verb.path@@:'"${blexisma_verb_path}"':g
s:@@blexisma.network.path@@:'"${blexisma_network_path}"':g
s:@@english-grammar-path@@:'"${stanford_english_grammar_path}"':g
s:@@german-grammar-path@@:'"${stanford_german_grammar_path}"':g
s:@@blexisma.training.save.frequency@@:'"${blexisma_training_save_frequency}"':g
s:@@blexisma.randomcv.coeffvar@@:'"${blexisma_randomcv_coeffvar}"':g
' < $soctmpl > $soc
done

echo The update-society.sh script does not execute the society anymore.
echo To launch the society file use the folowing commands:
echo "   export COUGAAR_INSTALL_PATH=\"$COUGAAR_INSTALL_PATH\""
echo "   cd" `pwd `
echo "   bin/launch [options]"
echo or use a society provided start script.   

