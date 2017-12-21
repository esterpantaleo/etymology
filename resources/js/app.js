/*globals
  $, d3, console, dagreD3, Rx, window, document, URLSearchParams
*/
/*jshint loopfunc: true, shadow: true, latedef: false */ // Consider removing this and fixing these
var APP = (function(module) {

	module.bindModule = function(base, moduleName) {
		var etyBase = base;

		/**
		 * Given an object consisting of Etymology Entries, 
		 * this function returns an object consisting of Nodes
		 *
		 * @param {Object} a list of Etymology Entries
		 * @return {Object} a list of Nodes
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
		 * Graphically displays the Graph of Ancestors
		 * 
		 * @params {Object} a list of Etymology Entries
		 */
		var graphAncestors = function(etymologyEntries) {
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
		 * Graphically displays the Disambiguation Graph
		 * 
		 * @params {Object} a list of Etymology Entries
		 */
		var graphDisambiguation = function(etymologyEntries) {
			var g = new etyBase.GRAPH.Graph("LR", { nodes: setNodes(etymologyEntries) });

			g.render("#tree-container", "tree-overlay")
				.selectAll("g.node")
				.on("click", function(d) {
					var iri = g.dagre.node(d).iri[0];
					showAncestors(iri);
				});
		};

		/**
		 * Graphically displays the Graph of Descendants in a specified language
		 * 
		 * @params {Graph} a Graph with all descendants in all languages
		 * @params {string} a language, e.g., "English"
		 */
		var graphDescendantsInLanguage = function(gg, language) {
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
			var h = Math.min(g.dagre.graph().height + 40, window.innerHeight - 15);
			d3.select("#div" + index)
				.attr("style", "height:" + h + "px;");
		};

		/**
		 * Graphically displays a dialog box with
		 * an accordion, where each section of the accordion 
		 * displays the graph of descendants in a specific language.
		 * 
		 * @params {Object} a list of Etymology Entries
		 */
		var graphDescendants = function(etymologyEntries) {
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
					graphDescendantsInLanguage(g, language);
				},
				active: false
			});
		};

		/**
		 * Graphically displays the page that will contain the Graph of Descendants 
		 * of a specified Node. It queries the database to get pos, gloss and links.
		 *
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
					title: "descendants of " + node.lang + " " + node.label,
					autoOpen: false,
					width: $(window).width() - 15,
					height: $(window).height() - 15,
					position: "top"
				});
 			$("#descendants")
				.dialog("open");

 			etyBase.DATAMODEL.descendantsQuery(node.iri, graphDescendants);
		};

		/**
		 * Graphically displays a "not available" message.
		 *
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
		 * Graphically displays the page that will contain the Graph of Ancestors 
		 * of an entry corresponding to a specified iri. It sequencially queries 
		 * the database to get the set of ancestors.
		 *
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

			etyBase.DATAMODEL.ancestorsQuery(iri, graphAncestors);
		};

		/**
		 * Graphically displays the page that will contain the Disambiguation Graph. 
		 * It queries the database to get disambiguations.
		 *
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

			etyBase.DATAMODEL.disambiguationQuery(response, graphDisambiguation);

		};

		/**
		 * Graphically displays the page that will contain the Etymology Graph 
		 * of a specified lemma
		 *
		 * @params {string} e.g., "door" 
		 */
		var show = function(lemma) {
		 	if (lemma.length < 2) {
				return;
			}

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
