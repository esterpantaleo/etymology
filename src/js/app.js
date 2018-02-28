/**
 * @module APP
 * @requires TOUR
 * @requires DATAMODEL  
 * @requires GRAPH
 */

const d3 = require('d3');
const $ = require('jquery');
require('webpack-jquery-ui');
require('webpack-jquery-ui/css');
const page = require('page');

const TOUR = require('./livetour');
const DATAMODEL = require('./datamodel');
const GRAPH = require('./graph');

const width = window.innerWidth,
      height = window.innerHeight,
      HELP = {
	  intro: "Enter a word in the search bar, then press enter or click on OK.",
	  disambiguation: "<b>Disambiguation page</b>" +
              "<br>Pick the word you are interested in." +
              "<ul>" +
              "<li>Mouse over a node to display lexical information</li>" +
              "<li>Click on a node to choose a word</li>" +
              "</ul>",
	  dagre: "Arrows go from ancestor to descendant.<ul>" +
              "<li>Mouse over a node to display lexical information</li>" +
              "<li>Click on a node to display its descendants, grouped by language</li>" +
              "</ul>",
	  noAncestors: "The searched word is not available. Try with a different word."
      },
      MESSAGE = {
	  notAvailable: "This word is not available in the database.",
	  loading: "Loading, please wait...",
	  noAncestors: "Etytree could not extract the etymology of this word from the English Wiktionary, <br>or there is no etymology in the English Wiktionary for this word. <br><br><br>Add/edit etymology of ",
	  disambiguation: "Click on the word you are interested in:",
	  clickForAncestors: "Click on a word to see its ancestors:",
	  clickForDescendants: "Click on a word to see its descendants:",
	  pageNotFound: "Page not found."
      },
      ETYTREE = {
	  title: "etytree",
	  subtitle: "a graphical and multilingual etymology dictionary", 
	  instructions: "Type a word in the search bar, then press enter.<br>" +
	      "You will see a list of words.<br>" +
	      "Explore the different words by mousing/tapping over them.<br>" +
	      "Click on the word you are interested in.",
	  description: "etytree can visualize the etymological tree of a word, i.e., <br>" +
	      "the etymology - or the origin of a word and the historical development of its meaning - in the form of a tree,<br>" +
	      "reconstructing its ancestral form, its cognate words, its derived words, etc.",
	  version: "This first version uses directed graphs instead of trees and extracts and parses data automatically from a dump of the English Wiktionary.",
          info: "This project is open source (source code " +
	      "<a href=\"https://github.com/esterpantaleo/etymology\">javascript</a>, "+
	      "<a href=\"https://bitbucket.org/esterpantaleo/dbnary_etymology\">java</a>) " +
	      "and uses <a href=\"http://d3js.org/\">d3</a> " +
	      "and <a href=\"https://www.wiktionary.org/\">Wiktionary</a>. " +
	      "There is a SPARQL endpoint <a href=\"http://etytree-virtuoso.wmflabs.org/sparql\">here</a>. " +
	      "Data is under CC BY-SA.<br>" +
	      "In the current version you cannot edit the graph, you can indirectly edit it " +
	      "by editing Etymology sections of words in the English Wiktionary. <br>" +
              "At each new release the database will be updated and the edits will be reflected into the graph.<br>" +
              "If you would like to contribute email esterpantaleo at gmail dot com " +
	      "or contact Wikimedia user <a href=\"https://meta.wikimedia.org/wiki/User_talk:Epantaleo\">Epantaleo</a>. " +
	      "If you spot a bug please open an issue <a href=\"https://github.com/esterpantaleo/etymology/issues\">here</a>.<br>"
      },
      notForeign = /^[a-z\u00C0-\u00F6\u00F8-\u017E]+$/i;

/**
 * This function transforms an object consisting of EtymologyEntry-s,
 * into an object consisting of Nodes
 * @function nodesFrom
 * @param {Object.<EtymologyEntry>} etymologyEntries
 * @param {String} langIsoCode - language iso code, e.g.: "eng"
 * @return {Object.<Node>} a list of Nodes
 */
