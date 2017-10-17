/*globals
    $, d3, console, dagreD3, Rx, window, document, URLSearchParams
*/
/*jshint loopfunc: true, shadow: true, latedef: false */ // Consider removing this and fixing these
var GRAPH = (function(module) {

    module.bindModule = function(base, moduleName) {
        var etyBase = base;

        var serverError = function(error) {
            console.error(error);

            $('#message')
                .css('display', 'inline')
                .html(etyBase.LOAD.MESSAGE.serverError);
        };

        var notAvailable = function(error) {
            console.error(error);

            $('#tree-overlay')
                .remove();
            d3.select("#tooltipPopup")
                .style("display", "none");
            $('#message')
                .css('display', 'inline')
                .html(etyBase.LOAD.MESSAGE.notAvailable);
        };

        var constructDisambiguationGraph = function(lemma) {
            //clean screen
            $('#tree-overlay')
                .remove();
            d3.select("#tooltipPopup")
                .style("display", "none");

            //query database
            var url = etyBase.config.urls.ENDPOINT + "?query=" + encodeURIComponent(etyBase.DB.disambiguationQuery(lemma));
            
            etyBase.helpers.debugLog("disambiguation query = " + url);

            etyBase.DB.getXMLHttpRequest(url).subscribe(
                response => {
                    var nodes = parseDisambiguationNodes(response);
                    etyBase.helpers.debugLog(nodes);
                    if (Object.keys(nodes).length === 0) {
                        $('#message')
                            .css('display', 'inline')
                            .html(etyBase.LOAD.MESSAGE.notAvailable);
                    } else if (Object.keys(nodes).length === 1) {
                        var iri = Object.keys(nodes)[0];
                        constructEtymologyGraph(iri);
                    } else {
                        $('#helpPopup')
                            .html(etyBase.LOAD.HELP.disambiguation);
                        $('#message')
                            .css('display', 'inline')
                            .html(etyBase.LOAD.MESSAGE.disambiguation);
                        var g = defineDisambiguationGraph(nodes);
                        renderGraph(g, nodes).selectAll("g.node")
                            .on("click", function(d) {
                                var iri = g.node(d).iri;
                                constructEtymologyGraph(iri);
                            });
                    }
                },
                error => {
                    $('#message')
                        .css('display', 'inline')
                        .html("Server error. " + error);
                },
                () => {
                    
                    etyBase.helpers.debugLog('done disambiguation');

                });
        };

        var parseDisambiguationNodes = function(response) {
            var nodes = {};
            var disambiguationArray = JSON.parse(response).results.bindings;

            //define nodes 
            disambiguationArray.forEach(function(n) {
                if (n.et.value === "" || n.et.value.split(",").length > 1) {
                    nodes[n.iri.value] = new etyBase.LOAD.classes.Node(n.iri.value, n.lemma.value);
                }
            });

	    return nodes;
	};

	var defineDisambiguationGraph = function(nodes) {
	    var g = new dagreD3.graphlib.Graph().setGraph({});
	    //add nodes and links to the graph                                                                                                                                                            
	    var m = null;
	    for (var n in nodes) {
		g.setNode(n, nodes[n]);
		if (null !== m) {
		    g.setEdge(n, m, { label: "", style: "stroke-width: 0" });
		}
		m = n;
	    }
	    return g;
	};

        var parseAncestors = function(response) {
            var ancestorArray = response.reduce((all, a) => {
                    return all.concat(JSON.parse(a).results.bindings);
                }, [])
                .reduce((ancestors, a) => {
                    ancestors.push(a.ancestor1.value);
                    if (a.der1.value === "0" && undefined !== a.ancestor2 && lemmaNotStartsOrEndsWithDash(a.ancestor1.value)) {
                        ancestors.push(a.ancestor2.value);
                        if (a.der2.value === "0" && undefined !== a.ancestor3 && lemmaNotStartsOrEndsWithDash(a.ancestor2.value)) {
                            ancestors.push(a.ancestor3.value);
                            if (a.der3.value === "0" && undefined !== a.ancestor4 && lemmaNotStartsOrEndsWithDash(a.ancestor3.value)) {
                                ancestors.push(a.ancestor4.value);
                                if (a.der4.value === "0" && undefined !== a.ancestor5 && lemmaNotStartsOrEndsWithDash(a.ancestor4.value)) {
                                    ancestors.push(a.ancestor5.value);
                                }
                            }
                        }
                    }
                    return ancestors;
                }, []).filter(etyBase.helpers.onlyUnique);
            etyBase.helpers.debugLog("ancestors");
            etyBase.helpers.debugLog(ancestorArray);
            return ancestorArray;
        };

        var parseDescendants = function(ancestorArray, response) {
            var descendantArray = response
                .reduce(
                    (descendants, d) => {
                        descendants = descendants.concat(JSON.parse(d).results.bindings.map(function(t) { return t.descendant1.value; }));
                        return descendants;
                    }, []).filter(etyBase.helpers.onlyUnique);
            return ancestorArray.concat(descendantArray).filter(etyBase.helpers.onlyUnique);
        };

        //if the searched word is derived from 2 or more words, return false
        //i.e. do not search for descendants
        //In this case the searched word is a compound word
        //we are not interested in descendants of the words that make up the searched word
        var doFindDescendants = function(response) {
            var firstAncestorArray = response.reduce((all, a) => {
                    return all.concat(JSON.parse(a).results.bindings);
                }, [])
                .reduce((ancestors, a) => {
                    if (a.der1.value === "1") {
                        ancestors.push(a.ancestor1.value);
                    }
                    return ancestors;
                }, []).filter(etyBase.helpers.onlyUnique);
            if (firstAncestorArray.length > 1)
                return false;
            else
                return true;
        };

        var lemmaNotStartsOrEndsWithDash = function(iri) {
            var tmp,
                label;
            tmp = iri.replace("http://etytree-virtuoso.wmflabs.org/dbnary/eng/", "")
                .split("/");
            if (tmp.length > 1) {
                label = tmp[1];
            } else {
                label = tmp[0];
            }
            label = label.replace(/__ee_[0-9]+_/g, "")
                .replace("__ee_", "");

            if (label.startsWith("-") || label.startsWith("_-") || label.endsWith("-")) {
                return false;
            } else {
                return true;
            }
        };

	var defineEtymologyNodes = function(nodes) {
	    var graphNodes = {};
	    for (var n in nodes) {
		var gn = nodes[n].graphNode;
		if (undefined === graphNodes[gn]) {
		    var gg = new etyBase.LOAD.classes.GraphNode(gn);
		    gg.counter = gn;
		    gg.iri = nodes[n].eqIri;
		    gg.iso = nodes[n].iso;
		    gg.label = gg.iri.map(function(i) {
			    return nodes[i].label;
			})
			.filter(etyBase.helpers.onlyUnique)
			.join(",");
		    gg.lang = nodes[n].lang;
		    gg.isAncestor = nodes[n].isAncestor;
		    if (gg.isAncestor) {
			gg.style = "fill: #F0E68C; stroke: black";
		    } else {
			gg.style = "fill: lightBlue; stroke: black";
		    }
		    graphNodes[gn] = gg;
		    
		    //                        g.setNode(gn, gg);    
                }
            }
	    return graphNodes;
	} 
	    

	var defineEtymologyLinks = function(nodes, graphNodes, propertyArray) {
            var graphLinks = [];
	    propertyArray.forEach(function(element) {
		    if (undefined !== element.rel && undefined !== element.s) {
			if (undefined !== nodes[element.s.value]) {
			    var source = nodes[element.rel.value].graphNode,
				target = nodes[element.s.value].graphNode;
			    if (source !== target) {
				if (graphNodes[source].isAncestor && graphNodes[target].isAncestor) {
				    graphLinks.push({source: source, target: target, color: "black"});
				} else {
				    graphLinks.push({source: source, target: target, color: "lightBlue"});
				}
			    }
			}
		    }
		});

	    //todo: add links between ancestors                                                      
	    etyBase.helpers.debugLog("nodes");
	    etyBase.helpers.debugLog(nodes);
	    etyBase.helpers.debugLog("graphLinks");
	    etyBase.helpers.debugLog(graphLinks);
	    return graphLinks;
	};

        var constructEtymologyGraph = function(iri) {
            $('#message')
                .css('display', 'inline')
                .html(etyBase.LOAD.MESSAGE.loadingMore);
            d3.select("#tooltipPopup")
                .attr("display", "none");
            $('#tree-overlay')
                .remove();

            const params = new URLSearchParams();
            params.set("format", "application/sparql-results+json");
            var url = etyBase.config.urls.ENDPOINT + "?" + params;

            etyBase.DB.ancestorQuery(iri, 5)
                .subscribe(
                    ancestorResponse => {
                        var ancestorArray = parseAncestors(ancestorResponse);
                        ancestorArray.push(iri);
                        //filteredAncestorArray is ancestorArray without words that start or end with dash
                        //we will find descendants of filteredAncestorArray, thus excluding descendants of words that start or end with dash
                        var filteredAncestorArray = ancestorArray.filter(function(element) { return lemmaNotStartsOrEndsWithDash(element); });
console.log("filteredAncestorArray")
console.log(filteredAncestorArray)
//                        if (doFindDescendants(ancestorResponse)) {
                            etyBase.DB.slicedQuery(filteredAncestorArray, etyBase.DB.descendantQuery, 8)
                                .subscribe(
                                    descendantResponse => {
                                        var allArray = parseDescendants(ancestorArray, descendantResponse);
					console.log("descendants");
					console.log(allArray);
                                        etyBase.DB.slicedQuery(allArray, etyBase.DB.propertyQuery, 3)
                                            .subscribe(
                                                propertyResponse => {
                                                    var propertyArray = parseProperty(propertyResponse);
                                                    var nodes = parseEtymologyNodes(ancestorArray, allArray, propertyArray);
                                                    if (Object.keys(nodes).length === 0) {
                                                        $('#message')
                                                            .css('display', 'inline')
                                                            .html(etyBase.LOAD.MESSAGE.noEtymology);
                                                    } else {
                                                        $('#helpPopup').html(etyBase.LOAD.HELP.dagre);
							var graphNodes = defineEtymologyNodes(nodes);
							var graphLinks = defineEtymologyLinks(nodes, graphNodes, propertyArray);
							var g = new dagreD3.graphlib.Graph().setGraph({ rankdir: 'LR' });
							for (var n in graphNodes) {
							    g.setNode(n, graphNodes[n]);
							}
							for (var l in graphLinks) {
							    g.setEdge(graphLinks[l].source, graphLinks[l].target, { label: "", lineInterpolate: "basis", style: "stroke: " + graphLinks[l].color + "; fill: none; stroke-width: 0.1em;", arrowheadStyle: "fill: " + graphLinks[l].color });
							}
					
                                                        var inner = renderGraph(g, nodes);
							inner.selectAll("g.node")
							    .on("click", function(d) {
								    toggleNode(inner, g, nodes, graphNodes, graphLinks, d);
							    });
                                                    }
                                                },
                                                error => serverError(error),
                                                () => {
                                                    etyBase.helpers.debugLog('done property query');
                                                });
                                    },
                                    error => serverError(error),
                                    () => {
                                        etyBase.helpers.debugLog('done descendants query');
                                    });
/*                        } else {
                            etyBase.DB.slicedQuery(ancestorArray, etyBase.DB.propertyQuery, 3)
                                .subscribe(
                                    propertyResponse => {
                                        parseProperty(ancestorArray, ancestorResponse, propertyResponse);
                                        var g = parseEtymologyNodes(ancestorArray, ancestorResponse, propertyResponse);

                                        if (Object.keys(nodes).length === 0) {
                                            $('#message')
                                                .css('display', 'inline')
                                                .html(etyBase.LOAD.MESSAGE.noEtymology);
                                        } else {
                                            $('#helpPopup').html(etyBase.LOAD.HELP.dagre);
                                            setGraph(g);
                                            renderGraph();
                                        }
                                    },
                                    error => serverError(error),
                                    () => {
                                        etyBase.helpers.debugLog('done property query');
                                    });
                        }*/
                    },
                    error => serverError(error),
                    () => {
                        etyBase.helpers.debugLog('done ancestor query');
                    });
        };

        var parseProperty = function(propertyResponse) {
            return propertyResponse.reduce((all, a) => {
                all = all.concat(JSON.parse(a).results.bindings);
                return all;
            }, []);
        };

        var toggleNode = function(inner, g, nodes, graphNodes, graphLinks, d) {
	    var render = new dagreD3.render();
	    graphNodes[d].childrenHidden = !graphNodes[d].childrenHidden;
            var numChildren = Object.keys(g._out[d]).length;
	    if (numChildren > 0) {
		var color = graphNodes[d].isAncestor? "#F0E68C" : "lightBlue";
		var greenColor = graphNodes[d].isAncestor? "greenYellow" : "limeGreen";
		var style = graphNodes[d].childrenHidden ? "fill: " + greenColor + "; stroke: black" : "fill: " + color + "; stroke: black";
		graphNodes[d].style = style;
	    }
	    var toggleChildNodes = function(d) {
		for (var i in g._out[d]) {
		    var childNode = g._out[d][i].w;
		    var hide = true;
		    for (var key in g._in[childNode]) {
			var parent = g._in[childNode][key].v;
			if (graphNodes[parent].childrenHidden === false) {
			    hide = false;
			}
		    }
		    graphNodes[childNode].childrenHidden = hide;
		    graphNodes[childNode].hidden = hide;

		    //graphNodes[childNode].childrenHidden = hide;
                    toggleChildNodes(childNode); 
		}
	    }
	    toggleChildNodes(d);
	    var gToggle = new dagreD3.graphlib.Graph().setGraph({ rankdir: 'LR' });
	    for (var n in graphNodes) {
		if (false === graphNodes[n].hidden) {
		    gToggle.setNode(n, graphNodes[n]);
		}
	    }
	    for (var l in graphLinks) {
	        if (graphNodes[graphLinks[l].source].hidden === false && graphNodes[graphLinks[l].target].hidden === false) {
		    gToggle.setEdge(graphLinks[l].source, 
				    graphLinks[l].target, 
	                            { label: "", lineInterpolate: "basis", style: "stroke: " + graphLinks[l].color + "; fill: none; stroke-width: 0.1em;", arrowheadStyle: "fill: " + graphLinks[l].color});
		}
	    }
	    
	    gToggle.graph.transition = function(selection) {
		return selection.transition().duration(500);
	    }
	    render(inner, gToggle);
	};

        //todo: filter derived terms if (they are leaves && they are not ancestors)
        //todo: iterate this step
        //returns nodes
        var parseEtymologyNodes = function(ancestors, allArray, propertyArray) {
            var nodes = {};
            if (propertyArray.length > 1) {
                //CONSTRUCTING NODES
                propertyArray.forEach(function(element) {
                    //save all nodes        
                    //define isAncestor
                    //push to eqIri
		    //if s is not in ancestors or descendants don't add it to the graph and therefore do not add its links 
                    if (undefined !== element.s && undefined === nodes[element.s.value] && allArray.indexOf(element.s.value) > -1) {
                        var label = (undefined === element.sLabel) ? undefined : element.sLabel.value;
                        nodes[element.s.value] = new etyBase.LOAD.classes.Node(element.s.value, label);
                    }
                    if (undefined !== element.rel) {
                        if (undefined === nodes[element.rel.value]) {
                            var label = (undefined === element.relLabel) ? undefined : element.relLabel.value;
                            nodes[element.rel.value] = new etyBase.LOAD.classes.Node(element.rel.value, label);
                        }
                        if (ancestors.indexOf(element.rel.value) > -1) {
                            nodes[element.rel.value].isAncestor = true;
                        }
                    }
                    if (undefined !== element.rel && undefined !== element.eq) {
                        if (undefined === nodes[element.eq.value]) {
                            var label = (undefined === element.eqLabel) ? undefined : element.eqLabel.value;
                            nodes[element.eq.value] = new etyBase.LOAD.classes.Node(element.eq.value, label);
                        }
                        if (element.rel.value !== element.eq.value) {
                            if (nodes[element.rel.value].eqIri.indexOf(element.eq.value) === -1) {
                                nodes[element.rel.value].eqIri.push(element.eq.value);
                            }
                            if (nodes[element.eq.value].eqIri.indexOf(element.rel.value) === -1) {
                                nodes[element.eq.value].eqIri.push(element.rel.value);
                            }
                        }
                    }
                });
                //CONSTRUCTING GRAPHNODES
                //a graphNode is some kind of super node that merges Nodes that are etymologically equivalent
                //or that refer to the same word - also called here identical Nodes 
                //(e.g.: if only ee_word and ee_n_word with n an integer belong to
                //the set of ancestors and descendants           
                //then merge them into one graphNode) 
                //the final graph will use these super nodes (graphNodes)
                var counter = 0; //counts how many graphNodes have been created so far
                for (var n in nodes) {
                    if (nodes[n].ety === 0) {
                        var iso = nodes[n].iso;
                        var label = nodes[n].label;
                        var tmp = [];
                        for (var m in nodes) {
                            if (undefined !== nodes[m]) {
                                if (nodes[m].iso === iso && nodes[m].label === label) {
                                    if (nodes[m].ety > 0) {
                                        tmp.push(m);
                                    }
                                }
                            }
                        }
                        tmp = tmp.filter(etyBase.helpers.onlyUnique);
                        //if only ee_word and ee_n_word with n an integer belong to
                        //the set of ancestors and descendants
                        //then merge them in one graphNode
                        if (tmp.length === 1) {
                            //        var gg = new etyBase.LOAD.classes.GraphNode(counter);

                            //define node.graphNode
                            var eqIri = nodes[n].eqIri.concat(nodes[tmp[0]].eqIri).filter(etyBase.helpers.onlyUnique);

                            eqIri = eqIri.reduce(function(eq, element) {
                                eq = eq.concat(nodes[element].eqIri).filter(etyBase.helpers.onlyUnique);
                                return eq;
                            }, []);
                            var graphNode = eqIri.reduce(function(gn, element) {
                                if (undefined === nodes[element].graphNode) {
                                    return gn;
                                } else {
                                    gn.push(nodes[element].graphNode);
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
                                nodes[element].eqIri = eqIri;
                                nodes[element].graphNode = graphNode;
                            });

                            counter++;
                        }
                    }
                }

                for (var n in nodes) {
                    if (undefined === nodes[n].graphNode) {
                        var eqIri = nodes[n].eqIri;
                        eqIri = eqIri.reduce(function(eq, element) {
                            eq = eq.concat(nodes[element].eqIri).filter(etyBase.helpers.onlyUnique);
                            return eq;
                        }, []);
                        var graphNode = eqIri.reduce(function(gn, element) {
                            if (undefined === nodes[element].graphNode) {
                                return gn;
                            } else {
                                gn.push(nodes[element].graphNode);
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
                            nodes[element].eqIri = eqIri;
                            nodes[element].graphNode = graphNode;
                        });

                        counter++;
                    }
                }
	    }
	    return nodes;
	};

        var setGraph = function(g) {
            etyBase.tree.graph = g;
        };

        var renderGraph = function(g, nodes) {
	    $('#message')
	        .css('display', 'none');
            var svg = d3.select("#tree-container").append("svg")
                .attr("id", "tree-overlay")
                .attr("width", window.innerWidth)
                .attr("height", window.innerHeight - $('#header').height());

            var inner = svg.append("g");

            // Set up zoom support                      
            var zoom = d3.behavior.zoom().on("zoom", function() {
                inner.attr("transform", "translate(" + d3.event.translate + ")" +
                    "scale(" + d3.event.scale + ")");
            });
            svg.call(zoom); //.on("dblclick.zoom", null);

            // Create the renderer          
            var render = new dagreD3.render();

            // Run the renderer. This is what draws the final graph.  
            render(inner, g);

            // Center the graph       
            var initialScale = 0.75;
            zoom.translate([(window.innerWidth - g.graph().width * initialScale) / 2, 20])
                .scale(initialScale)
                .event(svg);
          
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
                    var iri = g.node(d).iri;
                    if (typeof iri === "string") {
                        nodes[iri]
             		    .logTooltip()
			    .subscribe(text => {
			        d3.select("#tooltipPopup")
				    .append("p")
	                            .attr("class", "tooltip") 
			            .html(text);
			    }, error => {
				d3.select("#tooltipPopup")
                                    .append("p")
				    .attr("class", "tooltip")
				    .html("<b>" + that.label + "</b><br><br><br>-");
			    });
                    } else {
                        var tooltips = iri.reduce(function(obj, i) {
                            var label = nodes[i].label;
                            if (obj.labels.indexOf(label) === -1) {
                                obj.labels.push(label);
                                obj.text.push(nodes[i].logTooltip());
                                return obj;
                            } else {
                                return obj;
                            }
                        }, { labels: [], text: [] });
                        var obs = Rx.Observable.zip
			    .apply(this, tooltips.text)
			    .catch((err) => {
			        console.log(err); 
				return Rx.Observable.empty();
			    });
			obs.subscribe(res => {
			    d3.select("#tooltipPopup")  
			        .append("p") 
				.attr("class", "tooltip") 
				.html(res.join("<br><br>"));
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
                    return g.node(d).iso;
                });

            //show tooltip on mouseover language tag   
            inner.selectAll("g.node")
                .append("rect")
                .attr("x", "0.8em")
                .attr("y", "2.2em")
                .attr("width", function(d) {
                    return g.node(d).iso.length / 1.7 + "em";
                })
                .attr("height", "1em")
                .attr("fill", "red")
                .attr("fill-opacity", 0)
                .on("mouseover", function(d) {
                    d3.selectAll(".tooltip").remove();
                    d3.select("#tooltipPopup")
                        .style("display", "inline")
                        .style("left", (d3.event.pageX) + "px")
                        .style("top", (d3.event.pageY - 28) + "px")
                        .append("p")
                        .attr("class", "tooltip")
                        .html(g.node(d).lang);
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
                    var iri = g.node(d).iri;
                    if (typeof iri === "string") {
                        nodes[iri].logTooltip();
                    } else {
                        iri.reduce(function(obj, i) {
			    var label = nodes[i].label;
			    if (obj.labels.indexOf(label) === -1) {
				obj.labels.push(label);
				obj.iris.push(i);
				return obj;
			    } else {
				return obj;
			    }
                        }, { labels: [], iris: [] }).iris.forEach(function(i) { nodes[i].logTooltip(); });
                    }
                    d3.event.stopPropagation();
		});          
            //svg.attr("height", g.graph().height * initialScale + 40);}}
            return inner;
        };

        var init = function() {

            $('#helpPopup')
                .html(etyBase.LOAD.HELP.intro);

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

            $('#tooltipPopup').click(function(event) {
                event.stopPropagation();
            });

            $('#tags').on("keypress click", function(e) {
                var tag = this;
                if (e.which === 13 || e.type === 'click') {
                    var lemma = $(tag).val();

                    if (lemma) {
                        var width = window.innerWidth,
                            height = $(document).height() - $('#header').height();
                        constructDisambiguationGraph(lemma);
                    }
                }
            });


        };

        this.init = init;

        etyBase[moduleName] = this;
    };

    return module;

})(GRAPH || {});