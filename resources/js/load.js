/*globals 
    d3, Rx, SPARQL, console, XMLHttpRequest
*/

var debug = false;

//LOAD LANGUAGES
//used to print on screen the language name when the user clicks on a node (e.g.: eng -> "English")      
if (debug) {
    console.log("loading languages");
}

var langMap = new Map();
var ssv = d3.dsv(";", "text/plain");
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


//HELPER FUNCTIONS
function onlyUnique(value, index, self){
    return self.indexOf(value) === index;
}

function transform(d) {
    return "translate(" + d.x + "," + d.y + ")";
}

class GraphNode {
    constructor(i) {
        this.counter = i;
        this.iri = [];
        //this.all contains this.iri (i.e. equivalent nodes) 
        //and also identical nodes in the tree
        //e.g. ee_1_door and ee_door
        this.all = [];
        this.linked = undefined;
        this.linkedToSource = [];
	this.linkedToSourceCopy = [];
        this.linkedToTarget = [];

	this.der = undefined;
        this.isAncestor = false;

	this.shape = "rect";
        this.style = "fill: #F0E68C; stroke: lightBlue";
        this.rx = this.ry = 7;
    }
}

class Node { 
    constructor(i) {
        this.iri = i;
	var tmp = this.parseIri(i);
	this.iso = tmp.iso;
	this.label = tmp.label;
	//ety is an integer                              
        //and represents the etymology number encoded in the iri;
	this.ety = tmp.ety;
        this.lang = langMap.get(this.iso);
	//graphNode specifies the graphNode(s) corresponding to the node                           
        this.graphNode = [];
	//eqIri is an array of iri-s of Node-s that are equivalent to the Node             
        this.eqIri = [];

        this.der = undefined;
        this.isAncestor = false;

	this.shape = "rect";
	this.style = "fill: #F0E68C; stroke: lightBlue";
        this.rx = this.ry = 7;
    }

    logTooltip() {
	var query = SPARQL.lemmaQuery(this.iri);
        var url = SPARQL.ENDPOINT + "?query=" + encodeURIComponent(query);

        if (debug) {
            console.log(url);
        }

	var that = this;

        const source = SPARQL.getXMLHttpRequest(url);
        source.subscribe(
			 function(response){
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

    parseIri(iri){
	var iso, label, ety, 
	tmp = iri.replace("http://etytree-virtuoso.wmflabs.org/dbnary/eng/", "")
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
	
	label = label.replace(/__ee_[0-9]+_/g, "")
            .replace("__ee_", "")
            .replace("__", "'")
            .replace(/^_/g, "*")
            .replace(/_/g, " ")
            .replace("__", "'")
            .replace(/_/g, " ")
            .replace("%C2%B7", "·");
	
	var obj = {
            iso: iso,
            label: label,
            ety: ety
	};
	
	return obj;
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

