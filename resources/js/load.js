/*globals 
    d3, Rx, SPARQL, console, XMLHttpRequest
*/
//SET PARAMETERS FOR FUNCTION d3.xhr() 
var ENDPOINT = "https://etytree-virtuoso.wmflabs.org/sparql";

//ADD CONFIGURATION VARIABLE DEFINITIONS
var debug,
    langMap,
    ssv;

var HELP = {
    intro: "Enter a word in the search bar, then press enter or click.",
    disambiguation: "<b>Disambiguation page</b>" +
    "<br>Pick the word you are interested in." +
    "<ul>" +
    "<li>Click on a node to display lexical information</li>" +
    "<li>Click on the language tag under the node to display the language</li>" + 
    "<li>Double click on a node to choose a word</li>" +
    "</ul>",
    dagre: "Arrows go from ancestor to descendant.<ul>" + 
    "<li>Click on a node to display lexical information</li>" +
    "<li>Click on the language tag under the node to display the language</li>" +
    "</ul>"
};

var MESSAGE = {
    notAvailable: "This word is not available in the database",
    loading: "Loading, please wait...",
    serverError: "Sorry, the server cannot extract etymological relationships correctly for this word.",
    noEtymology: "Sorry, it seems like no etymology is available in the English Wiktionary for this word.",
    loadingMore: "This word is related to many words. Please wait a bit more."
};

function getXMLHttpRequest(url) {
    return Rx.Observable.create(observer => {
        const req = new XMLHttpRequest();
        req.open('GET', url);
        req.overrideMimeType('application/sparql-results+json');
        req.onload = () => {
            if (req.status === 200) {
                observer.next(req.responseText);
                observer.complete();
            } else {
                observer.error(new Error(req.statusText));
            }
        };
        req.onerror = () => {
            observer.error(new Error('An error occured'));
        };
        req.setRequestHeader('Accept', 'application/json, text/javascript');
        req.send();
    });
}

function onlyUnique(value, index, self){
    return self.indexOf(value) === index;
}

function transform(d) {
    return "translate(" + d.x + "," + d.y + ")";
}

function logDefinition(pos, gloss) {
    if (undefined !== pos && undefined !== gloss) {
        return gloss.value.split(";;;;").map(function(el) {
            return pos.value + " - " + el + "<br><br>";
        }).join("");
    } else {
        return "-";
    }
}

function logLinks(links) {
    var toreturn = [];
    links.split(",").forEach(function(e) {
        var linkName;
        if (e.startsWith("https://en.wiktionary.org/wiki/Reconstruction")) {
            linkName = e.replace(/https:\/\/en.wiktionary.org\/wiki\/Reconstruction:/g, "")
                .split("/").join(" ");
        } else {
            linkName = e.split("/")
                .pop().split("#").reverse().join(" ").replace(/_/g, " ");
        }
        toreturn.push("<a href=\"" + e + "\" target=\"_blank\">" + linkName + "</a>");
    });
    return toreturn.join(", ");
}

class GraphNode {
    constructor(i) {
        this.counter = i;
        this.iri = [];
        //this.all contains this.iri (i.e. equivalent nodes) 
        //and also identical nodes in the tree
        //e.g. ee_1_door and ee_door
        this.all = [];
        this.shape = "rect";
        this.style = "fill: #F0E68C; stroke: lightBlue";
        this.rx = this.ry = 7;
        this.der = undefined;
        this.isAncestor = false;
        this.linked = undefined;
        this.linkedToSource = [];
        this.linkedToTarget = [];
        this.linkedToSourceCopy = [];
    }
}

