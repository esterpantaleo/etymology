//SET PARAMETERS FOR FUNCTION d3.xhr() 
var ENDPOINT = "https://etytree-virtuoso.wmflabs.org/sparql";
var MIME = "application/sparql-results+json";

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
	}
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
                      .pop().split("#").reverse().join(" ").replace(/_/g, " ") + "</a>");
    })
    return toreturn.join(", ");
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
	this.ety = 0;
	if (this.label.match(/__ee_[0-9]+_/g) != null){
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
	    .replace(/_/g, " ");
	this.lang = langMap.get(this.iso);
	this.shape = "rect";
	this.style = "fill: #ffb380; stroke: lightBlue";
	this.rx = this.ry = 7;
	
    }

    disambiguate(){
	this.refersTo.forEach(function(iri){
            d3.selectAll("rect")
		.filter(function(f) { return (! f.classed("iso") && f.iri == iri); })
		.attr("fill", "red")
	});
	d3.select("#myPopup").html("<b>" +
				   this.label +
				   "</b><br><br><i>" +
				   "If you choose this word you will visualize the etymological tree of" +
				   "either of the words highlighted in red, " +
				   "because word sense was not specified in the corresponding Wiktionary Etymology Section." +
				   "</i>");
    }

    showTooltip(printLinks){
        this.showTooltipPart(this.iri, this.label.split(",")[0], printLinks);
        if (this.eqIri != undefined){
            for (var i=0; i<this.eqIri.length; i++){
                this.showTooltipPart(this.eqIri[i], this.eqLabel[i], printLinks);
            }
        }
    }

    showTooltipPart(iri, label, printLinks){
        var url = ENDPOINT + "?query=" + encodeURIComponent(sparql(iri, printLinks));

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
            text = "<b>" + label + "</b><br><br><br>" + text;
	    d3.select("#myPopup").append("p").html(text) 
		.style("left", (d3.event.pageX + 18) + "px")  
		.style("top", (d3.event.pageY - 28) + "px");
        });
    }
}

//CONFIGURE - print debugging messages when debug == true    
var debug = true;

//CONFIGURE - if excludeStarLikeStructures == true don't visualize node B if node B is the target of a node in the same language and if node B itself is not the source of a link  
//when set to true this removes many links that show up as stars in the graph (nodes with many links departing from it)       
var excludeStarLikeStructures = false;

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
