function exec(){
    var divResult = d3.select("body").append("div")
	.attr("class", "tooltip")
	.style("opacity", 0);

    var width = 1000,
	height = 7000,
	debug = true;

    //set query parameters
    var endpoint = "http://90.147.170.191:8890/sparql";

    d3.text("/resources/data/prefixes", function(prefixes){
	    var query = [
                         "DEFINE input:same-as \"yes\"",

			 "PREFIX dbnary: <http://kaiko.getalp.org/dbnary#>",

			 "PREFIX lemon: <http://lemon-model.net/lemon#>",

			 "PREFIX owl: <http://www.w3.org/2002/07/owl#>",

			 "PREFIX dbetym: <http://www.w3.org/2002/07/owl#>", 

			 "PREFIX ine-pro-eng: <http://kaiko.getalp.org/dbnary/eng/ine-pro/>",
			 "construct { ?s ?p ?o }",
			 "where",
			 "{",
			 "?s",
			 "(dbetym:etymologicallyRelatedTo|!dbetym:etymologicallyRelatedTo)*",
			 "ine-pro-eng:__ee__delh1-",
			 ".",
			 "?s ?p ?o .",
			 "}"
			 ];
	    //var sparql = prefixes.concat(query.join(" "));
	    var sparql = query.join(" ");
            d3.json("/resources/data/sparql_execution.json", function(data){
		    console.log(data);
                    var sparqlLinks = [];
                    var sparqlNodes = {};
		    data["@graph"].forEach(function(element, j){
                            var property = null; 
			    if (element["http://www.w3.org/2002/07/owl#etymologicallyDerivesFrom"] != undefined){
				property = "http://www.w3.org/2002/07/owl#etymologicallyDerivesFrom";	 	
			    } else if (element["http://www.w3.org/2002/07/owl#derivesFrom"] != undefined){
				property = "http://www.w3.org/2002/07/owl#derivesFrom";
			    } else if (element["http://www.w3.org/2002/07/owl#descendsFrom"] != undefined){
				property = "http://www.w3.org/2002/07/owl#descendsFrom";      
			    }
			    if (property != null){
                                var target = element["@id"].replace("http://kaiko.getalp.org/dbnary/eng/_","eng:_").replace("http://kaiko.getalp.org/dbnary/eng/", "").replace("/",":");
                                var sources = element[property];
                                target = target.replace(":__ee_1_","____").replace(":__ee_", "____");
                                for (var i = 0; i < sources.length; i ++){
				    var source = sources[i]["@id"].replace("http://kaiko.getalp.org/dbnary/eng/_","eng:_").replace("http://kaiko.getalp.org/dbnary/eng/", "").replace("/",":");
			            source = source.replace(":__ee_1_","____").replace(":__ee_", "____");
				    var mySource = {};
				    var myTarget = {};
				    mySource["id"] = source;
				    mySource["gloss"] = "unkn";
				    myTarget["id"] = target;
				    myTarget["gloss"] = "unkn";
				    sparqlNodes[source] = mySource;         
                                    sparqlNodes[target] = myTarget;
				}
			    }
			})
			data["@graph"].forEach(function(element, j){
				var property = null;
				if (element["http://www.w3.org/2002/07/owl#etymologicallyDerivesFrom"] != undefined){
				    property = "http://www.w3.org/2002/07/owl#etymologicallyDerivesFrom";
				} else if (element["http://www.w3.org/2002/07/owl#derivesFrom"] != undefined){
				    property = "http://www.w3.org/2002/07/owl#derivesFrom";  
				} else if (element["http://www.w3.org/2002/07/owl#descendsFrom"] != undefined){
				    property = "http://www.w3.org/2002/07/owl#descendsFrom";
				}
				if (property != null){
				    var target = element["@id"].replace("http://kaiko.getalp.org/dbnary/eng/_","eng:_").replace("http://kaiko.getalp.org/dbnary/eng/", "").replace("/",":");        
				    var sources = element[property];   
				    target = target.replace(":__ee_1_","____").replace(":__ee_", "____");
				    for (var i = 0; i < sources.length; i ++){ 
					var source = sources[i]["@id"].replace("http://kaiko.getalp.org/dbnary/eng/_","eng:_").replace("http://kaiko.getalp.org/dbnary/eng/", "").replace("/",":");
					source = source.replace(":__ee_1_","____").replace(":__ee_", "____");
					sparqlLinks.push({"source": sparqlNodes[source],"target": sparqlNodes[target], "type": "resolved"});  
				    }}})   

		    var force = d3.layout.force()
			.nodes(d3.values(sparqlNodes))
			.links(sparqlLinks)
			.size([width, height])
			.linkDistance(150)
			.charge(-700)
			.gravity(.2)
			.on("tick", tick)
			.start();
		    console.log(sparqlLinks);
		    console.log(sparqlNodes);
		    var svgGraph = d3.select("body").append("svg")
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
			.call(force.drag)
			.on("mouseover", function(d) {
				divResult.transition()
				.duration(200)
				.style("opacity", .9);
				divResult.html(sparqlNodes[d.id].gloss)
				.style("left", (d3.event.pageX) + "px")
				.style("top", (d3.event.pageY - 28) + "px");
			    })
			.on("mouseout", function(d) {
				divResult.transition()
				.duration(500)
				.style("opacity", 0);
			    });
		    
		    var isoText = svgGraph.append("g").selectAll("text")
			.data(force.nodes())
			.enter().append("text")
			.attr("x", -8)
			.attr("y", ".31em")
			.attr("fill", "lightBlue")
			.text(function(d) { return d.id.split("____")[0]; });
		    
		    var wordText = svgGraph.append("g").selectAll("text")
			.data(force.nodes())
			.enter().append("text")
			.attr("x", 14)
			.attr("y", ".31em")
			.attr("id", "word")
			.text(function(d) { return d.id.split("____")[1]; });
		    
		    
		    // Use elliptical arc path segments to doubly-encode directionality.
		    function tick() {
			path.attr("d", linkArc);
			circle.attr("transform", transform);
			wordText.attr("transform", transform);
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

		    var url = endpoint + "?query=" + encodeURIComponent(sparql);
		    if (debug) { console.log(endpoint) }
                    if (debug) { console.log(url) }
                    var mime = "application/sparql-results+json";
		    d3.xhr(url, mime, function(request) {
			    var json = request.responseText;
			    if (debug) { console.log(json) }
			    json = JSON.parse(json);
			    if (debug) { console.log(json) }
                            var theGraph = json.results.bindings;
			})
		    
		});
	})
	}





