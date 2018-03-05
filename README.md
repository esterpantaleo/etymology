# THE PROJECT

This is a first version of the Wikimedia project [etytree](https://meta.wikimedia.org/wiki/Grants:IEG/A_graphical_and_interactive_etymology_dictionary_based_on_Wiktionary). The aim of the project is to visualize in an interactive web page the etymological tree (i.e., the etymology of a word in the form of a tree, with ancestors, cognate words, derived words, etc.) of any word in any language using data extracted from Wiktionary. 

This project has been inspired by my interest in etymology, in  open source collaborative projects and in interactive visualizations.

If you have comments on the project please write on its [talk page](https://meta.wikimedia.org/wiki/Grants_talk:IEG/A_graphical_and_interactive_etymology_dictionary_based_on_Wiktionary).

## Description
Etytree uses data extracted from an XML dump of the English Wiktionary using an algorithm implemented in [dbnary_etymology](https://bitbucket.org/esterpantaleo/dbnary_etymology). The extracted data is kept in sync with Wiktionary each time a new dump is generated (the dump currently used dates back to September 28th, 2017). Data extracted with dbnary_etymology has been loaded into a Virtuoso DBMS which can be accessed at [wmflabs etytree-virtuoso sparql endpoint](http://etytree-virtuoso.wmflabs.org/sparql) and explored with a faceted browser. 

The list of languages and ISO codes can be found at [resources/data](https://github.com/esterpantaleo/etymology/tree/master/resources/data) and are imported from Wiktionary and periodically updated (the current files date back to September 22nd, 2017). File etymology-only_languages.csv has been created from Wiktionary data with a lua module available [here](https://en.wiktionary.org/wiki/Wiktionary:Etymology-only_languages,_csv_format). File iso-639-3.tab has been downloaded from [this link](http://www-01.sil.org/iso639-3/iso-639-3.tab) (the first line has been removed). File list_of_languages.csv has been downloaded from [Wiktionary](https://en.wiktionary.org/wiki/Wiktionary:List_of_languages,_csv_format). 

I have defined an ontology for etymologies [here](https://bitbucket.org/esterpantaleo/dbnary_etymology/src/078e0d9a2f274d63166a6bab1bf994587728277d/dbnary-ontology/src/main/resources/org/getalp/dbnary/dbnary_etymology.owl?at=master&fileviewer=file-view-default). In particular I have defined properties etymologicallyRelatedTo, etymologicallyDerivesFrom and etymologicallyEquivalentTo. This ontology needs improvements.

Property http://www.w3.org/2000/01/rdf-schema#seeAlso is used to link etymological entries to the Wiktionary pages they have been extracted from.

Besides etymological relationships, the database also contain POS-s, definitions, senses and more as extracted by [dbnary](https://bitbucket.org/serasset/dbnary). The ontology for dbnary is defined [here](https://bitbucket.org/esterpantaleo/dbnary_etymology/src/078e0d9a2f274d63166a6bab1bf994587728277d/dbnary-ontology/src/main/resources/org/getalp/dbnary/dbnary.owl?at=master&fileviewer=file-view-default).

## Licence

The code is distributed under [MIT licence](https://opensource.org/licenses/MIT) and the data is distributed under [Creative Commons Attribution-ShareAlike 3.0](https://creativecommons.org/licenses/by-sa/3.0/).

## Viewing the Site

The site's html files are contained in [the repo root](https://github.com/esterpantaleo/etymology/tree/master/). The main page is [index.html](https://github.com/esterpantaleo/etymology/tree/master/index.html). To view the site you just need to navigate to the root of the repo.

## Setting up the Dev Tools

[GruntJS](https://gruntjs.com/) is currently being used as the task manager. It runs [JSLint](http://jshint.com/docs/) on the JavaScript files under [resources/js](https://github.com/esterpantaleo/etymology/tree/master/resources/js). In order to use these tools you will need to take the following steps:

1. Install [Node](https://nodejs.org/)
2. Run `npm install` from the repo root.
3. Run `npm run build`.
4. Run `npm start`.

## Using the SPARQL ENDPOINT 
This code queries the [wmflabs etytree-virtuoso sparql endpoint](http://etytree-virtuoso.wmflabs.org/sparql) which I have set up and populated with data (RDF) produced with [dbnary_etymology](https://bitbucket.org/esterpantaleo/dbnary_etymology). 

An example query to the [sparql endpoint](http://etytree-virtuoso.wmflabs.org/sparql) follows:

    PREFIX eng: <http://etytree-virtuoso.wmflabs.org/dbnary/eng/>
    SELECT ?p ?o {
        eng:__ee_door ?p ?o
    }

If you want to find all entries containing string "door":

    SELECT DISTINCT ?s {
        ?s rdfs:label ?label .
        ?label bif:contains "door" .
    }
If you want to find ancestors of "door":

    PREFIX dbetym: <http://etytree-virtuoso.wmflabs.org//dbnaryetymology#>
    PREFIX eng: <http://etytree-virtuoso.wmflabs.org/dbnary/eng/>

    SELECT DISTINCT ?o { 
         eng:__ee_1_door dbetym:etymologicallyRelatedTo+ ?o .
    }

## etymology DOCUMENTATION
### INSTALL [jsdoc-to-markdown](https://www.npmjs.com/package/jsdoc-to-markdown)
You would to have sudo privileges
    
    npm install -g jsdoc-to-markdown

### GENERATE DOCUMENTATION

    mkdir ./docs
    cd ./src/js/
    jsdoc2md -f app.js datamodel.js languages.js etymologies.js liveTour.js graph.js > ../../docs/test.md

### GERRIT LINK
https://gerrit.wikimedia.org/r/#/admin/projects/labs/tools/etytree

## dbnary_etymology DOCUMENTATION
### EXTRACT THE DATA USING dbnary_etymology
The RDF database of etymological relationships is periodically extracted when a new dump of the English Wiktionary is released. The code used to extract the data is available at [dbnary_etymology](https://bitbucket.org/esterpantaleo/dbnary_etymology). 
#### COMPILE THE CODE
[dbnary_etymology](https://bitbucket.org/esterpantaleo/dbnary_etymology) is a [Maven](https://maven.apache.org/download.cgi) project (use java 8 and maven3).
#### GENERATE DOCUMENTATION
Let's assume you cloned the repository in your home:

    cd ~/dbnary_etymology/
    mvn site
    mvn javadoc:jar
#### PREPROCESS INPUT DATA
First you need an XML dump of English Wiktionary. Then you need to convert it into UTF-8 format (using [iconv](https://en.wikipedia.org/wiki/Iconv) for example). Assuming that the latest version is VERSION=20180220 and that the path to the dump is /public/dumps/public/enwiktionary/$VERSION/enwiktionary-$VERSION-pages-articles.xml.bz2 (if you have access to the Wikimedia Tool Labs):

    VERSION=20180220
    OUTPUT=/srv/datasets/dumps/$VERSION/                                                               #path to the output data folder
    INPUT=/public/dumps/public/enwiktionary/$VERSION/enwiktionary-$VERSION-pages-articles.xml.bz2      #path to the dump file
    
    mkdir ${OUTPUT}
    DUMP=${OUTPUT}/enwiktionary-$VERSION-pages-articles.utf-16.xml
    bzcat ${INPUT} | iconv -f UTF-8 -t UTF-16 > ${DUMP}                                                #takes approximately 7 minutes.

#### EXTRACT ENGLISH WORDS
With the following code you can extract data relative to English words:

    OUT_DIR=/srv/datasets/dbnary/${VERSION}/                                                               #output folder
    LOG_DIR=/srv/datasets/dbnary/$VERSION/logs/
    EXECUTABLE=~/dbnary_etymology/dbnary-extractor/target/dbnary-extractor-2.0e-SNAPSHOT-jar-with-dependencies.jar
    mkdir ${OUT_DIR}
    mkdir ${LOG_DIR}

    PREFIX=http://etytree-virtuoso.wmflabs.org/dbnary
    LOG_FILE=${LOG_DIR}/enwkt-${VERSION}.ttl.log
    OUT_FILE=${OUT_DIR}/enwkt-${VERSION}.ttl
    ETY_FILE=${OUT_DIR}/enwkt-${VERSION}.etymology.ttl
    rm ${LOG_FILE}
    java -Xmx24G -Dorg.slf4j.simpleLogger.log.org.getalp.dbnary=debug -cp ${EXECUTABLE} org.getalp.dbnary.cli.ExtractWiktionary -l en --prefix ${PREFIX} -E ${ETY_FILE} -o ${OUT_FILE} ${DUMP} test 3>&1 1>>${LOG_FILE} 2>&1   #This operation takes approximately 45 minutes
    #compress the output if needed
    gzip ${OUT_FILE}
    gzip ${ETY_FILE}
    #after inspecting the log file, I usually only keep the last few lines
    tail ${LOG_FILE} > ${LOG_DIR}/tmp
    mv ${LOG_DIR}/tmp  ${LOG_FILE}

#### EXTRACT FOREIGN WORDS
For memory reasons I only process a subset of the full data set at a time (from page 0 to page 1800000 - which takes approximately 100 minutes, from page 1899999 to page 3600000 which takes approximately 50 minutes, from page 3600000 to page 6000000 which takes approximately 100 minutes). Note that 24G are needed to process the data.

    fpage=0
    tpage=1800000
    LOG_FILE=${LOG_DIR}/enwkt-${VERSION}_x_${fpage}_${tpage}.ttl.log 
    OUT_FILE=${OUT_DIR}/enwkt-${VERSION}_x_${fpage}_${tpage}.ttl
    ETY_FILE=${OUT_DIR}/enwkt-${VERSION}_x_${fpage}_${tpage}.etymology.ttl
    rm ${LOG_FILE}
    java -Xmx24G -Dorg.slf4j.simpleLogger.log.org.getalp.dbnary=debug -cp ${EXECUTABLE} org.getalp.dbnary.cli.ExtractWiktionary -l en --prefix ${PREFIX} -x --frompage $fpage --topage $tpage -E ${ETY_FILE} -o ${OUT_FILE} ${DUMP} test 3>&1 1>>${LOG_FILE} 2>&1
    gzip ${OUT_FILE}
    gzip ${ETY_FILE}
    #after inspecting the log file, I usually only keep	the last few lines
    tail ${LOG_FILE} > ${LOG_DIR}/tmp
    mv ${LOG_DIR}/tmp  ${LOG_FILE}

    fpage=1800000
    tpage=3600000
    LOG_FILE=${LOG_DIR}/enwkt-${VERSION}_x_${fpage}_${tpage}.ttl.log
    OUT_FILE=${OUT_DIR}/enwkt-${VERSION}_x_${fpage}_${tpage}.ttl
    ETY_FILE=${OUT_DIR}/enwkt-${VERSION}_x_${fpage}_${tpage}.etymology.ttl
    rm ${LOG_FILE}
    java -Xmx24G -Dorg.slf4j.simpleLogger.log.org.getalp.dbnary=debug -cp ${EXECUTABLE} org.getalp.dbnary.cli.ExtractWiktionary -l en --prefix ${PREFIX} -x --frompage $fpage --topage $tpage -E ${ETY_FILE} -o ${OUT_FILE} ${DUMP} test 3>&1 1>>${LOG_FILE} 2>&1
    gzip ${OUT_FILE}
    gzip ${ETY_FILE}
    #after inspecting the log file, I usually only keep the last few lines
    tail ${LOG_FILE} > ${LOG_DIR}/tmp
    mv ${LOG_DIR}/tmp  ${LOG_FILE}

    fpage=3600000
    tpage=6000000
    LOG_FILE=${LOG_DIR}/enwkt-${VERSION}_x_${fpage}_${tpage}.ttl.log
    OUT_FILE=${OUT_DIR}/enwkt-${VERSION}_x_${fpage}_${tpage}.ttl    ETY_FILE=${OUT_DIR}/enwkt-${VERSION}_x_${fpage}_${tpage}.etymology.ttl
    rm ${LOG_FILE}
    java -Xmx24G -Dorg.slf4j.simpleLogger.log.org.getalp.dbnary=debug -cp ${EXECUTABLE} org.getalp.dbnary.cli.ExtractWiktionary -l en --prefix ${PREFIX} -x --frompage $fpage --topage $tpage -E ${ETY_FILE} -o ${OUT_FILE} ${DUMP} test 3>&1 1>>${LOG_FILE} 2>&1
    gzip ${OUT_FILE}
    gzip ${ETY_FILE}
    #after inspecting the log file, I usually only keep the last few lines
    tail ${LOG_FILE} > ${LOG_DIR}/tmp
    mv ${LOG_DIR}/tmp  ${LOG_FILE}

#### EXTRACT A SINGLE ENTRY - FOREIGN WORD
    WORD="door"
    java -Xmx24G -Dorg.slf4j.simpleLogger.log.org.getalp.dbnary.eng=debug -cp ${EXECUTABLE} org.getalp.dbnary.cli.GetExtractedSemnet -x -l en --etymology testfile ${DUMP} ${WORD}

### UPDATE DATABASE ON VIRTUOSO
#### Update ontology files 
For VERSION=20170920:

    cp ~/dbnary_etymology/dbnary-ontology/src/main/resources/org/getalp/dbnary/dbnary_etymology.owl  /srv/datasets/dbnary/${VERSION}/
    cp ~/dbnary_etymology/dbnary-ontology/src/main/resources/org/getalp/dbnary/dbnary.owl  /srv/datasets/dbnary/${VERSION}/

#### Update database
From isql execute the following steps (step A):

    SPARQL CLEAR GRAPH <http://etytree-virtuoso.wmflabs.org/dbnary>;
    SPARQL CLEAR GRAPH <http://etytree-virtuoso.wmflabs.org/dbnaryetymology>;
    ld_dir ('/srv/datasets/dbnary/20170920/', '*.ttl.gz','http://etytree-virtuoso.wmflabs.org/dbnary');
    ld_dir ('/srv/datasets/dbnary/20170920/', '*.owl','http://etytree-virtuoso.wmflabs.org/dbnaryetymology');
    -- do the following to see which files were registered to be added:
    SELECT * FROM DB.DBA.LOAD_LIST;
    -- if unsatisfied use:
    -- delete from DB.DBA.LOAD_LIST;
    rdf_loader_run();  ----- 1378390 msec. 
    -- do nothing too heavy while data is loading
    checkpoint;   ----- 50851 msec.
    commit WORK;  ----- 1417 msec.
    checkpoint;
    EXIT;

In case an error occurs:

    12:00:44 PL LOG:  File /srv/datasets/dbnary/20170920//enwkt-0_1800000.etymology.ttl.gz error 37000 SP029: TURTLE RDF loader, line 10636983: syntax error processed pending to here.
    12:06:09 PL LOG:  File /srv/datasets/dbnary/20170920//enwkt-1800000_3600000.etymology.ttl.gz error 37000 SP029: TURTLE RDF loader, line 4772623: syntax error processed pending to here.
    
edit files manually:

    zcat /srv/datasets/dbnary/20170920//enwkt-0_1800000.etymology.ttl.gz > /srv/datasets/dbnary/20170920//enwkt-0_1800000.etymology.ttl
    emacs -nw /srv/datasets/dbnary/20170920//enwkt-0_1800000.etymology.ttl.gz      #goto-line 10636983
    #change line
    gzip /srv/datasets/dbnary/20170920//enwkt-0_1800000.etymology.ttl

Go to step A above and repeat. Then run the following command from the terminal

    isql 1111 dba password /opt/virtuoso/db/bootstrap.sql

After dealing with errors relaunch the server.                              
From isql:

    sparql SELECT COUNT(*) WHERE { ?s ?p ?o } ;
    sparql SELECT ?g COUNT(*) { GRAPH ?g {?s ?p ?o.} } GROUP BY ?g ORDER BY DESC 2;
    -- Build Full Text Indexes by running the following commands using the Virtuoso isql program
    RDF_OBJ_FT_RULE_ADD (null, null, 'All');
    VT_INC_INDEX_DB_DBA_RDF_OBJ ();
    -- Run the following procedure using the Virtuoso isql program to populate label lookup tables periodically and activate the Label text box of the Entity Label Lookup tab:
    urilbl_ac_init_db();
    -- Run the following procedure using the Virtuoso isql program to calculate the IRI ranks. Note this should be run periodically as the data grows to re-rank the IRIs.
    s_rank();

#### CORS setup
The following link will help you set up CORS for Virtuoso: http://vos.openlinksw.com/owiki/wiki/VOS/VirtTipsAndTricksCORsEnableSPARQLURLs

#### Start and stop Virtuoso
To start:

    cd /opt/virtuoso/db
    virtuoso-t -f
To stop:

    cd /opt/virtuoso-opensource/bin
    isql 1111 dba password
    SQL> shutdown();
## ETYTREE TO DO

- [ ] Add qualifiers to links between nodes: inherited word (template inherited), borrowed word (template borrowed), named from people, developed from initialism, surface analysis, long detailed etymology (propose a new template?), invented word/coined expression (coined by), back-formation (e.g.: burglar -> burgle, play the tamburine -> tambour, i.e. remove a morpheme, real or perceived) (template back-form), compound (template compound), initialism, acronym, abbreviation, clipping, blend/portmanteau (template blend), calque/loan translation, year template (propose a new template?), cognates (I actually plan to ignore this).
- [ ] Parse glosses in templates
- [ ] Parse nested templates
- [ ] Add zoom to tooltip
- [ ] Add etymology controversies.
- [ ] Add alternative etymologies.
- [ ] Parse diacritics.
- [ ] Maybe consider Dialects:
```
    Module:da:Dialects ?
    Module:en:Dialects This module provides labels to {{alter}}, which is used in the Alternative forms section.
    Module:grc:Dialects This module translates from dialect codes to dialect names for templates such as {{alter}}. (e.g. aio -> link = 'Aeolic Greek', display = 'Aeolic')
    Module:he:Dialects
    Module:hy:Dialects ?
    Module:la:Dialects (e.g.: aug -> link = Late Latin#Late and post-classical Latin, display = post-Augustan)
```
- [ ] Maybe consider additional modules: 
```
    Module:families/data mapping language code -> language name  (e.g.: aav -> canonicalName = "Austro-Asiatic",otherNames = {"Austroasiatic"}
```
