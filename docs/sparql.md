<a name="module_DB"></a>

## DB

* [DB](#module_DB)
    * [~getXMLHttpRequest()](#module_DB..getXMLHttpRequest) ⇒ <code>Observable</code>
    * [~postXMLHttpRequest()](#module_DB..postXMLHttpRequest) ⇒ <code>Observable</code>
    * [~postMoreAncestorsQuery(of)](#module_DB..postMoreAncestorsQuery) ⇒ <code>Observable</code>
    * [~glossQuery(iri)](#module_DB..glossQuery) ⇒ <code>string</code>
    * [~iterativeAncestorQuery(iri)](#module_DB..iterativeAncestorQuery) ⇒ <code>string</code>
    * [~descendantQuery(iri)](#module_DB..descendantQuery) ⇒ <code>string</code>
    * [~propertyQuery(iri)](#module_DB..propertyQuery) ⇒ <code>string</code>
    * [~unionQuery(of)](#module_DB..unionQuery) ⇒ <code>function</code>

<a name="module_DB..getXMLHttpRequest"></a>

### DB~getXMLHttpRequest() ⇒ <code>Observable</code>
Gets an XMLHttpRequest using RxJs

**Kind**: inner method of [<code>DB</code>](#module_DB)  

| Type |
| --- |
| <code>url</code> | 

<a name="module_DB..postXMLHttpRequest"></a>

### DB~postXMLHttpRequest() ⇒ <code>Observable</code>
Posts an XMLHttpRequest using RxJs

**Kind**: inner method of [<code>DB</code>](#module_DB)  

| Type |
| --- |
| <code>string</code> | 

<a name="module_DB..postMoreAncestorsQuery"></a>

### DB~postMoreAncestorsQuery(of) ⇒ <code>Observable</code>
Posts an array of XMLHttpRequest to the server using RxJs
each requesting ancestors of an iri

**Kind**: inner method of [<code>DB</code>](#module_DB)  

| Param | Type | Description |
| --- | --- | --- |
| of | <code>array</code> | iri-s |

<a name="module_DB..glossQuery"></a>

### DB~glossQuery(iri) ⇒ <code>string</code>
Prints the query to get links, pos and gloss of an entry.

The generated response will consists of a table with three headers:
"iri"
"ee"
"pos": a string containing the rdfs label of the resource "iri"
"gloss": a string containing glossesseparated by ";;;;""
"links": a string containing links separated by ","

**Kind**: inner method of [<code>DB</code>](#module_DB)  
**Returns**: <code>string</code> - a query string  

| Param | Type |
| --- | --- |
| iri | <code>string</code> | 

<a name="module_DB..iterativeAncestorQuery"></a>

### DB~iterativeAncestorQuery(iri) ⇒ <code>string</code>
Prints the query to get ancestors

**Kind**: inner method of [<code>DB</code>](#module_DB)  
**Returns**: <code>string</code> - a query string  

| Param | Type |
| --- | --- |
|  | <code>integer</code> | 
| iri | <code>string</code> | 

<a name="module_DB..descendantQuery"></a>

### DB~descendantQuery(iri) ⇒ <code>string</code>
Prints the query to get descendants

**Kind**: inner method of [<code>DB</code>](#module_DB)  
**Returns**: <code>string</code> - a query string  

| Param | Type |
| --- | --- |
| iri | <code>string</code> | 

<a name="module_DB..propertyQuery"></a>

### DB~propertyQuery(iri) ⇒ <code>string</code>
Prints the query to get properties about nodes

**Kind**: inner method of [<code>DB</code>](#module_DB)  
**Returns**: <code>string</code> - a query string  

| Param | Type |
| --- | --- |
| iri | <code>string</code> | 

<a name="module_DB..unionQuery"></a>

### DB~unionQuery(of) ⇒ <code>function</code>
Prints the union of an array of queries 
one for each of the elements in input array iris

**Kind**: inner method of [<code>DB</code>](#module_DB)  
**Returns**: <code>function</code> - a function that takes as argument a string iri  

| Param | Type | Description |
| --- | --- | --- |
| of | <code>array</code> | strings iris |

