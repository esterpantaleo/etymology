/*globals 
    d3, Rx, console, XMLHttpRequest, vis
*/
var LOAD = (function(module) {

    module.bindModule = function(base, moduleName) {
        var etyBase = base;
        var HELP = {
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

        var MESSAGE = {
            notAvailable: "This word is not available in the database.",
            loading: "Loading, please wait.",
            serverError: "Sorry, the server cannot extract etymological relationships correctly for this word.",
            noEtymology: "Sorry, it seems like no etymology is available in the English Wiktionary for this word.",
            loadingMore: "Loading, please wait...",
	    disambiguation: "There are multiple words in the database. <br>Which word are you interested in?"
        };

	var nodeCount = -1;

	class Node {
	    constructor() {	       
		etyBase.LOAD.nodeCount ++;
		this.id = etyBase.LOAD.nodeCount;
		this.iri = [];
		this.all = []; 
		this.der = undefined;
		this.isAncestor = false;  
	    }
	    /*
	    static get counter() {
		Node._counter = (Node._counter || -1) + 1;
		return Node._counter;
		}*/
	}

	class tmpNode {
            constructor(iri, label) {
                this.id = iri;
                var tmp = this.parseIri(iri);
                this.label = label.replace(/^_/, '*');
                this.ety = tmp.ety;
		this.iso = tmp.iso;
                this.lang = etyBase.tree.langMap.get(this.iso);
                this.graphNode = [];
                this.eqIri = [];
                this.der = undefined;
                this.isAncestor = false;
            }

            logTooltip() {
                var query = etyBase.DB.lemmaQuery(this.id);
                var url = etyBase.config.urls.ENDPOINT + "?query=" + encodeURIComponent(query);

                if (etyBase.config.debug) {
                    console.log(url);
                }

                var that = this;

                const source = etyBase.DB.getXMLHttpRequest(url);
                source.subscribe(
                    function(response) {
                        var text = "<b>" + that.label + "</b><br><br><br>";
                        if (null !== response) {
                            //print definition  
                            var dataJson = JSON.parse(response).results.bindings;
                            text += dataJson.reduce(
                                function(s, element) {
                                    return s += that.logDefinition(element.pos, element.gloss);
                                },
                                ""
                            );
                            //print links 
                            text += "<br><br>Data is under CC BY-SA and has been extracted from: " +
                                that.logLinks(dataJson[0].links.value);
                        } else {
                            text += "-";
                        }
                        d3.select("#tooltipPopup")
			    .style("display", "inline")
                            .append("p")
                            .html(text);
                    },
                    function(error) {
                        console.error(error);
                        var text = "<b>" + that.label + "</b><br><br><br>";
                        text += "-";
                        d3.select("#tooltipPopup")
                            .append("p")
                            .html(text);
                    });
	    }

            parseIri(iri) {
                var iso, label, ety, tmp = iri
		.replace("http://etytree-virtuoso.wmflabs.org/dbnary/eng/", "")
		.split("/");

                if (tmp.length > 1) {
                    iso = tmp[0];
		    label = tmp[1];
                } else {
                    iso = "eng";
                    label = tmp[0];
                }
                //ety is an integer                 
                //and represents the etymology number encoded in the iri;
                //if ety === 0 the iri is __ee_word                                    
                //if ety === 1 the iri is __ee_1_word            
                //etc                                            
                ety = 0;
                if (null !== label.match(/__ee_[0-9]+_/g)) {
                    //ety is an integer specifying the etymology entry      
                    ety = label.match(/__ee_[0-9]+_/g)[0]
                        .match(/__ee_(.*?)_/)[1];
                }
		/*
                label = label.replace(/__ee_[0-9]+_/g, "")
                    .replace("__ee_", "")
                    .replace("__", "'")
                    .replace(/^_/g, "*")
                    .replace(/_/g, " ")
                    .replace("__", "'")
                    .replace(/_/g, " ")
                    .replace("%C2%B7", "·");
		*/
                return {iso: iso, ety: ety};
            }
	    
            logDefinition(pos, gloss) {
                if (undefined !== pos && undefined !== gloss) {
                    return gloss.value.split(";;;;").map(function(el) {
                        return pos.value + " - " + el + "<br><br>";
                    }).join("");
                } else {
                    return "-";
                }
            }

            logLinks(links) {
                return links.split(",")
                    .map(function(e) {
                        var linkName;
                        if (e.startsWith("https://en.wiktionary.org/wiki/Reconstruction")) {
                            linkName = e.replace(/https:\/\/en.wiktionary.org\/wiki\/Reconstruction:/g, "")
                                .split("/")
                                .join(" ");
                        } else {
                            linkName = e.split("/")
                                .pop()
                                .split("#")
                                .reverse()
                                .join(" ")
                                .replace(/_/g, " ");
                        }
                        return "<a href=\"" + e + "\" target=\"_blank\">" + linkName + "</a>";
                    }).join(", ");
	    }
        }

        var init = function() {
            //LOAD LANGUAGES
            //used to print on screen the language name when the user clicks on a node (e.g.: eng -> "English")      
            if (etyBase.config.debug) {
                console.log("loading languages");
            }

	    etyBase.tree.langMap = new Map();

	    var ssv = d3.dsv(";", "text/plain");
            ssv("./resources/data/etymology-only_languages.csv", function(data) {
		    data.forEach(function(entry) {
			    etyBase.tree.langMap.set(entry["code"], entry["canonical name"]);
			});
		});
            ssv("./resources/data/list_of_languages.csv", function(data) {
		    data.forEach(function(entry) {
			    etyBase.tree.langMap.set(entry["code"], entry["canonical name"]);
			});
		});
	    
            d3.text("./resources/data/iso-639-3.tab", function(error, textString) {
		    console.log(textString);
		    var headers = ["Id", "Part2B", "Part2T", "Part1", "Scope", "Language_Type", "Ref_Name", "Comment"].join("\t");
		    var data = d3.tsv.parse(headers + textString);
		    console.log(data)
		    data.forEach(function(entry) {
			    etyBase.tree.langMap.set(entry["Id"], entry["Ref_Name"]);
			});
		});
	    /*
	    const csv = require('csvtojson');
	    csv({delimiter:";"})
                .fromFile("./resources/data/etymology-only_languages.csv")
                .on('json', (jsonObj) => {
                        etyBase.tree.langMap.set(jsonObj["code"], jsonObj["canonical name"]);
                    });
	    csv({delimiter:";"})
                .fromFile("./resources/data/list_of_languages.csv")
                .on('json', (jsonObj) => {
                        etyBase.tree.langMap.set(jsonObj["code"], jsonObj["canonical name"]);
                    });
	    csv({delimiter:"\t", headers:["Id", "Part2B", "Part2T", "Part1", "Scope", "Language_Type", "Ref_Name", "Comment"]})
                .fromFile("./resources/data/iso-639-3.tab")
                .on('json', (jsonObj) => {
                        etyBase.tree.langMap.set(jsonObj["Id"], jsonObj["Ref_Name"]);
                    });
	    */
	};
    

        this.HELP = HELP;
        this.MESSAGE = MESSAGE;
	this.nodeCount = nodeCount;
        this.classes = {
            tmpNode: tmpNode,
            Node: Node
        };
        this.init = init;

        etyBase[moduleName] = this;
    };

    return module;

})(LOAD || {});
