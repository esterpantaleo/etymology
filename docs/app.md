<a name="module_APP"></a>

## APP

* [APP](#module_APP)
    * [~setNodes()](#module_APP..setNodes) ⇒ <code>Object</code>
    * [~renderAncestors()](#module_APP..renderAncestors)
    * [~renderDisambiguation()](#module_APP..renderDisambiguation)
    * [~renderDescendantsInLanguage()](#module_APP..renderDescendantsInLanguage)
    * [~renderDescendants()](#module_APP..renderDescendants)
    * [~showDescendants()](#module_APP..showDescendants)
    * [~showNotAvailable()](#module_APP..showNotAvailable)
    * [~showAncestors()](#module_APP..showAncestors)
    * [~showDisambiguation()](#module_APP..showDisambiguation)
    * [~show()](#module_APP..show)
    * [~init()](#module_APP..init)

<a name="module_APP..setNodes"></a>

### APP~setNodes() ⇒ <code>Object</code>
Given an object consisting of Etymology Entries, 
this function returns an object consisting of Nodes

**Kind**: inner method of [<code>APP</code>](#module_APP)  
**Returns**: <code>Object</code> - .<Node> a list of Nodes  

| Param | Type | Description |
| --- | --- | --- |
| .<EtymologyEntry> | <code>Object</code> | a list of Etymology Entries |

<a name="module_APP..renderAncestors"></a>

### APP~renderAncestors()
Render the Graph of Ancestors

**Kind**: inner method of [<code>APP</code>](#module_APP)  
**Params**: <code>Object</code>.<EtymologyEntry> a list of Etymology Entries  
<a name="module_APP..renderDisambiguation"></a>

### APP~renderDisambiguation()
Render the Disambiguation Graph

**Kind**: inner method of [<code>APP</code>](#module_APP)  
**Params**: <code>Object</code>.<EtymologyEntry> a list of Etymology Entries  
<a name="module_APP..renderDescendantsInLanguage"></a>

### APP~renderDescendantsInLanguage()
Render the Graph of Descendants in a specified language

**Kind**: inner method of [<code>APP</code>](#module_APP)  
**Params**: <code>Graph</code> a Graph with all descendants in all languages  
**Params**: <code>string</code> a language, e.g., "English"  
<a name="module_APP..renderDescendants"></a>

### APP~renderDescendants()
Render a dialog box with
an accordion, where each section of the accordion 
displays the graph of descendants in a specific language.

**Kind**: inner method of [<code>APP</code>](#module_APP)  
**Params**: <code>Node</code> the node whose descendants we are going to show  
**Params**: <code>Object</code> a list of Etymology Entries, descendants of Node  
<a name="module_APP..showDescendants"></a>

### APP~showDescendants()
Render the page that will contain the Graph of Descendants 
of a specified Node. It queries the database to get pos, gloss and links.

**Kind**: inner method of [<code>APP</code>](#module_APP)  
**Params**: <code>Node</code>  
<a name="module_APP..showNotAvailable"></a>

### APP~showNotAvailable()
Render a "not available" message.

**Kind**: inner method of [<code>APP</code>](#module_APP)  
**Params**: <code>Node</code>  
<a name="module_APP..showAncestors"></a>

### APP~showAncestors()
Render the page that will contain the Graph of Ancestors 
of an entry corresponding to a specified iri. It sequencially queries 
the database to get the set of ancestors.

**Kind**: inner method of [<code>APP</code>](#module_APP)  
**Params**: <code>iri</code>  
<a name="module_APP..showDisambiguation"></a>

### APP~showDisambiguation()
Render the page that will contain the Disambiguation Graph. 
It queries the database to get disambiguations.

**Kind**: inner method of [<code>APP</code>](#module_APP)  
**Params**: <code>string</code> response of a query  
<a name="module_APP..show"></a>

### APP~show()
Render the page that will contain the Etymology Graph 
of a specified lemma

**Kind**: inner method of [<code>APP</code>](#module_APP)  
**Params**: <code>string</code> e.g., "door"  
<a name="module_APP..init"></a>

### APP~init()
Initializes app

**Kind**: inner method of [<code>APP</code>](#module_APP)  
