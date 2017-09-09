/*globals
    $, d3, console, dagreD3, Rx, window, document, vis
*/
/*jshint loopfunc: true, shadow: true */ // Consider removing this and fixing these
var GRAPH = (function(module) {
  
    module.bindModule = function(base, moduleName) {
        var etyBase = base;
        var network;

        var draw = function(iri, parameter, width, height) {
	    d3.select("#tree-overlay").remove();

	    d3.select("#tree-container").append("div")
	        .attr("id", "tree-overlay");

            $('#message')
		.css('display', 'inline')
		.html(etyBase.LOAD.MESSAGE.loadingMore);

	    if (undefined !== network) {
		network.destroy();
	    }
	    
	    const endpoint = etyBase.config.urls.ENDPOINT;
	    const params = new URLSearchParams();
	    const format = "application/sparql-results+json";
	    params.set("format", format);
	    var formData = new FormData();
            var content = etyBase.DB.detailedAncestorQuery(iri);
            var blob = new Blob([content], { type: "text/xml" });
	    formData.append("query", blob);//encodeURIComponent(etyBase.DB.detailedAncestorQuery(iri)));	    
	    var settings = {
                method: "POST",
                body: formData
            }
	    console.log(etyBase.DB.detailedAncestorQuery(iri))
	    fetch(endpoint + "?" + params, settings)
            .then(res => { return res.json(); })
            .then(json => { return stepOne(json); })
	    .then(ancestorArray => {
		    if (ancestorArray.length === 0) { 
			$('#message')
			.css('display', 'inline')
			.html(etyBase.LOAD.MESSAGE.noEtymology);
		    } else {
			ancestorArray.push(iri);
			$('#helpPopup').html(etyBase.LOAD.HELP.dagre);
			var formData2 = new FormData();
			var content2 = etyBase.DB.unionQuery(ancestorArray, etyBase.DB.descendantQuery);
			
			var blob2 = new Blob([content2], { type: "text/xml" });
			formData2.append("query", blob2);
			var settings2 = {
			    method: "POST",
			    body: formData2
			}
			console.log(endpoint + "?" + encodeURIComponent(content2))
			fetch(endpoint + "?" + params, settings2)
			    .then(res => { return res.json(); })
			    .then(json => { return stepTwo(json); })
			    .then(descendantArray => {
				    var formData3 = new FormData();
				    var content3 = etyBase.DB.unionQuery(descendantArray, etyBase.DB.propertyQuery);
				    var blob3 = new Blob([content3], { type: "text/xml" });
				    formData3.append("query", blob3);
				    var settings3 = {
					method: "POST",
					body: formData3
				    }
				    fetch(endpoint + "?" + params, settings3)
				    .then(res => { return res.json(); })
				    .then(json => { return json.results.bindings; })
                                    .then(allArray => {
					    console.log("allArray");
					    console.log(allArray);
					    if (allArray.length === 0){
						$('#message')
						.css('display', 'inline')
						.html(etyBase.LOAD.MESSAGE.noEtymology);
					    } else {
						$('#message')
						.css('display', 'none');
						plot(ancestorArray, allArray);
					    }
					})
				    .catch(err => {
					    $('#message')
					    .css('display', 'inline')
					    .html(etyBase.LOAD.MESSAGE.serverError);
					    console.log(err);
					    })
				})
			    .catch(err => {
				    $('#message')
				    .css('display', 'inline')
				    .html(etyBase.LOAD.MESSAGE.serverError);
				    console.log(err);
				})
		    }
		})
	    .catch(err => {
                    $('#message')
		    .css('display', 'inline')
		    .html(etyBase.LOAD.MESSAGE.serverError);
		    console.log(err);
                });
		
	    function stepOne(json){
                results = json.results.bindings;
		
                var ancestorArray = results
		    .reduce((ancestors, a) => {
			    ancestors.push(a.ancestor1.value);
			    if (a.der1.value === "0" && undefined !== a.ancestor2) {
				ancestors.push(a.ancestor2.value);
				if (a.der2.value === "0" && undefined !== a.ancestor3){
				    ancestors.push(a.ancestor3.value);
				}
			    } 
			    return ancestors;
			}, []).filter(etyBase.helpers.onlyUnique);
		console.log("ancestorArray in stepOne");
		console.log(ancestorArray);
                return ancestorArray;
            };

	    function stepTwo(json){
		results = json.results.bindings;
		var descendantArray = results.reduce((descendants, d) => {
			descendants.push(d.descendant1.value);
			return descendants;
		    }, []);
		console.log("descendantArray in stepTwo");
		console.log(descendantArray);
		return descendantArray;
	    }

	    function plot(ancestorArray, allArray){
		var options = {
		    height: height.toString(),
		    autoResize: false,
		    nodes: {
			shape: "box",
			color: {
			    background: "lightBlue",
			    border: "black"
			},
			borderWidth: 0.5,
			font: {
			    size: 20
			}
		    },
		    edges: {
			smooth: {
			    type: "cubicBezier",
			    forceDirection: "horizontal"
			},
			arrows: "to"
		    },
		    layout: {
			hierarchical: {
			    direction: "LR"
			}
		    },
		    interaction: {
			hover: true
		    },
		    physics: false
		};

		var g = etyBase.GRAPH.define(ancestorArray, allArray);
		d3.select("#tree-overlay").append("div")
		.attr("position", "absolute")
		.attr("data-role", "popup")
		.attr("id", "tooltipPopup")
		.attr("class", "ui-content tooltipDiv")
		.style("display", "inline")
		.style("z-index", 2)
		.style("max-width", "400px");
		
		d3.select("#tree-overlay").append("div")
		.attr("id", "visNetwork")
		
		network = new vis.Network(document.getElementById("visNetwork"), g, options);
		//network.stabilize(100000);					    
		//window.onresize = function() {network.fit();}
		network.focus(0, {scale: 1, offset: {x: 0, y: 0}});
		network.on("beforeDrawing", function (ctx) {
			g.nodes.forEach(function(node) {
				var nodePosition = network.getPositions([node.id]);
				var scale = network.getScale();
				var fontsize = 16;
				
				var visibleFontSize = 16 * scale;
				if (visibleFontSize > 30) {
				    ctx.font = 30/scale + "px Arial";
				}
				else {
				    ctx.font = 16 + "px Arial";
				}
				ctx.fillText(node.lang, nodePosition[node.id].x + 10 , nodePosition[node.id].y + 27);
			    })
			    })
;
		network.on("hoverNode", function(params) {
			var node = network.body.nodes[params.node];			
			var coords = network.canvasToDOM({x: node.x, y: node.y});
			
			d3.select("#tooltipPopup")
			    .style("left", (coords.x + 60) + "px")
			    .style("top", coords.y + "px")
			    .html("");
			
			g.nodes.filter(function(e) {
				return e.id === params.node;
			    })[0].iri.forEach(function(iri) {
				    g.tmpNodes.filter(function(e) {
					    return e.id === iri;
					})[0].logTooltip();
				});
		    });
	    }
	};

        var define = function(ancestors, response) {
	    
	    //CONSTRUCTING TEMPORARY NODES 
	    var tmpNodes = [];
            response.forEach(function(element) {
		    //save all nodes        
		    //define isAncestor
		    if (undefined !== element.s && tmpNodes.filter(function(e) { return e.id === element.s.value; }).length === 0) {
			tmpNodes.push(new etyBase.LOAD.classes.tmpNode(element.s.value, element.sLabel.value));
		    }
		    if (undefined !== element.rel) {
			if (tmpNodes.filter(function(e) { return e.id === element.rel.value; }).length === 0) {
			    tmpNodes.push(new etyBase.LOAD.classes.tmpNode(element.rel.value, element.relLabel.value));
			}/*
			if (ancestors.indexOf(element.rel.value) > -1) {
			    tmpNodes.filter(function(e) { return e.id === element.rel.value; })[0].isAncestor = true;
			    }*/
		    }
		    if (undefined !== element.rel && undefined !== element.eq) {
			if (tmpNodes.filter(function(e) { return e.id === element.eq.value; }).length === 0) {
			    tmpNodes.push(new etyBase.LOAD.classes.tmpNode(element.eq.value, element.eqLabel.value));
			}
			//push to eqIri
			if (tmpNodes.filter(function(e) { return e.id === element.rel.value; })[0].eqIri.indexOf(element.eq.value) === -1) {
			    tmpNodes.filter(function(e) { return e.id === element.rel.value; })[0].eqIri.push(element.eq.value);
			}
			if (tmpNodes.filter(function(e) { return e.id === element.eq.value; })[0].eqIri.indexOf(element.rel.value) === -1) {
			    tmpNodes.filter(function(e) { return e.id === element.eq.value; })[0].eqIri.push(element.rel.value);
			}
		    }/*
		    if (undefined !== element.der) {
			if (tmpNodes.filter(function(e) { return e.id === element.der.value; }).length === 0) {
			    tmpNodes.push(new etyBase.LOAD.classes.tmpNode(element.der.value, element.derLabel.value));
			}
			//add property der
			tmpNodes.filter(function(e) { return e.id === element.s.value; })[0].der = true;
			}*/
		});
	    
	    var nodes = [];
	    
	    tmpNodes.forEach(function(nNode) {
		    
		    if (nNode.ety === 0) {		    
			var tmp = tmpNodes.filter(function(mNode) { 
				return (mNode.iso === nNode.iso && mNode.label === nNode.label && mNode.ety > 0); 
			    }).filter(etyBase.helpers.onlyUnique);
			//if only ee_word and ee_n_word with n an integer belong to
			//the set of ancestors and descendants
			//then merge them in one graphNode
			if (tmp.length === 1) {
			    var tmpNode = tmp[0]; 
			    var node = new etyBase.LOAD.classes.Node();
			    //initialize node.all 
			    node.all.push(nNode.id);
			    //define node.iri 
			    node.iri = nNode.eqIri;
			    node.iri.push(tmpNode.id);
			    //define node.graphNode
			    nNode.graphNode.push(node.id);//mofidy this
			    tmpNode.graphNode.push(node.id);//modify this
			    node.iri.forEach(function(element) {
				    tmpNodes.filter(function(e) { 
					    return e.id === element;
					})[0].graphNode.push(node.id);
				});
			    node.iri = node.iri.filter(etyBase.helpers.onlyUnique);
                        //push to graphNodes
			    nodes.push(node);
			}
		    }
		});  
            
	    tmpNodes.forEach(function(nNode) { 
                if (nNode.graphNode.length === 0) {
                    //add iri
                    var node = new etyBase.LOAD.classes.Node();
                    node.iri = nNode.eqIri;
                    node.iri.push(nNode.id);
		    var equivalent = node.iri.reduce(function(a,b) {
			    return a.concat(b.eqIri);
			}, []);
		    node.iri.concat(equivalent); 
                    node.iri = node.iri.filter(etyBase.helpers.onlyUnique);
		    //add graphNode, graphNodes 
                    node.iri.forEach(function(element) {
			    tmpNodes.filter(function(e) { 
				    return e.id === element;
				})[0].graphNode.push(node.id);
			});
                    nodes.push(node);
                } else {
                    var graphNode = nNode.graphNode[0];
                    
                    nNode.eqIri.forEach(function(element) {
			    //add graphNode
			    if (element !== nNode.id) {
				tmpNodes.filter(function(e) { 
					return e.id === element;
				    })[0].graphNode.push(graphNode);
			    }
			    //add iri
			    nodes[graphNode].iri.concat(tmpNodes.filter(function(e) { 
					return e.id === element;
				    })[0].eqIri);
			    nodes[graphNode].iri = nodes[graphNode].iri.filter(etyBase.helpers.onlyUnique);
			});
                }
		});

	    /*            var showDerivedNodes = true;
            //always show derived nodes if tree is small
            if (ancestors.length < 3) showDerivedNodes = true;
	    */
	    nodes.forEach(function(nn){
		    //define all
		    nn.all = nn.all.concat(nn.iri);

		    //define isAncestor
		    /*if (nn.all.filter(function(element) {
				return tmpNodes.filter(function(e) {
					return e.id === element;
				    })[0].isAncestor;   
			    }).length > 0) {
			nn.isAncestor = true;  
			}

		    //define der
		      var der = nodes[id].all.filter(function(element) {
		      return tmpNodes[element].der !== undefined; 
		      })
		      if (der.length > 0) {
		      nodes[id].der = true; 
		      }*/ 
		    
		    //define iso, label, lang 
		    var zeroNode = tmpNodes.filter(function(e) {
			    return e.id === nn.all[0]; 
			})[0];     
		    nn.iso = zeroNode.iso; 
		    nn.label = nn.iri.map(function(element) { 
			    return tmpNodes.filter(function(e) {
				    return e.id === element;    
				})[0].label; }).join(","); 
		    nn.lang = zeroNode.lang;
		});

            //CONSTRUCTING LINKS
            var edges = [];
            response.forEach(function(element) {
                if (undefined !== element.rel && undefined !== element.s) {
                    var source = tmpNodes.filter(function(e) { return e.id === element.rel.value; })[0].graphNode[0],
                        target = tmpNodes.filter(function(e) { return e.id === element.s.value; })[0].graphNode[0];
                    if (source !== target) {
			var edge = { "from": source, "to": target };
			edges.push(edge);
                    }
                }
            });
	    
	    if (etyBase.config.debug) {
		console.log("tmpNodes");
		console.log(tmpNodes) ;  
		console.log("nodes");
		console.log(nodes);
	    }


            return {tmpNodes: tmpNodes, nodes: nodes, edges: edges};
        };


        var init = function() {
            $('#helpPopup')
		.html(etyBase.LOAD.HELP.intro);

            $(window).click(function() {
                d3.select("#tooltipPopup")
                    .style("display", "none");
            });


            $('#tags').on("keypress click", function(e) {
                var tag = this;
                if (e.which === 13 || e.type === 'click') {
		    etyBase.LOAD.nodeCount = -1;
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
				    if (undefined !== network) {
					network.destroy();
				    }
				    var disambiguationArray = JSON.parse(response).results.bindings;
				    if (disambiguationArray.length === 0) {
					return null;
				    }

				    //define nodes                 
				    var nodes = [];
				    disambiguationArray.forEach(function(n) {
					    n.et.value.split(",")
						.forEach(function(element) {
							if (element !== "") {
							    nodes.push(new etyBase.LOAD.classes.tmpNode(element, n.lemma.value));
							} else {
							    nodes.push(new etyBase.LOAD.classes.tmpNode(n.iri.value, n.lemma.value));
							}
						    });
					});
				    if (etyBase.config.debug) {
					console.log("nodes");
					console.log(nodes);
				    }

				    d3.select("#tree-overlay").remove();

				    d3.select("#tree-container").append("div")
					.attr("id", "tree-overlay");

                                    if (nodes.length === 0) {
                                        $('#message')
					    .css('display', 'inline')
					    .html(etyBase.LOAD.MESSAGE.notAvailable);
                                    } else {
					if (nodes.length === 1){
					    var iri = nodes[0].id; 
					    etyBase.GRAPH.draw(iri, 2, width, height);
					} else {
                                            $('#helpPopup')
						.html(etyBase.LOAD.HELP.disambiguation);
                                            $('#message')
						.css('display', 'inline')
						.html(etyBase.LOAD.MESSAGE.disambiguation);
					    var options = {
						autoResize: false,
						height: height.toString(),
						nodes: {
						    shape: "box", 
						    color: {
							background: "lightBlue", 
							border: "black"
						    }, 
						    borderWidth: 0.5, 
						    font: {
							size: 20
						    }
						},
						edges: {
						    smooth: {
							type: "cubicBezier", 
							forceDirection: "horizontal"
						    }
						},
						layout: {
						    hierarchical: {
							direction: "LR"
						    }
						},
						interaction: {
						    hover: true
						},
						physics: false
					    };
					    
					    d3.select("#tree-overlay").append("div")
                                                .attr("position", "absolute")
                                                .attr("data-role", "popup")
                                                .attr("id", "tooltipPopup")
                                                .attr("class", "ui-content tooltipDiv")
                                                .style("display", "inline")
                                                .style("z-index", 2)
                                                .style("max-width", "400px");

                                            d3.select("#tree-overlay").append("div")
                                                .attr("id", "visNetwork")
                                            
					    network = new vis.Network(document.getElementById("visNetwork"), { nodes: nodes }, options);
					    
					    network.moveTo({scale:1.0});

					    //add language tag
					    network.on("beforeDrawing", function (ctx) {
						    nodes.forEach(function(node) {
							var nodePosition = network.getPositions([node.id]);
							var scale = network.getScale();
							var fontsize = 16;
							
							var visibleFontSize = 16 * scale;
							if (visibleFontSize > 30) {
							    ctx.font = 30/scale + "px Arial";  
							}
							else {
							    ctx.font = 16 + "px Arial";
							}
							ctx.fillText(node.lang, nodePosition[node.id].x + 10 , nodePosition[node.id].y + 27);
							})
						});

					    network.on("hoverNode", function(params) {						    
						    var node = network.body.nodes[params.node];
                                                    var coords = network.canvasToDOM({x: node.x, y: node.y});

                                                    d3.select("#tooltipPopup")
                                                        .style("left", (coords.x + 60) + "px")
                                                        .style("top", coords.y + "px")
							.html("");
						    						
						    nodes.filter(function(e) {
							    return e.id === params.node;
							})[0].logTooltip();
						});

					    network.on("click", function(params) {
						    if (params.nodes.length === 1) {
							var node = params.nodes[0];
							etyBase.GRAPH.draw(node, 2, width, height);
						    }
						});	
                                        } 
                                    }
                                }
                            },
                            function(error) {
                                console.error(error);
				network.destroy();
                                d3.select("#tooltipPopup")
                                    .style("display", "none");
                                $('#message')
				    .css('display', 'inline')
				    .html(etyBase.LOAD.MESSAGE.notAvailable);
                            },
                            () => console.log('done disambiguation'));
                    }
                }
            });


        };

        this.init = init;
        this.draw = draw;
        this.define = define;

        etyBase[moduleName] = this;
	};

    return module;

})(GRAPH || {});
