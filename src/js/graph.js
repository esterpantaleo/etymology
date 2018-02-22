/**
 * @module GRAPH
 */

/**
 * Class representing a Node.
 * @requires d3
 * @requires dagre-d3
 * @class
 */
const d3 = require("d3");
const dagreD3 = require("dagre-d3");

class Node {
    /**
     * Creates a Node with id counter (if counter is not undefined).
     * @param {Number} counter
     * @param {EtymologyEntry} etymologyEntry
     */
    constructor(counter, etymologyEntry) {
	if (undefined !== counter) {
	    if (undefined === etymologyEntry) {
		this.iri = []; //array of iris 
		this.isAncestor = false; //bool
		this.id = undefined; //integer
		this.iso = undefined; //string
		this.label = undefined; //string
		this.lang = undefined; //string
	    } else {
		this.iri = [etymologyEntry.iri];
		this.isAncestor = etymologyEntry.isAncestor;
		this.id = counter;
		this.iso = etymologyEntry.iso;
		this.lang = etymologyEntry.lang;
		this.label = etymologyEntry.label;
	    }
	    this.posAndGloss = []; //array of objects 
	    this.urlAndLabel = []; //array of objects 
	    
	    this.rx = this.ry = 25; //integer   radius of a node 
	} else {
	    console.error("Wrong input parameters to Node constructor");
	}
    }    
    
    /**
     * Prints tooltip.
     * @function tooltip
     */
    tooltip(element) {
	var labels = this.label.split(",");
	for (var i in labels) {
	    element.append("span")
		.html("<b>" + labels[i] + "</b>");
	    element.append("span")
		.html("<br><br>");
	    var ps = this.posAndGloss[i];
	    for (var j in ps) {
		for (var k in ps[j].gloss) {
		    element.append("span")
			.html(ps[j].pos + " - " + ps[j].gloss[k]);
		    element.append("span")
			.html("<br><br>");
		}
	    }
	    element.append("span")
		.html("<br><hr>Data is under CC BY-SA and has been extracted from: ");
	    var ual = this.urlAndLabel[i];
	    for (var j in ual) {
		element.append("a")
		    .attr("href", ual[j].url)
		    .attr("target", "_blank")
		    .text(ual[j].label);
		if (j < ual.length - 1) {
		    element.append("span").html(",");
		}
	    }
	    element.append("span")
		.html("<br><br>");
	}
    }
}

/**
 * Class representing a Dagre (Directed acyclic graph).
 * @class
 */
class Dagre {
    /**
     * Create a dagre.
     * @param {String} type - has value "TB" (top-bottom) or "LR" (left-right)
     */
    constructor(type) {
	//initialize dagre 
	this.dagre = new dagreD3.graphlib.Graph().setGraph({rankdir: type});
    }
    
    /**
     * Create an svg with the Dagre.
     * Render svg inside "element" and assign to it an "id".
     * Then fit to screen.
     * @function render
     * @param {Object} element - e.g., "#tree-overlay"
     * @param {String} id
     * @param {Number} width
     * @param {Number} height
     */
    render(element, id, width, height) {
	var that = this;
	
	var svg = d3.select(element)
	    .append("svg")
	    .attr("id", id)
	    .attr("width", width)
	    .attr("height", height);
	
	var inner = svg.append("g");
	
	// Set up zoom support                      
	var zoom = d3.zoom()
	    .on("zoom", () => {
		inner.attr("transform", d3.event.transform);
	    });
	svg.call(zoom);

	// Create the renderer          
	var render = new dagreD3.render();
	// Run the renderer. This is what draws the final graph.  
	render(inner, that.dagre);
	
	// Center the graph
	var graphWidth = that.dagre.graph().width;
	var zoomScale = (graphWidth > width) ? (0.8 * Math.max(width / graphWidth, 0.2)) : 0.75;

	svg.call(zoom.transform, d3.zoomIdentity
		 .translate((width - that.dagre.graph().width * zoomScale) / 2, 50)
		 .scale(zoomScale));
	
	// Decorate graph
	inner.selectAll("g.node")
	    .on('taphold mouseover', (d) => {
		var d3_target = d3.select(d3.event.target);
		d3.select("#glossPopup")
		    .remove();
		d3.event.preventDefault();
		var wordPopup = d3.select("body")
		    .append("div")
		    .attr("id", "glossPopup")
		    .style("top", (d3.event.pageY + 35) + "px");
		that.dagre.node(d).tooltip(wordPopup);
		d3.select("#glossPopup")
		    .on('click', () => {
			d3.select("#glossPopup")
			    .remove();
		    });
	});
	// Append language tag to nodes
        // and remove text after parenthesis
	inner.selectAll("g.node")
	    .append("text")
	    .style("display", "inline")
	    .attr("class", "isoText")
	    .attr("x", "1em")
	    .attr("y", "3em")
	    .html((d) => {
		return that.dagre.node(d).lang.split("(")[0];
	    });

	d3.select(window)
	    .on("click", () => {
		d3.select("#glossPopup")
		    .remove();
	    });
	
	return inner;
    }
    
