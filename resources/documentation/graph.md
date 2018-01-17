## Classes

<dl>
<dt><a href="#Node">Node</a></dt>
<dd><p>Creates a Node.</p>
</dd>
<dt><a href="#Dagre">Dagre</a></dt>
<dd><p>Creates a Dagre.</p>
</dd>
<dt><a href="#Graph">Graph</a> ⇐ <code><a href="#Dagre">Dagre</a></code></dt>
<dd><p>Class representing a Graph.</p>
</dd>
<dt><a href="#LanguageGraph">LanguageGraph</a> ⇐ <code><a href="#Graph">Graph</a></code></dt>
<dd><p>Class representing a Language Graph.</p>
</dd>
</dl>

<a name="Node"></a>

## Node
Creates a Node.

**Kind**: global class  

* [Node](#Node)
    * [new Node(counter, etymologyEntry)](#new_Node_new)
    * [.tooltip()](#Node+tooltip)

<a name="new_Node_new"></a>

### new Node(counter, etymologyEntry)
Create a Node with id counter (if counter is not undefined).


| Param | Type |
| --- | --- |
| counter | <code>number</code> | 
| etymologyEntry | <code>EtymologyEntry</code> | 

<a name="Node+tooltip"></a>

### node.tooltip()
Print tooltip

**Kind**: instance method of [<code>Node</code>](#Node)  
<a name="Dagre"></a>

## Dagre
Creates a Dagre.

**Kind**: global class  

* [Dagre](#Dagre)
    * [new Dagre(type)](#new_Dagre_new)
    * [.render(selector, id)](#Dagre+render)
    * [.setLanguages()](#Dagre+setLanguages)

<a name="new_Dagre_new"></a>

### new Dagre(type)
Create a dagre.


| Param | Type | Description |
| --- | --- | --- |
| type | <code>string</code> | has value "TB" (top-bottom) or "LR" (left-right) |

<a name="Dagre+render"></a>

### dagre.render(selector, id)
Draw a dagre from this.dagre in the element seected by "selector"
and call the the svg element "id". Then fit dagre to screen.

**Kind**: instance method of [<code>Dagre</code>](#Dagre)  

| Param | Type |
| --- | --- |
| selector | <code>selector</code> | 
| id | <code>string</code> | 

<a name="Dagre+setLanguages"></a>

### dagre.setLanguages()
Set the value of the array this.languages.

**Kind**: instance method of [<code>Dagre</code>](#Dagre)  
<a name="Graph"></a>

## Graph ⇐ [<code>Dagre</code>](#Dagre)
Class representing a Graph.

**Kind**: global class  
**Extends**: [<code>Dagre</code>](#Dagre)  

* [Graph](#Graph) ⇐ [<code>Dagre</code>](#Dagre)
    * [new Graph(type, graph)](#new_Graph_new)
    * [.setEdges()](#Graph+setEdges)
    * [.render(selector, id)](#Dagre+render)
    * [.setLanguages()](#Dagre+setLanguages)

<a name="new_Graph_new"></a>

### new Graph(type, graph)
Create a graph.


| Param | Type | Description |
| --- | --- | --- |
| type | <code>string</code> | has value "TB" (top-bottom) or "LR" (left-right) |
| graph | <code>Object</code> | with elements "nodes" and "edges" |

<a name="Graph+setEdges"></a>

### graph.setEdges()
Set the value of this.languages and this.edges (if undefined).

**Kind**: instance method of [<code>Graph</code>](#Graph)  
<a name="Dagre+render"></a>

### graph.render(selector, id)
Draw a dagre from this.dagre in the element seected by "selector"
and call the the svg element "id". Then fit dagre to screen.

**Kind**: instance method of [<code>Graph</code>](#Graph)  

| Param | Type |
| --- | --- |
| selector | <code>selector</code> | 
| id | <code>string</code> | 

<a name="Dagre+setLanguages"></a>

### graph.setLanguages()
Set the value of the array this.languages.

**Kind**: instance method of [<code>Graph</code>](#Graph)  
<a name="LanguageGraph"></a>

## LanguageGraph ⇐ [<code>Graph</code>](#Graph)
Class representing a Language Graph.

**Kind**: global class  
**Extends**: [<code>Graph</code>](#Graph)  

* [LanguageGraph](#LanguageGraph) ⇐ [<code>Graph</code>](#Graph)
    * [new LanguageGraph(type, g, language)](#new_LanguageGraph_new)
    * [.setEdges()](#Graph+setEdges)
    * [.render(selector, id)](#Dagre+render)
    * [.setLanguages()](#Dagre+setLanguages)

<a name="new_LanguageGraph_new"></a>

### new LanguageGraph(type, g, language)
Create a language graph.


| Param | Type | Description |
| --- | --- | --- |
| type | <code>string</code> | has value "TB" (top-bottom) or "LR" (left-right) |
| g | [<code>Graph</code>](#Graph) | the full Graph |
| language | <code>string</code> | the language (e.g., "English") |

<a name="Graph+setEdges"></a>

### languageGraph.setEdges()
Set the value of this.languages and this.edges (if undefined).

**Kind**: instance method of [<code>LanguageGraph</code>](#LanguageGraph)  
<a name="Dagre+render"></a>

### languageGraph.render(selector, id)
Draw a dagre from this.dagre in the element seected by "selector"
and call the the svg element "id". Then fit dagre to screen.

**Kind**: instance method of [<code>LanguageGraph</code>](#LanguageGraph)  

| Param | Type |
| --- | --- |
| selector | <code>selector</code> | 
| id | <code>string</code> | 

<a name="Dagre+setLanguages"></a>

### languageGraph.setLanguages()
Set the value of the array this.languages.

**Kind**: instance method of [<code>LanguageGraph</code>](#LanguageGraph)  
