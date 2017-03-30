function transform(d) {
    return "translate(" + d.x + "," + d.y + ")";
}

function reduceIRI(e){
    return e.replace("http://kaiko.getalp.org/dbnary/eng/", "").replace(/_[0-9]+_/g,"_");//.replace("_2_","_").replace("_3_","_");                                                                                                    
}

function fromIRItoWord(e){
    return e.replace(/__ee_[0-9]+_/g,"").replace("__ee_","").replace("__","'").replace(/^_/g,"*").replace(/_/g," ");
}

function showPosAndGlosses(e){
    var toreturn = "";
    for (var i = 0; i < e.pos.length; i ++)  toreturn += "<br><br>" + e.pos[i] + " - " + e.gloss[i];
    return toreturn;
}

//node constructor                                                                                                               
//this sa constructor for nodes in the cloud of words
function Node(element){ //iso, word, et, refersTo (an array with the specific etymology entries that the generic etymology entry refers to), pos (an array of pos-s), gloss, id                                                           
    //word is a string                                                                                                                 
    this.word = element.word.value;
    //iso is a string containing the language code                                                                                                                                                                                        
    this.iso = element.iso.value;
    //et is a uri                                                                                                                                                                                                                         
    this.et = (element.et != undefined) ? element.et.value : "";
    var splitted = this.et.split(",");
    if (splitted.length > 1){//uri is a generic etymology entry (e.g.: __ee_door)                                                                                                                                                         
        this.refersTo = splitted;
        this.et = element.uri.value;
    }
    this.pos = [];
    this.pos.push((element.pos != undefined) ? element.pos.value : "");
    this.gloss = [];
    this.gloss.push((element.gloss != undefined) ? element.gloss.value : "");
    //merge iri-s with the same etymological origin                                                                                                                                                                                       
    this.doMerge = function(nodes){
        var merge = false;
        for (var i in nodes){
            if (nodes[i].et != undefined){
                if (nodes[i].iso == this.iso){
                    if (nodes[i].word == this.word){
                        if (nodes[i].et == this.et) {
                            if (this.refersTo != undefined){//if the node is a generic etymology entry (e.g. __ee_door) and refers to different specific etymology entries       
                                nodes[i].pos = [];//no pos can be associated to it                                                           
                                nodes[i].refersTo = this.refersTo;
                            }
                            nodes[i].pos.push(this.pos[0]);
                            nodes[i].gloss.push(this.gloss[0]);
                            merge = true;
                            break;
                        }
                    }
                }
            }
        }
        return merge;
    }
}

