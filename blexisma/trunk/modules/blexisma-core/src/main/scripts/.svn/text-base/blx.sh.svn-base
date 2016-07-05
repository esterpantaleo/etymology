#!/bin/bash

SERVLET_PREFIX="http://getalp.imag.fr/"
SERVLET_PATH="blexisma"
TEXT_LANGUAGE="eng"
HELP_REQUESTED="no"

function usage () {
  	echo >&2 "Usage: $0 command [-s servlet_prefix] [-b servlet_name] [-l language] command_parameter"
	echo >&2 "  Where command is one of:"
	echo >&2 "    dist      compute the angular distance between 2 conceptual vectors."
	echo >&2 "              args: v1 v2 name of the files containing the vectors."
	echo >&2 "    sem       compute the semantic vector associated to the text read from STDIN \(or from filename if -f option is given\)."
#	echo >&2 "              args:  [-s servlet_prefix] [-b servlet_name] [-l language] [-f filename]"
	echo >&2 "    guess     guess and display the language used in the given file (use - as filename to read input from stdin). "
	echo >&2 "    stat      provide several stats on the given vectorfile."
	echo >&2 "    extract   extracts the data from a videolist (as given by ghanni) and output all item's content in seperate text files in the given output directory."
	
	echo >&2 "    vect    return the semantic vector associated to the word read from STDIN \(or from filename if -f option is given\)."
	echo >&2 "            args:  [language] [servlet_name]"
	echo >&2 "    prox    return the 10 first prox of the word read from STDIN \(or from filename if -f option is given\)."
	echo >&2 "            args:  [language] [servlet_name]"
	echo >&2 "    vprox   return the 10 first prox of the vector file in parameter"
	echo >&2 "            args:  [language] [servlet_name] [vector_file] [nb_prox]"
	echo >&2 "    def     return the definition associated with the word read from STDIN \(or from filename if -f option is given\)."
	echo >&2 "            args:  [language] [servlet_name]"
	echo >&2 "    help [cmd] "
    echo >&2 "            display the usage of the given command."
	
	echo >&2 "Options common to all commands:"
	echo >&2 "        -s http://my.servlet.url/: use http://my.servlet.url/ as the servlet prefix \(default: $SERVLET_PREFIX\)."
	echo >&2 "        -b servletname: use servletname as the servlet path name \(default: $SERVLET_PATH\)."
	echo >&2 "        -l language: the language of the text \(default: $TEXT_LANGUAGE\)."
}

if [ $# -eq 0 ]
then
  usage
  exit 1
fi

cmd=$1
shift

if [ $cmd == help ]
then
	HELP_REQUESTED="yes"
	cmd=$1
	shift
fi

case "$cmd" in
	dist)  main=org.getalp.blexisma.cli.ComputeDistanceOfVectorFiles;;
	stat)  main=org.getalp.blexisma.cli.ComputeStatsOnVectorFile;;
	guess)  main=org.getalp.blexisma.cli.DetectLanguage;;
	sem)   main=org.getalp.blexisma.cli.servletinterogation.CLISemanticAnalysis;;
	extract)	main=org.getalp.blexisma.cli.VideoList2TextFoder;;
	vect)  main=org.getalp.blexisma.cli.servletinterogation.CLIVector;;
	prox)  main=org.getalp.blexisma.cli.servletinterogation.CLIProx;;
	vprox)  main=org.getalp.blexisma.cli.servletinterogation.CLIProxVector;;
	def)   main=org.getalp.blexisma.cli.servletinterogation.CLIDefinition;;
	*)     usage ; exit 1;;
esac

if [ $HELP_REQUESTED == "yes" ]
then
	java -Xmx2G -cp $CLASSPATH $main -h
else
	java -Xmx2G -cp $CLASSPATH $main $@
fi