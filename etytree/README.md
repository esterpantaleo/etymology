# ETYTREE
## TO DO
* collapsing not working for mindmaps

* tooltip gets spatially translated when zooming

* in disambiguation pages, translation jumps?

* if node.position==left && link==abbreviation||borrowing||derived: node.year=node.parent.year -300

* fix timeline for nodes borrowing from abbreviations

* fix timeline axis for caffellatte

* add etymology controversies

* SPARQL queries: http://biohackathon.org/d3sparql/  d3dendrogram
http://togostanza.org/sparql

PREFIX up: <http://purl.uniprot.org/core/>
PREFIX tax: <http://purl.uniprot.org/taxonomy/>
SELECT ?root_name ?parent_name ?child_name
FROM <http://togogenome.org/graph/uniprot>
WHERE
{
  VALUES ?root_name { "Tardigrada" }
    ?root up:scientificName ?root_name .
      ?child rdfs:subClassOf+ ?root .
        ?child rdfs:subClassOf ?parent .
	  ?child up:scientificName ?child_name .
	    ?parent up:scientificName ?parent_name .
	    }



## Some useful reference material

http://stackoverflow.com/questions/29873947/hide-unrelated-parent-nodes-but-child-node-in-d3-js

http://stackoverflow.com/questions/19994357/multiple-tree-layouts-on-page-linking-between-them

http://stackoverflow.com/questions/21100058/using-d3-can-semantic-zoom-be-applied-to-a-radial-tree

http://bl.ocks.org/jdarling/2d4e84460d5f5df9c0ff

http://bl.ocks.org/jdarling/2503502

https://github.com/mbostock/d3/issues/213
