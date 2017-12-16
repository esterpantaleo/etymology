/*globals
  $, d3, console, dagreD3, Rx, window, document, URLSearchParams
*/
/*jshint loopfunc: true, shadow: true, latedef: false */ // Consider removing this and fixing these
var APP = (function(module) {
    
    module.bindModule = function(base, moduleName) {
        var etyBase = base;

	var setNodes = function(etymologyEntries) {
	    var nodes = {};
	    
	    for (var n in etymologyEntries) {
		var id = etymologyEntries[n].node;
		if (undefined === nodes[id]) {
		    nodes[id] = new etyBase.GRAPH.Node(id, etymologyEntries[n]);
		} else {
		    nodes[id].iri = nodes[id].iri.concat(etymologyEntries[n].iri);
		    nodes[id].iri = [].concat.apply([], nodes[id].iri).filter(etyBase.helpers.onlyUnique); 
		}
	    }
	    
	    for (var n in nodes) {
		nodes[n].label = nodes[n].iri
		    .map((id) => {
			return etymologyEntries[id].label;
		    })
		    .join(",");
		nodes[n].posAndGloss = nodes[n].iri.map((id) => {
		    return etymologyEntries[id].posAndGloss;
		});
		nodes[n].urlAndLabel = nodes[n].iri.map((id) => { 
		    return etymologyEntries[id].urlAndLabel;
		});
	    }
	    return nodes;
	};

	var setEdges = function(etymologyEntries) {
            return etymologyEntries.properties
		.filter((p) => {
                    return (
			undefined !== p.rel &&
                            undefined !== p.s &&
                            undefined !== etymologyEntries.values[p.s.value] &&
                            undefined !== etymologyEntries.values[p.rel.value]
		    ) ? true : false;
		})
		.reduce((a, p) => {
                    var source = etymologyEntries.values[p.rel.value].node,
                    target = etymologyEntries.values[p.s.value].node;
                    if (source !== target) {
			a.push({
                            source: source,
                            target: target,
                            style: {
				label: "",
                                lineInterpolate: "basis",
                                arrowheadStyle: "fill: steelblue"
			    }
			});
                    }
		    return a;
		}, [])//remove duplicate edges, maybe remove this
		.filter((thing, index, self) =>
			index === self.findIndex((t) => (
			    t.source === thing.source && t.target === thing.target
			))
		       );
        };

	var etytreeAncestors = function(etymologyEntries) {
	    //clean screen 
	    $("#tree-overlay") 
		.remove(); 
	    d3.select("#tooltipPopup") 
		.style("display", "none");
      
	    if (Object.keys(etymologyEntries.values).length < 2) {
		var node = etymologyEntries.values[Object.keys(etymologyEntries.values)[0]];
		$("#message")  
		    .html(etyBase.MESSAGE.noEtymology(node.lang, node.label));
		return {
		    nodes: {},
		    edges: []
		}
	    }
	    
	    return {
		nodes: setNodes(etymologyEntries.values),
		edges: setEdges(etymologyEntries)
	    };
	}

	var etytreeAncestorsGraph = function(etymologyEntries) {
	    //clean screen 
	    $("#tree-overlay") 
		.remove();     
	    d3.select("#tooltipPopup") 
		.style("display", "none");
	    $("#message")
	        .css("display", "none");

	    var g = new etyBase.GRAPH.Graph(etytreeAncestors(etymologyEntries));
	    g.setLanguages();
	    console.log(g.languages);
	    g.render("#tree-container", "tree-overlay")
		.selectAll("g.node")
	        .on("click", function(d) {             
                    var node = g.dagre.node(d);
                    var iri = node.iri[0];

		    //open dialog
		    $("#descendants").remove();
                    d3.select("#tree-container")
		        .append("div")
		        .attr("id", "descendants");
		    $("#descendants").dialog({
                        title: "descendants of " + node.lang + " " + node.label,
                        autoOpen: false,
			width: $(window).width() - 15,
			height: $(window).height() - 15,
			position: "top"
                    })
                    $("#descendants").dialog("open");
		    
		    etyBase.DATAMODEL.descendantsQuery(iri)
                        .map((response) => {
                            console.log(response)
                            var nodes = {};
                            var id = 0;
                            var keys = Object.keys(response);
                            for (var n in keys) {
                                nodes[n] = new etyBase.GRAPH.Node(n, response[keys[n]]);
                            }
                            return {
                                nodes: nodes
                            };
                        })
                        .subscribe((response) => {
			    console.log(g.languages);
			    var l = Array.apply(null, {length: g.languages.length})
				.map(Function.call, Number);
			    var accordion = d3.select("#descendants")
			        .append("div")
			        .attr("id", "accordion");

			    g.languages.map((lang, i) => {
				console.log(lang);
				console.log(i);
				$("#accordion").append("<h3>" + lang + "</h3>");
				$("#accordion").append("<div id=\"div" + i + "\"></div>");
			    });
					    
			    //collapse all windows in accordion
			    //when a window is activated, render language graph
			    $("#accordion").accordion({
				    collapsible: "true",
				    activate: function(event, ui) {
					console.log(ui)
					//render descendantsGraph by language when clicking on accordion header 
					var language = ui.newHeader.text();
					var index = g.languages.indexOf(language);
					console.log(g.languages[index])
					var gg = new etyBase.GRAPH.LanguageGraph(g, language);	
					console.log(gg)
					//clean accordion 
//					language = language.replace(/ /g, "_").replace(/ *\([^)]*\) * /g, "");
					
					d3.select("#overlay" + index).remove();
					
					//render Dagre of language 
					gg.render("#div" + index, "overlay" + index)
					    .selectAll("g.node")
					    .on("click", function(d) {
						//on click on node in language graph, render ancestorsGraph of clicked node
						$("#descendants").dialog("close");
						var iri = gg.dagre.node(d).iri[0];
						var accordionG = new etyBase.GRAPH.Graph(iri);
						$(search).val(gg.dagre.node(d).label);
					    });
					
					//resize language graph 
					var h = Math.min(gg.dagre.graph().height + 30, window.innerHeight - 15);
					d3.select("#div" + index)
					    .attr("style", "height:" + h + "px;");
				    },
				active: false
			    });
			});
		});
	};

	
	var etytree = function(lemma) {
	    if (lemma.length < 2) {
                return;
            }

	    //clean screen
            $("#tree-overlay")
                .remove();
            d3.select("#tooltipPopup")
                .style("display", "none");
	    	    
	    //visualize disambiguation
	    etyBase.DATAMODEL.disambiguationQuery(lemma)
		.subscribe((response) => {

		    var N = Object.keys(response).length;
		    
		    if (N === 0) {
			
			$("#message")
			    .css("display", "inline")
			    .html(etyBase.MESSAGE.notAvailable);
			
		    } else if (N === 1) {
			
			var iri = Object.keys(response)[0];
			$("#tree-overlay")
			    .remove();
			d3.select("#tooltipPopup")
			    .style("display", "none");
			$("#message")
			    .css("display", "inline")
			    .html(etyBase.MESSAGE.loading);
			etyBase.DATAMODEL.ancestorsQuery(iri);
			
		    } else {
			
			$("#helpPopup")
			    .html(etyBase.HELP.disambiguation);
			$("#message")
			    .css("display", "inline")
			    .html(etyBase.MESSAGE.disambiguation);
			
			etyBase.DATAMODEL.disambiguationNodesQuery(response)
			    .subscribe((nodes) => {
				var g = new etyBase.GRAPH.Graph({
				    nodes: nodes
				});

				g.render("#tree-container", "tree-overlay")
				    .selectAll("g.node")   
				    .on("click", function(d) {
					var iri = g.dagre.node(d).iri[0];
					$("#tree-overlay")
					    .remove();
					d3.select("#tooltipPopup")
					    .style("display", "none");
					$("#message")
					    .css("display", "inline")
					    .html(etyBase.MESSAGE.loading);
					etyBase.DATAMODEL.ancestorsQuery(iri);

				    });
			    });
		    }
		});
	};
	   
        var init = function() {
	    
	    $("#helpPopup")
                .html(etyBase.HELP.intro);
	    
            d3.select("body").append("div")
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
		    etytree(lemma);
		}
	    });
	    
	    $("#btnSearch").click(function(e) {
		var lemma = $("#search").val();
		etytree(lemma);
            });

        };

        this.init = init;
	this.etytreeAncestorsGraph = etytreeAncestorsGraph;
        etyBase[moduleName] = this;
    };

    return module;

})(APP || {});
