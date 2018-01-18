/*globals
  $, d3, console, dagreD3, Rx, window, document, URLSearchParams
*/
/*jshint loopfunc: true, shadow: true, latedef: false */ // Consider removing this and fixing these

/**
 * @module APP 
 */
var APP = (function(module) {

	module.bindModule = function(base, moduleName) {
		var etyBase = base;

		/**
		 * Given an object consisting of Etymology Entries, 
		 * this function returns an object consisting of Nodes
		 * @function setNodes
		 * @param {Object}.<EtymologyEntry> a list of Etymology Entries
		 * @return {Object}.<Node> a list of Nodes
		 */
		var setNodes = function(etymologyEntries) {
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
		 * Render the Graph of Ancestors
		 * @function renderAncestors
		 * @params {Object}.<EtymologyEntry> a list of Etymology Entries
		 */
		var renderAncestors = function(etymologyEntries) {
			if (Object.keys(etymologyEntries.values).length < 2) {

				var node = etymologyEntries.values[Object.keys(etymologyEntries.values)[0]];
				$("#message")
					.html(etyBase.MESSAGE.noEtymology(node.lang, node.label));

			} else {

				$("#message")
					.html(etyBase.MESSAGE.clickForDescendants);
				$("#helpPopup")
					.html(etyBase.HELP.dagre);

				var g = new etyBase.GRAPH.Graph("TB", { nodes: setNodes(etymologyEntries), edges: etymologyEntries.edges });

				g.render("#tree-container", "tree-overlay")
					.selectAll("g.node")
					.on("click", function(d) {
						var node = g.dagre.node(d);
						showDescendants(node);
					});
		   
			}
		};

		/**
                 * Render the Disambiguation Graph 
                 * @function renderDisambiguation
		 * 
		 * @params {Object}.<EtymologyEntry> a list of Etymology Entries
		 */
		var renderDisambiguation = function(etymologyEntries) {
			var g = new etyBase.GRAPH.Graph("LR", { nodes: setNodes(etymologyEntries) });

			g.render("#tree-container", "tree-overlay")
				.selectAll("g.node")
				.on("click", function(d) {
					var iri = g.dagre.node(d).iri[0];
					showAncestors(iri);
				});
		};

		/**
                 * Render the Graph of Descendants in a specified language
                 * @function renderDescendantsInLanguage
		 * 
		 * @params {Graph} a Graph with all descendants in all languages
		 * @params {string} a language, e.g., "English"
		 */
		var renderDescendantsInLanguage = function(gg, language) {
			var index = gg.languages.indexOf(language);

			//clean accordion                                          
			d3.select("#accordionMessage")
				.remove();
			d3.select("#overlay" + index)
				.remove();

			d3.select("#div" + index)
				.append("div")
				.style("text-align", "center")
				.attr("id", "accordionMessage")
				.html(etyBase.MESSAGE.clickForAncestors);

			//render Dagre of language        
			var g = new etyBase.GRAPH.LanguageGraph("LR", gg, language);
			g.render("#div" + index, "overlay" + index)
				.selectAll("g.node")
				.on("click", function(d) {
					//on click on node in language graph, render ancestorsGraph of clicked node    
					$("#descendants")
						.dialog("close");
					var iri = g.dagre.node(d).iri[0];
					showAncestors(iri);
					$(search).val(g.dagre.node(d).label);
				});

			//resize language graph         
			var h = Math.min(g.dagre.graph().height + 50, window.innerHeight - 15);
			d3.select("#div" + index)
				.attr("style", "height:" + h + "px;");
		};

		/**
		 * Render a dialog box with
		 * an accordion, where each section of the accordion 
		 * displays the graph of descendants in a specific language.
		 * @function renderDescendants 
		 * @params {Node} the node whose descendants we are going to show
		 * @params {Object} a list of Etymology Entries, descendants of Node
		 */
		var renderDescendants = function(node, etymologyEntries) {
		         $("#descendants")
		                .dialog({ title: "Descendants of " + node.lang + " " + node.label});
			 var accordion = d3.select("#descendants")
				.append("div")
				.attr("id", "accordion");

			var g = new etyBase.GRAPH.Graph("LR", { nodes: setNodes(etymologyEntries) });
			g.setLanguages();
			g.languages.map((lang, i) => {
				$("#accordion").append("<h3>" + lang + "</h3>");
				$("#accordion").append("<div id=\"div" + i + "\"></div>");
			});
		
			//collapse all windows in accordion                  
			//when a window is activated, render language graph         
			$("#accordion").accordion({
				collapsible: "true",
				activate: function(event, ui) {
					var language = ui.newHeader.text();		
					renderDescendantsInLanguage(g, language);
				},
				active: false
			});
		};

		/**
		 * Render the page that will contain the Graph of Descendants 
		 * of a specified Node. It queries the database to get pos, gloss and links.
		 * @function showDescendants
		 * @params {Node} 
		 */
		var showDescendants = function(node) {
			//open dialog   
			$("#descendants")
				.remove();
 			d3.select("#tree-container")
				.append("div")
				.attr("id", "descendants");
			$("#descendants")
				.dialog({
					title: "Loading descendants...",
					autoOpen: false,
					width: $(window).width() - 15,
					height: $(window).height() - 15,
					position: "top"
				});
 			$("#descendants")
				.dialog("open");

 			etyBase.DATAMODEL.descendantsQuery(node, graphDescendants);
		};

		/**
		 * Render a "not available" message.
		 * @function showNotAvailable
		 * @params {Node} 
		 */
		var showNotAvailable = function() {
			//clean screen       
			$("#tree-overlay")
				.remove();
			d3.select("#tooltipPopup")
				.style("display", "none");
			$("#message")
				.css("display", "inline")
				.html(etyBase.MESSAGE.notAvailable);
		};

		/**
		 * Render the page that will contain the Graph of Ancestors 
		 * of an entry corresponding to a specified iri. It sequencially queries 
		 * the database to get the set of ancestors.
		 * @function showAncestors
		 * @params {iri} 
		 */
		var showAncestors = function(iri) {
			$("#tree-overlay")
				.remove();
			d3.select("#tooltipPopup")
				.style("display", "none");
			$("#message")
				.css("display", "inline")
				.html(etyBase.MESSAGE.loading);

			etyBase.DATAMODEL.ancestorsQuery(iri, renderAncestors);
		};

		/**
		 * Render the page that will contain the Disambiguation Graph. 
		 * It queries the database to get disambiguations.
		 * @function showDisambiguation
		 * @params {string} response of a query
		 */
		var showDisambiguation = function(response) {
			$("#tree-overlay")
				.remove();
			d3.select("#tooltipPopup")
				.style("display", "none");
			$("#helpPopup")
				.html(etyBase.HELP.disambiguation);
			$("#message")
				.css("display", "inline")
				.html(etyBase.MESSAGE.disambiguation);

			etyBase.DATAMODEL.disambiguationQuery(response, renderDisambiguation);

		};

		/**
                 * Render the page that will contain the Etymology Graph 
		 * of a specified lemma
		 * @function show
		 * @params {string} e.g., "door" 
		 */
		var show = function(lemma) {
		 	if (lemma.length < 2)
			    //if lemma has length 1 but it is a foreign character (e.g. Chinese) don't return
			    if (etyBase.config.notForeign.test(lemma)) 
				return;
			

			etyBase.DATAMODEL.disambiguation(lemma)
				.subscribe((response) => {
					var N = Object.keys(response).length;
			
					if (N === 0) {
						showNotAvailable();
					} else if (N === 1) {
						var iri = Object.keys(response)[0];
						showAncestors(iri);
					} else {			
						showDisambiguation(response);
					}
				});
		};

		/**
                 * Initializes app
                 * @function init
		 */
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
					show(lemma);
				}
			});

			$("#btnSearch").click(function(e) {
				var lemma = $("#search").val();
				show(lemma);
			});
	
		};

		this.init = init;

		etyBase[moduleName] = this;
	};

	return module;

})(APP || {});
