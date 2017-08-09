# THE PROJECT

This is a first version of the Wikimedia project [etytree](https://meta.wikimedia.org/wiki/Grants:IEG/A_graphical_and_interactive_etymology_dictionary_based_on_Wiktionary). The aim of the project is to visualize in an interactive web page the etymological tree (i.e., the etymology of a word in the form of a tree, with ancestors, cognate words, derived words, etc.) of any word in any language using data extracted from Wiktionary. 

If you have comments on the project please write on the [talk page](https://meta.wikimedia.org/wiki/Grants_talk:IEG/A_graphical_and_interactive_etymology_dictionary_based_on_Wiktionary) of the project.
 
This project has been inspired by my interest in etymology, in  open source collaborative projects and in interactive visualizations.

## Licence

The code is distributed under [MIT licence](https://opensource.org/licenses/MIT) and the data is distributed under [Creative Commons Attribution-ShareAlike 3.0](https://creativecommons.org/licenses/by-sa/3.0/)

## Viewing the Site

The site's html files are contained in [the repo root](https://github.com/esterpantaleo/etymology/tree/master/). The main page is [index.html](https://github.com/esterpantaleo/etymology/tree/master/index.html). To view the site you just need to navigate to the root of the repo.

## Setting up the Dev Tools

[GruntJS](https://gruntjs.com/) is currently being used as the task manager. It runs [JSLint](http://jshint.com/docs/) on the JavaScript files under [resources/js](https://github.com/esterpantaleo/etymology/tree/master/resources/js). In order to use these tools you will need to take the following steps:

1. Install [Node](https://nodejs.org/)
2. Run `npm install` from your the repo root.

Then you can run a lint of the JS files by running `grunt js` in your command line, from the repo root.

## Language data
Files contained in [resources/data](https://github.com/esterpantaleo/etymology/tree/master/resources/data) are imported from Wiktionary and updated when a new dump of the English Wiktionary is generated (updated on 07/22/2017). 

File etymology-only_languages.csv has been created from Wiktionary data with a lua module available [here](https://en.wiktionary.org/wiki/Wiktionary:Etymology-only_languages,_csv_format).

File iso-639-3.tab has been downloaded from [this link](http://www-01.sil.org/iso639-3/iso-639-3.tab) (the first line has been removed).

File list_of_languages.csv has been downloaded from [Wiktionary](https://en.wiktionary.org/wiki/Wiktionary:List_of_languages,_csv_format).

## The SPARQL ENDPOINT 
This code queries the [wmflabs etytree-virtuoso sparql endpoint](http://etytree-virtuoso.wmflabs.org/sparql) which I have set up and populated with data (RDF) produced with [dbnary_etymology](https://bitbucket.org/esterpantaleo/dbnary_etymology). The extracted data is kept in sync with Wiktionary each time a new dump is generated (we are a little behind now - data was extracted on 01/06/2017).

I have defined an ontology for etymologies [here](https://bitbucket.org/esterpantaleo/dbnary_etymology/src/078e0d9a2f274d63166a6bab1bf994587728277d/dbnary-ontology/src/main/resources/org/getalp/dbnary/dbnary_etymology.owl?at=master&fileviewer=file-view-default). In particular I have defined properties etymologicallyRelatedTo, etymologicallyDerivesFrom and etymologicallyEquivalentTo.

Besides etymological relationships data also contain POS-s, definitions, senses and more as extracted by [dbnary](https://bitbucket.org/serasset/dbnary). The ontology for dbnary is defined [here](https://bitbucket.org/esterpantaleo/dbnary_etymology/src/078e0d9a2f274d63166a6bab1bf994587728277d/dbnary-ontology/src/main/resources/org/getalp/dbnary/dbnary.owl?at=master&fileviewer=file-view-default).

An example query to the [sparql endpoint](http://etytree-virtuoso.wmflabs.org/sparql) follows:

    PREFIX eng: <http://etytree-virtuoso.wmflabs.org/dbnary/eng/>
    SELECT DISTINCT ?p ?o {
        eng:__ee_get ?p ?o
    }

Property http://www.w3.org/2000/01/rdf-schema#seeAlso is used to link to the Wiktionary page the etymological entry was extracted from.
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

## DATA EXTRACTION: dbnary_etymology
The RDF database of etymological relationships is periodically extracted when a new dump of the English Wiktionary is released. The code used to extract the data is [dbnary_etymology](https://bitbucket.org/esterpantaleo/dbnary_etymology).
### COMPILING THE CODE
[dbnary_etymology](https://bitbucket.org/esterpantaleo/dbnary_etymology) is a [Maven](https://maven.apache.org/download.cgi) project (use java 8 and maven3).
#### GENERATE DOCUMENTATION
    cd dbnary_etymology/extractor/
    mvn site
    mvn javadoc:jar
#### UPDATE PACKAGE
    cd dbnary_etymology
    mvn package
#### FULL DATA EXTRACTION - FOREIGN WORDS
    VERSION=20170601
    EXEC=~/dbnary_etymology/dbnary-extractor/target/dbnary-extractor-2.0e-SNAPSHOT-jar-with-dependencies.jar
    DUMP=/srv/datasets/dumps/$VERSION/enwiktionary-$VERSION-pages-articles.utf-16.xml
    FPAGE=0
    TPAGE=2000000
    LOG=extracts/lemon/en/$VERSION/enwkt-$VERSION_x_${FPAGE}_${TPAGE}.ttl.log
    OUT=extracts/lemon/en/$VERSION/enwkt-$VERSION_x_${FPAGE}_${TPAGE}.ttl
    ETY=extracts/lemon/en/$VERSION/enwkt-$VERSION_x_${FPAGE}_${TPAGE}.etymology.ttl
    PREFIX=http://etytree-virtuoso.wmflabs.org/dbnary
    rm ${OUT}
    java -Xmx24G -Dorg.slf4j.simpleLogger.log.org.getalp.dbnary=debug -cp ${EXEC} org.getalp.dbnary.cli.ExtractWiktionary -l en --prefix $PREFIX -x --frompage ${FPAGE} --topage ${TPAGE} -E ${ETY} -o ${OUT} ${DUMP} 3>&1 1>>${LOG} 2>&1

#### SINGLE ENTRY EXTRACTION - ENGLISH WORD
    WORD="door"
    java -Xmx24G -Dorg.slf4j.simpleLogger.log.org.getalp.dbnary.eng=debug -cp $EXEC org.getalp.dbnary.cli.GetExtractedSemnet -l en --prefix http://etytree-virtuoso.wmflabs.org/ --etymology $DUMP $WORD

#### SINGLE ENTRY EXTRACTION - FOREIGN WORD
    WORD="door"
    java -Xmx24G -Dorg.slf4j.simpleLogger.log.org.getalp.dbnary.eng=debug -cp $EXEC org.getalp.dbnary.cli.GetExtractedSemnet -x -l en --etymology testfile $DUMP $WORD

## ETYTREE TO DO

- [ ] To speed up queries edit ontology and add new properties to identify different etymological categories as described in [Wiktionary's Etymology help page](https://en.wiktionary.org/wiki/Wiktionary:Etymology):
* inherited word (template inherited)
* borrowed word (template borrowed)
* named from people
* developed from initialism
* surface analysis
* long detailed etymology - propose a new template??
* invented word/coined expression (coined by)
* back-formation (e.g.: burglar -> burgle, play the tamburine -> tambour, i.e. remove a morpheme, real or perceived) (template back-form)
* compound (template compound)
* phrase
* initialism
* acronym
* abbreviation
* clipping
* blend/portmanteau (template blend)
* calque/loan translation
* year template - propose a new template??
* cognates - I plan to ignore this
- [ ] parse glosses in templates
- [x] I would like to add a preferred direction to the graph, that goes from left to right following the evolution of a word from the past to the present. This would mean in terms of force field to add a magnetic field that orients arrows towards a preferred direction.

- [ ] Add zoom to tooltip, set zoom also in google chrome and other browsers.

- [ ] Add etymology controversies.

- [ ] Currently for some words the Virtuoso server doesn't return data because it reaches timeout. I want to try a different query like the following 
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

- [x] Click on a word and interrogate the server to get data about the word.

- [ ] Search words with space or with accent 

- [ ] Extract Reconstructed words.

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
