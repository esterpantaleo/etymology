<a name="module_DB"></a>

## DB

* [DB](#module_DB)
    * [~Gets an XMLHttpRequest using RxJs()](#module_DB..Gets an XMLHttpRequest using RxJs) ⇒ <code>Observable</code>
    * [~Posts an XMLHttpRequest using RxJs()](#module_DB..Posts an XMLHttpRequest using RxJs) ⇒ <code>Observable</code>
    * [~Posts an array of XMLHttpRequest to the server using RxJs
each requesting ancestors of an iri(of)](#module_DB..Posts an array of XMLHttpRequest to the server using RxJs
each requesting ancestors of an iri) ⇒ <code>Observable</code>
    * [~Prints the disambiguation query into a string.
The generated response will consists of a table with three headers:
iri: the iri of a resources with rdfs label the input string (e.g. http://etytree-virtuoso.wmflabs.org/dbnary/eng/__ee_link)
et: a list of iris of resources that are described by the resource in iri (e.g. http://etytree-virtuoso.wmflabs.org/dbnary/eng/__ee_1_link,http://etytree-virtuoso.wmflabs.org/dbnary/eng/__ee_2_link,http://etytree-virtuoso.wmflabs.org/dbnary/eng/__ee_3_link)
lemma: a string containing the rdfs label of the resource iri(a)](#module_DB..Prints the disambiguation query into a string.
The generated response will consists of a table with three headers_
iri_ the iri of a resources with rdfs label the input string (e.g. http_//etytree-virtuoso.wmflabs.org/dbnary/eng/__ee_link)
et_ a list of iris of resources that are described by the resource in iri (e.g. http_//etytree-virtuoso.wmflabs.org/dbnary/eng/__ee_1_link,http_//etytree-virtuoso.wmflabs.org/dbnary/eng/__ee_2_link,http_//etytree-virtuoso.wmflabs.org/dbnary/eng/__ee_3_link)
lemma_ a string containing the rdfs label of the resource iri) ⇒ <code>string</code>
    * [~Prints the query to get links, pos and gloss of an entry.

The generated response will consists of a table with three headers:
iri
ee
pos: a string containing the rdfs label of the resource iri
gloss: a string containing glossesseparated by ;;;;
links: a string containing links separated by ,(iri)](#module_DB..Prints the query to get links, pos and gloss of an entry.

The generated response will consists of a table with three headers_
iri
ee
pos_ a string containing the rdfs label of the resource iri
gloss_ a string containing glossesseparated by ;;;;
links_ a string containing links separated by ,) ⇒ <code>string</code>
    * [~Prints the query to get ancestors(iri)](#module_DB..Prints the query to get ancestors) ⇒ <code>string</code>
    * [~Prints the query to get descendants(iri)](#module_DB..Prints the query to get descendants) ⇒ <code>string</code>
    * [~Prints the query to get properties about nodes(iri)](#module_DB..Prints the query to get properties about nodes) ⇒ <code>string</code>
    * [~Prints the union of an array of queries 
one for each of the elements in input array iris(of)](#module_DB..Prints the union of an array of queries 
one for each of the elements in input array iris) ⇒ <code>function</code>

<a name="module_DB..Gets an XMLHttpRequest using RxJs"></a>

### DB~Gets an XMLHttpRequest using RxJs() ⇒ <code>Observable</code>
**Kind**: inner method of [<code>DB</code>](#module_DB)  

| Type |
| --- |
| <code>url</code> | 

<a name="module_DB..Posts an XMLHttpRequest using RxJs"></a>

### DB~Posts an XMLHttpRequest using RxJs() ⇒ <code>Observable</code>
**Kind**: inner method of [<code>DB</code>](#module_DB)  

| Type |
| --- |
| <code>string</code> | 

<a name="module_DB..Posts an array of XMLHttpRequest to the server using RxJs
each requesting ancestors of an iri"></a>

### DB~Posts an array of XMLHttpRequest to the server using RxJs
each requesting ancestors of an iri(of) ⇒ <code>Observable</code>
**Kind**: inner method of [<code>DB</code>](#module_DB)  

| Param | Type | Description |
| --- | --- | --- |
| of | <code>array</code> | iri-s |

<a name="module_DB..Prints the disambiguation query into a string.
The generated response will consists of a table with three headers_
iri_ the iri of a resources with rdfs label the input string (e.g. http_//etytree-virtuoso.wmflabs.org/dbnary/eng/__ee_link)
et_ a list of iris of resources that are described by the resource in iri (e.g. http_//etytree-virtuoso.wmflabs.org/dbnary/eng/__ee_1_link,http_//etytree-virtuoso.wmflabs.org/dbnary/eng/__ee_2_link,http_//etytree-virtuoso.wmflabs.org/dbnary/eng/__ee_3_link)
lemma_ a string containing the rdfs label of the resource iri"></a>

### DB~Prints the disambiguation query into a string.
The generated response will consists of a table with three headers:
iri: the iri of a resources with rdfs label the input string (e.g. http://etytree-virtuoso.wmflabs.org/dbnary/eng/__ee_link)
et: a list of iris of resources that are described by the resource in iri (e.g. http://etytree-virtuoso.wmflabs.org/dbnary/eng/__ee_1_link,http://etytree-virtuoso.wmflabs.org/dbnary/eng/__ee_2_link,http://etytree-virtuoso.wmflabs.org/dbnary/eng/__ee_3_link)
lemma: a string containing the rdfs label of the resource iri(a) ⇒ <code>string</code>
**Kind**: inner method of [<code>DB</code>](#module_DB)  
**Returns**: <code>string</code> - a query string  

| Param | Type | Description |
| --- | --- | --- |
| a | <code>string</code> | word e.g. "door" |

<a name="module_DB..Prints the query to get links, pos and gloss of an entry.

The generated response will consists of a table with three headers_
iri
ee
pos_ a string containing the rdfs label of the resource iri
gloss_ a string containing glossesseparated by ;;;;
links_ a string containing links separated by ,"></a>

### DB~Prints the query to get links, pos and gloss of an entry.

The generated response will consists of a table with three headers:
iri
ee
pos: a string containing the rdfs label of the resource iri
gloss: a string containing glossesseparated by ;;;;
links: a string containing links separated by ,(iri) ⇒ <code>string</code>
**Kind**: inner method of [<code>DB</code>](#module_DB)  
**Returns**: <code>string</code> - a query string  

| Param | Type |
| --- | --- |
| iri | <code>string</code> | 

<a name="module_DB..Prints the query to get ancestors"></a>

### DB~Prints the query to get ancestors(iri) ⇒ <code>string</code>
**Kind**: inner method of [<code>DB</code>](#module_DB)  
**Returns**: <code>string</code> - a query string  

| Param | Type |
| --- | --- |
|  | <code>integer</code> | 
| iri | <code>string</code> | 

<a name="module_DB..Prints the query to get descendants"></a>

### DB~Prints the query to get descendants(iri) ⇒ <code>string</code>
**Kind**: inner method of [<code>DB</code>](#module_DB)  
**Returns**: <code>string</code> - a query string  

| Param | Type |
| --- | --- |
| iri | <code>string</code> | 

<a name="module_DB..Prints the query to get properties about nodes"></a>

### DB~Prints the query to get properties about nodes(iri) ⇒ <code>string</code>
**Kind**: inner method of [<code>DB</code>](#module_DB)  
**Returns**: <code>string</code> - a query string  

| Param | Type |
| --- | --- |
| iri | <code>string</code> | 

<a name="module_DB..Prints the union of an array of queries 
one for each of the elements in input array iris"></a>

### DB~Prints the union of an array of queries 
one for each of the elements in input array iris(of) ⇒ <code>function</code>
**Kind**: inner method of [<code>DB</code>](#module_DB)  
**Returns**: <code>function</code> - a function that takes as argument a string iri  

| Param | Type | Description |
| --- | --- | --- |
| of | <code>array</code> | strings iris |

