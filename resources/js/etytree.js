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
                "</ul>"
        };

        etyBase.MESSAGE = {
            notAvailable: "This word is not available in the database.",
            loading: "Loading, please wait.",
            serverError: "Sorry, the server cannot extract etymological relationships correctly for this word.",
            noEtymology: "Sorry, it seems like no etymology is available in the English Wiktionary for this word.",
            loadingMore: "Loading, please wait...",
            disambiguation: "There are multiple words in the database. <br>Which word are you interested in?"
        };

        //HELPER FUNCTIONS
        etyBase.helpers = {
            onlyUnique: function(value, index, self) {
                return self.indexOf(value) === index;
            },
            serverError: function(error) {
                console.error(error);

                $("#message")
                    .css("display", "inline")
                    .html(etyBase.LOAD.MESSAGE.serverError);
            },
            lemmaNotStartsOrEndsWithDash: function(iri) {
                var tmp = iri.replace("http://etytree-virtuoso.wmflabs.org/dbnary/eng/", "")
                    .split("/");
                var label = (tmp.length > 1) ? tmp[1] : tmp[0]; 
                label = label.replace(/__ee_[0-9]+_/g, "")
                    .replace("__ee_", "");

               return (label.startsWith("-") || (label.endsWith("-") && !label.startsWith("_"))) ? false : true;
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
            modules: ['DB', 'GRAPH', 'LOAD'],
            debug: false,
            urls: {
                ENDPOINT: "https://etytree-virtuoso.wmflabs.org/sparql"
            }
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