var nodesFrom = (etymologyEntries, langIsoCode) => {
    var nodes = {};
    
    var counter = 0;
    for (var n in etymologyEntries.values) {
	if (langIsoCode === null || langIsoCode === undefined || etymologyEntries.values[n].iso === langIsoCode) {
	    var id = etymologyEntries.values[n].node;
	    
	    if (undefined === id) {
		id = counter;
		nodes[id] = new GRAPH.Node(id, etymologyEntries.values[n]);
		counter++;
	    } else if (undefined === nodes[id]) {
		nodes[id] = new GRAPH.Node(id, etymologyEntries.values[n]);
	    } else {
		nodes[id].iri = nodes[id].iri.concat(etymologyEntries.values[n].iri);
		nodes[id].iri = [].concat.apply([], nodes[id].iri);
		nodes[id].iri = [...new Set(nodes[id].iri)];
	    }
	    nodes[id].ety = etymologyEntries.values[n].ety;
	}
    }
    
    for (var n in nodes) {
	nodes[n].label = nodes[n].iri
	    .map((id) => {
		return etymologyEntries.values[id].label;
	    });
	nodes[n].label = [...new Set(nodes[n].label)]
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
 * @function ancestorsGraphPage
 * @param {Graph} g
 */
var ancestorsGraphPage = (g) => {
    d3.select("#tree-overlay")
	.remove();
    d3.select("#glossPopup")
	.remove();
    
    d3.select("#helpPopup")
	.html(HELP.dagre);
    d3.select("#tree-container")
	.append("div")
	.attr("id", "tree-overlay");
        
    g.render("#tree-overlay", "ancestors", width, height)
	.selectAll("g.node")
	.on("click", (d) => {
	    var node = g.dagre.node(d);
	    descendantsDialog(node);
	});

    d3.select("#ancestors")
	.append("g")
	.append("text")
	.attr("id", "message")
	.attr("x", 30)
	.attr("y", 30)
	.attr("width", width)
	.html(MESSAGE.clickForDescendants);  
};

/**
 * Renders a no etymology message for the node
 * @function noAncestorsPage
 * @param {Node} node
 */
var noAncestorsPage = (node) => {
    d3.select("#tree-overlay")
	.remove();
    
    d3.select("#helpPopup")
	.html(HELP.noAncestors);  
    d3.select("#tree-container")
	.append("div")
	.attr("id", "tree-overlay")
	.append("div")
	.attr("id", "message")
	.html(MESSAGE.noAncestors);
    d3.select("#message")
	.append("a")
	.attr("href", DATAMODEL.wiktionaryLink(node.label, node.lang))
	.attr("target", "_blank")
	.text(node.lang + " " + node.label);
};

/**
 * Renders the Disambiguation Graph.
 * @function disambiguationGraphPage
 * @param {Graph} g
 */
var disambiguationGraphPage = (g) => {
    d3.select("#tree-overlay")
	.remove();
    d3.select("#glossPopup")
	.remove();

    d3.select("#helpPopup")
	.html(HELP.disambiguation);
    d3.select("#tree-container")
	.append("div")
	.attr("id", "tree-overlay");
    
    g.render("#tree-overlay", "disambiguation", width, height)
	.selectAll("g.node")
	.on("click", (d) => {
	    var node = g.dagre.node(d);
	    $(search).val(node.label);
	    window.location = "label=" + node.label + "&lang=" + node.iso + "&ety=" + node.ety; 
	});

    d3.select("#disambiguation")
	.append("g")
	.append("text")
	.attr("id", "message")
	.attr("x", 30)
	.attr("y", 30)
	.attr("width", width)
	.html(MESSAGE.disambiguation); 
};

/**
 * Renders the Accordion with descendants of node.
 * @function descendantsAccordion
 *
 * @param {Node} node
 * @param {Object} response
 */
var descendantsAccordion = (node, response) => {
    $("#descendants")
	.dialog({title: "Descendants of " + node.lang + " " + node.label});
    var accordion = d3.select("#descendants")
	.append("div")
	.attr("id", "accordion");
    
    var languages = Object.keys(response)
	.map((iri) => {
	    return new DATAMODEL.EtymologyEntry(iri).lang;
	});
    languages = [...new Set(languages)]
	.sort();
    
    languages.map((lang, i) => {
	$("#accordion").append("<h3>" + lang + "</h3>");
	$("#accordion").append("<div id=\"div" + i + "\"></div>");
    });
    
    //collapse all windows in accordion
    //when a window is activated, render language graph
    $("#accordion").accordion({
	collapsible: "true",
	activate: function (event, ui) {
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
		loadingDescendantsGraph(index);
	    }
	    
	    return DATAMODEL.queryGloss({values: languageResponse})
		.subscribe((etymologyEntries) => {
		    //todo: sort based on label and not on iri
		    var g = new GRAPH.Graph("LR", {nodes: nodesFrom(etymologyEntries)}, width);
		    descendantsGraph(index, g);
		});
	},
	active: false
    });
};

/**
 * Renders the loading message when loading the descendants graph.
 * @function loadingDescendantsGraph
 * @param {Number} index
 */
var loadingDescendantsGraph = (index) => {
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
 * Renders the graph of ancestors of a specified
 * word (label) in a specified language (langIsoCode) 
 * using data from Wiktionary etymology number ety.
 * @function etymologyGraphPage
 * @param {String} iri
 */
var etymologyGraphPage = (iri) => {
    etytreeLoading();

    DATAMODEL.queryAncestors(iri, (etymologyEntries) => {
	if (Object.keys(etymologyEntries.values).length < 2) {
	    var node = etymologyEntries.values[Object.keys(etymologyEntries.values)[0]];
	    noAncestorsPage(node);
	} else {
	    var gAncestors = new GRAPH.Graph("TB", {
		nodes: nodesFrom(etymologyEntries),
		edges: etymologyEntries.edges
	    }, width);
	    ancestorsGraphPage(gAncestors);
	}
    });
};

/**
 * Renders the Graph of Descendants in the accordion.
 * @function descendantsGraph
 * @param {Number} index - index indicating position in the accordion
 * @param {Graph} g - a Graph with only descendants in one language
 */
var descendantsGraph = (index, g) => {
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
    g.render("#div" + index, "overlay" + index, 0.9 * width, height)
	.selectAll("g.node")
	.on("click", d => {
	    //on click on node in language graph, render ancestorsGraph of clicked node
	    $("#descendants")
		.dialog("close");
	    d3.select("#tree-overlay")
		.remove();
	    d3.select("#glossPopup")
		.remove();

	    var node = g.dagre.node(d);
	    $(search).val(node.label);
	    window.location = "label=" + node.label + "&lang=" + node.iso + "&ety=" + node.ety;

	});
    
    //resize language graph
    var h = Math.min(g.dagre.graph().height + 80, window.innerHeight - 15);
    d3.select("#div" + index)
	.attr("style", "height:" + h + "px;");
};

/**
 * Renders the page that will contain the Graph of Descendants
 * of a specified Node; queries the database to get pos, gloss and links.
 * @function descendantsDialog
 * @param {Node} node
 */
var descendantsDialog = (node) => {
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
	    width: width - 6,
	    height: height - 5,
	    position: "top"
	});
    d3.select("#glossPopup")
	.remove();
    $("#descendants")
	.dialog("open");
    
    DATAMODEL.queryDescendants(node)
	.subscribe((response) => {
	    descendantsAccordion(node, response);
	});
};

