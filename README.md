# THE PROJECT

This is a first version of the Wikimedia project [etytree](https://meta.wikimedia.org/wiki/Grants:IEG/A_graphical_and_interactive_etymology_dictionary_based_on_Wiktionary). The aim of the project is to visualize in an interactive web page the etymological tree (i.e., the etymology of a word in the form of a tree, with ancestors, cognate words, derived words, etc.) of any word in any language using data extracted from Wiktionary. 

If you have comments on the project please write on the [talk page](https://meta.wikimedia.org/wiki/Grants_talk:IEG/A_graphical_and_interactive_etymology_dictionary_based_on_Wiktionary) of the project.
 
This project has been inspired by my interest in etymology, in  open source collaborative projects and in interactive visualizations.

##Licence

This code is distributed under Creative Commons Attribution-ShareAlike 3.0.

##Note
Files contained in [resources/data](https://github.com/esterpantaleo/etymology/tree/master/resources/data) are imported from Wiktionary and updated when a new dump of the English Wiktionary is generated.

##The SPARQL ENDPOINT 
This code queries the [wmflabs etytree-virtuoso sparql endpoint](http://etytree-virtuoso.wmflabs.org/sparql) which I have set up and populated with data (RDF) produced with [dbnary_etymology](https://bitbucket.org/esterpantaleo/dbnary_etymology). The extracted data is kept in sync with Wiktionary each time a new dump is generated (we are a little behind now - data was extracted on 12/20/2016).

I have defined an ontology for etymologies [here](https://bitbucket.org/esterpantaleo/dbnary_etymology/src/f120711cd96057f34880eab0b9abcae1f65dd49b/ontology/src/main/resources/org/getalp/dbnary/dbnary_etymology.owl?at=master&fileviewer=file-view-default). In particular I have defined properties etymologicallyDerivesFrom, derivesFrom and descendsFrom (and also etymologicallyEquivalentTo) as subproperties of etymologicallyRelatedTo. All these properties are transitive, etymologicallyEquivalentTo is reflexive. 

Besides etymological relationships data also contain POS-s, definitions, senses and more as extracted by [dbnary](https://bitbucket.org/serasset/dbnary). The ontology for dbnary is defined [here](https://bitbucket.org/esterpantaleo/dbnary_etymology/src/f120711cd96057f34880eab0b9abcae1f65dd49b/ontology/src/main/resources/org/getalp/dbnary/dbnary.owl?at=master&fileviewer=file-view-default).

An example query to the [sparql endpoint](http://etytree-virtuoso.wmflabs.org/sparql) follows:

    PREFIX eng: <http://kaiko.getalp.org/dbnary/eng/>
    SELECT DISTINCT ?p ?o {
        eng:__ee_get ?p ?o
    }

Property http://www.w3.org/2000/01/rdf-schema#seeAlso is used to link to the Wiktionary page the etymological entry was extracted from.
If you want to find all entries containing string "door"

    SELECT DISTINCT ?s {
        ?s rdfs:label ?label .
        ?label bif:contains "door" .
    }
If you want to find ancestors of "door":

    define input:inference "etymology_ontology"
    PREFIX dbetym: <http://kaiko.getalp.org/dbnaryetymology#>
    PREFIX eng: <http://kaiko.getalp.org/dbnary/eng/>

    SELECT DISTINCT ?o { 
         eng:__ee_1_door dbetym:etymologicallyRelatedTo{1,} ?o .
    }

## DATA EXTRACTION: dbnary_etymology
The RDF database of etymological relationships is periodically extracted when a new dump of the English Wiktionary is released. The code used to extract the data is [dbnary_etymology](https://bitbucket.org/esterpantaleo/dbnary_etymology).
###COMPILING THE CODE
[dbnary_etymology](https://bitbucket.org/esterpantaleo/dbnary_etymology) is a [Maven](https://maven.apache.org/download.cgi) project
#### GENERATE DOCUMENTATION
    cd dbnary_etymology/extractor/
    mvn site
    mvn javadoc:jar
#### UPDATE ONTOLOGY
    cd dbnary_etymology/ontology
    mvn install:install-file -Dfile=target/ontology-1.6-SNAPSHOT.jar -DgroupId=org.getalp.dbnary -DartifactId=ontology -Dversion=1.6-SNAPSHOT -Dpackaging=jar -DgeneratePom=true 
#### UPDATE PACKAGE
    cd dbnary_etymology/extractor
    mvn package
#### FULL DATA EXTRACTION
    VERSION=20161220
    EXEC=~/dbnary_etymology/extractor/target/dbnary-1.3e-SNAPSHOT-jar-with-dependencies.jar
    DUMP=/srv/datasets/dumps/$VERSION/enwiktionary-$VERSION-pages-articles.utf-16.xml
    FPAGE=0
    TPAGE=2000000
    LOG=extracts/lemon/en/$VERSION/enwkt-$VERSION_x_${FPAGE}_${TPAGE}.ttl.log
    OUT=extracts/lemon/en/$VERSION/enwkt-$VERSION_x_${FPAGE}_${TPAGE}.ttl
    ETY=extracts/lemon/en/$VERSION/enwkt-$VERSION_x_${FPAGE}_${TPAGE}.etymology.ttl
    rm ${OUT}
    java -Xmx24G -Dorg.slf4j.simpleLogger.log.org.getalp.dbnary=debug -cp ${EXEC} org.getalp.dbnary.cli.ExtractWiktionary -l en -x --frompage ${FPAGE} --topage ${TPAGE} -E ${ETY} -o ${OUT} ${DUMP} 3>&1 1>>${LOG} 2>&1
    java -Xmx24G -Dorg.slf4j.simpleLogger.log.org.getalp.dbnary=debug -cp ${EXEC} org.getalp.dbnary.cli.GetExtractedSemnet -x -l en --etymology ${DUMP} door
#### SINGLE ENTRY EXTRACTION
    WORD="door"
    java -Xmx24G -cp $EXEC org.getalp.dbnary.cli.GetExtractedSemnet -x -l en --etymology $DUMP $WORD

## ETYTREE TO DO

* I would like to add a preferred direction to the graph, that goes from left to right following the evolution of a word from the past to the present. This would mean in terms of force field to add a magnetic field that orients arrows towards a preferred direction.

* Add zoom to tooltip, set zoom also in google chrome and other browsers.

* Add etymology controversies.

* Currently for some words the Virtuoso server doesn't return data because it reaches timeout. I want to try a different query like the following 
```
    DEFINE input:inference "etymology_ontology"
    PREFIX dbetym: <http://kaiko.getalp.org/dbnaryetymology#>
    PREFIX owl: <http://www.w3.org/2002/07/owl#>
    PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>

    SELECT DISTINCT ?source ?p ?o ?cognate ?pcognate ?scognate
        { 
            ?source ?p ?o . 
            FILTER (?p in (dbetym:etymologicallyDerivesFrom, dbetym:descendsFrom, dbetym:derivesFrom,dbetym:etymologicallyEquivalentTo))
     #      {
     #          SELECT ?source
     #          {
     #              ?source dbetym:etymologicallyRelatedTo{1,}  <http://kaiko.getalp.org/dbnary/eng/__ee_1_water> . 
     #          }
     #      }
     #      UNION
            {
                SELECT ?source
                {
                    <http://kaiko.getalp.org/dbnary/eng/__ee_1_water> dbetym:etymologicallyRelatedTo{1,} ?source . 
                } 
            }
            OPTIONAL 
            {
                ?source dbetym:etymologicallyRelatedTo{1,} ?cognate . 
                ?scognate ?pcognate ?cognate . 
                FILTER (?pcognate in (dbetym:etymologicallyDerivesFrom, dbetym:descendsFrom, dbetym:derivesFrom,dbetym:etymologicallyEquivalentTo)) 
            }
        }
```

* Click on a word and interrogate the server to get data about the word.

* Search words with space or with accent 

* Extract Reconstructed words.

* Maybe consider Dialects:
    ```
    Module:da:Dialects ?
    Module:en:Dialects This module provides labels to {{alter}}, which is used in the Alternative forms section.
    Module:grc:Dialects This module translates from dialect codes to dialect names for templates such as {{alter}}. (e.g. aio -> link = 'Aeolic Greek', display = 'Aeolic')
    Module:he:Dialects
    Module:hy:Dialects ?
    Module:la:Dialects (e.g.: aug -> link = Late Latin#Late and post-classical Latin, display = post-Augustan)
```
* Maybe consider additional modules: 
```
    Module:families/data mapping language code -> language name  (e.g.: aav -> canonicalName = "Austro-Asiatic",otherNames = {"Austroasiatic"}
```