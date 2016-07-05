#TO DO
* Check if there is a memory leak
* bug in FOREIGN: lavagga' utes : hu-suffix compound of 3 words
* write the java code for symultaneous extraction of bot FOREIGn and ENGLISH words
* add try for each word with a warning if there is a parsing error
* Automatically extract languages from Wikipedia

#SOME NOTES
* parse template {{sense: connection}}
* parse template {{PIE root|en|bleh_3}}

#DOCUMENTATION
* the grant proposal https://meta.wikimedia.org/wiki/Grants:IEG/A_graphical_and_interactive_etymology_dictionary_based_on_Wiktionary
* the talk page of the grant proposal https://meta.wikimedia.org/wiki/Grants_talk:IEG/A_graphical_and_interactive_etymology_dictionary_based_on_Wiktionary
* an extract of the RDF output in file /Volumes/TOSHIBA\ EXT/DBnary/extract_trance.rtf 

#QUESTIONS
* travel expense reports
* which kind of resources can I use to: store the data etc
* which kind of technical support can I get
* can I be employed at the same time as the grant: part time job or postdoctoral research contract?
* the editing tool

#TECHNICAL DETAILS
##VISUALIZATION
* for the visualization the expert is yurik
* link to an interesting visualization https://en.wikipedia.org/wiki/List_of_most_expensive_paintings
* https://www.mediawiki.org/wiki/Extension:Graph/Interactive_Graph_Tutorial
* https://www.mediawiki.org/wiki/Extension:Graph/Demo#Trees
* jsonconfig extension https://phabricator.wikimedia.org/T120452
##IMPLEMENTATION
* to implement on http://tools.wmflabs.org/ as a tools lab project, i.e. virtual machines and not production server, where I can create my own database 
##DATABASE
* database updated at any article update
* sparql query to generate data just in time (not stored - caching if needed)
##INTEGRATION OF WIKTIONARY
* https://www.wikidata.org/wiki/Wikidata:Wiktionary/Development/Proposals/2015-05
* https://www.wikidata.org/wiki/Wikidata:Wiktionary
* https://www.wikidata.org/wiki/Wikidata:Data_collaborators#Machine-readable_Wiktionary