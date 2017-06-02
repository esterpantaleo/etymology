//SET PARAMETERS FOR FUNCTION d3.xhr() 
var ENDPOINT = "https://etytree-virtuoso.wmflabs.org/sparql";
var MIME = "application/sparql-results+json";

function transform(d) {
    return "translate(" + d.x + "," + d.y + ")";
}

function jsonToTooltip(json, printLinks){
    var toreturn = "";
    var last = json.results.bindings.length - 1;
    json.results.bindings
        .forEach(function(element, i ){
            toreturn += logDefinition(element.pos.value, element.gloss.value);
            if (printLinks && i == last){
                toreturn += "<br><br>as extracted from: " + logLinks(element.links.value);
            }
        });
     return toreturn;
}

function logDefinition(pos, gloss){
    return gloss.split(";;;;").map(function(el) {
        return pos + " - " + el + "<br><br>";
    }).join("");
}

function logLinks(links){
    var toreturn = [];
    links.split(",").forEach(function(e){
        toreturn.push("<a href=\"" + e + "\" target=\"_blank\">" + e.split("/")
                      .pop().split("#").reverse().join(" ").replace(/_/g," ") + "</a>");
    })
    return toreturn.join(", ");
}

class Node {
    constructor(i){
	this.iri = i;
	var tmp = this.iri
	    .replace("http://kaiko.getalp.org/dbnary/eng/", "")
	    .split("/");
	
	if (tmp.length > 1){
	    this.iso = tmp[0];
	    this.word = tmp[1];
	} else {
	    this.iso = "eng";
	    this.word = tmp[0];
	}
	this.word = this.word
	    .replace(/__ee_[0-9]+_/g,"")
	    .replace("__ee_","");
	this.word = this.word
	    .replace("__","'")
	    .replace(/^_/g,"*")
	    .replace(/_/g," ")
	    .replace("__","'")
	    .replace(/_/g," ");
	this.lang = langMap.get(this.iso);
    }

    disambiguate(){
	this.refersTo.forEach(function(iri){
            d3.selectAll("rect")
		.filter(function(f) { return (! f.classed("iso") && f.iri == iri); })
		.attr("fill", "red")
	});
	d3.select("#myPopup").html("<b>" +
				   this.word +
				   "</b><br><br><i>" +
				   "If you choose this word you will visualize the etymological tree of" +
				   "either of the words highlighted in red, " +
				   "because word sense was not specified in the corresponding Wiktionary Etymology Section." +
				   "</i>");
    }

    showTooltip(printLinks){
        this.showTooltipPart(this.iri, this.word.split(",")[0], printLinks);
        if (this.eqIri != undefined){
            for (var i=0; i<this.eqIri.length; i++){
                this.showTooltipPart(this.eqIri[i], this.eqWord[i], printLinks);
            }
        }
    }

    showTooltipPart(iri, word, printLinks){
        var url = ENDPOINT + "?query=" + encodeURIComponent(this.sparql(iri, printLinks));

        if (debug){
            console.log(url);
        }
        d3.xhr(url, MIME, function(requestData) {
            var text = "";
            if (requestData != null) {
                text += jsonToTooltip(JSON.parse(requestData.responseText), printLinks);
            }
            if (text == ""){
                text = "-";
            }
            text = "<b>" + word + "</b><br><br><br>" + text;
	    d3.select("#myPopup").append("p").html(text) 
		.style("left", (d3.event.pageX + 18) + "px")  
		.style("top", (d3.event.pageY - 28) + "px");
        });
    }

