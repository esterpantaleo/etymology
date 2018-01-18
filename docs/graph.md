## Classes

<dl>
<dt><a href="#Node">Node</a></dt>
<dd><p>Class representing a Node.</p>
</dd>
<dt><a href="#Dagre">Dagre</a></dt>
<dd><p>Class representing a Dagre.</p>
</dd>
<dt><a href="#Graph">Graph</a> ⇐ <code><a href="#Dagre">Dagre</a></code></dt>
<dd><p>Class representing a Graph.</p>
</dd>
<dt><a href="#LanguageGraph">LanguageGraph</a> ⇐ <code><a href="#Graph">Graph</a></code></dt>
<dd><p>Class representing a Language Graph.</p>
</dd>
</dl>

## Functions

<dl>
<dt><a href="#tooltip">tooltip()</a></dt>
<dd><p>Prints tooltip</p>
</dd>
<dt><a href="#render">render(id)</a></dt>
<dd><p>Render dagre from this.dagre in the element seected by &quot;selector&quot;
and call the the svg element &quot;id&quot;. Then fit dagre to screen.</p>
</dd>
<dt><a href="#setLanguages">setLanguages()</a></dt>
<dd><p>Sets the value of the array this.languages.</p>
</dd>
<dt><a href="#setEdges">setEdges()</a></dt>
<dd><p>Sets the value of this.languages and this.edges (if undefined).</p>
</dd>
</dl>

<a name="Node"></a>

## Node
Class representing a Node.

**Kind**: global class  
<a name="new_Node_new"></a>

### new Node(counter, etymologyEntry)
Create a Node with id counter (if counter is not undefined).


| Param | Type |
| --- | --- |
| counter | <code>number</code> | 
| etymologyEntry | <code>EtymologyEntry</code> | 

<a name="Dagre"></a>

## Dagre
Class representing a Dagre.

**Kind**: global class  
<a name="new_Dagre_new"></a>

### new Dagre(type)
Create a dagre.


| Param | Type | Description |
| --- | --- | --- |
| type | <code>string</code> | has value "TB" (top-bottom) or "LR" (left-right) |

<a name="Graph"></a>

## Graph ⇐ [<code>Dagre</code>](#Dagre)
Class representing a Graph.

**Kind**: global class  
**Extends**: [<code>Dagre</code>](#Dagre)  
<a name="new_Graph_new"></a>

### new Graph(type, graph)
Create a graph.


| Param | Type | Description |
| --- | --- | --- |
| type | <code>string</code> | has value "TB" (top-bottom) or "LR" (left-right) |
| graph | <code>Object</code> | with elements "nodes" and "edges" |

<a name="LanguageGraph"></a>

## LanguageGraph ⇐ [<code>Graph</code>](#Graph)
Class representing a Language Graph.

**Kind**: global class  
**Extends**: [<code>Graph</code>](#Graph)  
<a name="new_LanguageGraph_new"></a>

### new LanguageGraph(type, g, language)
Create a language graph.


| Param | Type | Description |
| --- | --- | --- |
| type | <code>string</code> | has value "TB" (top-bottom) or "LR" (left-right) |
| g | [<code>Graph</code>](#Graph) | the full Graph |
| language | <code>string</code> | the language (e.g., "English") |

<a name="tooltip"></a>

## tooltip()
Prints tooltip

**Kind**: global function  
<a name="render"></a>

## render(id)
Render dagre from this.dagre in the element seected by "selector"
and call the the svg element "id". Then fit dagre to screen.

**Kind**: global function  

| Param | Type |
| --- | --- |
|  | <code>selector</code> | 
| id | <code>string</code> | 

<a name="setLanguages"></a>

## setLanguages()
Sets the value of the array this.languages.

**Kind**: global function  
<a name="setEdges"></a>

## setEdges()
Sets the value of this.languages and this.edges (if undefined).

**Kind**: global function  
