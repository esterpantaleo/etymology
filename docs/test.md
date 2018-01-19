## Modules

<dl>
<dt><a href="#module_APP">APP</a></dt>
<dd></dd>
<dt><a href="#module_DATAMODEL">DATAMODEL</a></dt>
<dd></dd>
<dt><a href="#module_DATA">DATA</a></dt>
<dd></dd>
<dt><a href="#module_GRAPH">GRAPH</a></dt>
<dd></dd>
</dl>

## Objects

<dl>
<dt><a href="#Etytree">Etytree</a> : <code>object</code></dt>
<dd></dd>
<dt><a href="#Tour">Tour</a> : <code>object</code></dt>
<dd><p>A <a href="http://linkedin.github.io/hopscotch/">Hopscotch</a> tour.</p>
</dd>
</dl>

<a name="module_APP"></a>

## APP
**Requires**: [<code>GRAPH</code>](#module_GRAPH), [<code>DATAMODEL</code>](#module_DATAMODEL)  

* [APP](#module_APP)
    * [~nodesFrom(etymologyEntries)](#module_APP..nodesFrom) ⇒ <code>Object.&lt;Node&gt;</code>
    * [~renderAncestorsGraphPage()](#module_APP..renderAncestorsGraphPage)
    * [~renderDisambiguationGraphPage()](#module_APP..renderDisambiguationGraphPage)
    * [~renderDescendantsGraphInLanguage()](#module_APP..renderDescendantsGraphInLanguage)
    * [~renderDescendantsAccordion()](#module_APP..renderDescendantsAccordion)
    * [~renderDescendantsDialog()](#module_APP..renderDescendantsDialog)
    * [~renderSearchPage()](#module_APP..renderSearchPage)
    * [~init()](#module_APP..init)

<a name="module_APP..nodesFrom"></a>

### APP~nodesFrom(etymologyEntries) ⇒ <code>Object.&lt;Node&gt;</code>
Given an object consisting of EtymologyEntry-s, 
this function returns an object consisting of Nodes

**Kind**: inner method of [<code>APP</code>](#module_APP)  
**Returns**: <code>Object.&lt;Node&gt;</code> - a list of Nodes  

| Param | Type |
| --- | --- |
| etymologyEntries | <code>Object.&lt;EtymologyEntry&gt;</code> | 

<a name="module_APP..renderAncestorsGraphPage"></a>

### APP~renderAncestorsGraphPage()
Render the Graph of Ancestors

**Kind**: inner method of [<code>APP</code>](#module_APP)  
**Params**: <code>Object.&lt;EtymologyEntry&gt;</code> etymologyEntries  
<a name="module_APP..renderDisambiguationGraphPage"></a>

### APP~renderDisambiguationGraphPage()
Render the Disambiguation Graph

**Kind**: inner method of [<code>APP</code>](#module_APP)  
**Params**: <code>Object.&lt;EtymologyEntry&gt;</code> etymologyEntries  
<a name="module_APP..renderDescendantsGraphInLanguage"></a>

### APP~renderDescendantsGraphInLanguage()
Render the Graph of Descendants in a specified language

**Kind**: inner method of [<code>APP</code>](#module_APP)  
**Params**: <code>Graph</code> g - a Graph with all descendants in all languages  
**Params**: <code>string</code> language - a language, e.g., "English"  
<a name="module_APP..renderDescendantsAccordion"></a>

### APP~renderDescendantsAccordion()
Render a dialog box with
an accordion, where each section of the accordion 
displays the graph of descendants in a specific language.

**Kind**: inner method of [<code>APP</code>](#module_APP)  
**Params**: <code>Node</code> node - the node whose descendants we are going to show  
**Params**: <code>Object.&lt;DATAMODEL.EtymologyEntry&gt;</code> etymologyEntries  
<a name="module_APP..renderDescendantsDialog"></a>

### APP~renderDescendantsDialog()
Render the page that will contain the Graph of Descendants 
of a specified Node. It queries the database to get pos, gloss and links.

**Kind**: inner method of [<code>APP</code>](#module_APP)  
**Params**: <code>Node</code> node  
<a name="module_APP..renderSearchPage"></a>

### APP~renderSearchPage()
Render the page that will contain the Etymology Graph 
of a specified lemma

**Kind**: inner method of [<code>APP</code>](#module_APP)  
**Params**: <code>string</code> lemma - e.g., "door"  
<a name="module_APP..init"></a>

### APP~init()
Initializes app

**Kind**: inner method of [<code>APP</code>](#module_APP)  
<a name="module_DATAMODEL"></a>

## DATAMODEL

* [DATAMODEL](#module_DATAMODEL)
    * [~EtymologyEntry](#module_DATAMODEL..EtymologyEntry)
        * [new EtymologyEntry(iri, label)](#new_module_DATAMODEL..EtymologyEntry_new)
    * [~urlFromQuery(query)](#module_DATAMODEL..urlFromQuery) ⇒ <code>string</code>
    * [~wiktionaryLabelOf(iri)](#module_DATAMODEL..wiktionaryLabelOf) ⇒ <code>string</code>
    * [~parseLabel(label)](#module_DATAMODEL..parseLabel) ⇒ <code>string</code>
    * [~encodeLabel(label)](#module_DATAMODEL..encodeLabel) ⇒ <code>string</code>
    * [~dbnaryLabelOf(iri)](#module_DATAMODEL..dbnaryLabelOf) ⇒ <code>string</code>
    * [~dbnaryIsoOf(iri)](#module_DATAMODEL..dbnaryIsoOf) ⇒ <code>string</code>
    * [~dbnaryEtyOf(iri)](#module_DATAMODEL..dbnaryEtyOf) ⇒ <code>number</code>
    * [~assignNodes(values)](#module_DATAMODEL..assignNodes) ⇒ <code>Object</code>
    * [~disambiguation(lemma)](#module_DATAMODEL..disambiguation) ⇒ <code>Observable</code>
    * [~glossQuery(iri)](#module_DATAMODEL..glossQuery) ⇒ <code>Observable</code>
    * [~propertyQueryScalar(iri)](#module_DATAMODEL..propertyQueryScalar) ⇒ <code>Observable</code>
    * [~propertyQuery(iris)](#module_DATAMODEL..propertyQuery) ⇒ <code>Observable</code>
    * [~dataQuery(iris, graph)](#module_DATAMODEL..dataQuery) ⇒ <code>Observable</code>
    * [~parseData(response)](#module_DATAMODEL..parseData) ⇒ <code>Object</code>
    * [~parseDisambiguation(response)](#module_DATAMODEL..parseDisambiguation) ⇒ <code>array.&lt;EtymologyEntry&gt;</code>
    * [~parseProperties(response)](#module_DATAMODEL..parseProperties) ⇒ <code>Array</code>
    * [~disambiguationQuery(response, f)](#module_DATAMODEL..disambiguationQuery) ⇒ <code>Observable</code>
    * [~findMoreAncestors(response)](#module_DATAMODEL..findMoreAncestors) ⇒ <code>Observable</code>
    * [~findAncestors(iri)](#module_DATAMODEL..findAncestors) ⇒ <code>Observable</code>
    * [~mergeAncestors(ancestors, moreAncestors)](#module_DATAMODEL..mergeAncestors) ⇒ <code>array.&lt;string&gt;</code>
    * [~ancestorsQuery(iri, f)](#module_DATAMODEL..ancestorsQuery) ⇒ <code>array.&lt;string&gt;</code>
    * [~parseAncestors(response)](#module_DATAMODEL..parseAncestors) ⇒ <code>Object</code>
    * [~setEtymologyEntries(properties, ancestors)](#module_DATAMODEL..setEtymologyEntries) ⇒ <code>Object</code>
    * [~cleanEtymologyEntries(values)](#module_DATAMODEL..cleanEtymologyEntries) ⇒ <code>Object</code>
    * [~descendantsQuery(node, f)](#module_DATAMODEL..descendantsQuery) ⇒ <code>Observable</code>
    * [~descendantsQueryScalar(an)](#module_DATAMODEL..descendantsQueryScalar) ⇒ <code>Observable</code>
    * [~parseDescendants(response)](#module_DATAMODEL..parseDescendants) ⇒ <code>Object.&lt;EtymologyEntry&gt;</code>

<a name="module_DATAMODEL..EtymologyEntry"></a>

### DATAMODEL~EtymologyEntry
Class representing an Etymology Entry.

**Kind**: inner class of [<code>DATAMODEL</code>](#module_DATAMODEL)  
<a name="new_module_DATAMODEL..EtymologyEntry_new"></a>

#### new EtymologyEntry(iri, label)
Create an Etymology Entry.


| Param | Type | Description |
| --- | --- | --- |
| iri | <code>string</code> | The iri that identifies the Etymology Entry. |
| label | <code>string</code> | The label corresponding to the Etymology Entry. |

<a name="module_DATAMODEL..urlFromQuery"></a>

### DATAMODEL~urlFromQuery(query) ⇒ <code>string</code>
Encodes a query into an url.

**Kind**: inner method of [<code>DATAMODEL</code>](#module_DATAMODEL)  
**Returns**: <code>string</code> - a url  

| Param | Type | Description |
| --- | --- | --- |
| query | <code>string</code> | a query |

<a name="module_DATAMODEL..wiktionaryLabelOf"></a>

### DATAMODEL~wiktionaryLabelOf(iri) ⇒ <code>string</code>
Given an iri, returns language + label.

**Kind**: inner method of [<code>DATAMODEL</code>](#module_DATAMODEL)  
**Returns**: <code>string</code> - a label  

| Param | Type | Description |
| --- | --- | --- |
| iri | <code>string</code> | an iri |

<a name="module_DATAMODEL..parseLabel"></a>

### DATAMODEL~parseLabel(label) ⇒ <code>string</code>
Returns a label by replacing special characters.

**Kind**: inner method of [<code>DATAMODEL</code>](#module_DATAMODEL)  
**Returns**: <code>string</code> - a label  

| Param | Type | Description |
| --- | --- | --- |
| label | <code>string</code> | an encoded label |

<a name="module_DATAMODEL..encodeLabel"></a>

### DATAMODEL~encodeLabel(label) ⇒ <code>string</code>
Given a label, returns an encoded label.

**Kind**: inner method of [<code>DATAMODEL</code>](#module_DATAMODEL)  
**Returns**: <code>string</code> - an encoded label  

| Param | Type | Description |
| --- | --- | --- |
| label | <code>string</code> | a label |

<a name="module_DATAMODEL..dbnaryLabelOf"></a>

### DATAMODEL~dbnaryLabelOf(iri) ⇒ <code>string</code>
**Kind**: inner method of [<code>DATAMODEL</code>](#module_DATAMODEL)  
**Returns**: <code>string</code> - a label  

| Param | Type | Description |
| --- | --- | --- |
| iri | <code>string</code> | an iri |

<a name="module_DATAMODEL..dbnaryIsoOf"></a>

### DATAMODEL~dbnaryIsoOf(iri) ⇒ <code>string</code>
**Kind**: inner method of [<code>DATAMODEL</code>](#module_DATAMODEL)  
**Returns**: <code>string</code> - an ISO code (e.g: "eng")  

| Param | Type | Description |
| --- | --- | --- |
| iri | <code>string</code> | an iri |

<a name="module_DATAMODEL..dbnaryEtyOf"></a>

### DATAMODEL~dbnaryEtyOf(iri) ⇒ <code>number</code>
**Kind**: inner method of [<code>DATAMODEL</code>](#module_DATAMODEL)  
**Returns**: <code>number</code> - an ety number (e.g: "1")  

| Param | Type | Description |
| --- | --- | --- |
| iri | <code>string</code> | an iri |

<a name="module_DATAMODEL..assignNodes"></a>

### DATAMODEL~assignNodes(values) ⇒ <code>Object</code>
Used to merge EtymologyEntries into one Node
Assigns a node value (integer) to each EtymologyEntries
Different EtymologyEntries can be assigned the same node value 
if they are etymologically equivalent or refer to the same word.
The final graph will merge EtymologyEntries that have the same node value
into the same node
(e.g.: if only ee_word and ee_n_word with n an integer belong to
the set of ancestors and descendants then merge them into one node)

**Kind**: inner method of [<code>DATAMODEL</code>](#module_DATAMODEL)  
**Returns**: <code>Object</code> - containing a list of Etymology Entries  

| Param | Type | Description |
| --- | --- | --- |
| values | <code>Object</code> | containing a list of Etymology Entries |

<a name="module_DATAMODEL..disambiguation"></a>

### DATAMODEL~disambiguation(lemma) ⇒ <code>Observable</code>
Given a string returns an RxJS observable
containing the parsed response of the server to
the disambiguationQuery.

**Kind**: inner method of [<code>DATAMODEL</code>](#module_DATAMODEL)  

| Param | Type |
| --- | --- |
| lemma | <code>string</code> | 

<a name="module_DATAMODEL..glossQuery"></a>

### DATAMODEL~glossQuery(iri) ⇒ <code>Observable</code>
Given an iri returns an RxJS observable
containing the parsed response of the server to
the glossQuery.

**Kind**: inner method of [<code>DATAMODEL</code>](#module_DATAMODEL)  

| Param | Type | Description |
| --- | --- | --- |
| iri | <code>string</code> | an iri |

<a name="module_DATAMODEL..propertyQueryScalar"></a>

### DATAMODEL~propertyQueryScalar(iri) ⇒ <code>Observable</code>
**Kind**: inner method of [<code>DATAMODEL</code>](#module_DATAMODEL)  

| Param | Type | Description |
| --- | --- | --- |
| iri | <code>string</code> | an iri |

<a name="module_DATAMODEL..propertyQuery"></a>

### DATAMODEL~propertyQuery(iris) ⇒ <code>Observable</code>
**Kind**: inner method of [<code>DATAMODEL</code>](#module_DATAMODEL)  

| Param | Type |
| --- | --- |
| iris | <code>Array.&lt;string&gt;</code> | 

<a name="module_DATAMODEL..dataQuery"></a>

### DATAMODEL~dataQuery(iris, graph) ⇒ <code>Observable</code>
**Kind**: inner method of [<code>DATAMODEL</code>](#module_DATAMODEL)  

| Param | Type |
| --- | --- |
| iris | <code>Array.&lt;string&gt;</code> | 
| graph | <code>Graph</code> | 

<a name="module_DATAMODEL..parseData"></a>

### DATAMODEL~parseData(response) ⇒ <code>Object</code>
**Kind**: inner method of [<code>DATAMODEL</code>](#module_DATAMODEL)  
**Returns**: <code>Object</code> - with elements "posAndGloss" and "urlAndLabel"  

| Param | Type |
| --- | --- |
| response | <code>string</code> | 

<a name="module_DATAMODEL..parseDisambiguation"></a>

### DATAMODEL~parseDisambiguation(response) ⇒ <code>array.&lt;EtymologyEntry&gt;</code>
Parse response of [disambiguation query](disambiguationQuery) to the server.

**Kind**: inner method of [<code>DATAMODEL</code>](#module_DATAMODEL)  

| Param | Type |
| --- | --- |
| response | <code>string</code> | 

<a name="module_DATAMODEL..parseProperties"></a>

### DATAMODEL~parseProperties(response) ⇒ <code>Array</code>
**Kind**: inner method of [<code>DATAMODEL</code>](#module_DATAMODEL)  
**Returns**: <code>Array</code> - of properties  

| Param | Type |
| --- | --- |
| response | <code>string</code> | 

<a name="module_DATAMODEL..disambiguationQuery"></a>

### DATAMODEL~disambiguationQuery(response, f) ⇒ <code>Observable</code>
Posts an XMLHttpRequest to get data about disambiguation nodes.

**Kind**: inner method of [<code>DATAMODEL</code>](#module_DATAMODEL)  

| Param | Type |
| --- | --- |
| response | <code>string</code> | 
| f | <code>function</code> | 

<a name="module_DATAMODEL..findMoreAncestors"></a>

### DATAMODEL~findMoreAncestors(response) ⇒ <code>Observable</code>
Posts an XMLHttpRequest to more ancestors.

**Kind**: inner method of [<code>DATAMODEL</code>](#module_DATAMODEL)  

| Param | Type |
| --- | --- |
| response | <code>string</code> | 

<a name="module_DATAMODEL..findAncestors"></a>

### DATAMODEL~findAncestors(iri) ⇒ <code>Observable</code>
Posts an XMLHttpRequest to find ancestors.

**Kind**: inner method of [<code>DATAMODEL</code>](#module_DATAMODEL)  

| Param | Type |
| --- | --- |
| iri | <code>string</code> | 

<a name="module_DATAMODEL..mergeAncestors"></a>

### DATAMODEL~mergeAncestors(ancestors, moreAncestors) ⇒ <code>array.&lt;string&gt;</code>
**Kind**: inner method of [<code>DATAMODEL</code>](#module_DATAMODEL)  
**Returns**: <code>array.&lt;string&gt;</code> - }  

| Param | Type |
| --- | --- |
| ancestors | <code>array.&lt;string&gt;</code> | 
| moreAncestors | <code>array.&lt;string&gt;</code> | 

<a name="module_DATAMODEL..ancestorsQuery"></a>

### DATAMODEL~ancestorsQuery(iri, f) ⇒ <code>array.&lt;string&gt;</code>
**Kind**: inner method of [<code>DATAMODEL</code>](#module_DATAMODEL)  
**Returns**: <code>array.&lt;string&gt;</code> - an array of ancestors  

| Param | Type | Description |
| --- | --- | --- |
| iri | <code>string</code> |  |
| f | <code>function</code> | a function |

<a name="module_DATAMODEL..parseAncestors"></a>

### DATAMODEL~parseAncestors(response) ⇒ <code>Object</code>
**Kind**: inner method of [<code>DATAMODEL</code>](#module_DATAMODEL)  
**Returns**: <code>Object</code> - with elements "all" and "last"  

| Param | Type | Description |
| --- | --- | --- |
| response | <code>string</code> | a query response |

<a name="module_DATAMODEL..setEtymologyEntries"></a>

### DATAMODEL~setEtymologyEntries(properties, ancestors) ⇒ <code>Object</code>
**Kind**: inner method of [<code>DATAMODEL</code>](#module_DATAMODEL)  
**Returns**: <code>Object</code> - with elements "values" and "edges"  

| Param | Type |
| --- | --- |
| properties | <code>array.&lt;Object&gt;</code> | 
| ancestors | <code>array.&lt;string&gt;</code> | 

<a name="module_DATAMODEL..cleanEtymologyEntries"></a>

### DATAMODEL~cleanEtymologyEntries(values) ⇒ <code>Object</code>
**Kind**: inner method of [<code>DATAMODEL</code>](#module_DATAMODEL)  
**Returns**: <code>Object</code> - with elements "values" and "edges"  

| Param | Type | Description |
| --- | --- | --- |
| values | <code>Array.&lt;EtymologyEntry&gt;</code> | an array of EtymologyEntry-s |

<a name="module_DATAMODEL..descendantsQuery"></a>

### DATAMODEL~descendantsQuery(node, f) ⇒ <code>Observable</code>
**Kind**: inner method of [<code>DATAMODEL</code>](#module_DATAMODEL)  

| Param | Type | Description |
| --- | --- | --- |
| node | <code>Node</code> |  |
| f | <code>function</code> | a callback |

<a name="module_DATAMODEL..descendantsQueryScalar"></a>

### DATAMODEL~descendantsQueryScalar(an) ⇒ <code>Observable</code>
**Kind**: inner method of [<code>DATAMODEL</code>](#module_DATAMODEL)  

| Param | Type | Description |
| --- | --- | --- |
| an | <code>string</code> | iri |

<a name="module_DATAMODEL..parseDescendants"></a>

### DATAMODEL~parseDescendants(response) ⇒ <code>Object.&lt;EtymologyEntry&gt;</code>
**Kind**: inner method of [<code>DATAMODEL</code>](#module_DATAMODEL)  
**Returns**: <code>Object.&lt;EtymologyEntry&gt;</code> - containing a list of Etymology Entries  

| Param | Type | Description |
| --- | --- | --- |
| response | <code>string</code> | a query response |

<a name="module_DATA"></a>

## DATA
<a name="module_DATA..init"></a>

### DATA~init()
Loads etymology-only_languages.csv, list_of_languages.csv, iso-639-3.tab
located in the resources/data/ folder
into etyBase.tree.langMap .

**Kind**: inner method of [<code>DATA</code>](#module_DATA)  
<a name="module_GRAPH"></a>

## GRAPH

* [GRAPH](#module_GRAPH)
    * _static_
        * [.Node](#module_GRAPH.Node)
            * [new Node(counter, etymologyEntry)](#new_module_GRAPH.Node_new)
    * _inner_
        * [~Dagre](#module_GRAPH..Dagre)
            * [new Dagre(type)](#new_module_GRAPH..Dagre_new)
        * [~Graph](#module_GRAPH..Graph) ⇐ <code>Dagre</code>
            * [new Graph(type, graph, width)](#new_module_GRAPH..Graph_new)
        * [~LanguageGraph](#module_GRAPH..LanguageGraph) ⇐ <code>Graph</code>
            * [new LanguageGraph(type, g, language)](#new_module_GRAPH..LanguageGraph_new)
        * [~tooltip()](#module_GRAPH..tooltip)
        * [~render(selector, id, width, height)](#module_GRAPH..render)
        * [~setLanguages()](#module_GRAPH..setLanguages)
        * [~setEdges(nCol)](#module_GRAPH..setEdges)

<a name="module_GRAPH.Node"></a>

### GRAPH.Node
Class representing a Node.

**Kind**: static class of [<code>GRAPH</code>](#module_GRAPH)  
<a name="new_module_GRAPH.Node_new"></a>

#### new Node(counter, etymologyEntry)
Creates a Node with id counter (if counter is not undefined).


| Param | Type |
| --- | --- |
| counter | <code>number</code> | 
| etymologyEntry | <code>EtymologyEntry</code> | 

<a name="module_GRAPH..Dagre"></a>

### GRAPH~Dagre
Class representing a Dagre (Directed acyclic graph).

**Kind**: inner class of [<code>GRAPH</code>](#module_GRAPH)  
<a name="new_module_GRAPH..Dagre_new"></a>

#### new Dagre(type)
Create a dagre.


| Param | Type | Description |
| --- | --- | --- |
| type | <code>string</code> | has value "TB" (top-bottom) or "LR" (left-right) |

<a name="module_GRAPH..Graph"></a>

### GRAPH~Graph ⇐ <code>Dagre</code>
Class representing a Graph.

**Kind**: inner class of [<code>GRAPH</code>](#module_GRAPH)  
**Extends**: <code>Dagre</code>  
<a name="new_module_GRAPH..Graph_new"></a>

#### new Graph(type, graph, width)
Create a graph.


| Param | Type | Description |
| --- | --- | --- |
| type | <code>string</code> | has value "TB" (top-bottom) or "LR" (left-right) |
| graph | <code>Object</code> | with elements "nodes" and "edges" |
| width | <code>number</code> |  |

<a name="module_GRAPH..LanguageGraph"></a>

### GRAPH~LanguageGraph ⇐ <code>Graph</code>
Class representing a Language Graph.

**Kind**: inner class of [<code>GRAPH</code>](#module_GRAPH)  
**Extends**: <code>Graph</code>  
<a name="new_module_GRAPH..LanguageGraph_new"></a>

#### new LanguageGraph(type, g, language)
Creates a language graph.


| Param | Type | Description |
| --- | --- | --- |
| type | <code>string</code> | has value "TB" (top-bottom) or "LR" (left-right) |
| g | <code>Graph</code> | the full Graph |
| language | <code>string</code> | the language (e.g., "English") |

<a name="module_GRAPH..tooltip"></a>

### GRAPH~tooltip()
Prints tooltip.

**Kind**: inner method of [<code>GRAPH</code>](#module_GRAPH)  
<a name="module_GRAPH..render"></a>

### GRAPH~render(selector, id, width, height)
Create an svg with the Dagre. 
Assign id to the svg element.
Render svg inside the element selected by "selector".
Then fit to screen.

**Kind**: inner method of [<code>GRAPH</code>](#module_GRAPH)  

| Param | Type |
| --- | --- |
| selector | <code>selector</code> | 
| id | <code>string</code> | 
| width | <code>number</code> | 
| height | <code>number</code> | 

<a name="module_GRAPH..setLanguages"></a>

### GRAPH~setLanguages()
Sets the value of the array this.languages.

**Kind**: inner method of [<code>GRAPH</code>](#module_GRAPH)  
<a name="module_GRAPH..setEdges"></a>

### GRAPH~setEdges(nCol)
Sets edges in the graph so that nodes are displayed in 
lines with nCol elements.
Sets the value of this.languages if undefined

**Kind**: inner method of [<code>GRAPH</code>](#module_GRAPH)  

| Param | Type | Description |
| --- | --- | --- |
| nCol | <code>number</code> | number of nodes that will be displayed in a line |

<a name="Etytree"></a>

## Etytree : <code>object</code>
**Kind**: global namespace  

* [Etytree](#Etytree) : <code>object</code>
    * [.etyBase.HELP](#Etytree.etyBase.HELP)
    * [.etyBase.MESSAGE](#Etytree.etyBase.MESSAGE)
    * [.etyBase.helpers](#Etytree.etyBase.helpers)
    * [.etyBase.config](#Etytree.etyBase.config)
    * [.create()](#Etytree.create)
    * [.bindModules()](#Etytree.bindModules)
    * [.init()](#Etytree.init)

<a name="Etytree.etyBase.HELP"></a>

### Etytree.etyBase.HELP
Messages in the help.

**Kind**: static property of [<code>Etytree</code>](#Etytree)  
<a name="Etytree.etyBase.MESSAGE"></a>

### Etytree.etyBase.MESSAGE
Messages.

**Kind**: static property of [<code>Etytree</code>](#Etytree)  
<a name="Etytree.etyBase.helpers"></a>

### Etytree.etyBase.helpers
Helper functions.

**Kind**: static property of [<code>Etytree</code>](#Etytree)  
<a name="Etytree.etyBase.config"></a>

### Etytree.etyBase.config
Sets up basic settings.

**Kind**: static property of [<code>Etytree</code>](#Etytree)  
<a name="Etytree.create"></a>

### Etytree.create()
Refer to this by [create](#Etytree.create).

**Kind**: static method of [<code>Etytree</code>](#Etytree)  
<a name="Etytree.bindModules"></a>

### Etytree.bindModules()
Binds modules.

**Kind**: static method of [<code>Etytree</code>](#Etytree)  
<a name="Etytree.init"></a>

### Etytree.init()
**Kind**: static method of [<code>Etytree</code>](#Etytree)  
<a name="Tour"></a>

## Tour : <code>object</code>
A [Hopscotch](http://linkedin.github.io/hopscotch/) tour.

**Kind**: global namespace  
**Properties**

| Name | Type | Description |
| --- | --- | --- |
| id | <code>string</code> | Id of the tutorial. |
| steps | <code>Array.&lt;Object&gt;</code> | Array of steps in the tutorial |
| steps.target | <code>string</code> | Target of the step |
| steps.placement | <code>string</code> | Placement of the step |
| steps.title | <code>string</code> | Title of the step |
| steps.content | <code>content</code> | Description of the step |


* [Tour](#Tour) : <code>object</code>
    * [.onEnd()](#Tour.onEnd)
    * [.onClose()](#Tour.onClose)
    * [.getCookie()](#Tour.getCookie)

<a name="Tour.onEnd"></a>

### Tour.onEnd()
Sets cookie on end (Tour.onEnd).

**Kind**: static method of [<code>Tour</code>](#Tour)  
<a name="Tour.onClose"></a>

### Tour.onClose()
Sets cookie on close (Tour.onClose).

**Kind**: static method of [<code>Tour</code>](#Tour)  
<a name="Tour.getCookie"></a>

### Tour.getCookie()
Gets cookie.

**Kind**: static method of [<code>Tour</code>](#Tour)  
