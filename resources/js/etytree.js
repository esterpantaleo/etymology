/*globals
  jQuery, $, d3, console, window
*/
var EtyTree = {
    create: function() {
        var etyBase = Object.create(this);
	
        etyBase.HELP = {
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
        };
	
        etyBase.MESSAGE = {
            notAvailable: "This word is not available in the database.",
            loading: "Loading, please wait...",
            serverError: "Sorry, the server cannot extract etymological relationships correctly for this word.",
            noEtymology: function(lang, label) {
		var url = etyBase.config.urls.WIKT;
                url += label.startsWith("*") ? ("Reconstruction:" + lang + "/" + label.replace("*", "")) : (label + "#" + lang);
                var htmlLink = etyBase.helpers.htmlLink(url, lang + " " + label);

		return "Etytree could not extract the etymology of this word from the English Wiktionary, <br>or there is no etymology in the English Wiktionary for this word. <br><br><br>Add/edit etymology of " + htmlLink;
	    },
            disambiguation: "There are multiple words in the database. <br>Click on the word you are interested in."
        };
	
        //HELPER FUNCTIONS
        etyBase.helpers = {
	    htmlLink: function(url, label) {
//		return append("a").attr("href", url).attr("target", "_blank").text(label);
		return "<a href=\"" + url + "\" target=\"_blank\">" + label + "</a>"; //use this instead $.("<a>").attr({href:url})
	    },
            onlyUnique: function(value, index, self) {
                return self.indexOf(value) === index;
            },
            serverError: function(error) {
                console.error(error);
                $("#tooltipPopup")
                    .attr("display", "none");
                $("#tree-overlay")
                    .remove();
                $("#message")
                    .css("display", "inline")
                    .html(etyBase.MESSAGE.serverError);
	    },
            debugLog: function(logText) {
                if (etyBase.config.debug) {
                    console.log(logText);
                }
            }
        };
	
        /* Binding Modules */
        var bindModules = function(base, modules) {
            for (var i = 0; i < modules.length; i++) {
                if (!window[modules[i]]) {
                    console.error('Module ' + modules[i] + ' is not loaded.');
                    return false;
                }
                window[modules[i]].bindModule(base, modules[i]);
            }
        };
	
        /* Setup basic settings */
        etyBase.config = {
            modules: ['DB', 'GRAPH', 'DATA', 'DATAMODEL', 'APP'],
            debug: false,
            urls: {
                ENDPOINT: "https://etytree-virtuoso.wmflabs.org/sparql",
                DBNARY_ENG: "http://etytree-virtuoso.wmflabs.org/dbnary/eng/",
                WIKT: "https://en.wiktionary.org/wiki/",
                WIKT_RECONSTRUCTION: "https://en.wiktionary.org/wiki/Reconstruction:"
            },
	    //depth of etyBase.DB.ancestorQuery
            depthAncestors: 5
        };
	
        bindModules(etyBase, etyBase.config.modules);
        return etyBase;
    },
    init: function() {
        var etyBase = this;
	
        etyBase.tree = {}; // This will get populated with data, LangMap, inner, g, and some other key items
	
        /* Load init function for every module */
        etyBase.config.modules.forEach((moduleName) => {
            if (etyBase[moduleName] && (typeof etyBase[moduleName].init === 'function')) {
                etyBase[moduleName].init();
            }
        });
    }
};

var ety;

jQuery(window).load(function($) {
    ety = EtyTree.create();
    ety.init();
});
//window.location.href
//var url = new URL(url_string
//var c 0 url.searchParams.get("c"
