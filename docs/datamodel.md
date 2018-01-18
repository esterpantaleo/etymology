<a name="module_DATAMODEL"></a>

## DATAMODEL

* [DATAMODEL](#module_DATAMODEL)
    * [~EtymologyEntry](#module_DATAMODEL..EtymologyEntry)
        * [new EtymologyEntry(iri, label)](#new_module_DATAMODEL..EtymologyEntry_new)
    * [~urlFromQuery(a)](#module_DATAMODEL..urlFromQuery) ⇒ <code>string</code>
    * [~wiktionaryLabelOf(an)](#module_DATAMODEL..wiktionaryLabelOf) ⇒ <code>string</code>
    * [~parseLabel(an)](#module_DATAMODEL..parseLabel) ⇒ <code>string</code>
    * [~encodeLabel(a)](#module_DATAMODEL..encodeLabel) ⇒ <code>string</code>
    * [~dbnaryLabelOf(an)](#module_DATAMODEL..dbnaryLabelOf)
    * [~dbnaryIsoOf(an)](#module_DATAMODEL..dbnaryIsoOf)
    * [~dbnaryEtyOf(an)](#module_DATAMODEL..dbnaryEtyOf)
    * [~assignNodes()](#module_DATAMODEL..assignNodes) ⇒ <code>Object</code>
    * [~disambiguation()](#module_DATAMODEL..disambiguation) ⇒ <code>Observable</code>
    * [~glossQuery(an)](#module_DATAMODEL..glossQuery) ⇒ <code>Observable</code>
    * [~propertyQueryScalar(an)](#module_DATAMODEL..propertyQueryScalar) ⇒ <code>Observable</code>
    * [~propertyQuery()](#module_DATAMODEL..propertyQuery) ⇒ <code>Observable</code>
    * [~dataQuery()](#module_DATAMODEL..dataQuery) ⇒ <code>Observable</code>
    * [~parseData()](#module_DATAMODEL..parseData) ⇒ <code>Object</code>
    * [~parseDisambiguation
Parse response of {@link disambiguationQuery disambiguation query} to the server()](#module_DATAMODEL..parseDisambiguation
Parse response of {@link disambiguationQuery disambiguation query} to the server) ⇒ <code>array</code>
    * [~parseProperties()](#module_DATAMODEL..parseProperties) ⇒ <code>array</code>
    * [~disambiguationQuery
Posts an XMLHttpRequest to get data about disambiguation nodes()](#module_DATAMODEL..disambiguationQuery
Posts an XMLHttpRequest to get data about disambiguation nodes) ⇒ <code>Observable</code>
    * [~findMoreAncestors()](#module_DATAMODEL..findMoreAncestors) ⇒ <code>Observable</code>
    * [~findAncestors()](#module_DATAMODEL..findAncestors) ⇒ <code>Observable</code>
    * [~mergeAncestors()](#module_DATAMODEL..mergeAncestors) ⇒ <code>array</code>
    * [~ancestorsQuery(a)](#module_DATAMODEL..ancestorsQuery) ⇒ <code>array</code>
    * [~parseAncestors(a)](#module_DATAMODEL..parseAncestors) ⇒ <code>Object</code>
    * [~setEtymologyEntries()](#module_DATAMODEL..setEtymologyEntries) ⇒ <code>Object</code>
    * [~cleanEtymologyEntries(of)](#module_DATAMODEL..cleanEtymologyEntries) ⇒ <code>Object</code>
    * [~descendantsQuery(a)](#module_DATAMODEL..descendantsQuery) ⇒ <code>Observable</code>
    * [~descendantsQueryScalar(an)](#module_DATAMODEL..descendantsQueryScalar) ⇒ <code>Observable</code>
    * [~parseDescendants(a)](#module_DATAMODEL..parseDescendants) ⇒ <code>Object</code>

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

### DATAMODEL~urlFromQuery(a) ⇒ <code>string</code>
Encodes a query into an url

**Kind**: inner method of [<code>DATAMODEL</code>](#module_DATAMODEL)  
**Returns**: <code>string</code> - a url  

| Param | Type | Description |
| --- | --- | --- |
| a | <code>string</code> | query |

<a name="module_DATAMODEL..wiktionaryLabelOf"></a>

### DATAMODEL~wiktionaryLabelOf(an) ⇒ <code>string</code>
Given an iri, returns language + label

**Kind**: inner method of [<code>DATAMODEL</code>](#module_DATAMODEL)  
**Returns**: <code>string</code> - a label  

| Param | Type | Description |
| --- | --- | --- |
| an | <code>string</code> | iri |

<a name="module_DATAMODEL..parseLabel"></a>

### DATAMODEL~parseLabel(an) ⇒ <code>string</code>
Returns a label by replacing special characters

**Kind**: inner method of [<code>DATAMODEL</code>](#module_DATAMODEL)  
**Returns**: <code>string</code> - a label  

| Param | Type | Description |
| --- | --- | --- |
| an | <code>string</code> | encoded label |

<a name="module_DATAMODEL..encodeLabel"></a>

### DATAMODEL~encodeLabel(a) ⇒ <code>string</code>
Given a label, returns an encoded label

**Kind**: inner method of [<code>DATAMODEL</code>](#module_DATAMODEL)  
**Returns**: <code>string</code> - an encoded label  

| Param | Type | Description |
| --- | --- | --- |
| a | <code>string</code> | label |

<a name="module_DATAMODEL..dbnaryLabelOf"></a>

### DATAMODEL~dbnaryLabelOf(an)
**Kind**: inner method of [<code>DATAMODEL</code>](#module_DATAMODEL)  

| Param | Type | Description |
| --- | --- | --- |
| an | <code>string</code> | iri |

<a name="module_DATAMODEL..dbnaryIsoOf"></a>

### DATAMODEL~dbnaryIsoOf(an)
**Kind**: inner method of [<code>DATAMODEL</code>](#module_DATAMODEL)  

| Param | Type | Description |
| --- | --- | --- |
| an | <code>string</code> | iri |

<a name="module_DATAMODEL..dbnaryEtyOf"></a>

### DATAMODEL~dbnaryEtyOf(an)
**Kind**: inner method of [<code>DATAMODEL</code>](#module_DATAMODEL)  

| Param | Type | Description |
| --- | --- | --- |
| an | <code>string</code> | iri |

<a name="module_DATAMODEL..assignNodes"></a>

### DATAMODEL~assignNodes() ⇒ <code>Object</code>
Used to merge EtymologyEntries into one Node
Assigns a node value (integer) to each EtymologyEntries
Different EtymologyEntries can be assigned the same node value 
if they are etymologically equivalent or refer to the same word.
The final graph will merge EtymologyEntries that have the same node value
into the same node
(e.g.: if only ee_word and ee_n_word with n an integer belong to
the set of ancestors and descendants then merge them into one node)

**Kind**: inner method of [<code>DATAMODEL</code>](#module_DATAMODEL)  
**Returns**: <code>Object</code> - .<EtymologyEntry> containing a list of Etymology Entries  

| Param | Type | Description |
| --- | --- | --- |
| .<EtymologyEntry> | <code>Object</code> | containing a list of Etymology Entries |

<a name="module_DATAMODEL..disambiguation"></a>

### DATAMODEL~disambiguation() ⇒ <code>Observable</code>
Given a string returns an RxJS observable
containing the parsed response of the server to
the disambiguationQuery

**Kind**: inner method of [<code>DATAMODEL</code>](#module_DATAMODEL)  

| Type |
| --- |
| <code>string</code> | 

<a name="module_DATAMODEL..glossQuery"></a>

### DATAMODEL~glossQuery(an) ⇒ <code>Observable</code>
Given an iri returns an RxJS observable
containing the parsed response of the server to
the glossQuery

**Kind**: inner method of [<code>DATAMODEL</code>](#module_DATAMODEL)  

| Param | Type | Description |
| --- | --- | --- |
| an | <code>string</code> | iri |

<a name="module_DATAMODEL..propertyQueryScalar"></a>

### DATAMODEL~propertyQueryScalar(an) ⇒ <code>Observable</code>
**Kind**: inner method of [<code>DATAMODEL</code>](#module_DATAMODEL)  

| Param | Type | Description |
| --- | --- | --- |
| an | <code>string</code> | iri |

<a name="module_DATAMODEL..propertyQuery"></a>

### DATAMODEL~propertyQuery() ⇒ <code>Observable</code>
**Kind**: inner method of [<code>DATAMODEL</code>](#module_DATAMODEL)  

| Param | Type |
| --- | --- |
| .<string> | <code>array</code> | 

<a name="module_DATAMODEL..dataQuery"></a>

### DATAMODEL~dataQuery() ⇒ <code>Observable</code>
**Kind**: inner method of [<code>DATAMODEL</code>](#module_DATAMODEL)  

| Param | Type |
| --- | --- |
| .<string> | <code>array</code> | 
|  | <code>Graph</code> | 

<a name="module_DATAMODEL..parseData"></a>

### DATAMODEL~parseData() ⇒ <code>Object</code>
**Kind**: inner method of [<code>DATAMODEL</code>](#module_DATAMODEL)  
**Returns**: <code>Object</code> - with elements "posAndGloss" and "urlAndLabel"  

| Type |
| --- |
| <code>string</code> | 

<a name="module_DATAMODEL..parseDisambiguation
Parse response of {@link disambiguationQuery disambiguation query} to the server"></a>

### DATAMODEL~parseDisambiguation
Parse response of {@link disambiguationQuery disambiguation query} to the server() ⇒ <code>array</code>
**Kind**: inner method of [<code>DATAMODEL</code>](#module_DATAMODEL)  
**Returns**: <code>array</code> - of Etymology Entries  

| Type |
| --- |
| <code>string</code> | 

<a name="module_DATAMODEL..parseProperties"></a>

### DATAMODEL~parseProperties() ⇒ <code>array</code>
**Kind**: inner method of [<code>DATAMODEL</code>](#module_DATAMODEL)  
**Returns**: <code>array</code> - of properties  

| Type |
| --- |
| <code>string</code> | 

<a name="module_DATAMODEL..disambiguationQuery
Posts an XMLHttpRequest to get data about disambiguation nodes"></a>

### DATAMODEL~disambiguationQuery
Posts an XMLHttpRequest to get data about disambiguation nodes() ⇒ <code>Observable</code>
**Kind**: inner method of [<code>DATAMODEL</code>](#module_DATAMODEL)  

| Type |
| --- |
| <code>string</code> | 

<a name="module_DATAMODEL..findMoreAncestors"></a>

### DATAMODEL~findMoreAncestors() ⇒ <code>Observable</code>
Posts an XMLHttpRequest to more ancestors

**Kind**: inner method of [<code>DATAMODEL</code>](#module_DATAMODEL)  

| Type |
| --- |
| <code>string</code> | 

<a name="module_DATAMODEL..findAncestors"></a>

### DATAMODEL~findAncestors() ⇒ <code>Observable</code>
Posts an XMLHttpRequest to find ancestors

**Kind**: inner method of [<code>DATAMODEL</code>](#module_DATAMODEL)  

| Type |
| --- |
| <code>string</code> | 

<a name="module_DATAMODEL..mergeAncestors"></a>

### DATAMODEL~mergeAncestors() ⇒ <code>array</code>
**Kind**: inner method of [<code>DATAMODEL</code>](#module_DATAMODEL)  

| Param | Type |
| --- | --- |
| .<string> | <code>array</code> | 
| .<string> | <code>array</code> | 

<a name="module_DATAMODEL..ancestorsQuery"></a>

### DATAMODEL~ancestorsQuery(a) ⇒ <code>array</code>
**Kind**: inner method of [<code>DATAMODEL</code>](#module_DATAMODEL)  
**Returns**: <code>array</code> - .<string> an array of ancestors  

| Param | Type | Description |
| --- | --- | --- |
|  | <code>string</code> |  |
| a | <code>function</code> | callback |

<a name="module_DATAMODEL..parseAncestors"></a>

### DATAMODEL~parseAncestors(a) ⇒ <code>Object</code>
**Kind**: inner method of [<code>DATAMODEL</code>](#module_DATAMODEL)  
**Returns**: <code>Object</code> - with elements "all" and "last"  

| Param | Type | Description |
| --- | --- | --- |
| a | <code>string</code> | query response |

<a name="module_DATAMODEL..setEtymologyEntries"></a>

### DATAMODEL~setEtymologyEntries() ⇒ <code>Object</code>
**Kind**: inner method of [<code>DATAMODEL</code>](#module_DATAMODEL)  
**Returns**: <code>Object</code> - with elements "values" and "edges"  

| Param | Type | Description |
| --- | --- | --- |
| .<Object> | <code>array</code> | of properties |
| .<string> | <code>array</code> | of ancestors |

<a name="module_DATAMODEL..cleanEtymologyEntries"></a>

### DATAMODEL~cleanEtymologyEntries(of) ⇒ <code>Object</code>
**Kind**: inner method of [<code>DATAMODEL</code>](#module_DATAMODEL)  
**Returns**: <code>Object</code> - with elements "values" and "edges"  

| Param | Type | Description |
| --- | --- | --- |
| of | <code>array</code> | ancestors |

<a name="module_DATAMODEL..descendantsQuery"></a>

### DATAMODEL~descendantsQuery(a) ⇒ <code>Observable</code>
**Kind**: inner method of [<code>DATAMODEL</code>](#module_DATAMODEL)  

| Param | Type | Description |
| --- | --- | --- |
| .<string> | <code>array</code> | of iri-s |
| a | <code>function</code> | callback |

<a name="module_DATAMODEL..descendantsQueryScalar"></a>

### DATAMODEL~descendantsQueryScalar(an) ⇒ <code>Observable</code>
**Kind**: inner method of [<code>DATAMODEL</code>](#module_DATAMODEL)  

| Param | Type | Description |
| --- | --- | --- |
| an | <code>string</code> | iri |

<a name="module_DATAMODEL..parseDescendants"></a>

### DATAMODEL~parseDescendants(a) ⇒ <code>Object</code>
**Kind**: inner method of [<code>DATAMODEL</code>](#module_DATAMODEL)  
**Returns**: <code>Object</code> - containing a list of Etymology Entries  

| Param | Type | Description |
| --- | --- | --- |
| a | <code>string</code> | query response |

