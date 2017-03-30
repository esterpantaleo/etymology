//SET PARAMETERS FOR FUNCTION d3.xhr() 
var ENDPOINT = "https://etytree-virtuoso.wmflabs.org/sparql";
var MIME = "application/sparql-results+json";

function transform(d) {
    return "translate(" + d.x + "," + d.y + ")";
}

function writeDefinition(pos, gloss){
    return gloss.split(";;;;").map(function(el) {
        return pos + " - " + el + "<br><br>";
    }).join("");
}

function writeLinks(links){
    var toreturn = [];
    links.split(",").forEach(function(e){
        toreturn.push("<a href=\"" + e + "\" target=\"_blank\">" + e.split("/")
		      .pop().split("#").reverse().join(" ").replace(/_/g," ") + "</a>");
    })
    return toreturn.join(", ");
}

function jsonToTooltip(json, printLinks){
    var toreturn = "";
    var last = json.results.bindings.length - 1;
    json.results.bindings
        .forEach(function(element, i ){
            toreturn += writeDefinition(element.pos.value, element.gloss.value);
            if (printLinks && i == last){
                toreturn += "<br><br>as extracted from: " + writeLinks(element.links.value);
            }
        });
    return toreturn;
}

class Node {
    constructor(i){
	this.iri = i;
	var tmp = this.iri.replace("http://kaiko.getalp.org/dbnary/eng/", "").split("/");
	
	if (tmp.length > 1){
	    this.iso = tmp[0];
	    this.word = tmp[1];
	} else {
	    this.iso = "eng";
	    this.word = tmp[0];
	}
	this.word = this.word.replace(/__ee_[0-9]+_/g,"").replace("__ee_","");
	this.word = this.word.replace("__","'").replace(/^_/g,"*").replace(/_/g," ").replace("__","'").replace(/_/g," ");
	this.lang = langMap.get(this.iso)
    }
/*
    get links() {
        return this.dfghjk;
    } */
    
