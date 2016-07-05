# The etytree project

This is a pre-alpha version of the "etytree" project. This project has been inspired by my interest in etymology, in open source collaborative projects and in interactive visualizations. 


The aim of the project is to visualize the etymological tree (i.e., the etymology of a word in the form of a tree, with ancestors, cognate words, derived words, etc.) of any word in any language using data available in Wiktionary in an interactive web page.


My next step will be to automatically extract data from Wiktionary (which would require implementing a DBnary extractor for etymological entries) and thus, potentially, visualize the etymology of any word in any language.


The code is open source and anyone can contribute.


# To do

* collapsing not working for mindmaps

* tooltip gets translated when zooming 

* in disambiguation pages, translation jumps?

* if node.position==left && link==abbreviation||borrowing||derived: node.year=node.parent.year -300

* fix timeline for nodes borrowing from abbreviations

* fix timeline axis for caffellatte

# Some useful reference material

http://stackoverflow.com/questions/29873947/hide-unrelated-parent-nodes-but-child-node-in-d3-js                                                                                    
http://stackoverflow.com/questions/19994357/multiple-tree-layouts-on-page-linking-between-them                                                                                     
http://stackoverflow.com/questions/21100058/using-d3-can-semantic-zoom-be-applied-to-a-radial-tree                                                                                 
