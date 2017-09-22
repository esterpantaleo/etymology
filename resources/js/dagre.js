/*globals
    $, d3, console, dagreD3, Rx, window, document
*/
/*jshint loopfunc: true, shadow: true */ // Consider removing this and fixing these
var GRAPH = (function(module) {

    module.bindModule = function(base, moduleName) {
        var etyBase = base;

	var serverError = function(error) {
	    console.log(error);

	    $('#message')
                .css('display', 'inline')
                .html(etyBase.LOAD.MESSAGE.serverError);
	}

	var notAvailable = function(error) {
	    console.error(error);
            
	    $('#tree-overlay')
		.remove();
            d3.select("#tooltipPopup")
		.style("display", "none");
            $('#message')
		.css('display', 'inline')
		.html(etyBase.LOAD.MESSAGE.notAvailable);
	}

	var constructDisambiguationGraph = function(lemma) {
	    //clean screen
	    $('#tree-overlay')
                .remove();
            d3.select("#tooltipPopup")
                .style("display", "none");
	    
	    //query database
	    var url = etyBase.config.urls.ENDPOINT + "?query=" + encodeURIComponent(etyBase.DB.disambiguationQuery(lemma));
            if (etyBase.config.debug) {
                console.log("disambiguation query = " + url);
            }
            etyBase.DB.getXMLHttpRequest(url).subscribe(
                response => {
		    var g = parseDisambiguationNodes(response);
                    if (Object.keys(g.nodess).length === 0) {
                        $('#message')
                            .css('display', 'inline')
                            .html(etyBase.LOAD.MESSAGE.notAvailable);
                    } else if (Object.keys(g.nodess).length === 1) {
			var iri = Object.keys(g.nodess)[0];
                        constructEtymologyGraph(iri);
		    } else {
                        $('#helpPopup')
                            .html(etyBase.LOAD.HELP.disambiguation);
                        $('#message')
                            .css('display', 'inline')
                            .html(etyBase.LOAD.MESSAGE.disambiguation);
                        renderGraph(g).selectAll("g.node")
                            .on("click", function(d) {
                                var iri = g.node(d).iri;
				console.log(iri);
                                constructEtymologyGraph(iri);
                            })
                    } 
		},
		error => { 
		    $('#message')
			.css('display', 'inline')
			.html("Server error. " + error); 
		},
                () => { 
		    if (etyBase.config.debug) {
			console.log('done disambiguation');
		    }
		});
	};
		
        var parseDisambiguationNodes = function(response) {
	    var g = new dagreD3.graphlib.Graph().setGraph({});
	    g.nodess = {};
	    var disambiguationArray = JSON.parse(response).results.bindings;
	    
            //define nodes 
            disambiguationArray.forEach(function(n) {
                n.et.value.split(",")
                    .forEach(function(element) {
                        if (element !== "") {
                            g.nodess[element] = new etyBase.LOAD.classes.Node(element, n.lemma.value);
                        } else {
                            g.nodess[n.iri.value] = new etyBase.LOAD.classes.Node(n.iri.value, n.lemma.value);
                        }
                    });
            });

            //add nodes and links to the graph
            var m = null;
            for (var n in g.nodess) {
                g.setNode(n, g.nodess[n], { labelStyle: "font-size: 3em" });
                if (null !== m) {
                    g.setEdge(n, m, { label: "", style: "stroke-width: 0" });
                }
                m = n;
            }

	    if (etyBase.config.debug) {
                console.log(g.nodess);
            }
            return g;
        };

	var parseAncestors = function(response) {
	    var ancestorArray = response.reduce((all, a) => {
		return all.concat(JSON.parse(a).results.bindings);
	    }, [])
                .reduce((ancestors, a) => {
                    ancestors.push(a.ancestor1.value);
                    if (a.der1.value === "0" && undefined !== a.ancestor2) {
                        ancestors.push(a.ancestor2.value);
                        if (a.der2.value === "0" && undefined !== a.ancestor3){
                            ancestors.push(a.ancestor3.value); 
			    if (a.der3.value === "0" && undefined !== a.ancestor4){
				ancestors.push(a.ancestor4.value);
				if (a.der4.value === "0" && undefined !== a.ancestor5){
				    ancestors.push(a.ancestor5.value);
				}
			    }
                        }
                    }
                    return ancestors;
                }, []).filter(etyBase.helpers.onlyUnique);
	    console.log("ancestors");
	    console.log(ancestorArray);
	    return ancestorArray;
        };
	
	var parseDescendants = function(ancestorArray, response) {
	    var descendantArray = response
		.reduce(
		    (descendants, d) => {
			descendants = descendants.concat(JSON.parse(d).results.bindings.map(function(t) { return t.descendant1.value; }));
			return descendants;
		    }, []).filter(etyBase.helpers.onlyUnique);
	    console.log("descendants");
	    console.log(descendantArray);
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
	    tmp = iri.replace("http://etytree-virtuoso.wmflabs.org/dbnary/eng/", "")
	    .split("/");
	    if (tmp.length > 1) {
		label = tmp[1];
	    } else {
		label = tmp[0];
	    }
	    label = label.replace(/__ee_[0-9]+_/g, "")
                    .replace("__ee_", "");
	    console.log("label=" + label);
	    if (label.startsWith("-") || label.startsWith("_-") || label.endsWith("-")){
		return false;
	    } else {
		return true;
	    }
	};
	    
        var constructEtymologyGraph = function(iri) {
            $('#message')
		.css('display', 'inline')
		.html(etyBase.LOAD.MESSAGE.loadingMore);
            d3.select("#tooltipPopup")
		.attr("display", "none");
            $('#tree-overlay')
		.remove();
	    d3.select("#tooltipPopup")
                .attr("display", "none");

	    const params = new URLSearchParams();
	    params.set("format", "application/sparql-results+json");
	    var url = etyBase.config.urls.ENDPOINT + "?" + params;
	    
	    etyBase.DB.ancestorQuery(iri, 5)
		.subscribe(
		    ancestorResponse => {
			var ancestorArray = parseAncestors(ancestorResponse);
			ancestorArray.push(iri);
			var filteredAncestorArray = ancestorArray.filter(lemmaNotStartsOrEndsWithDash);
			console.log("filteredAncestorArray=" + filteredAncestorArray);
			if (doFindDescendants(ancestorResponse)) {
			    etyBase.DB.slicedQuery(filteredAncestorArray, etyBase.DB.descendantQuery, 8)
				.subscribe( 
				    descendantResponse => { 
					var allArray = parseDescendants(ancestorArray, descendantResponse);
					etyBase.DB.slicedQuery(allArray, etyBase.DB.propertyQuery, 3)
					    .subscribe(
						propertyResponse => {
						    var g = parseEtymologyNodes(ancestorArray, ancestorResponse, propertyResponse);
						    
						    if (Object.keys(g.nodess).length === 0) {
							$('#message')
							    .css('display', 'inline')
							    .html(etyBase.LOAD.MESSAGE.noEtymology);
						    } else {
							$('#helpPopup').html(etyBase.LOAD.HELP.dagre);
							renderGraph(g);
						    }
						},
						error => serverError(error),
						() => {
						    if (etyBase.config.debug) {
							console.log('done property query');
						    }
						});
				    },
				    error => serverError(error),
				    () => {
					if (etyBase.config.debug) {
					    console.log('done descendants query');
					}
				    });
			} else {
			    etyBase.DB.slicedQuery(ancestorArray, etyBase.DB.propertyQuery, 3)
                                .subscribe(
				    propertyResponse => {
                                        var g = parseEtymologyNodes(ancestorArray, ancestorResponse, propertyResponse);
					
                                        if (Object.keys(g.nodess).length === 0) {
                                            $('#message')
                                                .css('display', 'inline')
                                                .html(etyBase.LOAD.MESSAGE.noEtymology);
                                        } else {
                                            $('#helpPopup').html(etyBase.LOAD.HELP.dagre);
                                            renderGraph(g);
                                        }
                                    },
                                    error => serverError(error),
                                    () => {
                                        if (etyBase.config.debug) {
                                            console.log('done property query');
                                        }
                                    });
			}
                    },
                    error => serverError(error),
                    () => {
			if (etyBase.config.debug) {
			    console.log('done ancestor query');
			}
		    });
	};
	
        var parseEtymologyNodes = function(ancestors, ancestorResponse, propertyResponse) {
	    var g = new dagreD3.graphlib.Graph().setGraph({ rankdir: 'LR' });
	    g.nodess = {};

	    var allArray = propertyResponse.reduce((all, a) => {
		all = all.concat(JSON.parse(a).results.bindings);
                return all;
            }, []);
            if (allArray.length < 2) {
		return g;
	    } else {
		console.log("allArray");
		console.log(allArray);
		//CONSTRUCTING NODES
		allArray.forEach(function(element) {
		    //save all nodes        
		    //define isAncestor
		    if (undefined !== element.s && undefined === g.nodess[element.s.value]) {
			var label = (undefined === element.sLabel) ? undefined : element.sLabel.value; 
			g.nodess[element.s.value] = new etyBase.LOAD.classes.Node(element.s.value, label);
		    }
		    if (undefined !== element.rel) {
			if (undefined === g.nodess[element.rel.value]) {
			    var label = (undefined === element.relLabel) ? undefined : element.relLabel.value;
			    g.nodess[element.rel.value] = new etyBase.LOAD.classes.Node(element.rel.value, label);
			}
			if (ancestors.indexOf(element.rel.value) > -1) {
			    g.nodess[element.rel.value].isAncestor = true;
			}
		    }
		    if (undefined !== element.rel && undefined !== element.eq) {
			if (undefined === g.nodess[element.eq.value]) {
			    var label = (undefined === element.eqLabel) ? undefined : element.eqLabel.value;
			    g.nodess[element.eq.value] = new etyBase.LOAD.classes.Node(element.eq.value, label);
			}
			//push to eqIri
			if (element.rel.value !== element.eq.value) {
			    if (g.nodess[element.rel.value].eqIri.indexOf(element.eq.value) == -1) {
				g.nodess[element.rel.value].eqIri.push(element.eq.value);
			    }
			    if (g.nodess[element.eq.value].eqIri.indexOf(element.rel.value) == -1) {
				g.nodess[element.eq.value].eqIri.push(element.rel.value);
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
		g.graphNodes = {};
		var counter = 0; //counts how many graphNodes have been created so far
		for (var n in g.nodess) {
                    if (g.nodess[n].ety === 0) {
			var iso = g.nodess[n].iso;
			var label = g.nodess[n].label;
			var tmp = [];
			for (var m in g.nodess) {
                            if (undefined !== g.nodess[m]) {
				if (g.nodess[m].iso === iso && g.nodess[m].label === label) {
                                    if (g.nodess[m].ety > 0) {
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
			    var eqIri = g.nodess[n].eqIri.concat(g.nodess[tmp[0]].eqIri).filter(etyBase.helpers.onlyUnique);
			   
			    eqIri = eqIri.reduce(function(eq, element) {
				eq = eq.concat(g.nodess[element].eqIri).filter(etyBase.helpers.onlyUnique)
				return eq;
			    }, []);
			    var graphNode = eqIri.reduce(function(gn, element) {
				if (undefined === g.nodess[element].graphNode) {
				    return gn;
				} else {
				    gn.push(g.nodess[element].graphNode);
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
				g.nodess[element].eqIri = eqIri;
				g.nodess[element].graphNode = graphNode;
			    });			
                
                            counter++;
			}
                    }
		}
		
		for (var n in g.nodess) {
                    if (undefined === g.nodess[n].graphNode) {
			var eqIri = g.nodess[n].eqIri;
			eqIri = eqIri.reduce(function(eq, element) {
			    eq = eq.concat(g.nodess[element].eqIri).filter(etyBase.helpers.onlyUnique);
			    return eq;
			}, []);   
			var graphNode = eqIri.reduce(function(gn, element) {
                            if (undefined === g.nodess[element].graphNode) {
                                return gn;
                            } else {
                                gn.push(g.nodess[element].graphNode);
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
                            g.nodess[element].eqIri = eqIri;
                            g.nodess[element].graphNode = graphNode;
                        });
			
                        counter++;
		    }
		}
		
		var graphNodes = [];
		for (var n in g.nodess) {
		    var gn = g.nodess[n].graphNode; 
		    if (graphNodes.indexOf(gn) === -1) {
			var gg = new etyBase.LOAD.classes.GraphNode(gn);
			gg.counter = gn;
			gg.iri = g.nodess[n].eqIri;
			gg.iso = g.nodess[n].iso;
			gg.label = gg.iri.map(function(i) { return g.nodess[i].label; }).filter(etyBase.helpers.onlyUnique).join(",");
			gg.lang = g.nodess[n].lang;
			g.setNode(gn, gg);

			graphNodes.push(gg.counter);  
		    }
		}

		//CONSTRUCTING LINKS
		allArray.forEach(function(element) {
                    if (undefined !== element.rel && undefined !== element.s) {
			var source = g.nodess[element.rel.value].graphNode,
                        target = g.nodess[element.s.value].graphNode;
			if (source !== target) {
                            g.setEdge(source, target, { label: "", lineInterpolate: "basis" });
			}
                    }		    
		});

		//todo: add links between ancestors
		
		if (etyBase.config.debug) {
		    console.log("g.nodess");
		    console.log(g.nodess) ;  
		    console.log("g");
		    console.log(g);
		}
		console.log("g.nodess");
                console.log(g.nodess) ;
                console.log("g");
                console.log(g);
		
		$('#message')
                    .css('display', 'none');
		
		return g;
            }
	};

        var renderGraph = function(g) {
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

	    //append language tag to nodes            
            inner.selectAll("g.node")
                .append("text")
                .style("width", "auto")
                .style("height", "auto")
                .style("display", "inline")
		.attr("class", "isoText")
                .attr("x", "1em")
                .attr("y", "3em")
		.html(function(d) {
                    return g.node(d).iso;
                });
	    
            //show tooltip on click on language tag   
            inner.selectAll("g.node")
                .append("rect")
		.attr("x", "0.8em")
                .attr("y", "2.2em")
                .attr("x", "0.8em")
		.attr("width", function(d) {
                    return g.node(d).iso.length / 1.7 + "em";
                })
                .attr("height", "1em")
                .attr("fill", "red")
                .attr("fill-opacity", 0)
                .on("mouseover", function(d) {
                    d3.select("#tooltipPopup")
                        .style("display", "inline")
                        .style("left", (d3.event.pageX) + "px")
                        .style("top", (d3.event.pageY - 28) + "px")
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
                        g.nodess[iri].logTooltip();
                    } else {
                        iri.forEach(
                            function(i) {
                                g.nodess[i].logTooltip();
                            });
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
