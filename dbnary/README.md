#DOCUMENTATION
* the grant proposal https://meta.wikimedia.org/wiki/Grants:IEG/A_graphical_and_interactive_etymology_dictionary_based_on_Wiktionary
* the talk page of the grant proposal https://meta.wikimedia.org/wiki/Grants_talk:IEG/A_graphical_and_interactive_etymology_dictionary_based_on_Wiktionary
* an extract of the RDF output in file /Volumes/TOSHIBA\ EXT/DBnary/extract_trance.rtf

#TO DO
* select a subset of English words in wiktionary that are very popular; make sure they are represented correctly
* symultaneous extraction of both FOREIGN and ENGLISH words
* Automatically extract lua language modules:
 * https://en.wiktionary.org/wiki/Module:etymology_languages/data
 * https://en.wiktionary.org/wiki/Module:etymology
 * https://en.wiktionary.org/wiki/Module:etymology_languages/templates
 * https://en.wiktionary.org/wiki/Module:JSON_data
 * https://en.wiktionary.org/wiki/Module:languages
 * https://en.wiktionary.org/wiki/Wiktionary:List_of_languages/special
 * https://en.wiktionary.org/wiki/Wiktionary:List_of_families

 * https://en.wiktionary.org/wiki/Category:All_languages
 * https://en.wiktionary.org/wiki/Index:All_languages
 * https://en.wiktionary.org/wiki/Category:Language_indexes
 * https://en.wiktionary.org/wiki/Category:Data_modules
 * https://en.wiktionary.org/wiki/Module:labels/data
 * https://en.wiktionary.org/wiki/Module:labels/data/regional
 * https://en.wiktionary.org/wiki/Module:labels/data/subvarieties
 * https://en.wiktionary.org/wiki/Module:wikimedia_languages/data
 * https://en.wiktionary.org/wiki/Module:wikimedia_languages
 * https://en.wiktionary.org/wiki/Module:IPA/data
 * https://en.wiktionary.org/wiki/Module:list_of_languages,_csv_format
 * https://en.wiktionary.org/wiki/Module:grc:Dialects
 * https://en.wiktionary.org/wiki/Module:families/data
 * https://en.wiktionary.org/wiki/Module:families/by_name
 * https://en.wiktionary.org/wiki/Module:form_of/data
* Use lua code to parse templates and links
* parse template {{sense: connection}}
* parse template {{PIE root|en|bleh_3}}
* parse template {{rfv-etymology|lang=en}} ?
* parse template {{term/t|gem-pro|*fadēr}} ?
* check language LL. is correctly parsed
* parse Coined by [[w:J. R. R. Tolkien|J.R.R. Tolkien]] in 1937.
* parse {{calque|anti-|body|etyl lang=de|etyl term=Antikörper|lang=en}}.
* {{etyl|und}}.
* {{unk.}}
* parse {{rel-top|Cognates}} ??
* OR From {{etyl|fr|en}} {{term|assassin|lang=fr}} or {{etyl|it|en}} {{term|assassino|lang=it}}, from either {{etyl|ar|en}} {{term|حَشَّاشِين||hashish users|lang=ar}}, or {{term|أَسَاسِيُّون|lang=ar}}.
* bug in FOREIGN: lavagga' utes : hu-suffix compound of 3 words
* integrate this? https://www.knaw.nl/en/news/news/etymologiebank-online
* integrate this? https://en.wikipedia.org/wiki/List_of_medical_roots,_suffixes_and_prefixes
* Set emacs for java
* Check if there is a memory leak
* add try for each word with a warning if there is a parsing error

* some examples:
 * For an example of a medical etymology, the word "diabetes" is borrowed from the Greek word meaning "a siphon." The 2nd-century A.D. Greek physician, Aretus the Cappadocian, named the condition "diabetes." He explained that patients with it had polyuria and "passed water like a siphon."
 * The English word milk is clearly a cognate of German Milch, Dutch melk, Russianмолоко (moloko) and Croatian mlijeko.[4] On the other hand, French lait, Catalan llet, Italian latte, Romanian lapte, and Spanish leche (all meaning "milk") are less obviously cognates of Ancient Greek γάλακτος gálaktos (genitive singular of γάλα gála, "milk"), a relationship more evidently seen through the intermediate Latin lac "milk", as well as the English word lactic and other terms borrowed from Latin.
 * cognates may be semantic opposites. For instance, while the Hebrew word חוצפה chutzpah means "impudence," its Classical Arabic cognate حصافة ḥaṣāfah means "sound judgment."[5] English black and Polish biały, meaning white, are cognates with opposite meanings, both deriving from the PIE *bʰleg-, meaning, "to burn or shine."
 * brunch {{blend|breakfast|lunch|lang=en}}.
 * greed {{back-form|greedy}}
 * hobbit Coined by [[w:J. R. R. Tolkien|J.R.R. Tolkien]] in 1937. Ostensibly from {{etyl|ang|en}} {{term|holbytla||hole-builder|lang=ang}}.
 * chortle: Coined by [[w:Lewis Carroll|Lewis Carroll]] in ''[[w:Jabberwocky|Jabberwocky]]'', apparently as a {{blend|chuckle|snort|nocap=1|lang=en}}.
 * antibody: {{calque|anti-|body|etyl lang=de|etyl term=Antikörper|lang=en}}.
 * {{en-verb|head={{l|en|give}} {{l|en|up}}|gives up|giving up|gave up|given up}}. First attested in the United States in the 1920s period”. Further, one should state (briefly) when (and how) other senses came into use, if they differ from earlier meanings.
 * http://list25.com/25-interesting-and-somewhat-strange-word-origins/5/


