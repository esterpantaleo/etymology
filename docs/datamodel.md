<a name="module_DATAMODEL"></a>

## DATAMODEL

* [DATAMODEL](#module_DATAMODEL)
    * [~EtymologyEntry](#module_DATAMODEL..EtymologyEntry)
        * [new EtymologyEntry(iri, label)](#new_module_DATAMODEL..EtymologyEntry_new)
    * [~Encodes a query into an url(a)](#module_DATAMODEL..Encodes a query into an url) ⇒ <code>string</code>
    * [~Returns a label by replacing special characters(an)](#module_DATAMODEL..Returns a label by replacing special characters) ⇒ <code>string</code>
    * [~Used to merge EtymologyEntries into one Node
Assigns a node value (integer) to each EtymologyEntries
Different EtymologyEntries can be assigned the same node value 
if they are etymologically equivalent or refer to the same word.
The final graph will merge EtymologyEntries that have the same node value
into the same node
(e.g.: if only ee_word and ee_n_word with n an integer belong to
the set of ancestors and descendants then merge them into one node)()](#module_DATAMODEL..Used to merge EtymologyEntries into one Node
Assigns a node value (integer) to each EtymologyEntries
Different EtymologyEntries can be assigned the same node value 
if they are etymologically equivalent or refer to the same word.
The final graph will merge EtymologyEntries that have the same node value
into the same node
(e.g._ if only ee_word and ee_n_word with n an integer belong to
the set of ancestors and descendants then merge them into one node)) ⇒ <code>Object</code>
    * [~Given a string returns an RxJS observable
containing the parsed response of the server to
the disambiguationQuery()](#module_DATAMODEL..Given a string returns an RxJS observable
containing the parsed response of the server to
the disambiguationQuery) ⇒ <code>Observable</code>
    * [~Given an iri returns an RxJS observable
containing the parsed response of the server to
the glossQuery(an)](#module_DATAMODEL..Given an iri returns an RxJS observable
containing the parsed response of the server to
the glossQuery) ⇒ <code>Observable</code>
    * [~Parse response of {@link disambiguationQuery disambiguation query} to the server()](#module_DATAMODEL..Parse response of {@link disambiguationQuery disambiguation query} to the server) ⇒ <code>array</code>
    * [~Posts an XMLHttpRequest to get data about disambiguation nodes()](#module_DATAMODEL..Posts an XMLHttpRequest to get data about disambiguation nodes) ⇒ <code>Observable</code>
    * [~Posts an XMLHttpRequest to more ancestors()](#module_DATAMODEL..Posts an XMLHttpRequest to more ancestors) ⇒ <code>Observable</code>
    * [~Posts an XMLHttpRequest to find ancestors()](#module_DATAMODEL..Posts an XMLHttpRequest to find ancestors) ⇒ <code>Observable</code>

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

<a name="module_DATAMODEL..Encodes a query into an url"></a>

### DATAMODEL~Encodes a query into an url(a) ⇒ <code>string</code>
**Kind**: inner method of [<code>DATAMODEL</code>](#module_DATAMODEL)  
**Returns**: <code>string</code> - a url  

| Param | Type | Description |
| --- | --- | --- |
| a | <code>string</code> | query |

<a name="module_DATAMODEL..Returns a label by replacing special characters"></a>

### DATAMODEL~Returns a label by replacing special characters(an) ⇒ <code>string</code>
**Kind**: inner method of [<code>DATAMODEL</code>](#module_DATAMODEL)  
**Returns**: <code>string</code> - a label  

| Param | Type | Description |
| --- | --- | --- |
| an | <code>string</code> | encoded label |

<a name="module_DATAMODEL..Used to merge EtymologyEntries into one Node
Assigns a node value (integer) to each EtymologyEntries
Different EtymologyEntries can be assigned the same node value 
if they are etymologically equivalent or refer to the same word.
The final graph will merge EtymologyEntries that have the same node value
into the same node
(e.g._ if only ee_word and ee_n_word with n an integer belong to
the set of ancestors and descendants then merge them into one node)"></a>

### DATAMODEL~Used to merge EtymologyEntries into one Node
Assigns a node value (integer) to each EtymologyEntries
Different EtymologyEntries can be assigned the same node value 
if they are etymologically equivalent or refer to the same word.
The final graph will merge EtymologyEntries that have the same node value
into the same node
(e.g.: if only ee_word and ee_n_word with n an integer belong to
the set of ancestors and descendants then merge them into one node)() ⇒ <code>Object</code>
**Kind**: inner method of [<code>DATAMODEL</code>](#module_DATAMODEL)  
**Returns**: <code>Object</code> - .<EtymologyEntry> containing a list of Etymology Entries  

| Param | Type | Description |
| --- | --- | --- |
| .<EtymologyEntry> | <code>Object</code> | containing a list of Etymology Entries |

<a name="module_DATAMODEL..Given a string returns an RxJS observable
containing the parsed response of the server to
the disambiguationQuery"></a>

### DATAMODEL~Given a string returns an RxJS observable
containing the parsed response of the server to
the disambiguationQuery() ⇒ <code>Observable</code>
**Kind**: inner method of [<code>DATAMODEL</code>](#module_DATAMODEL)  

| Type |
| --- |
| <code>string</code> | 

<a name="module_DATAMODEL..Given an iri returns an RxJS observable
containing the parsed response of the server to
the glossQuery"></a>

### DATAMODEL~Given an iri returns an RxJS observable
containing the parsed response of the server to
the glossQuery(an) ⇒ <code>Observable</code>
**Kind**: inner method of [<code>DATAMODEL</code>](#module_DATAMODEL)  

| Param | Type | Description |
| --- | --- | --- |
| an | <code>string</code> | iri |

<a name="module_DATAMODEL..Parse response of {@link disambiguationQuery disambiguation query} to the server"></a>

### DATAMODEL~Parse response of {@link disambiguationQuery disambiguation query} to the server() ⇒ <code>array</code>
**Kind**: inner method of [<code>DATAMODEL</code>](#module_DATAMODEL)  
**Returns**: <code>array</code> - of Etymology Entries  

| Type |
| --- |
| <code>string</code> | 

<a name="module_DATAMODEL..Posts an XMLHttpRequest to get data about disambiguation nodes"></a>

### DATAMODEL~Posts an XMLHttpRequest to get data about disambiguation nodes() ⇒ <code>Observable</code>
**Kind**: inner method of [<code>DATAMODEL</code>](#module_DATAMODEL)  

| Type |
| --- |
| <code>string</code> | 

<a name="module_DATAMODEL..Posts an XMLHttpRequest to more ancestors"></a>

### DATAMODEL~Posts an XMLHttpRequest to more ancestors() ⇒ <code>Observable</code>
**Kind**: inner method of [<code>DATAMODEL</code>](#module_DATAMODEL)  

| Type |
| --- |
| <code>string</code> | 

<a name="module_DATAMODEL..Posts an XMLHttpRequest to find ancestors"></a>

### DATAMODEL~Posts an XMLHttpRequest to find ancestors() ⇒ <code>Observable</code>
**Kind**: inner method of [<code>DATAMODEL</code>](#module_DATAMODEL)  

| Type |
| --- |
| <code>string</code> | 

