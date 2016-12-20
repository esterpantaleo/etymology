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

* Dialects:
USEFUL? Module:da:Dialects ?
Module:en:Dialects This module provides labels to {{alter}}, which is used in the Alternative forms section.
USEFUL? Module:grc:Dialects This module translates from dialect codes to dialect names for templates such as {{alter}}. (e.g. aio -> link = 'Aeolic Greek', display = 'Aeolic')
Module:he:Dialects
USEFUL? Module:hy:Dialects ?
USEFUL? Module:la:Dialects (e.g.: aug -> link = Late Latin#Late and post-classical Latin, display = post-Augustan)
* additional modules: 
Module:families/data mapping language code -> language name  (e.g.: aav -> canonicalName = "Austro-Asiatic",otherNames = {"Austroasiatic"}

