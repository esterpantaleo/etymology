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

	    function reduceIRI(e){
                    return e.replace("http://kaiko.getalp.org/dbnary/eng/", "").replace("_1_","_").replace("_2_","_").replace("_3_","_");
            }

            function fromIRItoWord(e){
		return e.replace("__ee_1_","").replace("__ee_2_","").replace("__ee_3_","").replace("__ee_","").replace("__","'").replace(/^_/g,"*").replace(/_/g," ");
	    }

	    function fromIRItoCircle(e){
                var tmp = e.replace("http://kaiko.getalp.org/dbnary/","").split("/");
                var toreturn = {};
                toreturn.iso = (tmp.length> 2) ? tmp[1] : "eng";
                toreturn.word = (tmp.length > 2) ? fromIRItoWord(tmp[2]) : fromIRItoWord(tmp[1]);
		console.log(toreturn.iso)
		console.log(toreturn.word)
                return toreturn;
            }
	    
	    function loadTree(d) {
		console.log(d);
		var nodeId = "<" + d.id + ">";
                
		console.log(nodeId)
		var treeQuery = [
		    "define input:inference \"etymology_ontology\"",
		    "PREFIX dbetym: <http://kaiko.getalp.org/dbnaryetymology#>",
		    "PREFIX owl: <http://www.w3.org/2002/07/owl#>",
		    "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>",
		    "select distinct ?target1 ?target2 ?target3 ?source (group_concat(distinct ?ee ; separator=\",\") as ?ref) ?iso (group_concat(distinct ?p ; separator=\",\") as ?ety) ?word  ?pos ?gloss (group_concat(distinct ?links ; separator=\",\") as ?link){",
		    "?source ?p ?o . filter (?p in (dbetym:etymologicallyDerivesFrom,dbetym:descendsFrom,dbetym:derivesFrom))",
		    "{select ?source",
		    "{?source dbetym:etymologicallyRelatedTo* " + nodeId + " . }}" ,
		    "UNION",
		    "{select ?source",
		    "{" + nodeId + " dbetym:etymologicallyRelatedTo* ?source . }}",
		    " UNION",
		    "{select ?source",
		    "{" + nodeId + " dbetym:etymologicallyRelatedTo* ?descendantOf .",
		    "     filter (<LONG::IRI_RANK> (?descendantOf)<1000) .",
		    "     ?source dbetym:etymologicallyRelatedTo* ?descendantOf .}}",
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
		    "OPTIONAL {?source dbetym:derivesFrom ?target2}",
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
		    "BIND(if (bound(?def1),?def1,?def2) AS ?gloss )",
		    "BIND(strbefore(replace(str(?source),\"http://kaiko.getalp.org/dbnary/eng/\",\"\",\"i\"),\"/\") AS ?ll)",
		    "BIND(if (?ll = \"\",\"eng\",?ll) AS ?iso )",
		    "}"
		];
		var treeSparql = treeQuery.join(" ");
		var treeUrl = endpoint + "?query=" + encodeURIComponent(treeSparql);
		if (debug) { console.log(endpoint) }
		if (debug) { console.log(treeUrl) }
		var treeMime = "application/sparql-results+json";
		
		d3.select("#tree-container")
                    .insert("p", ":first-child")
                    .attr("id","message")
                    .attr("align", "center")
                    .html("Loading ...");
		
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
			    mySource.id = reduceIRI(element.source.value);
			    mySource.word = (element.word == undefined) ? "?" : element.word.value.replace("__","'").replace(/^_/g,"*").replace(/_/g," ");
			    mySource.iso = element.iso.value;
			    mySource.gloss = (element.gloss == undefined) ? "" : element.gloss.value;
			    mySource.pos = (element.pos == undefined) ? "" : element.pos.value;
			    mySource.link = (element.link == undefined) ? "" : element.link.value;
			    treeSparqlNodes[mySource["id"]] = mySource;
			    
			    if (element.target1 != undefined) {
				mySource = {};
				mySource.id = reduceIRI(element.target1.value);
				if (treeSparqlNodes[mySource.id] == undefined){
				    var tmp = fromIRItoCircle(element.target1.value);
				    mySource.iso = tmp.iso;
                                    mySource.word = tmp.word;
				    treeSparqlNodes[mySource.id] = mySource;
				}
			    }
			    if (element.target2 != undefined) {
				mySource = {};
				mySource.id = reduceIRI(element.target2.value);
				if (treeSparqlNodes[mySource.id] == undefined){
				    var tmp = fromIRItoCircle(element.target2.value);
				    mySource.iso = tmp.iso;
                                    mySource.word = tmp.word;
				    treeSparqlNodes[mySource.id] = mySource;
				}
			    }
			    if (element.target3 != undefined) {
				mySource = {};
				mySource.id = reduceIRI(element.target3.value);
				if (treeSparqlNodes[mySource.id] == undefined){
                                    var tmp = fromIRItoCircle(element.target3.value);
				    mySource.iso = tmp.iso;
				    mySource.word = tmp.word;
				    treeSparqlNodes[mySource.id] = mySource;
				}
			    }
			})
			
			//set links
			treeGraph.forEach(function(element, j){
			    var source = reduceIRI(element.source.value);
			    var target = null;
			    if (element.target1 != undefined ) {
				target = reduceIRI(element.target1.value);
				if (target != source){
				    var Link = {"source": treeSparqlNodes[target],"target": treeSparqlNodes[source], "type": "inherited"};
				    var a = treeSparqlLinks.indexOf(Link);
				    if (a == -1) {treeSparqlLinks.push(Link);}
				} 
			    }
			    if (element.target2 != undefined ) {
				target = reduceIRI(element.target2.value);
				if (target != source){
				    var Link = {"source": treeSparqlNodes[target],"target": treeSparqlNodes[source], "type": "inherited"};
				    if (treeSparqlLinks.indexOf(Link) == -1) {treeSparqlLinks.push(Link);}
				}
			    }
			    if (element.target3 != undefined ) {
				target = reduceIRI(element.target3.value);
				if (target != source){
				    var Link = {"source": treeSparqlNodes[target],"target": treeSparqlNodes[source], "type": "inherited"};
				    if (treeSparqlLinks.indexOf(Link) == -1) {treeSparqlLinks.push(Link);}
				}
			    }
			    
			})
			
			if (treeSparqlLinks.length > 100){
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
				.html("Sorry, no etymology available for this word");
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
			    });

			
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
				if (links.length == 0) return toreturn;
                                toreturn = "<br><br>as extracted from: ";
				links.forEach(function(element, i) {
				    var a = element.split("/");
				    var b = a[a.length-1].split("#");
				    toreturn = toreturn + " <a href=\"" + element + "\">" + b[1].replace(/_/g," ") + " " + b[0].replace(/_/g," ") +"</a>\n";
				});
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
			    path.attr("d", function(d){ 
				return "M" + d.source.x + "," + d.source.y + "A0,0 0 0,1 " + d.target.x + "," + d.target.y; 
			    });
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
	    
	    function loadNodes(search, langMap, endpoint){
		var query = [
		    "PREFIX dbnary: <http://kaiko.getalp.org/dbnary#>",
		    "PREFIX dbetym: <http://kaiko.getalp.org/dbnaryetymology#>",
		    "PREFIX lemon: <http://lemon-model.net/lemon#>",
		    "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>",
                    "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>",
		    "PREFIX owl: <http://www.w3.org/2002/07/owl#>",
		    "select distinct (group_concat(distinct ?ee ; separator=\",\") as ?et) ?uri ?word ?iso ?pos ?gloss (group_concat(distinct ?links ; separator=\",\") as ?link)",
		    "where {",
		    "?uri rdfs:label ?label . ?label bif:contains \"" + search + "\" .",
                    //exclude entries that contain the searched word but include other words (e.g.: search="door" label="doorbell", exclude "doorbell") 
		    "FILTER regex(?label, \"^" + search + "$\", 'i') .",
		    "BIND (STR(?label)  AS ?word) .",
		    "OPTIONAL {?uri rdfs:seeAlso ?links} .",
		    //case uri is an etymology entry like __ee_door
		    "OPTIONAL {?uri dbnary:refersTo ?ee.",
                    "          ?ee rdf:type dbetym:EtymologyEntry .",
                   // "          ?le rdf:type lemon:LexicalEntry .", 
                   // "          ?ee dbnary:refersTo ?le .", 
                   // "          ?uri rdf:type dbetym:EtymologyEntry .",
                   // "          ?le dbnary:partOfSpeech ?pos .",
                   // "          ?le lemon:sense ?sense .",
                   // "          ?sense lemon:definition ?val .",
                   // "          ?val lemon:value ?gloss .",
		    "          }",
                    "OPTIONAL {?uri dbnary:refersTo ?ee .",
		    "          ?ee rdf:type lemon:LexicalEntry .}",
		    //case uri is a canonical form
		    "OPTIONAL {?le lemon:canonicalForm ?uri .",
                    "          ?le rdf:type lemon:LexicalEntry .",
		    "          ?le dbnary:partOfSpeech ?pos .",
		    "          OPTIONAL{ ?le lemon:sense ?sense .",
		    "                    ?sense lemon:definition ?val .",
		    "                    ?val lemon:value ?gloss .}",
		    "           OPTIONAL {",
		    "                     ?ee rdf:type dbetym:EtymologyEntry .",
		    "                     ?ee dbnary:refersTo ?le .}",
                    "}",
		    "BIND(strbefore(replace(str(?uri),\"http://kaiko.getalp.org/dbnary/eng/\",\"\",\"i\"),\"/\") AS ?ll)",
		    "BIND(if (?ll = \"\",\"eng\",?ll) AS ?iso )",
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
			
			theGraph.forEach(function(element){
			    //process all elements
			    var mySource = {};
			    //in output of sparql query, ignore results with iri starting with __ee_ if the result they represent is already present with iri starting with __cf_
			    mySource.ignore = true;
                            var uri = element.uri.value.split("/");
			    if (uri[uri.length - 1].startsWith("__ee_")){
				if (element.et.value == ""){
				    mySource.ignore = false;
				} else { //if element.et != undefined
				    var tmp = element.et.value.split(",");
				    if (tmp.length > 1){
                                        mySource.refersTo = tmp;
                                        element.et.value = element.uri.value;
					mySource.ignore = false;
				    }
				}
			    } else {
				mySource.ignore = false;
			    }
			    if (!mySource.ignore){
                                var gloss = (element.gloss != undefined) ? element.gloss.value : "";
				var pos = (element.pos != undefined) ? element.pos.value : "";
				mySource.word = element.word.value;
				mySource.iso = element.iso.value;
                                mySource.et = (element.et != undefined) ? element.et.value : "";

				mySource.merge = false;
								
				for (var i in sparqlNodes){
				    if (sparqlNodes[i].et != undefined){ 
					if (sparqlNodes[i].iso == mySource.iso){
					    if (sparqlNodes[i].word == mySource.word){
						if (sparqlNodes[i].et == mySource.et) {
						    sparqlNodes[i].pos.push(pos); 
						    sparqlNodes[i].gloss.push(gloss); 
						    mySource.merge = true;
						    break;
						}
					    }
					}
				    }
				}
                                if (!mySource.merge){
				    mySource.pos = [];
				    mySource.gloss = [];
				    mySource.id = (element.et.value.split("/").pop().startsWith("__ee_")) ? mySource.et : element.uri.value;
				    mySource.gloss.push(gloss);
				    mySource.pos.push(pos);
				    delete mySource.ignore;
				    delete mySource.merge;
				    sparqlNodes[mySource.id] = mySource;
				}
			    }
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
				d3.select(this).style("cursor", "pointer");
				d3.selectAll("circle").attr("fill", "orange");
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
				div.transition()
				    .duration(500)
				    .style("opacity", .9);
				var optionalHtml = "";		
                                if (d.refersTo != undefined){
				    optionalHtml = "<br><br>The etymological tree of this word corresponds to either of the words higlighted in red";
				    d.refersTo.forEach(function(iri){ d3.selectAll("circle").filter(function(f) { return (f.et == iri); }).attr("fill", "red")});
				} else {
				    d3.selectAll("circle").attr("fill", "orange");
				}
				div.html("<b>" + sparqlNodes[d.id].word + "</b>" + showGlosses(sparqlNodes[d.id]) + optionalHtml)
                                    .style("left", (d3.event.pageX + 18) + "px")
                                    .style("top", (d3.event.pageY - 28) + "px");
			    })
			    .on("mouseout", function(d) {
				div.transition()
				    .duration(500)
				    .style("opacity", 0);
			    });
			
			function showGlosses(sparqlNode){
			    var toreturn = "";
                            for (var i = 0; i < sparqlNode.pos.length; i ++) { toreturn += "<br><br>" + sparqlNode.pos[i] + " - " + sparqlNode.gloss[i]}
			    return toreturn;
			}
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


