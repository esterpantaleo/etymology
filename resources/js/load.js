/*globals 
	d3, Rx, sparql, console, XMLHttpRequest
*/
//SET PARAMETERS FOR FUNCTION d3.xhr() 
var ENDPOINT = "https://etytree-virtuoso.wmflabs.org/sparql";
var MIME = "application/sparql-results+json";

//ADD CONFIGURATION VARIABLE DEFINITIONS
var debug,
	excludeStarLikeStructures,
	mergeEquivalentNodes,
	langMap,
	ssv;

function get(url){ 
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

function sort_unique(arr) {
    if (arr.length === 0) return arr;
    arr = arr.sort();
    var ret = [arr[0]];
    for (var i = 1; i < arr.length; i++) { // start loop at 1 as element 0 can never be a duplicate  
        if (arr[i-1] !== arr[i]) {
            ret.push(arr[i]);
        }
    }
    return ret;
}

function transform(d) {
    return "translate(" + d.x + "," + d.y + ")";
}

function logDefinition(pos, gloss){
    if (undefined !== pos && undefined !== gloss){ 
	return gloss.value.split(";;;;").map(function(el) {
            return pos.value + " - " + el + "<br><br>";
	}).join("");
    } else {
	return "-";
    }
}

function logLinks(links){
    var toreturn = [];
    links.split(",").forEach(function(e){
        toreturn.push("<a href=\"" + e + "\" target=\"_blank\">" + e.split("/")
                      .pop().split("#").reverse().join(" ").replace(/_/g, " ") + "</a>");
    });
    return toreturn.join(", ");
}

class GraphNode {
    constructor(i){
	this.counter = i;
	this.iri = [];
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

class Node {//eqIri is an array of iri-s of Node-s that are equivalent to the Node
    constructor(i){
	this.iri = i;
	var tmp = this.iri
	    .replace("http://etytree-virtuoso.wmflabs.org/dbnary/eng/", "")
	    .split("/");
	
	if (tmp.length > 1){
	    this.iso = tmp[0];
	    this.label = tmp[1];
	} else {
	    this.iso = "eng";
	    this.label = tmp[0];
	}
	this.graphNode = [];
	this.eqIri = [];
	this.der = undefined;
	this.isAncestor = false;
	this.ety = 0;
	if (null !== this.label.match(/__ee_[0-9]+_/g)){
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

    disambiguate(){
	this.refersTo.forEach(function(iri){
            d3.selectAll("rect")
		.filter(function(f) { return (! f.classed("iso") && f.iri === iri); })
		.attr("fill", "red");
	});
	d3.select("#myPopup").html("<b>" +
				   this.label +
				   "</b><br><br><i>" +
				   "If you choose this word you will visualize the etymological tree of" +
				   "either of the words highlighted in red, " +
				   "because word sense was not specified in the corresponding Wiktionary Etymology Section." +
				   "</i>");
    }

    showTooltip(x,y){
        var url = ENDPOINT + "?query=" + encodeURIComponent(sparql(this.iri));

        if (debug){
            console.log(url);
        }
	const source = get(url);
	source.subscribe(response => this.popTooltip(response, x, y),
			 error => console.error(error),
			 () => console.log('done DAGRE'));
    }

    printTooltip(resp){
	var text = "<b>" + this.label + "</b><br><br><br>";
	if (null !== resp) {
	    var dataJson = JSON.parse(resp).results.bindings;
	    dataJson.forEach(function(element){
		text += logDefinition(element.pos, element.gloss);
	    });
	    text += "der=" + this.dero + "<br><br>as extracted from: " + logLinks(dataJson[0].links.value); 
	} else {
	    text += "-";
	}
	return text;
    }
    
    popTooltip(resp, x, y){
	var text = this.printTooltip(resp);
	d3.select("#myPopup")
	    .style("left", (x + 38) + "px")
	    .style("top", (y - 28) + "px")
	    .append("p").html(text);
    }
}

//CONFIGURE - print debugging messages when debug == true    
debug = true;

//CONFIGURE - if excludeStarLikeStructures == true don't visualize node B if node B is the target of a node in the same language and if node B itself is not the source of a link  
//when set to true this removes many links that show up as stars in the graph (nodes with many links departing from it)       
excludeStarLikeStructures = false;

//CONFIGURE - if mergeEquivalentnodes == true, if node A is etymologically equivalent to node B merge A and B into one node and merge their links too   
//otherwise equivalent nodes are linked by links with no arrow   
mergeEquivalentNodes = true;

//LOAD LANGUAGES                            
//used to print on screen the language name when the user clicks on a node (e.g.: eng -> "English") 
if (debug) console.log("loading languages");
langMap = new Map();
ssv = d3.dsv(";", "text/plain");
ssv("./resources/data/etymology-only_languages.csv", function(data) {
    data.forEach(function(entry){
        langMap.set(entry.code, entry["canonical name"]);
    });
});
ssv("./resources/data/list_of_languages.csv", function(data) {
    data.forEach(function(entry){
        langMap.set(entry.code, entry["canonical name"]);
    });
});
d3.text("./resources/data/iso-639-3.tab", function(error, textString){
    var headers = ["Id", "Part2B", "Part2T", "Part1", "Scope", "Language_Type", "Ref_Name", "Comment"].join("\t");
    var data = d3.tsv.parse(headers + textString);
    data.forEach(function(entry){
        langMap.set(entry.Id, entry.Ref_Name);
    });
});