/**
 * Renders the page corresponding to a search
 * of a specified word (label) in a specific language 
 * (if specified) with a specific value of ety (if specified).
 * @function searchPage
 * @param {String} label - e.g., "door"
 * @param {String} langIsoCode - language iso code, e.g., "eng" 
 * @param {Number} ety 
 */
var searchPage = (label, langIsoCode, ety) => {
    if (label.length < 2)
	//if label has length 1 but it is not foreign character
	//(e.g. Chinese) return (as we don't want to show the
	//etymology of single English characters
	if (notForeign.test(label))
	    return;
    
    if (label !== null && langIsoCode !== null && ety !== null) {
	var iri = DATAMODEL.etytreeLink(label, langIsoCode, ety);
	etymologyGraphPage(iri);
	return;
    }
    
    DATAMODEL.queryDisambiguation(label)
	.subscribe((response) => {
	    var N = Object.keys(response).length;
	    if (N === 0) {
		
		d3.select("#tree-overlay")
		    .remove();
		
		d3.select("#tree-container")
		    .append("div")
		    .attr("id", "tree-overlay")
		    .append("p")
		    .attr("id", "message")
		    .attr("display", "inline")
		    .html(MESSAGE.notAvailable);
		
	    } else if (N === 1) {
		
		var iri = Object.keys(response)[0];
		etymologyGraphPage(iri);
		
	    } else {		
		DATAMODEL.queryDisambiguationGloss(response)
		    .subscribe((etymologyEntries) => {
			var nodes = nodesFrom(etymologyEntries, langIsoCode);
			if (Object.keys(nodes).length > 1) {
			
			    var g = new GRAPH.Graph("LR", {nodes: nodes}, width);
			    disambiguationGraphPage(g);
			    
			} else if (Object.keys(nodes).length === 1) {
			
			    var iri = nodes[0].iri[0];//todo: check this
			    etymologyGraphPage(iri);
			    
			}
		    });
	    }
	});
};