#TECHNICAL DETAILS
##INTEGRATION OF WIKTIONARY
* https://www.wikidata.org/wiki/Wikidata:Wiktionary/Development/Proposals/2015-05
* https://www.wikidata.org/wiki/Wikidata:Wiktionary
* https://www.wikidata.org/wiki/Wikidata:Data_collaborators#Machine-readable_Wiktionary

#REPORT OF MERGING WITH Dbnary
* In file pom.txt
 * using a new ontology owl file that includes an Etymological Ontology - this ontology will be improved soon
 * added javadoc - I'm using "mvn site" and "mvn javadoc:jar" 

* In file AbstractWiktionaryExtractor.java:
 * functions removeXMLComments, removeBiblRef, stripParentheses could be merged into "removeContainedBetween()"
 * definiton of some Pattern Strings: debutOrfinDecomPatternString, biblRefPatternString, defOrExamplePatternString, definitionMarkupString
 * function stripParentheses now handles nested parentheses
 * TODO:not sure function removeBiblRef is working!!

* In file IWiktionaryDataHandler.java:
 * added functions extractEtymology, cleanEtymology, getEtymology, resetCurrentLexieCount

* In file Langtools.java:
 * edited function threeLettersCode

* In file LemonBasedRDFDataHandler.java:
 * added variables etymologyPos, etymologyString, currentEtymologyEntry, currentEtymologyNumber, currentEntryLanguage, currentPrefix, prefixes
 * added functions setCurrentLanguage, resetCurrentLexieCount, extractEtymology, cleanEtymology, getEtymology, computeEtymologyId, getPrefixe
 * modified function addPartOfSpeech with "etymologyPos.add(currentLexEntry);"
 * modified function getPrefix to return currentPrefix;
 * what is "private HashMap<String,Resource> languages" needed for?

* added file Pair.java:
 * this is needed to parse Etymology strings

* In file wiki/WikiPatterns:
 * added multipleBulletListPatternString to parse section: Descendents

* In file wiki/WikiTools.java:
 * added function locateEnclosedString locates a String	enclosed between any two symbols,
 e.g. {{ and }} or between [[ and ]] or	between	<ref and \ref> etc
 * added function removeTextWithin to remove text between two specified	locations, locations are specified in Pairs, i.e. a start and an end

* Added file eng/POE.java
  * POE is a part of etymology
  * The constructor takes as input the content of a template or of a wiktionary link. If given the content of a template (e.g., "m|en|door") it parses it using function WikiTool.parseArgs and outputs an object POE with POE.string="m|en|door", POE.part={"LEMMA"}, POE.args={("1","m"), "lang","eng"), ("word1", "door")}
  * If a template can handle:
    * {"COGNATE_WITH"} <-> {{cog|fr|orgue}} or {{cognate|fr|orgue}} or {{etymtwin|lang=en}}{{m|en|foo}} <-> cog, cognate, etymtwin
    * {"LEMMA"} <-> inh, inherited, der, derived, bor, borrowing, loan
    * {"LEMMA"} <->  compound, calque, blend (with word1, word2, word3 etc)
    * {"LEMMA"} <-> etycomp (e.g. {{etycomp|lang1=de|inf1=|case1=|word1=dumm|trans1=dumb|lang2=|inf2=|case2=|word2=Kopf|trans2=head}} )
    * {"LEMMA"} <-> vi-etym-sino
    * {"FROM", "LEMMA"} <-> abbreviation of
    * {"FROM", "LEMMA"} <-> back-form, named-after
    * {"LEMMA"} <-> m, mention, l, link
    * {"LEMMA"} <-> affix, confix, prefix, suffix
    * {"LEMMA"} <-> infix, circumfix, clipping, hu-prefix, hu-suffix
    * {"LANGUAGE"} <-> etyl
    * {"EMPTY"} <-> etystub, rfe
    * {"LEMMA"} <-> -er, -or
  * If a wiktionary link (e.g., fr:bon)
    * check that it does not contain character "|"
    * split ":" and remove anything that looks like "Image:..." "Category:..." "File:..."
    * Replace the language code with the normalized language code

