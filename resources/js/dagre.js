/*globals
    $, d3, console, dagreD3, Rx, window, document
*/
/*jshint loopfunc: true, shadow: true */ // Consider removing this and fixing these
var GRAPH = (function(module) {

    module.bindModule = function(base, moduleName) {
        var etyBase = base;

        var appendLanguageTagTextAndTooltip = function(inner, g) {
            //append language tag to nodes        
            inner.selectAll("g.node")
                .append("text")
                .style("width", "auto")
                .style("height", "auto")
                .style("display", "inline")
                .attr("class", "isoText")
                .attr("y", "3em")
                .attr("x", "1em")
                .html(function(d) {
                    return g.node(d).iso;
                });
            //show tooltip on click on laguage tag
            inner.selectAll("g.node")
                .append("rect")
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
                    console.log(g.node(d).lang);
                    d3.event.stopPropagation();
                });
        };

        var appendDefinitionTooltip = function(inner, g) {
            //show tooltip on click on nodes                  
            inner.selectAll("g.node")
                .on("mouseover", function(d) {
                    d3.select("#tooltipPopup")
                        .style("display", "inline")
                        .style("left", (d3.event.pageX + 38) + "px")
                        .style("top", (d3.event.pageY - 28) + "px")
                        .html("");
                    g.node(d).iri.forEach(
                        function(iri) {
                            g.nodess[iri].logTooltip();
                        });
                    d3.event.stopPropagation();
                });
        };

        var appendDefinitionTooltipOrDrawDAGRE = function(inner, g, width, height) {
            inner.selectAll("g.node")
                .on('click', function(d) {
                    var iri = g.node(d).iri;
                    etyBase.GRAPH.drawDAGRE(iri, 2, width, height);
                    d3.select("#tooltipPopup")
                        .style("display", "none");
                })
                .on('mouseover', function(d) {

                    d3.select(this).style("cursor", "pointer");

                    d3.select("#tooltipPopup")
                        .style("display", "inline")
                        .style("left", (d3.event.pageX + 38) + "px")
                        .style("top", (d3.event.pageY - 28) + "px")
                        .html("");
                    var iri = g.node(d).iri;
                    g.nodess[iri].logTooltip();
                });
        };

        var buildDisambiguationDAGRE = function(response) {
            var disambiguationArray = JSON.parse(response).results.bindings;
            if (disambiguationArray.length === 0) {
                return null;
            }
            var g = new dagreD3.graphlib.Graph().setGraph({});

            //define nodes 
            g.nodess = {};
            disambiguationArray.forEach(function(n) {
		var label = n.lemma.value.replace(/^_/, '*');
                n.et.value.split(",")
                    .forEach(function(element) {
                        if (element !== "") {
                            g.nodess[element] = new etyBase.LOAD.classes.Node(element, label);
                        } else {
                            g.nodess[n.iri.value] = new etyBase.LOAD.classes.Node(n.iri.value, label);
                        }
                    });
            });
            if (etyBase.config.debug) {
                console.log(g.nodess);
            }

            //add nodes and links to the graph
            var m = null;
            for (var n in g.nodess) {
                g.setNode(n, g.nodess[n], { labelStyle: "font-size: 3em" });
                if (null !== m) {
                    g.setEdge(n, m, { label: "", style: "stroke-width: 0" });
                }
                m = n;
            }

            return g;
        };

        var drawDAGRE = function(iri, parameter, width, height) {
            //if parameter == 1 submit a short (but less detailed) query
            //if parameter == 2 submit a longer (but more detailed) query
            var url = etyBase.config.urls.ENDPOINT + "?query=" + encodeURIComponent(etyBase.DB.ancestorQuery(iri, parameter));
            var source;
            if (etyBase.config.debug) {
                console.log(url);
            }
            d3.select("#message")
		.style("display", "inline")
		.html(etyBase.LOAD.MESSAGE.loadingMore);
            d3.select("#tooltipPopup")
		.attr("display", "none");
            d3.select("#tree-overlay").remove();

            source = etyBase.DB.getXMLHttpRequest(url);

            source.subscribe(
                function(response) {

                    if (null === response) {
                        d3.select("#message")
			    .style("display", "inline")
			    .html(etyBase.LOAD.MESSAGE.serverError);
                        return;
                    }

                    d3.select("#helpPopup").html(etyBase.LOAD.HELP.dagre);

                    var ancestorArray = JSON.parse(response).results.bindings
                        .reduce((ancestors, a) => {
                            ancestors.push(a.ancestor1.value);
                            if (undefined !== a.ancestor2) {
                                ancestors.push(a.ancestor2.value);
                            }
                            return ancestors;
                        }, []).filter(etyBase.helpers.onlyUnique);

                    console.log("ANCESTORS");
                    console.log(ancestorArray);
                    const subscribe = etyBase.DB.slicedQuery(ancestorArray, etyBase.DB.descendantQuery, 8)
                        .subscribe(
                            function(val) {
                                var descendantArray = val.reduce((descendants, d) => {
                                    return descendants.concat(JSON.parse(d).results.bindings.map(function(t) { return t.descendant1.value; }));
                                }, []);
                                const subscribe = etyBase.DB.slicedQuery(descendantArray, etyBase.DB.propertyQuery, 4)
                                    .subscribe(function(val) {
                                        var allArray = val.reduce((all, a) => {
                                            return all.concat(JSON.parse(a).results.bindings);
                                        }, []);
                                        if (allArray.length === 0) {
                                            d3.select("#message")
						.style("display", "inline")
						.html(etyBase.LOAD.MESSAGE.noEtymology);
                                        } else {
                                            var g = etyBase.GRAPH.defineGraph(ancestorArray, allArray);
                                            d3.select("#message")
						.style("display", "none");

                                            var inner = etyBase.GRAPH.renderGraph(g, width, height);
                                            etyBase.GRAPH
						.appendLanguageTagTextAndTooltip(inner, g);
                                            etyBase.GRAPH
						.appendDefinitionTooltip(inner, g);
                                        }
                                    });
                            },
                            function(error) {
                                d3.select("#message")
				    .style("display", "inline")
				    .html(etyBase.LOAD.MESSAGE.serverError);
                                console.log(error);
                            },
                            () => console.log('done descendants query'));
                },
                function(error) {
                    if (parameter === 1) {
                        d3.select("#message")
			    .style("display", "inline")
			    .html(etyBase.LOAD.MESSAGE.serverError);
                        console.log(error);
                    } else {
                        etyBase.GRAPH.drawDAGRE(iri, 1, width, height);
                    }
                },
                function() {
                    console.log('done DAGRE' + parameter);
                });
        };

        var defineGraph = function(ancestors, response) {
            var g = new dagreD3.graphlib.Graph().setGraph({ rankdir: 'LR' });

            //CONSTRUCTING NODES
            g.nodess = {};
            response.forEach(function(element) {
		    var label;
		    //save all nodes        
		    //define isAncestor
		    if (undefined !== element.s && undefined === g.nodess[element.s.value]) {
			label = element.sLabel.value.replace(/^_/, '*');
			g.nodess[element.s.value] = new etyBase.LOAD.classes.Node(element.s.value, label);
		    }
		    if (undefined !== element.rel) {
			label = element.relLabel.value.replace(/^_/, '*');
			if (undefined === g.nodess[element.rel.value]) {
			    g.nodess[element.rel.value] = new etyBase.LOAD.classes.Node(element.rel.value, label);
			}
			if (ancestors.indexOf(element.rel.value) > -1) {
			    g.nodess[element.rel.value].isAncestor = true;
			}
		    }
		    if (undefined !== element.rel && undefined !== element.eq) {
			if (undefined === g.nodess[element.eq.value]) {
			    label = element.eqLabel.value.replace(/^_/, '*');
			    g.nodess[element.eq.value] = new etyBase.LOAD.classes.Node(element.eq.value, label);
			}
			//push to eqIri
			if (g.nodess[element.rel.value].eqIri.indexOf(element.eq.value) == -1) {
			    g.nodess[element.rel.value].eqIri.push(element.eq.value);
			}
			if (g.nodess[element.eq.value].eqIri.indexOf(element.rel.value) == -1) {
			    g.nodess[element.eq.value].eqIri.push(element.rel.value);
			}
		    }
		    if (undefined !== element.der) {
			if (undefined === g.nodess[element.der.value]) {
			    label = element.derLabel.value.replace(/^_/, '*');
			    g.nodess[element.der.value] = new etyBase.LOAD.classes.Node(element.der.value, label);
			}
			//add property der
			g.nodess[element.s.value].der = true;
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
                        var gg = new etyBase.LOAD.classes.GraphNode(counter);
                        //initialize graphNode.all 
                        gg.all.push(n);
                        //define graphNode.iri 
                        gg.iri = g.nodess[tmp[0]].eqIri;
                        gg.iri.push(tmp[0]);
                        //define node.graphNode
                        g.nodess[n].graphNode.push(counter);
                        g.nodess[tmp[0]].graphNode.push(counter);
                        gg.iri.forEach(function(element) {
                            g.nodess[element].graphNode.push(counter);
                        });

                        //push to graphNodes
                        g.graphNodes[counter] = gg;
                        g.graphNodes[counter].iri = g.graphNodes[counter].iri.filter(etyBase.helpers.onlyUnique);
                        counter++;
                    }
                }
            }

            for (var n in g.nodess) {
                if (g.nodess[n].graphNode.length === 0) {
                    //add iri
                    var gg = new etyBase.LOAD.classes.GraphNode(counter);
                    gg.iri = g.nodess[n].eqIri;
                    gg.iri.push(n);
		    var equivalent = gg.iri.reduce(function(a,b) {
			    return a.concat(b.eqIri);
			}, [])
		    gg.iri.concat(equivalent); 
                    gg.iri = gg.iri.filter(etyBase.helpers.onlyUnique);
		    //add graphNode, graphNodes
                    gg.iri.forEach(function(element) {
                        g.nodess[element].graphNode.push(counter);
                    });
                    g.graphNodes[counter] = gg;
                    counter++;
                } else {
                    var graphNode = g.nodess[n].graphNode[0];

                    g.nodess[n].eqIri.forEach(function(element) {
			    //add graphNode
			    if (element != n) {
				g.nodess[element].graphNode.push(graphNode);
			    }
			    //add iri
			    g.graphNodes[graphNode].iri.concat(g.nodess[element].eqIri);
			    g.graphNodes[graphNode].iri = g.graphNodes[graphNode].iri.filter(etyBase.helpers.onlyUnique);
			});
                }
            }

            var showDerivedNodes = true;
            if (ancestors.length < 3) showDerivedNodes = true;

            for (var gg in g.graphNodes) {
                //define all
                g.graphNodes[gg].all = g.graphNodes[gg].all.concat(g.graphNodes[gg].iri);

                //define isAncestor
                if (g.graphNodes[gg].all.filter(function(element) { return g.nodess[element].isAncestor; }).length > 0) {
                    g.graphNodes[gg].isAncestor = true;
                }

                //define der
                var der = g.graphNodes[gg].all.filter(function(element) {
                    return g.nodess[element].der !== undefined;
                });
                if (der.length > 0) {
                    g.graphNodes[gg].der = true;
                }

                //define iso, label, lang
                g.graphNodes[gg].iso = g.nodess[g.graphNodes[gg].all[0]].iso;
                g.graphNodes[gg].label = g.graphNodes[gg].iri.map(function(i) { return g.nodess[i].label; }).join(",");
                g.graphNodes[gg].lang = g.nodess[g.graphNodes[gg].all[0]].lang;
            }

            //define linkedToTarget and linkedToSource
            response.forEach(function(element) {
                if (element.rel !== undefined && element.s !== undefined) {
                    var source = g.nodess[element.rel.value].graphNode[0],
                        target = g.nodess[element.s.value].graphNode[0];

                    if (source !== target) {
                        if (showDerivedNodes) {
                            g.graphNodes[source].linkedToTarget.push(target);
                        } else {
                            //linkedToTarget only counts the number of descendants that are not derived words
                            if (!(g.graphNodes[source].der || g.graphNodes[target].der)) {
                                g.graphNodes[source].linkedToTarget.push(target);
                            }
                        }
                        if (g.graphNodes[target].linkedToSource.indexOf(source) === -1) {
                            g.graphNodes[target].linkedToSource.push(source);
                        }
                    }
                }
            });

            for (var gg in g.graphNodes) {
                //collapse nodes that have more than 10 descendants and color them differently      
                if (!showDerivedNodes && g.graphNodes[gg].linkedToTarget.length > 10) {
                    g.graphNodes[gg].linkedToTarget.map(function(e) {
                        if (g.graphNodes[e].linkedToSource.length === 1) {
                            g.graphNodes[e].der = true;
                        }
                    });

                    //    graphNodes[gg].linkedToTarget.map(function(e){  graphNodes[e].der = true; });
                    g.graphNodes[gg].style = "fill: sandyBrown; stroke: lightBlue";
                }
            }

            //CONSTRUCTING LINKS
            var links = [];
            response.forEach(function(element) {
                if (undefined !== element.rel && undefined !== element.s) {
                    var source = g.nodess[element.rel.value].graphNode[0],
                        target = g.nodess[element.s.value].graphNode[0];
                    if (source !== target) {
                        if (showDerivedNodes || g.graphNodes[target].isAncestor || !(g.graphNodes[source].der || g.graphNodes[target].der)) {
                            var Link = { "source": source, "target": target };
                            if (g.graphNodes[target].linkedToSourceCopy.indexOf(source) === -1) {
                                //define linked and linkedToSourceCopy
                                links.push(Link);
                                g.graphNodes[source].linked = true;
                                g.graphNodes[target].linkedToSourceCopy.push(source);
                                g.graphNodes[target].linked = true;
                            }
                        }
                    }
                }
            });

            //INITIALIZING NODES IN GRAPH
            //only draw nodes that are linked to other nodes
            //always show ancestors
            for (var gg in g.graphNodes) {
                if (g.graphNodes[gg].linked || g.graphNodes[gg].isAncestor) {
                    g.setNode(gg, g.graphNodes[gg]);
                }
            }

            //INITIALIZING LINKS IN GRAPH
            links.forEach(function(element) {
                g.setEdge(element.source,
                    element.target, { label: "", lineInterpolate: "basis" });
            });
	    
	    if (etyBase.config.debug) {
		console.log("g.nodess");
		console.log(g.nodess) ;  
		console.log("g.graphNodes");
		console.log(g.graphNodes);
	    }


            return g;
        };

        var renderGraph = function(g, width, height) {
            var svg = d3.select("#tree-container").append("svg")
                .attr("id", "tree-overlay")
                .attr("width", width)
                .attr("height", height);

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
            zoom.translate([(width - g.graph().width * initialScale) / 2, 20])
                .scale(initialScale)
                .event(svg);

            //svg.attr("height", g.graph().height * initialScale + 40);}}
            return inner;
        };

        var init = function() {

            d3.select("#helpPopup")
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
                        if (etyBase.config.debug) {
                            console.log("searching lemma in database");
                        }
                        var width = window.innerWidth,
                            height = $(document).height() - $('#header').height();
                        var url = etyBase.config.urls.ENDPOINT + "?query=" + encodeURIComponent(etyBase.DB.disambiguationQuery(lemma));
                        if (etyBase.config.debug) {
			    console.log("disambiguation query");
                            console.log(url);
                        }

                        const source = etyBase.DB.getXMLHttpRequest(url);
                        source.subscribe(
                            function(response) {
                                if (response !== undefined && response !== null) {
                                    d3.select("#tree-overlay")
					.remove();
                                    d3.select("#tooltipPopup")
					.style("display", "none");

                                    var g = etyBase.GRAPH.buildDisambiguationDAGRE(response);
                                    if (null === g) {
                                        d3.select("#message")
					    .style("display", "inline")
					    .html(etyBase.LOAD.MESSAGE.notAvailable);
                                    } else {
                                        if (Object.keys(g.nodess).length > 1) {
                                            d3.select("#helpPopup")
						.html(etyBase.LOAD.HELP.disambiguation);
                                            d3.select("#message")
						.style("display", "inline")
						.html(etyBase.LOAD.MESSAGE.disambiguation);
                                            var inner = etyBase.GRAPH.renderGraph(g, width, height);
                                            etyBase.GRAPH.appendLanguageTagTextAndTooltip(inner, g);
                                            etyBase.GRAPH.appendDefinitionTooltipOrDrawDAGRE(inner, g, width, height);

                                            d3.selectAll(".edgePath").remove();
                                        } else {
                                            var iri = Object.keys(g.nodess)[0];
                                            etyBase.GRAPH.drawDAGRE(iri, 2, width, height);
                                        }
                                    }
                                }
                            },
                            function(error) {
                                console.error(error);
				d3.select("#tree-overlay")
                                    .remove();
                                d3.select("#tooltipPopup")
                                    .style("display", "none");
                                d3.select("#message")
				    .style("display", "inline")
				    .html(etyBase.LOAD.MESSAGE.notAvailable);
                            },
                            () => console.log('done disambiguation'));
                    }
                }
            });


        };

        this.init = init;
        this.appendLanguageTagTextAndTooltip = appendLanguageTagTextAndTooltip;
        this.appendDefinitionTooltip = appendDefinitionTooltip;
        this.appendDefinitionTooltipOrDrawDAGRE = appendDefinitionTooltipOrDrawDAGRE;
        this.buildDisambiguationDAGRE = buildDisambiguationDAGRE;
        this.drawDAGRE = drawDAGRE;
        this.defineGraph = defineGraph;
        this.renderGraph = renderGraph;

        etyBase[moduleName] = this;
    };

    return module;

})(GRAPH || {});