    /**
     * Sets the value of the array this.languages.
     * @function setLanguages
     */
    setLanguages() {
	for (var gn in this.nodes) {
	    var lang = this.nodes[gn].lang;
	    if (undefined !== lang) {
		this.languages.push(lang);
	    }
	}
	this.languages = [...new Set(this.languages)];
    }
}

/**
 * Class representing a Graph.
 * @extends Dagre
 */
class Graph extends Dagre {
    /**
     * Create a graph.
     * @param {String} type - has value "TB" (top-bottom) or "LR" (left-right)
     * @param {Object} graph - with elements "nodes" and "edges"
     * @param {Number} width
     */
    constructor(type, graph, width) {
	super(type);
	
	//initialize nodes            
	if (undefined === graph.nodes) {
	    console.error("Error: no arguments provided to Graph constructor");
	} else {
	    this.nodes = graph.nodes;
	    for (var n in this.nodes) {
		this.dagre.setNode(n, this.nodes[n]);
	    }
	}
	
	//initialize languages 
	this.languages = [];
	
	//initialize edges
	if (undefined === graph.edges) {
	    //group nodes by language and display them in columns of length 230
	    var nCol = Math.max(Math.floor(width / 150), 1);
	    this.setEdges(nCol);
	    for (var e in this.edges) {
		var source = this.edges[e].source,
		    target = this.edges[e].target;
		this.dagre.setEdge(source, target, this.edges[e].style);
	    }
	} else {
	    this.edges = graph.edges;
	    for (var e in this.edges) {
		var source = this.edges[e].source,
		    target = this.edges[e].target;
		if (this.nodes[source].isAncestor && this.nodes[target].isAncestor) {
		    this.dagre.setEdge(source, target, this.edges[e].style);
		}
	    }
	}
    }
    
    
    /**
     * Sets edges in the graph so nodes are displayed in
     * lines with nCol elements, and nodes in the same language
     * are next to each other.
     * Sets the value of this.languages if undefined
     * @function setEdges
     * @param {Number} nCol - number of nodes that will be displayed in a line
     */
    setEdges(nCol) {
	this.setLanguages();
	this.edges = [];
	
	var m = null, col = 1;
	for (var l in this.languages) {
	    for (var n in this.nodes) {
		if (this.nodes[n].lang === this.languages[l]) {
		    if (m !== null) {
			this.edges.push({
			    source: m,
			    target: n,
			    style: {
				label: "",
				style: "stroke: none",
				lineInterpolate: "basis",
				arrowheadStyle: "fill: none"
			    }
			});
			col += 1;
		    }
		    if (col === nCol) {
			m = null;
			col = 1;
		    } else {
			m = n;
		    }
		}
	    }
	}
    }
}

var  wrap = (element, text, y, dy, width) => {
    var words = text.split(/\s+/).reverse(),
        word,
        line = [],
        lineNumber = 0,
        lineHeight = 1.1, // ems
        tspan = element.append("tspan").attr("x", 0).attr("y", y).attr("dy", dy + "em");
    while (word = words.pop()) {
	line.push(word);
	tspan.text(line.join(" "));
	if (tspan.node().getComputedTextLength() > width) {
            line.pop();
            tspan.text(line.join(" "));
            line = [word];
            tspan = element.append("tspan").attr("x", 0).attr("y", y).attr("dy", ++lineNumber * lineHeight + dy + "em").text(word);
	}
    }
    
}

module.exports = {
    Node: Node,
    Graph: Graph,
    wrap: wrap
}
