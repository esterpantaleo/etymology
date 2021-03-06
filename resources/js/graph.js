/*globals
  $, d3, console, dagreD3, Rx, window, document, URLSearchParams
*/

/**
 * @module GRAPH
 */
var GRAPH = (function(module) {

	module.bindModule = function(base, moduleName) {
		var etyBase = base; 

		/**
		* Class representing a Node.
 		* @class
                * @alias module:GRAPH.Node
 		*/
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
				this.dagre = new dagreD3.graphlib.Graph().setGraph({ rankdir: type }); 
			}

			/**
                         * Create an svg with the Dagre. 
                         * Assign id to the svg element.
			 * Render svg inside the element selected by "selector".
                         * Then fit to screen.
                         * @function render 
			 * @param {Object} selector
			 * @param {String} id
                         * @param {Number} width
                         * @param {Number} height 
			 */
		         render(selector, id, width, height) {
				var that = this;
		
				var svg = d3.select(selector).append("svg")
					.attr("id", id)
					.attr("width", width)
					.attr("height", height);

				var inner = svg.append("g");
		
				// Set up zoom support                      
				var zoom = d3.behavior.zoom().on("zoom", function() {
					inner.attr("transform", 
						"translate(" +
						d3.event.translate + 
						")" +
						"scale(" + 
						d3.event.scale + 
						")");
					});
				svg.call(zoom);

				// Create the renderer          
				var render = new dagreD3.render();

				// Run the renderer. This is what draws the final graph.  
				render(inner, that.dagre);
		
				// Center the graph
				var graphWidth = that.dagre.graph().width;
				var zoomScale = (graphWidth > width) ? (0.8 * Math.max(width / graphWidth, 0.2)) : 0.75;
		
				zoom.translate([(width - that.dagre.graph().width * zoomScale) / 2, 20])
					.scale(zoomScale)
					.event(svg);

				// Decorate graph
				inner.selectAll("g.node > rect")
					.attr("class", "word");
		
				//show tooltip on mouseover node
				inner.selectAll(".word")
					.on("mouseover", function(d) {
					        d3.select(this).style("cursor", "pointer");
						d3.selectAll(".tooltipText").remove();
						var tooltipDiv = d3.select("#tooltipPopup")
						    .attr("style", "padding: 1em 1em 1em !important;" +
							   "display:inline;" +
							   "left:" + Math.min(d3.event.pageX + 38, width - 190) + "px;" +
							   "top" + (d3.event.pageY - 28) + "px;")
						    .append("div")
                                                    .attr("class", "tooltipText");
					     d3.select(".tooltip")
                                                    .style("padding", "1em 1em 1em 1em !important");
					    that.dagre.node(d).tooltip(tooltipDiv);
						d3.event.stopPropagation();
					});
		
				//append language tag to nodes            
				inner.selectAll("g.node")
					.append("text")
					.style("display", "inline")
					.attr("class", "isoText")
					.attr("x", "1em")
					.attr("y", "3em")
					.html(function(d) {
						return that.dagre.node(d).iso;
					});
		
				//show tooltip on mouseover language tag   
				inner.selectAll("g.node")
					.append("rect")
					.attr("class", "isoRect")
					.attr("x", "0.8em")
					.attr("y", "2.2em")
					.attr("width", function(d) {
						return that.dagre.node(d).iso.length / 1.7 + "em";
					})
					.attr("height", "1em")
				        .on("mouseover", function(d) {
						d3.selectAll(".tooltipText").remove();
						d3.select("#tooltipPopup")
						        .attr("style", "padding: 0em 0.5em 0em 0.5em !important;" +
						      "display: inline;" +
						      "left: " + (d3.event.pageX) + "px;" +
						      "top:" + (d3.event.pageY - 28) + "px;")
					                .append("div")
					                .attr("class", "tooltipText")
					                .append("p")
							.html(that.dagre.node(d).lang);
					        d3.event.stopPropagation();
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
				this.languages = this.languages
					.filter(etyBase.helpers.onlyUnique);
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
				        var nCol = Math.max(Math.floor(width/230), 2); 
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
                         * Sets edges in the graph so that nodes are displayed in 
                         * lines with nCol elements.
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
		
		this.Node = Node;
		this.Graph = Graph;

		etyBase[moduleName] = this;
	};

	return module;
})(GRAPH || {});
