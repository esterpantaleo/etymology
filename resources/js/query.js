$('document').ready(function(){
    console.log("loading languages");
    //load languages
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

    var debug = true;

    // Define the div for the tooltip                               
    var div = d3.select("body").append("div")
        .attr("id", "tooltip")
        .attr("class", "tooltip")
        .style("opacity", 0);
    
    var margin = [0, 0, 0, 0],
    width = window.innerWidth - margin[0],
    height = window.innerHeight - margin[0] - margin[2];
    
    var endpoint = "http://etytree-virtuoso.wmflabs.org/sparql";
    
    $('#tags').on("keypress click", function(e){
        if (e.which == 13 || e.type === 'click') {
            var search = $('#tags').val();
	    console.log("loading nodes");
	    loadNodes(search, langMap, endpoint);

	    function loadTree(d) {
		console.log(d);
		var nodeIso = "eng";
		if (d.iso != "eng") nodeIso = nodeIso + "/" + d.iso; 
		var nodeId = "<http://kaiko.getalp.org/dbnary/" + nodeIso + "/__ee_1_" + d.word + ">"; 
		var treeQuery = [
		    "define input:inference \"etymology_ontology\"",
		    "PREFIX dbetym: <http://kaiko.getalp.org/dbnaryetymology#>",
		    "PREFIX owl: <http://www.w3.org/2002/07/owl#>",
		    "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>",
		    "select distinct ?target1 ?target3 ?source (group_concat(distinct ?ee ; separator=\",\") as ?ref) ?language_code (group_concat(distinct ?p ; separator=\",\") as ?ety) ?word  ?pos ?def (group_concat(distinct ?links ; separator=\",\") as ?link){",
		    "?source ?p ?o . filter (?p in (dbetym:etymologicallyDerivesFrom,dbetym:descendsFrom))",//dbetym:derivesFrom
		    "{select ?source",
		    "{?source dbetym:etymologicallyRelatedTo* " + nodeId + " . }}" ,
		    "UNION",
		    "{select ?source",
		    "{" + nodeId + " dbetym:etymologicallyRelatedTo* ?source . }}",
		    " UNION",
		    "{select ?source",
		    "{" + nodeId + " dbetym:etymologicallyRelatedTo* ?descendantOf .",
		    "?source dbetym:etymologicallyRelatedTo* ?descendantOf .}}",
		    " .",
		    "OPTIONAL {?source rdfs:label ?l .}",
		    "            BIND (STR(?l)  AS ?word1) .",
		    "OPTIONAL {?source rdfs:seeAlso ?links .}",
		    "OPTIONAL {?source dbnary:refersTo ?ee .",
		    "          ?ee dbnary:refersTo ?cf1 .",
		    "          ?cf1 dbnary:partOfSpeech ?pos1 .",
		    "          ?cf1 lemon:sense ?sense1 .",
		    "          ?sense1 lemon:definition ?val1 .",
		    "          ?val1 lemon:value ?def1}",
		    "OPTIONAL {?source dbetym:etymologicallyDerivesFrom ?target1}",
		    //"OPTIONAL {?source dbetym:derivesFrom ?target2}"
		    "OPTIONAL {?source dbetym:descendsFrom ?target3}",
		    "OPTIONAL {?source dbnary:refersTo ?le .",
		    "          ?le lemon:canonicalForm ?cf2 .",
		    "          ?cf2 lemon:writtenRep ?ww .",
		    "          ?le dbnary:partOfSpeech ?pos2 .",
		    "          ?le lemon:sense ?sense2 .",
		    "          ?sense2 lemon:definition ?val2 .",
		    "          ?val2 lemon:value ?def2 .}",
		    "            BIND (STR(?ww)  AS ?word2) .",
		    "BIND(if (bound(?word1),?word1,?word2) AS ?word )",
		    "BIND(if (bound(?pos1),?pos1,?pos2) AS ?pos )",
		    "BIND(if (bound(?def1),?def1,?def2) AS ?def )",
		    "BIND(strbefore(replace(str(?source),\"http://kaiko.getalp.org/dbnary/eng/\",\"\",\"i\"),\"/\") AS ?ll)",
		    "BIND(if (?ll = \"\",\"eng\",?ll) AS ?language_code )",
		    "}"
		];
		var treeSparql = treeQuery.join(" ");
		var treeUrl = endpoint + "?query=" + encodeURIComponent(treeSparql);
		if (debug) { console.log(endpoint) }
		if (debug) { console.log(treeUrl) }
		var treeMime = "application/sparql-results+json";
		
		d3.xhr(treeUrl, treeMime, function(request) {
		    if (request != null) {
                        d3.select("#tree-overlay").remove();
			d3.select("#tooltip").style("opacity", 0);
                        d3.select("#message").remove();
			var treeJson = request.responseText;
			treeJson = JSON.parse(treeJson);
			var treeGraph = treeJson.results.bindings;
			if (debug) { console.log(treeGraph) };
			var treeSparqlLinks = [];
			var treeSparqlNodes = {};
			
			//set nodes
			treeGraph.forEach(function(element, j){
			    var mySource = {};
			    
			    mySource["id"] = element.source.value.replace("http://kaiko.getalp.org/dbnary/eng/", "").replace("_1_","_").replace("_2_","_").replace("_3_","_");
			    if (element.word != undefined){
				mySource["word"] = element.word.value;
			    } else {
				mySource["word"] = "?";
			    }
			    if (element.def != undefined){
				mySource["gloss"] = element.def.value;
			    }
			    if (element.pos != undefined){
				mySource["pos"] = element.pos.value;
			    }
			    mySource["iso"] = element.language_code.value;
			    if (element.link != undefined){
				mySource["link"] = element.link.value;
			    }
			    treeSparqlNodes[mySource["id"]] = mySource;
			    
			    if (element.target1 != undefined ) {
				mySource = {};
				mySource["id"] = element.target1.value.replace("http://kaiko.getalp.org/dbnary/eng/", "").replace("_1_","_").replace("_2_","_").replace("_3_","_");
				if (treeSparqlNodes[mySource["id"]] == undefined){
				    var tmp = element.target1.value.replace("http://kaiko.getalp.org/dbnary/","");
				    var lang = tmp.split("/");
				    if (lang.length > 2){
					mySource["iso"] = lang[1];			    
					mySource["word"] = lang[2].replace("__ee_","");
				    } else {
					mySource["iso"] = "eng";
					mySource["word"] = mySource["id"].replace("__ee_","");
				    }
				    treeSparqlNodes[mySource["id"]] = mySource;
				}
			    }
			    if (element.target2 != undefined ) {
				mySource = {};
				mySource["id"] = element.target2.value.replace("http://kaiko.getalp.org/dbnary/eng/", "").replace("_1_","_").replace("_2_","_").replace("_3_","_");
				if (treeSparqlNodes[mySource["id"]] == undefined){
				    var tmp = element.target2.value.replace("http://kaiko.getalp.org/dbnary/","");
				    var lang = tmp.split("/");
				    if (lang.length> 2){
					mySource["iso"] = lang[1];
					mySource["word"] = lang[2].replace("__ee_","");
				    } else {
					mySource["iso"] = "eng";
					mySource["word"] = mySource["id"].replace("__ee_","");
				    }
				    treeSparqlNodes[mySource["id"]] = mySource;
				}
			    }
			    if (element.target3 != undefined) {
				mySource = {};
				mySource["id"] = element.target3.value.replace("http://kaiko.getalp.org/dbnary/eng/", "").replace("_1_","_").replace("_2_","_").replace("_3_","_");
				if (treeSparqlNodes[mySource["id"]] == undefined){
				    var tmp = element.target3.value.replace("http://kaiko.getalp.org/dbnary/","");
				    var lang = tmp.split("/");
				    if (lang.length> 2){
					mySource["iso"] = lang[1];
					mySource["word"] = lang[2].replace("__ee_1_","").replace("__ee_2_","").replace("__ee_3_","");
				    } else {
					mySource["iso"] = "eng";
					mySource["word"] = mySource["id"];
				    }
				    treeSparqlNodes[mySource["id"]] = mySource;
				}
			    }
			})
			
			//set links
			treeGraph.forEach(function(element, j){
			    var source = element.source.value.replace("http://kaiko.getalp.org/dbnary/eng/", "").replace("_1_","_").replace("_2_","_").replace("_3_","_");
			    var target = null;
			    if (element.target1 != undefined ) {
				target = element.target1.value.replace("http://kaiko.getalp.org/dbnary/eng/", "").replace("_1_","_").replace("_2_","_").replace("_3_","_");
				if (target != source){
				    var Link = {"source": treeSparqlNodes[target],"target": treeSparqlNodes[source], "type": "inherited"};
				    var a = treeSparqlLinks.indexOf(Link);
				    if (a == -1) {treeSparqlLinks.push(Link);}
				} 
			    }
			    if (element.target2 != undefined ) {
				target = element.target2.value.replace("http://kaiko.getalp.org/dbnary/eng/", "").replace("_1_","_").replace("_2_","_").replace("_3_","_");
				if (target != source){
				    var Link = {"source": treeSparqlNodes[target],"target": treeSparqlNodes[source], "type": "inherited"};
				    var a = treeSparqlLinks.indexOf(Link);
				    if (a == -1) {treeSparqlLinks.push(Link);}
				}
			    }
			    if (element.target3 != undefined ) {
				target = element.target3.value.replace("http://kaiko.getalp.org/dbnary/eng/", "").replace("_1_","_").replace("_2_","_").replace("_3_","_");
			    if (target != source){
				var Link = {"source": treeSparqlNodes[target],"target": treeSparqlNodes[source], "type": "inherited"};
				var a = treeSparqlLinks.indexOf(Link);
				if (a == -1) {treeSparqlLinks.push(Link);}
			    }
			    }
			    
			})
			
			if (treeSparqlLinks.length >100){
			    var sameIsoLinks = treeSparqlLinks.filter(function(d) { return d.source["iso"] == d.target["iso"]; })
			    
			    var toDeleteLinks = sameIsoLinks.filter(function(d) { for (var i=0; i<treeSparqlLinks.length; i++) {if (d.target.id == treeSparqlLinks[i].source.id) return false; } return true;});
			    treeSparqlLinks = treeSparqlLinks.filter(function(d) { for (var i=0; i<toDeleteLinks.length; i++) {if (toDeleteLinks[i] == d) return false;} return true;}); 
            		    
			    for (var aNode in treeSparqlNodes) {
				
				var isLinked = false;
				for (var i=0; i<treeSparqlLinks.length; i++) { if (treeSparqlLinks[i].source["id"] == aNode || treeSparqlLinks[i].target["id"] == aNode) {isLinked = true; break;}}
				if (isLinked == false) {
				    delete treeSparqlNodes[aNode];
				}
			    }
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
			    .call(d3.behavior.zoom().on("zoom", function () {
				svgGraph.attr("transform", "translate(" + d3.event.translate + ")")
			    }));
			
			if (treeSparqlLinks.length == 0){
			    d3.select("#tree-overlay").remove();
			    d3.select("#tree-container")
				.append("p")
				.attr("id","message")
				.attr("align", "center")
				.html("Sorry, no etymology is available for this word");
			}
			
			// Per-type markers, as they don't inherit styles.                      
			svgGraph.append("defs").selectAll("marker")
			    .data(["clonedInto", "borrowedBy", "inherited"])
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
			    .attr("fill", "orange")
			    .attr("stroke", "red")
			    .call(force.drag)
			    .on("mouseover", function(d) {
				div.transition()
				    .duration(100)
				    .style("opacity", .9);
				div.html(langMap.get(d.iso))
				    .style("left", (d3.event.pageX) + "px")
				    .style("top", (d3.event.pageY - 28) + "px");
			    })
			    .on("mouseout", function(d) {
				div.transition()
				    .duration(100)
				    .style("opacity", 0);
			    })
			    .on("click", loadTree);
			
			var isoText = svgGraph.append("g").selectAll("text")
			    .data(force.nodes())
			    .enter().append("text")
			    .attr("x", 0)
			    .attr("y", ".31em")
			    .attr("fill", "black")
			    .attr("text-anchor", "middle")
			    .text(function(d) { return d.iso; });
			
			function showLinks(l){
			    var toreturn = "";
			    if (l != undefined && l != ""){
				var links = l.split(",");
				if (links.length >0){
				    var toreturn = "<br><br>as extracted from: ";
				    for (var i=0; i<links.length; i++) {
					var ref = links[i].split("/");
					var elements = ref[ref.length-1].split("#");
					toreturn = toreturn + " <a href=\"" + links[i] + "\">" + elements[1].replace("_"," ") + " " + elements[0].replace("_"," ") +"</a>";
					console.log(toreturn)
					if (i < links.length-1) 
					    toreturn = toreturn + ",";
				    }
				}
			    }
			    return toreturn;
			}
			
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
				div.transition()
				    .duration(500)
				    .style("opacity", .9);
				div.html("<b>" + treeSparqlNodes[d.id].word + "</b>-" + treeSparqlNodes[d.id].pos + "<br><br>" + treeSparqlNodes[d.id].gloss + showLinks(treeSparqlNodes[d.id].link))
				    .style("left", (d3.event.pageX + 18) + "px")
				    .style("top", (d3.event.pageY - 28) + "px");
			    })
			    .on("mouseout", function(d) {
				div.transition()
				    .duration(500)
				    .style("opacity", 1);
			    });
			
			var wordText = svgGraph.append("g").selectAll("text")
			    .data(force.nodes())
			.enter().append("text")
			    .attr("x", 14)
			    .attr("y", ".31em")
			    .attr("id", "word")
			    .text(function(d) { return d.word; });
			
			// Use elliptical arc path segments to doubly-encode directionality.                                                                                                                                                                                                                                                                                                                                  
			function tick() {
			    path.attr("d", linkArc);
			circle.attr("transform", transform);
			    wordText.attr("transform", transform);
			    rectangle.attr("transform", transform);
			isoText.attr("transform", transform);
			}
			
			function linkArc(d) {
			    var dx = d.target.x - d.source.x,
			    dy = d.target.y - d.source.y,
			    dr = 0;//Math.sqrt(dx * dx + dy * dy);                                                                                                                                                                                                                                                                                                                                                            
			    return "M" + d.source.x + "," + d.source.y + "A" + dr + "," + dr + " 0 0,1 " + d.target.x + "," + d.target.y;
			}
			
			function transform(d) {
			return "translate(" + d.x + "," + d.y + ")";
			}
		    }	
		})		
	    }	    
	    
	    function loadNodes(search, langMap, endpoint){
		var query = [
		    "PREFIX dbnary: <http://kaiko.getalp.org/dbnary#>",
		    "PREFIX dbetym: <http://kaiko.getalp.org/dbnaryetymology#>",
		    "PREFIX lemon: <http://lemon-model.net/lemon#>",
		    "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>",
                    "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>",
		    "PREFIX owl: <http://www.w3.org/2002/07/owl#>",
		    "select distinct ?uri ?word ?language_code (group_concat(distinct ?ee ; separator=\",\") as ?et) ?pos ?def (group_concat(distinct ?links ; separator=\",\") as ?link)",
		    "where {",
		    "?uri rdfs:label ?label . ?label bif:contains \"" + search + "\" .",
                    //exclude entries that contain the searched word but include other words (e.g.: search="door" label="doorbell", exclude "doorbell") 
		    "FILTER regex(?label, \"^" + search + "$\", 'i') .",
		    "BIND (STR(?label)  AS ?word) .",
		    "OPTIONAL {?uri rdfs:seeAlso ?links} .",
		    "OPTIONAL {?uri dbnary:refersTo ?ee .",
		    "          ?ee dbnary:refersTo ?le .",
		    "          ?le dbnary:partOfSpeech ?pos .",
		    "          ?le lemon:sense ?sense .",
		    "          ?sense lemon:definition ?val .",
		    "          ?val lemon:value ?def}",
		    "OPTIONAL {?le lemon:canonicalForm ?uri .",
		    "          ?le dbnary:partOfSpeech ?pos .",
		    "          ?le lemon:sense ?sense .",
		    "          ?sense lemon:definition ?val .",
		    "          ?val lemon:value ?def.",
		    "           OPTIONAL {",
		    "                     ?ee rdf:type <http://kaiko.getalp.org/dbnaryetymology#EtymologyEntry> .",
    //                "                     ?ee rdf:type dbetym:etymologyEntry .",
		    "                     ?ee dbnary:refersTo ?le .}}",
		    "BIND(strbefore(replace(str(?uri),\"http://kaiko.getalp.org/dbnary/eng/\",\"\",\"i\"),\"/\") AS ?ll)",
		    "BIND(if (?ll = \"\",\"eng\",?ll) AS ?language_code )",
		    "}"
		];
		
		var sparql = query.join(" ");
		var url = endpoint + "?query=" + encodeURIComponent(sparql);
		var mime = "application/sparql-results+json";
		console.log(url);
		var nodes = {};
		
		d3.xhr(url, mime, function(request) {
		    if (request != null) {
			d3.select("#tree-overlay").remove();
			d3.select("#tooltip").style("opacity", 0);
			d3.select("#message").remove();
		    
			var json = request.responseText;
			json = JSON.parse(json);
			var theGraph = json.results.bindings;
			if (debug) { console.log(theGraph) };
			var sparqlLinks = {};
			var sparqlNodes = {};                             
			
			theGraph.forEach(function(element, j){
			    var mySource = {};
			    mySource["id"] = element.uri.value;
			    mySource["word"] = element.word.value;
			    mySource["gloss"] = (element.def != undefined) ? element.def.value : "";
			    mySource["pos"] = (element.pos != undefined) ? element.pos.value : "";
			    mySource["iso"] = element.language_code.value;
			    sparqlNodes[element.uri.value] = mySource;
			})
			
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
			    .attr("height", height);         
						
			var path = svgGraph.append("g").selectAll("path")
			    .data(force.links())           
			    .enter().append("path")      
			    .attr("class", function(d) { return "link " + d.type; })  
			    .attr("marker-end", function(d) { return "url(#" + d.type + ")"; });
			
			var circle = svgGraph.append("g").selectAll("circle") 
			    .data(force.nodes())             
			    .enter().append("circle")                     
			    .attr("r", 12)  
			    .attr("fill", "orange")
			.attr("stroke", "red")               
			    .call(force.drag) 
			    .on("mouseover", function(d) {                    
				div.transition()           
				    .duration(200)            
				    .style("opacity", .9);  
			    div.html(langMap.get(d.iso)) 
				    .style("left", (d3.event.pageX) + "px")     
				    .style("top", (d3.event.pageY - 28) + "px");
			    })                                         
			    .on("mouseout", function(d) {          
				div.transition()           
				    .duration(500)               
				    .style("opacity", 0);    
			    })
			    .on("click", loadTree);
			
			var isoText = svgGraph.append("g").selectAll("text")  
			    .data(force.nodes())                             
			    .enter().append("text")      
			    .attr("x", 0)  
			    .attr("y", ".31em")   
			    .attr("fill", "black") 
			    .attr("text-anchor", "middle")  
			    .text(function(d) { return d.iso; });//sparqlNodes[d.id].iso; });
			
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
			    div.transition()
				    .duration(500)
				    .style("opacity", .9);
				div.html(sparqlNodes[d.id].pos + " - " + sparqlNodes[d.id].gloss)
				    .style("left", (d3.event.pageX + 18) + "px")
				    .style("top", (d3.event.pageY - 28) + "px");
			    })
			    .on("mouseout", function(d) {
				div.transition()
				    .duration(500)
				    .style("opacity", 0);
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
			
			function transform(d) {            
			    return "translate(" + d.x + "," + d.y + ")"; 
			}
            	    }	
		})
	    }
	};
    });
});


