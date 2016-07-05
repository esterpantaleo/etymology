#!/bin/sh

DIR=/home/serasset/dev/wiktionary
LANGS="fr en de pt it fi ru el tr ja es bg pl nl sh"
TLANGS="fra,eng,por,deu,ell,rus,ita,fin,tur,jpn"
JAVA=/usr/java/jdk7/bin/java
VERS=1.3-SNAPSHOT
MIRROR=http://wikipedia.c3sl.ufpr.br/
#MIRROR=ftp://ftpmirror.your.org/pub/wikimedia/dumps/

$JAVA  -cp /home/serasset/.m2/repository/org/getalp/dbnary/$VERS/dbnary-${VERS}-jar-with-dependencies.jar org.getalp.dbnary.cli.UpdateAndExtractDumps -d $DIR -m lemon -s $MIRROR -k 1 -z --enable morpho $LANGS

# Updating latest extractions stats
$JAVA  -cp /home/serasset/.m2/repository/org/getalp/dbnary/$VERS/dbnary-${VERS}-jar-with-dependencies.jar org.getalp.dbnary.cli.UpdateLatestStatistics  -d $DIR/extracts -c $TLANGS

# Updating archived extraction stats
for lg in $LANGS
do
    $JAVA -cp /home/serasset/.m2/repository/org/getalp/dbnary/$VERS/dbnary-${VERS}-jar-with-dependencies.jar org.getalp.dbnary.cli.UpdateDiachronicStatistics -d /home/serasset/dev/wiktionary/extracts -c $TLANGS $lg
done

#cd /home/serasset/bin/parrot/
#$JAVA -jar parrot-jar-with-dependencies.jar -i http://kaiko.getalp.org/dbnary -o /home/serasset/dev/wiktionary/extracts/lemon/dbnary-doc/index.html -t html/dbnarytemplate.vm -s report/css/custom.css -b ./

rsync -avz /home/serasset/dev/wiktionary/extracts/ serasset@ken-web.imag.fr:/opt/www/kaiko/static/