    d3DisambiguationText(){
	this.refersTo.forEach(function(iri){
            d3.selectAll("circle")
		.filter(function(f) { return (f.iri == iri); })
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

    d3XhrText(printLinks){
	this.d3XhrTextPart(this.iri, this.word.split(",")[0], printLinks);
	if (this.eqIri != undefined){
	    for (var i=0; i<this.eqIri.length; i++){
		this.d3XhrTextPart(this.eqIri[i], this.eqWord[i], printLinks);
	    }
	}
    }

    d3XhrTextPart(iri, word, printLinks){
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

//DEFINE QUERY TO PLOT CLOUD OF WORDS                                                     
var cloudSparql = function(search){
    //var encodedSearch = encodeURIComponent(search);                
    var encodedSearch = search;
    var query = [
        "PREFIX dbetym: <http://kaiko.getalp.org/dbnaryetymology#>",
        "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>",
        "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>",
        "SELECT DISTINCT ?iri (group_concat(distinct ?ee ; separator=\",\") as ?et) ",
        "WHERE {",
        "    ?iri rdfs:label ?label . ?label bif:contains \"\'" + encodedSearch + "\'\" .",
        //exclude entries that contain the searched word but include other words (e.g.: search="door" label="doorbell", exclude "doorbell")  
        "    FILTER REGEX(?label, \"^" + encodedSearch + "$\", 'i') .",
	"    ?iri rdf:type dbetym:EtymologyEntry .", 
        "    OPTIONAL {",
	"        ?iri dbnary:refersTo ?ee .",
	"        ?ee rdf:type dbetym:EtymologyEntry .",
        "    }",
  	"}"      
    ];
    return query.join(" ");
}

//DEFINE QUERY TO PLOT GRAPH
var treeSparql = function(id){
    var treeQuery = [
        "DEFINE input:inference \"etymology_ontology\"",
        "PREFIX dbetym: <http://kaiko.getalp.org/dbnaryetymology#>",
        "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>",
        "SELECT DISTINCT ?target1 ?target2 ?target3 ?target4 ?source (group_concat(distinct ?p ; separator=\",\") as ?ety) {",
        "    ?source ?p ?o .",
        "    FILTER (?p in (dbetym:etymologicallyDerivesFrom,dbetym:descendsFrom,dbetym:derivesFrom,dbetym:etymologicallyEquivalentTo))",
        "    {",
        "        SELECT ?source",
        "            {",
        "                ?source dbetym:etymologicallyRelatedTo{1,} <" + id + "> .",
        "            } LIMIT 100",
        "    }",
        "    UNION",
        "    {",
        "        SELECT ?source",
        "            {<" + id + "> dbetym:etymologicallyRelatedTo{1,} ?source .",
        "            } LIMIT 100",
        "    }",
        "    UNION",
        "    {",
        "        SELECT ?source",
        "            {<" + id + "> dbetym:etymologicallyRelatedTo{1,} ?ancestor .",
        "                ?source dbetym:etymologicallyRelatedTo{1,} ?ancestor .",
        "            } LIMIT 100",
        "    }",
        "    OPTIONAL",
        "    {",
        "        ?source dbetym:etymologicallyDerivesFrom ?target1",
        "    }",
        "    OPTIONAL",
        "    {",
        "        ?source dbetym:derivesFrom ?target2",
        "    }",
        "    OPTIONAL",
        "    {",
        "        ?source dbetym:descendsFrom ?target3",
            "    }",
        "    OPTIONAL",
        "    {",
        "        ?source dbetym:etymologicallyEquivalentTo ?target4",
        "    }",
        "}"
    ];
    return treeQuery.join(" ");
}
    
//TO DO: could ask server if the word has an etymological relationship and if the answer is no ignore that node    
function loadNodes(myWord, langMap){
    var url = ENDPOINT + "?query=" + encodeURIComponent(cloudSparql(myWord));
   
    if (debug) {
	console.log(url);
    }

    var nodes = {};

    d3.xhr(url, MIME, function(request) {
        if (request != null) {
            //clean screen and change help
            d3.select("#tree-overlay").remove();
            d3.select("#myPopup").style("display", "none");
            d3.select("#message").remove();
            d3.select("#p-helpPopup").remove();
            d3.select("#helpPopup")
                .append("p")
                .attr("id", "p-helpPopup")
                .attr("style", "font-size:12px;border-radius:8px;max-width:255px")
                .html("<b>Disambiguation page</b><br>Pick the word you are interested in." + 
                      "<ul>" + 
                      "<li>Click on a circle to display the language</li>" + 
                      "<li>Click on a word to display lexical information</li>" + 
                      "<li>Double click on a circle to choose a word</li>" + 
                      "</ul>");
	    
	    //DEFINE SIZE                     
            var width = window.innerWidth,
            height = $(document).height();
            $("#tree-container").css({
                "overflow":"scroll !important"
            })

            //perform query
            var json = JSON.parse(request.responseText);
	    
            var theGraph = json.results.bindings;
	    if (theGraph.length == 0){
		d3.select("#tree-container")
		    .append("p")
		    .attr("id", "message")
		    .attr("align", "center")
		    .html("This word is not available in the database")
		    .append("p")
	    }

            if (debug) { 
		console.log(theGraph); 
	    }

            var sparqlLinks = {};
            var sparqlNodes = {};

            theGraph.forEach(function(n){
		var iris = n.et.value.split(",");
		if (iris == ""){
		    sparqlNodes[n.iri.value] = new Node(n.iri.value);
		} else {
		    if (iris.length > 1){
			sparqlNodes[n.iri.value] = new Node(n.iri.value);
			sparqlNodes[n.iri.value].refersTo = iris;
		    }
		    iris.forEach(function(element) {
                        sparqlNodes[element] = new Node(element);
                    });
		}
            })
	    if (debug) {
		console.log(sparqlNodes);
	    }

            var force = d3.layout.force()
                .nodes(d3.values(sparqlNodes))
                .links(sparqlLinks)
                .size([width, height])
                .linkDistance(150)
                .charge(-700)
                .gravity(.2)
                .on("tick", tick)
                .start();
	    
            var svgGraph = d3.select("#tree-container").append("svg")
                .attr("id", "tree-overlay")
                .attr("width", width)
                .attr("height", height)
                .on("click", function(){
                    d3.select("#myPopup")
			.style("display", "none");
		    d3.event.stopPropagation();
                });
	    
            var circle = svgGraph.append("g").selectAll("circle")
                .data(force.nodes())
                .enter().append("circle")
                .attr("r", 12)
                .attr("fill", "#ffb380")
                .attr("stroke", "lightBlue")
                .call(force.drag)
                .on("mouseover", function(d) {
                    d3.select(this).style("cursor", "pointer");
                })
                .on("click", function(d) {
                    d3.selectAll("circle").attr("fill", "#ffb380");
                    d3.select(this)
                        .append("a")
                        .attr("href", "#myPopup")
                        .attr("data-rel", "popup")
                        .attr("data-transition", "pop");
                    d3.select("#myPopup")
			.style("width", "auto")
			.style("height", "auto")
			.style("display", "inline");
                    d3.select("#myPopup")
			.html(d.lang)
                        .style("left", (d3.event.pageX) + "px")
                        .style("top", (d3.event.pageY - 28) + "px");
                    d3.event.stopPropagation();
                })
                .on("dblclick", loadTree);
	    
            var isoText = svgGraph.append("g").selectAll("text")
                .data(force.nodes())
                .enter().append("text")
                .attr("x", 0)
                .attr("y", ".31em")
                .attr("fill", "black")
                .attr("text-anchor", "middle")
		.style("font-size", "0.5em")
                .text(function(d) { return d.iso; })
	    
            var rectangle = svgGraph.append("g").selectAll("rectangle")
                .data(force.nodes())
                .enter().append("rect")
                .attr("x", 20)
                .attr("y", "-.31em")
                .attr("width", "2em")
                .attr("height", "0.7em")
                .attr("fill", "red")
                .attr("fill-opacity", 0)
                .on("mouseover", function(d) {
                    d3.select(this).style("cursor", "pointer");
                })
                .on("click", function(d){
		    d3.select(this)
			.append("a")
			.attr("href", "#myPopup")
			.attr("data-rel", "popup")
			.attr("data-transition", "pop");
		    d3.select("#myPopup")
			.style("width", "auto")
			.style("height", "auto")
			.style("display", "inline");
		    d3.select("#myPopup").html("");
		    d3.selectAll("circle").attr("fill", "#ffb380");
		    if (d.refersTo != undefined){
			d.d3DisambiguationText();
		    } else {
			console.log(d)
			d.d3XhrText(false);
		    }
		    d3.event.stopPropagation();
		});

            var wordText = svgGraph.append("g").selectAll("text")
                .data(force.nodes())
                .enter().append("text")
                .attr("x", 20)
                .attr("y", ".31em")
                .attr("id", "word")
                .text(function(d) { return d.word; });
	    
            function tick() {
                circle.attr("transform", transform);
                wordText.attr("transform", transform);
                rectangle.attr("transform", transform);
                isoText.attr("transform", transform);
            }
        }
    })
}

function loadTree(d) {
    var treeUrl = ENDPOINT + "?query=" + encodeURIComponent(treeSparql(d.iri));
    
    if (debug) { 
	console.log(treeUrl);
    }
    
    //TODO: use wheel
    //DEFINE SIZE            
    var width = window.innerWidth,
    height = $('#tree-container').height();

    d3.select("#tree-container")
	.insert("p", ":first-child")
	.attr("id", "message")
	.attr("align", "center")
	.html("Loading, please wait...");
    
    //TODO: MANAGE ERROR and RELOAD FROM CALLBACK
    d3.xhr(treeUrl, MIME, function(request) {
	//clean screen
	d3.select("#tree-overlay").remove();
	d3.select("#myPopup")
	    .style("display", "none");
	d3.select("#message").remove();
	if (request == null){
	    //print error message
            d3.select("#tree-container")
                .append("p")
                .attr("id", "message")
		.attr("align", "center")
                .html("Sorry, the server cannot extract etymological relationships correctly for this word. <br>We are working to fix this!")
                .append("p")
		.attr("id", "messageReload")
                .attr("align", "center")
	} else {
	    //change help       
	    d3.select("#p-helpPopup").remove(); 
	    d3.select("#helpPopup")
		.append("p")
		.attr("id", "p-helpPopup")
		.attr("style", "font-size:12px;border-radius:8px;max-width:255px")
		.html("Arrows go from ancestor to descendant.<ul>" +
                      "<li>Click on a circle to display the language</li>" + 
                      "<li>Click on a word to display lexical information.</li>" +
                      "</ul>");
	    
	    var treeJson = JSON.parse(request.responseText);

	    var treeGraph = treeJson.results.bindings;
	    
	    if (debug) { 
		console.log(treeGraph); 
	    }

	    var treeSparqlLinks = [];
	    var treeSparqlNodes = {};
	    
	    treeGraph.forEach(function(element, j){
		
		treeSparqlNodes[element.source.value] = new Node(element.source.value);
	
		["target1", "target2", "target3", "target4"].map(function(target){
		    if (element[target] != undefined) {		
			if (treeSparqlNodes[element[target].value] == undefined) {
			    treeSparqlNodes[element[target].value] = new Node(element[target].value);
			}
		    }			    
		});
	    });
	    
	    //set links
	    treeGraph.forEach(function(element){
		["target1", "target2", "target3", "target4"].forEach(function(target){
		    var type = "inherited";
                    if (target == "target4"){
                        type = "equivalent";
                    }

		    if (element[target] != undefined) {
			if (element[target].value != element.source.value){
                            var Link = {"source": treeSparqlNodes[element[target].value], "target": treeSparqlNodes[element.source.value], "type": type};
			    if (treeSparqlLinks.indexOf(Link) == -1) {
                                treeSparqlLinks.push(Link);
                            }
			}
		    }
		})
	    })

            //merge nodes that are linked by a Link of type equivalent             
            if (mergeEquivalentNodes) {
		treeSparqlLinks.forEach(function(element){
                    if (element.type == "equivalent"){
			if (element.target.eqIri == undefined){
			    if (element.source.eqIri == undefined){
				element.target.eqIri = [];
				element.target.eqWord = [];
			    } else {
				element.target.eqIri = element.source.eqIri;
				element.target.eqWord = element.source.eqWord;
				element.source.eqIri = undefined;
				element.source.eqWord = undefined;
			    }
			}
			if (!element.target.eqIri.includes(element.source.iri)) {//if it is not in array
			    element.target.eqIri.push(element.source.iri);
			    element.target.eqWord.push(element.source.word);
			    
			    if (element.source.eqIri != undefined){
				element.source.eqIri.forEach(function(q){
				    if (!element.target.eqIri.includes(q)) {
					element.target.eqIri.push(q);
				    }
				});
				element.source.eqWord.forEach(function(q){
				    if (!element.target.eqWord.includes(q)) {
					element.target.eqWord.push(q);
				    }
				});
			    }
			} 
			//merge node element.source into node element.target, and delete node element.source
			treeSparqlLinks.forEach(function(f){
                            if (f != element){
				if (f.source.iri == element.source.iri) {
				    f.source = element.target;
				} else if (f.target.iri == element.source.iri) {
				    f.target = element.target;
				}
                            }
			})
		    }
                });

                for (var i = treeSparqlLinks.length-1; i >= 0; i--){
		    if (treeSparqlLinks[i].source.iri == treeSparqlLinks[i].target.iri){
			treeSparqlLinks.splice(i, 1);
		    } else if (treeSparqlLinks[i].type == "equivalent"){
			if (treeSparqlLinks[i].source.eqIri == undefined){
			    delete treeSparqlNodes[treeSparqlLinks[i].source.iri];
			}
			if (treeSparqlLinks[i].target.eqIri == undefined){
			    delete treeSparqlNodes[treeSparqlLinks[i].target.iri];
                        }
                        treeSparqlLinks.splice(i, 1);
                    }
                }
		for (var n in treeSparqlNodes){		    
		    if (treeSparqlNodes[n].eqWord != undefined) {
			treeSparqlNodes[n].word += "," + treeSparqlNodes[n].eqWord.join(",");
		    }
		}
	    }
console.log(treeSparqlNodes)
	    if (excludeStarLikeStructures){
		//find links between words in the same language, but exclude links that have as target the searched word
                var toDeleteLinks = treeSparqlLinks.filter(function(element) {
		    //if (element.target.word.find(function(w){ return w == myWord; }) == undefined){
			return element.source.iso == element.target.iso;
		    //} else {
	//		return false;
	//	    }
                }).filter(function(element) {//don't delete a node if a link starts from it
                    for (var i=0; i<treeSparqlLinks.length; i++) {
                        if (element.target.iri == treeSparqlLinks[i].source.iri)
                            return false;
                    }
                    return true;
                });
		
		//remove links  
                treeSparqlLinks = treeSparqlLinks.filter(function(d) {
                    for (var i=0; i<toDeleteLinks.length; i++) {
                        if (toDeleteLinks[i] == d) return false;
                    }
                    return true;
                });
		
		//remove nodes that are not connected by a link
                for (var aNode in treeSparqlNodes) {
                    var isLinked = false;
                        for (var i=0; i<treeSparqlLinks.length; i++) {
                            if (treeSparqlLinks[i].source.iri == aNode || treeSparqlLinks[i].target.iri == aNode) {
                                isLinked = true;
                                break;
                            }
                        }
                    if (isLinked == false) {
                        delete treeSparqlNodes[aNode];
                    }
                }
            }
	    
	    if (treeSparqlLinks.length == 0){
                d3.select("#tree-overlay").remove();
                d3.select("#tree-container")
                    .append("p")
                    .attr("id", "message")
                    .attr("align", "center")
                    .html("Sorry, no etymology available for this word");
            }
	    
	    var force = d3.layout.force()
		.nodes(d3.values(treeSparqlNodes))
		.links(treeSparqlLinks)
		.size([width, height])
		.linkDistance(150)
		.charge(-700)
		.gravity(.2)
		.on("tick", tick)
		.start();

	    var svgGraph = d3.select("#tree-container").append("svg")
		.attr("id", "tree-overlay")
		.attr("width", width)
		.attr("height", height)
		.on("click", function(){
		    d3.select("#myPopup")
		        .style("display", "none");
		});
	    
	    // Per-type markers, as they don't inherit styles.     
	    svgGraph.append("defs").selectAll("marker")
		.data(["borrowed", "inherited"])
		.enter().append("marker")
		.attr("id", function(d) { return d; })
		.attr("viewBox", "0 -5 10 10")
		.attr("refX", 26)
		.attr("refY", -1.5)
		.attr("markerWidth", 6)
		.attr("markerHeight", 6)
		.attr("orient", "auto")
		.append("path")
		.attr("d", "M0,-5L10,0L0,5")
		.attr("fill", "lightBlue");
	    
	    var path = svgGraph.append("g").selectAll("path")
		.data(force.links())
		.enter().append("path")
		.attr("class", function(d) { return "link " + d.type; })
		.attr("marker-end", function(d) { return "url(#" + d.type + ")"; });
	    
	    var circle = svgGraph.append("g").selectAll("circle")
		.data(force.nodes())
		.enter().append("circle")
		.attr("r", 12)
		.attr("fill", function(d){ 
//		    if (d.iso == myIso && d.word == myWord) 
//			return "red"; 
//		    else 
			return "#ffb380"; 
		})
		.attr("stroke", "lightBlue")
		.call(force.drag)
		.on("mouseover", function(d) {
		    d3.select(this).style("cursor", "pointer");
		}) 
		.on("click", function(d) {
		    d3.select(this)
			.append("a") 
			.attr("href", "#myPopup") 
			.attr("data-rel", "popup")
			.attr("class", "ui-btn ui-corner-all ui-shadow ui-btn ui-icon-delete ui-btn-icon-notext ui-btn-right")  
			.attr("data-position-to", "origin"); 
		    d3.select("#myPopup")
			.style("width", "auto")
                        .style("height", "auto")
		        .style("display", "inline");
		    d3.select("#myPopup").html(langMap.get(d.iso))
			.style("left", (d3.event.pageX) + "px")
			.style("top", (d3.event.pageY - 28) + "px");
		    d3.event.stopPropagation();
		});
	    
	    var isoText = svgGraph.append("g").selectAll("text")
		.data(force.nodes())
		.enter().append("text")
		.attr("x", 0)
		.attr("y", ".31em")
		.attr("fill", "black")
		.attr("text-anchor", "middle")
		.style("font-size", "0.5em")
		.text(function(d) { return d.iso; });
	    
	    var rectangle = svgGraph.append("g").selectAll("rectangle")
		.data(force.nodes())
		.enter().append("rect")
		.attr("x", 20)
		.attr("y", "-.31em")
		.attr("width", "2em")
		.attr("height", "0.7em")
		.attr("fill", "red")
		.attr("fill-opacity", 0)
		.on("click", function(d) {
		    d3.select(this)
			.append("a")
			.attr("href", "#myPopup") 
			.attr("data-rel", "popup")
			.attr("data-transition", "pop");
		    d3.select("#myPopup").html("")
			.style("width", "auto")
                        .style("height", "auto")
		        .style("display", "inline");
		    console.log(d)
		    d.d3XhrText(true);
		    d3.event.stopPropagation();
		});
	    
	    var wordText = svgGraph.append("g").selectAll("text")
		.data(force.nodes())
		.enter().append("text")
		.attr("x", 20)
		.attr("y", ".31em")
		.attr("id", "word")
		.text(function(d) { return d.word; });
	    
	    function tick() {
		var radius = 13;
                circle.attr("cx", function(d) { return d.x = Math.max(radius, Math.min(width - radius, d.x)); })
                    .attr("cy", function(d) { return d.y = Math.max(radius, Math.min(height - radius, d.y)); });
		
		path.attr("d", function(d){
                    return "M" + d.source.x + "," + d.source.y + "A0,0 0 0,1 " + d.target.x + "," + d.target.y;
                });
		wordText.attr("transform", transform);
		rectangle.attr("transform", transform);
		isoText.attr("transform", transform);
	    }
	}	
    });
}

$('document').ready(function(){
    //DEFINE TOOLTIP DIVISION
    var div = d3.select("body").append("div")
	.attr("data-role", "popup")
	.attr("data-dismissible", "true")
	.attr("id", "myPopup")
	.attr("class", "ui-content")
	.style("position", "absolute")
	.style("background", "lightBlue")
	.style("text-align", "left")
	.style("padding", "2px")
	.style("font", "12px sans-serif")
	.style("border", "0px")
	.style("border-radius", "8px")
	.attr("width", 0)
        .attr("height", 0);
    
    $('#tags').on("keypress click", function(e){
	if (e.which == 13 || e.type === 'click') {
            var searchedWord = $(this).val();//.replace("/", "!slash!");
	    
            if (searchedWord){
		if (debug) console.log("loading nodes");
		loadNodes(searchedWord, langMap);
	    }
	};
    });
});

