/*globals
    $, d3, console, dagreD3, Rx, window, document, URLSearchParams
*/
/*jshint loopfunc: true, shadow: true, latedef: false */ // Consider removing this and fixing these
var GRAPH = (function(module) {

    module.bindModule = function(base, moduleName) {
        var etyBase = base;

        class Node {
            constructor(iri, label) {                
                //set this.iri
                this.iri = iri;

                var id = iri.replace("http://etytree-virtuoso.wmflabs.org/dbnary/eng/", "")
                    .split("/");
	        var tmp = (id.length > 1) ? id[1] : id[0];

                //set this.iso
                this.iso = (id.length > 1) ? id[0] : "eng";

                //set this.label
                this.label = (undefined === label) ? tmp.replace("__ee_", "") : label;
                this.label = this.label.replace(/^_/, '*').replace("__", "'").replace("%C2%B7", "·").replace(/_/g, " ");
                
                //set this.ety
                //this.ety is an integer representing the etymology number encoded in the iri;
                //if ety === 0 the iri is __ee_word                                    
                //if ety === 1 the iri is __ee_1_word            
                //etc       
                tmp = tmp.match(/__ee_[0-9]+_/g);
                this.ety = (null === tmp) ? 0 : tmp[0].match(/__ee_(.*?)_/)[1]; 
                
                //set this.lang
                this.lang = etyBase.tree.langMap.get(this.iso);
                
                //initialize this.graphNode specifying the graphNode corresponding to the node
                this.graphNode = undefined;
                
                //initialize this.eqIri is an array of iri-s of Node-s that are equivalent to the Node 
                this.eqIri = [];
                this.eqIri.push(iri);
                
                //initialize this.isAncestor
                this.isAncestor = false;
		
		//set radius on node
		this.rx = this.ry = 25;
            }

            tooltipQuery() { 
                var that = this;
                d3.selectAll(".tooltip").remove();
                var query = etyBase.DB.lemmaQuery(this.iri);
                var url = etyBase.config.urls.ENDPOINT + "?query=" + encodeURIComponent(query);
                return etyBase.DB.getXMLHttpRequest(url)
		    .flatMap(response => that.setTooltip(response));
            }

            setTooltip(response) {
                var tooltip = "<b>" + this.label + "</b><br><br><br>";
                if (null !== response) {
                    //print definition  
                    var dataJson = JSON.parse(response).results.bindings;
                    tooltip += this.setDefinition(dataJson);
                    //print links 
                    tooltip += this.setLinks(dataJson);
                } else {
                    tooltip += "-";
                }
		return Promise.resolve(tooltip);
            }
                
            setDefinition(dataJson) {
                return dataJson.map(d => {
                    if (undefined !== d.pos && undefined !== d.gloss) {
                        return d.gloss.value.split(";;;;").map(e => {
                            return d.pos.value + " - " + e + "<br><br>";
                        }).join("");
                    }
                    return "-";
                 }).join("");
            }

            setLinks(dataJson) {
                var toreturn = "<br><br>Data is under CC BY-SA and has been extracted from: ";
                toreturn += dataJson[0].links.value.split(",")
                    .map(function(url) {
                        var label;
                        if (url.startsWith("https://en.wiktionary.org/wiki/Reconstruction")) {
                            label = url.replace(/https:\/\/en.wiktionary.org\/wiki\/Reconstruction:/g, "")
                                .split("/")
                                .join(" ");
                        } else {
                            label = url.split("/")
                                .pop()
                                .split("#")
                                .reverse()
                                .join(" ")
                                .replace(/_/g, " ");
                        }
                        return etyBase.helpers.htmlLink(url, label);
                    }).join(", ");
                return toreturn;
            }		
        }

        class GraphNode {
            constructor(i) {		
                this.counter = i;
                this.iri = [];
                this.isAncestor = false;

		//set radius of node
		this.rx = this.ry = 25;
            }
        }

        class Graph {
            constructor() {
                this.nodes = {};
                this.graphNodes = {};
                this.graphEdges = [];
                this.dagre = new dagreD3.graphlib.Graph().setGraph({ rankdir: "LR" });
		this.languages = [];
            }

	    setLanguages() {
		for (var i in this.graphNodes) {
		    if (undefined !== this.graphNodes[i].lang) {
			this.languages.push(this.graphNodes[i].lang);
		    }
		}
		this.languages = this.languages.filter(etyBase.helpers.onlyUnique);
	    }

	    //given this.nodes assign this.nodes[i].graphNode to each node i using function 
	    //this.setGraphNodeProperty()
	    //and then define this.graphNodes
	    setGraphNodes() {//used by descendantsgraph and ancestors graph
                var that = this;
                that.setGraphNodeProperty();
                for (var n in that.nodes) {
                    var gn = that.nodes[n].graphNode;
                    if (undefined === that.graphNodes[gn]) {
                        var gg = new GraphNode(gn);
                        gg.counter = gn;
                        gg.iri = that.nodes[n].eqIri;
                        gg.iso = that.nodes[n].iso;
                        gg.label = gg.iri.map(function(i) {
                                return that.nodes[i].label;
                            })
                            .filter(etyBase.helpers.onlyUnique)
                            .join(",");
                        gg.lang = that.nodes[n].lang;
                        gg.isAncestor = that.nodes[n].isAncestor;
                        that.graphNodes[gn] = gg;
                    }
                }
            }

	    //assign this.nodes[i].graphNode for each node i
	    setGraphNodeProperty() {
                var that = this;
                //CONSTRUCTING GRAPHNODES
                //a graphNode merges Nodes that are etymologically equivalent 
                //or that refer to the same word 
                //MERGE NODES THAT HAVE THE SAME iso AND label BUT A DIFFERENT ety 
                //(e.g.: if only ee_word and ee_n_word with n an integer belong to 
                //the set of ancestors and descendants 
                //then merge them into one graphNode)
                //the final graph will use these super nodes (graphNodes)
                var counter = 0; //counts how many graphNodes have been created so far 
                for (var n in that.nodes) {
                    if (that.nodes[n].ety === 0) {
                        //count how many nodes share the same iso and label
                        var iso = that.nodes[n].iso;
                        var label = that.nodes[n].label;
                        var tmp = [];
                        for (var m in that.nodes) {
                            if (undefined !== that.nodes[m]) {
                                if (that.nodes[m].iso === iso && that.nodes[m].label === label && that.nodes[m].ety > 0) {
                                    tmp.push(m);
                                }
                            }
                        }
                        tmp = tmp.filter(etyBase.helpers.onlyUnique);
                        //if only nodes[ee_word] and nodes[ee_ety_word] exist (with ety an integer > 0)
                        //then merge them in one graphNode 
                        if (tmp.length === 1) {
                            //define node.graphNode 
                            var eqIri = that.nodes[n].eqIri
                                .concat(that.nodes[tmp[0]].eqIri)
                                .filter(etyBase.helpers.onlyUnique)
                                .reduce(function(eq, element) {
                                        eq = eq.concat(that.nodes[element].eqIri).filter(etyBase.helpers.onlyUnique);
                                        return eq;
                                    }, []);

                            var graphNode = eqIri.reduce(function(gn, element) {
                                    if (undefined === that.nodes[element].graphNode) {
                                        return gn;
                                    } else {
                                        gn.push(that.nodes[element].graphNode);
                                        return gn;
                                    }
                                }, []).filter(etyBase.helpers.onlyUnique).sort();
                            if (graphNode.length === 0) {
                                graphNode = counter;
                                counter++;
                            } else {
                                graphNode = graphNode[0];
                            }

                            eqIri.forEach(function(element) {
                                    that.nodes[element].eqIri = eqIri;
                                    that.nodes[element].graphNode = graphNode;
                                });

                            counter++;
                        }
                    }
                }

                //MERGE NODES IN eqIri
                for (var n in that.nodes) {
                    if (undefined === that.nodes[n].graphNode) {
                        var eqIri = that.nodes[n].eqIri;
                        eqIri = eqIri.reduce(function(eq, element) {
				eq = eq.concat(that.nodes[element].eqIri).filter(etyBase.helpers.onlyUnique);
				return eq;
			    }, []);
                        var graphNode = eqIri.reduce(function(gn, element) {
				if (undefined === that.nodes[element].graphNode) {
				    return gn;
				} else {
				    gn.push(that.nodes[element].graphNode);
				    return gn;
				}
			    }, []).filter(etyBase.helpers.onlyUnique).sort();
                        if (graphNode.length === 0) {
                            graphNode = counter;
                            counter ++;
                        } else {
                            graphNode = graphNode[0];
                        }

                        eqIri.forEach(function(element) {
				that.nodes[element].eqIri = eqIri;
				that.nodes[element].graphNode = graphNode;
			    });

                        counter ++;
                    }
                }
            }

	    //given this.graphNodes and this.graphEdges
	    //define this.dagre nodes and edges
	    //and assign this.graphEdges[e].style to the edges
            setGraphEdges() {
		//group nodes by language and place them in columns of length 150
		this.setLanguages();
		var style = {label: "",
			style: "stroke: none",
			lineInterpolate: "basis",
			arrowheadStyle: "fill: none",
			}
		var m = null, col = 1;
		var nCol = Math.max(Math.floor(window.innerWidth/150), 2); 
		
		for (var l in this.languages) {
		    for (var n in this.graphNodes) {
			if (this.graphNodes[n].lang === this.languages[l]) {
			    if (m !== null) {
				this.dagre.setEdge(m, n, style);
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

	    setDagre() {
		this.setGraphNodes();
		for (var n in this.graphNodes) {
		    this.dagre.setNode(n, this.graphNodes[n]);
		}
		this.setGraphEdges();
	    }

	    //draw this.dagre in the element selected by selector
	    //and call the svg element id
	    //fit dagre to screen 
            renderDagre(selector, id) {
                var that = this;

		var svg = d3.select(selector).append("svg")
                    .attr("id", id)
		    .attr("width", window.innerWidth)
		    .attr("height", window.innerHeight - $("#header").height());
                
                var inner = svg.append("g");

                // Set up zoom support                      
                var zoom = d3.behavior.zoom().on("zoom", function() {
                    inner.attr("transform", "translate(" + d3.event.translate + ")" +
                        "scale(" + d3.event.scale + ")");
                });
                svg.call(zoom);
                
                // Create the renderer          
                var render = new dagreD3.render();
                
                // Run the renderer. This is what draws the final graph.  
                render(inner, that.dagre);

                // Center the graph
		var width = window.innerWidth;
		var graphWidth = that.dagre.graph().width;
		var zoomScale = (graphWidth > width) ? (0.8 * Math.max(width / graphWidth, 0.2)) : 0.75;
		    
		zoom.translate([(window.innerWidth - that.dagre.graph().width * zoomScale) / 2, 20])
		    .scale(zoomScale)
		    .event(svg);
                
	        // Decorate graph
	        inner.selectAll("g.node > rect")
	            .attr("class", "word");
	        //show tooltip on mouseover nodes 
                inner.selectAll(".word")
                    .on("mouseover", function(d) {
                        d3.selectAll(".tooltip").remove();
                        d3.select("#tooltipPopup")
                            .style("display", "inline")
                            .style("left", (d3.event.pageX + 38) + "px")
                            .style("top", (d3.event.pageY - 28) + "px");
                        var iri = that.dagre.node(d).iri;
                        if (typeof iri === "string") {
                            var label = that.dagre.node(d).label;
                            that.nodes[iri]
             		        .tooltipQuery()
			        .subscribe(text => {
			            d3.select("#tooltipPopup")
				        .append("p")
				        .attr("class", "tooltip") 
				        .html(text);
			        }, error => {
				    d3.select("#tooltipPopup")
                                        .append("p")
				        .attr("class", "tooltip")
				        .html("<b>" + label + "</b><br><br><br>-");
			        });
                        } else {
                            var tooltips = iri.reduce(function(t, i) {
                                var label = that.nodes[i].label;
                                if (t.labels.indexOf(label) === -1) {
                                    t.labels.push(label);
                                    t.text.push(that.nodes[i].tooltipQuery());
                                } 
                                return t;
                            }, { labels: [], text: [] });
                            Rx.Observable.zip.apply(this, tooltips.text)
			        .catch((err) => {
			            console.log(err); 
			            return Rx.Observable.empty();
			        }).subscribe(response => {
			            d3.select("#tooltipPopup")  
			                .append("p") 
				        .attr("class", "tooltip") 
				        .html(response.join("<br><br>"));
			        });
                        }
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
		//.attr("x", "0.8em")
		//  .attr("y", "2.2em")
                    .attr("width", function(d) {
                        return that.dagre.node(d).iso.length / 1.7 + "em";
                    })
		//  .attr("height", "1em")
		//  .attr("fill", "red")
		//  .attr("fill-opacity", 0)
                    .on("mouseover", function(d) {
                        d3.selectAll(".tooltip").remove();
                        d3.select("#tooltipPopup")
                            .style("display", "inline")
                            .style("left", (d3.event.pageX) + "px")
                            .style("top", (d3.event.pageY - 28) + "px")
                            .append("p")
                            .attr("class", "tooltip")
                            .html(that.dagre.node(d).lang);
                        d3.event.stopPropagation();
                    });

                //show tooltip on click on nodes                
                inner.selectAll("g.node")
                    .on("mouseover", function(d) {
                        d3.select("#tooltipPopup")
                            .style("display", "inline")
                            .style("left", (d3.event.pageX + 38) + "px")
                            .style("top", (d3.event.pageY - 28) + "px")
                            .html("");
                        var iri = that.dagre.node(d).iri;
                        if (typeof iri === "string") {
                            that.nodes[iri].tooltipQuery();
                        } else {
                            iri.reduce(function(obj, i) {
			        var label = that.nodes[i].label;
			        if (obj.labels.indexOf(label) === -1) {
				    obj.labels.push(label);
				    obj.iris.push(i);
				    return obj;
			        } else {
				    return obj;
			        }
                            }, 
                            { labels: [], iris: [] }).iris.forEach(function(i) { 
                                that.nodes[i].tooltipQuery(); 
                            });
                        }
                        d3.event.stopPropagation();
		    });
		return inner;
            }
        }

        class AncestorsGraph extends Graph {
            constructor(iri) {
                super();

		this.ancestors = [];
                this.lastAncestors = [];

                $("#message")
                    .css("display", "inline")
                    .html(etyBase.MESSAGE.loadingMore);
                d3.select("#tooltipPopup")
                    .attr("display", "none");
                $("#tree-overlay")
                    .remove();

                const params = new URLSearchParams();
                params.set("format", "application/sparql-results+json");
                var url = etyBase.config.urls.ENDPOINT + "?" + params;
                
                etyBase.DB.ancestorQuery(iri, 5)
                    .subscribe(ancestorResponse => {
                        this.setAncestors(iri, ancestorResponse);
			
                        var sources = this.lastAncestors.map(function(element) {
                                return etyBase.DB.ancestorQuery(element, 5);
                            });
                        var obs = (this.lastAncestors.length === 0) ? Rx.Observable.timer(1) : Rx.Observable.zip.apply(this, sources)
                            .catch((err) => {
                                d3.select("#message").html(etyBase.MESSAGE.serverError);

                                /* Return an empty Observable which gets collapsed in the output */
                                return Rx.Observable.empty();
                            });
                        obs.subscribe(moreAncestorsResponse => {
				if (0 !== moreAncestorsResponse) {
				    this.setAdditionalAncestors(moreAncestorsResponse);
				}
                                etyBase.DB.slicedQuery(this.ancestors, etyBase.DB.propertyQuery, 3) 
                                    .subscribe(propertyResponse => {
                                        //constructing etymologyNodes, graphNodes, graphEdges
		                        //etymologyNodes is the set of input etymology entries
		                        //graphNodes is the set of nodes in the full graph; a graphNode can correspond to multiple etymology entries (i.e. multiple elements in etymologyNodes) 
		                        this.setProperties(propertyResponse);
                                        this.setNodes();
					var keys = Object.keys(this.nodes);
					console.log(keys);
					if (keys.length === 0) {
					    var node = new Node(iri);
					    var link = "https://en.wiktionary.org/wiki/";
					    if (node.label.startsWith("*")) {
						link += "Reconstruction:" + node.lang + "/" + node.label.replace("*", "");
					    } else {
						link += node.label + "#" + node.lang;
					    }
					    link = etyBase.helpers.htmlLink(link, node.lang + " " + node.label);
					    $("#message")
                                                .css("display", "inline")
                                                .html(etyBase.MESSAGE.noEtymology(link));
					} else if (keys.length === 1) {
					    var link = "https://en.wiktionary.org/wiki/";
					    var lang = keys[0].lang;
					    var label = keys[0].label;
					    if (!label.startsWith("*")) {
						link = link + label + "#" + lang;
						console.log(link);
					    } else {
						console.log(label);
					    }
                                            $("#message")
                                                .css("display", "inline")
                                                .html(etyBase.MESSAGE.noEtymology(link));
                                        } else {
                                            $("#helpPopup")
					        .html(etyBase.HELP.dagre);
			                    this.setDagre();
                                            $("#message")
		                                .css("display", "none");
                                            var innerTree = this.renderDagre("#tree-container", "tree-overlay");
                                            var that = this;
                                            innerTree.selectAll("g.node")
					        .on("click", function(d) {             
                                                        var clickedNode = that.dagre.node(d);
                                                        var iri = clickedNode.iri[0];
                                                        
							//open dialog
							$("#descendants").remove();
                                                        d3.select("#tree-container").append("div").attr("id", "descendants");
							$("#descendants").dialog({
                                                                title: "descendants of " + clickedNode.lang + " " + clickedNode.label,
                                                                autoOpen: false,
								    //close:  function() { $("#descendants").remove(); },
							        width: $(window).width() - 15,
							        height: $(window).height() - 15,
								position: "top"
                                                            });
                                                        $("#descendants").dialog("open");

							//draw descendants in dialog
							etyBase.DB.getXMLHttpRequest(etyBase.config.urls.ENDPOINT + "?query=" + encodeURIComponent(etyBase.DB.descendantQuery(iri))).subscribe(response => {
								var descendantsGraph = new DescendantsGraph();
								descendantsGraph.setNodes(response);
								descendantsGraph.setGraphNodes();
								descendantsGraph.setLanguages();
								d3.select("#descendants").append("div").attr("id", "accordion").html(function() {
                                                                        return descendantsGraph.languages.map(l => {
										return"<h3>" + l + "</h3><div id=\"div" + l.replace(/ /g, "_").replace(/ *\([^)]*\) */g, "") + "\"></div>";
                                                                            }).join("");
                                                                    });
								
                                                                $("#accordion").accordion({
                                                                        collapsible: "true",
									activate: function(event, ui) {
									    var language = ui.newHeader.text();
									    descendantsGraph.renderDagreByLanguage(language);
									},
									active: false
                                                                    });
							    });
						    });
                                        }
			    },
                            error => etyBase.helpers.serverError(error),
                            () => etyBase.helpers.debugLog("done property query"));
                        },
                        error => etyBase.helpers.serverError(error),
                        () => etyBase.helpers.debugLog("done more ancestors query"));
                     },
                     error => etyBase.helpers.serverError(error),
                     () => etyBase.helpers.debugLog("done ancestor query"));
            }
	    
	    parseAncestors(response) {
                return response.reduce((all, a) => {
                        return all.concat(JSON.parse(a).results.bindings);
                    }, []).reduce((val, a) => {
                            val.all.push(a.ancestor1.value);
                            if (a.der1.value === "0" && undefined !== a.ancestor2 && etyBase.helpers.lemmaNotStartsOrEndsWithDash(a.ancestor1.value)) {
                                val.all.push(a.ancestor2.value);
                                if (a.der2.value === "0" && undefined !== a.ancestor3 && etyBase.helpers.lemmaNotStartsOrEndsWithDash(a.ancestor2.value)) {
                                    val.all.push(a.ancestor3.value);
                                    if (a.der3.value === "0" && undefined !== a.ancestor4 && etyBase.helpers.lemmaNotStartsOrEndsWithDash(a.ancestor3.value)) {
                                        val.all.push(a.ancestor4.value);
                                        if (a.der4.value === "0" && undefined !== a.ancestor5 && etyBase.helpers.lemmaNotStartsOrEndsWithDash(a.ancestor4.value)) {
                                            val.all.push(a.ancestor5.value);
                                            val.last.push(a.ancestor5.value);
                                        }
                                    }
                                }
                            }
                            return val;
                        }, { all: [], last: [] });
            }

            setAncestors(iri, response) {
                var that = this;
                var ancestors = that.parseAncestors(response);
                ancestors.all.push(iri);
                this.ancestors = ancestors.all.filter(etyBase.helpers.onlyUnique);
                this.lastAncestors = ancestors.last.filter(etyBase.helpers.onlyUnique);
            }

            //look for additional ancestors 
            setAdditionalAncestors(response) {
                var that = this;
                
		var ancestors = response.reduce(function(val, r) {
			return val.concat(that.parseAncestors(r).all);
		    },
		    []);
		this.ancestors = this.ancestors.concat(ancestors).filter(etyBase.helpers.onlyUnique);
            }

            setProperties(response) {
                this.properties = response.reduce((val, a) => {
                        val = val.concat(JSON.parse(a).results.bindings);
                        return val;
                    }, []);
            }

            setNodes() {
                if (this.properties.length < 2) {
                    return;
                } else {
                    var that = this;
                    //CONSTRUCTING NODES
                    that.properties.forEach(function(element) {
			    //save all nodes 
			    //define isAncestor
			    //push to eqIri 
			    if (undefined !== element.s && undefined === that.nodes[element.s.value]) {
				var label = (undefined === element.sLabel) ? undefined : element.sLabel.value;
				that.nodes[element.s.value] = new Node(element.s.value, label);
				//temporarily add nodes that are not ancestors
				if (that.ancestors.indexOf(element.s.value) === -1) {
				    that.nodes[element.s.value].temporary = true;
				}
			    }
			    if (undefined !== element.rel) {
				if (undefined === that.nodes[element.rel.value]) {
				    var label = (undefined === element.relLabel) ? undefined : element.relLabel.value;
				    that.nodes[element.rel.value] = new Node(element.rel.value, label);
				}
				if (that.ancestors.indexOf(element.rel.value) > -1) {
				    that.nodes[element.rel.value].isAncestor = true;
				}
			    }
			    if (undefined !== element.rel && undefined !== element.eq) {
				if (undefined === that.nodes[element.eq.value]) {
				    var label = (undefined === element.eqLabel) ? undefined : element.eqLabel.value;
				    that.nodes[element.eq.value] = new Node(element.eq.value, label);
				}
				if (element.rel.value !== element.eq.value) {
				    if (that.nodes[element.rel.value].eqIri.indexOf(element.eq.value) === -1) {
					that.nodes[element.rel.value].eqIri.push(element.eq.value);
				    }
				    if (that.nodes[element.eq.value].eqIri.indexOf(element.rel.value) === -1) {
					that.nodes[element.eq.value].eqIri.push(element.rel.value);
				    }
				}
			    }
			});
                }
            }

            setGraphEdges() {
                var that = this;
                that.properties.forEach(function(element) {
			if (undefined !== element.rel && undefined !== element.s){
			    if (undefined !== that.nodes[element.s.value] && undefined !== that.nodes[element.rel.value]) {
				var source = that.nodes[element.rel.value].graphNode, target = that.nodes[element.s.value].graphNode;
				if (source !== target) {
				    that.graphEdges.push({
					    source: source,
						target: target,
						style: {label: "",
						    lineInterpolate: "basis",
						    arrowheadStyle: "fill: steelblue"
						    }
					});
				}
			    }
			}
		    });
            }

            clean() { //remove temporary nodes
                var that = this;
                for (var n in that.nodes) {
                    if (that.nodes[n].temporary) {
                        var iso = that.nodes[n].iso;
                        var label = that.nodes[n].label;
                        for (var m in that.nodes) {
                            if (that.nodes[m].iso === iso && that.nodes[m].label === label) {
                                if (!that.nodes[m].temporary) {
                                    that.nodes[n].temporary = false;
                                }
                                if (that.nodes[m].isAncestor) {
                                    that.nodes[n].isAncestor = true;
                                }
                            }
                        }
                    }
                }

                for (var n in that.nodes) {
                    if (that.nodes[n].temporary) {
                        that.nodes[n].eqIri.forEach(function(e) {
                                if (!that.nodes[e].temporary) {
                                    that.nodes[n].temporary = false;
                                }
                                if (that.nodes[e].isAncestor) {
                                    that.nodes[n].isAncestor = true;
                                }
                            });
                    }
                }

                for (var n in that.nodes) {
                    if (!that.nodes[n].temporary) {
                        that.nodes[n].eqIri.forEach(function(e) {
                                that.nodes[e].temporary = false;
                            });
                    }
                }

                for (var n in that.nodes) {
                    if (that.nodes[n].temporary) {
                        delete that.nodes[n];
                    }
                }
	    }

            setDagre() {
                this.clean();
		this.setGraphNodes();
                this.setGraphEdges();
	        for (var n in this.graphNodes) {
                    if (this.graphNodes[n].isAncestor) {
		        this.dagre.setNode(n, this.graphNodes[n]);
                    }
		}
	        for (var e in this.graphEdges) {
                    var source = this.graphEdges[e].source, target = this.graphEdges[e].target;
                    if (this.graphNodes[source].isAncestor && this.graphNodes[target].isAncestor) {
	                this.dagre.setEdge(source, target, this.graphEdges[e].style);	
                    }
                }
            }
        }

	class DescendantsGraph extends Graph {
	    setNodes(response) {
		var that = this;
                JSON.parse(response).results.bindings.forEach(function(j) {
			if (undefined !== j.descendant1) {
			    that.nodes[j.descendant1.value] = new Node(j.descendant1.value, j.label1.value);
			    if (undefined != j.ee) {
				that.nodes[j.ee.value] = new Node(j.ee.value, j.labele.value);
				that.nodes[j.descendant1.value].eqIri.push(j.ee.value);
			    }
			}
		    });
            }
            
	    renderDagreByLanguage(language) {
		var languageGraph = new DescendantsGraph();
		languageGraph.nodes = this.nodes;
		for (var i in this.graphNodes) {
		    var iri = this.graphNodes[i].iri[0];
		    if (this.nodes[iri].lang === language) {
			var gn = this.nodes[iri].graphNode;
			languageGraph.graphNodes[gn] = this.graphNodes[gn];
			languageGraph.dagre.setNode(gn, this.graphNodes[gn]);
		    }
		}
		languageGraph.setGraphEdges();
		language = language.replace(/ /g, "_").replace(/ *\([^)]*\) */g, "");
	        
	        d3.select("#overlay" + language).remove();
	    
		var inner = languageGraph.renderDagre("#div" + language, "overlay" + language);
	    d3.select("#overlay" + language).attr("transform", "translate(" + (-50) + ")");
	    var h = Math.min(languageGraph.dagre.graph().height + 30, window.innerHeight - 15);
	    d3.select("#div" + language).attr("style", "height:" + h + "px;");
		inner.selectAll("g.node")
		    .on("click", function(d) {
			$("#descendants").dialog("close");
			    
			var iri = languageGraph.dagre.node(d).iri[0];
			new AncestorsGraph(iri);
		    });
		return languageGraph;
	    }
	}

        class DisambiguationGraph extends Graph {
            constructor(lemma) {
                super();

	        if (lemma.length < 2) {
		    return;
	        }

                //clean screen
                $("#tree-overlay")
                    .remove();
                d3.select("#tooltipPopup")
                    .style("display", "none");

                //query database
                var url = etyBase.config.urls.ENDPOINT + "?query=" + encodeURIComponent(etyBase.DB.disambiguationQuery(lemma));
                etyBase.DB.getXMLHttpRequest(url).subscribe(response => {
                    this.setNodes(response);
                    if (Object.keys(this.nodes).length === 0) {
                        $("#message")
                            .css("display", "inline")
                            .html(etyBase.MESSAGE.notAvailable);
                    } else if (Object.keys(this.nodes).length === 1) {
                        var iri = Object.keys(this.nodes)[0];
                        new AncestorsGraph(iri);
                    } else {
                        $("#helpPopup")
                            .html(etyBase.HELP.disambiguation);
                        $("#message")
                            .css("display", "inline")
                            .html(etyBase.MESSAGE.disambiguation);
			this.setDagre();
                        var innerDisambiguation = this.renderDagre("#tree-container", "tree-overlay");
                        var that = this;
			innerDisambiguation.selectAll("g.node")
                            .on("click", function(d) {
                                var iri = that.dagre.node(d).iri[0];
                                new AncestorsGraph(iri);
                            });
                    }
                },
                error => $("#message")
                             .css("display", "inline")
                             .html("Server error. " + error),
                () => etyBase.helpers.debugLog("done disambiguation"));
            }

            setNodes(response) {
                var that = this;
                JSON.parse(response).results.bindings.forEach(function(n) {
                    if (n.et.value === "" || n.et.value.split(",").length > 1) {
                        that.nodes[n.iri.value] = new Node(n.iri.value, n.lemma.value);
                    }
		    });
            }
        }

        var init = function() {

            $("#helpPopup")
                .html(etyBase.HELP.intro);

            var div = d3.select("body").append("div")
                .attr("data-role", "popup")
                .attr("data-dismissible", "true")
                .attr("id", "tooltipPopup")
                .style("display", "none")
                .attr("class", "ui-content tooltipDiv");

            $(window).click(function() {
                d3.select("#tooltipPopup")
                    .style("display", "none");
            });

            $("#tooltipPopup").click(function(e) {
                e.stopPropagation();
            });

            $("#search").on("keypress", function(e) {
                var search = this;
                if (e.which === 13) {
                    var lemma = $(search).val();
		    new DisambiguationGraph(lemma);
		}
	    });

	    $("#btnSearch").click(function(e) {
		var lemma = $("#search").val();
		new DisambiguationGraph(lemma);
            });

        };

        this.init = init;

        etyBase[moduleName] = this;
    };

    return module;

})(GRAPH || {});
