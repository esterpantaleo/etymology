## Modules

<dl>
<dt><a href="#module_APP">APP</a></dt>
<dd></dd>
<dt><a href="#module_DATAMODEL">DATAMODEL</a></dt>
<dd></dd>
<dt><a href="#module_LANGUAGES">LANGUAGES</a></dt>
<dd></dd>
<dt><a href="#module_ETYMOLOGIES">ETYMOLOGIES</a></dt>
<dd></dd>
<dt><a href="#module_TOUR">TOUR</a></dt>
<dd></dd>
<dt><a href="#module_GRAPH">GRAPH</a></dt>
<dd></dd>
</dl>

<a name="module_APP"></a>

## APP
**Requires**: [<code>TOUR</code>](#module_TOUR), [<code>DATAMODEL</code>](#module_DATAMODEL), [<code>GRAPH</code>](#module_GRAPH)  

* [APP](#module_APP)
    * [~etymologyEntries2Nodes(etymologyEntries, langIsoCode)](#module_APP..etymologyEntries2Nodes) ⇒ <code>Object.&lt;Node&gt;</code>
    * [~etytreeShowAncestors(g)](#module_APP..etytreeShowAncestors)
    * [~etytreeNoAncestors(node)](#module_APP..etytreeNoAncestors)
    * [~etytreeShowDisambiguation(g)](#module_APP..etytreeShowDisambiguation)
    * [~etytreeParseDescendants(response)](#module_APP..etytreeParseDescendants)
    * [~etytreeLoadingDescendants(index)](#module_APP..etytreeLoadingDescendants)
    * [~etytreeShowDescendantsAccordion(index, g)](#module_APP..etytreeShowDescendantsAccordion)
    * [~etytreeDescendantsDialog(node)](#module_APP..etytreeDescendantsDialog)
    * [~etyreeUpdate(state)](#module_APP..etyreeUpdate)
    * [~etytreeTitle()](#module_APP..etytreeTitle)
    * [~etytreeDescription()](#module_APP..etytreeDescription)
    * [~etytreeHelpPopup()](#module_APP..etytreeHelpPopup)
    * [~etytreeSearchButton()](#module_APP..etytreeSearchButton)
    * [~etytreeLoading()](#module_APP..etytreeLoading)
    * [~etytreeNotFound()](#module_APP..etytreeNotFound)
    * [~etytreeHomePage()](#module_APP..etytreeHomePage)

<a name="module_APP..etymologyEntries2Nodes"></a>

### APP~etymologyEntries2Nodes(etymologyEntries, langIsoCode) ⇒ <code>Object.&lt;Node&gt;</code>
This function transforms an object consisting of EtymologyEntry-s,
into an object consisting of Nodes.
If the second argument is specified, i.e., a language,
only nodes in the specified language are returned.

**Kind**: inner method of [<code>APP</code>](#module_APP)  
**Returns**: <code>Object.&lt;Node&gt;</code> - a list of Nodes  

| Param | Type | Description |
| --- | --- | --- |
| etymologyEntries | <code>Object.&lt;EtymologyEntry&gt;</code> |  |
| langIsoCode | <code>String</code> | language iso code, e.g.: "eng" |

<a name="module_APP..etytreeShowAncestors"></a>

### APP~etytreeShowAncestors(g)
Renders the Graph of Ancestors.

**Kind**: inner method of [<code>APP</code>](#module_APP)  

| Param | Type |
| --- | --- |
| g | <code>Graph</code> | 

<a name="module_APP..etytreeNoAncestors"></a>

### APP~etytreeNoAncestors(node)
Renders a no etymology message for the given node

**Kind**: inner method of [<code>APP</code>](#module_APP)  

| Param | Type |
| --- | --- |
| node | <code>Node</code> | 

<a name="module_APP..etytreeShowDisambiguation"></a>

### APP~etytreeShowDisambiguation(g)
Renders the Disambiguation Page.

**Kind**: inner method of [<code>APP</code>](#module_APP)  

| Param | Type |
| --- | --- |
| g | <code>Graph</code> | 

<a name="module_APP..etytreeParseDescendants"></a>

### APP~etytreeParseDescendants(response)
Renders the Accordion with descendants of node.

**Kind**: inner method of [<code>APP</code>](#module_APP)  

| Param | Type |
| --- | --- |
| response | <code>Object</code> | 

<a name="module_APP..etytreeLoadingDescendants"></a>

### APP~etytreeLoadingDescendants(index)
Renders the loading message when loading the descendants graph.

**Kind**: inner method of [<code>APP</code>](#module_APP)  

| Param | Type |
| --- | --- |
| index | <code>Number</code> | 

<a name="module_APP..etytreeShowDescendantsAccordion"></a>

### APP~etytreeShowDescendantsAccordion(index, g)
Renders the Graph of Descendants in the accordion.

**Kind**: inner method of [<code>APP</code>](#module_APP)  

| Param | Type | Description |
| --- | --- | --- |
| index | <code>Number</code> | index indicating position in the accordion |
| g | <code>Graph</code> | a Graph with only descendants in one language |

<a name="module_APP..etytreeDescendantsDialog"></a>

### APP~etytreeDescendantsDialog(node)
Renders the page that will contain the Graph of Descendants
of a node; queries the database to get pos, gloss and links.

**Kind**: inner method of [<code>APP</code>](#module_APP)  

| Param | Type |
| --- | --- |
| node | <code>Node</code> | 

<a name="module_APP..etyreeUpdate"></a>

### APP~etyreeUpdate(state)
Renders the page corresponding to a search
of a specified word (label), in a specific language 
(if specified), with a specific value of ety (if specified).

**Kind**: inner method of [<code>APP</code>](#module_APP)  

| Param | Type | Description |
| --- | --- | --- |
| state | <code>Object</code> | e.g., { label: "door", lang: "eng", ety: "1" } |

<a name="module_APP..etytreeTitle"></a>

### APP~etytreeTitle()
Renders the title

**Kind**: inner method of [<code>APP</code>](#module_APP)  
<a name="module_APP..etytreeDescription"></a>

### APP~etytreeDescription()
Renders the description

**Kind**: inner method of [<code>APP</code>](#module_APP)  
<a name="module_APP..etytreeHelpPopup"></a>

### APP~etytreeHelpPopup()
Renders the help popup of the main page

**Kind**: inner method of [<code>APP</code>](#module_APP)  
<a name="module_APP..etytreeSearchButton"></a>

### APP~etytreeSearchButton()
Defines interactions with the search bar and the search button

**Kind**: inner method of [<code>APP</code>](#module_APP)  
<a name="module_APP..etytreeLoading"></a>

### APP~etytreeLoading()
Renders the loading message

**Kind**: inner method of [<code>APP</code>](#module_APP)  
<a name="module_APP..etytreeNotFound"></a>

### APP~etytreeNotFound()
Renders a not found page

**Kind**: inner method of [<code>APP</code>](#module_APP)  
<a name="module_APP..etytreeHomePage"></a>

### APP~etytreeHomePage()
Renders the home page

**Kind**: inner method of [<code>APP</code>](#module_APP)  
<a name="module_DATAMODEL"></a>

## DATAMODEL
**Requires**: [<code>LANGUAGES</code>](#module_LANGUAGES), [<code>ETYMOLOGIES</code>](#module_ETYMOLOGIES)  

* [DATAMODEL](#module_DATAMODEL)
    * [~EtymologyEntry](#module_DATAMODEL..EtymologyEntry)
        * [new EtymologyEntry(iri, label)](#new_module_DATAMODEL..EtymologyEntry_new)
    * [~DBNARY_ENG](#module_DATAMODEL..DBNARY_ENG)
    * [~onlyUnique()](#module_DATAMODEL..onlyUnique) ⇒ <code>Array</code>
    * [~encodeQuery(query)](#module_DATAMODEL..encodeQuery) ⇒ <code>String</code>
    * [~wiktionaryLink()](#module_DATAMODEL..wiktionaryLink) ⇒ <code>String</code>
    * [~parseLabel(label)](#module_DATAMODEL..parseLabel) ⇒ <code>String</code>
    * [~encodeLabel(label)](#module_DATAMODEL..encodeLabel) ⇒ <code>String</code>
    * [~wiktionaryLabel(iri)](#module_DATAMODEL..wiktionaryLabel) ⇒ <code>String</code>
    * [~dbnaryLabel(iri)](#module_DATAMODEL..dbnaryLabel) ⇒ <code>String</code>
    * [~dbnaryIso(iri)](#module_DATAMODEL..dbnaryIso) ⇒ <code>String</code>
    * [~dbnaryEty(iri)](#module_DATAMODEL..dbnaryEty) ⇒ <code>Number</code>
    * [~etytreeLink(label, lang, ety)](#module_DATAMODEL..etytreeLink)
    * [~assignNodes(etymologyEntries)](#module_DATAMODEL..assignNodes) ⇒ <code>Object.&lt;EtymologyEntry&gt;</code>
    * [~setEtymologyEntries(properties, ancestors)](#module_DATAMODEL..setEtymologyEntries) ⇒ <code>Object</code>
    * [~cleanEtymologyEntries(values)](#module_DATAMODEL..cleanEtymologyEntries) ⇒ <code>Object</code>
    * [~queryDisambiguation(label)](#module_DATAMODEL..queryDisambiguation) ⇒ <code>Observable</code>
    * [~parseDisambiguation(response)](#module_DATAMODEL..parseDisambiguation) ⇒ <code>Array.&lt;EtymologyEntry&gt;</code>
    * [~queryGloss(graph)](#module_DATAMODEL..queryGloss) ⇒ <code>Observable</code>
    * [~parseGloss(response)](#module_DATAMODEL..parseGloss) ⇒ <code>Object</code>
    * [~queryProperty(iris)](#module_DATAMODEL..queryProperty) ⇒ <code>Observable</code>
    * [~parseProperty(response)](#module_DATAMODEL..parseProperty) ⇒ <code>Array</code>
    * [~queryDisambiguationGloss(response)](#module_DATAMODEL..queryDisambiguationGloss) ⇒ <code>Observable</code>
    * [~findMoreAncestors(response)](#module_DATAMODEL..findMoreAncestors) ⇒ <code>Observable</code>
    * [~findAncestors(iri)](#module_DATAMODEL..findAncestors) ⇒ <code>Observable</code>
    * [~mergeAncestors(ancestors, moreAncestors)](#module_DATAMODEL..mergeAncestors) ⇒ <code>Array.&lt;String&gt;</code>
    * [~queryAncestors(iri, f)](#module_DATAMODEL..queryAncestors) ⇒ <code>Array.&lt;String&gt;</code>
    * [~parseAncestors(response)](#module_DATAMODEL..parseAncestors) ⇒ <code>Object</code>
    * [~queryDescendants(iris)](#module_DATAMODEL..queryDescendants) ⇒ <code>Observable</code>
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
| iri | <code>String</code> | The iri that identifies the Etymology Entry. |
| label | <code>String</code> | The label corresponding to the Etymology Entry. |

<a name="module_DATAMODEL..DBNARY_ENG"></a>

### DATAMODEL~DBNARY_ENG
**Kind**: inner constant of [<code>DATAMODEL</code>](#module_DATAMODEL)  
**Properties**

| Name | Type |
| --- | --- |
| DBNARY_ENG | <code>String</code> | 
| WIKT | <code>String</code> | 
| WIKT_RECONSTRUCTION | <code>String</code> | 

<a name="module_DATAMODEL..onlyUnique"></a>

### DATAMODEL~onlyUnique() ⇒ <code>Array</code>
**Kind**: inner method of [<code>DATAMODEL</code>](#module_DATAMODEL)  
<a name="module_DATAMODEL..encodeQuery"></a>

### DATAMODEL~encodeQuery(query) ⇒ <code>String</code>
Encodes a query into an url.

**Kind**: inner method of [<code>DATAMODEL</code>](#module_DATAMODEL)  
**Returns**: <code>String</code> - a url  

| Param | Type | Description |
| --- | --- | --- |
| query | <code>String</code> | a query |

<a name="module_DATAMODEL..wiktionaryLink"></a>

### DATAMODEL~wiktionaryLink() ⇒ <code>String</code>
Returns Wiktionary link to a lemma with a given label and language

**Kind**: inner method of [<code>DATAMODEL</code>](#module_DATAMODEL)  
**Parameter**: <code>String</code> label  
**Parameter**: <code>String</code> language  
<a name="module_DATAMODEL..parseLabel"></a>

### DATAMODEL~parseLabel(label) ⇒ <code>String</code>
Returns a label by replacing special characters.

**Kind**: inner method of [<code>DATAMODEL</code>](#module_DATAMODEL)  
**Returns**: <code>String</code> - a label  

| Param | Type | Description |
| --- | --- | --- |
| label | <code>String</code> | an encoded label |

<a name="module_DATAMODEL..encodeLabel"></a>

### DATAMODEL~encodeLabel(label) ⇒ <code>String</code>
Given a label, returns an encoded label.

**Kind**: inner method of [<code>DATAMODEL</code>](#module_DATAMODEL)  
**Returns**: <code>String</code> - an encoded label  

| Param | Type | Description |
| --- | --- | --- |
| label | <code>String</code> | a label |

<a name="module_DATAMODEL..wiktionaryLabel"></a>

### DATAMODEL~wiktionaryLabel(iri) ⇒ <code>String</code>
Given an iri, returns language + label.

**Kind**: inner method of [<code>DATAMODEL</code>](#module_DATAMODEL)  
**Returns**: <code>String</code> - a label  

| Param | Type | Description |
| --- | --- | --- |
| iri | <code>String</code> | an iri |

<a name="module_DATAMODEL..dbnaryLabel"></a>

### DATAMODEL~dbnaryLabel(iri) ⇒ <code>String</code>
**Kind**: inner method of [<code>DATAMODEL</code>](#module_DATAMODEL)  
**Returns**: <code>String</code> - a label  

| Param | Type | Description |
| --- | --- | --- |
| iri | <code>String</code> | an iri |

<a name="module_DATAMODEL..dbnaryIso"></a>

### DATAMODEL~dbnaryIso(iri) ⇒ <code>String</code>
**Kind**: inner method of [<code>DATAMODEL</code>](#module_DATAMODEL)  
**Returns**: <code>String</code> - an ISO code (e.g: "eng")  

| Param | Type | Description |
| --- | --- | --- |
| iri | <code>String</code> | an iri |

<a name="module_DATAMODEL..dbnaryEty"></a>

### DATAMODEL~dbnaryEty(iri) ⇒ <code>Number</code>
**Kind**: inner method of [<code>DATAMODEL</code>](#module_DATAMODEL)  
**Returns**: <code>Number</code> - an ety number (e.g: "1")  

| Param | Type | Description |
| --- | --- | --- |
| iri | <code>String</code> | an iri |

<a name="module_DATAMODEL..etytreeLink"></a>

### DATAMODEL~etytreeLink(label, lang, ety)
**Kind**: inner method of [<code>DATAMODEL</code>](#module_DATAMODEL)  

| Param | Type | Description |
| --- | --- | --- |
| label | <code>String</code> |  |
| lang | <code>String</code> | an ISO code (e.g: "eng") |
| ety | <code>String</code> | a number or a string with integers |

<a name="module_DATAMODEL..assignNodes"></a>

### DATAMODEL~assignNodes(etymologyEntries) ⇒ <code>Object.&lt;EtymologyEntry&gt;</code>
Used to merge EtymologyEntries into one Node
Assigns a node value (integer) to each EtymologyEntries
Different EtymologyEntries can be assigned the same node value
if they are etymologically equivalent or refer to the same word.
The final graph will merge EtymologyEntries that have the same node value
into the same node
(e.g.: if only ee_word and ee_n_word with n an integer belong to
the set of ancestors and descendants then merge them into one node)

**Kind**: inner method of [<code>DATAMODEL</code>](#module_DATAMODEL)  

| Param | Type | Description |
| --- | --- | --- |
| etymologyEntries | <code>Object</code> | containing a list of Etymology Entries |

<a name="module_DATAMODEL..setEtymologyEntries"></a>

### DATAMODEL~setEtymologyEntries(properties, ancestors) ⇒ <code>Object</code>
**Kind**: inner method of [<code>DATAMODEL</code>](#module_DATAMODEL)  
**Returns**: <code>Object</code> - with elements "values" and "edges"  

| Param | Type |
| --- | --- |
| properties | <code>Array.&lt;Object&gt;</code> | 
| ancestors | <code>Array.&lt;String&gt;</code> | 

<a name="module_DATAMODEL..cleanEtymologyEntries"></a>

### DATAMODEL~cleanEtymologyEntries(values) ⇒ <code>Object</code>
**Kind**: inner method of [<code>DATAMODEL</code>](#module_DATAMODEL)  
**Returns**: <code>Object</code> - with elements "values" and "edges"  

| Param | Type | Description |
| --- | --- | --- |
| values | <code>Array.&lt;EtymologyEntry&gt;</code> | an array of EtymologyEntry-s |

<a name="module_DATAMODEL..queryDisambiguation"></a>

### DATAMODEL~queryDisambiguation(label) ⇒ <code>Observable</code>
Given a string returns an RxJS observable
containing the parsed response of the server to
the disambiguationQuery.

**Kind**: inner method of [<code>DATAMODEL</code>](#module_DATAMODEL)  

| Param | Type |
| --- | --- |
| label | <code>String</code> | 

<a name="module_DATAMODEL..parseDisambiguation"></a>

### DATAMODEL~parseDisambiguation(response) ⇒ <code>Array.&lt;EtymologyEntry&gt;</code>
Parse response of [disambiguation query](disambiguationQuery) to the server.

**Kind**: inner method of [<code>DATAMODEL</code>](#module_DATAMODEL)  

| Param | Type |
| --- | --- |
| response | <code>String</code> | 

<a name="module_DATAMODEL..queryGloss"></a>

### DATAMODEL~queryGloss(graph) ⇒ <code>Observable</code>
**Kind**: inner method of [<code>DATAMODEL</code>](#module_DATAMODEL)  

| Param | Type |
| --- | --- |
| graph | <code>Graph</code> | 

<a name="module_DATAMODEL..parseGloss"></a>

### DATAMODEL~parseGloss(response) ⇒ <code>Object</code>
**Kind**: inner method of [<code>DATAMODEL</code>](#module_DATAMODEL)  
**Returns**: <code>Object</code> - with elements "posAndGloss" and "urlAndLabel"  

| Param | Type |
| --- | --- |
| response | <code>String</code> | 

<a name="module_DATAMODEL..queryProperty"></a>

### DATAMODEL~queryProperty(iris) ⇒ <code>Observable</code>
**Kind**: inner method of [<code>DATAMODEL</code>](#module_DATAMODEL)  

| Param | Type |
| --- | --- |
| iris | <code>Array.&lt;string&gt;</code> | 

<a name="module_DATAMODEL..parseProperty"></a>

### DATAMODEL~parseProperty(response) ⇒ <code>Array</code>
**Kind**: inner method of [<code>DATAMODEL</code>](#module_DATAMODEL)  
**Returns**: <code>Array</code> - of properties  

| Param | Type |
| --- | --- |
| response | <code>String</code> | 

<a name="module_DATAMODEL..queryDisambiguationGloss"></a>

### DATAMODEL~queryDisambiguationGloss(response) ⇒ <code>Observable</code>
Posts an XMLHttpRequest to get data about disambiguation nodes.

**Kind**: inner method of [<code>DATAMODEL</code>](#module_DATAMODEL)  

| Param | Type |
| --- | --- |
| response | <code>String</code> | 

<a name="module_DATAMODEL..findMoreAncestors"></a>

### DATAMODEL~findMoreAncestors(response) ⇒ <code>Observable</code>
Posts an XMLHttpRequest to more ancestors.

**Kind**: inner method of [<code>DATAMODEL</code>](#module_DATAMODEL)  

| Param | Type |
| --- | --- |
| response | <code>String</code> | 

<a name="module_DATAMODEL..findAncestors"></a>

### DATAMODEL~findAncestors(iri) ⇒ <code>Observable</code>
Posts an XMLHttpRequest to find ancestors.

**Kind**: inner method of [<code>DATAMODEL</code>](#module_DATAMODEL)  

| Param | Type |
| --- | --- |
| iri | <code>String</code> | 

<a name="module_DATAMODEL..mergeAncestors"></a>

### DATAMODEL~mergeAncestors(ancestors, moreAncestors) ⇒ <code>Array.&lt;String&gt;</code>
**Kind**: inner method of [<code>DATAMODEL</code>](#module_DATAMODEL)  

| Param | Type |
| --- | --- |
| ancestors | <code>Array.&lt;String&gt;</code> | 
| moreAncestors | <code>Array.&lt;String&gt;</code> | 

<a name="module_DATAMODEL..queryAncestors"></a>

### DATAMODEL~queryAncestors(iri, f) ⇒ <code>Array.&lt;String&gt;</code>
**Kind**: inner method of [<code>DATAMODEL</code>](#module_DATAMODEL)  
**Returns**: <code>Array.&lt;String&gt;</code> - an array of ancestors  

| Param | Type | Description |
| --- | --- | --- |
| iri | <code>String</code> |  |
| f | <code>function</code> | a function that renders graphs |

<a name="module_DATAMODEL..parseAncestors"></a>

### DATAMODEL~parseAncestors(response) ⇒ <code>Object</code>
**Kind**: inner method of [<code>DATAMODEL</code>](#module_DATAMODEL)  
**Returns**: <code>Object</code> - with elements "all" and "last"  

| Param | Type | Description |
| --- | --- | --- |
| response | <code>String</code> | a query response |

<a name="module_DATAMODEL..queryDescendants"></a>

### DATAMODEL~queryDescendants(iris) ⇒ <code>Observable</code>
**Kind**: inner method of [<code>DATAMODEL</code>](#module_DATAMODEL)  

| Param | Type |
| --- | --- |
| iris | <code>Array.&lt;String&gt;</code> | 

<a name="module_DATAMODEL..parseDescendants"></a>

### DATAMODEL~parseDescendants(response) ⇒ <code>Object.&lt;EtymologyEntry&gt;</code>
**Kind**: inner method of [<code>DATAMODEL</code>](#module_DATAMODEL)  
**Returns**: <code>Object.&lt;EtymologyEntry&gt;</code> - containing a list of Etymology Entries  

| Param | Type | Description |
| --- | --- | --- |
| response | <code>String</code> | a query response |

<a name="module_LANGUAGES"></a>

## LANGUAGES
<a name="module_LANGUAGES..load"></a>

### LANGUAGES~load() ⇒ <code>Map</code>
Loads etymology-only_languages.csv, list_of_languages.csv, iso-639-3.tab
located in the resources/data/ folder
these data is used to print on screen the language name 
when user hovers on a node (e.g.: eng -> "English")

**Kind**: inner method of [<code>LANGUAGES</code>](#module_LANGUAGES)  
**Returns**: <code>Map</code> - a map of languages  
<a name="module_ETYMOLOGIES"></a>

## ETYMOLOGIES

* [ETYMOLOGIES](#module_ETYMOLOGIES)
    * [~ANCESTORS_DEPTH](#module_ETYMOLOGIES..ANCESTORS_DEPTH)
    * [~getXMLHttpRequest(url)](#module_ETYMOLOGIES..getXMLHttpRequest) ⇒ <code>Observable</code>
    * [~postXMLHttpRequest(content)](#module_ETYMOLOGIES..postXMLHttpRequest) ⇒ <code>Observable</code>
    * [~postMoreAncestorsQuery()](#module_ETYMOLOGIES..postMoreAncestorsQuery) ⇒ <code>Observable</code>
    * [~disambiguationQuery(lemma)](#module_ETYMOLOGIES..disambiguationQuery) ⇒ <code>String</code>
    * [~glossQuery(iri)](#module_ETYMOLOGIES..glossQuery) ⇒ <code>String</code>
    * [~iterativeAncestorQuery(iri)](#module_ETYMOLOGIES..iterativeAncestorQuery) ⇒ <code>String</code>
    * [~descendantQuery(iri)](#module_ETYMOLOGIES..descendantQuery) ⇒ <code>String</code>
    * [~propertyQuery(iri)](#module_ETYMOLOGIES..propertyQuery) ⇒ <code>String</code>
    * [~unionQuery()](#module_ETYMOLOGIES..unionQuery) ⇒ <code>function</code>

<a name="module_ETYMOLOGIES..ANCESTORS_DEPTH"></a>

### ETYMOLOGIES~ANCESTORS_DEPTH
**Kind**: inner constant of [<code>ETYMOLOGIES</code>](#module_ETYMOLOGIES)  
**Properties**

| Name | Type | Description |
| --- | --- | --- |
| ANCESTORS_DEPTH | <code>Number</code> | depth of ancestorQuery |

<a name="module_ETYMOLOGIES..getXMLHttpRequest"></a>

### ETYMOLOGIES~getXMLHttpRequest(url) ⇒ <code>Observable</code>
Gets an XMLHttpRequest using RxJs

**Kind**: inner method of [<code>ETYMOLOGIES</code>](#module_ETYMOLOGIES)  

| Param | Type |
| --- | --- |
| url | <code>url</code> | 

<a name="module_ETYMOLOGIES..postXMLHttpRequest"></a>

### ETYMOLOGIES~postXMLHttpRequest(content) ⇒ <code>Observable</code>
Posts an XMLHttpRequest using RxJs

**Kind**: inner method of [<code>ETYMOLOGIES</code>](#module_ETYMOLOGIES)  

| Param | Type |
| --- | --- |
| content | <code>String</code> | 

<a name="module_ETYMOLOGIES..postMoreAncestorsQuery"></a>

### ETYMOLOGIES~postMoreAncestorsQuery() ⇒ <code>Observable</code>
Posts an array of XMLHttpRequest to the server using RxJs
each requesting ancestors of an iri

**Kind**: inner method of [<code>ETYMOLOGIES</code>](#module_ETYMOLOGIES)  

| Param | Type | Description |
| --- | --- | --- |
| .<string> | <code>Array</code> | iris - an array of iri-s |

<a name="module_ETYMOLOGIES..disambiguationQuery"></a>

### ETYMOLOGIES~disambiguationQuery(lemma) ⇒ <code>String</code>
Prints the disambiguation query into a string.
The generated response will consists of a table with three headers:
<ul><li>"iri": the iri of a resources with rdfs label the input string (e.g. http://etytree-virtuoso.wmflabs.org/dbnary/eng/__ee_link)</li>
<li>"et": a list of iris of resources that are described by the resource in "iri" (e.g. http://etytree-virtuoso.wmflabs.org/dbnary/eng/__ee_1_link,http://etytree-virtuoso.wmflabs.org/dbnary/eng/__ee_2_link,http://etytree-virtuoso.wmflabs.org/dbnary/eng/__ee_3_link)</li>
<li>"lemma": a string containing the rdfs label of the resource "iri"</li></ul>

**Kind**: inner method of [<code>ETYMOLOGIES</code>](#module_ETYMOLOGIES)  
**Returns**: <code>String</code> - a query string  

| Param | Type | Description |
| --- | --- | --- |
| lemma | <code>String</code> | a word e.g. "door" |

<a name="module_ETYMOLOGIES..glossQuery"></a>

### ETYMOLOGIES~glossQuery(iri) ⇒ <code>String</code>
Prints the query to get links, pos and gloss of an entry.

The generated response will consists of a table with five headers:
<ul><li>"iri"</li>
<li>"ee"</li>
<li>"pos"</li>: a string containing the rdfs label of the resource "iri"
<li>"gloss"</li>: a string containing glossesseparated by ";;;;""
<li>"links"</li>: a string containing links separated by ","
</ul>

**Kind**: inner method of [<code>ETYMOLOGIES</code>](#module_ETYMOLOGIES)  
**Returns**: <code>String</code> - a query string  

| Param | Type |
| --- | --- |
| iri | <code>String</code> | 

<a name="module_ETYMOLOGIES..iterativeAncestorQuery"></a>

### ETYMOLOGIES~iterativeAncestorQuery(iri) ⇒ <code>String</code>
Prints the query to get ancestors

**Kind**: inner method of [<code>ETYMOLOGIES</code>](#module_ETYMOLOGIES)  
**Returns**: <code>String</code> - a query string  

| Param | Type |
| --- | --- |
|  | <code>Number</code> | 
| iri | <code>String</code> | 

<a name="module_ETYMOLOGIES..descendantQuery"></a>

### ETYMOLOGIES~descendantQuery(iri) ⇒ <code>String</code>
Prints the query to get descendants

**Kind**: inner method of [<code>ETYMOLOGIES</code>](#module_ETYMOLOGIES)  
**Returns**: <code>String</code> - a query string  

| Param | Type |
| --- | --- |
| iri | <code>String</code> | 

<a name="module_ETYMOLOGIES..propertyQuery"></a>

### ETYMOLOGIES~propertyQuery(iri) ⇒ <code>String</code>
Prints the query to get properties about nodes

**Kind**: inner method of [<code>ETYMOLOGIES</code>](#module_ETYMOLOGIES)  
**Returns**: <code>String</code> - a query string  

| Param | Type |
| --- | --- |
| iri | <code>String</code> | 

<a name="module_ETYMOLOGIES..unionQuery"></a>

### ETYMOLOGIES~unionQuery() ⇒ <code>function</code>
Prints the union of an array of queries
one for each of the elements in input array iris

**Kind**: inner method of [<code>ETYMOLOGIES</code>](#module_ETYMOLOGIES)  
**Returns**: <code>function</code> - a function that takes as argument a string iri  

| Param | Type | Description |
| --- | --- | --- |
| .<string> | <code>Array</code> | iris - an array of strings |

<a name="module_TOUR"></a>

## TOUR
<a name="module_TOUR..Tour"></a>

### TOUR~Tour : <code>object</code>
A [Hopscotch](http://linkedin.github.io/hopscotch/) tour.

**Kind**: inner namespace of [<code>TOUR</code>](#module_TOUR)  
**Properties**

| Name | Type | Description |
| --- | --- | --- |
| id | <code>String</code> | Id of the tutorial. |
| steps | <code>Array.&lt;Object&gt;</code> | Array of steps in the tutorial. |
| steps.target | <code>String</code> | Target of the step. |
| steps.placement | <code>String</code> | Placement of the step. |
| steps.title | <code>String</code> | Title of the step. |
| steps.content | <code>String</code> | Description of the step. |

<a name="module_GRAPH"></a>

## GRAPH

* [GRAPH](#module_GRAPH)
    * [~d3](#module_GRAPH..d3)
        * [new d3()](#new_module_GRAPH..d3_new)
    * [~Dagre](#module_GRAPH..Dagre)
        * [new Dagre(type)](#new_module_GRAPH..Dagre_new)
    * [~Graph](#module_GRAPH..Graph) ⇐ <code>Dagre</code>
        * [new Graph(type, graph, width)](#new_module_GRAPH..Graph_new)
    * [~tooltip()](#module_GRAPH..tooltip)
    * [~render(element, id, width, height)](#module_GRAPH..render)
    * [~setLanguages()](#module_GRAPH..setLanguages)
    * [~setEdges(nCol)](#module_GRAPH..setEdges)

<a name="module_GRAPH..d3"></a>

### GRAPH~d3
**Kind**: inner class of [<code>GRAPH</code>](#module_GRAPH)  
**Requires**: <code>module:d3</code>, <code>module:dagre-d3</code>  
<a name="new_module_GRAPH..d3_new"></a>

#### new d3()
Class representing a Node.

<a name="module_GRAPH..Dagre"></a>

### GRAPH~Dagre
Class representing a Dagre (Directed acyclic graph).

**Kind**: inner class of [<code>GRAPH</code>](#module_GRAPH)  
<a name="new_module_GRAPH..Dagre_new"></a>

#### new Dagre(type)
Create a dagre.


| Param | Type | Description |
| --- | --- | --- |
| type | <code>String</code> | has value "TB" (top-bottom) or "LR" (left-right) |

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
| type | <code>String</code> | has value "TB" (top-bottom) or "LR" (left-right) |
| graph | <code>Object</code> | with elements "nodes" and "edges" |
| width | <code>Number</code> |  |

<a name="module_GRAPH..tooltip"></a>

### GRAPH~tooltip()
Prints tooltip.

**Kind**: inner method of [<code>GRAPH</code>](#module_GRAPH)  
<a name="module_GRAPH..render"></a>

### GRAPH~render(element, id, width, height)
Create an svg with the Dagre.
Render svg inside "element" and assign to it an "id".
Then fit to screen.

**Kind**: inner method of [<code>GRAPH</code>](#module_GRAPH)  

| Param | Type | Description |
| --- | --- | --- |
| element | <code>Object</code> | e.g., "#tree-overlay" |
| id | <code>String</code> |  |
| width | <code>Number</code> |  |
| height | <code>Number</code> |  |

<a name="module_GRAPH..setLanguages"></a>

### GRAPH~setLanguages()
Sets the value of the array this.languages.

**Kind**: inner method of [<code>GRAPH</code>](#module_GRAPH)  
<a name="module_GRAPH..setEdges"></a>

### GRAPH~setEdges(nCol)
Sets edges in the graph so nodes are displayed in
lines with nCol elements, and nodes in the same language
are next to each other.
Sets the value of this.languages if undefined

**Kind**: inner method of [<code>GRAPH</code>](#module_GRAPH)  

| Param | Type | Description |
| --- | --- | --- |
| nCol | <code>Number</code> | number of nodes that will be displayed in a line |