    //DEFINE QUERY TO GET LINKS, POS AND GLOSS           
    sparql(iri, printLinks){
	var select = "", option = "";
	if (printLinks){
	    select = "(group_concat(distinct ?also ; separator=\",\") as ?links)";
	    option = "OPTIONAL {<" + iri.replace(/__ee_[0-9]+_/g,"__ee_") + "> rdfs:seeAlso ?also .}";
	}
	var query = [
	    "PREFIX dbnary: <http://kaiko.getalp.org/dbnary#>",
	    "PREFIX dbetym: <http://kaiko.getalp.org/dbnaryetymology#>",
	    "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>",
	    "PREFIX lemon: <http://lemon-model.net/lemon#>",
	    "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>",
	    "SELECT DISTINCT ?ee ?pos (group_concat(distinct ?def ; separator=\";;;;\") as ?gloss)" + select,
	    "WHERE {",
	    "    <" + iri + "> dbnary:refersTo ?ee ." + option,
	    "    OPTIONAL {",
	    "        ?ee rdf:type lemon:LexicalEntry .",
	    "        ?ee dbnary:partOfSpeech ?pos .",
	    "    }",
	    "    OPTIONAL {",
	    "        ?ee dbnary:refersTo ?nee .",
	    "        ?nee rdf:type lemon:LexicalEntry .",
	    "        ?nee dbnary:partOfSpeech ?pos .",
	    "    }",
	    "    OPTIONAL {",
	    "        ?ee dbnary:refersTo ?cee .",
	    "        ?cee dbnary:refersTo ?nee .",
	    "        ?nee rdf:type lemon:LexicalEntry .",
	    "        ?nee dbnary:partOfSpeech ?pos .",
	    "    }",
	    "    OPTIONAL {",
	    "        ?ee lemon:sense ?sense .",
	    "        ?sense lemon:definition ?val .",
	    "        ?val lemon:value ?def .",
	    "    }",
	    "    OPTIONAL {",
	    "        ?ee dbnary:refersTo ?nee .",
	    "        ?nee rdf:type lemon:LexicalEntry .",
	    "        ?nee lemon:sense ?sense .",
	    "        ?sense lemon:definition ?val .",
	    "        ?val lemon:value ?def .",
	    "    }",
	    "    OPTIONAL {",
	    "        ?ee dbnary:refersTo ?cee .",
	    "        ?cee dbnary:refersTo ?nee .",
	    "        ?nee rdf:type lemon:LexicalEntry .",
	    "        ?nee lemon:sense ?sense .",
	    "        ?sense lemon:definition ?val .",
	    "        ?val lemon:value ?def .",
	    "    }",
	    "}"
	];
	return query.join(" ");
    }
}

//CONFIGURE - print debugging messages when debug == true         
var debug = true;

//CONFIGURE - if excludeStarLikeStructures == true don't visualize node B if node B is the target of a node in the same language and if node B itself is not the source of a link  
//when set to true this removes many links that show up as stars in the graph (nodes with many links departing from it)       
var excludeStarLikeStructures = true;

//CONFIGURE - if mergeEquivalentnodes == true, if node A is etymologically equivalent to node B merge A and B into one node and merge their links too   
//otherwise equivalent nodes are linked by links with no arrow              
var mergeEquivalentNodes = true;

//LOAD LANGUAGES                            
//used to print on screen the language name when the user clicks on a node (e.g.: eng -> "English") 
if (debug) console.log("loading languages");
var langMap = new Map();
var ssv = d3.dsv(";", "text/plain");
ssv("../data/etymology-only_languages.csv", function(data) {
    data.forEach(function(entry){
        langMap.set(entry["code"], entry["canonical name"]);
    })
});
ssv("../data/list_of_languages.csv", function(data) {
    data.forEach(function(entry){
        langMap.set(entry["code"], entry["canonical name"]);
    })
});
d3.text("../data/iso-639-3.tab", function(error, textString){
    var headers = ["Id", "Part2B", "Part2T", "Part1", "Scope", "Language_Type", "Ref_Name", "Comment"].join("\t");
    var data = d3.tsv.parse(headers + textString);
    data.forEach(function(entry){
        langMap.set(entry["Id"], entry["Ref_Name"]);
    });
});
