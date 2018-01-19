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
		var etyBase = base;

		/**
		 * Given an object consisting of Etymology Entries, 
		 * this function returns an object consisting of Nodes
		 * @function setNodes
		 * @param {Object}.<DATAMODEL~EtymologyEntry> a list of Etymology Entries
		 * @return {Object}.<GRAPH~Node> a list of Nodes
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
		 * @function renderAncestorsGraphPage
		 * @params {Object}.<DATAMODEL~EtymologyEntry> a list of Etymology Entries
		 */
		var renderAncestorsGraphPage = function(etymologyEntries) {
			if (Object.keys(etymologyEntries.values).length < 2) {

				var node = etymologyEntries.values[Object.keys(etymologyEntries.values)[0]];
				d3.select("#message")
					.html(etyBase.MESSAGE.noEtymology(node.lang, node.label));

			} else {

				d3.select("#message")
					.html(etyBase.MESSAGE.clickForDescendants);
				d3.select("#helpPopup")
					.html(etyBase.HELP.dagre);

				var g = new etyBase.GRAPH.Graph("TB", { nodes: setNodes(etymologyEntries), edges: etymologyEntries.edges });

				g.render("#tree-container", "tree-overlay", window.innerWidth, window.innerHeight - $("#header").height())
					.selectAll("g.node")
					.on("click", d => {
						var node = g.dagre.node(d);
						renderDescendantsDialog(node);
					});
		   
			}
		};

		/**
                 * Render the Disambiguation Graph 
                 * @function renderDisambiguationGraphPage
		 * 
		 * @params {Object}.<EtymologyEntry> a list of Etymology Entries
		 */
		var renderDisambiguationGraphPage = function(etymologyEntries) {
			var g = new etyBase.GRAPH.Graph("LR", { nodes: setNodes(etymologyEntries) });

			g.render("#tree-container", "tree-overlay")
				.selectAll("g.node")
				.on("click", d => {
					var iri = g.dagre.node(d).iri[0];
					d3.select("#tree-overlay")
					    .remove();
					d3.select("#tooltipPopup")
					    .style("display", "none");
					d3.select("#message")
					    .attr("display", "inline")
					    .html(etyBase.MESSAGE.loading);

					etyBase.DATAMODEL.ancestorsQuery(iri, renderAncestorsGraphPage);
				});
		};

		/**
                 * Render the Graph of Descendants in a specified language
                 * @function renderDescendantsGraphInLanguage
		 * 
		 * @params {Graph} a Graph with all descendants in all languages
		 * @params {string} a language, e.g., "English"
		 */
		var renderDescendantsGraphInLanguage = function(gg, language) {
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
					    .html(etyBase.MESSAGE.loading);

					etyBase.DATAMODEL.ancestorsQuery(iri, renderAncestorsGraphPage);
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
		 * @function renderDescendantsAccordion 
		 * @params {Node} the node whose descendants we are going to show
		 * @params {Object} a list of Etymology Entries, descendants of Node
		 */
		var renderDescendantsAccordion = function(node, etymologyEntries) {
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
					renderDescendantsGraphInLanguage(g, language);
				},
				active: false
			});
		};

		/**
		 * Render the page that will contain the Graph of Descendants 
		 * of a specified Node. It queries the database to get pos, gloss and links.
		 * @function renderDescendantsDialog
		 * @params {Node} 
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
					width: $(window).width() - 15,
					height: $(window).height() - 15,
					position: "top"
				});
 			$("#descendants")
				.dialog("open");

 			etyBase.DATAMODEL.descendantsQuery(node, renderDescendantsAccordion);
		};

		/**
                 * Render the page that will contain the Etymology Graph 
		 * of a specified lemma
		 * @function renderSearchPage
		 * @params {string} e.g., "door" 
		 */
		var renderSearchPage = function(lemma) {
		 	if (lemma.length < 2)
			    //if lemma has length 1 but it is a foreign character (e.g. Chinese) don't return
			    if (etyBase.config.notForeign.test(lemma)) 
				return;

			etyBase.DATAMODEL.disambiguation(lemma)
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
						.html(etyBase.MESSAGE.notAvailable);
					} else if (N === 1) {
					    var iri = Object.keys(response)[0];
					    d3.select("#message")
						.attr("display", "inline")
						.html(etyBase.MESSAGE.loading);

					    etyBase.DATAMODEL.ancestorsQuery(iri, renderAncestorsGraphPage);
					} else {			
					    d3.select("#helpPopup")
					        .html(etyBase.HELP.disambiguation);
                                            d3.select("#message")
					        .attr("display", "inline")  
                                                .html(etyBase.MESSAGE.disambiguation);

                                            etyBase.DATAMODEL.disambiguationQuery(response, renderDisambiguationGraphPage);
					}
				});
		};

		/**
                 * Initializes app
                 * @function init
		 */
		var init = function() {

		        d3.select("#helpPopup")
				.html(etyBase.HELP.intro);

			d3.select("body").append("div")
				.attr("data-role", "popup")
				.attr("data-dismissible", "true")
				.attr("id", "tooltipPopup")
				.style("display", "none")
				.attr("class", "ui-content tooltipDiv");

			d3.select(window)
                                .on("click", () => d3.select("#tooltipPopup").style("display", "none"));

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
                                var lemma = d3.select("#search").property("value");	
				renderSearchPage(lemma);
			});
	
		};

		this.init = init;

		etyBase[moduleName] = this;
	};

	return module;

})(APP || {});
