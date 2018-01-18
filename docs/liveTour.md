## Objects

<dl>
<dt><a href="#tour">tour</a> : <code>object</code></dt>
<dd><p>A <a href="http://linkedin.github.io/hopscotch/">Hopscotch</a> tour.</p>
</dd>
</dl>

## Functions

<dl>
<dt><a href="#setCookie">setCookie(key, value)</a></dt>
<dd><p>Set cookie for the <a href="http://linkedin.github.io/hopscotch/">Hopscotch</a> tour.</p>
</dd>
<dt><a href="#getCookies">getCookies(key)</a></dt>
<dd><p>Get cookie for the <a href="http://linkedin.github.io/hopscotch/">Hopscotch</a> tour</p>
</dd>
</dl>

<a name="tour"></a>

## tour : <code>object</code>
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


* [tour](#tour) : <code>object</code>
    * [.onEnd()](#tour.onEnd)
    * [.onClose()](#tour.onClose)

<a name="tour.onEnd"></a>

### tour.onEnd()
Set cookie on end (tour.onEnd).

**Kind**: static method of [<code>tour</code>](#tour)  
<a name="tour.onClose"></a>

### tour.onClose()
Set cookie on close (tour.onClose).

**Kind**: static method of [<code>tour</code>](#tour)  
<a name="setCookie"></a>

## setCookie(key, value)
Set cookie for the [Hopscotch](http://linkedin.github.io/hopscotch/) tour.

**Kind**: global function  

| Param | Type |
| --- | --- |
| key | <code>string</code> | 
| value | <code>string</code> | 

<a name="getCookies"></a>

## getCookies(key)
Get cookie for the [Hopscotch](http://linkedin.github.io/hopscotch/) tour

**Kind**: global function  

| Param | Type |
| --- | --- |
| key | <code>string</code> | 

