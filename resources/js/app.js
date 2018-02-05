/*globals
  $, d3, console, dagreD3, Rx, window, document, URLSearchParams
*/
/*jshint loopfunc: true, shadow: true, latedef: false */ // Consider removing this and fixing these

/**
 * @module APP 
 * @requires GRAPH
 * @requires DATAMODEL
 */
var APP = (function(module) {
    
    module.bindModule = function(base, moduleName) {
	var etyBase = base,
	width = window.innerWidth,
	height = window.innerHeight,
	HELP = {
	    intro: "Enter a word in the search bar, then press enter or click.",
	    disambiguation: "<b>Disambiguation page</b>" +
		"<br>Pick the word you are interested in." +
		"<ul>" +
		"<li>Mouse over a node to display lexical information</li>" +
		"<li>Mouse over the language tag under the node to display the language</li>" +
		"<li>Click on a node to choose a word</li>" +
		"</ul>",
	    dagre: "Arrows go from ancestor to descendant.<ul>" +
		"<li>Mouse over a node to display lexical information</li>" +
		"<li>Mouse over the language tag under the node to display the language</li>" +
		"<li>Click on a node to display its descendants, grouped by language</li>" +
		"</ul>"
	},
	MESSAGE = {
	    notAvailable: "This word is not available in the database.",
	    loading: "Loading, please wait...",
	    noEtymology: "Etytree could not extract the etymology of this word from the English Wiktionary, <br>or there is no etymology in the English Wiktionary for this word. <br><br><br>Add/edit etymology of ",
	    disambiguation: "There are multiple words in the database. <br>Click on the word you are interested in to see its ancestors:",
		    clickForAncestors: "Click on a word to see its ancestors:",
	    clickForDescendants: "Click on a word to see its descendants"
	};
	
	/**
	 * Given an object consisting of EtymologyEntry-s, 
	 * this function returns an object consisting of Nodes
	 * @function nodesFrom
	 * @param {Object.<EtymologyEntry>} etymologyEntries 
	 * @return {Object.<Node>} a list of Nodes
	 */
	var nodesFrom = function(etymologyEntries) {
	    var nodes = {};
	    
	    var counter = 0;
	    for (var n in etymologyEntries.values) {
		var id = etymologyEntries.values[n].node;
		
		if (undefined === id) {
		    id = counter; 
		    nodes[id] = new etyBase.GRAPH.Node(id, etymologyEntries.values[n]);
		    counter ++;
		} else if (undefined === nodes[id]) {
		    nodes[id] = new etyBase.GRAPH.Node(id, etymologyEntries.values[n]);
		} else {
		    nodes[id].iri = nodes[id].iri.concat(etymologyEntries.values[n].iri);
		    nodes[id].iri = [].concat.apply([], nodes[id].iri)
			.filter(etyBase.helpers.onlyUnique); 
		}
	    }
	    
	    for (var n in nodes) {
		nodes[n].label = nodes[n].iri
		    .map((id) => {
			return etymologyEntries.values[id].label;
		    })
		    .filter(etyBase.helpers.onlyUnique)
		    .join(",");
		nodes[n].posAndGloss = nodes[n].iri.map((id) => {
		    return etymologyEntries.values[id].posAndGloss;
		});
		nodes[n].urlAndLabel = nodes[n].iri.map((id) => { 
		    return etymologyEntries.values[id].urlAndLabel;
		});
	    }
	    
	    return nodes;
	};
	
	/**
	 * Renders the Graph of Ancestors.
	 * @function renderAncestorsGraphPage
	 * @param {Graph} g
	 */
	var renderAncestorsGraphPage = function(g) {
	    d3.select("#message")
		.html(MESSAGE.clickForDescendants);
	    d3.select("#helpPopup")
		.html(HELP.dagre);
	    
	    g.render("#tree-container", "tree-overlay", width, height)
		.selectAll("g.node")
		.on("click", d => {
		    var node = g.dagre.node(d);
		    renderDescendantsDialog(node);
		});		   
	};

	/**
	 * Renders a no etymology message for the node
	 * @function renderNoEtymologyPage
	 * @param {Node} node
	 */
        var renderNoEtymologyPage = function(node) {
            d3.select("#message").html("");
	    d3.select("#message")   
		.append("div")  
		.html(MESSAGE.noEtymology);  
	    d3.select("#message").append("a") 
		.attr("href", etyBase.DATAMODEL.wikiLink(node.label, node.lang))
		.attr("target", "_blank")  
		.text(node.lang + " " + node.label);
	}
	
	/**
         * Renders the Disambiguation Graph. 
         * @function renderDisambiguationGraphPage
	 * 
	 * @param {Graph} g
	 */
	var renderDisambiguationGraphPage = function(g) {		    
	    g.render("#tree-container", "tree-overlay", width, height)
		.selectAll("g.node")
		    .on("click", d => {
			var iri = g.dagre.node(d).iri[0];
			d3.select("#tree-overlay")
			    .remove();
			d3.select("#tooltipPopup")
			    .style("display", "none");
			d3.select("#message")
			    .attr("display", "inline")
			    .html(MESSAGE.loading);
			    
			etyBase.DATAMODEL.queryAncestors(iri, (etymologyEntries) => {
			    if (Object.keys(etymologyEntries.values).length < 2) {
				var node = etymologyEntries.values[Object.keys(etymologyEntries.values)[0]];
				renderNoEtymologyPage(node);
			    } else {
				var gAncestors = new etyBase.GRAPH.Graph("TB", { nodes: nodesFrom(etymologyEntries), edges: etymologyEntries.edges }, width);
				renderAncestorsGraphPage(gAncestors);
			    }
			});
		    });
	};

	var renderDescendantsAccordion = function(node, response) {	    
	    $("#descendants")  
		.dialog({ title: "Descendants of " + node.lang + " " + node.label});
	    var accordion = d3.select("#descendants")   
		.append("div") 
		.attr("id", "accordion");    
	
	    var languages = Object.keys(response)
		.map((iri) => {
		    return new etyBase.DATAMODEL.EtymologyEntry(iri).lang;
		})
		.filter(etyBase.helpers.onlyUnique)
	        .sort();

	    languages.map((lang, i) => {
		    $("#accordion").append("<h3>" + lang + "</h3>");
		    $("#accordion").append("<div id=\"div" + i + "\"></div>");  
		});

	    //collapse all windows in accordion 
	    //when a window is activated, render language graph  
	    $("#accordion").accordion({    
		collapsible: "true",   
		activate: function(event, ui) {    
		    var language = ui.newHeader.text(),
		    index = languages.indexOf(language), 
		    languageResponse = {},
		    languageIris = [];

                    Object.keys(response).forEach((iri) => {
			if (response[iri].lang === language) {
			    languageIris.push(iri);
			    languageResponse[iri] = response[iri];
			}
		    });
		    
		    if (languageIris.length > 40) {  
			renderLoadingDescendantsGraph(index);
		    }

		    return etyBase.DATAMODEL.queryGloss({ values: languageResponse })
			.subscribe((etymologyEntries) => {
			    //todo: sort based on label and not on iri
			    var g = new etyBase.GRAPH.Graph("LR", { nodes: nodesFrom(etymologyEntries) }, width);         
			    renderDescendantsGraph(index, g);        
			});
		},
		active: false 
	    });
	};
	
	var renderLoadingDescendantsGraph = function(index) {
	    //clean accordion   
	    d3.select("#accordionMessage")
		.remove();  
	    d3.select("#overlay" + index)
		.remove();  
	    
	    d3.select("#div" + index) 
		.append("div")  
		.style("text-align", "center") 
		.attr("id", "accordionMessage")    
		.html(MESSAGE.clickForAncestors + "<br><br>" + MESSAGE.loading); 
	    
	    d3.select("#div" + index) 
		.style("height", "60px");
	};

	/**
         * Renders the Graph of Descendants in the accordion.
         * @function renderDescendantsGraph
	 * 
	 * @param {Number} index - index indicating position in the accordion
	 * @param {Graph} g - a Graph with only descendants in one language
	 */
	var renderDescendantsGraph = function(index, g) {
	    //clean accordion                                          
	    d3.select("#accordionMessage")
		.remove();
	    d3.select("#overlay" + index)
		.remove();
	    
	    d3.select("#div" + index)
		.append("div")
		.style("text-align", "center")
		.attr("id", "accordionMessage")
		.html(MESSAGE.clickForAncestors);
	    
	    //render Dagre of language        
	    g.render("#div" + index, "overlay" + index, width, height)
		.selectAll("g.node")
		.on("click", d => {
		    //on click on node in language graph, render ancestorsGraph of clicked node    
		    $("#descendants")
			.dialog("close");
		    var iri = g.dagre.node(d).iri[0];
		    d3.select("#tree-overlay")
			.remove();
		    d3.select("#tooltipPopup")
			.style("display", "none");
		    d3.select("#message")
			.attr("display", "inline")
			.html(MESSAGE.loading);
		    
		    etyBase.DATAMODEL.queryAncestors(iri, (etymologyEntries) => {
			if (Object.keys(etymologyEntries.values).length < 2) {
			    var node = etymologyEntries.values[Object.keys(etymologyEntries.values)[0]];
			    renderNoEtymologyPage(node);
			} else {
			    var gAncestors = new etyBase.GRAPH.Graph("TB", { nodes: nodesFrom(etymologyEntries), edges: etymologyEntries.edges }, width);
			    renderAncestorsGraphPage(gAncestors);
			}
		    });
		    $(search).val(g.dagre.node(d).label);
		});

	    //resize language graph         
	    var h = Math.min(g.dagre.graph().height + 50, window.innerHeight - 15);
	    d3.select("#div" + index)
		.attr("style", "height:" + h + "px;");
	};
	
	/**
	 * Renders the page that will contain the Graph of Descendants 
	 * of a specified Node; queries the database to get pos, gloss and links.
	 * @function renderDescendantsDialog
	 * @param {Node} node
	 */
	var renderDescendantsDialog = function(node) {
	    //open dialog   
	    d3.select("#descendants")
		.remove();
 	    d3.select("#tree-container")
		.append("div")
		.attr("id", "descendants");
	    $("#descendants")
		.dialog({
		    title: "Loading descendants...",
		    autoOpen: false,
		    width: width - 15,
		    height: height - 15,
		    position: "top"
		});
 	    $("#descendants")
		.dialog("open");
	    
 	    etyBase.DATAMODEL.queryDescendants(node)
		.subscribe((response) => {
                    renderDescendantsAccordion(node, response);
		});
	};
	
	/**
         * Renders the page that will contain the Etymology Graph 
	 * of a specified lemma.
	 * @function renderSearchPage
	 * @param {String} lemma - e.g., "door" 
	 */
	var renderSearchPage = function(lemma) {
	    if (lemma.length < 2)
		//if lemma has length 1 but it is a foreign character (e.g. Chinese) don't return
		if (etyBase.config.notForeign.test(lemma)) 
		    return;
	    
	    etyBase.DATAMODEL.queryDisambiguation(lemma)
		.subscribe((response) => {
                    //clean screen
                    d3.select("#tree-overlay")
			.remove();
                    d3.select("#tooltipPopup")
			.style("display", "none");
		    
		    var N = Object.keys(response).length;
		    
		    if (N === 0) {
			d3.select("#message")
			    .attr("display", "inline")
			    .html(MESSAGE.notAvailable);
		    } else if (N === 1) {
			var iri = Object.keys(response)[0];
			d3.select("#message")
			    .attr("display", "inline")
			    .html(MESSAGE.loading);
			
			etyBase.DATAMODEL.queryAncestors(iri, (etymologyEntries) => {
			    if (Object.keys(etymologyEntries.values).length < 2) {
				var node = etymologyEntries.values[Object.keys(etymologyEntries.values)[0]];
				renderNoEtymologyPage(node);
			    } else {
				var gAncestors = new etyBase.GRAPH.Graph("TB", { nodes: nodesFrom(etymologyEntries), edges: etymologyEntries.edges }, width);
				renderAncestorsGraphPage(gAncestors);
						    }
			});
		    } else {			
			d3.select("#helpPopup")
					        .html(HELP.disambiguation);
                        d3.select("#message")
			    .attr("display", "inline")  
                            .html(MESSAGE.disambiguation);
			
                        etyBase.DATAMODEL.queryDisambiguationGloss(response)
			    .subscribe((etymologyEntries) => {
				var g = new etyBase.GRAPH.Graph("LR", { nodes: nodesFrom(etymologyEntries) }, width);
				renderDisambiguationGraphPage(g);
			    });
		    }
		});
	};
	
	/**
         * Initializes app.
         * @function init
	 */
	var init = function() {
	    
	    d3.select("#helpPopup")
		.html(HELP.intro);
	    
	    d3.select("body").append("div")
		.attr("data-role", "popup")
		.attr("data-dismissible", "true")
		.attr("id", "tooltipPopup")
		.style("display", "none")
		.attr("class", "ui-content tooltip");
	    
	    d3.select(window)
                .on("click", () => 
		    d3.select("#tooltipPopup")
		    .style("display", "none")
		   );
	    
	    d3.select("#tooltipPopup")
                .on("click", e => e.stopPropagation());
	    
	    $("#search").on("keypress", e => {
		var search = this;
		if (e.which === 13) {
		    var lemma = $(search).val();
		    renderSearchPage(lemma);
		}
	    });
	    
	    d3.select("#btnSearch").on("click", () => {
		var lemma = $("#search").val();
		renderSearchPage(lemma);
	    });
	    
	};
	
	this.init = init;

	etyBase[moduleName] = this;
    };
    
    return module;
    
})(APP || {});
