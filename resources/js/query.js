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
        };
    });

    function loadTree(d) {
	d3.select("#tree-overlay").remove();
	d3.select("#tooltip").style("opacity", 0);
	var nodeId = "eng:__ee_1_door";//"<" + d.uri + ">";//eng:__ee_1_door";                     
	var treeQuery = [
            "define input:inference \"etymology_ontology\"",
            "PREFIX dbetym: <http://kaiko.getalp.org/dbnaryetymology#>",
            "PREFIX owl: <http://www.w3.org/2002/07/owl#>",
            "PREFIX eng: <http://kaiko.getalp.org/dbnary/eng/>",
            "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>",
            "select distinct ?source ?target (group_concat(distinct ?ee ; separator=\",\") as ?ref) ?language_code (group_concat(distinct ?p ; separator=\",\") as ?ety) ?word  ?pos ?def (group_concat(distinct ?links ; separator=\",\") as ?link){",
            "?source ?p ?o . filter (?p in (dbetym:etymologicallyDerivesFrom,dbetym:derivesFrom,dbetym:descendsFrom))",
            "{select ?source",
            "{?source dbetym:etymologicallyRelatedTo* " + nodeId + " . }} UNION",
            "{select ?source",
            "{" + nodeId + " dbetym:etymologicallyRelatedTo* ?source . }} UNION",
            "{select ?source",
            "{" + nodeId + " dbetym:etymologicallyRelatedTo* ?descendantOf .",
            "?source dbetym:etymologicallyRelatedTo* ?descendantOf .}} .",
            "OPTIONAL {?source rdfs:label ?l .}",
	    "            BIND (STR(?l)  AS ?word1) .",
            "OPTIONAL {?source rdfs:seeAlso ?links .}",
            "OPTIONAL {?source dbnary:refersTo ?ee .",
            "          ?ee dbnary:refersTo ?cf1 .",
            "          ?cf1 dbnary:partOfSpeech ?pos1 .",
            "          ?cf1 lemon:sense ?sense1 .",
            "          ?sense1 lemon:definition ?val1 .",
            "          ?val1 lemon:value ?def1}",
            "OPTIONAL {?source dbetym:derivesFrom ?target}",
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
	if (debug) { console.log(treeQuery) };
	var treeSparql = treeQuery.join(" ");
	var treeUrl = endpoint + "?query=" + encodeURIComponent(treeSparql);
	if (debug) { console.log(endpoint) }
	if (debug) { console.log(treeUrl) }
	var treeMime = "application/sparql-results+json";
	
	d3.xhr(treeUrl, treeMime, function(request) {
            var treeJson = request.responseText;
            treeJson = JSON.parse(treeJson);
            var treeGraph = treeJson.results.bindings;
            if (debug) { console.log(treeGraph) };
            var treeSparqlLinks = {};
            var treeSparqlNodes = {};
	    
            treeGraph.forEach(function(element, j){
                var mySource = {};
                mySource["id"] = element.uri.value;
                mySource["word"] = element.word.value;
                if (element.def != undefined ) {
                    mySource["gloss"] = element.def.value;
                } else {
                    mySource["gloss"] = "";
                }
                if (element.pos != undefined ) {
                    mySource["pos"] = element.pos.value;
                } else {
                    mySource["pos"] = "";
                }
                    mySource["iso"] = element.language_code.value;
                treeSparqlNodes[element.uri.value] = mySource;
            })
            console.log(treeGraph);
            console.log("testo");
        })
    }
    
    
    function loadNodes(search, langMap, endpoint){
        d3.select("#tree-overlay").remove();
        d3.select("#tooltip").style("opacity", 0);

	var query = [
            "PREFIX dbnary: <http://kaiko.getalp.org/dbnary#>",
            "PREFIX lemon: <http://lemon-model.net/lemon#>",
            "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>",
            "select distinct ?uri ?word ?language_code (group_concat(distinct ?ee ; separator=\",\") as ?et) ?pos ?def (group_concat(distinct ?links ; separator=\",\") as ?link)",
            "where {",
            "?uri rdfs:label ?label . ?label bif:contains \"" + search + "\" .",
            "FILTER regex(?label, \"^" + search + "$\", 'i') .",
            "BIND (STR(?label)  AS ?word) .",
            "OPTIONAL {?uri rdfs:seeAlso ?links} .",
            "OPTIONAL {?uri dbnary:refersTo ?ee .",
            "          ?ee dbnary:refersTo ?le .",
            "          ?le dbnary:partOfSpeech ?pos .",
            "          ?le lemon:sense ?sense .",
            "          ?sense lemon:definition ?val .",
            "          ?val lemon:value ?def}",
            "OPTIONAL {OPTIONAL {?ee2 dbnary:refersTo ?ee .",
            "                    ?ee dbnary:refersTo ?le .}",
            "          ?le lemon:canonicalForm ?uri .",
            "          ?le dbnary:partOfSpeech ?pos .",
            "          ?le lemon:sense ?sense .",
            "          ?sense lemon:definition ?val .",
            "          ?val lemon:value ?def}",
            "BIND(strbefore(replace(str(?uri),\"http://kaiko.getalp.org/dbnary/eng/\",\"\",\"i\"),\"/\") AS ?ll)",
            "BIND(if (?ll = \"\",\"eng\",?ll) AS ?language_code )",
            "}"
	];
	
	var sparql = query.join(" ");
	var url = endpoint + "?query=" + encodeURIComponent(sparql);
	var mime = "application/sparql-results+json";
	
	var nodes = {};

	d3.xhr(url, mime, function(request) {
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
		if (element.def != undefined ) { 
		    mySource["gloss"] = element.def.value;
		} else {
                    mySource["gloss"] = "";
		}
		if (element.pos != undefined ) {
		    mySource["pos"] = element.pos.value;  
		} else {    
		    mySource["pos"] = "";
		} 
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
	    
            // Per-type markers, as they don't inherit styles. 
            svgGraph.append("defs").selectAll("marker")     
		.data(["suit", "licensing", "resolved"])  
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
	    
	})
    }
});