/**
 * Renders the title
 * @function etytreeTitle
 */
var etytreeTitle = () => {
    d3.select("#tree-overlay")
	.remove();
    d3.select("#tree-container")
        .append("div")
        .attr("id", "tree-overlay");
    var title = d3.select("#tree-overlay")
	.append("div")
	.attr("id", "title");
    title.append("span")
	.attr("class", "bigSpan")
	.html("<br>" + ETYTREE.title);
    title.append("span")
	.attr("class", "mediumSpan")
	.html("<br>" + ETYTREE.subtitle);
    title.append("div")
	.html("<br><br><br><br>");
}

/**
 * Renders the description
 * @function etytreeDescription
 */
var etytreeDescription = () => {
    var title = d3.select("#tree-overlay");
    title.append("span")
	.attr("class", "blueSpan")
	.html("<br><br><br><br><br><br>" + ETYTREE.instructions);
    title.append("div")
	.html("<br><br><br><br><br>" + ETYTREE.description + "<br><br>")
	.append("span")
	.attr("class", "redSpan")
	.html(ETYTREE.version);
    title.append("div")
	.html("<br>")
    title.append("span")
	.attr("class", "smallSpan")
	.html(ETYTREE.info);  
};

/**
 * Renders the hep popup of the main page
 * @function etytreeHelpPopup
 */
var etytreeHelpPopup = () => {
    d3.select("#helpPopup")
	.html(HELP.intro);
};

/**
 * Defines interactions with the search bar and the search button
 * @function etytreeSearchButton
 */
var etytreeSearchButton = () => {
    $("#search").on("keypress", (e) => {
	if (e.which === 13 || e.keyCode === 13) {
	    var label = $(search).val();
	    window.location = "label="+ label;
	}
    });
    d3.select("#btnSearch").on("click", () => {
	var label = $("#search").val();
	window.location = "label=" + label;
    });
};

/** 
 * Given a window location returns the current state of etytree
 * @function getState 
 * @param {String} location - e.g.: "label=door&lang=eng&ety=0"
 * @return {Object} an object with elements label, lang and ety
 */
var getState = (location) => {
    var state = {
	label: null,
	lang: null,
	ety: null
    };

    if (location !== "index.html" && location !== "") {
        var l = location.split("&");
	if (l[0] !== undefined) {
            var label = l[0].split("=");
            if (label.length === 2 && label[0] === "label") {
		state.label = label[1];
            } else {
		return state;
	    }
	}

	if (l[1] !== undefined) {
            var lang = l[1].split("=");
            if (lang.length === 2 && lang[0] === "lang") {
		//encode language lang = encode(lang); 
		state.lang = lang[1];
            } else {
                return state;
            }
	}

	if (l[2] !== undefined) {
            var ety = l[2].split("=");
            if (ety.length === 2 && ety[0] === "ety") {
		if (ety[1] >= 0) {
                    state.ety = ety[1];
		}
            }
	}
    }
    return state;
};

var etytreeLoading = () => {
    d3.select("#tree-overlay")
        .remove();
    d3.select("#glossPopup")
        .remove();

    d3.select("#tree-container")
        .append("div")
        .attr("id", "tree-overlay")
        .append("p")
        .append("text")
        .attr("id", "message")
        .attr("display", "inline")
        .html(MESSAGE.loading);
};

var etytreeNotFound = () => {
    d3.select("#tree-container")
        .append("div")
        .attr("id", "tree-overlay")
        .append("p")
        .attr("id", "message")
        .html(MESSAGE.pageNotFound);
};

var pageNotFoundCallback = () => {
    state = {
        label: null,
        lang: null,
        ety: null
    };

    etytreeNotFound();
};

var pageHomeCallback = () => {
    state = {
        label: null,
        lang: null,
        ety: null
    };

    etytreeTitle();
    etytreeDescription();
    etytreeHelpPopup();
};

var pageStateCallback = (context) => {
    var newState = getState(context.params.state);
    if (newState.label === null) {

	etytreeNotFound();
	
    } else {
	if (newState.label !== state.label || newState.lang !== state.lang || newState.ety !== state.ety) {
	 
            state = newState;
            $(search).val(state.label);

	    etytreeLoading(); 
            searchPage(state.label, state.lang, state.ety);
	}
    }
};

console.log("reading body");
var state = {
    label: null,
    lang: null,
    ety: null
};

etytreeSearchButton();
page('/', pageHomeCallback);    
page('/:state', pageStateCallback);
page('*', pageNotFoundCallback);
page();