* Added file eng/ArrayListPOE.java
  * to parse "COMPOUND_OF LEMMA AND LEMMA" -> "LEMMA"
  * ArrayList<Pair> match(Pattern p), public int getIndexOfCognateOr()

* In file eng/EnglishLangToCode.java:
 * removed those keys that are already present in file data3.txt (mostly three letters codes)
 * would like to have automatic extraction from the language module using
https://en.wiktionary.org/wiki/Module:JSON_data
or
https://en.wiktionary.org/wiki/Wiktionary:List_of_languages,_csv_format

* In file eng/EtymologyPatterns.java <br />

//aggiungere word boundary/b?????????????

originPatternString = "(FROM )?(LANGUAGE LEMMA |LEMMA )(COMMA |DOT |OR )"<br />

cognatePatternString = "(COGNATE_WITH )(?:(LANGUAGE LEMMA |LEMMA )+(COMMA |AND )?)+(DOT )?"<br />

compoundPatternString = "((COMPOUND_OF )(LANGUAGE )?(LEMMA )(PLUS |AND |WITH )(LANGUAGE )?(LEMMA ))"+<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;          "|"+<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;          "((LANGUAGE )?(LEMMA )(PLUS )(LANGUAGE )(LEMMA ))" <-> "LEMMA"<br />

"DOT"   <-> textAfterSupersededPatternString="(?:[Ss]uperseded|[Dd]isplaced( native)?|[Rr]eplaced|[Mm]ode(?:l)?led on)";<br />
??      <-> textEquivalentToPatternString="equivalent to\\s*\\{\\{[^\\}]+\\}\\}"; <br />
"PLUS"  <-> plusPatternString="\\+";  ;<br />
"DOT"   <-> dotPatternString="\\.|;";<br />
"COMMA" <-> commaPatternString=",";<br />
"FROM"  <-> fromPatternString="[Ff]rom|"+<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;         "[Bb]ack-formation (?:from)?|"+<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;         "[Aa]bbreviat(?:ion|ed)? (?:of|from)?|"+<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;         "[Cc]oined from|"+<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;         "[Bb]orrow(?:ing|ed)? (?:of|from)?|"+<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;         "[Cc]ontracted from|"+<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;         "[Aa]dopted from|"+<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;         "[Cc]alque(?: of)?|"+<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;         "[Ii]terative of|"+<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;         "[Ss]hort(?:hening|hen|hened)? (?:form )?(?:of|from)?|"+<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;         "[Tt]hrough|"+<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;         //"\\>"<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;         "[Aa]lteration of|"+<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;         "[Vv]ia|"+<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;         "[Dd]iminutive (?:form )?of|"+<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;          "[Uu]ltimately of|"+<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;          "[Vv]ariant of|"+<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;          "[Pp]et form of|"+<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;          "[Aa]phetic variation of|"+<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;          "[Dd]everbal of|"+<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;          "\\<"<br />

"COGNATE_WITH" <-> cognateWithPatternString="[Rr]elated(?: also)? to|"+<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;          "[Cc]ognate(?:s)? (?:include |with |to |including )?|"+<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;          "[Cc]ompare (?:also )?|"+<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;          "[Ww]hence (?:also )?|"+<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;          "(?:[Bb]elongs to the )?[Ss]ame family as |"+<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;          "[Mm]ore at |"+<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;          "[Aa]kin to |"+<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;          "[Ss]ee(?:n)? (?:also )?"<br />

"COMPOUND_OF" <-> compoundOfPatternString="[Cc]ompound(?:ed)? (?:of|from) |"+<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp          "[Mm]erg(?:ing |er )(?:of |with )?(?: earlier )?|"+<br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp          "[Uu]niverbation of ";<br />

? <-> uncertainPatternString="[Oo]rigin uncertain";<br />
"ABOVE" <-> abovePatternString="[Ss]ee above";<br />
"YEAR" <-> yearPatternString="(?:[Aa].\\s*?[Cc].?|[Bb].?\\s*[Cc].?)?\\s*\\d++\\s*(?:[Aa].?\\s*[Cc].?|[Bb].?\\s*[Cc].?|th century|\\{\\{C\\.E\\.\\}\\})?";<br />
"AND" <-> andPatternString="\\s+and\\s+";<br />
"OR" <-> orPatternString="[^a-zA-Z0-9]or[^a-zA-Z0-9]";<br />
"WITH" <-> withPatternString="[^a-zA-Z0-9]with[^a-zA-Z0-9]";<br />


#NOTES TO SELF
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
##QUESTIONS TO WIKIMEDIA TECH STAFF
* travel expense reports
* which kind of resources can I use to: store the data etc
* which kind of technical support can I get