//treeNode constructor
//this is a constructor for graph nodes
function treeNode(id, word, iso, gloss, pos, link){
    this.id = id;
    this.word = [];
    if (word == undefined) {
        var splitted = id.split("/");
        this.iso = (splitted.length > 1) ? splitted[0] : "eng";
        this.word.push((splitted.length > 1) ? fromIRItoWord(splitted[1]) : fromIRItoWord(splitted[0]));
        this.gloss = ["-"];
        this.pos = [""];
        this.link = [""];
    } else {
        this.word.push((word == undefined) ? "?" : word.value.replace("__","'").replace(/^_/g,"*").replace(/_/g," "));
        this.iso = iso.value;
        this.gloss = [];
        this.gloss.push((gloss == undefined) ? "-" : gloss.value);
        this.pos = [];
        this.pos.push((pos == undefined) ? "" : pos.value);
        this.link = [];
        this.link.push((link == undefined) ? "" : link.value);
    }
    
    //TODO: improve this function                                                                                                                                                 
    this.mergeInto = function(nodes, pos, gloss){
        var merge = false;
        for (var i in nodes){
            if (nodes[i].et != undefined){
                if (nodes[i].iso == this.iso){
                    if (nodes[i].word == this.word){
                        if (nodes[i].et == this.et) {
                            if (this.refersTo != undefined){
                                nodes[i].pos = [];
                                nodes[i].refersTo = this.refersTo;
                            }
                            nodes[i].pos.push(pos);
                            nodes[i].gloss.push(gloss);
                            merge = true;
                            break;
                        }
                    }
                }
            }
        }
        return merge;
    }
    
    //print this.word, separated by separator                                                                                   
    this.printWord = function(separator){
        var toreturn = "";
        this.word.forEach(function(d){ toreturn += d + separator; });
        return toreturn.slice(0, -1); //trim the last separator                                                                                                                                                       
    }
    
    //print this.link[i]                              
    this.printLink = function(i){
        var toreturn = "";
        if (this.link[i] == ""){
            return toreturn;
        }
        var links = this.link[i].split(",");
        if (links.length == 0) return toreturn;
        toreturn = "<br><br>as extracted from: ";
        links.forEach(function(element, i) {
            var a = element.split("/");
            var b = a[a.length-1].split("#");
            toreturn = toreturn + " <a href=\"" + element + "\" target=\"_blank\">" + b[1].replace(/_/g," ") + " " + b[0].replace(/_/g," ") +"</a>\n";
        });
	
        return toreturn;
    }
    
    //print this.word, this.pos, this.gloss, this.link                                                                           
    this.printData = function(){
        var toreturn = "";
        for (var i=0; i<this.word.length; i++){
            toreturn += "<b>" + this.word[i] + "</b>";
            var pos = this.pos[i];
            if (pos != ""){
                toreturn += " - " + this.pos[i];
            }
            toreturn += "<br><br>";
            var gloss = this.gloss[i];
            if (gloss != ""){
                toreturn += this.gloss[i];
            }
            toreturn += this.printLink(i);
            toreturn += "<br><br><hr>";
        }
        return toreturn.slice(0, -12);
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

//DEFINE MARGINS AND SIZE                                                                                                                     
var margin = [0, 0, 0, 0],
width = window.innerWidth - margin[0],
height = window.innerHeight - margin[0] - margin[2];

//SET PARAMETERS FOR FUNCTION d3.xhr()                                                                                                                           
var endpoint = "https://etytree-virtuoso.wmflabs.org/sparql";
var mime = "application/sparql-results+json";

//DEFINE QUERY TO PLOT CLOUD OF WORDS                                                                                                             
var nodeSparql = function(search){
    //var encodedSearch = encodeURIComponent(search);                                                                                
    var encodedSearch = search;
    var query = [
        "PREFIX dbnary: <http://kaiko.getalp.org/dbnary#>",
        "PREFIX dbetym: <http://kaiko.getalp.org/dbnaryetymology#>",
        "PREFIX lemon: <http://lemon-model.net/lemon#>",
        "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>",
        "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>",
        "SELECT DISTINCT (group_concat(distinct ?ee ; separator=\",\") as ?et) ?uri ?word ?iso ?pos ?gloss (group_concat(distinct ?links ; separator=\",\") as ?link)",
        "WHERE {",
        "    ?uri rdfs:label ?label . ?label bif:contains \"\'" + encodedSearch + "\'\" .",
        //exclude entries that contain the searched word but include other words (e.g.: search="door" label="doorbell", exclude "doorbell")                                   
        "    FILTER REGEX(?label, \"^" + encodedSearch + "$\", 'i') .",
        "    BIND (STR(?label)  AS ?word) .",
        "    OPTIONAL {",
        "        ?uri rdfs:seeAlso ?links",
        "    } .",
        //case uri is an etymology entry like __ee_door                                                                                                            
        "    OPTIONAL {",
        "        ?uri dbnary:refersTo ?ee .",
        "        ?ee rdf:type dbetym:EtymologyEntry .",
        "    }",
        "    OPTIONAL {",
        "        ?uri dbnary:refersTo ?ee .",
        "        ?ee rdf:type lemon:LexicalEntry .",
        "    }",
        //case uri is a canonical form                                                                                                        
        "    OPTIONAL {",
        "        ?le lemon:canonicalForm ?uri .",
        "        ?le rdf:type lemon:LexicalEntry .",
        "        ?le dbnary:partOfSpeech ?pos .",
        "        OPTIONAL{",
        "             ?le lemon:sense ?sense .",
        "             ?sense lemon:definition ?val .",
        "             ?val lemon:value ?gloss .",
        "        }",
        "        OPTIONAL {",
        "             ?ee rdf:type dbetym:EtymologyEntry .",
        "             ?ee dbnary:refersTo ?le .}",
        "        }",
        "    BIND(strbefore(replace(str(?uri),\"http://kaiko.getalp.org/dbnary/eng/\",\"\",\"i\"),\"/\") AS ?ll)",
        "    BIND(if (?ll = \"\",\"eng\",?ll) AS ?iso )",
        "}"
    ];
    return query.join(" ");
}

//DEFINE QUERY TO PLOT GRAPH
var treeSparql = function(id, filter){
    var treeQuery = [
        "DEFINE input:inference \"etymology_ontology\"",
        "PREFIX dbetym: <http://kaiko.getalp.org/dbnaryetymology#>",
        "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>",
        "SELECT DISTINCT ?target1 ?target2 ?target3 ?target4 ?source (group_concat(distinct ?ee ; separator=\",\") as ?ref) ?iso (group_concat(distinct ?p ; separator=\",\") as ?ety) ?word ?pos ?gloss (group_concat(distinct ?links ; separator=\",\") as ?link){",
        "    ?source ?p ?o .",
        "    FILTER (?p in (dbetym:etymologicallyDerivesFrom,dbetym:descendsFrom,dbetym:derivesFrom,dbetym:etymologicallyEquivalentTo))",
        "    {",
        "        SELECT ?source",
        "            {",
        "                ?source dbetym:etymologicallyRelatedTo{1,} " + id + " .",
        "            } LIMIT 100",
        "    }",
        "    UNION",
        "    {",
        "        SELECT ?source",
        "            {" + id + " dbetym:etymologicallyRelatedTo{1,} ?source .",
        "            } LIMIT 100",
        "    }",
        "    UNION",
        "    {",
        "        SELECT ?source",
        "            {" + id + " dbetym:etymologicallyRelatedTo{1,} ?ancestor ",
        filter,
        "                ?source dbetym:etymologicallyRelatedTo{1,} ?ancestor .",
        "            } LIMIT 100",
        "    }",
        "    OPTIONAL",
        "    {",
        "        ?source rdfs:label ?l .",
        "    }",
        "    BIND (STR(?l)  AS ?word1) .",
        "    OPTIONAL",
        "    {",
        "        ?source rdfs:seeAlso ?links .",
        "    }",
        "    OPTIONAL",
        "    {",
        "        ?source dbnary:refersTo ?ee .",
        "        ?ee dbnary:refersTo ?cf1 .",
        "        ?cf1 dbnary:partOfSpeech ?pos1 .",
        "        ?cf1 lemon:sense ?sense1 .",
        "        ?sense1 lemon:definition ?val1 .",
        "        ?val1 lemon:value ?def1",
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
        "    OPTIONAL",
        "    {",
        "        ?source dbnary:refersTo ?le .",
            "        ?le lemon:canonicalForm ?cf2 .",
        "        ?cf2 lemon:writtenRep ?ww .",
        "        ?le dbnary:partOfSpeech ?pos2 .",
        "        ?le lemon:sense ?sense2 .",
        "        ?sense2 lemon:definition ?val2 .",
        "        ?val2 lemon:value ?def2 .",
        "    }",
        "    BIND (STR(?ww)  AS ?word2) .",
        "    BIND(if (bound(?word1),?word1,?word2) AS ?word )",
            "    BIND(if (bound(?pos1),?pos1,?pos2) AS ?pos )",
        "    BIND(if (bound(?def1),?def1,?def2) AS ?gloss )",
        "    BIND(strbefore(replace(str(?source),\"http://kaiko.getalp.org/dbnary/eng/\",\"\",\"i\"),\"/\") AS ?ll)",
        "    BIND(if (?ll = \"\",\"eng\",?ll) AS ?iso )",
            "}"
    ];
    return treeQuery.join(" ");
}

//ignore element with uri starting with __ee_ if there is a corresponding element with uri starting with __cf_                               
function doIgnore(n){
    var ignore = true;
    if (n.uri.value.split("/").pop().startsWith("__ee_")){
        var et = n.et.value;
        if (et == "") ignore = false;
        var splitted = et.split(",");
        if (splitted.length > 1 && splitted[1].split("/").pop().startsWith("__ee_")) ignore = false; //ignore if et is a list like gipsy__Noun_1                             
    } else {
        ignore = false;
    }
    return ignore;
}

$('document').ready(function(){
    //DEFINE DIVISION FOR THE TOOLTIP
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
	.style("border-radius", "8px");    
    
    var filter = ".";
    //"filter (<LONG::IRI_RANK> (?ancestor)<1000) .";    
    
    //TO DO: could ask server if the word has an etymological relationship and if the answer is no ignore that node    
    function loadNodes(myWord, langMap, endpoint){
        var url = endpoint + "?query=" + encodeURIComponent(nodeSparql(myWord));
        console.log(url);
        var nodes = {};
	
        d3.xhr(url, mime, function(request) {
            if (request != null) {
                //clean screen and change help
                d3.select("#tree-overlay").remove();
                div.style("opacity", 0);
                d3.select("#message").remove();
                d3.select("#p-helpPopup").remove();
                d3.select("#helpPopup")
                    .append("p")
                    .attr("id", "p-helpPopup")
                    .attr("style", "font-size:12px;border-radius:8px;max-width:255px")
                    .html("Pick the word you are interested in. <ul><li>Click on a circle to display the language</li> <li>Click on a word to display the data</li> <li>Double click on a circle to choose a word</li></ul>");
		
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
                if (debug) { console.log(theGraph) };
                var sparqlLinks = {};
                var sparqlNodes = {};
		
                theGraph.forEach(function(jsonNode){
                    if (!doIgnore(jsonNode)){
                        var aNode = new Node(jsonNode);
                        if (!aNode.doMerge(sparqlNodes)){
			    aNode.id = (jsonNode.et.value.split("/").pop().startsWith("__ee_")) ? aNode.et : jsonNode.uri.value;
                         
                            //push to sparqlNodes  
                            sparqlNodes[aNode.id] = aNode;
                        }
                    }
                })
		console.log(sparqlNodes)
		
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
                    //.call(d3.behavior.zoom().scaleExtent([1, 10]).on("zoom", function () {
                    //    svgGraph.attr("transform", "translate(" + d3.event.translate + ")scale(" + d3.event.scale + ")");
                    //    div.attr("transform", "translate(" + d3.event.translate + ")scale(" + d3.event.scale + ")");
                    //}))
                    .on("click", function(){
                        div.style("opacity", 0);
                    });
		
                var circle = svgGraph.append("g").selectAll("circle")
                    .data(force.nodes())
                    .enter().append("circle")
                    .attr("r", 12)
                    .attr("fill", "orange")
                    .attr("stroke", "lightBlue")
                    .call(force.drag)
                    .on("mouseover", function(d) {
                        d3.select(this).style("cursor", "pointer");
                    })
                    .on("click", function(d) {
                        d3.selectAll("circle").attr("fill", "orange");
                        d3.select(this)
                            .append("a")
                            .attr("href", "#myPopup")
                            .attr("data-rel", "popup")
                            .attr("data-transition", "pop");
                        div.style("opacity", 1);
                        div.html(langMap.get(d.iso))
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
                    .text(function(d) { return d.iso; })
		
                var rectangle = svgGraph.append("g").selectAll("rectangle")
                    .data(force.nodes())
                    .enter().append("rect")
                    .attr("x", 14)
                    .attr("y", "-.31em")
                    .attr("width", "2em")
                    .attr("height", "0.7em")
                    .attr("fill", "red")
                    .attr("fill-opacity", 0)
                    .on("mouseover", function(d) {
                        d3.select(this).style("cursor", "pointer");
                    })
                    .on("click", function(d) {
                        d3.select(this)
                            .append("a")
                            .attr("href", "#myPopup")
                            .attr("data-rel", "popup")
                            .attr("data-transition", "pop");
                        div.style("opacity", 1);
                        d3.selectAll("circle").attr("fill", "orange");
                        if (d.refersTo != undefined){
                            d.refersTo.forEach(function(iri){
                                d3.selectAll("circle")
                                    .filter(function(f) { return (f.et == iri); })
                                    .attr("fill", "red")
                            });
                            div.html("<b>" +
                                     sparqlNodes[d.id].word +
                                     "</b><br><br><i>" +
                                     "If you choose this word you will visualize the etymological tree of either of the words higlighted in red, " +
                                     "probably of the most popular word among them." +
                                     "<br><br>" +
                                     "This is because this data has been extracted from Wiktionary Etymology Sections and (for the most part) " +
                                     "Wiktionary Etymology Sections link to etymologically related words without specifying their meaning." +
                                     "</i>");
                        } else {
                            div.html("<b>" + sparqlNodes[d.id].word + "</b>" + showPosAndGlosses(sparqlNodes[d.id]))
                                .style("left", (d3.event.pageX + 18) + "px")
                                .style("top", (d3.event.pageY - 28) + "px");
                        }
                        d3.event.stopPropagation();
                    });
		
                var wordText = svgGraph.append("g").selectAll("text")
                    .data(force.nodes())
                    .enter().append("text")
                    .attr("x", 14)
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
	var nodeId = "<" + d.id + ">";
	
	var splitted = reduceIRI(d.id).split("/");
        var searchedIso = (splitted.length > 1) ? splitted[0] : "eng";
	
	var treeUrl = endpoint + "?query=" + encodeURIComponent(treeSparql(nodeId, filter));
	
	if (debug) { 
            console.log(nodeId)
	    console.log(endpoint); 
	    console.log(treeUrl);
	}
	
	//TODO: use wheel
	d3.select("#tree-container")
	    .insert("p", ":first-child")
	    .attr("id", "message")
	    .attr("align", "center")
	    .html("Loading, please wait...");
	
	//TODO: MANAGE ERROR and RELOAD FROM CALLBACK
	d3.xhr(treeUrl, mime, function(request) {
	    //clean screen
	    d3.select("#tree-overlay").remove();
	    div.style("opacity", 0);
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
                //.html("<input type=\"button\" id=\"loadTree\" value=\"Reload with filter\" onclick=\"reloadTree()\"/>");
		optionalFilter = ". filter (<LONG::IRI_RANK> (?ancestor)<1000) ."; 
	    } else {
		//change help       
		d3.select("#p-helpPopup").remove(); 
		d3.select("#helpPopup")
		    .append("p")
		    .attr("id", "p-helpPopup")
		    .attr("style", "font-size:12px;border-radius:8px;max-width:255px")
		    .html("<ul><li>Click on a circle to display the language</li> <li>Click on a word to display the data.</li></ul>");
		
		var treeJson = JSON.parse(request.responseText);
		var treeGraph = treeJson.results.bindings;
		if (debug) { console.log(treeGraph) };
		var treeSparqlLinks = [];
		var treeSparqlNodes = {};
		
		treeGraph.forEach(function(element, j){
		    var mySourceNodeId = reduceIRI(element.source.value);
		    var mySourceNode = new treeNode(mySourceNodeId, element.word, element.iso, element.gloss, element.pos, element.link);
		    //if (treeSparqlNodes[mySourceNodeId] == undefined){
		    treeSparqlNodes[mySourceNodeId] = mySourceNode;
		    //} else {
		    mySourceNode.mergeInto(treeSparqlNodes, element.pos, element.gloss);
		    //merge treeSparqlNodes[mySourceNodeId] with mySourceNode
		    //}
		    ["target1", "target2", "target3", "target4"].map(function(target){
			//console.log(element[target])
			if (element[target] != undefined) {
			    var myTargetNodeId = reduceIRI(element[target].value);
			    if (treeSparqlNodes[myTargetNodeId] == undefined) {
				var myTargetNode = new treeNode(myTargetNodeId);
				treeSparqlNodes[myTargetNodeId] = myTargetNode;
			    }
			}			    
		    });
		});
		
		//set links
		treeGraph.forEach(function(element){
		    var source = reduceIRI(element.source.value);			    
		    var target = null;
		    var t = ["target1", "target2", "target3"]
		    //inherited
		    for (var i in t){
			if (element[t[i]] != undefined) {
			    target = reduceIRI(element[t[i]].value);
			    if (target != source){
				var Link = {"source": treeSparqlNodes[target], "target": treeSparqlNodes[source], "type": "inherited"};
				if (treeSparqlLinks.indexOf(Link) == -1) {
				    treeSparqlLinks.push(Link);
				}
			    } 
			}
		    }	
		    //equivalent
		    t = "target4";
		    if (element[t] != undefined ) {
                        target = reduceIRI(element[t].value);
                        if (target != source){
                            var Link = {"source": treeSparqlNodes[target], "target": treeSparqlNodes[source], "type": "equivalent"};
                            if (treeSparqlLinks.indexOf(Link) == -1) { 
				treeSparqlLinks.push(Link); 
			    }
                        }
                    }
		})
		
                //merge nodes that are linked by a Link of type equivalent             
                if (mergeEquivalentNodes) {
		    treeSparqlLinks.forEach(function(d){
                        if (d.type == "equivalent"){
			    if (d.source.id != d.target.id){
				if (d.target.equivalentTo == undefined){
				    if (d.source.equivalentTo == undefined){
					d.target.equivalentTo = [];
				    } else {
					d.target.equivalentTo = d.source.equivalentTo;
					d.source.equivalentTo = undefined;
				    }
				}
				d.target.equivalentTo = d.target.equivalentTo.concat(d.source.id);
				
				d.target.word = d.target.word.concat(d.source.word);
				if (d.source.pos == undefined){
				    d.source.pos = "";
				}
				d.target.pos = d.target.pos.concat(d.source.pos);
				if (d.source.gloss == undefined){
				    d.source.gloss = "";
				}
				d.target.gloss = d.target.gloss.concat(d.source.gloss);
				if (d.source.link == undefined){
				    d.source.link = "";
				}
				d.target.link = d.target.link.concat(d.source.link);
				//merge node d.source into node d.target, and delete node d.source                 
				treeSparqlLinks.forEach(function(f){
                                    if (f != d){
					if (f.source.id == d.source.id) {
					    f.source = d.target;
					} else if (f.target.id == d.source.id) {
					    f.target = d.target;
					}
                                    }
				})
			    }
			}
                    });
		    
                    for (var i = treeSparqlLinks.length-1; i >= 0; i--){
			if (treeSparqlLinks[i].source.id == treeSparqlLinks[i].target.id){
			    treeSparqlLinks.splice(i, 1);
			} else if (treeSparqlLinks[i].type == "equivalent"){
			    if (treeSparqlLinks[i].source.equivalentTo == undefined){
				delete treeSparqlNodes[treeSparqlLinks[i].source.id];
			    }
			    if (treeSparqlLinks[i].target.equivalentTo == undefined){
				delete treeSparqlNodes[treeSparqlLinks[i].target.id];
                            }
                            treeSparqlLinks.splice(i, 1);
                        }
                    }
		}
		
		if (excludeStarLikeStructures){
		    //find links between words in the same language, but exclude links that have as target the searched word
                    var toDeleteLinks = treeSparqlLinks.filter(function(d) {
			if (d.target.word.find(function(w){ return w == searchedWord; }) == undefined){
			    return d.source.iso == d.target.iso;
			} else {
			    return false;
			}
                    }).filter(function(d) {//don't delete a node if a link starts from it
                        for (var i=0; i<treeSparqlLinks.length; i++) {
                            if (d.target.id == treeSparqlLinks[i].source.id)
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
                            if (treeSparqlLinks[i].source.id == aNode || treeSparqlLinks[i].target.id == aNode) {
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
		    .call(d3.behavior.zoom().scaleExtent([1, 10]).on("zoom", function () {
			svgGraph.attr("transform", "translate(" + d3.event.translate + ")scale(" + d3.event.scale + ")");
			div.attr("transform", "translate(" + d3.event.translate + ")scale(" + d3.event.scale + ")");
		    }))
		    .on("click", function(){
			div.style("opacity", 0);
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
			if (d.iso == searchedIso && d.word == searchedWord) 
			    return "red"; 
			else 
			    return "orange"; 
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
			div.style("opacity", 1);
			div.html(langMap.get(d.iso))
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
		    .text(function(d) { return d.iso; });
		
		var rectangle = svgGraph.append("g").selectAll("rectangle")
		    .data(force.nodes())
		    .enter().append("rect")
		    .attr("x", 14)
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
			div.style("opacity", 1);
			div.html(treeSparqlNodes[d.id].printData())
			    .style("left", (d3.event.pageX + 18) + "px")
			    .style("top", (d3.event.pageY - 28) + "px");
			d3.event.stopPropagation();
		    });
		
		var wordText = svgGraph.append("g").selectAll("text")
		    .data(force.nodes())
		    .enter().append("text")
		    .attr("x", 14)
		    .attr("y", ".31em")
		    .attr("id", "word")
		    .text(function(d) { return d.printWord(","); });
		
		function tick() {
		    path.attr("d", function(d){ 
			return "M" + d.source.x + "," + d.source.y + "A0,0 0 0,1 " + d.target.x + "," + d.target.y; 
		    });
		    circle.attr("transform", transform);
		    wordText.attr("transform", transform);
		    rectangle.attr("transform", transform);
		    isoText.attr("transform", transform);
		}
	    }	
	});
    }
    
    var searchedWord = undefined;

    $('#tags').on("keypress click", function(e){
	if (e.which == 13 || e.type === 'click') {
            searchedWord = $(this).val();
	    
            if (searchedWord){
		if (debug) console.log("loading nodes");
		loadNodes(searchedWord, langMap, endpoint);
	    }
	};
    });
});


