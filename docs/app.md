<a name="module_APP"></a>

## APP

* [APP](#module_APP)
    * [~Given an object consisting of Etymology Entries, 
this function returns an object consisting of Nodes()](#module_APP..Given an object consisting of Etymology Entries, 
this function returns an object consisting of Nodes) ⇒ <code>Object</code>
    * [~Render the Graph of Ancestors()](#module_APP..Render the Graph of Ancestors)
    * [~Render the Disambiguation Graph()](#module_APP..Render the Disambiguation Graph)
    * [~Render the Graph of Descendants in a specified language()](#module_APP..Render the Graph of Descendants in a specified language)
    * [~Render the page that will contain the Graph of Descendants 
of a specified Node. It queries the database to get pos, gloss and links.()](#module_APP..Render the page that will contain the Graph of Descendants 
of a specified Node. It queries the database to get pos, gloss and links.)
    * [~Render a not available message.()](#module_APP..Render a not available message.)
    * [~Render the page that will contain the Graph of Ancestors 
of an entry corresponding to a specified iri. It sequencially queries 
the database to get the set of ancestors.()](#module_APP..Render the page that will contain the Graph of Ancestors 
of an entry corresponding to a specified iri. It sequencially queries 
the database to get the set of ancestors.)
    * [~Render the page that will contain the Disambiguation Graph. 
It queries the database to get disambiguations.()](#module_APP..Render the page that will contain the Disambiguation Graph. 
It queries the database to get disambiguations.)
    * [~Render the page that will contain the Etymology Graph 
of a specified lemma()](#module_APP..Render the page that will contain the Etymology Graph 
of a specified lemma)
    * [~Initializes app()](#module_APP..Initializes app)

<a name="module_APP..Given an object consisting of Etymology Entries, 
this function returns an object consisting of Nodes"></a>

### APP~Given an object consisting of Etymology Entries, 
this function returns an object consisting of Nodes() ⇒ <code>Object</code>
**Kind**: inner method of [<code>APP</code>](#module_APP)  
**Returns**: <code>Object</code> - .<Node> a list of Nodes  

| Param | Type | Description |
| --- | --- | --- |
| .<EtymologyEntry> | <code>Object</code> | a list of Etymology Entries |

<a name="module_APP..Render the Graph of Ancestors"></a>

### APP~Render the Graph of Ancestors()
**Kind**: inner method of [<code>APP</code>](#module_APP)  
**Params**: <code>Object</code>.<EtymologyEntry> a list of Etymology Entries  
<a name="module_APP..Render the Disambiguation Graph"></a>

### APP~Render the Disambiguation Graph()
**Kind**: inner method of [<code>APP</code>](#module_APP)  
**Params**: <code>Object</code>.<EtymologyEntry> a list of Etymology Entries  
<a name="module_APP..Render the Graph of Descendants in a specified language"></a>

### APP~Render the Graph of Descendants in a specified language()
**Kind**: inner method of [<code>APP</code>](#module_APP)  
**Params**: <code>Graph</code> a Graph with all descendants in all languages  
**Params**: <code>string</code> a language, e.g., "English"  
<a name="module_APP..Render the page that will contain the Graph of Descendants 
of a specified Node. It queries the database to get pos, gloss and links."></a>

### APP~Render the page that will contain the Graph of Descendants 
of a specified Node. It queries the database to get pos, gloss and links.()
**Kind**: inner method of [<code>APP</code>](#module_APP)  
**Params**: <code>Node</code>  
<a name="module_APP..Render a not available message."></a>

### APP~Render a not available message.()
**Kind**: inner method of [<code>APP</code>](#module_APP)  
**Params**: <code>Node</code>  
<a name="module_APP..Render the page that will contain the Graph of Ancestors 
of an entry corresponding to a specified iri. It sequencially queries 
the database to get the set of ancestors."></a>

### APP~Render the page that will contain the Graph of Ancestors 
of an entry corresponding to a specified iri. It sequencially queries 
the database to get the set of ancestors.()
**Kind**: inner method of [<code>APP</code>](#module_APP)  
**Params**: <code>iri</code>  
<a name="module_APP..Render the page that will contain the Disambiguation Graph. 
It queries the database to get disambiguations."></a>

### APP~Render the page that will contain the Disambiguation Graph. 
It queries the database to get disambiguations.()
**Kind**: inner method of [<code>APP</code>](#module_APP)  
**Params**: <code>string</code> response of a query  
<a name="module_APP..Render the page that will contain the Etymology Graph 
of a specified lemma"></a>

### APP~Render the page that will contain the Etymology Graph 
of a specified lemma()
**Kind**: inner method of [<code>APP</code>](#module_APP)  
**Params**: <code>string</code> e.g., "door"  
<a name="module_APP..Initializes app"></a>

### APP~Initializes app()
**Kind**: inner method of [<code>APP</code>](#module_APP)  
