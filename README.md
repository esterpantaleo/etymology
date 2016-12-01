# THE PROJECT

This is a pre-alpha version of the "etytree" project. The aim of the project is to visualize in an interactive web page the etymological tree (i.e., the etymology of a word in the form of a tree, with ancestors, cognate words, derived words, etc.) of any word in any language using data available in Wiktionary. 

This project has been inspired by my interest in etymology, in  open source collaborative projects and in interactive visualizations.

##Licence

This code is distributed under Creative Commons Attribution-ShareAlike 3.0.

## TO DO

* collapsing not working for mindmaps

* tooltip gets spatially translated when zooming

* in disambiguation pages, translation jumps?

* if node.position==left && link==abbreviation||borrowing||derived: node.year=node.parent.year -300

* fix timeline for nodes borrowing from abbreviations

* fix timeline axis for caffellatte

* add etymology controversies

## Some useful reference material

http://stackoverflow.com/questions/29873947/hide-unrelated-parent-nodes-but-child-node-in-d3-js

http://stackoverflow.com/questions/19994357/multiple-tree-layouts-on-page-linking-between-them

http://stackoverflow.com/questions/21100058/using-d3-can-semantic-zoom-be-applied-to-a-radial-tree

http://bl.ocks.org/jdarling/2d4e84460d5f5df9c0ff

http://bl.ocks.org/jdarling/2503502

https://github.com/mbostock/d3/issues/213

a static hierarchical tree in wikipedia https://en.wikipedia.org/wiki/Hellenic_languages

## PARSING LANGUAGES USED IN THE ENGLISH WIKTIONARY

Going through the code used by Wiktionary to parse language information.
For this I'm going through links in page https://en.wiktionary.org/wiki/Category:Data_modules

Data relative to languages used in the Definition and in the Etymology section of the English Wiktionary are available at:

* https://en.wiktionary.org/wiki/Module:etymology_languages/data

* https://en.wiktionary.org/wiki/Module:languages/alldata which includes
     Module:languages/data2
    Module:languages/data3/a
    Module:languages/data3/b
    Module:languages/data3/c
    Module:languages/data3/d
    Module:languages/data3/e
    Module:languages/data3/f
    Module:languages/data3/g
    Module:languages/data3/h
    Module:languages/data3/i
    Module:languages/data3/j
    Module:languages/data3/k
    Module:languages/data3/l
    Module:languages/data3/m
    Module:languages/data3/n
    Module:languages/data3/o
    Module:languages/data3/p
    Module:languages/data3/q
    Module:languages/data3/r
    Module:languages/data3/s
    Module:languages/data3/t
    Module:languages/data3/u
    Module:languages/data3/v
    Module:languages/data3/w
    Module:languages/data3/x
    Module:languages/data3/y
    Module:languages/data3/z
    Module:languages/datax

* Module:wikimedia languages/data containes mappings of language codes used by Wikimedia and language codes used by Wiktionary. For example, for language "Alemannic German"  code "als" is used by Wikimedia and code "gsw" is used by Wiktionary.

* Dialects:
USEFUL? Module:da:Dialects ?
Module:en:Dialects This module provides labels to {{alter}}, which is used in the Alternative forms section.
USEFUL? Module:grc:Dialects This module translates from dialect codes to dialect names for templates such as {{alter}}. (e.g. aio -> link = 'Aeolic Greek', display = 'Aeolic')
Module:he:Dialects
USEFUL? Module:hy:Dialects ?
USEFUL? Module:la:Dialects (e.g.: aug -> link = Late Latin#Late and post-classical Latin, display = post-Augustan)
* additional modules: 
Module:families/data mapping language code -> language name  (e.g.: aav -> canonicalName = "Austro-Asiatic",otherNames = {"Austroasiatic"}
Module:list_of_languages,_csv_format mw.loadData("Module:languages/alldata") mw.loadData("Module:families/data")



==Two-letter codes==
These codes are taken from [[w:ISO 639-1|ISO 639-1]].
{{#invoke:list of languages|show|ids=1|two-letter code}}

==Three-letter codes==
These codes are taken from [[w:ISO 639-3|ISO 639-3]], a few from [[w:ISO 639-2|ISO 639-2]].

===a===
{{#invoke:list of languages|show|ids=1|three-letter code|a}}

===b===
{{#invoke:list of languages|show|ids=1|three-letter code|b}}

===c===
{{#invoke:list of languages|show|ids=1|three-letter code|c}}

===d===
{{#invoke:list of languages|show|ids=1|three-letter code|d}}

===e===
{{#invoke:list of languages|show|ids=1|three-letter code|e}}

===f===
{{#invoke:list of languages|show|ids=1|three-letter code|f}}

===g===
{{#invoke:list of languages|show|ids=1|three-letter code|g}}

===h===
{{#invoke:list of languages|show|ids=1|three-letter code|h}}

===i===
{{#invoke:list of languages|show|ids=1|three-letter code|i}}

===j===
{{#invoke:list of languages|show|ids=1|three-letter code|j}}

===k===
{{#invoke:list of languages|show|ids=1|three-letter code|k}}

===l===
{{#invoke:list of languages|show|ids=1|three-letter code|l}}

===m===
{{#invoke:list of languages|show|ids=1|three-letter code|m}}

===n===
{{#invoke:list of languages|show|ids=1|three-letter code|n}}

===o===
{{#invoke:list of languages|show|ids=1|three-letter code|o}}

===p===
{{#invoke:list of languages|show|ids=1|three-letter code|p}}

===q===
{{#invoke:list of languages|show|ids=1|three-letter code|q}}

===r===
{{#invoke:list of languages|show|ids=1|three-letter code|r}}

===s===
{{#invoke:list of languages|show|ids=1|three-letter code|s}}

===t===
{{#invoke:list of languages|show|ids=1|three-letter code|t}}

===u===
{{#invoke:list of languages|show|ids=1|three-letter code|u}}

===v===
{{#invoke:list of languages|show|ids=1|three-letter code|v}}

===w===
{{#invoke:list of languages|show|ids=1|three-letter code|w}}

===x===
{{#invoke:list of languages|show|ids=1|three-letter code|x}}

===y===
{{#invoke:list of languages|show|ids=1|three-letter code|y}}

===z===
{{#invoke:list of languages|show|ids=1|three-letter code|z}}

==Exceptional codes==
These codes are not part of any standard, but have been adopted within Wiktionary.
{{#invoke:list of languages|show|ids=1|exceptional}}

==Etymology language codes==
{{#invoke:list of languages|show_etym}}


when I run this on 



{{#invoke:list of languages, csv format|show}}





module sandbox
https://en.wiktionary.org/w/index.php?title=Module:sandbox&action=edit
then hit save changes
