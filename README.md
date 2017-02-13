# THE PROJECT

This is a first version of the "etytree" project. The aim of the project is to visualize in an interactive web page the etymological tree (i.e., the etymology of a word in the form of a tree, with ancestors, cognate words, derived words, etc.) of any word in any language using data extracted from Wiktionary. 

This project has been inspired by my interest in etymology, in  open source collaborative projects and in interactive visualizations.

##Licence

This code is distributed under Creative Commons Attribution-ShareAlike 3.0.

## TO DO

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
which works for the English word door.

* Click on a word and interrogate the server to get data about the word.

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
* Use 
```
    Template:defdate
```
## NOTE
Files contained in [resources/data](https://github.com/esterpantaleo/etymology/tree/master/resources/data) are imported from Wiktionary
## NOTES TO SELF REGARDING dbnary_etymology
### GENERATE DOCUMENTATION
    mvn site
    mvn javadoc:jar
### UPDATE ONTOLOGY
    cd ontology                                                                                            
    mvn install:install-file -Dfile=target/ontology-1.6-SNAPSHOT.jar -DgroupId=org.getalp.dbnary -DartifactId=ontology -Dversion=1.6-SNAPSHOT -Dpackaging=jar -DgeneratePom=true                                                                                                                          
###UPDATE PACKAGE
    mvn package
### DATA EXTRACTION
    VERSION=20161201
    EXEC=target/dbnary-1.3e-SNAPSHOT-jar-with-dependencies.jar
    DUMP=/home/getalp/serasset/dev/wiktionary/dumps/en/$VERSION/enwkt-$VERSION.xml
    FPAGE=0
    TPAGE=2000000
    LOG=extracts/lemon/en/$VERSION/enwkt-$VERSION_x_${FPAGE}_${TPAGE}.ttl.log
    OUT=extracts/lemon/en/$VERSION/enwkt-$VERSION_x_${FPAGE}_${TPAGE}.ttl
    ETY=extracts/lemon/en/$VERSION/enwkt-$VERSION_x_${FPAGE}_${TPAGE}.etymology.ttl 
    rm ${OUT}
    java -Xmx24G -Dorg.slf4j.simpleLogger.log.org.getalp.dbnary=debug -cp ${EXEC} org.getalp.dbnary.cli.ExtractWiktionary -l en -x --frompage ${FPAGE} --topage ${TPAGE} -E ${ETY} -o ${OUT} ${DUMP} 3>&1 1>>${LOG} 2>&1
    java -Xmx24G -Dorg.slf4j.simpleLogger.log.org.getalp.dbnary=debug -cp ${EXEC} org.getalp.dbnary.cli.GetExtractedSemnet -x -l en --etymology ${DUMP} door