class Node { //eqIri is an array of iri-s of Node-s that are equivalent to the Node
    constructor(i) {
        this.iri = i;
        var tmp = this.iri
            .replace("http://etytree-virtuoso.wmflabs.org/dbnary/eng/", "")
            .split("/");

        if (tmp.length > 1) {
            this.iso = tmp[0];
            this.label = tmp[1];
        } else {
            this.iso = "eng";
            this.label = tmp[0];
        }
        //graphNode specifies the graphnoce corresponding to the node
        this.graphNode = [];
        this.eqIri = [];
        this.der = undefined;
        this.isAncestor = false;
        //ety is an integer
        //and represents the etymology number encoded in the iri; 
        //if ety === 0 the iri is __ee_word
        //if ety === 1 the iri is __ee_1_word
        //etc
        this.ety = 0;
        if (null !== this.label.match(/__ee_[0-9]+_/g)) {
            //ety is an integer specifying the etymology entry
            this.ety = this.label.match(/__ee_[0-9]+_/g)[0].match(/__ee_(.*?)_/)[1];
        }

        this.label = this.label
            .replace(/__ee_[0-9]+_/g, "")
            .replace("__ee_", "");

        this.label = this.label
            .replace("__", "'")
            .replace(/^_/g, "*")
            .replace(/_/g, " ")
            .replace("__", "'")
            .replace(/_/g, " ")
            .replace("%C2%B7", "·");
        this.lang = langMap.get(this.iso);
        this.shape = "rect";
        this.style = "fill: sandyBrown; stroke: lightBlue";
        this.rx = this.ry = 7;
    }
    /* commented for now
        disambiguate() {
            this.refersTo.forEach(function(iri) {
                d3.selectAll("rect")
                    .filter(function(f) { return (!f.classed("iso") && f.iri === iri); })
                    .attr("fill", "red");
            });
            d3.select("#tooltipPopup").html("<b>" +
                this.label +
                "</b><br><br><i>" +
                "If you choose this word you will visualize the etymological tree of" +
                "either of the words highlighted in red, " +
                "because word sense was not specified in the corresponding Wiktionary Etymology Section." +
                "</i>");
        }*/
    showTooltip(x, y) {
	var text = "<b>" + this.label + "</b><br><br><br>";

        var url = ENDPOINT + "?query=" + encodeURIComponent(SPARQL.lemmaQuery(this.iri));

        if (debug) {
            console.log(url);
        }
        const source = getXMLHttpRequest(url);
        source.subscribe(
			 function(response){
			     if (null !== response) {
				 //print definition                                                      
				 var dataJson = JSON.parse(response).results.bindings;
				 dataJson.forEach(function(element) {
					 text += logDefinition(element.pos, element.gloss);
				     });
				 //print links 
				 text += "<br><br>as extracted from: " + logLinks(dataJson[0].links.value);
			     } else {
				 text += "-";
			     }
			 },
			 function(error) {
			     console.error(error);
			     text += "-";
			     d3.select("#tooltipPopup")
				 .html(text)
				 .style("display", "inline")
				 .style("left", (x + 38) + "px")
				 .style("top", (y - 28) + "px");
			 },
			 function(){
			     d3.select("#tooltipPopup")
                                 .html(text)
				 .style("display", "inline")
                                 .style("left", (x + 38) + "px")
                                 .style("top", (y - 28) + "px");

			 });
    }
}

//CONFIGURE - print debugging messages when debug == true    
debug = false;

//LOAD LANGUAGES                            
//used to print on screen the language name when the user clicks on a node (e.g.: eng -> "English") 
if (debug) console.log("loading languages");
langMap = new Map();
ssv = d3.dsv(";", "text/plain");
ssv("./resources/data/etymology-only_languages.csv", function(data) {
    data.forEach(function(entry) {
        langMap.set(entry.code, entry["canonical name"]);
    });
});
ssv("./resources/data/list_of_languages.csv", function(data) {
    data.forEach(function(entry) {
        langMap.set(entry.code, entry["canonical name"]);
    });
});
d3.text("./resources/data/iso-639-3.tab", function(error, textString) {
    var headers = ["Id", "Part2B", "Part2T", "Part1", "Scope", "Language_Type", "Ref_Name", "Comment"].join("\t");
    var data = d3.tsv.parse(headers + textString);
    data.forEach(function(entry) {
        langMap.set(entry.Id, entry.Ref_Name);
    });
});